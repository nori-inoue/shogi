package jp.gr.norinori.shogi.honshogi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import jp.gr.norinori.shogi.Action;
import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.Piece;
import jp.gr.norinori.shogi.PieceLocations;
import jp.gr.norinori.shogi.PieceType;
import jp.gr.norinori.shogi.Player;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.utility.StringUtil;

/**
 * 本将棋ユーティリティ
 *
 * @author nori
 *
 */
public class HonShogiDisplayUtil {

	/**
	 *
	 * @param gameInformation
	 * @return
	 */
	public static String displayByCharacter(HonShogiScene scene) {

		List<String> displayList = new ArrayList<>();

		HonShogiField field = scene.getField();

		displayList.add("手番：" + scene.getInitiativePlayer().getName());
		displayList.add(scene.getHash());
		displayList.add("");

		PieceLocations PieceLocations = scene.getPieceLocations();

		StringBuilder xNumberLine = new StringBuilder();
		for (byte x = 0; x <= field.x; x++) {
			String number = String.valueOf(field.x - x + 1);
			xNumberLine.append(StringUtil.center(number, 7, ' '));
		}
		displayList.add(xNumberLine.toString());

		displayList.add(StringUtil.pad("+------", field.x + 1) + "+");

		for (byte y = 0; y <= field.y; y++) {
			StringBuilder line = new StringBuilder();

			for (byte x = 0; x <= field.x; x++) {
				Point point = new Point(x, y);
				String square = "";
				if (PieceLocations.existsPiece(point)) {
					square = PieceLocations.get(point).name;
				}
				line.append('|');
				line.append(StringUtil.center(square, 6, ' '));
			}
			line.append("|  ").append(y + 1);
			displayList.add(line.toString());

			displayList.add(StringUtil.pad("+------", field.x + 1) + "+");
		}

		for (Player player : scene.getPlayers().valueList()) {
			displayList.add("");
			displayList.add(player.getName() + " 持ち駒");

			String directionDisplay = "";
			if (player.getDirection() == Direction.DOWN) {
				directionDisplay = "v";
			}

			StringBuilder line = new StringBuilder();
			for (Entry<PieceType, Integer> en : player.getStocks().entrySet()) {
				line.append(" ").append(directionDisplay).append(HonShogiPieceType.getPieceName(en.getKey()))
						.append(" x ").append(en.getValue());
			}
			if (line.length() > 0) {
				displayList.add(line.toString());
			} else {
				displayList.add(" <なし>");
			}
		}

		StringBuilder boardDisplay = new StringBuilder();
		boardDisplay.append(System.lineSeparator());
		for (String s : displayList) {
			boardDisplay.append(s).append(System.lineSeparator());
		}

		return boardDisplay.toString();
	}

	/**
	 *
	 * @param field
	 * @param action
	 * @return
	 */
	public static String displayActionByCharacter(HonShogiField field, Action action) {
		return displayActionByCharacter(field, action.from, action.fromPiece, action.to, action.toPiece);
	}

	/**
	 *
	 * @param field
	 * @param originalPoint
	 * @param originalPiece
	 * @param movePoint
	 * @param movePiece
	 * @return
	 */
	public static String displayActionByCharacter(HonShogiField field, Point originalPoint, Piece originalPiece,
			Point movePoint, Piece movePiece) {
		StringBuilder actionMessage = new StringBuilder("指し手：");
		Point numberOriginalPoint = toNumberPoint(field, originalPoint);
		Point numberMovePoint = toNumberPoint(field, movePoint);

		if (numberOriginalPoint != null) {
			actionMessage.append(numberOriginalPoint.x + "" + numberOriginalPoint.y);
		} else {
			actionMessage.append("  ");
		}
		actionMessage.append(" => ");
		actionMessage.append(numberMovePoint.x + "" + numberMovePoint.y);
		actionMessage.append(" ").append(originalPiece.name);
		if (!originalPiece.type.equals(movePiece.type)) {
			actionMessage.append("成");
		}

		return actionMessage.toString();
	}

	/**
	 *
	 * @param field
	 * @param point
	 * @return
	 */
	public static Point toNumberPoint(HonShogiField field, Point point) {
		if (point == null) {
			return null;
		}
		return new Point(field.x - point.x + 1, point.y + 1);
	}

}
