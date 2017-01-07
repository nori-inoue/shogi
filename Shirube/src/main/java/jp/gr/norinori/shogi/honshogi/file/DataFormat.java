package jp.gr.norinori.shogi.honshogi.file;

import java.util.List;

import jp.gr.norinori.shogi.PieceMove;

public interface DataFormat {
	/**
	 *
	 * @return 移動可能情報リスト
	 */
	public List<PieceMove> getPieceMoveList();
}
