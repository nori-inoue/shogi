package jp.gr.norinori.shogi.honshogi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.gr.norinori.core.collection.NumberingHashMap;
import jp.gr.norinori.core.collection.NumberingMap;
import jp.gr.norinori.core.element.Pair;
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
import jp.gr.norinori.shogi.honshogiengine.RateSearchActionEngine;

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
	public ActionResult judge(Scene scene, Action lastAction, List<Pair<Action, String>> history) {
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
			} else {
				result.status.message = "詰み";
				result.status.winners.add(otherPlayer);
			}
		}

		else {
			Map<String, Integer> hisitoryMap = new HashMap<>(250);
			for (Pair<Action, String> pair : history) {
				String hash = pair.getSecond();
				int count = 0;
				if (hisitoryMap.containsKey(hash)) {
					count = hisitoryMap.get(hash);
					if (count >= 2) {
						result.status.isEnd = true;
						result.status.message = "千日手";
						break;
					}
				}
				hisitoryMap.put(hash, count + 1);
			}
		}

		return result;
	}

	@Override
	public ActionStatus analyzeScene(Scene scene) {
		LoggerLabel.analyzePieceZoneOfControl = Timer.start("analyzePieceZoneOfControl", "analyzeScene",
				LoggerLabel.analyzePieceZoneOfControl);
		this.analyzer.analyzePieceZoneOfControl(scene);
		Timer.stop(LoggerLabel.analyzePieceZoneOfControl);

		LoggerLabel.analyzeOute = Timer.start("analyzeOute", "analyzeScene", LoggerLabel.analyzeOute);
		HonShogiActionStatus status = analyzeOute((HonShogiScene) scene);
		Timer.stop(LoggerLabel.analyzeOute);

		return status;
	}

	private boolean canEscape(HonShogiScene scene, PieceMove outPieceMove) {
		HonShogiPlayer player = scene.getInitiativePlayer();
		HonShogiPlayer otherPlayer = scene.getOtherPlayer();

		HonShogiPieceZoneOfControl otherPieceZoneOfControl = (HonShogiPieceZoneOfControl) scene
				.getPieceZoneOfControl(otherPlayer);

		boolean existsOtherPiece = false;
		for (Entry<Piece, EffectiveRange> en : otherPieceZoneOfControl.getEffectiveRanges().entrySet()) {
			EffectiveRange effectiveRange = en.getValue();
			for (Point effectivePoint : effectiveRange.list) {
				if (effectivePoint.equals(outPieceMove.to)) {
					existsOtherPiece = true;
					Logger.debug("脱出不可：" + outPieceMove.to + " <= " + en.getKey());
					break;
				}
			}
		}

		if (!existsOtherPiece) {
			Logger.debug("脱出ポイント：" + outPieceMove.to);
			scene.addOuteEscape(player, outPieceMove);
			return true;
		}
		return false;
	}

	private HonShogiActionStatus analyzeOute(HonShogiScene honShogeiScene) {
		HonShogiActionStatus status = new HonShogiActionStatus();

		HonShogiPlayer player = honShogeiScene.getInitiativePlayer();
		HonShogiPlayer otherPlayer = honShogeiScene.getOtherPlayer();

		// 王手コントロールはクリア
		honShogeiScene.clearOuteEscape();

		// 王手判定
		Timer.start("oute", "analyzeOute");
		NumberingMap<Piece, Point> outePieces = new NumberingHashMap<>();
		for (PieceMove pieceMove : honShogeiScene.getPieceZoneOfControl(otherPlayer).getList()) {
			Piece piece = player.getPiece(pieceMove.to);
			if (piece != null && piece.type instanceof Ou) {
				status.message = "王手";
				status.isOute = true;
				outePieces.put(pieceMove.fromPiece, pieceMove.from);
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
			for (PieceMove pieceMove : honShogiPieceZoneOfControl.getOuList()) {
				if (canEscape(honShogeiScene, pieceMove)) {
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
				List<PieceMove> outeBlockLine = otherHonShogiPieceZoneOfControl.getOuteBlockLine();
				Timer.stop("getOuteline");

				// 移動可能範囲ありなら詰みでない
				Timer.start("outePoint", "outePieces");
				oute_range: for (PieceMove pieceMove : honShogeiScene.getPieceZoneOfControl(player).getList()) {
					// 自駒が王手の駒を取得可能な場合かつその駒が他の王手軌道にない場合、その駒で取得可能
					if (pieceMove.to.equals(outePoint) && !(pieceMove.fromPiece.type instanceof Ou)) {
						for (PieceMove outePieceMove : outeBlockLine) {
							if (!outePieceMove.from.equals(outePoint) && outePieceMove.to.equals(pieceMove.from)) {
								continue oute_range;
							}
						}
						isTumi = false;
						Logger.debug("取れる駒：" + pieceMove.from + ":" + pieceMove.fromPiece);
						honShogeiScene.addOuteEscape(player, pieceMove);

					} else if (!(pieceMove.fromPiece.type instanceof Ou)) {

						// 自駒が王手軌道に移動かつその駒が他の王手軌道にない場合、その駒でブロック可能
						boolean isExists = false;
						for (PieceMove outePieceMove : outeBlockLine) {
							// 他の王手軌道にあればブロック不可
							if (!outePieceMove.from.equals(outePoint) && outePieceMove.to.equals(pieceMove.from)) {
								continue oute_range;
							}
							// 該当の王手軌道に移動可
							if (outePieceMove.from.equals(outePoint) && outePieceMove.to.equals(pieceMove.to)) {
								isExists = true;
							}
						}
						if (!isExists) {
							continue oute_range;
						}

						isTumi = false;
						Logger.debug("止める駒：" + pieceMove.from + ":" + pieceMove.toPiece);
						honShogeiScene.addOuteEscape(player, pieceMove);
					}
				}
				Timer.stop("outePoint");
			}
			Timer.stop("outePieces");
		}

		status.isTumi = isTumi;

		return status;
	}

	@Override
	public Scene initializeScene(GameInformation gameInformation) {
		HonShogiScene scene = new HonShogiScene(gameInformation);

		HonShogiPlayer sente = new HonShogiPlayer(HonShogiPlayer.SENTE);
		sente.setName("先手");
		sente.setDirection(Direction.UP);
//		sente.setActionEngine(new RandomActionEngine());
		sente.setActionEngine(new RateSearchActionEngine());

		scene.addPlayer(sente);

		HonShogiPlayer gote = new HonShogiPlayer(HonShogiPlayer.GOTE);
		gote.setName("後手");
		gote.setDirection(Direction.DOWN);
//		gote.setActionEngine(new RandomActionEngine());
		gote.setActionEngine(new RateSearchActionEngine());

		scene.addPlayer(gote);

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
		String directionDisplay1 = "／";
		String directionDisplay2 = "＼";

		if (player.getDirection() == Direction.DOWN) {
			directionDisplay1 = "＼";
			directionDisplay2 = "／";
		}

		Piece piece = null;

		if (pieceType instanceof Tokin)
			piece = new Piece(new Fu(), directionDisplay1 + "歩" + directionDisplay2);
		if (pieceType instanceof Narikyo)
			piece = new Piece(new Kyo(), directionDisplay1 + "香" + directionDisplay2);
		if (pieceType instanceof Narikei)
			piece = new Piece(new Keima(), directionDisplay1 + "桂" + directionDisplay2);
		if (pieceType instanceof Narigin)
			piece = new Piece(new Gin(), directionDisplay1 + "銀" + directionDisplay2);
		if (pieceType instanceof Uma)
			piece = new Piece(new Kaku(), directionDisplay1 + "角" + directionDisplay2);
		if (pieceType instanceof Ryu)
			piece = new Piece(new Hisha(), directionDisplay1 + "飛" + directionDisplay2);

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
		String directionDisplay1 = "／";
		String directionDisplay2 = "＼";

		if (player.getDirection() == Direction.DOWN) {
			directionDisplay1 = "＼";
			directionDisplay2 = "／";
		}

		Piece piece = null;
		if (pieceType instanceof Fu)
			piece = new Piece(pieceType, directionDisplay1 + "歩" + directionDisplay2);
		if (pieceType instanceof Kyo)
			piece = new Piece(pieceType, directionDisplay1 + "香" + directionDisplay2);
		if (pieceType instanceof Keima)
			piece = new Piece(pieceType, directionDisplay1 + "桂" + directionDisplay2);
		if (pieceType instanceof Gin)
			piece = new Piece(pieceType, directionDisplay1 + "銀" + directionDisplay2);
		if (pieceType instanceof Kin)
			piece = new Piece(pieceType, directionDisplay1 + "金" + directionDisplay2);
		if (pieceType instanceof Kaku)
			piece = new Piece(pieceType, directionDisplay1 + "角" + directionDisplay2);
		if (pieceType instanceof Hisha)
			piece = new Piece(pieceType, directionDisplay1 + "飛" + directionDisplay2);
		if (pieceType instanceof Ou)
			piece = new Piece(pieceType, directionDisplay1 + "王" + directionDisplay2);
		if (pieceType instanceof Tokin)
			piece = new Piece(pieceType, directionDisplay1 + "と" + directionDisplay2);
		if (pieceType instanceof Narikyo)
			piece = new Piece(pieceType, directionDisplay1 + "杏" + directionDisplay2);
		if (pieceType instanceof Narikei)
			piece = new Piece(pieceType, directionDisplay1 + "圭" + directionDisplay2);
		if (pieceType instanceof Narigin)
			piece = new Piece(pieceType, directionDisplay1 + "全" + directionDisplay2);
		if (pieceType instanceof Uma)
			piece = new Piece(pieceType, directionDisplay1 + "馬" + directionDisplay2);
		if (pieceType instanceof Ryu)
			piece = new Piece(pieceType, directionDisplay1 + "竜" + directionDisplay2);

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
