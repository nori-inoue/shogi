package jp.gr.norinori.shogi.honshogiengine;

import java.util.Random;

import jp.gr.norinori.shogi.Action;
import jp.gr.norinori.shogi.ActionEngine;
import jp.gr.norinori.shogi.ActionStatus;
import jp.gr.norinori.shogi.Logger;
import jp.gr.norinori.shogi.PieceMove;
import jp.gr.norinori.shogi.Player;
import jp.gr.norinori.shogi.Scene;
import jp.gr.norinori.shogi.Timer;
import jp.gr.norinori.shogi.honshogi.HonShogi;
import jp.gr.norinori.shogi.honshogi.HonShogiActionStatus;
import jp.gr.norinori.shogi.honshogi.HonShogiScene;
import jp.gr.norinori.shogi.honshogi.HonShogiPieceZoneOfControl;
import jp.gr.norinori.shogi.honshogi.piece.Fu;

public class RandomActionEngine implements ActionEngine {

	public Action action(Scene scene) {
		Timer.start("pieceZoneOfControls", "action");
		Player player = scene.getInitiativePlayer();

		HonShogiPieceZoneOfControl pieceZoneOfControl;
		HonShogiPieceZoneOfControl outePieceZoneOfControl = ((HonShogiScene) scene).getOuteEscapePieceZoneOfControls(player);
		if (outePieceZoneOfControl != null && !outePieceZoneOfControl.isEmpty()) {
			pieceZoneOfControl = outePieceZoneOfControl;
		} else {
			pieceZoneOfControl = (HonShogiPieceZoneOfControl)scene.getPieceZoneOfControl(player);
		}

		Action action = new Action();
		if (pieceZoneOfControl.isEmpty()) {
			ActionStatus status = new HonShogiActionStatus();
			status.isEnd = true;
			status.message = "移動可能駒なし：投了";
			return action;
		}
		Timer.stop("pieceZoneOfControls");

		Timer.start("checkTumi", "action");
		HonShogi honshogi = (HonShogi) scene.getGameInformation().getGameProtocol();
		for (PieceMove pieceMove : pieceZoneOfControl.getList()) {
			Action checkAction = new Action();
			checkAction.setFromPieceMove(pieceMove);
			Timer.start("scene clone", "checkTumi");
			HonShogiScene checkScene = (HonShogiScene) scene.clone();
			Timer.stop("scene clone");
			Timer.start("nextPhage checkScene", "checkTumi");
			HonShogiActionStatus checkStatus = (HonShogiActionStatus) honshogi.nextPhage(checkScene, checkAction);
			Timer.stop("nextPhage checkScene");

			if (checkStatus.isTumi) {
				// 打ち歩詰め禁止
				if ((checkAction.from == null) && checkAction.toPiece.type instanceof Fu) {
					continue;
				}

				Logger.debug(pieceZoneOfControl.toString());
				Logger.debug("選択:詰みあり " + pieceMove);
				return checkAction;
			}
		}
		Timer.stop("checkTumi");

		Random random = new Random();
		int index = random.nextInt(pieceZoneOfControl.getList().size());
		PieceMove pieceMove = pieceZoneOfControl.getList().get(index);

		Logger.debug(pieceZoneOfControl.toString());
		Logger.debug("選択:" + index + " " + pieceMove);
		action.setFromPieceMove(pieceMove);

		return action;
	}

}
