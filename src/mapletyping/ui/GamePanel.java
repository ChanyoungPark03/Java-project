package mapletyping.ui;

import mapletyping.model.Theme;
import mapletyping.model.WordSprite;
import mapletyping.service.GameStats;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GamePanel extends JPanel {

    // 게임 전반의 상태(레벨, 정확도, 난이도 등)를 관리하는 객체
    private final GameStats stats;

    // 좌측 하단 상태바에 사용하는 고정 폰트 (테마 변경과 무관)
    private final Font statusFont = FontManager.getMapleFont(32f);

    // 화면에 출력되는 모든 몬스터(단어) 목록
    // 멀티스레드 환경에서 안전하게 접근하기 위해 CopyOnWriteArrayList 사용
    private final List<WordSprite> sprites = new CopyOnWriteArrayList<>();

    // 몬스터 이미지 캐시 (이미 로딩한 이미지는 재사용)
    private final Map<String, Image> monsterCache = new HashMap<>();

    // 몬스터 피격 시 흔들림 효과를 위한 카운터 저장
    private final Map<WordSprite, Integer> hitShakeMap = new HashMap<>();

    // 현재 적용 중인 테마
    private Theme theme = Theme.HENESYS;

    // 현재 테마의 배경 이미지
    private BufferedImage background;

    // 몬스터 기본 크기와 확대 비율
    private static final int BASE_MONSTER_SIZE = 48;
    private static final double MONSTER_SCALE = 1.66;

    public GamePanel(GameStats stats) {
        this.stats = stats;
        setBackground(Color.BLACK);
        setFocusable(true);
    }

    /* =========================
       Theme
       ========================= */

    // 테마 변경 시 배경 이미지 갱신
    public void setTheme(Theme t) {
        if (t == null) return;
        this.theme = t;
        this.background = loadImageOrNull(t.getBackgroundPath());
        repaint();
    }

    public Theme getTheme() {
        return theme;
    }

    /* =========================
       Sprite Control
       ========================= */

    // 모든 몬스터 제거 (테마 전환, 게임 시작 시 사용)
    public void clearSprites() {
        sprites.clear();
        hitShakeMap.clear();
    }

    // 새로운 몬스터 추가
    public void addSprite(WordSprite s) {
        sprites.add(s);
    }

    // 현재 화면에 실제로 보이는 몬스터 개수 계산
    private int getVisibleSpriteCount() {
        int h = getHeight();
        int count = 0;

        for (WordSprite s : sprites) {
            int y = s.getY();
            if (y >= 0 && y <= h) {
                count++;
            }
        }
        return count;
    }

    /* =========================
       Input
       ========================= */

    // 사용자가 입력한 단어와 몬스터 단어를 비교하여 타격 처리
    public boolean tryHit(String typed) {
        for (WordSprite s : sprites) {
            if (!s.isAlive()) continue;

            if (s.getWord().equalsIgnoreCase(typed)) {
                s.damage();
                hitShakeMap.put(s, 6); // 피격 흔들림 프레임 수

                if (!s.isAlive()) {
                    sprites.remove(s);
                    hitShakeMap.remove(s);
                }
                return true;
            }
        }
        return false;
    }

    /* =========================
       Tick
       ========================= */

    // 매 프레임마다 호출되어 몬스터 이동 및 충돌 처리
    public void tick() {
        int h = getHeight();

        // 테마별 몬스터 낙하 속도 배율
        double speedMul = switch (theme) {
            case HENESYS -> 1.3;
            case LUDIBRIUM -> 1.6;
            case LEAFRE -> 2.0;
            default -> 1.0;
        };

        for (WordSprite cur : sprites) {
            cur.fall(speedMul);

            // 화면 하단 도달 시 실패 처리
            if (cur.getY() >= h - 60) {
                sprites.remove(cur);
                hitShakeMap.remove(cur);
                stats.onMiss();
            }
        }

        // 피격 흔들림 카운트 감소
        hitShakeMap.replaceAll((k, v) -> v - 1);
        hitShakeMap.entrySet().removeIf(e -> e.getValue() <= 0);
    }

    /* =========================
       Render
       ========================= */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 배경 출력
        if (background != null) {
            g2.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2.setColor(theme.getFallbackBg());
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // 난이도 표시
        drawDifficulty(g2);

        // 몬스터 출력
        for (WordSprite s : sprites) {
            drawSprite(g2, s);
        }

        // 좌측 하단 상태바 출력
        AffineTransform oldTransform = g2.getTransform();
        Stroke oldStroke = g2.getStroke();

        g2.setTransform(new AffineTransform());
        g2.setStroke(new BasicStroke(1f));
        g2.setFont(statusFont);

        String statusText = String.format(
                "테마: %s | 단어: %d | 정확도: %d%%",
                theme.name(),
                getVisibleSpriteCount(),
                stats.getAccuracyPercent()
        );

        int x = 20;
        int y = getHeight() - 30;

        g2.setColor(Color.BLACK);
        g2.drawString(statusText, x + 2, y + 2);

        g2.setColor(Color.WHITE);
        g2.drawString(statusText, x, y);

        g2.setTransform(oldTransform);
        g2.setStroke(oldStroke);
    }

    /* =========================
       Draw Sprite
       ========================= */

    // 개별 몬스터 렌더링
    private void drawSprite(Graphics2D g2, WordSprite s) {
        int monsterSize = (int) (BASE_MONSTER_SIZE * MONSTER_SCALE);

        int shake = hitShakeMap.getOrDefault(s, 0);
        int shakeOffset = (shake % 2 == 0) ? -4 : 4;

        int x = s.getX() + (shake > 0 ? shakeOffset : 0);
        int y = s.getY();

        int wordBoxHeight = 26;
        int hpBarHeight = 9;

        int boxX = x - 8;
        int boxY = y - wordBoxHeight - 8;
        int boxW = monsterSize + 16;
        int boxH = monsterSize + wordBoxHeight + hpBarHeight + 16;

        // 몬스터 전체 영역 배경
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 16, 16);

        g2.setColor(new Color(255, 255, 255, 120));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 16, 16);

        // 몬스터 이미지 출력
        Image img = loadMonsterImage(s.getMonsterGifPath());
        if (img != null) {
            g2.drawImage(img, x, y, monsterSize, monsterSize, null);
        }

        // 2타 몬스터 강조 테두리
        if (s.getMaxHp() >= 2) {
            g2.setStroke(new BasicStroke(3));
            g2.setColor(new Color(180, 80, 255));
            g2.drawRoundRect(x - 2, y - 2, monsterSize + 4, monsterSize + 4, 16, 16);
        }

        // 단어 박스
        g2.setFont(FontManager.getMapleFont(22f));
        FontMetrics fm = g2.getFontMetrics();

        String word = s.getWord();
        int textW = fm.stringWidth(word);
        int textH = fm.getHeight();

        int wordX = x + (monsterSize - textW) / 2 - 10;
        int wordY = y - textH - 8;

        g2.setColor(new Color(255, 255, 255, 230));
        g2.fillRoundRect(wordX, wordY, textW + 20, textH + 6, 10, 10);

        g2.setColor(Color.BLACK);
        g2.drawRoundRect(wordX, wordY, textW + 20, textH + 6, 10, 10);
        g2.drawString(word, wordX + 10, wordY + fm.getAscent() + 3);

        // HP 바
        int barY = y + monsterSize + 6;

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x, barY, monsterSize, 8);

        int hpW = (int) (monsterSize * (s.getHp() / (double) s.getMaxHp()));
        g2.setColor(Color.RED);
        g2.fillRect(x, barY, hpW, 8);
    }

    /* =========================
       Utils
       ========================= */

    // 몬스터 이미지 로딩 및 캐싱
    private Image loadMonsterImage(String path) {
        if (path == null) return null;

        Image cached = monsterCache.get(path);
        if (cached != null) return cached;

        var url = getClass().getClassLoader().getResource(path);
        if (url == null) return null;

        Image img = new ImageIcon(url).getImage();
        if (img.getWidth(null) <= 0) return null;

        monsterCache.put(path, img);
        return img;
    }

    // 배경 이미지 로딩
    private BufferedImage loadImageOrNull(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) return null;
            return ImageIO.read(is);
        } catch (Exception e) {
            return null;
        }
    }

    // 난이도 표시 출력
    private void drawDifficulty(Graphics2D g2) {
        String text = "<" + stats.getDifficulty() + ">";
        g2.setFont(FontManager.getMapleFont(60f));

        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;

        g2.setColor(Color.BLACK);
        g2.drawString(text, x + 2, 57);
        g2.setColor(Color.WHITE);
        g2.drawString(text, x, 55);
    }

    // 스폰 시 전체 몬스터 영역 높이 계산
    public int getSpriteTotalHeight() {
        int monsterSize = (int) (BASE_MONSTER_SIZE * MONSTER_SCALE);
        int wordBoxHeight = 26;
        int hpBarHeight = 9;
        int padding = 20;
        return monsterSize + wordBoxHeight + hpBarHeight + padding;
    }

    // 상단 스폰 시 겹침 방지를 위한 X 좌표 선택
    public int pickSpawnXAvoidTopOverlap(int panelWidth) {
        int monsterSize = (int) (BASE_MONSTER_SIZE * MONSTER_SCALE);

        int left = 20;
        int right = Math.max(left + 10, panelWidth - monsterSize - 40);

        for (int attempt = 0; attempt < 12; attempt++) {
            int x = left + (int) (Math.random() * (right - left));

            boolean overlap = false;
            for (WordSprite s : sprites) {
                if (s.getY() < 140) {
                    int sx = s.getX();
                    if (Math.abs(sx - x) < monsterSize * 0.8) {
                        overlap = true;
                        break;
                    }
                }
            }

            if (!overlap) return x;
        }

        return (panelWidth - monsterSize) / 2;
    }

    // 현재 관리 중인 전체 몬스터 수 반환
    public int getSpriteCount() {
        return sprites.size();
    }
}
