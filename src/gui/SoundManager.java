package gui;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

public class SoundManager {
    private final Map<SoundEffect, Clip> clips;
    private boolean enabled;

    public SoundManager() {
        clips = new EnumMap<>(SoundEffect.class);
        enabled = true;
        preloadAll();
    }

    public void play(SoundEffect soundEffect) {
        if (!enabled || soundEffect == null) {
            return;
        }

        Clip clip = clips.get(soundEffect);

        if (clip == null) {
            clip = loadClip(soundEffect);

            if (clip == null) {
                return;
            }

            clips.put(soundEffect, clip);
        }

        if (clip.isRunning()) {
            clip.stop();
        }

        clip.setFramePosition(0);
        clip.start();
    }

    public void stopAll() {
        for (Clip clip : clips.values()) {
            if (clip != null) {
                if (clip.isRunning()) {
                    clip.stop();
                }

                clip.setFramePosition(0);
            }
        }
    }

    public void closeAll() {
        for (Clip clip : clips.values()) {
            if (clip != null) {
                clip.stop();
                clip.close();
            }
        }

        clips.clear();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (!enabled) {
            stopAll();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    private void preloadAll() {
        for (SoundEffect soundEffect : SoundEffect.values()) {
            Clip clip = loadClip(soundEffect);

            if (clip != null) {
                clips.put(soundEffect, clip);
            }
        }
    }

    private Clip loadClip(SoundEffect soundEffect) {
        AudioInputStream audioInputStream = null;

        try {
            File file = new File("src/assets/sounds/" + soundEffect.getFileName());

            if (file.exists()) {
                audioInputStream = AudioSystem.getAudioInputStream(file);
            } else {
                InputStream inputStream = getClass().getResourceAsStream("/assets/sounds/" + soundEffect.getFileName());

                if (inputStream != null) {
                    audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream));
                }
            }

            if (audioInputStream == null) {
                System.out.println("Sound tidak ditemukan: " + soundEffect.getFileName());
                return null;
            }

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            audioInputStream.close();

            return clip;
        } catch (Exception exception) {
            System.out.println("Sound gagal dimuat: " + soundEffect.getFileName());

            try {
                if (audioInputStream != null) {
                    audioInputStream.close();
                }
            } catch (Exception ignored) {
            }

            return null;
        }
    }
}
