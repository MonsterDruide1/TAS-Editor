package io.github.jadefalke2.script;

import io.github.jadefalke2.Script;
import io.github.jadefalke2.util.CorruptedScriptException;

import java.io.File;
import java.io.IOException;

public enum Format {

	STAS,
	nxTAS;

	public static Script read(File file, Format format) throws IOException, CorruptedScriptException {
		if(format == nxTAS) return NXTas.read(file);
		else if(format == STAS) return STas.read(file);
		else throw new IllegalStateException("Unexpected value: " + format);
	}
	public static void write(Script script, File file, Format format) throws IOException {
		if(format == nxTAS) NXTas.write(script, file);
		else if(format == STAS) STas.write(script, file);
		else throw new IllegalStateException("Unexpected value: " + format);
	}

}
