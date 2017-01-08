package jp.gr.norinori.shogi.honshogi;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;

import jp.gr.norinori.core.element.KeyValuePair;
import jp.gr.norinori.shogi.NumberBase64;
import jp.gr.norinori.shogi.Piece;
import jp.gr.norinori.shogi.PieceType;
import jp.gr.norinori.shogi.Player;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.honshogi.piece.Fu;
import jp.gr.norinori.shogi.honshogi.piece.Gin;
import jp.gr.norinori.shogi.honshogi.piece.Hisha;
import jp.gr.norinori.shogi.honshogi.piece.Kaku;
import jp.gr.norinori.shogi.honshogi.piece.Keima;
import jp.gr.norinori.shogi.honshogi.piece.Kin;
import jp.gr.norinori.shogi.honshogi.piece.Kyo;
import jp.gr.norinori.shogi.honshogi.piece.Narigin;
import jp.gr.norinori.shogi.honshogi.piece.Narikei;
import jp.gr.norinori.shogi.honshogi.piece.Narikyo;
import jp.gr.norinori.shogi.honshogi.piece.Ou;
import jp.gr.norinori.shogi.honshogi.piece.Ryu;
import jp.gr.norinori.shogi.honshogi.piece.Tokin;
import jp.gr.norinori.shogi.honshogi.piece.Uma;

/**
 * 局面ハッシュ
 *
 * @author nori
 *
 */
public class HonShogiSceneHash {

	// メソッド=================================================================
	public static String getHash(HonShogiScene scene) {
		int[] fu = new int[18];
		int fuCount = 0;
		int[] kyo = new int[4];
		int kyoCount = 0;
		int[] keima = new int[4];
		int keimaCount = 0;
		int[] gin = new int[4];
		int ginCount = 0;
		int[] kin = new int[4];
		int kinCount = 0;
		int[] kaku = new int[2];
		int kakuCount = 0;
		int[] hisya = new int[2];
		int hisyaCount = 0;
		int[] ou = new int[2];
		int ouCount = 0;

		for (Player player : scene.getPlayers().valueList()) {
			for (KeyValuePair<Point, Piece> pieceLocations : player.getPieceLocations()) {
				PieceType honShogiPiece = pieceLocations.getValue().type;
				if (honShogiPiece instanceof Fu) {
					fu[fuCount++] = createHash(player, pieceLocations.getKey());
				} else if (honShogiPiece instanceof Kyo) {
					kyo[kyoCount++] = createHash(player, pieceLocations.getKey());
				} else if (honShogiPiece instanceof Keima) {
					keima[keimaCount++] = createHash(player, pieceLocations.getKey());
				} else if (honShogiPiece instanceof Gin) {
					gin[ginCount++] = createHash(player, pieceLocations.getKey());
				} else if (honShogiPiece instanceof Kin) {
					kin[kinCount++] = createHash(player, pieceLocations.getKey());
				} else if (honShogiPiece instanceof Kaku) {
					kaku[kakuCount++] = createHash(player, pieceLocations.getKey());
				} else if (honShogiPiece instanceof Hisha) {
					hisya[hisyaCount++] = createHash(player, pieceLocations.getKey());
				} else if (honShogiPiece instanceof Ou) {

					// 手番情報を王の成情報に乗せる
					if (player.getId() == HonShogiPlayer.SENTE) {
						ou[ouCount++] = createHash(player, pieceLocations.getKey(), scene.getInitiativePlayer().getId() - 1);
					} else {
						ou[ouCount++] = createHash(player, pieceLocations.getKey());
					}
				} else if (honShogiPiece instanceof Tokin) {
					fu[fuCount++] = createHash(player, pieceLocations.getKey(), 1);
				} else if (honShogiPiece instanceof Narikyo) {
					kyo[kyoCount++] = createHash(player, pieceLocations.getKey(), 1);
				} else if (honShogiPiece instanceof Narikei) {
					keima[keimaCount++] = createHash(player, pieceLocations.getKey(), 1);
				} else if (honShogiPiece instanceof Narigin) {
					gin[ginCount++] = createHash(player, pieceLocations.getKey(), 1);
				} else if (honShogiPiece instanceof Uma) {
					kaku[kakuCount++] = createHash(player, pieceLocations.getKey(), 1);
				} else if (honShogiPiece instanceof Ryu) {
					hisya[hisyaCount++] = createHash(player, pieceLocations.getKey(), 1);
				}
			}

			for (Map.Entry<PieceType, Integer> en : player.getStocks().entrySet()) {
				int stock = 82;
				PieceType honShogiPiece = en.getKey();
				if (honShogiPiece instanceof Fu) {
					for (int i = 0; i < en.getValue(); i++) {
						fu[fuCount++] = createHash(player, null) + stock;
						stock++;
					}
				} else if (honShogiPiece instanceof Kyo) {
					for (int i = 0; i < en.getValue(); i++) {
						kyo[kyoCount++] = createHash(player, null) + stock;
						stock++;
					}
				} else if (honShogiPiece instanceof Keima) {
					for (int i = 0; i < en.getValue(); i++) {
						keima[keimaCount++] = createHash(player, null) + stock;
						stock++;
					}
				} else if (honShogiPiece instanceof Gin) {
					for (int i = 0; i < en.getValue(); i++) {
						gin[ginCount++] = createHash(player, null) + stock;
						stock++;
					}
				} else if (honShogiPiece instanceof Kin) {
					for (int i = 0; i < en.getValue(); i++) {
						kin[kinCount++] = createHash(player, null) + stock;
						stock++;
					}
				} else if (honShogiPiece instanceof Kaku) {
					for (int i = 0; i < en.getValue(); i++) {
						kaku[kakuCount++] = createHash(player, null) + stock;
						stock++;
					}
				} else if (honShogiPiece instanceof Hisha) {
					for (int i = 0; i < en.getValue(); i++) {
						hisya[hisyaCount++] = createHash(player, null) + stock;
						stock++;
					}
				} else if (honShogiPiece instanceof Ou) {
					ou[ouCount++] = createHash(player, null) + stock;
					stock++;
				}
			}
		}

		Arrays.sort(fu);
		Arrays.sort(kyo);
		Arrays.sort(keima);
		Arrays.sort(gin);
		Arrays.sort(kin);
		Arrays.sort(kaku);
		Arrays.sort(hisya);
		Arrays.sort(ou);

		StringBuilder hash = new StringBuilder();
		hash.append(to64x(createHash(fu), 17));
		hash.append(to64x(createHash(kyo), 5));
		hash.append(to64x(createHash(keima), 5));
		hash.append(to64x(createHash(gin), 5));
		hash.append(to64x(createHash(kin), 5));
		hash.append(to64x(createHash(kaku), 3));
		hash.append(to64x(createHash(hisya), 3));
		hash.append(to64x(createHash(ou), 3));

		return hash.toString();
	}

	private static int createHash(Player player, Point key) {
		return createHash(player, key, 0);
	}

	private static int createHash(Player player, Point key, int nari) {
		if (key != null) {
			return ((player.getId() - 1) + nari * 2) * 90 + (key.x + (key.y * 9));
		}
		return ((player.getId() - 1) + nari * 2) * 90;
	}

	public static BigInteger createHash(int table[]) {
		return createHash(table, 360);
	}

	public static BigInteger createHash(int table[], int size) {
		BigInteger hash = BigInteger.ZERO;
		for (int i = table.length - 1; i >= 0; i--) {
			hash = hash.add(combination(table[i], i + 1));
		}
		return hash;
	}

	public static String to64x(BigInteger value, int length) {
		String base64 = NumberBase64.encode(value);
		String pad = "====================".substring(0, length - base64.length());
		return base64 + pad;
	}

	public static void loadScene(String hash, HonShogiScene scene) {

		loadPieces(hash, 0, 17, 18, 0, scene);
		loadPieces(hash, 17, 5, 4, 1, scene);
		loadPieces(hash, 22, 5, 4, 2, scene);
		loadPieces(hash, 27, 5, 4, 3, scene);
		loadPieces(hash, 32, 5, 4, 4, scene);
		loadPieces(hash, 37, 3, 2, 5, scene);
		loadPieces(hash, 40, 3, 2, 6, scene);
		loadPieces(hash, 43, 3, 2, 7, scene);
	}

	private static void loadPieces(String hash, int position, int hashLength, int length, int type, HonShogiScene scene) {
		int points[];
		Player sente = scene.getPlayers().get(HonShogiPlayer.SENTE);
		Player gote = scene.getPlayers().get(HonShogiPlayer.GOTE);

		Player player;
		HonShogiPieceType pieceType;

		int[] pointValuees = toCombinantion(toBigInteger(hash, position, hashLength), 360, length);

		for (int p : pointValuees) {
			points = toPoints(p);

			if (type == 0) {
				if (points[3] == 0) {
					pieceType = new Fu();
				} else {
					pieceType = new Tokin();
				}
			} else if (type == 1) {
				if (points[3] == 0) {
					pieceType = new Kyo();
				} else {
					pieceType = new Narikyo();
				}
			} else if (type == 2) {
				if (points[3] == 0) {
					pieceType = new Keima();
				} else {
					pieceType = new Narikei();
				}
			} else if (type == 3) {
				if (points[3] == 0) {
					pieceType = new Gin();
				} else {
					pieceType = new Narigin();
				}
			} else if (type == 4) {
				pieceType = new Kin();
			} else if (type == 5) {
				if (points[3] == 0) {
					pieceType = new Kaku();
				} else {
					pieceType = new Uma();
				}
			} else if (type == 6) {
				if (points[3] == 0) {
					pieceType = new Hisha();
				} else {
					pieceType = new Ryu();
				}
			} else if (type == 7) {
				pieceType = new Ou();
				// 王の成情報に手番情報が設定されている
				if (points[2] == 0 && points[3] == 0) {
					scene.setInitiative(sente);
				} else if (points[2] == 0 && points[3] == 1) {
					scene.setInitiative(gote);
				}
			} else {
				pieceType = null;
			}

			if (points[2] == 0) {
				player = sente;
			} else {
				player = gote;
			}
			Piece piece = HonShogi.createPiece(player, pieceType);
			if (points[1] > 8) {
				player.addStock(pieceType);
			} else {
				player.addPiece(new Point(points[0], points[1]), piece);
			}
		}
	}

	/*
	 * @return int[0]:x int[1]:y int[2]:Player 0|1 int[3]:nari 0|1
	 */
	private static int[] toPoints(int value) {
		int[] result = new int[4];

		int point = value % 90;
		int playerNari = (value - point) / 90;

		result[0] = point % 9;
		result[1] = (point - result[0]) / 9;

		result[2] = playerNari % 2;
		result[3] = (playerNari - result[2]) / 2;

		return result;
	}

	public static int[] toCombinantion(BigInteger value, int n, int r) {
		int[] result = new int[r];

		BigInteger v = value;
		for (int i = 0; i < r; i++) {
			for (int j = n - 1 - i; j >= 0; j--) {
				BigInteger max = combination(j, r - i);
				if (max.compareTo(v) <= 0) {
					v = v.subtract(max);
					result[r - 1 - i] = j;
					break;
				}
			}
		}

		return result;
	}

	/**
	 *
	 * 入山徳夫氏アルゴリズム
	 *
	 * @param n
	 * @param r
	 * @return 組み合わせ数 nCr
	 */
	public static BigInteger combination(int n, int r) {
		if (n < 0 || r < 0 || r > n) {
			return BigInteger.ZERO;
		}

		if (n - r < r) {
			r = n - r;
		}
		if (r == 0) {
			return BigInteger.ONE;
		}
		if (r == 1) {
			return BigInteger.valueOf(n);
		}

		int[] numerator = new int[r];
		int[] denominator = new int[r];

		for (int k = 0; k < r; k++) {
			numerator[k] = n - r + k + 1;
			denominator[k] = k + 1;
		}

		for (int p = 2; p <= r; p++) {
			int pivot = denominator[p - 1];
			if (pivot > 1) {
				int offset = (n - r) % p;
				for (int k = p - 1; k < r; k += p) {
					numerator[k - offset] /= pivot;
					denominator[k] /= pivot;
				}
			}
		}

		BigInteger result = BigInteger.ONE;
		for (int k = 0; k < r; k++) {
			if (numerator[k] > 1) {
				result = result.multiply(BigInteger.valueOf(numerator[k]));
			}
		}

		return result;
	}

	private static BigInteger toBigInteger(String hash, int position, int length) {
		String base64 = hash.substring(position, position + length);
		return NumberBase64.decode(base64.replaceAll("=", ""));
	}
}
