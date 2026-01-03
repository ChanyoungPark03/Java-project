package mapletyping.sound;

import javax.sound.sampled.*;
import java.net.URL;

// 게임에서 사용되는 모든 효과음을 재생하는 클래스
// 클릭, 정답/오답, 레벨업, 게임 종료 등 짧은 소리를 담당한다.
public class SoundEffectPlayer {

    /* =========================
       공통 재생 메서드
       ========================= */

    // 지정된 경로의 효과음을 한 번 재생한다.
    // 효과음 재생이 끝나면 자동으로 자원을 해제한다.
    private static void play(String path) {
        try {
            URL url = SoundEffectPlayer.class
                    .getClassLoader()
                    .getResource(path);

            if (url == null) {
                System.err.println("사운드 파일 없음: " + path);
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);

            // 재생이 끝났을 때 Clip과 스트림 자원을 정리
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    try {
                        ais.close();
                    } catch (Exception ignored) {
                    }
                }
            });

            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* =========================
       효과음 API
       ========================= */

    // 마우스 버튼 hover 또는 클릭 시 재생되는 효과음
    public static void playMouse() {
        play("sound/mouse.wav");
    }

    // 단어 저장, 단어 관리 화면 진입 시 재생되는 효과음
    public static void playCash() {
        play("sound/cash.wav");
    }

    // 레벨업 발생 시 재생되는 효과음
    public static void playLevelUp() {
        play("sound/level.wav");
    }

    // 몬스터 처치(단어 입력 성공) 시 재생되는 효과음
    public static void playKill() {
        play("sound/kill.wav");
    }

    // 단어 입력 실패(오타) 시 재생되는 효과음
    public static void playWrong() {
        play("sound/wrong.wav");
    }

    // 테마(지역) 변경 시 재생되는 효과음
    public static void playTheme() {
        play("sound/theme.wav");
    }

    // 게임 종료 시 재생되는 효과음
    public static void playGameOver() {
        play("sound/gameover.wav");
    }
}
