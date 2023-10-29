package io.github.jadefalke2.util;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Util {

	/**
	 * Takes in a file and converts it to a readable string
	 * @param file the file to be read
	 * @return the contents of the file
	 */
	public static String fileToString (File file) throws IOException {
		try(BufferedReader br = new BufferedReader(new FileReader(file))){
			return br.lines().collect(Collectors.joining("\n"));
		}
	}
	public static byte[] fileToBytes(File file) throws IOException {
		return Files.readAllBytes(file.toPath());
	}

	/**
	 * Writes a string to a file in the filesystem
	 * @param string the string to be written
	 * @param file the file it should be written to
	 */
	public static void writeFile(String string, File file) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(string);
		writer.close();
	}

	public static void writeFile(byte[] data, File file) throws IOException {
		Files.write(file.toPath(), data);
	}

	public static byte[] merge(ArrayList<byte[]> data) {
		int totalLength = 0;
		for (byte[] bytes : data) {
			totalLength += bytes.length;
		}
		byte[] result = new byte[totalLength];
		int currentPos = 0;
		for (byte[] bytes : data) {
			System.arraycopy(bytes, 0, result, currentPos, bytes.length);
			currentPos += bytes.length;
		}
		return result;
	}
	public static byte[] merge(byte[]... data) {
		int totalLength = 0;
		for (byte[] bytes : data) {
			totalLength += bytes.length;
		}
		byte[] result = new byte[totalLength];
		int currentPos = 0;
		for (byte[] bytes : data) {
			System.arraycopy(bytes, 0, result, currentPos, bytes.length);
			currentPos += bytes.length;
		}
		return result;
	}
}
