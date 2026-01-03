package mapletyping.thread;

import mapletyping.model.Difficulty;
import mapletyping.ui.GameFrame;
import mapletyping.ui.GamePanel;
import mapletyping.service.GameStats;

// 게임 화면의 실시간 애니메이션을 담당하는 스레드
// 몬스터 이동, 충돌 처리, 화면 갱신을 주기적으로 수행한다.
public class AnimatorThread extends Thread {

    // 게임 전체 상태 제어용 프레임
    private final GameFrame frame;

    // 실제 게임 화면을 담당하는 패널
    private final GamePanel gamePanel;

    // 점수, 레벨, 난이도 등의 게임 상태 정보
    private final GameStats stats;

    // 스레드 종료 여부를 판단하기 위한 플래그
    // 다른 스레드에서 변경될 수 있으므로 volatile로 선언
    private volatile boolean stop = false;

    public AnimatorThread(GameFrame frame, GamePanel gamePanel, GameStats stats) {
        super("AnimatorThread");
        this.frame = frame;
        this.gamePanel = gamePanel;
        this.stats = stats;
    }

    // 외부(GameFrame)에서 스레드를 안전하게 종료하기 위한 메서드
    public void requestStop() {
        stop = true;
        interrupt();
    }

    @Override
    public void run() {

        // 게임이 실행 중이고 종료 요청이 없는 동안 반복
        while (!stop && frame.isRunning()) {

            // 일시정지 상태가 아닐 때만 게임 로직 처리
            if (!frame.isPaused()) {

                // 몬스터 이동 및 충돌 처리
                gamePanel.tick();

                // UI 갱신은 EDT에서 처리되도록 위임
                frame.updateUiOnce();
            }

            try {
                // 난이도와 레벨에 따라 프레임 간격 조절
                Thread.sleep(frameDelayByDifficulty());
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /* =========================
       Difficulty-based FPS
       ========================= */

    /**
     * 난이도와 레벨에 따라 프레임 간격을 계산한다.
     * 값이 작을수록 tick 호출 빈도가 높아져
     * 몬스터가 더 빠르게 내려오는 느낌을 준다.
     */
    private int frameDelayByDifficulty() {
        Difficulty diff = stats.getDifficulty();
        int level = stats.getLevel();

        int baseDelay;

        switch (diff) {
            case EASY -> baseDelay = 20;    // 초보자용, 느린 속도
            case NORMAL -> baseDelay = 16;  // 기본 속도 (약 60fps)
            case HARD -> baseDelay = 13;    // 빠른 속도
            default -> baseDelay = 16;
        }

        // 레벨이 오를수록 프레임 간격을 줄여 난이도 상승
        int delay = baseDelay - level / 5;

        // 최소 간격 제한으로 과도한 속도 증가 방지
        return Math.max(10, delay);
    }
}
