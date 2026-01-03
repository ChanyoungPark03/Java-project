package mapletyping.ui;

import javax.swing.*;
import java.awt.*;
import mapletyping.service.GameStats;

public class GameResultPanel extends JPanel {

    // 플레이어 이름, 레벨, 점수를 표시하는 라벨
    private final JLabel nameLabel = new JLabel();
    private final JLabel levelLabel = new JLabel();
    private final JLabel scoreLabel = new JLabel();

    // 결과 정보를 담는 중앙 박스 패널
    private final JPanel box = new JPanel();

    // 결과 확인 버튼 클릭 시 실행할 콜백
    private final Runnable onConfirm;

    // 결과 화면에서 사용하는 폰트들
    private final Font titleFont = FontManager.getMapleFont(32f);
    private final Font textFont = FontManager.getMapleFont(22f);
    private final Font btnFont = FontManager.getMapleFont(20f);

    public GameResultPanel(Runnable onConfirm) {
        this.onConfirm = onConfirm;

        // GlassPane 위에 표시되므로 배경은 투명 처리
        setOpaque(false);
        setLayout(null);

        /* =========================
           중앙 결과 박스 설정
           ========================= */

        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(0, 0, 0, 200));
        box.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        box.setSize(500, 260);

        // 결과 화면 제목
        JLabel title = new JLabel("[ 게임 결과 ]");
        title.setFont(titleFont);
        title.setForeground(Color.RED);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 결과 텍스트 폰트 적용
        nameLabel.setFont(textFont);
        levelLabel.setFont(textFont);
        scoreLabel.setFont(textFont);

        // 텍스트 색상 설정
        nameLabel.setForeground(Color.WHITE);
        levelLabel.setForeground(Color.WHITE);
        scoreLabel.setForeground(Color.WHITE);

        // 중앙 정렬
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 확인 버튼 (결과 확인 후 다음 화면으로 이동)
        JButton confirmBtn = new JButton("확인");
        confirmBtn.setFont(btnFont);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmBtn.addActionListener(e -> {
            setVisible(false);
            onConfirm.run();
        });

        // 컴포넌트 배치
        box.add(Box.createVerticalStrut(15));
        box.add(title);
        box.add(Box.createVerticalStrut(25));
        box.add(nameLabel);
        box.add(Box.createVerticalStrut(10));
        box.add(levelLabel);
        box.add(Box.createVerticalStrut(10));
        box.add(scoreLabel);
        box.add(Box.createVerticalStrut(25));
        box.add(confirmBtn);

        add(box);
    }

    /* =========================
       게임 결과 갱신
       ========================= */

    // 게임 종료 시 GameStats 값을 받아 결과 텍스트를 갱신
    public void refresh(GameStats stats) {
        nameLabel.setText("닉네임 : " + stats.getPlayerName());
        levelLabel.setText("레벨 : " + stats.getLevel());
        scoreLabel.setText("점수 : " + stats.getScore());

        centerBox();
        revalidate();
        repaint();
    }

    /* =========================
       중앙 정렬 처리
       ========================= */

    // 화면 크기에 맞춰 결과 박스를 중앙에 배치
    private void centerBox() {
        int x = (getWidth() - box.getWidth()) / 2;
        int y = (getHeight() - box.getHeight()) / 2;
        box.setLocation(x, y);
    }

    // 패널이 표시될 때마다 중앙 정렬 보장
    @Override
    public void setVisible(boolean flag) {
        super.setVisible(flag);
        if (flag) {
            SwingUtilities.invokeLater(this::centerBox);
        }
    }

    /* =========================
       배경 어둡게 처리
       ========================= */

    // 결과 화면 표시 시 기존 화면을 어둡게 덮는 효과
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
