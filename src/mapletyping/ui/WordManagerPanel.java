package mapletyping.ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import mapletyping.sound.SoundEffectPlayer;


public class WordManagerPanel extends JPanel {

    private Image background;
    private JTextField wordField;

    public WordManagerPanel(Runnable onBack) {
        setLayout(null);

        // 🎨 인트로 배경 재사용
        URL bgUrl = getClass().getClassLoader().getResource("images/intro_bg.png");
        if (bgUrl != null) {
            background = new ImageIcon(bgUrl).getImage();
        }

        // 🏷 제목
        JLabel title = new JLabel("단어 저장", SwingConstants.CENTER);
        title.setFont(FontManager.getMapleFont(80f));
        title.setForeground(Color.BLACK);
        title.setBounds(0, 60, 1200, 80);
        add(title);

        // ✏ 단어 입력창
        wordField = new JTextField();
        wordField.setFont(FontManager.getMapleFont(30f));
        wordField.setBounds(400, 180, 400, 40);
        add(wordField);

        // 버튼 폰트
        Font btnFont = FontManager.getMapleFont(50f);

        JButton saveBtn = new JButton("저장");
        saveBtn.setFont(FontManager.getMapleFont(40f));
        saveBtn.setBounds(450, 260, 300, 55);
        saveBtn.addActionListener(e -> {
            SoundEffectPlayer.playMouse();;   // 🔊 효과음
            saveWord();                 // 기존 저장 로직
        });
        add(saveBtn);

        JButton backBtn = new JButton("돌아가기");
        backBtn.setFont(FontManager.getMapleFont(40f));
        backBtn.setBounds(450, 330, 300, 55);
        backBtn.addActionListener(e -> {
            SoundEffectPlayer.playMouse();   // 🔊 효과음
            onBack.run();               // Intro로 돌아가기
        });
        add(backBtn);

    }

    private JButton createButton(String text, Font font, int x, int y) {
        JButton b = new JButton(text);
        b.setFont(font);
        b.setBounds(x, y, 500, 70);
        return b;
    }

    private void saveWord() {
        String word = wordField.getText().trim();
        if (word.isEmpty()) {
            JOptionPane.showMessageDialog(this, "단어를 입력하세요!");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream("words.txt", true),
                        StandardCharsets.UTF_8))) {

            bw.write(word);
            bw.newLine();

            JOptionPane.showMessageDialog(this, "저장 완료!");
            wordField.setText("");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "저장 실패");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}


