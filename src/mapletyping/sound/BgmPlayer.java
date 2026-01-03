package mapletyping.sound;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

// 배경 음악(BGM)과 효과음을 재생하는 클래스
// BGM은 반복 재생, 효과음은 단발 재생 방식으로 분리하여 관리한다.
public class BgmPlayer {

    // 현재 재생 중인 배경 음악 클립
    private Clip bgmClip;

    // 현재 재생 중인 효과음 클립
    private Clip effectClip;

    /* =========================
       BGM (Loop)
       ========================= */

    // 배경 음악을 반복 재생한다.
    // 기존에 재생 중이던 BGM이 있으면 먼저 중지한 후 새로 재생한다.
    public synchronized void playLoop(String path) {
        stopBgm();

        try {
            InputStream is =
                    getClass().getClassLoader().getResourceAsStream(path);

            if (is == null) {
                System.out.println("[BGM] resource not found: " + path);
                return;
            }

            // 스트림 버퍼링을 통해 안정적인 오디오 로딩
            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);

            bgmClip = AudioSystem.getClip();
            bgmClip.open(ais);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* =========================
       Effect Sound (Once)
       ========================= */

    // 효과음을 한 번만 재생한다.
    // 새로운 효과음이 재생될 경우 이전 효과음은 중지된다.
    public synchronized void playOnce(String path) {
        try {
            InputStream is =
                    getClass().getClassLoader().getResourceAsStream(path);

            if (is == null) {
                System.out.println("[EFFECT] resource not found: " + path);
                return;
            }

            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);

            // 이전 효과음이 재생 중이면 정리
            if (effectClip != null) {
                effectClip.stop();
                effectClip.close();
            }

            effectClip = AudioSystem.getClip();
            effectClip.open(ais);
            effectClip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* =========================
       Stop
       ========================= */

    // 현재 재생 중인 BGM을 중지하고 자원을 해제한다.
    public synchronized void stopBgm() {
        if (bgmClip != null) {
            bgmClip.stop();
            bgmClip.close();
            bgmClip = null;
        }
    }

    // BGM과 효과음을 모두 중지하고 자원을 해제한다.
    public synchronized void stopAll() {
        stopBgm();

        if (effectClip != null) {
            effectClip.stop();
            effectClip.close();
            effectClip = null;
        }
    }
}
