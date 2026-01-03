package mapletyping.thread;

import mapletyping.ui.GameFrame;
import mapletyping.service.GameStats;

// 제한 시간을 관리하는 스레드
// 1초 단위로 시간을 감소시키고 시간이 끝나면 게임 종료를 알린다.
public class CountdownThread extends Thread {

    // 게임 전체 흐름을 제어하는 프레임
    private final GameFrame frame;

    // 남은 시간 정보를 저장하는 게임 상태 객체
    private final GameStats stats;

    // 현재 남은 시간(초)
    private int seconds;

    // 스레드 종료 제어 플래그
    private volatile boolean stop = false;

    public CountdownThread(GameFrame frame, GameStats stats, int seconds) {
        super("CountdownThread");
        this.frame = frame;
        this.stats = stats;
        this.seconds = seconds;
    }

    // 외부에서 스레드를 안전하게 종료하기 위한 메서드
    public void requestStop() {
        stop = true;
        interrupt();
    }

    @Override
    public void run() {
        // 게임이 실행 중이고 시간이 남아 있는 동안 반복
        while (!stop && seconds > 0 && frame.isRunning()) {
            try {
                Thread.sleep(1000); // 1초 대기
            } catch (InterruptedException e) {
                break;
            }

            seconds--;

            // 남은 시간을 GameStats에 반영
            stats.setRemainSeconds(seconds);

            // UI 갱신 요청
            frame.updateUiOnce();
        }

        // 시간이 끝났고 정상 종료 상태라면 게임 종료 처리
        if (!stop && frame.isRunning()) {
            frame.onTimeOver();
        }
    }
}
