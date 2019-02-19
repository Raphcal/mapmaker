package fr.rca.mapmaker.io.shmup;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.common.Streams;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Raphaël Calabro (ddaeke-github@yahoo.fr)
 */
public class BmpWithAlphaBufferedImageDataHandler implements DataHandler<BufferedImage> {

    /**
     * Taille de l'en-tête BMP.
     */
    private static final int BITMAP_HEADER_SIZE = 14;

    /**
     * Taille de l'en-tête DIB et identificateur du format BMP v4.
     */
    private static final int BITMAPV4HEADER = 108;

    /**
     * Indique que la position des couleurs RGBA sera décrite par des champs de
     * bits.
     */
    private static final int BI_BITFIELDS = 3;

    /**
     * Valeur représentant 1 DPI.
     */
    private static final double DPI_1 = 39.3701;

    /**
     * Valeur représentant 72 DPI.
     */
    private static final double DPI_72 = 72 * DPI_1;

    @Override
    public void write(BufferedImage t, OutputStream outputStream) throws IOException {
        final int width = t.getWidth();
        final int height = t.getHeight();
        final int[] BGRAs = new int[width * height];
        t.getRGB(0, 0, width, height, BGRAs, 0, width);
        
        // TODO: Convertir le BGRA en RGBA

        // En-tête du BMP
        // Mot magique.
        Streams.write((byte) 'B', outputStream);
        Streams.write((byte) 'M', outputStream);
        // Taille du fichier.
        Streams.write(BITMAPV4HEADER + BITMAP_HEADER_SIZE + width * height * 4, outputStream);
        // Zone de 4 octets pour identifier l'application.
        Streams.write((byte) 0, outputStream);
        Streams.write((byte) 0, outputStream);
        Streams.write((byte) 0, outputStream);
        Streams.write((byte) 0, outputStream);
        // Premier octet où se situent les pixels.
        Streams.write(BITMAPV4HEADER + BITMAP_HEADER_SIZE, outputStream);

        // En-tête DIB
        // Taille de l'en-tête DIB.
        Streams.write(BITMAPV4HEADER, outputStream);
        // Taille de l'image.
        Streams.write(width, outputStream);
        Streams.write(height, outputStream);
        // Nombre de plans de couleurs.
        Streams.write((short) 1, outputStream);
        // Nombre de bits par pixel.
        Streams.write((short) 32, outputStream);
        // Position des couleurs dans chaque pixel.
        Streams.write(BI_BITFIELDS, outputStream);
        // Poids en octet des pixels.
        Streams.write(width * height * 4, outputStream);
        // DPI de l'image (horizontal, vertical)
        Streams.write((int) Math.ceil(DPI_72), outputStream);
        Streams.write((int) Math.ceil(DPI_72), outputStream);
        // Nombre de couleurs dans la palette.
        Streams.write(0, outputStream);
        // Nombre de couleurs importantes (0 = tout).
        Streams.write(0, outputStream);
        // Masque binaire de la couleur rouge.
        Streams.write(0x000000FF, outputStream);
        // Masque binaire de la couleur bleu.
        Streams.write(0x0000FF00, outputStream);
        // Masque binaire de la couleur verte.
        Streams.write(0x00FF0000, outputStream);
        // Masque binaire de la transparence.
        Streams.write(0xFF000000, outputStream); // D'ici il doit rester 52 octets
        // Colorspace
        Streams.write((byte) ' ', outputStream);
        Streams.write((byte) 'n', outputStream);
        Streams.write((byte) 'i', outputStream);
        Streams.write((byte) 'W', outputStream);
        for (int i = 0; i < 0x24; i++) {
            Streams.write((byte) 0, outputStream);
        }
        // Gamma rouge
        Streams.write(0, outputStream);
        // Gamma vert
        Streams.write(0, outputStream);
        // Gamma bleu
        Streams.write(0, outputStream);
        
        // Pixels
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                final int BGRA = BGRAs[y * width + x];
                final int red = (BGRA & 0x00FF0000) >> 16;
                final int green = (BGRA & 0x0000FF00) >> 8;
                final int blue = BGRA & 0x000000FF;
                final int alpha = (BGRA & 0xFF000000) >> 24;
                
                Streams.write((byte) red, outputStream);
                Streams.write((byte) green, outputStream);
                Streams.write((byte) blue, outputStream);
                Streams.write((byte) alpha, outputStream);
            }
        }
    }

    @Override
    public BufferedImage read(InputStream inputStream) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
