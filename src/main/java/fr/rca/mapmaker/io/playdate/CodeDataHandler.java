package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.io.DataHandler;
import java.io.IOException;
import java.io.InputStream;

/**
 * Classe de base permettant de générer l'en-tête d'un fichier.
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 * @param <T> Type des objets écrits.
 */
public abstract class CodeDataHandler<T> implements DataHandler<T> {
	private String generatedDate = Headers.getGeneratedDate();
	protected PlaydateExportConfiguration configuration;

	@Override
	public T read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}

	public CodeDataHandler<T> withConfiguration(PlaydateExportConfiguration configuration) {
		this.configuration = configuration;
		return this;
	}

	public CodeDataHandler<T> withGeneratedDate(String generatedDate) {
		this.generatedDate = generatedDate;
		return this;
	}

	protected String generateHeader(T t) {
		return "//\n"
				+ "// " + fileNameFor(t) + "\n"
				+ "//\n"
				+ "// Generated by MapMaker on " + generatedDate + ".\n"
				+ "//\n"
				+ "\n";
	}
}
