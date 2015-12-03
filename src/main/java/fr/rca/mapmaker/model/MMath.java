package fr.rca.mapmaker.model;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public final class MMath {
	
	public static int mod(int value, int modulo) {
		return ((value % modulo) + modulo) % modulo;
	}
	
}
