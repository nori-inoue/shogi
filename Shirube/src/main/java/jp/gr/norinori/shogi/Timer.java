package jp.gr.norinori.shogi;

import jp.gr.norinori.core.collection.TreeNode;
import jp.gr.norinori.core.time.RoutineTimer;

/**
 * 処理時間測定
 *
 * @author nori
 *
 */
public class Timer {
	/**
	 * デバッグモード
	 */
	public static boolean debug = true;

	// メンバ===================================================================
	private static RoutineTimer timer = null;

	// メソッド=================================================================
	/**
	 * 時間計測を開始する(非デバッグ）
	 *
	 * @param timeid 計測対象のタイムＩＤ
	 * @param parentTimeid 計測対象の親タイムＩＤ
	 * @return タイマＩＤ
	 */
	public static int startInfo(String timeid, String parentTimeid) {
		if (timer == null) {
			initialize();
		}
		return timer.start(timeid, parentTimeid);
	}

	/**
	 * 時間計測を開始する(非デバッグ）
	 *
	 * @param timeid 計測対象のタイムＩＤ
	 * @return タイマＩＤ
	 */
	public static int startInfo(String timeid) {
		if (timer == null) {
			initialize();
		}
		return timer.start(timeid);
	}

	/**
	 * 時間計測を停止する(非デバッグ）
	 *
	 * @param timeid 計測対象のタイムＩＤ
	 */
	public static void stopInfo(String timeid) {
		if (timer == null) {
			initialize();
		}
		timer.stop(timeid);
	}

	/**
	 * 時間計測を開始する
	 *
	 * @param timeid 計測対象のタイムＩＤ
	 * @param parentTimeid 計測対象の親タイムＩＤ
	 * @return タイマＩＤ
	 */
	public static int start(String timeid, String parentTimeid) {
		if (!debug)
			return -1;

		if (timer == null) {
			initialize();
		}
		return timer.start(timeid, parentTimeid);
	}

	/**
	 * 時間計測を開始する
	 *
	 * @param timeid 計測対象のタイムＩＤ
	 * @param parentTimeid 計測対象の親タイムＩＤ
	 * @param timerid 計測対象のタイマＩＤ
	 * @return タイマＩＤ
	 */
	public static int start(String timeid, String parentTimeid, int timerid) {
		if (!debug)
			return -1;

		if (timer == null) {
			initialize();
		}
		if (timerid >= 0) {
			return timer.start(timerid);
		}
		return timer.start(timeid, parentTimeid);
	}

	/**
	 * 時間計測を開始する
	 *
	 * @param timeid 計測対象のタイムＩＤ
	 * @return タイマＩＤ
	 */
	public static int start(String timeid) {
		if (!debug)
			return -1;

		if (timer == null) {
			initialize();
		}
		return timer.start(timeid);
	}

	/**
	 * 時間計測を開始する
	 *
	 * @param timeid 計測対象のタイムＩＤ
	 * @param timerid 計測対象のタイマＩＤ
	 * @return タイマＩＤ
	 */
	public static int start(String timeid, int timerid) {
		if (!debug)
			return -1;

		if (timer == null) {
			initialize();
		}
		if (timerid >= 0) {
			return timer.start(timerid);
		}
		return timer.start(timeid);
	}

	/**
	 * 時間計測を開始する
	 *
	 * @param timerid 計測対象のタイマＩＤ
	 * @return タイマＩＤ
	 */
	public static int start(int timerid) {
		if (!debug)
			return -1;

		if (timer == null) {
			initialize();
		}
		return timer.start(timerid);
	}

	/**
	 * 時間計測を停止する
	 *
	 * @param timeid 計測対象のタイムＩＤ
	 */
	public static void stop(String timeid) {
		if (!debug)
			return;

		if (timer == null) {
			initialize();
		}
		timer.stop(timeid);
	}

	/**
	 * 時間計測を停止する
	 *
	 * @param timerid 計測対象のタイマＩＤ
	 */
	public static void stop(int timerid) {
		if (!debug)
			return;

		if (timer == null) {
			initialize();
		}
		timer.stop(timerid);
	}

	/**
	 * 時間計測結果を取得する
	 *
	 * @param timeid 計測対象のタイムＩＤ
	 * @return 時間計測結果(ms)
	 */
	public static long getTotal(String timeid) {
		if (timer == null) {
			return 0;
		}
		return timer.getTotal(timeid);
	}

	/**
	 * 時間計測回数を取得する
	 *
	 * @param timeid 計測対象のタイムＩＤ
	 * @return 時間計測回数
	 */
	public static long getCount(String timeid) {
		if (timer == null) {
			return 0;
		}
		return timer.getCount(timeid);
	}

	/**
	 * 計測対象のタイムＩＤの配列を取得する
	 *
	 * @return 計測対象のタイムＩＤの配列
	 */
	public static String[] getTimeids() {
		if (timer == null) {
			initialize();
		}
		return timer.getTimeids();
	}

	/**
	 * 計測対象のタイムＩＤの配列をツリーを取得する
	 *
	 * @return 計測対象のタイムＩＤの配列
	 */
	public static TreeNode<String> getTreeTimeids() {
		if (timer == null) {
			initialize();
		}
		return timer.getTreeTimeids();
	}

	/**
	 * タイマーをリセットする
	 */
	public static void reset() {
		initialize();
	}

	private static void initialize() {
		timer = new RoutineTimer();
	}
}
