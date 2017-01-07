package jp.gr.norinori.shogi.honshogi;

import org.junit.Test;

import jp.gr.norinori.shogi.Action;
import jp.gr.norinori.shogi.GameInformation;
import jp.gr.norinori.shogi.GameProtocol;
import jp.gr.norinori.shogi.Logger;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.Timer;

public class RandomActionEngineTest {

	@Test
	public void testAction1() {
		Logger.useDebug = true;

		GameInformation gameInformation = new GameInformation();
		GameProtocol gameProtocol = new HonShogi();
		gameInformation.setGameProtocol(gameProtocol);

		HonShogiScene scene = new HonShogiScene(gameInformation);

		HonShogiSceneHash.loadScene("DlABH5XWkZzY3h===b7Cp=NsOV=pVDR=V8Yq=BYH08=HYd", scene);
		Action action = new Action();
		action.from = new Point(3, 1);
		action.fromPiece = scene.getPieceLocations().get(action.from);
		action.to = new Point(4, 1);
		action.toPiece = action.fromPiece;
		action.status = new HonShogiActionStatus();

		gameProtocol.nextPhage(scene, action);

		Logger.debug(HonShogiDisplayUtil.displayByCharacter(scene));
		gameProtocol.judge(scene, action);
	}

	@Test
	public void testAction2() {
		Logger.useDebug = true;

		GameInformation gameInformation = new GameInformation();
		GameProtocol gameProtocol = new HonShogi();
		gameInformation.setGameProtocol(gameProtocol);

		HonShogiScene scene = new HonShogiScene(gameInformation);

		HonShogiSceneHash.loadScene("CHTS+z5jVvYoDEiTGcGkl=KiyQ=HW+C=pZDK=BLRB2RBM9", scene);
		gameProtocol.analyzeScene(scene);

		Logger.debug(HonShogiDisplayUtil.displayByCharacter(scene));

		HonShogiPlayer player = scene.getInitiativePlayer();
		player.getActionEngine().action(scene);

	}

	@Test
	public void testAction3() {
		Logger.useDebug = true;

		GameInformation gameInformation = new GameInformation();
		GameProtocol gameProtocol = new HonShogi();
		gameInformation.setGameProtocol(gameProtocol);

		HonShogiScene scene = new HonShogiScene(gameInformation);

		HonShogiSceneHash.loadScene("G1aP3sidS9iHWp===BBFOdCHpQNE8GC8CHZIUJwnEbtHCB", scene);
		gameProtocol.analyzeScene(scene);

		Logger.debug(HonShogiDisplayUtil.displayByCharacter(scene));

		HonShogiPlayer player = scene.getInitiativePlayer();
		player.getActionEngine().action(scene);

	}

	@Test
	public void testAction4() {
		Logger.useDebug = true;

		GameInformation gameInformation = new GameInformation();
		GameProtocol gameProtocol = new HonShogi();
		gameInformation.setGameProtocol(gameProtocol);

		HonShogiScene scene = new HonShogiScene(gameInformation);

		HonShogiSceneHash.loadScene("HW7f0XOGIdWm0CtNXC9fkGCpojuimDwqBhCdXBu/0k=Beb", scene);
		gameProtocol.analyzeScene(scene);

		Logger.debug(HonShogiDisplayUtil.displayByCharacter(scene));

		HonShogiPlayer player = scene.getInitiativePlayer();
		player.getActionEngine().action(scene);

		HonShogiPlayer otherPlayer = scene.getOtherPlayer();
		System.out.println(scene.getPieceZoneOfControl(otherPlayer));
	}

	@Test
	public void testAction5() {
		Logger.useDebug = true;

		GameInformation gameInformation = new GameInformation();
		GameProtocol gameProtocol = new HonShogi();
		gameInformation.setGameProtocol(gameProtocol);

		HonShogiScene scene = new HonShogiScene(gameInformation);

		HonShogiSceneHash.loadScene("ICYzVs6tUqDBJdkpTDcTsXjW5Aji6s31BpasABwmEpvBKk", scene);
		gameProtocol.analyzeScene(scene);

		Logger.debug(HonShogiDisplayUtil.displayByCharacter(scene));

		HonShogiPlayer player = scene.getInitiativePlayer();
		player.getActionEngine().action(scene);

		HonShogiPlayer otherPlayer = scene.getOtherPlayer();
		System.out.println(scene.getPieceZoneOfControl(otherPlayer));

		Logger.logTimer(Timer.getTreeTimeids());
	}

	// 王手ラインが無視されるバグO
	@Test
	public void testAction6() {
		Logger.useDebug = true;

		GameInformation gameInformation = new GameInformation();
		GameProtocol gameProtocol = new HonShogi();
		gameInformation.setGameProtocol(gameProtocol);

		HonShogiScene scene = new HonShogiScene(gameInformation);

		HonShogiSceneHash.loadScene("E80UAB61+Zz9ws===b7Ba=G6pE=dOzT=BqOtwBlzD/oG2b", scene);
		gameProtocol.analyzeScene(scene);

		Logger.debug(HonShogiDisplayUtil.displayByCharacter(scene));

		HonShogiPlayer player = scene.getInitiativePlayer();
		player.getActionEngine().action(scene);

		HonShogiPlayer otherPlayer = scene.getOtherPlayer();
		System.out.println(scene.getPieceZoneOfControl(otherPlayer));
	}

}
