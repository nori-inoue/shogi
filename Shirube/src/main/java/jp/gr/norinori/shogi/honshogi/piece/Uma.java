package jp.gr.norinori.shogi.honshogi.piece;

import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.honshogi.HonShogiField;
import jp.gr.norinori.shogi.honshogi.HonShogiPieceType;
import jp.gr.norinori.shogi.honshogi.PieceZoneOfControlManager;

public class Uma extends HonShogiPieceType {
	public final static int ID = 13;

	@Override
	public boolean addPieceLocations(PieceZoneOfControlManager pieceZoneOfControlManager) {
		HonShogiField field = (HonShogiField) pieceZoneOfControlManager.honShogeiScene.getField();

		validateAndAdd(1, 1, pieceZoneOfControlManager, field);
		validateAndAdd(1, -1, pieceZoneOfControlManager, field);
		validateAndAdd(-1, -1, pieceZoneOfControlManager, field);
		validateAndAdd(-1, 1, pieceZoneOfControlManager, field);

		Point newPointFront = pieceZoneOfControlManager.point.clone();
		Point newPointBack = pieceZoneOfControlManager.point.clone();

		newPointFront.y -= 1;
		newPointBack.y += 1;

		Point newPoint1 = pieceZoneOfControlManager.point.clone();
		Point newPoint2 = pieceZoneOfControlManager.point.clone();
		newPoint1.x -= 1;
		newPoint2.x += 1;

		pieceZoneOfControlManager.validateAndAdd(newPointFront, pieceZoneOfControlManager.piece);
		pieceZoneOfControlManager.validateAndAdd(newPoint1, pieceZoneOfControlManager.piece);
		pieceZoneOfControlManager.validateAndAdd(newPoint2, pieceZoneOfControlManager.piece);
		pieceZoneOfControlManager.validateAndAdd(newPointBack, pieceZoneOfControlManager.piece);

		return false;
	}

	private void validateAndAdd(int diffx, int diffy, PieceZoneOfControlManager pieceZoneOfControlManager,
			HonShogiField field) {

		boolean isBreak = false;
		for (byte i = 1; i <= field.y; i++) {
			Point newPoint = pieceZoneOfControlManager.point.clone();

			newPoint.x -= i * diffx;
			newPoint.y -= i * diffy;

			// 軌道を追加する
			pieceZoneOfControlManager.addLine(newPoint);

			if (!isBreak) {
				if (!pieceZoneOfControlManager.validateAndAdd(newPoint, pieceZoneOfControlManager.piece)) {
					isBreak = true;
					continue;
				}
			}
		}
	}

	public Uma clone() {
		return new Uma();
	}

	@Override
	public int hashCode() {
		return ID;
	}
}
