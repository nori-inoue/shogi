package jp.gr.norinori.shogi;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import jp.gr.norinori.core.collection.TreeNode;
import jp.gr.norinori.define.Encoding;
import jp.gr.norinori.utility.StringUtil;

public class Logger {
	public static boolean useDebug = false;
	public static boolean useInfo = false;
	public static boolean useHistory = false;

	private static java.util.logging.Logger logger;
	private static java.util.logging.Logger debugLogger;
	private static java.util.logging.Logger historyLogger;

	/**
	 * 情報ログ
	 *
	 * @param message ログメッセージ
	 */
	public static void info(String message) {
		if (!useInfo) {
			return;
		}
		if (logger == null) {
			initializeInfo();
		}
		logger.info(message);
	}

	/**
	 * 情報ログ
	 *
	 * @param messages ログメッセージ配列
	 */
	public static void info(String[] messages) {
		if (!useInfo) {
			return;
		}
		if (logger == null) {
			initializeInfo();
		}
		for (String message : messages) {
			logger.info(message);
		}
	}

	/**
	 * デバッグログ
	 *
	 * @param message ログメッセージ
	 */
	public static void debug(String message) {
		if (!useDebug) {
			return;
		}
		if (debugLogger == null) {
			initializeDebug();
		}
		debugLogger.info(message);
	}

	/**
	 * エラーログ
	 *
	 * @param message ログメッセージ
	 */
	public static void error(String message) {
		if (debugLogger == null) {
			initializeDebug();
		}
		debugLogger.info(message);
	}

	/**
	 * 履歴ログ
	 *
	 * @param message ログメッセージ
	 */
	public static void history(String message) {
		if (!useHistory) {
			return;
		}
		if (historyLogger == null) {
			initializeHistory();
		}
		historyLogger.info(message);
	}

	/**
	 * ツリーログ出力
	 *
	 * @param rootNode ルートノード
	 */
	public static void logTimer(TreeNode<String> rootNode) {
		for (TreeNode<String> node : rootNode.children()) {
			Logger.logTimer(node, 0);
		}
	}

	/**
	 * ツリーログ出力
	 *
	 * @param node ノード
	 * @param depth 深さ
	 */
	public static void logTimer(TreeNode<String> node, int depth) {
		String pad = "";
		if (depth > 0) {
			pad = StringUtil.pad(" ", depth * 2);
		}
		Logger.info(pad + node.getValue() + ":" + StringUtil.formatNumber(Timer.getTotal(node.getValue()), "#,###")
				+ "ms (" + StringUtil.formatNumber(Timer.getCount(node.getValue()), "#,###") + ")");

		for (TreeNode<String> child : node.children()) {
			logTimer(child, depth + 1);
		}
	}

	private static void initializeInfo() {
		logger = java.util.logging.Logger.getLogger("MyLogger");
		logger.setLevel(Level.INFO);

		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(new SimpleFormatter());
		consoleHandler.setLevel(Level.INFO);

		FileHandler fileHandler = null;
		try {
			fileHandler = new FileHandler("result.log", true);
			fileHandler.setEncoding(Encoding.UTF8.getCode());
			fileHandler.setFormatter(new Formatter() {
				@Override
				public String format(LogRecord record) {
					return record.getMessage() + System.lineSeparator();
				}
			});
			fileHandler.setLevel(Level.INFO);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 親ロガーへ通知しない
		logger.setUseParentHandlers(false);
		logger.addHandler(consoleHandler);
		logger.addHandler(fileHandler);
	}

	private static void initializeDebug() {
		debugLogger = java.util.logging.Logger.getLogger("MyDebugLogger");
		debugLogger.setLevel(Level.CONFIG);

		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(new SimpleFormatter());
		consoleHandler.setLevel(Level.CONFIG);

		FileHandler fileHandler = null;
		try {
			fileHandler = new FileHandler("debug.log");
			fileHandler.setEncoding(Encoding.UTF8.getCode());
			fileHandler.setFormatter(new Formatter() {
				@Override
				public String format(LogRecord record) {
					return record.getMessage() + System.lineSeparator();
				}
			});
			fileHandler.setLevel(Level.CONFIG);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 親ロガーへ通知しない
		debugLogger.setUseParentHandlers(false);
		debugLogger.addHandler(consoleHandler);
		debugLogger.addHandler(fileHandler);
	}

	private static void initializeHistory() {
		historyLogger = java.util.logging.Logger.getLogger("MyHistoryLogger");
		historyLogger.setLevel(Level.CONFIG);

		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(new SimpleFormatter());
		consoleHandler.setLevel(Level.CONFIG);

		FileHandler fileHandler = null;
		try {
			fileHandler = new FileHandler("history.log");
			fileHandler.setEncoding(Encoding.UTF8.getCode());
			fileHandler.setFormatter(new Formatter() {
				@Override
				public String format(LogRecord record) {
					return record.getMessage() + System.lineSeparator();
				}
			});
			fileHandler.setLevel(Level.CONFIG);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 親ロガーへ通知しない
		historyLogger.setUseParentHandlers(false);
		historyLogger.addHandler(consoleHandler);
		historyLogger.addHandler(fileHandler);
	}
}
