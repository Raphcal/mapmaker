package fr.rca.mapmaker.exception;

import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JOptionPane;

/**
 *
 * @author daeke
 */
public final class Exceptions {
	
	private Exceptions() {}
	
	public static void showStackTrace(Throwable e, Component parent) {
		final Throwable rootCause = getRootCause(e);
		
		JOptionPane.showMessageDialog(parent, getStackTrace(rootCause), "Erreur", JOptionPane.ERROR_MESSAGE);
	}
	
	private static Throwable getRootCause(Throwable e) {
		Throwable cause = e.getCause();
		while(cause != null) {
			e = cause;
			cause = e.getCause();
		}
		return e;
	}
	
	private static String getStackTrace(Throwable e) {
		final StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		
		return writer.toString();
	}
}
