package jp.gr.norinori.shogi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

		Timer.start("move scene", "total");
		DatabaseConnection connection = null;
		try {
			connection = Database.getConnection();
			connection.setAutoCommit(false);

			// sceneへ移動
			Timer.start("update scene", "move scene");
			StringBuilder sceneSql = new StringBuilder("update scene ");
			sceneSql.append(" inner join on_scene on scene.hash = on_scene.hash");
			sceneSql.append(" set scene.first_win_count = scene.first_win_count + on_scene.first_win_count");
			sceneSql.append(",scene.second_win_count = scene.second_win_count + on_scene.second_win_count");
			sceneSql.append(",scene.draw = scene.draw + on_scene.draw");
			sceneSql.append(",scene.total = scene.total + on_scene.total");
			PreparedStatement onSceneUpdateStatement = connection.prepareStatement(sceneSql.toString());
			onSceneUpdateStatement.executeUpdate();
			Timer.stop("update scene");

			Timer.start("insert scene", "move scene");
			sql = "insert into scene(hash, first_win_count, second_win_count, draw, total, tumi) select hash, first_win_count, second_win_count, draw, total, tumi from on_scene where not exists(select * from scene where scene.hash = on_scene.hash)";
			PreparedStatement onSceneStatement = connection.prepareStatement(sql);
			onSceneStatement.executeUpdate();
			Timer.stop("insert scene");

			Timer.start("delete scene", "move scene");
			onSceneStatement = connection.prepareStatement("delete from on_scene");
			onSceneStatement.executeUpdate();
			Timer.stop("delete scene");

			// game_sceneへ移動
			Timer.start("insert game_scene", "move scene");
			sql = "insert into game_scene(game_id, scene_id) select on_game_scene.game_id, scene.id from on_game_scene inner join scene on scene.hash = on_game_scene.hash";
			PreparedStatement onGameSceneStatement = connection.prepareStatement(sql);
			onGameSceneStatement.executeUpdate();
			Timer.stop("insert game_scene");

			Timer.start("delete on_game_scene", "move scene");
			// on_game_scene削除
			onGameSceneStatement = connection.prepareStatement("delete from on_game_scene");
			onGameSceneStatement.executeUpdate();
			Timer.stop("delete on_game_scene");

			Timer.start("commit", "move scene");
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
		Timer.stop("move scene");

		Timer.start("extend", "total");
		try {
			connection = Database.getConnection();
			connection.setAutoCommit(false);

			sql = "truncate table ex_scene";
			PreparedStatement truncateStatement = connection.prepareStatement(sql);
			truncateStatement.executeUpdate();

			Timer.start("insert ex_scene", "extend");
			sql = "insert into ex_scene(hash, first_win_rate, second_win_rate, total, tumi, reliability) select hash, truncate((truncate((first_win_count+draw/2) / total, 4) - 0.5) * 20000 ,0), truncate((truncate((second_win_count+draw/2) / total, 4) - 0.5) * 20000 ,0), total, tumi, 0 from scene where total > 10;";
			PreparedStatement exSceneStatement = connection.prepareStatement(sql);
			exSceneStatement.executeUpdate();
			Timer.stop("insert ex_scene");

			Timer.start("update ex_scene", "extend");

			PreparedStatement totalStatement = connection.prepareStatement("select max(total) total from ex_scene");

			int total = 0;
			ResultSet totalRs = totalStatement.executeQuery();
			if (totalRs.next()) {
				total = totalRs.getInt("total");
			}

			PreparedStatement selectStatement = connection
					.prepareStatement("select id, (first_win_rate - second_win_rate) / 2 as rate, total from ex_scene");

			ResultSet rs = selectStatement.executeQuery();

			sql = "update ex_scene set reliability=? where id=?";
			PreparedStatement exSceneUpdateStatement = connection.prepareStatement(sql);
			while (rs.next()) {
				double sn = rs.getInt("total");
				double lN = total;
				double rate = rs.getInt("rate");
				double p = (rate + 10000) / 20000;
				double a = 1.96 * 1.96 * p * (1 - p);
				float reliability = 1 - (float)Math.sqrt(a / sn * (1 - sn / lN));

				exSceneUpdateStatement.setFloat(1, reliability);
				exSceneUpdateStatement.setInt(2, rs.getInt("id"));
				exSceneUpdateStatement.executeUpdate();
			}
			Timer.stop("update ex_scene");

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

		Timer.stop("total");

		Logger.useInfo = true;
		Logger.logTimer(Timer.getTreeTimeids());
	}
}
