package fr.rca.mapmaker.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Buffer auto-extensible.
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
public class Buffer implements Appendable {

    /**
     * Tableau d'octets.
     */
    private byte[] memory;

    /**
     * Indice actuel.
     */
    private int index;

    /**
     * Encodage pour convertir des octets en String et vice-versa.
     */
    private Charset charset = StandardCharsets.UTF_8;

    /**
     * Créé un nouveau buffer avec une taille initiale de 1024 octets.
     */
    public Buffer() {
        this(1024);
    }

    /**
     * Créé un nouveau buffer avec la taille initiale donnée.
     *
     * @param initialLength Taille initiale du buffer.
     */
    public Buffer(final int initialLength) {
        memory = new byte[initialLength];
    }

    /**
     * Récupère le tableau de <code>byte</code>s alimenté par ce buffer.
     *
     * @return Le tableau de bytes.
     */
    public byte[] array() {
        final byte[] array = new byte[index];
        System.arraycopy(memory, 0, array, 0, index);
        return array;
    }

    /**
     * Récupère l'octet à l'indice donné.
     *
     * @param index Indice.
     * @return L'octet à l'indice donné.
     */
    public byte get(int index) {
        return memory[index];
    }

    /**
     * Rempli le tableau donné avec les octets correspondants.
     *
     * @param array Tableau à remplir.
     * @param sourceOffset Indice de début des données à récupérer.
     * @param destinationOffset Indice où commencer à écrire dans le tableau de
     * destination.
     * @param length Longueur des données à récupérer.
     */
    public void get(byte[] array, int sourceOffset, int destinationOffset, int length) {
        System.arraycopy(memory, sourceOffset, array, destinationOffset, length);
    }

    /**
     * Renvoi la taille du buffer.
     *
     * @return La taille du buffer.
     */
    public int length() {
        return index;
    }

    /**
     * Indique si le buffer est vide.
     *
     * @return <code>true</code> si le buffer est vide, <code>false</code>
     * sinon.
     */
    public boolean isEmpty() {
        return index == 0;
    }

    /**
     * Vide le contenu du buffer mais conserve la mémoire allouée.
     */
    public void clear() {
        index = 0;
    }

    /**
     * Supprime le nombre d'octets donné au début du buffer.
     *
     * @param bytes Nombre d'octets à supprimer.
     */
    public void removeFirsts(int bytes) {
        if (bytes == 0) {
            return;
        } else if (bytes > index) {
            throw new IndexOutOfBoundsException(bytes + " bytes to remove but only " + index + " available");
        }
        final byte[] copy = new byte[memory.length];
        System.arraycopy(memory, bytes, copy, 0, index - bytes);
        index -= bytes;
        memory = copy;
    }

    /**
     * Supprime le nombre d'octets donné à la fin du buffer.
     *
     * @param bytes Nombre d'octets à supprimer.
     */
    public void removeLasts(int bytes) {
        if (bytes == 0) {
            return;
        } else if (bytes > index) {
            throw new IndexOutOfBoundsException(bytes + " bytes to remove but only " + index + " available");
        }
        index -= bytes;
    }

    /**
     * Récupère l'encodage utilisé pour lire les <code>String</code>.
     *
     * @return L'encodage utilisé.
     */
    public Charset getCharset() {
        return charset;
    }
    
    /**
     * Défini l'encodage utilisé pour ajouter une <code>String</code> au buffer.
     *
     * @param charset Encodage à utiliser.
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * Ajoute un octet au buffer.
     *
     * @param value Octet à ajouter.
     * @return L'instance du buffer.
     */
    public Buffer append(final int value) {
        return append((byte) value);
    }

    /**
     * Ajoute un octet au buffer.
     *
     * @param value Octet à ajouter.
     * @return L'instance du buffer.
     */
    public Buffer append(final byte value) {
        memory[index++] = value;

        if (index == memory.length) {
            grow();
        }

        return this;
    }

    /**
     * Ajoute un tableau d'octets au buffer.
     *
     * @param values Valeurs à ajouter.
     * @return Le buffer.
     */
    public Buffer append(final byte[] values) {
        return append(values, 0, values.length);
    }

    /**
     * Ajoute un tableau d'octets au buffer.
     *
     * @param values Valeurs à ajouter.
     * @param offset Indice où commencer à lire le tableau <code>values</code>.
     * @param length Nombre d'octets à lire.
     * @return Le buffer.
     */
    public Buffer append(final byte[] values, final int offset, final int length) {
        if (available() <= length) {
            grow(Math.max(index + length * 2, memory.length * 2));
        }
        System.arraycopy(values, offset, memory, index, length);
        index += length;

        return this;
    }

    /**
     * Ajoute un tableau d'octets au buffer.
     *
     * @param values Valeurs à ajouter.
     * @return Le buffer.
     */
    public Buffer append(final ByteBuffer values) {
        final int length = values.limit();
        if (available() <= length) {
            grow(Math.max(index + length * 2, memory.length * 2));
        }
        values.get(memory, index, length);
        index += length;

        return this;
    }

    /**
     * Ajoute le contenu du flux donné au buffer.
     *
     * @param inputStream Flux à lire.
     * @return Le buffer.
     * @throws IOException En cas d'erreur pendant la lecture du flux donné.
     */
    public Buffer append(final InputStream inputStream) throws IOException {
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        final byte[] bytes = new byte[4096];
        int read = bufferedInputStream.read(bytes, 0, bytes.length);
        while (read != -1) {
            append(bytes, 0, read);
            read = bufferedInputStream.read(bytes, 0, bytes.length);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Buffer append(CharSequence csq) {
        append(charset.encode(CharBuffer.wrap(csq)));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Buffer append(CharSequence csq, int start, int end) {
        return append(csq.subSequence(start, end));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Buffer append(char c) {
        append(new String(new char[] {c}).getBytes(charset));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new String(memory, 0, index, charset);
    }

    /**
     * Transforme le contenu du buffer en string à l'aide de l'encodage donné.
     *
     * @param charset Encodage à utiliser.
     * @return Le buffer en string.
     */
    public String toString(Charset charset) {
        return new String(memory, 0, index, charset);
    }

    /**
     * Transforme le contenu du buffer en string à l'aide de l'encodage donné.
     *
     * @param charsetName Nom de l'encodage à utiliser.
     * @return Le buffer en string.
     */
    public String toString(String charsetName) {
        return toString(Charset.forName(charsetName));
    }

    /**
     * Créé un flux d'octets à partir du contenu de ce buffer.
     *
     * @return Flux d'octets.
     */
    public ByteArrayInputStream toInputStream() {
        return new ByteArrayInputStream(memory, 0, index);
    }
    
    /**
     * Créé un flux d'octets écrivant dans ce buffer.
     *
     * @return Flux d'octets en écriture.
     */
    public BufferOutputStream toOutputStream() {
        return new BufferOutputStream(this);
    }

    /**
     * Renvoi la taille disponible dans le buffer.
     *
     * @return Taille disponible dans le buffer.
     */
    private int available() {
        return memory.length - index;
    }

    /**
     * Double la taille du buffer.
     */
    private void grow() {
        grow(memory.length * 2);
    }

    /**
     * Augmente la taille du buffer à la taille donnée.
     *
     * @param newLength Taille du buffer.
     */
    private void grow(int newLength) {
        final byte[] copy = new byte[newLength];
        System.arraycopy(memory, 0, copy, 0, memory.length);
        memory = copy;
    }
}
