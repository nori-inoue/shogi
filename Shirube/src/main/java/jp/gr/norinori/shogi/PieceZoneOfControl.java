package jp.gr.norinori.shogi;

import java.util.List;

import jp.gr.norinori.core.collection.NumberingMap;

/**
 * ZOC情報
 *
 * @author nori
 */
public interface PieceZoneOfControl {

	/**
	 * 移動可能情報を追加する
	 *
	 * @param pieceMove 移動可能情報
	 */
	public void add(PieceMove pieceMove);

	/**
	 * 移動可能情報から除外する
	 *
	 * @param pieceMove 移動可能情報
	 */
	public void remove(PieceMove pieceMove);

	/**
	 * 移動可能情報リストを取得する
	 *
	 * @return 移動可能情報リスト
	 */
	public List<PieceMove> getList();

	/**
	 * 移動可能な駒が存在するかを判定する
	 *
	 * @return true:存在する / false:存在しない
	 */
	public boolean isEmpty();

	/**
	 * 影響範囲を追加する
	 *
	 * @param point 位置
	 * @param piece 駒
	 */
	public void addEffectiveRange(Point point, Piece piece);

	/**
	 * 影響範囲のソースを設定する
	 *
	 * @param point 位置
	 * @param piece 駒
	 */
	public void setEffectiveRangeSource(Point point, Piece piece);

	/**
	 * 影響範囲を取得する
	 *
	 * @return 影響範囲
	 */
	public NumberingMap<Piece, EffectiveRange> getEffectiveRanges();

}
