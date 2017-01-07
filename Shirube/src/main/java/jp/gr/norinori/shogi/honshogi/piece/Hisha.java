package jp.gr.norinori.shogi.honshogi.piece;

import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.Piece;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.honshogi.HonShogi;
import jp.gr.norinori.shogi.honshogi.HonShogiField;
import jp.gr.norinori.shogi.honshogi.HonShogiPieceType;
import jp.gr.norinori.shogi.honshogi.PieceZoneOfControlManager;

public class Hisha extends HonShogiPieceType {

	@Override
	public boolean addPieceLocations(PieceZoneOfControlManager pieceZoneOfControlManager) {
		HonShogiField field = (HonShogiField) pieceZoneOfControlManager.honShogeiScene.getField();

		validateAndAddX(1, pieceZoneOfControlManager, field);
		validateAndAddX(-1, pieceZoneOfControlManager, field);
		validateAndAddY(1, pieceZoneOfControlManager, field);
		validateAndAddY(-1, pieceZoneOfControlManager, field);

		return false;
	}

	/**
	 *
	 * @param diff
	 * @param pieceZoneOfControlManager
	 * @param field
	 */
	private void validateAndAddX(int diff, PieceZoneOfControlManager pieceZoneOfControlManager, HonShogiField field) {

		Direction direction = pieceZoneOfControlManager.player.getDirection();
		boolean isBreak = false;
		for (byte i = 1; i <= field.x; i++) {
			Point newPoint = pieceZoneOfControlManager.point.clone();

			newPoint.x -= i * diff;

			// 盤の範囲内の場合は軌道を追加する
			if (HonShogiField.isRange(newPoint)) {
				pieceZoneOfControlManager.addLine(newPoint);
			}

			if (!isBreak) {
				if (!pieceZoneOfControlManager.validateAndAdd(newPoint, pieceZoneOfControlManager.piece)) {
					isBreak = true;

					// 相手エリアへ移動指定の成
					if (canUpgrade(direction, newPoint)) {
						Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Ryu());
						pieceZoneOfControlManager.validateAndAdd(newPoint, newPiece);
						continue;
					}

					// 相手エリアから移動指定の成
					if (canUpgrade(direction, pieceZoneOfControlManager.point)) {
						Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Ryu());
						pieceZoneOfControlManager.validateAndAdd(newPoint, newPiece);
					}
					continue;
				}

				// 相手エリアへ移動指定の成
				if (canUpgrade(direction, newPoint)) {
					Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Ryu());
					pieceZoneOfControlManager.validateAndAdd(newPoint, newPiece);
					continue;
				}
				// 相手エリアから移動指定の成
				if (canUpgrade(direction, pieceZoneOfControlManager.point)) {
					Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Ryu());
					pieceZoneOfControlManager.validateAndAdd(newPoint, newPiece);
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

		Direction direction = pieceZoneOfControlManager.player.getDirection();
		boolean isBreak = false;
		for (byte i = 1; i <= field.y; i++) {
			Point newPoint = pieceZoneOfControlManager.point.clone();

			newPoint.y -= i * diff;

			// 盤の範囲内の場合は軌道を追加する
			if (HonShogiField.isRange(newPoint)) {
				pieceZoneOfControlManager.addLine(newPoint);
			}

			if (!isBreak) {
				if (!pieceZoneOfControlManager.validateAndAdd(newPoint, pieceZoneOfControlManager.piece)) {
					isBreak = true;

					// 相手エリアへ移動指定の成
					if (canUpgrade(direction, newPoint)) {
						Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Ryu());
						pieceZoneOfControlManager.validateAndAdd(newPoint, newPiece);
						continue;
					}

					// 相手エリアから移動指定の成
					if (canUpgrade(direction, pieceZoneOfControlManager.point)) {
						Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Ryu());
						pieceZoneOfControlManager.validateAndAdd(newPoint, newPiece);
					}
					continue;
				}

				// 相手エリアへ移動指定の成
				if (canUpgrade(direction, newPoint)) {
					Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Ryu());
					pieceZoneOfControlManager.validateAndAdd(newPoint, newPiece);
					continue;
				}
				// 相手エリアから移動指定の成
				if (canUpgrade(direction, pieceZoneOfControlManager.point)) {
					Piece newPiece = HonShogi.createPiece(pieceZoneOfControlManager.player, new Ryu());
					pieceZoneOfControlManager.validateAndAdd(newPoint, newPiece);
				}
			}
		}
	}

	public Hisha clone() {
		return new Hisha();
	}

	@Override
	public int hashCode() {
		return 7;
	}
}
