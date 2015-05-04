package fr.rca.mapmaker.model.sprite;

import fr.rca.mapmaker.model.map.TileLayer;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class CollisionMask {
	private static final int FLAGS_BY_BYTE = 8;
	
	private final byte[] mask;

	public CollisionMask(TileLayer layer) {
		final int length = layer.getWidth() * layer.getHeight() / FLAGS_BY_BYTE;
		mask = new byte[length];
		
		int index = 0;
		for(int y = 0; y < layer.getHeight(); y++) {
			for(int x = 0; x < layer.getWidth(); x++) {
				setFlag(index, layer.getTile(x, y) != -1);
				index++;
			}
		}
	}
	
	private void setFlag(int index, boolean flag) {
		final int byteIndex = index / FLAGS_BY_BYTE;
		final int flagValue = 1 << index % FLAGS_BY_BYTE;
		if(flag) {
			mask[byteIndex] |= flagValue;
		}  else {
			mask[byteIndex] = (byte) (mask[byteIndex] & ~flagValue);
		}
	}
	
	public byte[] getMask() {
		return mask.clone();
	}
}
