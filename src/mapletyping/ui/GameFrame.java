package mapletyping.ui;

import mapletyping.model.Theme;
import mapletyping.model.WordRepository;
import mapletyping.service.CsvRecordWriter;
import mapletyping.sound.BgmPlayer;
import mapletyping.thread.AnimatorThread;
import mapletyping.thread.CountdownThread;
import mapletyping.thread.SpawnerThread;
import mapletyping.service.GameStats;
import mapletyping.sound.SoundEffectPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GameFrame extends JFrame {

    /* =========================
       Core
       ========================= */

    // 게임 전반의 상태(점수, 레벨, HP 등)를 관리하는 싱글톤 객체
    private final GameStats stats = GameStats.getInstance();

    // 단어 데이터 관리 객체
    private final WordRepository wordRepo = new WordRepository();

    // 실제 게임 화면(몬스터, 배경 등)을 담당
    private final GamePanel gamePanel = new GamePanel(stats);

    // 우측 정보 패널(점수, HP, 입력창 등)
    private final InfoPanel infoPanel = new InfoPanel(stats);

    // 게임 진행을 담당하는 스레드들
    private AnimatorThread animatorThread;
    private SpawnerThread spawnerThread;
    private CountdownThread countdownThread;

    // 게임 실행 / 일시정지 상태
    private boolean running;
    private boolean paused;

    // 배경음 및 효과음 재생기
    private final BgmPlayer bgmPlayer = new BgmPlayer();

    // 현재 재생 중인 BGM 테마
    private Theme currentBgmTheme = null;

    // 마지막으로 적용된 테마 (중복 변경 방지)
    private Theme lastAppliedTheme = null;

    /* =========================
       Screen
       ========================= */

    // 화면 전환을 위한 CardLayout
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel root = new JPanel(cardLayout);

    // 각 화면 패널
    private IntroPanel introPanel;
    private JPanel gameRoot;
    private WordManagerPanel wordManagerPanel;

    /* =========================
       Overlay
       ========================= */

    // 랭킹 화면과 게임 결과 화면 (GlassPane 사용)
    private RankingPanel rankingPanel;
    private GameResultPanel gameResultPanel;

    public GameFrame() {
        super("Maple Typing Adventure");

        // 기본 프레임 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 상단 메뉴와 툴바 구성
        setJMenuBar(buildMenuBar());
        add(buildToolBar(), BorderLayout.NORTH);

        /* ===== GAME 화면 구성 ===== */

        // 게임 화면과 정보 패널을 좌우로 분리
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                gamePanel,
                infoPanel
        );
        split.setDividerLocation(880);

        gameRoot = new JPanel(new BorderLayout());
        gameRoot.add(split, BorderLayout.CENTER);

        /* ===== INTRO / WORD 관리 화면 ===== */

        // 인트로 화면 (시작, 단어관리, 랭킹 버튼)
        introPanel = new IntroPanel(
                this::onClickStartGame,
                this::showWordManager,
                this::showRanking
        );

        // 단어 관리 화면
        wordManagerPanel = new WordManagerPanel(this::showIntro);

        // CardLayout에 화면 등록
        root.add(introPanel, "INTRO");
        root.add(gameRoot, "GAME");
        root.add(wordManagerPanel, "WORDS");

        add(root, BorderLayout.CENTER);

        /* ===== Overlay (GlassPane) ===== */

        rankingPanel = new RankingPanel(this::hideRanking);
        gameResultPanel = new GameResultPanel(this::onResultConfirm);

        rankingPanel.setBounds(0, 0, 1200, 720);
        gameResultPanel.setBounds(0, 0, 1200, 720);

        JPanel glass = new JPanel(null);
        glass.setOpaque(false);
        glass.add(rankingPanel);
        glass.add(gameResultPanel);

        rankingPanel.setVisible(false);
        gameResultPanel.setVisible(false);

        setGlassPane(glass);

        /* ===== 초기 상태 ===== */

        showIntro();

        // 단어 입력 이벤트 연결
        infoPanel.setOnSubmit(this::handleWordSubmit);
    }

    /* =========================
       Screen Control
       ========================= */

    // 인트로 화면 표시
    private void showIntro() {
        bgmPlayer.stopAll();
        bgmPlayer.playLoop("sound/maplestory.wav");
        cardLayout.show(root, "INTRO");
    }

    // 게임 시작 버튼 클릭 시 처리
    private void onClickStartGame() {
        stats.setPlayerName(introPanel.getPlayerName());
        stats.setDifficulty(introPanel.getSelectedDifficulty());

        cardLayout.show(root, "GAME");
        startGameLogic();
    }

    // 단어 관리 화면 표시
    private void showWordManager() {
        bgmPlayer.stopBgm();
        bgmPlayer.playOnce("sound/cash.wav");
        cardLayout.show(root, "WORDS");
    }

    // 랭킹 화면 표시
    private void showRanking() {
        rankingPanel.reload();
        getGlassPane().setVisible(true);
        rankingPanel.setVisible(true);
    }

    // 랭킹 화면 닫기
    private void hideRanking() {
        rankingPanel.setVisible(false);
        getGlassPane().setVisible(false);
    }

    /* =========================
       Game Logic
       ========================= */

    // 게임 시작 시 초기화 및 스레드 실행
    private void startGameLogic() {
        stats.resetForNewRun();
        gamePanel.clearSprites();

        running = true;
        paused = false;

        animatorThread = new AnimatorThread(this, gamePanel, stats);
        spawnerThread = new SpawnerThread(this, gamePanel, stats, wordRepo);
        countdownThread = new CountdownThread(this, stats, 90);

        animatorThread.start();
        spawnerThread.start();
        countdownThread.start();

        // 최초 테마 강제 적용
        currentBgmTheme = Theme.HENESYS;
        lastAppliedTheme = Theme.HENESYS;

        gamePanel.setTheme(Theme.HENESYS);
        infoPanel.setTheme(Theme.HENESYS);
        bgmPlayer.playLoop(Theme.HENESYS.getBgmPath());
    }

    // 모든 게임 스레드 종료 요청
    private void stopGameThreads() {
        if (animatorThread != null) animatorThread.requestStop();
        if (spawnerThread != null) spawnerThread.requestStop();
        if (countdownThread != null) countdownThread.requestStop();
    }

    // 단어 입력 처리
    private void handleWordSubmit(String typed) {
        if (!running || paused) return;

        boolean hit = gamePanel.tryHit(typed.trim());

        if (hit) {
            SoundEffectPlayer.playKill();
            stats.onHit(typed);
        } else {
            SoundEffectPlayer.playWrong();
            stats.onMiss();
        }

        // 레벨에 따른 테마 변경 판단
        Theme next = Theme.byLevel(stats.getLevel());

        if (next != lastAppliedTheme) {
            applyTheme(next);
            lastAppliedTheme = next;
        }

        if (stats.isDead()) {
            showGameResult();
        }
    }

    // 제한 시간 종료 시 처리
    public void onTimeOver() {
        showGameResult();
    }

    /* =========================
       Result
       ========================= */

    // 게임 결과 화면 표시
    private void showGameResult() {
        stopGameThreads();
        running = false;

        gameResultPanel.refresh(stats);
        getGlassPane().setVisible(true);
        gameResultPanel.setVisible(true);

        SoundEffectPlayer.playGameOver();
    }

    // 결과 확인 버튼 클릭 시 처리
    private void onResultConfirm() {
        CsvRecordWriter.write(stats);
        gameResultPanel.setVisible(false);
        getGlassPane().setVisible(false);
        showIntro();
    }

    /* =========================
       Theme Apply
       ========================= */

    // 테마 변경 적용
    private void applyTheme(Theme theme) {
        if (theme == null) return;

        boolean themeChanged = theme != gamePanel.getTheme();

        // 테마가 바뀔 때만 몬스터 제거
        if (themeChanged) {
            gamePanel.clearSprites();
        }

        gamePanel.setTheme(theme);
        infoPanel.setTheme(theme);

        // BGM은 테마 변경 시에만 재생
        if (currentBgmTheme != theme) {

            if (currentBgmTheme != null) {
                SoundEffectPlayer.playTheme();
            }

            bgmPlayer.playLoop(theme.getBgmPath());
            currentBgmTheme = theme;
        }
    }

    /* =========================
       UI Update
       ========================= */

    // UI 갱신 (EDT에서 실행)
    public void updateUiOnce() {
        SwingUtilities.invokeLater(() -> {
            infoPanel.refreshLabels(running, paused);
            gamePanel.repaint();
        });
    }

    public boolean isRunning() { return running; }
    public boolean isPaused() { return paused; }

    /* =========================
       Menu / Toolbar
       ========================= */

    // 상단 메뉴바 생성
    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu game = new JMenu("메뉴");

        game.add(new AbstractAction("시작") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClickStartGame();
            }
        });

        game.add(new AbstractAction("끝내기") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGameResult();
            }
        });

        bar.add(game);
        return bar;
    }

    // 툴바 생성
    private JToolBar buildToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);

        JButton start = new JButton("시작");
        start.addActionListener(e -> onClickStartGame());

        JButton stop = new JButton("끝내기");
        stop.addActionListener(e -> showGameResult());

        tb.add(start);
        tb.add(stop);
        return tb;
    }
}
