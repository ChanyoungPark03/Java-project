package mapletyping.ui;

import mapletyping.sound.SoundEffectPlayer;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * UI 컴포넌트에 마우스 호버 효과음을 간단히 적용하기 위한 유틸리티 클래스
 * 버튼, 라벨 등 다양한 Swing 컴포넌트에 공통으로 사용할 수 있다.
 */
public class HoverSoundUtil {

    /**
     * 지정된 컴포넌트에 마우스 호버 사운드 기능을 부여한다.
     * - 마우스가 컴포넌트 영역에 처음 진입할 때만 효과음을 재생한다.
     * - 영역을 벗어나면 다시 재생 가능 상태로 초기화된다.
     */
    public static void apply(JComponent comp) {

        comp.addMouseListener(new MouseAdapter() {

            // 마우스가 이미 들어와 있는 상태인지 여부
            private boolean entered = false;

            @Override
            public void mouseEntered(MouseEvent e) {
                // 중복 재생 방지
                if (!entered) {
                    SoundEffectPlayer.playMouse();
                    entered = true;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // 마우스가 나가면 다시 재생 가능하도록 상태 초기화
                entered = false;
            }
        });
    }
}
