package jp.gr.norinori.shogi.honshogi;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import jp.gr.norinori.core.element.KeyValuePair;
import jp.gr.norinori.core.element.Pair;
import jp.gr.norinori.shogi.Action;
import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.GameInformation;
import jp.gr.norinori.shogi.Logger;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.Timer;
import jp.gr.norinori.shogi.honshogiengine.RandomActionEngine;

public class RandomActionEngineTest {

	// メンバ===================================================================
	private GameInformation gameInformation;
	private HonShogiScene scene;

	// メソッド=================================================================
	@Before
	public void setup() {
		this.gameInformation = new GameInformation();
		this.gameInformation.setGameProtocol(new HonShogi());
		this.scene = new HonShogiScene(this.gameInformation);

		HonShogiPlayer sente = new HonShogiPlayer(HonShogiPlayer.SENTE);
		sente.setName("先手");
		sente.setDirection(Direction.UP);
		sente.setActionEngine(new RandomActionEngine());

		this.scene.addPlayer(sente);

		HonShogiPlayer gote = new HonShogiPlayer(HonShogiPlayer.GOTE);
		gote.setName("後手");
		gote.setDirection(Direction.DOWN);
		gote.setActionEngine(new RandomActionEngine());

		this.scene.addPlayer(gote);
	}

	@Test
	public void testAction1() {
		Logger.useDebug = true;

		HonShogiSceneHash.loadScene("DlABH5XWkZzY3h===b7Cp=NsOV=pVDR=V8Yq=BYH08=HYd", this.scene);
		Action action = new Action();
		action.from = new Point(3, 1);
		action.fromPiece = this.scene.getPieceLocations().get(action.from);
		action.to = new Point(4, 1);
		action.toPiece = action.fromPiece;
		action.status = new HonShogiActionStatus();

		this.gameInformation.getGameProtocol().nextPhage(this.scene, action);

		Logger.debug(HonShogiDisplayUtil.displayByCharacter(this.scene));

		List<Pair<Action, String>> hisotry = new ArrayList<>();
		hisotry.add(new KeyValuePair<Action, String>(action, this.scene.getHash()));
		this.gameInformation.getGameProtocol().judge(this.scene, action, hisotry);
	}

	@Test
	public void testAction2() {
		Logger.useDebug = true;


		HonShogiSceneHash.loadScene("CHTS+z5jVvYoDEiTGcGkl=KiyQ=HW+C=pZDK=BLRB2RBM9", this.scene);
		this.gameInformation.getGameProtocol().analyzeScene(this.scene);

		Logger.debug(HonShogiDisplayUtil.displayByCharacter(this.scene));

		HonShogiPlayer player = this.scene.getInitiativePlayer();
		player.getActionEngine().action(this.scene);

	}

	@Test
	public void testAction3() {
		Logger.useDebug = true;

		HonShogiSceneHash.loadScene("G1aP3sidS9iHWp===BBFOdCHpQNE8GC8CHZIUJwnEbtHCB", this.scene);
		this.gameInformation.getGameProtocol().analyzeScene(this.scene);

		Logger.debug(HonShogiDisplayUtil.displayByCharacter(this.scene));

		HonShogiPlayer player = this.scene.getInitiativePlayer();
		player.getActionEngine().action(this.scene);

	}

	@Test
	public void testAction4() {
		Logger.useDebug = true;

		HonShogiSceneHash.loadScene("HW7f0XOGIdWm0CtNXC9fkGCpojuimDwqBhCdXBu/0k=Beb", this.scene);
		this.gameInformation.getGameProtocol().analyzeScene(this.scene);

		Logger.debug(HonShogiDisplayUtil.displayByCharacter(this.scene));

		HonShogiPlayer player = this.scene.getInitiativePlayer();
		player.getActionEngine().action(this.scene);

		HonShogiPlayer otherPlayer = this.scene.getOtherPlayer();
		System.out.println(this.scene.getPieceZoneOfControl(otherPlayer));
	}

	@Test
	public void testAction5() {
		Logger.useDebug = true;

		HonShogiSceneHash.loadScene("ICYzVs6tUqDBJdkpTDcTsXjW5Aji6s31BpasABwmEpvBKk", this.scene);
		this.gameInformation.getGameProtocol().analyzeScene(this.scene);

		Logger.debug(HonShogiDisplayUtil.displayByCharacter(this.scene));

		HonShogiPlayer player = this.scene.getInitiativePlayer();
		player.getActionEngine().action(this.scene);

		HonShogiPlayer otherPlayer = this.scene.getOtherPlayer();
		System.out.println(this.scene.getPieceZoneOfControl(otherPlayer));

		Logger.logTimer(Timer.getTreeTimeids());
	}

	// 王手ラインが無視されるバグ
	@Test
	public void testAction6() {
		Logger.useDebug = true;

		HonShogiSceneHash.loadScene("E80UAB61+Zz9ws===b7Ba=G6pE=dOzT=BqOtwBlzD/oG2b", this.scene);
		this.gameInformation.getGameProtocol().analyzeScene(this.scene);

		Logger.debug(HonShogiDisplayUtil.displayByCharacter(this.scene));

		HonShogiPlayer player = this.scene.getInitiativePlayer();
		player.getActionEngine().action(this.scene);

		HonShogiPlayer otherPlayer = this.scene.getOtherPlayer();
		System.out.println(this.scene.getPieceZoneOfControl(otherPlayer));
	}

	// 打ち駒でない場合、歩／飛／角は成優先
	@Test
	public void testAction7() {
		Logger.useDebug = true;

		HonShogiSceneHash.loadScene("CBo8WOJ9xX9Gw====T6Ox=jdD5+ZMHL=RhoQ=BK5BAKBnk", this.scene);
		this.gameInformation.getGameProtocol().analyzeScene(this.scene);

		Logger.debug(HonShogiDisplayUtil.displayByCharacter(this.scene));

		HonShogiPlayer player = this.scene.getInitiativePlayer();
		player.getActionEngine().action(this.scene);
	}

}
