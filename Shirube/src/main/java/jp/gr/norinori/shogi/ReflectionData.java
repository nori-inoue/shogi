package jp.gr.norinori.shogi;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.gr.norinori.application.Arguments;
import jp.gr.norinori.application.CommandLineArgument;
import jp.gr.norinori.database.DatabaseConnection;

public class ReflectionData {

	public static void main(String[] args) {
		Arguments arguments = new CommandLineArgument(args);
		ReflectionData me = new ReflectionData();
		me.run(arguments);
	}

	public void run(Arguments arguments) {
		String sql;
		Timer.start("total");
		DatabaseConnection connection = null;
		try {
			connection = Database.getConnection();
			connection.setAutoCommit(false);

			// sceneへ移動
			Timer.start("update scene", "total");
			StringBuilder sceneSql = new StringBuilder("update scene ");
			sceneSql.append(" inner join on_scene on scene.hash = on_scene.hash");
			sceneSql.append(" set scene.first_win_count = scene.first_win_count + on_scene.first_win_count");
			sceneSql.append(",scene.second_win_count = scene.second_win_count + on_scene.second_win_count");
			sceneSql.append(",scene.draw = scene.draw + on_scene.draw");
			sceneSql.append(",scene.total = scene.total + on_scene.total");
			PreparedStatement onSceneUpdateStatement = connection.prepareStatement(sceneSql.toString());
			onSceneUpdateStatement.executeUpdate();
			Timer.stop("update scene");

			Timer.start("insert scene", "total");
			sql = "insert into scene(hash, first_win_count, second_win_count, draw, total, tumi) select hash, first_win_count, second_win_count, draw, total, tumi from on_scene where not exists(select * from scene where scene.hash = on_scene.hash)";
			PreparedStatement onSceneStatement = connection.prepareStatement(sql);
			onSceneStatement.executeUpdate();
			Timer.stop("insert scene");

			Timer.start("delete scene", "total");
			onSceneStatement = connection.prepareStatement("delete from on_scene");
			onSceneStatement.executeUpdate();
			Timer.stop("delete scene");

			// game_sceneへ移動
			Timer.start("insert game_scene", "total");
			sql = "insert into game_scene(game_id, scene_id) select on_game_scene.game_id, scene.id from on_game_scene inner join scene on scene.hash = on_game_scene.hash";
			PreparedStatement onGameSceneStatement = connection.prepareStatement(sql);
			onGameSceneStatement.executeUpdate();
			Timer.stop("insert game_scene");

			Timer.start("delete on_game_scene", "total");
			// on_game_scene削除
			onGameSceneStatement = connection.prepareStatement("delete from on_game_scene");
			onGameSceneStatement.executeUpdate();
			Timer.stop("delete on_game_scene");

			Timer.start("commit", "total");
			connection.commit();
			Timer.stop("commit");
		} catch (SQLException e) {
			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (SQLException ignore) {
			}
			e.printStackTrace();
		}
		Timer.stop("total");


		Timer.start("extend");
		try {
			connection = Database.getConnection();
			connection.setAutoCommit(false);

			sql = "truncate table ex_scene";
			PreparedStatement truncateStatement = connection.prepareStatement(sql);
			truncateStatement.executeUpdate();

			Timer.start("insert ex_scene", "extend");
			sql = "insert into ex_scene(hash, first_win_rate, second_win_rate, total, tumi) select hash, truncate((truncate((first_win_count+draw/2) / total, 4) - 0.5) * 20000 ,0), truncate((truncate((second_win_count+draw/2) / total, 4) - 0.5) * 20000 ,0), total, tumi from scene where total > 10;";
			PreparedStatement exSceneStatement = connection.prepareStatement(sql);
			exSceneStatement.executeUpdate();
			Timer.stop("insert ex_scene");

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
		Timer.stop("extend");

		Logger.logTimer(Timer.getTreeTimeids());
	}
}
