package fr.rca.mapmaker.model.sprite;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Animation {
	private String name;
	private String code;

	public Animation() {
	}

	public Animation(String name, String code) {
		this.name = name;
		this.code = code;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public String getNameForDirection(double direction) {
		return code + '-' + direction;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public static Animation[] getDefaultAnimations() {
		return new Animation[] {
			new Animation("À l'arrêt", "stand"),
			new Animation("Marche", "walk"),
			new Animation("Course", "run"),
			new Animation("Saute", "jump"),
			new Animation("Tombe", "fall"),
			new Animation("Se baisse", "duck"),
			new Animation("Se lève", "raise"),
			new Animation("Attaque", "attack"),
			new Animation("Blessé", "hurt"),
			new Animation("Meurt", "die")
		};
	}
}
