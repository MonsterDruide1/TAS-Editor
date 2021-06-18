package io.github.jadefalke2.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

	public static void log (String s) {
		// using this to make it easier to later change this behaviour
		System.out.println(getTimeString() + s);
	}

	private static String getTimeString () {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.ms");
		LocalDateTime now = LocalDateTime.now();
		return "[" + dtf.format(now) + "]: ";
	}
}
