package mapletyping.ui;

import mapletyping.model.Theme;
import mapletyping.service.GameStats;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.function.Consumer;

/**
 * 게임 진행 중 우측에 표시되는 정보 패널
 * 플레이어 상태, 점수, HP, EXP, 남은 시간과
 * 단어 입력을 담당한다.
 */
public class InfoPanel extends JPanel {

    // 게임 전체 상태를 관리하는 객체
    private final GameStats stats;

    // 단어 입력 필드
    private JTextField inputField;

    // 입력 완료 시 GameFrame으로 전달하기 위한 콜백
    private Consumer<String> onSubmit;

    // 플레이어 정보 표시 라벨들
    private JLabel nameLabel;
    private JLabel levelLabel;
    private JLabel scoreLabel;
    private JLabel comboLabel;

    // 체력 / 경험치 진행바
    private JProgressBar hpBar;
    private JProgressBar expBar;

    // 체력, 경험치, 시간 텍스트
    private JLabel hpText;
    private JLabel expText;
    private JLabel timeLabel;

    // 캐릭터 이미지
    private Image characterImage;

    // 레벨업 시 EXP 바 깜빡임 효과용 카운터
    private int levelUpFlashTick = 0;

    /**
     * InfoPanel 생성자
     * 게임 상태 객체를 전달받아 UI와 연동한다.
     */
    public InfoPanel(GameStats stats) {
        this.stats = stats;

        // absolute layout 사용 (게임 UI 고정 배치)
        setLayout(null);
        setPreferredSize(new Dimension(300, 720));
        setBackground(new Color(230, 230, 210));

        loadCharacterImage();
        initComponents();
    }

    /**
     * 캐릭터 이미지 로딩
     * 리소스가 없을 경우에도 게임이 정상 동작하도록 예외는 무시한다.
     */
    private void loadCharacterImage() {
        try {
            URL url = getClass().getClassLoader().getResource("images/character.png");
            if (url != null) {
                characterImage = new ImageIcon(url).getImage();
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 정보 패널에 사용되는 모든 UI 컴포넌트 초기화
     */
    private void initComponents() {

        nameLabel = new JLabel("닉네임 : -");
        nameLabel.setFont(FontManager.getMapleFont(18f));
        nameLabel.setBounds(20, 160, 260, 25);
        add(nameLabel);

        levelLabel = new JLabel("Level : 1");
        levelLabel.setFont(FontManager.getMapleFont(18f));
        levelLabel.setBounds(20, 185, 260, 25);
        add(levelLabel);

        scoreLabel = new JLabel("Score : 0");
        scoreLabel.setFont(FontManager.getMapleFont(16f));
        scoreLabel.setBounds(20, 215, 260, 22);
        add(scoreLabel);

        comboLabel = new JLabel("Combo : 0");
        comboLabel.setFont(FontManager.getMapleFont(16f));
        comboLabel.setBounds(20, 240, 260, 22);
        add(comboLabel);

        hpText = new JLabel("HP : 10 / 10");
        hpText.setFont(FontManager.getMapleFont(16f));
        hpText.setBounds(20, 270, 260, 22);
        add(hpText);

        // HP 진행 바
        hpBar = new JProgressBar(0, stats.getMaxHp());
        hpBar.setBounds(20, 295, 260, 16);
        hpBar.setForeground(new Color(220, 70, 70));
        hpBar.setBackground(new Color(80, 80, 80));
        hpBar.setBorderPainted(false);
        add(hpBar);

        expText = new JLabel("EXP : 0%");
        expText.setFont(FontManager.getMapleFont(16f));
        expText.setBounds(20, 320, 260, 22);
        add(expText);

        // EXP 진행 바
        expBar = new JProgressBar(0, 100);
        expBar.setBounds(20, 345, 260, 16);
        expBar.setForeground(new Color(255, 204, 0));
        expBar.setBackground(new Color(80, 80, 80));
        expBar.setBorderPainted(false);
        add(expBar);

        // 남은 시간 표시
        timeLabel = new JLabel("남은 시간 : 90s", SwingConstants.CENTER);
        timeLabel.setFont(FontManager.getMapleFont(22f));
        timeLabel.setForeground(new Color(60, 60, 60));
        add(timeLabel);

        // 단어 입력 필드
        inputField = new JTextField();
        inputField.setFont(FontManager.getMapleFont(18f));
        add(inputField);

        // 엔터 입력 시 단어 제출
        inputField.addActionListener(e -> {
            if (onSubmit == null) return;

            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                onSubmit.accept(text);
                inputField.setText("");
            }
        });
    }

    /**
     * 패널 크기 변경 시 하단 UI 위치 재배치
     */
    @Override
    public void doLayout() {
        super.doLayout();
        int h = getHeight();
        timeLabel.setBounds(20, h - 110, 260, 40);
        inputField.setBounds(20, h - 60, 260, 36);
    }

    /**
     * 단어 입력 시 호출될 콜백 등록
     */
    public void setOnSubmit(Consumer<String> onSubmit) {
        this.onSubmit = onSubmit;
    }

    /**
     * 테마 변경 시 호출 (현재는 배경색 등만 repaint 처리)
     */
    public void setTheme(Theme theme) {
        repaint();
    }

    /**
     * 게임 상태 변화에 따라 UI 정보 갱신
     */
    public void refreshLabels(boolean running, boolean paused) {

        nameLabel.setText("닉네임 : " + stats.getPlayerName());
        levelLabel.setText("Level : " + stats.getLevel());
        scoreLabel.setText("Score : " + stats.getScore());
        comboLabel.setText("Combo : " + stats.getCombo());

        hpText.setText("HP : " + stats.getHp() + " / " + stats.getMaxHp());
        hpBar.setValue(stats.getHp());

        int exp = stats.getExpPercent();
        expBar.setValue(exp);
        expText.setText("EXP : " + exp + "%");

        timeLabel.setText("남은 시간 : " + stats.getSecondsLeft() + "s");

        // 레벨업 발생 시 EXP 바 깜빡임 효과
        if (stats.consumeLevelUpEffectFrame()) {
            levelUpFlashTick = 10;
        }

        if (levelUpFlashTick > 0) {
            expBar.setForeground(Color.WHITE);
            levelUpFlashTick--;
        } else {
            expBar.setForeground(new Color(255, 204, 0));
        }
    }

    /**
     * 캐릭터 이미지를 패널 상단에 출력
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (characterImage != null) {
            g.drawImage(characterImage, 70, 20, 160, 120, this);
        }
    }
}
