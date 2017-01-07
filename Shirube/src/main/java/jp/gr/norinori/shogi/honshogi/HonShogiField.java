package jp.gr.norinori.shogi.honshogi;

import jp.gr.norinori.shogi.Field;
import jp.gr.norinori.shogi.Point;

public class HonShogiField implements Field {

    // メンバ===================================================================
	public static byte MIN_X = 0;
	public static byte MAX_X = 8;
	public static byte MIN_Y = 0;
	public static byte MAX_Y = 8;

	public byte x = MAX_X;
	public byte y = MAX_Y;

	/**
	 * 移動可能範囲確認
	 *
	 * @param point
	 * @return true:移動可能 / false:移動不可
	 */
	public static boolean isRange(Point point) {
		if (point.y <= MAX_Y && point.y >= MIN_Y && point.x <= MAX_X && point.x >= MIN_X) {
			return true;
		}
		return false;
	}
}
