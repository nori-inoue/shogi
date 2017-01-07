package jp.gr.norinori.shogi.honshogi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map.Entry;

import jp.gr.norinori.core.element.Pair;
import jp.gr.norinori.shogi.Piece;
import jp.gr.norinori.shogi.PieceType;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.utility.StringUtil;

public class HonshogiSettings {

	public static boolean load(HonShogiScene scene, String settingsFile) {

		HonShogiPlayer sente = (HonShogiPlayer) scene.getPlayers().get(HonShogiPlayer.SENTE);
		HonShogiPlayer gote = (HonShogiPlayer) scene.getPlayers().get(HonShogiPlayer.GOTE);

		HonShogiPlayer player = sente;

		byte[] fileContentBytes;
		try {
			fileContentBytes = Files.readAllBytes(Paths.get(settingsFile));
			String fileContent = new String(fileContentBytes, StandardCharsets.UTF_8);

			boolean isInitiativeMode = false;
			String[] strs = fileContent.split(System.lineSeparator());
			for (String str : strs) {
				str = str.trim();
				if (str.trim().isEmpty()) {
					continue;
				}
				if (isInitiativeMode) {
					if (str.equals("先手")) {
						scene.setInitiative(sente);
					} else if (str.equals("後手")) {
						scene.setInitiative(gote);
					}
					continue;
				}
				if (str.equals("=== 手番 ===")) {
					isInitiativeMode = true;
					continue;
				}

				if (str.equals("=== 先手 ===")) {
					player = sente;
					continue;
				}
				if (str.equals("=== 後手 ===")) {
					player = gote;
					continue;
				}

				String x = str.substring(0, 1);
				String y = str.substring(1, 2);
				String typeString = str.substring(2, 3);

				if (typeString.trim().isEmpty()) {
					throw new IllegalArgumentException("No resolve record " + str);
				}
				Piece piece = HonShogi.createPiece(player, HonShogiPieceType.getPieceType(typeString));
				if (x.equals("x")) {
					player.addStock(piece.type);
				} else {
					int x1 = 9 - Integer.parseInt(StringUtil.toHankakuNumber(x));
					int y1 = Integer.parseInt(StringUtil.toHankakuNumber(y)) - 1;
					player.addPiece(new Point(x1, y1), piece);
				}
			}

			// 効率の良い順番にソートしておく
			sente.getPieceLocations().sort();
			gote.getPieceLocations().sort();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean write(HonShogiScene scene, String settingsFile) {

		HonShogiPlayer sente = (HonShogiPlayer) scene.getPlayers().get(HonShogiPlayer.SENTE);
		HonShogiPlayer gote = (HonShogiPlayer) scene.getPlayers().get(HonShogiPlayer.GOTE);

		try {
			File file = new File(settingsFile);
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("=== 先手 ===");
			bw.newLine();

			StringBuilder s = new StringBuilder();
			for (Pair<Point, Piece> pair : sente.getPieceLocations()) {
				s = new StringBuilder();
				Point point = pair.getFirst();
				s.append(9 - (int) point.x).append(1 + (int) point.y);
				s.append(HonShogiPieceType.getPieceName(pair.getSecond().type));
				bw.write(s.toString());
				bw.newLine();
			}
			for (Entry<PieceType, Integer> en : sente.getStocks().entrySet()) {
				int number = en.getValue();
				for (int i = 0; i < number; i++) {
					s = new StringBuilder();
					s.append("xx");
					s.append(HonShogiPieceType.getPieceName(en.getKey()));
					bw.write(s.toString());
					bw.newLine();
				}
			}

			bw.newLine();
			bw.write("=== 後手 ===");
			bw.newLine();

			for (Pair<Point, Piece> pair : gote.getPieceLocations()) {
				s = new StringBuilder();
				Point point = pair.getFirst();
				s.append(9 - (int) point.x).append(1 + (int) point.y);
				s.append(HonShogiPieceType.getPieceName(pair.getSecond().type));
				bw.write(s.toString());
				bw.newLine();
			}
			for (Entry<PieceType, Integer> en : gote.getStocks().entrySet()) {
				int number = en.getValue();
				for (int i = 0; i < number; i++) {
					s = new StringBuilder();
					s.append("xx");
					s.append(HonShogiPieceType.getPieceName(en.getKey()));
					bw.write(s.toString());
					bw.newLine();
				}
			}

			bw.flush();
			// 文字ストリームをクローズする
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
