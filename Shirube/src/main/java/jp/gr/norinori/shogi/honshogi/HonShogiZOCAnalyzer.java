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

		Timer.start("analyzePieceZoneOfControls");
		// 自駒移動可能範囲を求める
		Timer.start("analyzePieceZoneOfControls initiative", "analyzePieceZoneOfControls");
		PieceZoneOfControlManager pieceZoneOfControlManager = createPieceZoneOfControlManager(honShogeiScene, player,
				otherPlayer);
		Timer.stop("analyzePieceZoneOfControls initiative");

		// 相手駒移動可能範囲を求める
		Timer.start("analyzePieceZoneOfControls other", "analyzePieceZoneOfControls");
		PieceZoneOfControlManager otherPieceZoneOfControlManager = createPieceZoneOfControlManager(honShogeiScene,
				otherPlayer, player);
		Timer.stop("analyzePieceZoneOfControls other");

		Timer.stop("analyzePieceZoneOfControls");

		Timer.start("analyzePieceZoneOfControlManager");
		// 自駒移動可能範囲を駒の状況による移動可能範囲を解析
		analyzePieceZoneOfControls(pieceZoneOfControlManager, otherPieceZoneOfControlManager, player, otherPlayer);

		// 相手駒移動可能範囲を駒の状況による移動可能範囲を解析
		analyzePieceZoneOfControls(otherPieceZoneOfControlManager, pieceZoneOfControlManager, otherPlayer, player);
		Timer.stop("analyzePieceZoneOfControlManager");

		scene.setPieceZoneOfControl(player, pieceZoneOfControlManager.pieceZoneOfControl);
		scene.setPieceZoneOfControl(otherPlayer, otherPieceZoneOfControlManager.pieceZoneOfControl);
	}

	// 移動可能範囲を解析する
	private PieceZoneOfControlManager createPieceZoneOfControlManager(HonShogiScene scene, HonShogiPlayer player,
			HonShogiPlayer otherPlayer) {
		Timer.start("pieceZoneOfControlManager", "analyzePieceZoneOfControls");
		PieceZoneOfControlManager pieceZoneOfControlManager = new PieceZoneOfControlManager(scene, player, otherPlayer);
		Timer.stop("pieceZoneOfControlManager");

		for (Pair<Point, Piece> pair : pieceZoneOfControlManager.pieceLocations) {
			Point point = pair.getFirst();
			Piece piece = pair.getSecond();

			Timer.start("setCurrent", "analyzePieceZoneOfControls");
			pieceZoneOfControlManager.setCurrent(point, piece);
			Timer.stop("setCurrent");

			Timer.start("addPieceLocations", "analyzePieceZoneOfControls");
			((HonShogiPieceType) piece.type).addPieceLocations(pieceZoneOfControlManager);
			Timer.stop("addPieceLocations");
		}

		return pieceZoneOfControlManager;
	}

	// 駒の状況による移動可能範囲を解析する
	private void analyzePieceZoneOfControls(PieceZoneOfControlManager pieceZoneOfControlManager,
			PieceZoneOfControlManager otherPieceZoneOfControlManager, HonShogiPlayer player,
			HonShogiPlayer otherPlayer) {

		HonShogiPieceZoneOfControl pieceZoneOfControl = pieceZoneOfControlManager.pieceZoneOfControl;
		HonShogiPieceZoneOfControl otherPieceZoneOfControl = otherPieceZoneOfControlManager.pieceZoneOfControl;

		Point ouPoint = pieceZoneOfControlManager.ouPoint;

		// 持駒の移動可能範囲を求める
		// 移動不可の位置への打ち禁止
		Timer.start("check movable1", "analyzePieceZoneOfControlManager");
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

		// 打ち込み可能
		Map<Point, Piece> existsMap = new HashMap<>();

		// 二歩打ち禁止
		Map<Byte, Piece> fuMap = new HashMap<>();
		for (Pair<Point, Piece> pair : pieceZoneOfControlManager.pieceLocations) {
			if (pair.getSecond().type instanceof Fu) {
				fuMap.put(pair.getFirst().x, pair.getSecond());
			}
			existsMap.put(pair.getFirst(), pair.getSecond());
		}

		for (Pair<Point, Piece> pair : otherPieceZoneOfControlManager.pieceLocations) {
			existsMap.put(pair.getFirst(), pair.getSecond());
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
				for (byte y = startIndex; y <= endIndex; y++) {
					Point newPoint = new Point(x, y);
					if (!existsMap.containsKey(newPoint) && (!(pieceType instanceof Fu) || !fuMap.containsKey(x))) {
						PieceMove pieceMove = new PieceMove(null, newPoint, piece, piece);
						pieceZoneOfControl.add(pieceMove);
					}
				}
			}
		}
		Timer.stop("check movable1");

		Timer.start("check movable2", "analyzePieceZoneOfControlManager");
		List<PieceMove> removePieceZoneOfControl = new ArrayList<>();

		// 王は相手の移動範囲に移動不可
		// 王手軌道を除外
		Map<Point, Integer> effectiveRangePoint = otherPieceZoneOfControl.getEffectiveRangePoint();

		// 王手軌道を除外
		for (PieceMove pieceMove : otherPieceZoneOfControl.getOuteLine()) {
			effectiveRangePoint.put(pieceMove.to, 1);
		}
		for (PieceMove pieceMove : pieceZoneOfControl.getOuList()) {
			// 影響範囲を除外
			if (effectiveRangePoint.containsKey(pieceMove.to)) {
				removePieceZoneOfControl.add(pieceMove);
			}
		}
		Timer.stop("check movable2");

		// 自分への王手ラインを取得する(自分の駒を無視した王手ラインを取得）
		Timer.start("check movable3", "analyzePieceZoneOfControlManager");
		List<List<Point>> outeLines = HonShogi.getOuteline(otherPieceZoneOfControl, otherPlayer, null, ouPoint);
		for (List<Point> outeLine : outeLines) {
			Point blockPoint = null;
			List<PieceMove> blockPieceZoneOfControlList = new ArrayList<>();
			oute_range: for (Point outeLinePoint : outeLine) {
				for (PieceMove pieceMove : pieceZoneOfControl.getList()) {
					// 王手ラインに駒がある場合
					if (outeLinePoint.equals(pieceMove.from)) {
						// 既にブロックした駒がある場合は王手ラインに２駒あるので自由に移動可能
						if (blockPoint != null && !blockPoint.equals(pieceMove.from)) {
							blockPoint = null;
							break oute_range;
						}

						// 移動先が王手ライン上以外は移動不可
						boolean existsMovable = false;
						for (Point p1 : outeLine) {
							if (p1.equals(pieceMove.to)) {
								existsMovable = true;
								break;
							}
						}
						if (!existsMovable) {
							blockPoint = pieceMove.from;
							blockPieceZoneOfControlList.add(pieceMove);
						}
					}
				}
			}
			if (blockPoint != null) {
				for (PieceMove pieceMove : blockPieceZoneOfControlList) {
					pieceZoneOfControl.remove(pieceMove);
				}
			}
		}
		Timer.stop("check movable3");

		// 移動不可範囲の抽出
		Timer.start("check movable4", "analyzePieceZoneOfControlManager");
		for (PieceMove pieceMove : pieceZoneOfControl.getList()) {
			PieceType pieceType = pieceMove.toPiece.type;

			if (pieceType instanceof Fu) {
				if (player.getDirection() == Direction.UP) {
					if (pieceMove.to.y == 0) {
						removePieceZoneOfControl.add(pieceMove);
					}
				} else {
					if (pieceMove.to.y == HonShogiField.MAX_Y) {
						removePieceZoneOfControl.add(pieceMove);
					}
				}
			} else if (pieceType instanceof Kyo) {
				if (player.getDirection() == Direction.UP) {
					if (pieceMove.to.y == 0) {
						removePieceZoneOfControl.add(pieceMove);
					}
				} else {
					if (pieceMove.to.y == HonShogiField.MAX_Y) {
						removePieceZoneOfControl.add(pieceMove);
					}
				}
			} else if (pieceType instanceof Keima) {
				if (player.getDirection() == Direction.UP) {
					if (pieceMove.to.y <= 1) {
						removePieceZoneOfControl.add(pieceMove);
					}
				} else {
					if (pieceMove.to.y >= HonShogiField.MAX_Y - 1) {
						removePieceZoneOfControl.add(pieceMove);
					}
				}
			}
		}
		Timer.stop("check movable4");

		// 移動不可分は削除
		for (PieceMove pieceMove : removePieceZoneOfControl) {
			pieceZoneOfControl.remove(pieceMove);
		}
	}
}
