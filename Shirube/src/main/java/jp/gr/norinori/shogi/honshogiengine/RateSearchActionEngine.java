package jp.gr.norinori.shogi.honshogiengine;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.gr.norinori.database.DatabaseConnection;
import jp.gr.norinori.shogi.Action;
import jp.gr.norinori.shogi.ActionEngine;
import jp.gr.norinori.shogi.ActionStatus;
import jp.gr.norinori.shogi.Database;
import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.Logger;
import jp.gr.norinori.shogi.PieceMove;
import jp.gr.norinori.shogi.Player;
import jp.gr.norinori.shogi.Scene;
import jp.gr.norinori.shogi.Timer;
import jp.gr.norinori.shogi.honshogi.HonShogi;
import jp.gr.norinori.shogi.honshogi.HonShogiActionStatus;
import jp.gr.norinori.shogi.honshogi.HonShogiPieceType;
import jp.gr.norinori.shogi.honshogi.HonShogiPlayer;
import jp.gr.norinori.shogi.honshogi.HonShogiScene;
import jp.gr.norinori.shogi.honshogi.LoggerLabel;
import jp.gr.norinori.shogi.honshogi.piece.Fu;
import jp.gr.norinori.shogi.honshogi.piece.Hisha;
import jp.gr.norinori.shogi.honshogi.piece.Kaku;

public class RateSearchActionEngine implements ActionEngine {

	// メソッド=================================================================

	@Override
	public Action action(Scene scene) {
		LoggerLabel.pieceZoneOfControl = Timer.start("pieceZoneOfControl", "action", LoggerLabel.pieceZoneOfControl);
		Player player = scene.getInitiativePlayer();

		List<PieceMove> movableList;
		List<PieceMove> outePieceMoveList = ((HonShogiScene) scene).getOuteEscapeList(player);
		if (outePieceMoveList != null && !outePieceMoveList.isEmpty()) {
			movableList = outePieceMoveList;
		} else {
			movableList = scene.getPieceZoneOfControl(player).getList();
		}

		Action action = new Action();
		if (movableList.isEmpty()) {
			ActionStatus status = new HonShogiActionStatus();
			status.isEnd = true;
			status.message = "移動可能駒なし：投了";
			return action;
		}
		Timer.stop(LoggerLabel.pieceZoneOfControl);

		LoggerLabel.checkTumi = Timer.start("checkTumi", "action", LoggerLabel.checkTumi);
		List<PieceMove> removePieceMoveList = new ArrayList<>();
		HonShogi honshogi = (HonShogi) scene.getGameInformation().getGameProtocol();
		DatabaseConnection connection = null;

		int maxRate = -10000;
		Action maxAction = null;
		try {
			connection = Database.getConnection();
			PreparedStatement statement = connection.prepareStatement("select * from ex_scene where hash=?");

			for (PieceMove pieceMove : movableList) {

				// 打ち駒でない場合、歩／飛／角は成優先
				if (pieceMove.from != null) {
					HonShogiPieceType piceType = (HonShogiPieceType) pieceMove.toPiece.type;
					if (player.getDirection() == Direction.UP) {
						if (piceType.hashCode() == Fu.ID && pieceMove.to.y <= 2) {
							removePieceMoveList.add(pieceMove);
							continue;
						}
						if (piceType.hashCode() == Kaku.ID && (pieceMove.to.y <= 2 || pieceMove.from.y <= 2)) {
							removePieceMoveList.add(pieceMove);
							continue;
						}
						if (piceType.hashCode() == Hisha.ID && (pieceMove.to.y <= 2 || pieceMove.from.y <= 2)) {
							removePieceMoveList.add(pieceMove);
							continue;
						}
					} else {
						if (piceType.hashCode() == Fu.ID && pieceMove.to.y >= 6) {
							removePieceMoveList.add(pieceMove);
							continue;
						}
						if (piceType.hashCode() == Kaku.ID && (pieceMove.to.y >= 6 || pieceMove.from.y >= 6)) {
							removePieceMoveList.add(pieceMove);
							continue;
						}
						if (piceType.hashCode() == Hisha.ID && (pieceMove.to.y >= 6 || pieceMove.from.y >= 6)) {
							removePieceMoveList.add(pieceMove);
							continue;
						}
					}
				}

				Action checkAction = new Action();
				checkAction.setFromPieceMove(pieceMove);
				LoggerLabel.sceneClone = Timer.start("scene clone", "checkTumi", LoggerLabel.sceneClone);
				HonShogiScene checkScene = (HonShogiScene) scene.clone();
				Timer.stop(LoggerLabel.sceneClone);

				LoggerLabel.analyzeSceneCheckTumi = Timer.start("analyzeScene checkTumi", "checkTumi");
				HonShogiActionStatus checkStatus = (HonShogiActionStatus) honshogi.nextPhage(checkScene, checkAction);
				Timer.stop(LoggerLabel.analyzeSceneCheckTumi);

				if (checkStatus.isTumi) {
					// 打ち歩詰め禁止
					if ((checkAction.from == null) && checkAction.toPiece.type instanceof Fu) {
						removePieceMoveList.add(pieceMove);
						continue;
					}

					Logger.debug(movableList.toString());
					Logger.debug("選択:詰みあり " + pieceMove);
					return checkAction;
				}

				// ハッシュから勝率を取得
				statement.setString(1, checkScene.getHash());
				ResultSet sceneRs = statement.executeQuery();
				if (sceneRs.next()) {
					if (scene.getInitiativePlayer().getId() == HonShogiPlayer.SENTE) {
						int first_win_rate = sceneRs.getInt("first_win_rate");
						if (first_win_rate != 0 && maxRate < first_win_rate) {
							maxRate = first_win_rate;
							maxAction = checkAction;
						}
					} else {
						int second_win_rate = sceneRs.getInt("second_win_rate");
						if (second_win_rate != 0 && maxRate < second_win_rate) {
							maxRate = second_win_rate;
							maxAction = checkAction;
						}
					}
				}
			}
		} catch (SQLException e) {
			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (SQLException ignore) {
			}
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
			}
		}

		// 高勝率アクションが存在する場合はそれを返す。それ以外はランダム
		if (maxAction != null) {
			Logger.debug(maxAction.toString());
			Logger.debug("Rate:" + maxRate);
			return maxAction;
		}

		// 移動不可分は削除
		for (PieceMove pieceMove : removePieceMoveList) {
			movableList.remove(pieceMove);
		}
		if (movableList.isEmpty()) {
			ActionStatus status = new HonShogiActionStatus();
			status.isEnd = true;
			status.message = "移動可能駒なし：投了";
			return action;
		}

		Timer.stop(LoggerLabel.checkTumi);

		Random random = new Random();
		int index = random.nextInt(movableList.size());
		PieceMove pieceMove = movableList.get(index);

		Logger.debug(movableList.toString());
		Logger.debug("選択:" + index + " " + pieceMove);
		action.setFromPieceMove(pieceMove);

		return action;
	}

}
