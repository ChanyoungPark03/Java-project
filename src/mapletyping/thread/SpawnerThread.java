package mapletyping.thread;

import mapletyping.model.Theme;
import mapletyping.model.WordRepository;
import mapletyping.model.WordSprite;
import mapletyping.service.GameStats;
import mapletyping.model.Difficulty;
import mapletyping.ui.GameFrame;
import mapletyping.ui.GamePanel;

public class SpawnerThread extends Thread {

    private final GameFrame frame;
    private final GamePanel gamePanel;
    private final GameStats stats;
    private final WordRepository repo;

    private volatile boolean stop = false;

    public SpawnerThread(GameFrame frame,
                         GamePanel gamePanel,
                         GameStats stats,
                         WordRepository repo) {
        super("SpawnerThread");
        this.frame = frame;
        this.gamePanel = gamePanel;
        this.stats = stats;
        this.repo = repo;
    }

    public void requestStop() {
        stop = true;
        interrupt();
    }

    private int getMaxWordsByRegion() {
        if (stats.isInLeafre()) return 11;
        if (stats.isInLudi()) return 8;
        return 5;
    }

    @Override
    public void run() {

        if (!frame.isPaused() && frame.isRunning()) {
            spawn();
        }

        while (!stop && frame.isRunning()) {

            if (!frame.isPaused()) {
                spawn();
            }

            try {
                Thread.sleep(spawnDelayByDifficulty());
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void spawn() {

        if (gamePanel.getSpriteCount() >= getMaxWordsByRegion()) return;

        String word = repo.randomWord();
        if (word == null) return;

        Theme theme = gamePanel.getTheme();
        Difficulty diff = stats.getDifficulty();

        int baseSpeed = switch (diff) {
            case EASY -> 1;
            case NORMAL -> 2;
            case HARD -> 2;
        };

        int speed = Math.min(4, baseSpeed + stats.getLevel() / 12);
        int hits = stats.getRequiredHitsPerWord();

        int monsterSize = (int) (48 * 1.66);
        int wordBoxHeight = 26;
        int hpBarHeight = 9;
        int totalHeight = monsterSize + wordBoxHeight + hpBarHeight + 20;
        int y = -totalHeight;

        int panelWidth = Math.max(300, gamePanel.getWidth());
        int x = gamePanel.pickSpawnXAvoidTopOverlap(panelWidth);

        gamePanel.addSprite(new WordSprite(
                word,
                theme.getRandomMonsterPath(),
                x,
                y,
                speed,
                hits
        ));
    }

    private int spawnDelayByDifficulty() {
        Difficulty diff = stats.getDifficulty();
        int level = stats.getLevel();

        int baseDelay = switch (diff) {
            case EASY -> 1900;
            case NORMAL -> 1400;
            case HARD -> 1000;
        };

        int delay = baseDelay - level * 35;
        return Math.max(500, delay);
    }
}
