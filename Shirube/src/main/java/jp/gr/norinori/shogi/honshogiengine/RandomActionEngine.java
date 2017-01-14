package jp.gr.norinori.shogi.honshogiengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.gr.norinori.shogi.Action;
import jp.gr.norinori.shogi.ActionEngine;
import jp.gr.norinori.shogi.ActionStatus;
import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.Logger;
import jp.gr.norinori.shogi.PieceMove;
import jp.gr.norinori.shogi.Player;
import jp.gr.norinori.shogi.Scene;
import jp.gr.norinori.shogi.Timer;
import jp.gr.norinori.shogi.honshogi.HonShogi;
import jp.gr.norinori.shogi.honshogi.HonShogiActionStatus;
import jp.gr.norinori.shogi.honshogi.HonShogiPieceType;
import jp.gr.norinori.shogi.honshogi.HonShogiScene;
import jp.gr.norinori.shogi.honshogi.LoggerLabel;
import jp.gr.norinori.shogi.honshogi.piece.Fu;
import jp.gr.norinori.shogi.honshogi.piece.Hisha;
import jp.gr.norinori.shogi.honshogi.piece.Kaku;

public class RandomActionEngine implements ActionEngine {

	// メソッド=================================================================

	@Override
	public Action action(Scene scene) {
		LoggerLabel.pieceZoneOfControl = Timer.start("pieceZoneOfControl", "action", LoggerLabel.pieceZoneOfControl);
		Player player = scene.getInitiativePlayer();

		List<PieceMove> movableList;
		List<PieceMove> outePieceMoveList = ((HonShogiScene) scene).getOuteEscapeList(player);
		if (outePieceMoveList != null && !outePieceMoveList.isEmpty()) {
			movableList = outePieceMoveList;
		} else {
			movableList = scene.getPieceZoneOfControl(player).getList();
		}

		Action action = new Action();
		if (movableList.isEmpty()) {
			ActionStatus status = new HonShogiActionStatus();
			status.isEnd = true;
			status.message = "移動可能駒なし：投了";
			return action;
		}
		Timer.stop(LoggerLabel.pieceZoneOfControl);

		LoggerLabel.checkTumi = Timer.start("checkTumi", "action", LoggerLabel.checkTumi);
		List<PieceMove> removePieceMoveList = new ArrayList<>();
		HonShogi honshogi = (HonShogi) scene.getGameInformation().getGameProtocol();
		for (PieceMove pieceMove : movableList) {

			// 打ち駒でない場合、歩／飛／角は成優先
			if (pieceMove.from != null) {
				HonShogiPieceType piceType = (HonShogiPieceType) pieceMove.toPiece.type;
				if (player.getDirection() == Direction.UP) {
					if (piceType.hashCode() == Fu.ID && pieceMove.to.y <= 3) {
						removePieceMoveList.add(pieceMove);
						continue;
					}
					if (piceType.hashCode() == Kaku.ID && (pieceMove.to.y <= 3 || pieceMove.from.y <= 3)) {
						removePieceMoveList.add(pieceMove);
						continue;
					}
					if (piceType.hashCode() == Hisha.ID && (pieceMove.to.y <= 3 || pieceMove.from.y <= 3)) {
						removePieceMoveList.add(pieceMove);
						continue;
					}
				} else {
					if (piceType.hashCode() == Fu.ID && pieceMove.to.y >= 6) {
						removePieceMoveList.add(pieceMove);
						continue;
					}
					if (piceType.hashCode() == Kaku.ID && (pieceMove.to.y >= 6 || pieceMove.from.y >= 6)) {
						removePieceMoveList.add(pieceMove);
						continue;
					}
					if (piceType.hashCode() == Hisha.ID && (pieceMove.to.y >= 6 || pieceMove.from.y >= 6)) {
						removePieceMoveList.add(pieceMove);
						continue;
					}
				}
			}

			Action checkAction = new Action();
			checkAction.setFromPieceMove(pieceMove);
			LoggerLabel.sceneClone = Timer.start("scene clone", "checkTumi", LoggerLabel.sceneClone);
			HonShogiScene checkScene = (HonShogiScene) scene.clone();
			Timer.stop(LoggerLabel.sceneClone);

			LoggerLabel.analyzeSceneCheckTumi = Timer.start("analyzeScene checkTumi", "checkTumi");
			HonShogiActionStatus checkStatus = (HonShogiActionStatus) honshogi.nextPhage(checkScene, checkAction);
			Timer.stop(LoggerLabel.analyzeSceneCheckTumi);

			if (checkStatus.isTumi) {
				// 打ち歩詰め禁止
				if ((checkAction.from == null) && checkAction.toPiece.type instanceof Fu) {
					continue;
				}

				Logger.debug(movableList.toString());
				Logger.debug("選択:詰みあり " + pieceMove);
				return checkAction;
			}
		}

		// 移動不可分は削除
		for (PieceMove pieceMove : removePieceMoveList) {
			movableList.remove(pieceMove);
		}
		Timer.stop(LoggerLabel.checkTumi);

		Random random = new Random();
		int index = random.nextInt(movableList.size());
		PieceMove pieceMove = movableList.get(index);

		Logger.debug(movableList.toString());
		Logger.debug("選択:" + index + " " + pieceMove);
		action.setFromPieceMove(pieceMove);

		return action;
	}

}
