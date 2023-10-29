package io.github.jadefalke2.script;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.util.CorruptedScriptException;
import io.github.jadefalke2.util.Logger;
import io.github.jadefalke2.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NXTas {

	public static void write(Script script, File file) throws IOException {
		Logger.log("saving script to " + file.getAbsolutePath());
		Util.writeFile(write(script), file);
	}

	public static String write(Script script) {
		InputLine[] inputLines = script.getLines();
		return IntStream.range(0, inputLines.length).filter(i -> !inputLines[i].isEmpty()).mapToObj(i -> inputLines[i].getFull(i) + "\n").collect(
			Collectors.joining());
	}

	public static Script read(File file) throws CorruptedScriptException, IOException {
		Script s = read(Util.fileToString(file));
		s.setFile(file, Format.nxTAS);
		return s;
	}

	public static Script read(String script) throws CorruptedScriptException {
		List<InputLine> inputLines = new ArrayList<>();
		String[] lines = script.split("\n");

		int currentFrame = 0;

		for (String line : lines) {
			InputLine currentInputLine = new InputLine(line);
			int frame = Integer.parseInt(line.split(" ")[0]);

			if (frame < currentFrame){
				throw new CorruptedScriptException("Line numbers misordered", currentFrame);
			}

			while(currentFrame < frame){
				inputLines.add(InputLine.getEmpty());
				currentFrame++;
			}

			inputLines.add(currentInputLine);
			currentFrame++;
		}
		return new Script(inputLines.toArray(new InputLine[0]), 0);
	}

}
