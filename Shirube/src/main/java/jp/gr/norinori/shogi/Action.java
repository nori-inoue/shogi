package jp.gr.norinori.shogi;

/**
 * アクション情報
 *
 * @author nori
 */
public class Action {
	public Point from;
	public Point to;
	public Piece fromPiece;
	public Piece toPiece;
	public ActionStatus status;

	// コンストラクタ===========================================================
	/**
	 * アクション情報のインスタンスを生成する
	 */
	public Action() {
	}

	// メソッド=============================================================
	/**
	 * 移動情報を元にアクションを設定する
	 *
	 * @param pieceMove
	 */
	public void setFromPieceMove(PieceMove pieceMove) {
		this.from = pieceMove.from;
		this.fromPiece = pieceMove.fromPiece;
		this.to = pieceMove.to;
		this.toPiece = pieceMove.toPiece;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (this.from != null && this.from != null) {
			sb.append(this.from);
		} else {
			sb.append("  ");
		}
		sb.append(" => ");
		if (this.to != null && this.to != null) {
			sb.append(this.to);
		}
		sb.append(" ");
		if (this.fromPiece != null) {
			sb.append(this.fromPiece);
			if (this.toPiece != null && !this.fromPiece.type.equals(this.toPiece.type)) {
				sb.append("成");
			}
		}

		return sb.toString();
	}
}
