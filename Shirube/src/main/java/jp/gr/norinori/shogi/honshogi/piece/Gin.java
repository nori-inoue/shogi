package jp.gr.norinori.shogi.honshogi.piece;

import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.Piece;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.honshogi.HonShogi;
import jp.gr.norinori.shogi.honshogi.HonShogiPieceType;
import jp.gr.norinori.shogi.honshogi.PieceZoneOfControlManager;

public class Gin extends HonShogiPieceType {
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

		Point newPointBack1 = newPointBack.clone();
		Point newPointBack2 = newPointBack.clone();
		newPointBack1.x -= 1;
		newPointBack2.x += 1;

		pieceZoneOfControlManager.validateAndAdd(newPointFront);
		pieceZoneOfControlManager.validateAndAdd(newPointFront1);
		pieceZoneOfControlManager.validateAndAdd(newPointFront2);
		pieceZoneOfControlManager.validateAndAdd(newPointBack1);
		pieceZoneOfControlManager.validateAndAdd(newPointBack2);

		if (canUpgrade(direction, newPointFront)) {
			Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Narigin());
			pieceZoneOfControlManager.validateAndAdd(newPointFront, newPiece);
		}
		if (canUpgrade(direction, newPointFront1)) {
			Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Narigin());
			pieceZoneOfControlManager.validateAndAdd(newPointFront1, newPiece);
		}
		if (canUpgrade(direction, newPointFront2)) {
			Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Narigin());
			pieceZoneOfControlManager.validateAndAdd(newPointFront2, newPiece);
		}

		if (canUpgrade(direction, pieceZoneOfControlManager.point)) {
			Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Narigin());
			pieceZoneOfControlManager.validateAndAdd(newPointBack1, newPiece);
			pieceZoneOfControlManager.validateAndAdd(newPointBack2, newPiece);
		}

		return false;
	}

	public Gin clone() {
		return new Gin();
	}

	@Override
	public int hashCode() {
		return 4;
	}
}
