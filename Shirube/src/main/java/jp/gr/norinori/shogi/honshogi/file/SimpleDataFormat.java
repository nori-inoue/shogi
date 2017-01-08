package jp.gr.norinori.shogi.honshogi.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jp.gr.norinori.shogi.Action;
import jp.gr.norinori.shogi.Piece;
import jp.gr.norinori.shogi.PieceMove;
import jp.gr.norinori.shogi.Player;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.honshogi.HonShogi;
import jp.gr.norinori.shogi.honshogi.HonShogiPieceType;
import jp.gr.norinori.utility.StringUtil;

public class SimpleDataFormat implements DataFormat {

	// メンバ===================================================================
	private List<PieceMove> list = new ArrayList<>();

	// メソッド=================================================================
	/**
	 *
	 * @param dataFile データファイル
	 */
	public SimpleDataFormat(Player player, String dataFile) {
		byte[] fileContentBytes;
		try {
			fileContentBytes = Files.readAllBytes(Paths.get(dataFile));
			String fileContent = new String(fileContentBytes, StandardCharsets.UTF_8);

			String[] strs = fileContent.split(System.lineSeparator());

			int startIndex = 0;
			if (player.getId() == 2) {
				startIndex = 1;
			}
			for (int i = startIndex; i < strs.length; i += 2) {
				String command = strs[i];
				if (command.isEmpty()) {
					continue;
				}

				PieceMove pieceMove = analyzeCommand(player, command);
				this.list.add(pieceMove);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param player
	 * @param command
	 * @return
	 */
	public static PieceMove analyzeCommand(Player player, String command) {
		Point point = null;

		if (command.isEmpty() || command.length() < 6) {
			throw new IllegalArgumentException("Error command ");
		}

		String x = command.substring(0, 1);
		String y = command.substring(1, 2);
		if (StringUtil.isNumeric(x) && StringUtil.isNumeric(y)) {
			int x1 = 9 - Integer.parseInt(StringUtil.toHankakuNumber(x));
			int y1 = Integer.parseInt(StringUtil.toHankakuNumber(y)) - 1;
			point = new Point(x1, y1);
		}
		String type1 = command.substring(2, 3);
		HonShogiPieceType pieceType = HonShogiPieceType.getPieceType(type1);

		x = command.substring(3, 4);
		y = command.substring(4, 5);

		if (!StringUtil.isNumeric(x) && !StringUtil.isNumeric(y)) {
			throw new IllegalArgumentException("Error command ");
		}

		int x2 = 9 - Integer.parseInt(StringUtil.toHankakuNumber(x));
		int y2 = Integer.parseInt(StringUtil.toHankakuNumber(y)) - 1;
		Point newPoint = new Point(x2, y2);

		String type2 = command.substring(5, 6);
		HonShogiPieceType newPieceType = HonShogiPieceType.getPieceType(type2);

		Piece piece = HonShogi.createPiece(player, pieceType);
		Piece newPiece = HonShogi.createPiece(player, newPieceType);

		return new PieceMove(point, newPoint, piece, newPiece);
	}

	@Override
	public List<PieceMove> getPieceMoveList() {
		return this.list;
	}

	/**
	 *
	 * @param action
	 * @return
	 */
	public static String createCommand(Action action) {
		if (action == null) {
			return "";
		}

		StringBuilder command = new StringBuilder();

		if (action.from == null) {
			command.append("xx");
		} else {
			command.append(9 - action.from.x);
			command.append(action.from.y + 1);
		}
		command.append(HonShogiPieceType.getPieceName(action.fromPiece.type));

		command.append(9 - action.to.x);
		command.append(action.to.y + 1);
		command.append(HonShogiPieceType.getPieceName(action.toPiece.type));

		return command.toString();
	}
}
