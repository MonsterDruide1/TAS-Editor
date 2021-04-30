package io.github.jadefalke2;

import java.io.*;
import java.util.stream.Collectors;

public class Util {

	/**
	 * Takes in a file and converts it to a readable string
	 * @param file the file to be read
	 * @return the contents of the file
	 */
	public static String fileToString (File file) throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		return br.lines().map(sCurrentLine -> sCurrentLine + "\n").collect(Collectors.joining());
	}

	/**
	 * Writes a string to a file in the filesystem
	 * @param string the string to be written
	 * @param file the file it should be written to
	 */
	public static void writeFile(String string, File file) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(string);
		writer.flush();
		writer.close();
	}

}
