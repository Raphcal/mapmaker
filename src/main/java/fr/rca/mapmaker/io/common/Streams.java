package fr.rca.mapmaker.io.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author daeke
 */
public final class Streams {
	
	private static final int CHAR_SIZE = 2;
	private static final int INTEGER_SIZE = 4;
	private static final int LONG_SIZE = 8;
	
	private Streams() {}
	
	private static byte[] asArray(long value, int size) {
		final byte[] result = new byte[size];
		
		for(int index = 0; index < size; index++) {
			result[index] = (byte) (value & 0xFF);
			value = value >> 8;
		}
		
		return result;
	}
	
	private static long fromArray(byte... bytes) {
		long result = 0L;
		
		for(int index = 0; index < bytes.length; index++) {
			result = result | (((long)(bytes[index] & 0xFF)) << (8 * index));
		}
		
		return result;
	}
	
	public static void write(byte b, OutputStream outputStream) throws IOException {
		outputStream.write((int) b);
	}
	
	public static void write(boolean b, OutputStream outputStream) throws IOException {
		write(b ? (byte)1 : (byte)0, outputStream);
	}
	
	public static void write(char c, OutputStream outputStream) throws IOException {
		outputStream.write(asArray(c, CHAR_SIZE));
	}
	
	public static void write(int i, OutputStream outputStream) throws IOException {
		outputStream.write(asArray(i, INTEGER_SIZE));
	}
	
	public static void write(long l, OutputStream outputStream) throws IOException {
		outputStream.write(asArray(l, LONG_SIZE));
	}
	
	public static void write(float f, OutputStream outputStream) throws IOException {
		write(Float.floatToIntBits(f), outputStream);
	}
	
	public static void write(double d, OutputStream outputStream) throws IOException {
		write(Double.doubleToLongBits(d), outputStream);
	}
	
	public static void write(Date date, OutputStream outputStream) throws IOException {
		write(date.getTime(), outputStream);
	}
	
	public static void write(byte[] array, OutputStream outputStream) throws IOException {
		write(array.length, outputStream);
		
		for(final byte b : array) {
			write(b, outputStream);
		}
	}
	
	public static void write(char[] array, OutputStream outputStream) throws IOException {
		write(array.length, outputStream);
		
		for(final char c : array) {
			write(c, outputStream);
		}
	}
	
	public static void write(String s, OutputStream outputStream) throws IOException {
		write(s.toCharArray(), outputStream);
	}
	
	public static void writeNullable(String s, OutputStream outputStream) throws IOException {
		write(s != null, outputStream);
		if(s != null) {
			write(s, outputStream);
		}
	}
	
	public static void write(Class<?> clazz, OutputStream outputStream) throws IOException {
		write(clazz.getName(), outputStream);
	}
	
	public static void write(int[] array, OutputStream outputStream) throws IOException {
		write(array.length, outputStream);
		
		for(final int i : array) {
			write(i, outputStream);
		}
	}
	
	public static void write(long[] array, OutputStream outputStream) throws IOException {
		write(array.length, outputStream);
		
		for(final long l : array) {
			write(l, outputStream);
		}
	}
	
	public static void write(float[] array, OutputStream outputStream) throws IOException {
		write(array.length, outputStream);
		
		for(final float f : array) {
			write(f, outputStream);
		}
	}
	
	public static void write(double[] array, OutputStream outputStream) throws IOException {
		write(array.length, outputStream);
		
		for(final double d : array) {
			write(d, outputStream);
		}
	}
	
	public static byte readByte(InputStream inputStream) throws IOException {
		return (byte) inputStream.read();
	}
	
	public static boolean readBoolean(InputStream inputStream) throws IOException {
		return readByte(inputStream) == (byte)1;
	}
	
	public static char readChar(InputStream inputStream) throws IOException {
		final byte[] bytes = new byte[CHAR_SIZE];
		inputStream.read(bytes);
		
		return (char) fromArray(bytes);
	}
	
	public static int readInt(InputStream inputStream) throws IOException {
		final byte[] bytes = new byte[INTEGER_SIZE];
		inputStream.read(bytes);
		
		return (int) fromArray(bytes);
	}
	
	public static long readLong(InputStream inputStream) throws IOException {
		final byte[] bytes = new byte[LONG_SIZE];
		inputStream.read(bytes);
		
		return fromArray(bytes);
	}
	
	public static float readFloat(InputStream inputStream) throws IOException {
		return Float.intBitsToFloat(readInt(inputStream));
	}
	
	public static double readDouble(InputStream inputStream) throws IOException {
		return Double.longBitsToDouble(readLong(inputStream));
	}
	
	public static Date readDate(InputStream inputStream) throws IOException {
		return new Date(readLong(inputStream));
	}
	
	public static byte[] readByteArray(InputStream inputStream) throws IOException {
		final byte[] array = new byte[readInt(inputStream)];
		
		for(int index = 0; index < array.length; index++) {
			array[index] = readByte(inputStream);
		}
		
		return array;
	}
	
	public static char[] readCharArray(InputStream inputStream) throws IOException {
		final char[] array = new char[readInt(inputStream)];
		
		for(int index = 0; index < array.length; index++) {
			array[index] = readChar(inputStream);
		}
		
		return array;
	}
	
	@NotNull
	public static String readString(InputStream inputStream) throws IOException {
		return new String(readCharArray(inputStream));
	}
	
	@Nullable
	public static String readNullableString(InputStream inputStream) throws IOException {
		if(readBoolean(inputStream)) {
			return readString(inputStream);
		} else {
			return null;
		}
	}
	
	public static Class<?> readClass(InputStream inputStream) throws IOException {
		final String className = readString(inputStream);
		try {
			return Class.forName(className);
			
		} catch (ClassNotFoundException ex) {
			throw new IOException("Impossible de lire la class '" + className + "'.", ex);
		}
	}
	
	public static int[] readIntArray(InputStream inputStream) throws IOException {
		final int[] array = new int[readInt(inputStream)];
		
		for(int index = 0; index < array.length; index++) {
			array[index] = readInt(inputStream);
		}
		
		return array;
	}
	
	public static long[] readLongArray(InputStream inputStream) throws IOException {
		final long[] array = new long[readInt(inputStream)];
		
		for(int index = 0; index < array.length; index++) {
			array[index] = readLong(inputStream);
		}
		
		return array;
	}
	
	public static float[] readFloatArray(InputStream inputStream) throws IOException {
		final float[] array = new float[readInt(inputStream)];
		
		for(int index = 0; index < array.length; index++) {
			array[index] = readFloat(inputStream);
		}
		
		return array;
	}
	
	public static double[] readDoubleArray(InputStream inputStream) throws IOException {
		final double[] array = new double[readInt(inputStream)];
		
		for(int index = 0; index < array.length; index++) {
			array[index] = readDouble(inputStream);
		}
		
		return array;
	}
}
