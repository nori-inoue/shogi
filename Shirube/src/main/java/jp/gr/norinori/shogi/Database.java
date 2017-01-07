package jp.gr.norinori.shogi;

import java.sql.SQLException;

import jp.gr.norinori.database.DatabaseConnection;
import jp.gr.norinori.database.mysql.MySql;

public class Database {

	private static DatabaseConnection connection = null;

	// 定数=====================================================================
	private final static String RESOURCE_PATH = "./src/main/resources/jp/gr/norinori/shogi";

	// メソッド=============================================================
	public static DatabaseConnection getConnection() {

		try {
			if (Database.connection == null || !Database.connection.isLiving()) {

				MySql db = new MySql();
				db.configure(RESOURCE_PATH + "/db.properties");
				Database.connection = db.createConnection();
			}

			return Database.connection;

		} catch (Exception e) {
			Logger.error(e.getMessage());
		}

		return null;
	}

	public static void close() {

		if (Database.connection != null) {
			try {
				Database.connection.close();
			} catch (SQLException e) {
				Logger.error(e.getMessage());
			}
		}
	}

}
