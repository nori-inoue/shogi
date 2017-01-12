package jp.gr.norinori.shogi.honshogi.piece;

import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.honshogi.HonShogiPieceType;
import jp.gr.norinori.shogi.honshogi.PieceZoneOfControlManager;

public class Narikei extends HonShogiPieceType {
	public final static int ID = 10;

	@Override
	public boolean addPieceLocations(PieceZoneOfControlManager pieceZoneOfControlManager) {
		Direction direction = pieceZoneOfControlManager.player.getDirection();

		Point newPointFront = pieceZoneOfControlManager.point.clone();
		Point newPointBack = pieceZoneOfControlManager.point.clone();

		switch (direction) {
		case UP:
			newPointFront.y -= 1;
			newPointBack.y += 1;
			break;

		case DOWN:
			newPointFront.y += 1;
			newPointBack.y -= 1;
			break;

		default:
			break;
		}

		Point newPointFront1 = newPointFront.clone();
		Point newPointFront2 = newPointFront.clone();
		newPointFront1.x -= 1;
		newPointFront2.x += 1;

		Point newPoint1 = pieceZoneOfControlManager.point.clone();
		Point newPoint2 = pieceZoneOfControlManager.point.clone();
		newPoint1.x -= 1;
		newPoint2.x += 1;

		pieceZoneOfControlManager.validateAndAdd(newPointFront, pieceZoneOfControlManager.piece);
		pieceZoneOfControlManager.validateAndAdd(newPointFront1, pieceZoneOfControlManager.piece);
		pieceZoneOfControlManager.validateAndAdd(newPointFront2, pieceZoneOfControlManager.piece);
		pieceZoneOfControlManager.validateAndAdd(newPoint1, pieceZoneOfControlManager.piece);
		pieceZoneOfControlManager.validateAndAdd(newPoint2, pieceZoneOfControlManager.piece);
		pieceZoneOfControlManager.validateAndAdd(newPointBack, pieceZoneOfControlManager.piece);

		return false;
	}

	public Narikei clone() {
		return new Narikei();
	}

	@Override
	public int hashCode() {
		return ID;
	}
}
