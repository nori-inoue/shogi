package jp.gr.norinori.shogi.honshogi.piece;

import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.Piece;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.honshogi.HonShogi;
import jp.gr.norinori.shogi.honshogi.HonShogiField;
import jp.gr.norinori.shogi.honshogi.HonShogiPieceType;
import jp.gr.norinori.shogi.honshogi.PieceZoneOfControlManager;

public class Kyo extends HonShogiPieceType {

	@Override
	public boolean addPieceLocations(PieceZoneOfControlManager pieceZoneOfControlManager) {
		HonShogiField field = (HonShogiField) pieceZoneOfControlManager.honShogeiScene.getField();
		Direction direction = pieceZoneOfControlManager.player.getDirection();

		boolean isBreak = false;
		for (byte i = 1; i <= field.y; i++) {
			Point newPoint = pieceZoneOfControlManager.point.clone();
			switch (direction) {
			case UP:
				newPoint.y -= i;
				break;

			case DOWN:
				newPoint.y += i;
				break;

			default:
				break;
			}

			// 盤の範囲内の場合は軌道を追加する
			if (HonShogiField.isRange(newPoint)) {
				pieceZoneOfControlManager.addLine(newPoint);
			}

			if (!isBreak) {
				if (!pieceZoneOfControlManager.validateAndAdd(newPoint)) {

					if (canUpgrade(direction, newPoint)) {
						Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Narikyo());
						pieceZoneOfControlManager.validateAndAdd(newPoint, newPiece);
					}
					isBreak = true;
					continue;
				}

				if (canUpgrade(direction, newPoint)) {
					Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Narikyo());
					pieceZoneOfControlManager.validateAndAdd(newPoint, newPiece);
				}
			}
		}
		return false;
	}

	public Kyo clone() {
		return new Kyo();
	}

	@Override
	public int hashCode() {
		return 2;
	}
}
