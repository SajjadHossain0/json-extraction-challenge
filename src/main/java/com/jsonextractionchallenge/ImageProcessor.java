package com.jsonextractionchallenge;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageProcessor {

    public static String addWhiteBackgroundToImage(String base64Image) throws IOException {
        // Decode the base64 image to a BufferedImage
        byte[] imageBytes = Base64.getDecoder().decode(base64Image.split(",")[1]);
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        BufferedImage originalImage = ImageIO.read(bais);

        // Create a new BufferedImage with a white background
        BufferedImage newImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = newImage.createGraphics();

        // Fill the background with white color
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());

        // Draw the original image on top of the white background
        graphics.drawImage(originalImage, 0, 0, null);
        graphics.dispose();

        // Convert the new image back to base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(newImage, "PNG", baos);
        byte[] newImageBytes = baos.toByteArray();

        return "data:image/png;base64," + Base64.getEncoder().encodeToString(newImageBytes);
    }
}
