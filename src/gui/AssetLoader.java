package gui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

public class AssetLoader {
    public BattleAssets loadBattleAssets() {
        BufferedImage defenderImage = makeBackgroundTransparent(loadImage("src/assets/defender.png", "/assets/defender.png"));
        BufferedImage fleaImage = makeBackgroundTransparent(loadImage("src/assets/flea.png", "/assets/flea.png"));
        BufferedImage backgroundImage = loadImage("src/assets/background.png", "/assets/background.png");

        return new BattleAssets(
                new SpriteSheet("DEFENDER", defenderImage, 4),
                new SpriteSheet("FLEA", fleaImage, 4),
                backgroundImage
        );
    }

    private BufferedImage loadImage(String filePath, String resourcePath) {
        try {
            File file = new File(filePath);

            if (file.exists()) {
                return ImageIO.read(file);
            }

            InputStream inputStream = getClass().getResourceAsStream(resourcePath);

            if (inputStream != null) {
                return ImageIO.read(inputStream);
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private BufferedImage makeBackgroundTransparent(BufferedImage source) {
        if (source == null) {
            return null;
        }

        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int pixel = source.getRGB(x, y);

                int alpha = (pixel >> 24) & 255;
                int red = (pixel >> 16) & 255;
                int green = (pixel >> 8) & 255;
                int blue = pixel & 255;

                boolean almostWhite = red > 232 && green > 232 && blue > 232;
                boolean lightGray = red > 212 && green > 212 && blue > 212 && Math.abs(red - green) < 18 && Math.abs(green - blue) < 18;

                if (alpha == 0 || almostWhite || lightGray) {
                    result.setRGB(x, y, 0);
                } else {
                    result.setRGB(x, y, (alpha << 24) | (red << 16) | (green << 8) | blue);
                }
            }
        }

        return result;
    }
}
