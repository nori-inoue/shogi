package jp.gr.norinori.shogi;

import java.util.ArrayList;
import java.util.List;

/**
 * 影響範囲
 *
 * @author nori
 *
 */
public class EffectiveRange {
	// メンバ===================================================================
	public Point source;
	public List<Point> list;

	// コンストラクタ===========================================================
	/**
	 * 影響範囲のインスタンスを生成する
	 */
	public EffectiveRange() {
		this.list = new ArrayList<>();
	}

	/**
	 * 影響範囲のインスタンスを生成する
	 *
	 * @param source 影響元
	 */
	public EffectiveRange(Point source) {
		this.list = new ArrayList<>();
		this.source = source;
	}

	// メソッド=============================================================
	/**
	 * 影響範囲のポイントを追加する
	 *
	 * @param point
	 */
	public void add(Point point) {
		this.list.add(point);
	}

	/**
	 * クローン
	 */
	public EffectiveRange clone() {
		EffectiveRange newEffectiveRange = new EffectiveRange();
		newEffectiveRange.list = this.list;
		newEffectiveRange.source = this.source;

		return newEffectiveRange;
	}
}
