package jp.gr.norinori.shogi;

import java.math.BigInteger;

/**
 * Original Base64
 *
 * @author nori
 */
public class NumberBase64 {
	private static final String TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	private static final BigInteger BIT64 = BigInteger.valueOf(0x3f);

	/**
	 * long to Original Base64
	 *
	 * @param data
	 * @return Base64
	 */
	public static String encode(long data) {
		StringBuilder result = new StringBuilder("");
		try {

			while (data > 0) {
				int x = (int) data & 0x3f; // 6bit分の値取得
				result.append(TABLE.charAt(x));

				data = data >> 6; // 6bit分シフト
			}
		} catch (Exception e) {
			return "";
		}
		return result.reverse().toString();
	}

	/**
	 * BigInteger to Original Base64
	 *
	 * @param data
	 * @return Base64
	 */
	public static String encode(BigInteger data) {
		StringBuilder result = new StringBuilder("");
		try {

			while (data.compareTo(BigInteger.ZERO) > 0) {
				BigInteger x = data.and(BIT64);
				result.append(TABLE.charAt(x.intValue()));

				data = data.shiftRight(6);
			}
		} catch (Exception e) {
			return "";
		}
		return result.reverse().toString();
	}

	/**
	 * Original Base64 to BigInteger
	 *
	 * @param data
	 * @return BigInteger
	 */
	public static BigInteger decode(String base64) {
		BigInteger result = BigInteger.ZERO;
		int length = base64.length();

		for (int i = 0; i < length; i++) {
			int c = base64.charAt(i);
			int data = 0;

			// TABLE.indexOf()は遅い
			if ('A' <= c && c <= 'Z') {
				data = c - 'A';
			} else if ('a' <= c && c <= 'z') {
				data = c - 'a' + 26;
			} else if ('0' <= c && c <= '9') {
				data = c - '0' + 52;
			} else if (c == '+') {
				data = 62;
			} else if (c == '/') {
				data = 63;
			}
			result = result.add(BigInteger.valueOf(data));
			if (i + 1 < length) {
				result = result.shiftLeft(6);
			}
		}

		return result;
	}
}
