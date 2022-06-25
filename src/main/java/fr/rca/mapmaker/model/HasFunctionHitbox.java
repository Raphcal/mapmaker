package fr.rca.mapmaker.model;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public interface HasFunctionHitbox {
	String getFunction(int index);
	void setFunction(int index, String function);

	String getYFunction(int index);
	void setYFunction(int index, String function);
}
