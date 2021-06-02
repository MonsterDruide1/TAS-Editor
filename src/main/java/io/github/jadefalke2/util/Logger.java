package io.github.jadefalke2.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

	public static void log (String s) {
		// using this to make it easier to later change this behaviour

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String fullDateFormatted = "[" + dtf.format(now) + "]: ";

		System.out.println(fullDateFormatted + s);
	}
}
