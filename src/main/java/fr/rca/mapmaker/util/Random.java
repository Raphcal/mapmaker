package fr.rca.mapmaker.util;

import java.security.SecureRandom;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public final class Random {
	private static final SecureRandom secureRandom = new SecureRandom();

	private Random() {}

	public static long nextLong() {
		return secureRandom.nextLong();
	}
}
