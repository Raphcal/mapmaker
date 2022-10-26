package fr.rca.mapmaker.operation;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface Function extends Operator {
	int getNumberOfArguments();

	@Override
	default Priority getPriority() {
		return Priority.FUNCTION;
	}
}
