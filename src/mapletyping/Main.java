package mapletyping;

import javax.swing.SwingUtilities;
import mapletyping.ui.GameFrame;

public class Main {

    public static void main(String[] args) {

        // Swing 기반 UI는 Event Dispatch Thread에서 실행되어야 하므로
        // invokeLater를 사용해 화면 생성과 이벤트 처리를 안전하게 수행한다.
        SwingUtilities.invokeLater(() -> {

            // 게임 전체 화면과 흐름을 관리하는 메인 프레임을 생성한다.
            GameFrame frame = new GameFrame();

            // 생성된 프레임을 화면에 표시하여 프로그램을 시작한다.
            frame.setVisible(true);
        });
    }
}
