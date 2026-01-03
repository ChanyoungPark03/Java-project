package mapletyping.ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import mapletyping.sound.SoundEffectPlayer;

/**
 * 게임 종료 후 또는 인트로 화면에서 호출되는 랭킹 화면
 * CSV 파일에 저장된 점수 정보를 불러와 상위 랭킹을 시각적으로 출력한다.
 */
public class RankingPanel extends JPanel {

    // 랭킹 데이터가 저장되는 CSV 파일
    private static final File RANK_FILE = new File("ranking.csv");

    // [이름, 점수] 형태로 저장되는 랭킹 레코드 목록
    private final List<String[]> records = new ArrayList<>();

    // "돌아가기" 버튼 클릭 시 실행될 동작
    private final Runnable onBack;

    /**
     * RankingPanel 생성자
     * 뒤로가기 동작은 외부(GameFrame)에서 전달받는다.
     */
    public RankingPanel(Runnable onBack) {
        this.onBack = onBack;

        // GlassPane 위에 띄우기 위해 투명 패널 사용
        setOpaque(false);
        setLayout(null);
        setBounds(0, 0, 1200, 720);

        /* =========================
           뒤로가기 버튼
           ========================= */
        JButton backBtn = new JButton("돌아가기");
        backBtn.setFont(FontManager.getMapleFont(26f));
        backBtn.setBounds(900, 600, 250, 55);

        backBtn.addActionListener(e -> {
            // 버튼 클릭 시 효과음 재생
            SoundEffectPlayer.playMouse();
            // 랭킹 화면 종료 처리
            onBack.run();
        });

        add(backBtn);
    }

    /**
     * 랭킹 데이터를 다시 불러오는 메서드
     * ranking.csv 파일을 읽어 상위 점수 순으로 정렬한다.
     */
    public void reload() {
        records.clear();

        // 랭킹 파일이 없으면 아무 것도 표시하지 않음
        if (!RANK_FILE.exists()) return;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(RANK_FILE),
                        StandardCharsets.UTF_8))) {

            // 첫 줄은 헤더(name,score)이므로 건너뜀
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] tok = line.split(",");
                if (tok.length == 2) {
                    records.add(tok);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 점수를 기준으로 내림차순 정렬
        records.sort(
                Comparator.comparingInt(r -> -parseInt(r[1]))
        );

        repaint();
    }

    /**
     * 문자열 점수를 안전하게 정수로 변환
     */
    private int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 랭킹 화면 렌더링
     * 반투명 박스 안에 TOP 10 랭킹을 출력한다.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        /* =========================
           반투명 랭킹 박스
           ========================= */
        g2.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.88f)
        );
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(120, 80, 960, 480, 40, 40);
        g2.setComposite(AlphaComposite.SrcOver);

        /* =========================
           타이틀
           ========================= */
        g2.setColor(Color.BLACK);
        g2.setFont(FontManager.getMapleFont(46f));
        g2.drawString("[ TOP 10 ]", 460, 140);

        g2.setFont(FontManager.getMapleFont(32f));

        // 좌우 2열 배치 기준 좌표
        int leftX = 240;
        int rightX = 650;
        int startY = 220;
        int gap = 60;

        // 최대 10개 랭킹 출력
        for (int i = 0; i < Math.min(10, records.size()); i++) {
            String[] r = records.get(i);
            String text = (i + 1) + ". " + r[0] + "  " + r[1];

            // 상위 5개는 왼쪽, 나머지는 오른쪽에 출력
            if (i < 5) {
                g2.drawString(text, leftX, startY + i * gap);
            } else {
                g2.drawString(text, rightX, startY + (i - 5) * gap);
            }
        }
    }
}
