package jp.gr.norinori.shogi.honshogi;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import jp.gr.norinori.shogi.GameInformation;
import jp.gr.norinori.shogi.PieceMove;
import jp.gr.norinori.shogi.Point;

public class HonShogiTest {

	// 定数=====================================================================
	private final static String RESOURCE_PATH = "./src/test/resources/jp/gr/norinori/shogi/honshogi";

    // メンバ===================================================================
	private GameInformation gameInformation;
	private HonShogiScene scene;

	// メソッド=================================================================
	@Before
	public void setup() {
		this.gameInformation = new GameInformation();
		this.gameInformation.setGameProtocol(new HonShogi());
		this.scene = new HonShogiScene(this.gameInformation);
	}

	@Test
	public void testOuteLine1() {
		HonshogiSettings.load(this.scene, RESOURCE_PATH + "/testOuteLine1.txt");

		this.gameInformation.getGameProtocol().analyzeScene(this.scene);
		System.out.println(HonShogiDisplayUtil.displayByCharacter(this.scene));
		HonShogiPieceZoneOfControl pieceZoneOfControl = (HonShogiPieceZoneOfControl) this.scene
				.getPieceZoneOfControl(this.scene.getInitiativePlayer());

//		System.out.println(pieceZoneOfControl);

		List<PieceMove> actualOuteLine = pieceZoneOfControl.getOuteLine();
		Point[] expectPoints = new Point[3];
		expectPoints[0] = new Point(5, 2);
		expectPoints[1] = new Point(5, 1);
		expectPoints[2] = new Point(5, 0);
		for (int i = 0; i < expectPoints.length; i++) {
			Point actualPoint = actualOuteLine.get(i).to;
			assertEquals(expectPoints[i], actualPoint);
		}

		assertEquals("飛", actualOuteLine.get(0).fromPiece.name);

		List<PieceMove> actualOuteLineSpace = pieceZoneOfControl.getOuteBlockLine();
		expectPoints = new Point[3];
		expectPoints[0] = new Point(7, 1);
		expectPoints[1] = new Point(6, 1);
		expectPoints[2] = new Point(5, 2);
		for (int i = 0; i < expectPoints.length; i++) {
			Point actualPoint = actualOuteLineSpace.get(i).to;
			assertEquals(expectPoints[i], actualPoint);
		}

		assertEquals("飛", actualOuteLineSpace.get(0).fromPiece.name);
		assertEquals("飛", actualOuteLineSpace.get(2).fromPiece.name);
	}

	@Test
	public void testOuteLine2() {
		HonshogiSettings.load(this.scene, RESOURCE_PATH + "/testOuteLine2.txt");

		this.gameInformation.getGameProtocol().analyzeScene(this.scene);
		System.out.println(HonShogiDisplayUtil.displayByCharacter(this.scene));
		HonShogiPieceZoneOfControl pieceZoneOfControl = (HonShogiPieceZoneOfControl) this.scene
				.getPieceZoneOfControl(this.scene.getInitiativePlayer());

//		System.out.println(pieceZoneOfControl);

		List<PieceMove> actualOuteLine = pieceZoneOfControl.getOuteLine();
		Point[] expectPoints = new Point[4];
		expectPoints[0] = new Point(7, 3);
		expectPoints[1] = new Point(6, 2);
		expectPoints[2] = new Point(5, 1);
		expectPoints[3] = new Point(4, 0);
		for (int i = 0; i < expectPoints.length; i++) {
			Point actualPoint = actualOuteLine.get(i).to;
			assertEquals(expectPoints[i], actualPoint);
		}

		assertEquals("角", actualOuteLine.get(0).fromPiece.name);

		List<PieceMove> actualOuteLineSpace = pieceZoneOfControl.getOuteBlockLine();
		expectPoints = new Point[2];
		expectPoints[0] = new Point(7, 3);
		expectPoints[1] = new Point(6, 2);
		for (int i = 0; i < expectPoints.length; i++) {
			Point actualPoint = actualOuteLineSpace.get(i).to;
			assertEquals(expectPoints[i], actualPoint);
		}

		assertEquals("角", actualOuteLineSpace.get(0).fromPiece.name);
	}


	@Test
	public void testOutMove1() {
		HonshogiSettings.load(this.scene, RESOURCE_PATH + "/testOuMove1.txt");

		this.gameInformation.getGameProtocol().analyzeScene(this.scene);
		System.out.println(HonShogiDisplayUtil.displayByCharacter(this.scene));
		HonShogiPieceZoneOfControl pieceZoneOfControl = (HonShogiPieceZoneOfControl) this.scene
				.getPieceZoneOfControl(this.scene.getInitiativePlayer());

//		 System.out.println(pieceZoneOfControl);

		List<PieceMove> actualOuList = pieceZoneOfControl.getOuList();
		Point[] expectPoints = new Point[5];
		expectPoints[0] = new Point(4, 2);
		expectPoints[1] = new Point(6, 2);
		expectPoints[2] = new Point(4, 1);
		expectPoints[3] = new Point(4, 0);
		expectPoints[4] = new Point(6, 0);
		for (int i = 0; i < expectPoints.length; i++) {
			Point actualPoint = actualOuList.get(i).to;
			assertEquals(expectPoints[i], actualPoint);
		}
	}


	@Test
	public void testOutMove2() {
		HonshogiSettings.load(this.scene, RESOURCE_PATH + "/testOuMove2.txt");

		this.gameInformation.getGameProtocol().analyzeScene(this.scene);
		System.out.println(HonShogiDisplayUtil.displayByCharacter(this.scene));
		HonShogiPieceZoneOfControl pieceZoneOfControl = (HonShogiPieceZoneOfControl) this.scene
				.getPieceZoneOfControl(this.scene.getInitiativePlayer());
//		HonShogiPieceZoneOfControl otherPieceZoneOfControl = (HonShogiPieceZoneOfControl) this.scene
//				.getPieceZoneOfControl(this.scene.getOtherPlayer());

//		 System.out.println(pieceZoneOfControl);
//		 System.out.println(otherPieceZoneOfControl);

		List<PieceMove> actualOuList = pieceZoneOfControl.getOuList();
		Point[] expectPoints = new Point[4];
		expectPoints[0] = new Point(5, 2);
		expectPoints[1] = new Point(5, 0);
		expectPoints[2] = new Point(4, 0);
		expectPoints[3] = new Point(6, 0);
		for (int i = 0; i < expectPoints.length; i++) {
			Point actualPoint = actualOuList.get(i).to;
			assertEquals(expectPoints[i], actualPoint);
		}
	}


	@Test
	public void testNGPosition1() {
		HonshogiSettings.load(this.scene, RESOURCE_PATH + "/testNGPosition1.txt");

		this.gameInformation.getGameProtocol().analyzeScene(this.scene);
		System.out.println(HonShogiDisplayUtil.displayByCharacter(this.scene));
		HonShogiPieceZoneOfControl pieceZoneOfControl = (HonShogiPieceZoneOfControl) this.scene
				.getPieceZoneOfControl(this.scene.getInitiativePlayer());
		HonShogiPieceZoneOfControl otherPieceZoneOfControl = (HonShogiPieceZoneOfControl) this.scene
				.getPieceZoneOfControl(this.scene.getOtherPlayer());

		 System.out.println(pieceZoneOfControl);
		 System.out.println(otherPieceZoneOfControl);

		List<PieceMove> actualOuList = pieceZoneOfControl.getList();
		Point[] expectPoints = new Point[6];
		expectPoints[0] = new Point(5, 0);
		expectPoints[1] = new Point(7, 0);
		expectPoints[2] = new Point(4, 1);
		expectPoints[3] = new Point(6, 1);
		expectPoints[4] = new Point(7, 0);
		expectPoints[5] = new Point(8, 0);
		for (int i = 0; i < expectPoints.length; i++) {
			Point actualPoint = actualOuList.get(i).to;
			assertEquals(expectPoints[i], actualPoint);
		}
	}
}
