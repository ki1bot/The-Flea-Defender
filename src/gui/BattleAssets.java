package gui;

import java.awt.image.BufferedImage;

public class BattleAssets {
    private final SpriteSheet defenderSheet;
    private final SpriteSheet fleaSheet;
    private final BufferedImage forestBackground;

    public BattleAssets(SpriteSheet defenderSheet, SpriteSheet fleaSheet, BufferedImage forestBackground) {
        this.defenderSheet = defenderSheet;
        this.fleaSheet = fleaSheet;
        this.forestBackground = forestBackground;
    }

    public SpriteSheet getDefenderSheet() {
        return defenderSheet;
    }

    public SpriteSheet getFleaSheet() {
        return fleaSheet;
    }

    public BufferedImage getForestBackground() {
        return forestBackground;
    }
}
