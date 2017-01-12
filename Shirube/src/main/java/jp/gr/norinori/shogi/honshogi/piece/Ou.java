package jp.gr.norinori.shogi.honshogi.piece;

import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.honshogi.HonShogiPieceType;
import jp.gr.norinori.shogi.honshogi.PieceZoneOfControlManager;

public class Ou extends HonShogiPieceType {
	public final static int ID = 15;

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

		Point newPointBack1 = newPointBack.clone();
		Point newPointBack2 = newPointBack.clone();
		newPointBack1.x -= 1;
		newPointBack2.x += 1;

		validateAndAdd(pieceZoneOfControlManager, newPointFront);
		validateAndAdd(pieceZoneOfControlManager, newPointFront1);
		validateAndAdd(pieceZoneOfControlManager, newPointFront2);
		validateAndAdd(pieceZoneOfControlManager, newPoint1);
		validateAndAdd(pieceZoneOfControlManager, newPoint2);
		validateAndAdd(pieceZoneOfControlManager, newPointBack);
		validateAndAdd(pieceZoneOfControlManager, newPointBack1);
		validateAndAdd(pieceZoneOfControlManager, newPointBack2);

		return false;
	}

	private void validateAndAdd(PieceZoneOfControlManager pieceZoneOfControlManager, Point point) {
		pieceZoneOfControlManager.validateAndAdd(point, pieceZoneOfControlManager.piece);
	}

	public Ou clone() {
		return new Ou();
	}

	@Override
	public int hashCode() {
		return ID;
	}
}
