package jp.gr.norinori.shogi;

/**
 * 駒
 *
 * @author nori
 *
 */
public class Piece {
	public final PieceType type;
	public final String name;

	// コンストラクタ===========================================================
	/**
	 * 駒のインスタンスを生成する
	 *
	 * @param type 駒タイプ
	 * @param name 駒名称
	 */
	public Piece(PieceType type, String name) {
		this.type = type;
		this.name = name;
	}

	@Override
	public String toString() {
		return "Piece [name=" + name + "]";
	}
}
