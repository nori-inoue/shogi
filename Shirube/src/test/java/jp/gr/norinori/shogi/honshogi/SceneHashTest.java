package jp.gr.norinori.shogi.honshogi;

import java.math.BigInteger;

import org.junit.Test;

import jp.gr.norinori.shogi.GameInformation;
import jp.gr.norinori.shogi.GameProtocol;
import jp.gr.norinori.shogi.Logger;

public class SceneHashTest {

	@Test
	public void testGetHash() {
		String result;

		int[] data = new int[18];
		for (int i = 0; i < 18; i++) {
			data[i] = 342 + i;
		}
		result = HonShogiSceneHash.to64x(HonShogiSceneHash.createHash(data, 360), 17);
		System.out.println(result);
	}

	@Test
	public void testCreateHash() {
		BigInteger result;

		result = HonShogiSceneHash.createHash(new int[] { 2, 3 }, 4);
		System.out.println(result);

		result = HonShogiSceneHash.createHash(new int[] { 1, 3 }, 4);
		System.out.println(result);

		result = HonShogiSceneHash.createHash(new int[] { 0, 3 }, 4);
		System.out.println(result);

		result = HonShogiSceneHash.createHash(new int[] { 1, 2 }, 4);
		System.out.println(result);

		result = HonShogiSceneHash.createHash(new int[] { 0, 2 }, 4);
		System.out.println(result);

		result = HonShogiSceneHash.createHash(new int[] { 0, 1 }, 4);
		System.out.println(result);

		System.out.println("===================");

		result = HonShogiSceneHash.createHash(new int[] { 2, 3, 4 }, 5);
		System.out.println(result);

		result = HonShogiSceneHash.createHash(new int[] { 1, 3, 4 }, 5);
		System.out.println(result);

		result = HonShogiSceneHash.createHash(new int[] { 0, 3, 4 }, 5);
		System.out.println(result);

		result = HonShogiSceneHash.createHash(new int[] { 1, 2, 4 }, 5);
		System.out.println(result);

		result = HonShogiSceneHash.createHash(new int[] { 0, 2, 4 }, 5);
		System.out.println(result);

		result = HonShogiSceneHash.createHash(new int[] { 0, 1, 4 }, 5);
		System.out.println(result);

		result = HonShogiSceneHash.createHash(new int[] { 1, 2, 3 }, 5);
		System.out.println(result);

		result = HonShogiSceneHash.createHash(new int[] { 0, 2, 3 }, 5);
		System.out.println(result);

		result = HonShogiSceneHash.createHash(new int[] { 0, 1, 3 }, 5);
		System.out.println(result);

		result = HonShogiSceneHash.createHash(new int[] { 0, 1, 2 }, 5);
		System.out.println(result);

		int[] data = new int[18];
		for (int i = 0; i < 18; i++) {
			data[i] = 342 + i;
		}
		result = HonShogiSceneHash.createHash(data, 360);
		System.out.println(result);

		result = HonShogiSceneHash.createHash(new int[] { 356, 357, 358, 359 }, 360);
		System.out.println(result);

		result = HonShogiSceneHash.createHash(new int[] { 358, 359 }, 360);
		System.out.println(result);
	}

	@Test
	public void testToCombinantion() {
		int[] results;

		results = HonShogiSceneHash.toCombinantion(BigInteger.valueOf(5), 4, 2);
		for (int result : results) {
			System.out.println(result);
		}

		System.out.println("===================");
		results = HonShogiSceneHash.toCombinantion(BigInteger.valueOf(7), 5, 3);
		for (int result : results) {
			System.out.println(result);
		}

		System.out.println("===================");
		results = HonShogiSceneHash.toCombinantion(BigInteger.valueOf(3), 5, 3);
		for (int result : results) {
			System.out.println(result);
		}

		System.out.println("===================");
		results = HonShogiSceneHash.toCombinantion(BigInteger.valueOf(2), 5, 3);
		for (int result : results) {
			System.out.println(result);
		}

		System.out.println("===================");
		results = HonShogiSceneHash.toCombinantion(BigInteger.valueOf(1), 5, 3);
		for (int result : results) {
			System.out.println(result);
		}
		System.out.println("===================");
		results = HonShogiSceneHash.toCombinantion(BigInteger.valueOf(0), 5, 3);
		for (int result : results) {
			System.out.println(result);
		}

		System.out.println("===================");
		results = HonShogiSceneHash.toCombinantion(new BigInteger("1045835108081250316007013036599"), 360, 18);
		for (int result : results) {
			System.out.println(result);
		}
	}

	@Test
	public void testLoadScene() {
		Logger.useDebug = true;

		GameInformation gameInformation = new GameInformation();
		GameProtocol gameProtocol = new HonShogi();
		gameInformation.setGameProtocol(gameProtocol);

		HonShogiScene scene = new HonShogiScene(gameInformation);

		HonShogiSceneHash.loadScene("JASZrzOEFbdA=====OPYA=NsVn=NKb5=MppY=BX9BOcBFf", scene);
		System.out.println(HonShogiDisplayUtil.displayByCharacter(scene));

		gameInformation = new GameInformation();
		gameInformation.setGameProtocol(gameProtocol);

		scene = new HonShogiScene(gameInformation);

		HonShogiSceneHash.loadScene("BKuGdsAU4YXJH====UJYY=n/3j=Qw9q=QGSP=BlPBOdHcV", scene);
		System.out.println(HonShogiDisplayUtil.displayByCharacter(scene));
		System.out.println(scene.getInitiativePlayer().getName());


		scene = new HonShogiScene(gameInformation);

		// 王手に対して香が打てないバグ
		HonShogiSceneHash.loadScene("GWaU0HNXUrcmXzRMmETyb1h2RCnilIiufeqE=E5GJO=HJD", scene);
		gameProtocol.analyzeScene(scene);
		System.out.println(HonShogiDisplayUtil.displayByCharacter(scene));
		System.out.println(scene.getPieceZoneOfControl(scene.getInitiativePlayer()));

		scene = new HonShogiScene(gameInformation);
	}

	public void testCombination() {
		BigInteger result;

		result = HonShogiSceneHash.combination(4, 2);
		System.out.println(result);

		result = HonShogiSceneHash.combination(5, 2);
		System.out.println(result);
	}
}
