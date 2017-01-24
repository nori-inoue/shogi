package jp.gr.norinori.shogi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.gr.norinori.application.Arguments;
import jp.gr.norinori.application.CommandLineArgument;
import jp.gr.norinori.core.element.KeyValuePair;
import jp.gr.norinori.core.element.Pair;
import jp.gr.norinori.database.DatabaseConnection;
import jp.gr.norinori.shogi.honshogi.HonShogi;
import jp.gr.norinori.shogi.honshogi.HonShogiActionStatus;
import jp.gr.norinori.shogi.honshogi.HonShogiDisplayUtil;
import jp.gr.norinori.shogi.honshogi.HonShogiField;
import jp.gr.norinori.shogi.honshogi.HonShogiScene;
import jp.gr.norinori.shogi.honshogi.file.SimpleDataFormat;

/**
 * 将棋型ゲームメインクラス
 *
 * @author nori
 */
public class Main {

	public static void main(String[] args) {
		Arguments arguments = new CommandLineArgument(args);

		int loop = 1;
		if (arguments.hasOption("-loop")) {
			loop = Integer.parseInt(arguments.getOptionValue("-loop"));
		}
		if (arguments.hasOption("-debug")) {
			Logger.useDebug = true;
		}
		Logger.useInfo = true;

//		Timer.debug = false;
		Timer.startInfo("total");
		for (int i = 0; i < loop; i++) {
			Main me = new Main();
			me.run(arguments);
		}
		Timer.stopInfo("total");

		Logger.logTimer(Timer.getTreeTimeids());
	}

	/**
	 * ゲーム実行
	 *
	 * @param arguments 実行オプション
	 */
	public void run(Arguments arguments) {
		GameInformation gameInformation = new GameInformation();
		GameProtocol gameProtocol = new HonShogi();
		gameInformation.setGameProtocol(gameProtocol);

		Timer.start("initializeGame", "total");
		Scene scene = initializeGame(gameInformation);
		gameProtocol.analyzeScene(scene);
		Timer.stop("initializeGame");

		List<Pair<Action, String>> hisotry;
		ActionResult result;
		Action action = null;
		try {
			hisotry = new ArrayList<>();
			hisotry.add(new KeyValuePair<Action, String>(action, scene.getHash()));

			result = null;
			for (int i = 0; i < 500; i++) {
				Logger.debug("=== Phase:" + (i + 1));
				Player player = scene.getInitiativePlayer();

				Timer.start("action", "total");
				action = player.getActionEngine().action(scene);
				if (action != null && action.status != null && action.status.isEnd) {
					Logger.debug(result.status.message);

					HonShogiField field = (HonShogiField) scene.getField();
					Logger.info(HonShogiDisplayUtil.displayActionByCharacter(field, action));
					Logger.info(HonShogiDisplayUtil.displayByCharacter((HonShogiScene) scene));
					break;
				}
				Timer.stop("action");

				Timer.start("nextPhage", "total");
				action.status = gameProtocol.nextPhage(scene, action);
				Timer.stop("nextPhage");

				hisotry.add(new KeyValuePair<Action, String>(action, scene.getHash()));

				Timer.start("judge", "total");
				result = gameProtocol.judge(scene, action, hisotry);
				Timer.stop("judge");
				if (!result.status.message.isEmpty()) {
					Logger.debug(result.status.message);
				}


				if (result.status.isEnd) {
					HonShogiField field = (HonShogiField) scene.getField();
					Logger.info(HonShogiDisplayUtil.displayActionByCharacter(field, action));
					Logger.info(HonShogiDisplayUtil.displayByCharacter((HonShogiScene) scene));
					break;
				}

				gameProtocol.displayScene(scene, action);
			}
			if (!result.status.isEnd) {
				result.status.message = "引き分け";
			}

			Timer.start("finalizeGame", "total");
			finalizeGame(result, hisotry);
			gameProtocol.displayScene(scene, null);
			Timer.stop("finalizeGame");
		} catch (Exception e) {
			gameProtocol.displayScene(scene, action);
			throw e;
		}
	}

	private int game_id;

	private Scene initializeGame(GameInformation gameInformation) {
		Scene scene = gameInformation.getGameProtocol().initializeScene(gameInformation);

		DatabaseConnection connection = null;
		try {
			connection = Database.getConnection();
			connection.setAutoCommit(false);

			PreparedStatement statement = connection.prepareStatement("insert into game (starttime) values(?)",
					Statement.RETURN_GENERATED_KEYS);
			statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			statement.executeUpdate();

			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				this.game_id = rs.getInt(1);
			}
			statement.close();

			connection.commit();
		} catch (SQLException e) {
			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (SQLException ignore) {
			}
			e.printStackTrace();
		}

		return scene;
	}

	private void finalizeGame(ActionResult result, List<Pair<Action, String>> hisotry) {
		int winner = 9;
		if (result != null && result.status != null) {
			if (result.status.winners != null && result.status.winners.size() > 0) {
				winner = result.status.winners.get(0).getId();
			}
		}

		DatabaseConnection connection = null;
		try {
			connection = Database.getConnection();

			Timer.start("update game", "finalizeGame");
			PreparedStatement statement = connection.prepareStatement("update game set winner=?,endtime=? where id=?");
			statement.setInt(1, winner);
			statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			statement.setInt(3, this.game_id);
			statement.executeUpdate();
			Timer.stop("update game");

			Logger.history("====================");
			for (Pair<Action, String> pair : hisotry) {
				String hash = pair.getSecond();

				Timer.start("update", "finalizeGame");

				Timer.start("select_hash", "update");
				PreparedStatement sceneStatement = connection.prepareStatement("select id from on_scene where hash=?");
				sceneStatement.setString(1, hash);

				ResultSet sceneRs = sceneStatement.executeQuery();
				Timer.stop("select_hash");

				Timer.start("insert_on_scene", "update");
				if (!sceneRs.next()) {
					StringBuilder onSceneSql = new StringBuilder(
							"insert on_scene (hash, first_win_count, second_win_count, draw, total, tumi) ");
					onSceneSql.append("values(?");
					if (winner == 1) {
						onSceneSql.append(",1 ,0 ,0 ");
					} else if (winner == 2) {
						onSceneSql.append(",0 ,1 ,0");
					} else {
						onSceneSql.append(",0 ,0 ,1");
					}
					onSceneSql.append(",1 ,0");
					onSceneSql.append(")");

					PreparedStatement onSceneStatement = connection.prepareStatement(onSceneSql.toString());
					onSceneStatement.setString(1, hash);
					onSceneStatement.executeUpdate();
					onSceneStatement.close();
				} else {
					StringBuilder onSceneSql = new StringBuilder("update on_scene set ");
					if (winner == 1) {
						onSceneSql.append("first_win_count = first_win_count+1");
					} else if (winner == 2) {
						onSceneSql.append("second_win_count = second_win_count+1");
					} else {
						onSceneSql.append("draw = draw+1");
					}
					onSceneSql.append(",total = total+1");
					onSceneSql.append(" where hash = ?");

					PreparedStatement onSceneStatement = connection.prepareStatement(onSceneSql.toString());
					onSceneStatement.setString(1, hash);
					onSceneStatement.executeUpdate();
					onSceneStatement.close();
				}
				sceneStatement.close();
				Timer.stop("insert_on_scene");

				Timer.start("insert_on_game_scene", "update");
				PreparedStatement gameScebeStatement = connection
						.prepareStatement("insert into on_game_scene (game_id, hash) values(?, ?)");
				gameScebeStatement.setInt(1, this.game_id);
				gameScebeStatement.setString(2, hash);
				gameScebeStatement.executeUpdate();
				Timer.stop("insert_on_game_scene");

				Logger.history(SimpleDataFormat.createCommand(pair.getFirst()));
				Timer.stop("update");
			}

			if (hisotry.size() > 0 && ((HonShogiActionStatus) result.status).isTumi) {
				Pair<Action, String> lastPair = hisotry.get(hisotry.size() - 1);
				String onSceneTumiSql = "update on_scene set tumi = 1 where hash = ?";

				PreparedStatement onSceneTumiStatement = connection.prepareStatement(onSceneTumiSql);
				onSceneTumiStatement.setString(1, lastPair.getSecond());
				onSceneTumiStatement.executeUpdate();
				onSceneTumiStatement.close();
			}

			Timer.start("commit game", "finalizeGame");
			connection.commit();
			Timer.stop("commit game");
		} catch (SQLException e) {
			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (SQLException ignore) {
			}
			e.printStackTrace();
		}

		Logger.info(result.status.message + ":" + this.game_id);
	}

}
