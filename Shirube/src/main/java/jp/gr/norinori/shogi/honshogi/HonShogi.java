package jp.gr.norinori.shogi.honshogi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import jp.gr.norinori.core.collection.NumberingHashMap;
import jp.gr.norinori.core.collection.NumberingMap;
import jp.gr.norinori.shogi.Action;
import jp.gr.norinori.shogi.ActionResult;
import jp.gr.norinori.shogi.ActionStatus;
import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.EffectiveRange;
import jp.gr.norinori.shogi.GameInformation;
import jp.gr.norinori.shogi.GameProtocol;
import jp.gr.norinori.shogi.Logger;
import jp.gr.norinori.shogi.Piece;
import jp.gr.norinori.shogi.PieceMove;
import jp.gr.norinori.shogi.Player;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.Scene;
import jp.gr.norinori.shogi.Timer;
import jp.gr.norinori.shogi.honshogi.piece.Fu;
import jp.gr.norinori.shogi.honshogi.piece.Gin;
import jp.gr.norinori.shogi.honshogi.piece.Hisha;
import jp.gr.norinori.shogi.honshogi.piece.Kaku;
import jp.gr.norinori.shogi.honshogi.piece.Keima;
import jp.gr.norinori.shogi.honshogi.piece.Kin;
import jp.gr.norinori.shogi.honshogi.piece.Kyo;
import jp.gr.norinori.shogi.honshogi.piece.Narigin;
import jp.gr.norinori.shogi.honshogi.piece.Narikei;
import jp.gr.norinori.shogi.honshogi.piece.Narikyo;
import jp.gr.norinori.shogi.honshogi.piece.Ou;
import jp.gr.norinori.shogi.honshogi.piece.Ryu;
import jp.gr.norinori.shogi.honshogi.piece.Tokin;
import jp.gr.norinori.shogi.honshogi.piece.Uma;

/**
 * 本将棋用プロトコル
 *
 * @author nori
 */
public class HonShogi implements GameProtocol {
	private HonShogiZOCAnalyzer analyzer;

	// コンストラクタ===========================================================
	/**
	 * 本将棋用プロトコルのインスタンスを生成する
	 */
	public HonShogi() {
		this.analyzer = new HonShogiZOCAnalyzer();
	}

	@Override
	public ActionResult judge(Scene scene, Action lastAction) {
		ActionResult result = new ActionResult();
		result.status = lastAction.status;

		HonShogiScene honShogeiScene = (HonShogiScene) scene;

		boolean isTumi = ((HonShogiActionStatus) result.status).isTumi;
		if (isTumi) {
			HonShogiPlayer player = honShogeiScene.getInitiativePlayer();
			HonShogiPlayer otherPlayer = honShogeiScene.getOtherPlayer();

			result.status.isEnd = true;
			result.status.winners = new ArrayList<Player>();
			if (lastAction != null && (lastAction.from == null) && lastAction.toPiece.type instanceof Fu) {
				result.status.message = "打ち歩詰め";
				result.status.winners.add(player);
				result.status.winners.add(otherPlayer);
			} else {
				result.status.message = "詰み";
				result.status.winners.add(otherPlayer);
				result.status.winners.add(player);
			}
		}
		return result;
	}

	@Override
	public ActionStatus analyzeScene(Scene scene) {
		this.analyzer.analyzePieceZoneOfControl(scene);
		return analyzeOute((HonShogiScene) scene);
	}

	/**
	 * 王手ライン生成する
	 *
	 * @param pieceZoneOfControls ZOC情報
	 * @param player 手番プレイヤー
	 * @param otherPlayer 相手プレイヤー
	 * @param ouPoint 王位置
	 * @return 王手ライン
	 */
	public static List<List<Point>> getOuteline(HonShogiPieceZoneOfControl pieceZoneOfControls, HonShogiPlayer player,
			HonShogiPlayer otherPlayer, Point ouPoint) {

		List<List<Point>> outeLines = new ArrayList<>();
		List<Point> outeLine = new ArrayList<>();
		Point outeLinePoint = null;
		Point preFromPoint = null;
		for (PieceMove pieceZoneOfControl : pieceZoneOfControls.getOuteLine()) {
			if (preFromPoint == null || !pieceZoneOfControl.from.equals(preFromPoint)) {
				outeLine = new ArrayList<>();
				outeLine.add(pieceZoneOfControl.from);
			}
			preFromPoint = pieceZoneOfControl.from;
			outeLinePoint = pieceZoneOfControl.to;

			// 相手駒が間にあれば王手ラインなし
			if (otherPlayer != null && otherPlayer.getPieceLocations().existsPiece(outeLinePoint)) {
				outeLine = new ArrayList<>();
				continue;
			}

			// 間の自駒があれば王手ラインはなし
			if (player != null && player.getPieceLocations().existsPiece(outeLinePoint)) {
				outeLine = new ArrayList<>();
				continue;
			}

			if (outeLinePoint.equals(ouPoint)) {
				outeLines.add(outeLine);
				outeLine = new ArrayList<>();
			}
			outeLine.add(outeLinePoint);
		}
		return outeLines;
	}

	private boolean canEscape(HonShogiScene scene, PieceMove pieceMove) {
		HonShogiPlayer player = scene.getInitiativePlayer();
		HonShogiPlayer otherPlayer = scene.getOtherPlayer();

		boolean existsOtherPiece = false;
		for (Entry<Piece, EffectiveRange> en : scene.getPieceZoneOfControl(otherPlayer).getEffectiveRanges()
				.entrySet()) {
			EffectiveRange effectiveRange = en.getValue();
			for (Point effectivePoint : effectiveRange.list) {
				if (effectivePoint.equals(pieceMove.to)) {
					existsOtherPiece = true;
					Logger.debug("脱出不可：" + pieceMove.to + " <= " + en.getKey());
					break;
				}
			}
		}

		if (!existsOtherPiece) {
			Logger.debug("脱出ポイント：" + pieceMove.to);
			scene.addOuteEscapePieceZoneOfControl(player, pieceMove);
			return true;
		}
		return false;
	}

	private HonShogiActionStatus analyzeOute(HonShogiScene honShogeiScene) {
		HonShogiActionStatus status = new HonShogiActionStatus();

		HonShogiPlayer player = honShogeiScene.getInitiativePlayer();
		HonShogiPlayer otherPlayer = honShogeiScene.getOtherPlayer();

		// 王手コントロールはクリア
		honShogeiScene.clearOuteEscapePieceZoneOfControls();

		// 王手判定
		Timer.start("oute", "analyzeOute");
		NumberingMap<Piece, Point> outePieces = new NumberingHashMap<>();
		Point ouPoint = null;
		for (PieceMove pieceMove : honShogeiScene.getPieceZoneOfControl(otherPlayer).getList()) {
			Piece piece = player.getPiece(pieceMove.to);
			if (piece != null && piece.type instanceof Ou) {
				status.message = "王手";
				status.isOute = true;
				outePieces.put(pieceMove.fromPiece, pieceMove.from);
				ouPoint = pieceMove.to;
				Logger.debug("王手 " + pieceMove.fromPiece);
			}
		}
		Timer.stop("oute");

		// 王手がある場合、詰み判定
		boolean isTumi = false;
		if (!outePieces.isEmpty()) {
			Timer.start("escape", "analyzeOute");
			isTumi = true;
			// 王の移動範囲をチェック
			HonShogiPieceZoneOfControl honShogiPieceZoneOfControl = (HonShogiPieceZoneOfControl) honShogeiScene
					.getPieceZoneOfControl(player);
			for (PieceMove pieceZoneOfControl : honShogiPieceZoneOfControl.getOuList()) {
				if (canEscape(honShogeiScene, pieceZoneOfControl)) {
					isTumi = false;
				}
			}
			Timer.stop("escape");

			if (isTumi) {
				Logger.debug("脱出経路なし");
			}

			// 両王手でない場合、王手している駒の取得を考慮
			Timer.start("outePieces", "analyzeOute");
			if (outePieces.size() == 1) {
				Piece outePiece = outePieces.getOfKey(0);
				Point outePoint = outePieces.getOf(0);
				Logger.debug("王手している駒：" + outePoint + ":" + outePiece);

				Timer.start("getOuteline", "outePieces");
				HonShogiPieceZoneOfControl otherHonShogiPieceZoneOfControl = (HonShogiPieceZoneOfControl) honShogeiScene
						.getPieceZoneOfControl(otherPlayer);
				List<List<Point>> outeLines = getOuteline(otherHonShogiPieceZoneOfControl, player, otherPlayer,
						ouPoint);
				Timer.stop("getOuteline");

				// 移動可能範囲ありなら詰みでない
				Timer.start("outePoint", "outePieces");
				oute_range: for (PieceMove pieceMove : honShogeiScene.getPieceZoneOfControl(player).getList()) {
					// 自駒が王手の駒を取得可能な場合かつその駒が王手軌道にない場合、その駒で取得可能
					if (pieceMove.to.equals(outePoint) && !(pieceMove.toPiece.type instanceof Ou)) {
						for (List<Point> outeLine : outeLines) {
							for (Point p : outeLine) {
								if (p.equals(pieceMove.from)) {
									continue oute_range;
								}
							}
						}
						isTumi = false;
						Logger.debug("取れる駒：" + pieceMove.from + ":" + pieceMove.fromPiece);
						honShogeiScene.addOuteEscapePieceZoneOfControl(player, pieceMove);
					}
				}
				Timer.stop("outePoint");

				// 間に移動可能範囲ありなら詰みでない
				Timer.start("blockPoint", "outePieces");
				if (outePiece.type instanceof Hisha || outePiece.type instanceof Ryu || outePiece.type instanceof Kaku
						|| outePiece.type instanceof Uma || outePiece.type instanceof Kyo) {
					int diffX = Math.abs(outePoint.x - ouPoint.x);
					int diffY = Math.abs(outePoint.y - ouPoint.y);

					Point blockPoint = outePoint.clone();
					int dx = 0;
					int dy = 0;
					if (outePoint.x > ouPoint.x) {
						dx = -1;
					} else if (outePoint.x < ouPoint.x) {
						dx = 1;
					}
					if (outePoint.y > ouPoint.y) {
						dy = -1;
					} else if (outePoint.y < ouPoint.y) {
						dy = 1;
					}
					int diff = Math.max(diffX, diffY);

					for (int d = 0; d < diff; d++) {
						blockPoint.x += dx;
						blockPoint.y += dy;
						for (PieceMove pieceZoneOfControl : honShogeiScene.getPieceZoneOfControl(player).getList()) {
							if (pieceZoneOfControl.to.equals(blockPoint)
									&& !(pieceZoneOfControl.toPiece.type instanceof Ou)) {
								isTumi = false;
								Logger.debug("止める駒：" + blockPoint + ":" + pieceZoneOfControl.toPiece);
								honShogeiScene.addOuteEscapePieceZoneOfControl(player, pieceZoneOfControl);
								// break;
							}
						}
					}
				}
				Timer.stop("blockPoint");
			}
			Timer.stop("outePieces");
		}

		status.isTumi = isTumi;

		return status;
	}

	@Override
	public Scene initializeScene(GameInformation gameInformation) {
		HonShogiScene scene = new HonShogiScene(gameInformation);
		String settingsFile = "src/main/resources/jp/gr/norinori/shogi/honshogi/initializeSettings.txt";
		HonshogiSettings.load(scene, settingsFile);

		return scene;
	}

	/**
	 *
	 * @param player
	 * @param pieceType
	 * @return
	 */
	public static Piece createInitializePiece(Player player, HonShogiPieceType pieceType) {
		String directionDisplay = "";

		if (player.getDirection() == Direction.DOWN) {
			directionDisplay = "v";
		}

		Piece piece = null;

		if (pieceType instanceof Tokin)
			piece = new Piece(new Fu(), directionDisplay + "歩");
		if (pieceType instanceof Narikyo)
			piece = new Piece(new Kyo(), directionDisplay + "香");
		if (pieceType instanceof Narikei)
			piece = new Piece(new Keima(), directionDisplay + "桂");
		if (pieceType instanceof Narigin)
			piece = new Piece(new Gin(), directionDisplay + "銀");
		if (pieceType instanceof Uma)
			piece = new Piece(new Kaku(), directionDisplay + "角");
		if (pieceType instanceof Ryu)
			piece = new Piece(new Hisha(), directionDisplay + "飛");

		if (piece == null) {
			piece = createPiece(player, pieceType);
		}

		return piece;
	}

	/**
	 *
	 * @param player
	 * @param pieceType
	 * @return
	 */
	public static Piece createPiece(Player player, HonShogiPieceType pieceType) {
		String directionDisplay = "";

		if (player.getDirection() == Direction.DOWN) {
			directionDisplay = "v";
		}

		Piece piece = null;
		if (pieceType instanceof Fu)
			piece = new Piece(pieceType, directionDisplay + "歩");
		if (pieceType instanceof Kyo)
			piece = new Piece(pieceType, directionDisplay + "香");
		if (pieceType instanceof Keima)
			piece = new Piece(pieceType, directionDisplay + "桂");
		if (pieceType instanceof Gin)
			piece = new Piece(pieceType, directionDisplay + "銀");
		if (pieceType instanceof Kin)
			piece = new Piece(pieceType, directionDisplay + "金");
		if (pieceType instanceof Kaku)
			piece = new Piece(pieceType, directionDisplay + "角");
		if (pieceType instanceof Hisha)
			piece = new Piece(pieceType, directionDisplay + "飛");
		if (pieceType instanceof Ou)
			piece = new Piece(pieceType, directionDisplay + "王");
		if (pieceType instanceof Tokin)
			piece = new Piece(pieceType, directionDisplay + "と");
		if (pieceType instanceof Narikyo)
			piece = new Piece(pieceType, directionDisplay + "杏");
		if (pieceType instanceof Narikei)
			piece = new Piece(pieceType, directionDisplay + "圭");
		if (pieceType instanceof Narigin)
			piece = new Piece(pieceType, directionDisplay + "全");
		if (pieceType instanceof Uma)
			piece = new Piece(pieceType, directionDisplay + "馬");
		if (pieceType instanceof Ryu)
			piece = new Piece(pieceType, directionDisplay + "竜");

		if (piece == null) {
			throw new IllegalArgumentException("No Piece Type" + pieceType);
		}

		return piece;
	}

	@Override
	public ActionStatus nextPhage(Scene scene, Action action) {
		HonShogiActionStatus status = null;
		Player player = scene.getInitiativePlayer();
		HonShogiPlayer otherPlayer = ((HonShogiScene) scene).getOtherPlayer();
		if (action != null) {
			Piece getPiece = otherPlayer.getPiece(action.to);
			if (getPiece != null) {
				otherPlayer.removePiece(action.to);

				getPiece = HonShogi.createInitializePiece(player, (HonShogiPieceType) getPiece.type);
				player.addStock(getPiece.type);
			}

			if (action.from != null) {
				player.removePiece(action.from);
			} else {
				player.removeStock(action.toPiece.type);
			}
			player.addPiece(action.to, action.toPiece);

			if (getPiece != null && getPiece.type instanceof Ou) {
				status = new HonShogiActionStatus();
				status.isEnd = true;
				status.message = player.getName() + "の勝利";
				status.winners = new ArrayList<Player>();
				status.winners.add(player);
				status.winners.add(otherPlayer);

				return status;
			}
		}

		scene.setInitiative(otherPlayer);

		LoggerLabel.analyzeScene = Timer.start("analyzeScene", LoggerLabel.analyzeScene);
		status = (HonShogiActionStatus) analyzeScene(scene);
		Timer.stop(LoggerLabel.analyzeScene);

		return status;
	}

	@Override
	public void displayScene(Scene scene, Action action) {
		if (action != null) {
			HonShogiField field = (HonShogiField) scene.getField();
			Logger.debug(HonShogiDisplayUtil.displayActionByCharacter(field, action));
		}

		Logger.debug(HonShogiDisplayUtil.displayByCharacter((HonShogiScene) scene));
	}
}
