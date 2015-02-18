package fr.rca.mapmaker.io.mkz;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class Surfaces {
	
	protected Surfaces() {
	}
	
	public static int getNearestUpperPowerOfTwoForSurface(long value) {
		for(int pot = 0;; pot++) {
			final int pow = (int) Math.pow(2.0, pot);
			final long surface = pow * pow;
			
			if(surface >= value) {
				return pow;
			}
		}
	}
	
	public static int getNearestLowerPowerOfTwo(int value) {
		for(int pot = 1;; pot++) {
			final int pow = (int) Math.pow(2.0, pot);
			
			if(pow > value) {
				return (int) Math.pow(2.0, pot - 1);
			}
		}
	}
}
