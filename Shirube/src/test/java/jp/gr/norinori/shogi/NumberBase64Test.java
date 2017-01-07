package jp.gr.norinori.shogi;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

public class NumberBase64Test {

	@Test
	public void testEncode() {
		String result;

		result = NumberBase64.encode(1);
		assertEquals("B", result);

		result = NumberBase64.encode(64);
		assertEquals("BA", result);

		result = NumberBase64.encode(4096);
		assertEquals("BAA", result);
	}

	@Test
	public void testDecode() {
		BigInteger result;

		result = NumberBase64.decode("B");
		assertEquals(BigInteger.valueOf(1), result);

		result = NumberBase64.decode("BA");
		assertEquals(BigInteger.valueOf(64), result);


		result = NumberBase64.decode("BAA");
		assertEquals(BigInteger.valueOf(4096), result);
	}
}
