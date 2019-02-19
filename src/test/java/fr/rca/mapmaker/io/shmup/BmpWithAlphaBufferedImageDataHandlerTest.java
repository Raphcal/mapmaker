package fr.rca.mapmaker.io.shmup;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.junit.Test;

/**
 *
 * @author Raphaël Calabro (ddaeke-github@yahoo.fr)
 */
public class BmpWithAlphaBufferedImageDataHandlerTest {
    
    /**
     * Test of write method, of class BmpWithAlphaBufferedImageDataHandler.
     */
//     @Test
    public void testWrite() throws Exception {
        System.out.println("write");
        BufferedImage t = createTestImage();
        try (OutputStream outputStream = new FileOutputStream(new File("/Users/raphael/Downloads/toto.bmp"))) {
            BmpWithAlphaBufferedImageDataHandler instance = new BmpWithAlphaBufferedImageDataHandler();
            instance.write(t, outputStream);
        }
        // TODO: Vérifier les données.
    }
    
    private BufferedImage createTestImage() {
        final BufferedImage image = new BufferedImage(4, 2, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = image.createGraphics();
        
        graphics.setColor(Color.BLUE);
        graphics.fillRect(0, 0, 1, 1);
        graphics.setColor(Color.GREEN);
        graphics.fillRect(1, 0, 1, 1);
        graphics.setColor(Color.RED);
        graphics.fillRect(2, 0, 1, 1);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(3, 0, 1, 1);
        
        graphics.setColor(new Color(0, 0, 255, 127));
        graphics.fillRect(0, 1, 1, 1);
        graphics.setColor(new Color(0, 255, 0, 127));
        graphics.fillRect(1, 1, 1, 1);
        graphics.setColor(new Color(255, 0, 0, 127));
        graphics.fillRect(2, 1, 1, 1);
        graphics.setColor(new Color(255, 255, 255, 127));
        graphics.fillRect(3, 1, 1, 1);
		
		graphics.dispose();
        return image;
    }
    
}
