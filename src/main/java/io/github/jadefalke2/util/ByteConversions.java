package io.github.jadefalke2.util;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.BitSet;

public class ByteConversions {

	public static byte[] fromShort(short s, ByteOrder order) {
		if(order.equals(ByteOrder.BIG_ENDIAN)) {
			return new byte[] {(byte)(s >> 8), (byte)s};
		} else {
			return new byte[] {(byte)s, (byte)(s >> 8)};
		}
	}

	public static byte[] fromInt(int i, ByteOrder order) {
		if(order.equals(ByteOrder.BIG_ENDIAN)) {
			return new byte[] {
				(byte)(i >> 24),
				(byte)(i >> 16),
				(byte)(i >> 8),
				(byte)i};
		} else {
			return new byte[] {
				(byte)i,
				(byte)(i >> 8),
				(byte)(i >> 16),
				(byte)(i >> 24)};
		}
	}

	public static byte[] fromLong(long l, ByteOrder order) {
		if(order.equals(ByteOrder.BIG_ENDIAN)) {
			return new byte[] {
				(byte) (l >> 56),
				(byte) (l >> 48),
				(byte) (l >> 40),
				(byte) (l >> 32),
				(byte) (l >> 24),
				(byte) (l >> 16),
				(byte) (l >> 8),
				(byte) l};
		} else {
			return new byte[] {
				(byte) l,
				(byte) (l >> 8),
				(byte) (l >> 16),
				(byte) (l >> 24),
				(byte) (l >> 32),
				(byte) (l >> 40),
				(byte) (l >> 48),
				(byte) (l >> 56)};
		}
	}

	public static byte[] fromFloat(float f, ByteOrder order) {
		return fromInt(Float.floatToIntBits(f), order);
	}

	public static byte[] fromDouble(double d, ByteOrder order) {
		return fromLong(Double.doubleToLongBits(d), order);
	}

	public static byte[] fromBooleans(boolean... bools) {
		BitSet bits = new BitSet(bools.length);
		for (int i = 0; i < bools.length; i++) {
			if (bools[i]) {
				bits.set(i);
			}
		}

		byte[] bytes = bits.toByteArray();
		if (bytes.length * 8 >= bools.length) {
			return bytes;
		} else {
			return Arrays.copyOf(bytes, bools.length / 8 + (bools.length % 8 == 0 ? 0 : 1));
		}
	}

	public static int toInt(byte[] data, ByteOrder order) {
		if(order.equals(ByteOrder.BIG_ENDIAN)) {
			return
				((data[0] & 0xff) << 24) |
				((data[1] & 0xff) << 16) |
				((data[2] & 0xff) << 8) |
				(data[3] & 0xff);
		} else {
			return
				((int)data[0] & 0xff) |
				(((int)data[1] & 0xff) << 8) |
				(((int)data[2] & 0xff) << 16) |
				(((int)data[3] & 0xff) << 24);
		}
	}

}
