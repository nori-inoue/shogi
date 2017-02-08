package jp.gr.norinori.shogi.honshogi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.gr.norinori.core.element.Pair;
import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.Piece;
import jp.gr.norinori.shogi.PieceMove;
import jp.gr.norinori.shogi.PieceType;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.Scene;
import jp.gr.norinori.shogi.Timer;
import jp.gr.norinori.shogi.ZOCAnalyzer;
import jp.gr.norinori.shogi.honshogi.piece.Fu;
import jp.gr.norinori.shogi.honshogi.piece.Keima;
import jp.gr.norinori.shogi.honshogi.piece.Kyo;

public class HonShogiZOCAnalyzer implements ZOCAnalyzer {

	@Override
	public void analyzePieceZoneOfControl(Scene scene) {
		HonShogiScene honShogeiScene = (HonShogiScene) scene;
		HonShogiPlayer player = honShogeiScene.getInitiativePlayer();
		HonShogiPlayer otherPlayer = honShogeiScene.getOtherPlayer();

		honShogeiScene.clearExistsPiece(); // 駒の位置有無情報をクリア

		// 自駒移動可能範囲を求める
		LoggerLabel.createPieceZoneOfControlManager = Timer.start("createPieceZoneOfControlManager",
				"analyzePieceZoneOfControl", LoggerLabel.createPieceZoneOfControlManager);
		PieceZoneOfControlManager pieceZoneOfControlManager = createPieceZoneOfControlManager(honShogeiScene, player,
				otherPlayer);

		// 相手駒移動可能範囲を求める
		PieceZoneOfControlManager otherPieceZoneOfControlManager = createPieceZoneOfControlManager(honShogeiScene,
				otherPlayer, player);
		Timer.stop(LoggerLabel.createPieceZoneOfControlManager);

		LoggerLabel.analyzePieceZoneOfControlManager = Timer.start("analyzePieceZoneOfControlManager",
				"analyzePieceZoneOfControl", LoggerLabel.analyzePieceZoneOfControlManager);
		// 自駒移動可能範囲を駒の状況による移動可能範囲を解析
		analyzePieceZoneOfControls(honShogeiScene, pieceZoneOfControlManager, otherPieceZoneOfControlManager, player,
				otherPlayer);

		// 相手駒移動可能範囲を駒の状況による移動可能範囲を解析
		analyzePieceZoneOfControls(honShogeiScene, otherPieceZoneOfControlManager, pieceZoneOfControlManager,
				otherPlayer, player);
		Timer.stop(LoggerLabel.analyzePieceZoneOfControlManager);

		scene.setPieceZoneOfControl(player, pieceZoneOfControlManager.pieceZoneOfControl);
		scene.setPieceZoneOfControl(otherPlayer, otherPieceZoneOfControlManager.pieceZoneOfControl);
	}

	// 移動可能範囲を解析する
	private PieceZoneOfControlManager createPieceZoneOfControlManager(HonShogiScene scene, HonShogiPlayer player,
			HonShogiPlayer otherPlayer) {
		LoggerLabel.pieceZoneOfControlManager = Timer.start("pieceZoneOfControlManager",
				"createPieceZoneOfControlManager", LoggerLabel.pieceZoneOfControlManager);
		PieceZoneOfControlManager pieceZoneOfControlManager = new PieceZoneOfControlManager(scene, player, otherPlayer);
		Timer.stop(LoggerLabel.pieceZoneOfControlManager);

		for (Pair<Point, Piece> pair : pieceZoneOfControlManager.pieceLocations) {
			Point point = pair.getFirst();
			Piece piece = pair.getSecond();

			LoggerLabel.setCurrent = Timer.start("setCurrent", "createPieceZoneOfControlManager",
					LoggerLabel.setCurrent);
			pieceZoneOfControlManager.setCurrent(point, piece);
			Timer.stop(LoggerLabel.setCurrent);

			LoggerLabel.addPieceLocations = Timer.start("addPieceLocations", "createPieceZoneOfControlManager",
					LoggerLabel.addPieceLocations);
			((HonShogiPieceType) piece.type).addPieceLocations(pieceZoneOfControlManager);

			scene.setExistsPiece(point);
			Timer.stop(LoggerLabel.addPieceLocations);
		}

		return pieceZoneOfControlManager;
	}

	// 駒の状況による移動可能範囲を解析する
	private void analyzePieceZoneOfControls(HonShogiScene scene, PieceZoneOfControlManager pieceZoneOfControlManager,
			PieceZoneOfControlManager otherPieceZoneOfControlManager, HonShogiPlayer player,
			HonShogiPlayer otherPlayer) {

		HonShogiPieceZoneOfControl pieceZoneOfControl = pieceZoneOfControlManager.pieceZoneOfControl;
		HonShogiPieceZoneOfControl otherPieceZoneOfControl = otherPieceZoneOfControlManager.pieceZoneOfControl;

		// 持駒の移動可能範囲を求める
		// ===========================================================
		Timer.start("check movable1", "analyzePieceZoneOfControlManager");
		List<PieceMove> stockPieceMoveList = new ArrayList<>();

		byte startFuIndex = 1;
		byte endFuIndex = HonShogiField.MAX_Y;
		byte startKeimaIndex = 2;
		byte endKeimaIndex = HonShogiField.MAX_Y;
		if (player.getDirection() == Direction.DOWN) {
			startFuIndex = 0;
			endFuIndex = (byte) (HonShogiField.MAX_Y - 1);
			startKeimaIndex = 0;
			endKeimaIndex = (byte) (HonShogiField.MAX_Y - 2);
		}

		// 二歩打ち禁止
		Map<Byte, Piece> fuMap = new HashMap<>();
		for (Pair<Point, Piece> pair : pieceZoneOfControlManager.pieceLocations) {
			if (pair.getSecond().type instanceof Fu) {
				fuMap.put(pair.getFirst().x, pair.getSecond());
			}
		}

		for (PieceType pieceType : player.getStockList()) {
			byte startIndex = 0;
			byte endIndex = HonShogiField.MAX_Y;

			if (pieceType instanceof Fu) {
				startIndex = startFuIndex;
				endIndex = endFuIndex;

			} else if (pieceType instanceof Kyo) {
				startIndex = startFuIndex;
				endIndex = endFuIndex;
			} else if (pieceType instanceof Keima) {
				startIndex = startKeimaIndex;
				endIndex = endKeimaIndex;
			}

			Piece piece = HonShogi.createPiece(player, (HonShogiPieceType) pieceType);
			for (byte x = 0; x <= HonShogiField.MAX_X; x++) {
				if ((pieceType instanceof Fu && fuMap.containsKey(x))) {
					continue;
				}
				for (byte y = startIndex; y <= endIndex; y++) {
					Point newPoint = new Point(x, y);
					if (!scene.existsPiece(newPoint)) {
						PieceMove pieceMove = new PieceMove(null, newPoint, piece, piece);
						stockPieceMoveList.add(pieceMove);
					}
				}
			}
		}
		Timer.stop("check movable1");

		// 王は相手の移動範囲に移動不可
		// =================================================================
		Timer.start("check movable2", "analyzePieceZoneOfControlManager");
		List<PieceMove> removePieceMoveList = new ArrayList<>();

		// 王手軌道を除外
		Map<Point, Integer> effectiveRangePoint = otherPieceZoneOfControl.getEffectiveRangePoint();

		// 王手軌道を除外
		for (PieceMove pieceMove : otherPieceZoneOfControl.getOuteLine()) {
			effectiveRangePoint.put(pieceMove.to, 1);
		}
		for (PieceMove pieceMove : pieceZoneOfControl.getOuList()) {
			// 影響範囲を除外
			if (effectiveRangePoint.containsKey(pieceMove.to)) {
				removePieceMoveList.add(pieceMove);
			}
		}
		Timer.stop("check movable2");

		// 王手軌道上の駒の移動可能を判定(王は除外)
		// =================================================================
		Timer.start("check movable3", "analyzePieceZoneOfControlManager");
		List<PieceMove> outeBlockLine = otherPieceZoneOfControl.getOuteBlockLine();
		for (PieceMove pieceMove : pieceZoneOfControl.getList()) {
			// 移動先が王手軌道上以外は移動不可
			for (PieceMove outePieceMove : outeBlockLine) {
				if (pieceMove.from.equals(outePieceMove.to)) {

					boolean existsMovable = false;
					for (PieceMove outePieceMove2 : outeBlockLine) {
						// 同じ王手している駒の軌道のみ対象
						if (outePieceMove.from.equals(outePieceMove2.from) && pieceMove.to.equals(outePieceMove2.to)) {
							existsMovable = true;
							break;
						}
					}
					if (!existsMovable) {
						removePieceMoveList.add(pieceMove);
					}
				}
			}
		}
		Timer.stop("check movable3");

		// 移動不可範囲の抽出
		Timer.start("check movable4", "analyzePieceZoneOfControlManager");
		for (PieceMove pieceMove : pieceZoneOfControl.getList()) {
			HonShogiPieceType pieceType = (HonShogiPieceType) pieceMove.toPiece.type;

			if (pieceType.hashCode() == Fu.ID) {
				if (player.getDirection() == Direction.UP) {
					if (pieceMove.to.y == 0) {
						removePieceMoveList.add(pieceMove);
					}
				} else {
					if (pieceMove.to.y == HonShogiField.MAX_Y) {
						removePieceMoveList.add(pieceMove);
					}
				}
			} else if (pieceType.hashCode() == Kyo.ID) {
				if (player.getDirection() == Direction.UP) {
					if (pieceMove.to.y == 0) {
						removePieceMoveList.add(pieceMove);
					}
				} else {
					if (pieceMove.to.y == HonShogiField.MAX_Y) {
						removePieceMoveList.add(pieceMove);
					}
				}
			} else if (pieceType.hashCode() == Keima.ID) {
				if (player.getDirection() == Direction.UP) {
					if (pieceMove.to.y <= 1) {
						removePieceMoveList.add(pieceMove);
					}
				} else {
					if (pieceMove.to.y >= HonShogiField.MAX_Y - 1) {
						removePieceMoveList.add(pieceMove);
					}
				}
			}
		}
		Timer.stop("check movable4");

		// 移動不可分は削除
		for (PieceMove pieceMove : removePieceMoveList) {
			pieceZoneOfControl.remove(pieceMove);
		}
		for (PieceMove pieceMove : stockPieceMoveList) {
			pieceZoneOfControl.add(pieceMove);
		}

	}
}
