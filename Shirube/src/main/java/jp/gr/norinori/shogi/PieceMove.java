package jp.gr.norinori.shogi;

/**
 * 移動情報
 *
 * @author nori
 */
public class PieceMove {
	public final Point from;
	public final Point to;
	public final Piece fromPiece;
	public final Piece toPiece;

	// コンストラクタ===========================================================
	/**
	 * 移動情報のインスタンスを生成する
	 *
	 * @param from 移動元情報
	 * @param to 移動先情報
	 * @param piece 移動駒
	 */
	public PieceMove(Point from, Point to, Piece piece) {
		this(from, to, piece, piece);
	}

	/**
	 * 移動情報のインスタンスを生成する(駒が変更する場合)
	 *
	 * @param from 移動元情報
	 * @param to 移動先情報
	 * @param fromPiece 移動元駒
	 * @param toPiece 移動先駒
	 */
	public PieceMove(Point from, Point to, Piece fromPiece, Piece toPiece) {
		this.from = from;
		this.to = to;
		this.fromPiece = fromPiece;
		this.toPiece = toPiece;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.from != null) {
			sb.append("FROM:" + this.from);
		}
		sb.append(" TO:" + this.to + " " + this.fromPiece);
		if (this.fromPiece != null && this.fromPiece.type != null && this.toPiece != null
				&& !this.fromPiece.type.equals(this.toPiece.type)) {
			sb.append(" => " + this.toPiece);
		}

		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((fromPiece == null || fromPiece.type == null) ? 0 : fromPiece.type.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		result = prime * result + ((toPiece == null || toPiece.type == null) ? 0 : toPiece.type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		PieceMove other = (PieceMove) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;

		if (fromPiece == null) {
			if (other.fromPiece != null)
				return false;
		} else if (fromPiece.type == null || other.fromPiece.type == null) {
			return false;
		} else if (!fromPiece.type.equals(other.fromPiece.type))
			return false;

		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;

		if (toPiece == null) {
			if (other.toPiece != null)
				return false;
		} else if (toPiece.type == null || other.toPiece.type == null) {
			return false;
		} else if (!toPiece.type.equals(other.toPiece.type))
			return false;

		return true;
	}

}
