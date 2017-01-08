package jp.gr.norinori.shogi.honshogi.piece;

import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.honshogi.HonShogiField;
import jp.gr.norinori.shogi.honshogi.HonShogiPieceType;
import jp.gr.norinori.shogi.honshogi.PieceZoneOfControlManager;

public class Ryu extends HonShogiPieceType {

	@Override
	public boolean addPieceLocations(PieceZoneOfControlManager pieceZoneOfControlManager) {
		HonShogiField field = (HonShogiField) pieceZoneOfControlManager.honShogeiScene.getField();

		validateAndAddX(1, pieceZoneOfControlManager, field);
		validateAndAddX(-1, pieceZoneOfControlManager, field);
		validateAndAddY(1, pieceZoneOfControlManager, field);
		validateAndAddY(-1, pieceZoneOfControlManager, field);

		Point newPointFront1 = pieceZoneOfControlManager.point.clone();
		Point newPointFront2 = pieceZoneOfControlManager.point.clone();
		Point newPointBack1 = pieceZoneOfControlManager.point.clone();
		Point newPointBack2 = pieceZoneOfControlManager.point.clone();

		newPointFront1.y -= 1;
		newPointFront2.y -= 1;
		newPointBack1.y += 1;
		newPointBack2.y += 1;

		newPointFront1.x -= 1;
		newPointFront2.x += 1;
		newPointBack1.x -= 1;
		newPointBack2.x += 1;

		pieceZoneOfControlManager.validateAndAdd(newPointFront1, pieceZoneOfControlManager.piece);
		pieceZoneOfControlManager.validateAndAdd(newPointFront2, pieceZoneOfControlManager.piece);
		pieceZoneOfControlManager.validateAndAdd(newPointBack1, pieceZoneOfControlManager.piece);
		pieceZoneOfControlManager.validateAndAdd(newPointBack2, pieceZoneOfControlManager.piece);

		return false;
	}

	/**
	 *
	 * @param diff
	 * @param pieceZoneOfControlManager
	 * @param field
	 */
	private void validateAndAddX(int diff, PieceZoneOfControlManager pieceZoneOfControlManager, HonShogiField field) {

		boolean isBreak = false;
		for (byte i = 1; i <= field.x; i++) {
			Point newPoint = pieceZoneOfControlManager.point.clone();

			newPoint.x -= i * diff;

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

	/**
	 *
	 * @param diff
	 * @param pieceZoneOfControlManager
	 * @param field
	 */
	private void validateAndAddY(int diff, PieceZoneOfControlManager pieceZoneOfControlManager, HonShogiField field) {

		boolean isBreak = false;
		for (byte i = 1; i <= field.y; i++) {
			Point newPoint = pieceZoneOfControlManager.point.clone();

			newPoint.y -= i * diff;

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

	public Ryu clone() {
		return new Ryu();
	}

	@Override
	public int hashCode() {
		return 14;
	}
}
