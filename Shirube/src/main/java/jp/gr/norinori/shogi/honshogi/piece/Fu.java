package jp.gr.norinori.shogi.honshogi.piece;

import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.Piece;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.honshogi.HonShogi;
import jp.gr.norinori.shogi.honshogi.HonShogiPieceType;
import jp.gr.norinori.shogi.honshogi.PieceZoneOfControlManager;

public class Fu extends HonShogiPieceType {
	public final static int ID = 1;

	@Override
	public boolean addPieceLocations(PieceZoneOfControlManager pieceZoneOfControlManager) {
		Direction direction = pieceZoneOfControlManager.player.getDirection();
		Point newPoint = pieceZoneOfControlManager.point.clone();
		switch (direction) {
		case UP:
			newPoint.y -= 1;
			break;

		case DOWN:
			newPoint.y += 1;
			break;

		default:
			break;
		}

		pieceZoneOfControlManager.validateAndAdd(newPoint);
		if (canUpgrade(direction, newPoint)) {
			Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Tokin());
			pieceZoneOfControlManager.validateAndAdd(newPoint, newPiece);
		}
		return false;
	}

	public Fu clone() {
		return new Fu();
	}

	@Override
	public int hashCode() {
		return ID;
	}
}
