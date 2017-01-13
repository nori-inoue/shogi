package jp.gr.norinori.shogi.honshogi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.List;

import org.junit.Test;

import jp.gr.norinori.core.collection.TreeNode;
import jp.gr.norinori.shogi.GameInformation;
import jp.gr.norinori.shogi.GameProtocol;
import jp.gr.norinori.shogi.Logger;
import jp.gr.norinori.shogi.PieceMove;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.Timer;
import jp.gr.norinori.utility.StringUtil;

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
	}

	// 王手に対して香が打てないバグ
	@Test
	public void testAction1() {
		Logger.useDebug = true;

		GameInformation gameInformation = new GameInformation();
		GameProtocol gameProtocol = new HonShogi();
		gameInformation.setGameProtocol(gameProtocol);

		HonShogiScene scene = new HonShogiScene(gameInformation);

		HonShogiSceneHash.loadScene("GWaU0HNXUrcmXzRMmETyb1h2RCnilIiufeqE=E5GJO=HJD", scene);
		gameProtocol.analyzeScene(scene);

		System.out.println(HonShogiDisplayUtil.displayByCharacter(scene));
		System.out.println(scene.getPieceZoneOfControl(scene.getInitiativePlayer()));
	}

	// 王手が無視されるバグ
	@Test
	public void testAction2() {
		Logger.useDebug = true;

		GameInformation gameInformation = new GameInformation();
		GameProtocol gameProtocol = new HonShogi();
		gameInformation.setGameProtocol(gameProtocol);

		HonShogiScene scene = new HonShogiScene(gameInformation);

		HonShogiSceneHash.loadScene("EZ32lHzV1pXUDT===BRwtVBIp0vEnZE3dmTT=BJfga=Byn", scene);
		gameProtocol.analyzeScene(scene);

		System.out.println(HonShogiDisplayUtil.displayByCharacter(scene));
		System.out.println(scene.getPieceZoneOfControl(scene.getInitiativePlayer()));
	}

	// 王手が無視されるバグ
	@Test
	public void testAction3() {
		Logger.useDebug = true;

		GameInformation gameInformation = new GameInformation();
		GameProtocol gameProtocol = new HonShogi();
		gameInformation.setGameProtocol(gameProtocol);

		HonShogiScene scene = new HonShogiScene(gameInformation);

		HonShogiSceneHash.loadScene("EmPhtu2vrf68RFByWVNUr=jeTr=aEGf=CIsbaBIQBUoBBL", scene);
		gameProtocol.analyzeScene(scene);

		System.out.println(HonShogiDisplayUtil.displayByCharacter(scene));
		System.out.println(scene.getPieceZoneOfControl(scene.getInitiativePlayer()));

		List<PieceMove> actualOuteEscape = scene.getOuteEscapeList(scene.getInitiativePlayer());
		Point[] expectPoints = new Point[3];
		expectPoints[0] = new Point(3, 7);
		expectPoints[1] = new Point(5, 7);
		expectPoints[2] = new Point(3, 7);
		for (int i = 0; i < expectPoints.length; i++) {
			Point actualPoint = actualOuteEscape.get(i).to;
			assertEquals(expectPoints[i], actualPoint);
		}

		assertEquals("王", actualOuteEscape.get(0).fromPiece.name);
		assertEquals("銀", actualOuteEscape.get(2).fromPiece.name);
	}

	// 持ち駒で防げないバグ
	@Test
	public void testAction4() {
		Logger.useDebug = true;

		GameInformation gameInformation = new GameInformation();
		GameProtocol gameProtocol = new HonShogi();
		gameInformation.setGameProtocol(gameProtocol);

		HonShogiScene scene = new HonShogiScene(gameInformation);

		HonShogiSceneHash.loadScene("IciauRUt+dsGov===VNns=CG4npR44n=Wv7f=BX9OwcBIc", scene);
		gameProtocol.analyzeScene(scene);

		System.out.println(HonShogiDisplayUtil.displayByCharacter(scene));
		System.out.println(scene.getPieceZoneOfControl(scene.getInitiativePlayer()));

		boolean isExists = false;
		List<PieceMove> list = scene.getPieceZoneOfControl(scene.getInitiativePlayer()).getList();
		for (PieceMove pieceMove : list) {
			if (pieceMove.from == null && pieceMove.to.equals(new Point(5, 8))) {
				isExists = true;
				break;
			}
		}
		assertTrue(isExists);

		// List<PieceMove> actualOuteEscape =
		// scene.getOuteEscapeList(scene.getInitiativePlayer());
		// Point[] expectPoints = new Point[3];
		// expectPoints[0] = new Point(3, 7);
		// expectPoints[1] = new Point(5, 7);
		// expectPoints[2] = new Point(3, 7);
		// for (int i = 0; i < expectPoints.length; i++) {
		// Point actualPoint = actualOuteEscape.get(i).to;
		// assertEquals(expectPoints[i], actualPoint);
		// }
		//
		// assertEquals("王", actualOuteEscape.get(0).fromPiece.name);
		// assertEquals("銀", actualOuteEscape.get(2).fromPiece.name);
	}

	// 局面のクローンの速度調査
	@Test
	public void testSceneClone() {
		GameInformation gameInformation = new GameInformation();
		GameProtocol gameProtocol = new HonShogi();
		gameInformation.setGameProtocol(gameProtocol);

		HonShogiScene scene = new HonShogiScene(gameInformation);

		HonShogiSceneHash.loadScene("EmPhtu2vrf68RFByWVNUr=jeTr=aEGf=CIsbaBIQBUoBBL", scene);
		gameProtocol.analyzeScene(scene);

		LoggerLabel.sceneClone = Timer.start("scene clone", LoggerLabel.sceneClone);
		for (int i = 0; i < 50000; i++) {
			scene = (HonShogiScene) scene.clone();
		}
		Timer.stop(LoggerLabel.sceneClone);

		logTimer(Timer.getTreeTimeids());
	}

	/**
	 * ツリーログ出力
	 *
	 * @param rootNode ルートノード
	 */
	private void logTimer(TreeNode<String> rootNode) {
		for (TreeNode<String> node : rootNode.children()) {
			logTimer(node, 0);
		}
	}

	/**
	 * ツリーログ出力
	 *
	 * @param node ノード
	 * @param depth 深さ
	 */
	private void logTimer(TreeNode<String> node, int depth) {
		String pad = "";
		if (depth > 0) {
			pad = StringUtil.pad(" ", depth * 2);
		}
		System.out
				.println(pad + node.getValue() + ":" + StringUtil.formatNumber(Timer.getTotal(node.getValue()), "#,###")
						+ "ms (" + StringUtil.formatNumber(Timer.getCount(node.getValue()), "#,###") + ")");

		for (TreeNode<String> child : node.children()) {
			logTimer(child, depth + 1);
		}
	}
}
