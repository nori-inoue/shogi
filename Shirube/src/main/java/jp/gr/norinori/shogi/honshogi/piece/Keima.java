package jp.gr.norinori.shogi.honshogi.piece;

import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.Piece;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.honshogi.HonShogi;
import jp.gr.norinori.shogi.honshogi.HonShogiPieceType;
import jp.gr.norinori.shogi.honshogi.PieceZoneOfControlManager;

public class Keima extends HonShogiPieceType {

	@Override
	public boolean addPieceLocations(PieceZoneOfControlManager pieceZoneOfControlManager) {
		Direction direction = pieceZoneOfControlManager.player.getDirection();

		Point newPoint = pieceZoneOfControlManager.point.clone();
		switch (direction) {
		case UP:
			newPoint.y -= 2;
			break;

		case DOWN:
			newPoint.y += 2;
			break;

		default:
			break;
		}
		Point newPoint1 = newPoint.clone();
		Point newPoint2 = newPoint.clone();
		newPoint1.x -= 1;
		newPoint2.x += 1;

		pieceZoneOfControlManager.validateAndAdd(newPoint1);
		pieceZoneOfControlManager.validateAndAdd(newPoint2);

		if (canUpgrade(direction, newPoint1)) {
			Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Narikei());
			pieceZoneOfControlManager.validateAndAdd(newPoint1, newPiece);
		}
		if (canUpgrade(direction, newPoint2)) {
			Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Narikei());
			pieceZoneOfControlManager.validateAndAdd(newPoint2, newPiece);
		}

		return false;
	}

	public Keima clone() {
		return new Keima();
	}

	@Override
	public int hashCode() {
		return 3;
	}
}
