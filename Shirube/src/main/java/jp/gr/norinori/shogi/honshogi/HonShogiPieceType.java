package jp.gr.norinori.shogi.honshogi;

import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.PieceType;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.honshogi.piece.Fu;
import jp.gr.norinori.shogi.honshogi.piece.Gin;
import jp.gr.norinori.shogi.honshogi.piece.Hisha;
import jp.gr.norinori.shogi.honshogi.piece.Kaku;
import jp.gr.norinori.shogi.honshogi.piece.Keima;
import jp.gr.norinori.shogi.honshogi.piece.Kin;
import jp.gr.norinori.shogi.honshogi.piece.Kyo;
import jp.gr.norinori.shogi.honshogi.piece.Narigin;
import jp.gr.norinori.shogi.honshogi.piece.Narikei;
import jp.gr.norinori.shogi.honshogi.piece.Narikyo;
import jp.gr.norinori.shogi.honshogi.piece.Ou;
import jp.gr.norinori.shogi.honshogi.piece.Ryu;
import jp.gr.norinori.shogi.honshogi.piece.Tokin;
import jp.gr.norinori.shogi.honshogi.piece.Uma;

/**
 * 本将棋駒タイプ
 *
 * @author nori
 *
 */
public abstract class HonShogiPieceType implements PieceType {
	// メソッド=================================================================
	/**
	 * 成れるかを判定する
	 *
	 * @param direction
	 * @param point
	 * @return true:成れる / false:成れない
	 */
	protected boolean canUpgrade(Direction direction, Point point) {

		switch (direction) {
		case UP:
			if (point.y <= 2) {
				return true;
			}
			return false;

		case DOWN:
			if (point.y >= 6) {
				return true;
			}
			return false;

		default:
			break;
		}

		return false;
	}

	/**
	 * 本将棋駒タイプを生成する
	 *
	 * @param typeString タイプ文字
	 * @return 本将棋駒タイプ
	 */
	public static HonShogiPieceType getPieceType(String typeString) {

		switch (typeString) {
		case "歩":
			return new Fu();

		case "香":
			return new Kyo();

		case "桂":
			return new Keima();

		case "銀":
			return new Gin();

		case "金":
			return new Kin();

		case "角":
			return new Kaku();

		case "飛":
			return new Hisha();

		case "王":
		case "玉":
			return new Ou();

		case "と":
			return new Tokin();

		case "全":
			return new Narigin();

		case "圭":
			return new Narikei();

		case "杏":
			return new Narikyo();

		case "馬":
			return new Uma();

		case "竜":
			return new Ryu();

		default:
			break;
		}

		throw new IllegalArgumentException("No Piece Type " + typeString);
	}

	/**
	 * 駒タイプ文字
	 *
	 * @param honShogiPiece 本将棋駒タイプ
	 * @return 駒タイプ文字
	 */
	public static String getPieceName(PieceType honShogiPiece) {

		if (honShogiPiece instanceof Fu) {
			return "歩";
		} else if (honShogiPiece instanceof Kyo) {
			return "香";
		} else if (honShogiPiece instanceof Keima) {
			return "桂";
		} else if (honShogiPiece instanceof Gin) {
			return "銀";
		} else if (honShogiPiece instanceof Kin) {
			return "金";
		} else if (honShogiPiece instanceof Kaku) {
			return "角";
		} else if (honShogiPiece instanceof Hisha) {
			return "飛";
		} else if (honShogiPiece instanceof Ou) {
			return "王";
		} else if (honShogiPiece instanceof Tokin) {
			return "と";
		} else if (honShogiPiece instanceof Narigin) {
			return "全";
		} else if (honShogiPiece instanceof Narikei) {
			return "圭";
		} else if (honShogiPiece instanceof Narikyo) {
			return "杏";
		} else if (honShogiPiece instanceof Uma) {
			return "馬";
		} else if (honShogiPiece instanceof Ryu) {
			return "竜";
		}

		throw new IllegalArgumentException("No Piece Type " + honShogiPiece);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		return true;
	}

	// 抽象メソッド=============================================================
	public abstract boolean addPieceLocations(PieceZoneOfControlManager pieceZoneOfControlManager);

	public abstract PieceType clone();

}
