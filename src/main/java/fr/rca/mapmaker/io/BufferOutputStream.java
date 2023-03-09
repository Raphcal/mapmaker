package fr.rca.mapmaker.io;

import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Implémentation d'{@link OutputStream} stockant ses données dans un
 * {@link Buffer}.
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
public class BufferOutputStream extends OutputStream {

    /**
     * Buffer où sont stockées les données.
     */
    private final Buffer buffer;

    /**
     * Créé une nouvelle instance avec un nouveau buffer.
     */
    public BufferOutputStream() {
        this.buffer = new Buffer();
    }

    /**
     * Créé une nouvelle instance avec un nouveau buffer initialisé à la
     * capacité donnée.
     *
     * @param initialCapacity Capacité initiale du buffer.
     */
    public BufferOutputStream(int initialCapacity) {
        this.buffer = new Buffer(initialCapacity);
    }

    /**
     * Créé une nouvelle instance avec le buffer donné.
     *
     * @param buffer Buffer à utiliser.
     */
    public BufferOutputStream(Buffer buffer) {
        this.buffer = buffer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int b) {
        buffer.append(b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] b) {
        buffer.append(b, 0, b.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] b, int off, int len) {
        buffer.append(b, off, len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // Aucune opération
    }

    /**
     * Récupère le buffer utilisé.
     *
     * @return Le buffer utilisé.
     */
    public Buffer getBuffer() {
        return buffer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return buffer.toString();
    }

    /**
     * Transforme le contenu du buffer en string à l'aide de l'encodage donné.
     *
     * @param charset Encodage à utiliser.
     * @return Le buffer en string.
     */
    public String toString(Charset charset) {
        return buffer.toString(charset);
    }

    /**
     * Transforme le contenu du buffer en string à l'aide de l'encodage donné.
     *
     * @param charsetName Nom de l'encodage à utiliser.
     * @return Le buffer en string.
     */
    public String toString(String charsetName) {
        return buffer.toString(charsetName);
    }

}
