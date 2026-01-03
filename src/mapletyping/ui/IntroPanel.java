package mapletyping.ui;

import mapletyping.model.Difficulty;
import mapletyping.sound.SoundEffectPlayer;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * 게임 시작 전 최초로 표시되는 인트로 화면
 * 닉네임 입력, 난이도 선택, 게임 시작 및 부가 메뉴를 담당한다.
 */
public class IntroPanel extends JPanel {

    // 인트로 배경 이미지
    private Image background;

    // 플레이어 닉네임 입력 필드
    private JTextField nameField;

    // 난이도 선택 라디오 버튼
    private JRadioButton easyBtn;
    private JRadioButton normalBtn;
    private JRadioButton hardBtn;

    // 화면 상단 타이틀 문구
    private static final String TITLE_TEXT = "Maple Typing Adventure";

    /**
     * IntroPanel 생성자
     * 버튼 클릭 시 실행될 동작을 외부(GameFrame)에서 전달받는다.
     */
    public IntroPanel(
            Runnable onStart,
            Runnable onWordManage,
            Runnable onRanking
    ) {
        // absolute layout 사용 (게임 화면 고정 배치)
        setLayout(null);

        /* =========================
           배경 이미지 로딩
           ========================= */
        URL bgUrl = getClass().getClassLoader().getResource("images/intro_bg.png");
        if (bgUrl != null) {
            background = new ImageIcon(bgUrl).getImage();
        }

        /* =========================
           닉네임 입력 영역
           ========================= */
        JPanel loginBox = new JPanel(null);
        loginBox.setBounds(420, 160, 360, 100);
        loginBox.setBackground(new Color(255, 255, 255, 220));
        loginBox.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        add(loginBox);

        JLabel nameLabel = new JLabel("닉네임");
        nameLabel.setFont(FontManager.getMapleFont(26f));
        nameLabel.setBounds(20, 30, 80, 30);
        loginBox.add(nameLabel);

        nameField = new JTextField();
        nameField.setFont(FontManager.getMapleFont(20f));
        nameField.setBounds(100, 30, 230, 34);
        loginBox.add(nameField);

        // 마우스 호버 시 효과음
        applyHoverSound(nameField);

        /* =========================
           난이도 선택 영역
           ========================= */
        JPanel diffBox = new JPanel(null);
        diffBox.setBounds(420, 280, 360, 90);
        diffBox.setBackground(new Color(255, 255, 255, 220));
        diffBox.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        add(diffBox);

        JLabel diffLabel = new JLabel("난이도");
        diffLabel.setFont(FontManager.getMapleFont(26f));
        diffLabel.setBounds(20, 30, 80, 30);
        diffBox.add(diffLabel);

        easyBtn = new JRadioButton("EASY");
        normalBtn = new JRadioButton("NORMAL");
        hardBtn = new JRadioButton("HARD");

        Font diffFont = FontManager.getMapleFont(18f);
        easyBtn.setFont(diffFont);
        normalBtn.setFont(diffFont);
        hardBtn.setFont(diffFont);

        easyBtn.setBounds(90, 30, 80, 30);
        normalBtn.setBounds(170, 30, 110, 30);
        hardBtn.setBounds(280, 30, 80, 30);

        // 배경 투명 처리
        easyBtn.setOpaque(false);
        normalBtn.setOpaque(false);
        hardBtn.setOpaque(false);

        // 하나의 난이도만 선택 가능하도록 그룹화
        ButtonGroup group = new ButtonGroup();
        group.add(easyBtn);
        group.add(normalBtn);
        group.add(hardBtn);
        easyBtn.setSelected(true); // 기본 난이도 EASY

        diffBox.add(easyBtn);
        diffBox.add(normalBtn);
        diffBox.add(hardBtn);

        applyHoverSound(easyBtn);
        applyHoverSound(normalBtn);
        applyHoverSound(hardBtn);

        /* =========================
           메인 버튼 영역
           ========================= */
        Font btnFont = FontManager.getMapleFont(40f);

        JButton startBtn = createButton("게임 시작", btnFont, 450, 400);
        JButton wordBtn  = createButton("단어 저장", btnFont, 450, 470);
        JButton rankBtn  = createButton("랭킹 보기", btnFont, 450, 540);

        applyHoverSound(startBtn);
        applyHoverSound(wordBtn);
        applyHoverSound(rankBtn);

        // 게임 시작 버튼
        startBtn.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "닉네임을 입력해주세요!",
                        "알림",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            onStart.run();
        });

        // 단어 관리 화면 이동
        wordBtn.addActionListener(e -> onWordManage.run());

        // 랭킹 화면 표시
        rankBtn.addActionListener(e -> onRanking.run());

        add(startBtn);
        add(wordBtn);
        add(rankBtn);
    }

    /* =========================
       Getter
       ========================= */

    /**
     * 입력된 플레이어 닉네임 반환
     */
    public String getPlayerName() {
        return nameField.getText().trim();
    }

    /**
     * 선택된 난이도를 Difficulty enum으로 반환
     */
    public Difficulty getSelectedDifficulty() {
        if (easyBtn.isSelected()) return Difficulty.EASY;
        if (hardBtn.isSelected()) return Difficulty.HARD;
        return Difficulty.NORMAL;
    }

    /* =========================
       Utils
       ========================= */

    /**
     * 공통 버튼 생성 메서드
     */
    private JButton createButton(String text, Font font, int x, int y) {
        JButton b = new JButton(text);
        b.setFont(font);
        b.setBounds(x, y, 300, 60);
        return b;
    }

    /**
     * 컴포넌트에 마우스 호버 효과음을 적용
     */
    private void applyHoverSound(JComponent comp) {
        comp.addMouseListener(new java.awt.event.MouseAdapter() {
            private boolean entered = false;

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!entered) {
                    SoundEffectPlayer.playMouse();
                    entered = true;
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                entered = false;
            }
        });
    }

    /* =========================
       Paint
       ========================= */

    /**
     * 인트로 배경과 게임 타이틀 렌더링
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // 배경 이미지 출력
        if (background != null) {
            g2.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }

        // 타이틀 텍스트 렌더링
        Font titleFont = FontManager.getMapleFont(60f);
        g2.setFont(titleFont);

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(TITLE_TEXT);
        int x = (getWidth() - textWidth) / 2;
        int y = 110;

        // 그림자 효과
        g2.setColor(Color.BLACK);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx != 0 || dy != 0) {
                    g2.drawString(TITLE_TEXT, x + dx, y + dy);
                }
            }
        }

        // 본문 텍스트
        g2.setColor(Color.WHITE);
        g2.drawString(TITLE_TEXT, x, y);
    }
}
