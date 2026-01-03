package mapletyping.service;

import java.util.concurrent.atomic.AtomicBoolean;
import mapletyping.model.Difficulty;
import mapletyping.sound.SoundEffectPlayer;

// 게임 전체 상태를 관리하는 클래스
// 점수, 레벨, 경험치, 체력, 정확도 등 게임 진행에 필요한 모든 정보를 담당한다.
// 싱글톤 패턴을 사용하여 게임 전반에서 하나의 상태만 공유하도록 설계되었다.
public class GameStats {

    /* =========================
       Singleton
       ========================= */

    // GameStats는 하나의 인스턴스만 사용된다.
    private static final GameStats instance = new GameStats();

    // 외부에서 상태 객체를 가져올 때 사용하는 메서드
    public static GameStats getInstance() {
        return instance;
    }

    /* =========================
       Region / Theme Stage
       ========================= */

    // 현재 루디브리엄 지역인지 여부
    public boolean isInLudi() {
        return movedToLudi && !movedToLeafre;
    }

    // 현재 리프레 지역인지 여부
    public boolean isInLeafre() {
        return movedToLeafre;
    }

    /* =========================
       Fields
       ========================= */

    // 현재 게임 난이도
    private Difficulty difficulty = Difficulty.EASY;

    // 점수, 콤보, 레벨 정보
    private int score;
    private int combo;
    private int level;

    // 경험치 및 레벨업에 필요한 경험치
    private int exp;
    private int requiredExp;

    // 입력한 단어 수 및 정확히 입력한 단어 수
    private int totalTyped;
    private int correctTyped;

    // 플레이어 이름
    private String playerName;

    // 남은 제한 시간
    private int secondsLeft = 90;

    // 체력 정보
    private int hp;
    private static final int MAX_HP = 10;

    // 레벨업 효과를 한 프레임만 표시하기 위한 플래그
    private final AtomicBoolean levelUpEffect = new AtomicBoolean(false);

    // 지역 이동 상태
    private boolean movedToLudi;
    private boolean movedToLeafre;

    // 지역 이동 요청 플래그 (UI / GameFrame에서 소비)
    private final AtomicBoolean requestLudi = new AtomicBoolean(false);
    private final AtomicBoolean requestLeafre = new AtomicBoolean(false);

    // 외부에서 생성하지 못하도록 생성자 제한
    private GameStats() {}

    /* =========================
       Reset
       ========================= */

    // 새로운 게임을 시작할 때 모든 상태를 초기화한다.
    public synchronized void resetForNewRun() {
        score = 0;
        combo = 0;
        level = 1;

        exp = 0;
        requiredExp = calcRequiredExp(level);

        totalTyped = 0;
        correctTyped = 0;
        secondsLeft = 90;

        hp = MAX_HP;

        levelUpEffect.set(false);
        movedToLudi = false;
        movedToLeafre = false;
        requestLudi.set(false);
        requestLeafre.set(false);
    }

    /* =========================
       Game Logic
       ========================= */

    // 단어 입력 성공 시 호출되는 로직
    // 점수, 콤보, 경험치 증가 및 레벨업 처리를 담당한다.
    public synchronized void onHit(String word) {
        totalTyped++;
        correctTyped++;
        combo++;

        // 콤보에 따라 점수 증가
        score += 12 + Math.min(combo, 20);

        // 경험치 계산
        int baseExp = 22;
        int comboBonus = Math.min(combo * 3, 20);

        double difficultyMul = switch (difficulty) {
            case EASY -> 1.25;
            case NORMAL -> 1.5;
            case HARD -> 2.0;
        };

        // 레벨이 올라갈수록 경험치 증가량이 급격히 줄지 않도록 보정
        double levelPenalty = Math.max(
                0.85,
                1.0 - level * 0.025
        );

        int gainedExp = (int) (
                (baseExp + comboBonus)
                        * difficultyMul
                        * levelPenalty
        );

        exp += gainedExp;

        // 레벨업 처리
        while (exp >= requiredExp) {
            exp -= requiredExp;
            level++;
            requiredExp = calcRequiredExp(level);

            levelUpEffect.set(true);

            // 레벨업 효과음 재생
            SoundEffectPlayer.playLevelUp();

            // 특정 레벨 도달 시 지역 변경 여부 확인
            checkRegionUnlock();
        }
    }

    // 단어 입력 실패 시 호출되는 로직
    public synchronized void onMiss() {
        totalTyped++;
        combo = 0;
        score = Math.max(0, score - 2);
        loseHp();
    }

    // 난이도에 따라 체력 감소량을 조절
    private void loseHp() {
        int dmg = switch (difficulty) {
            case EASY -> 1;
            case NORMAL -> 2;
            case HARD -> 3;
        };
        hp = Math.max(0, hp - dmg);
    }

    /* =========================
       EXP Curve
       ========================= */

    // 레벨에 따라 필요한 경험치 계산
    private int calcRequiredExp(int level) {
        if (level <= 2) return 70;
        if (level <= 4) return 110;
        if (level <= 6) return 160;
        return 220 + (level - 6) * 55;
    }

    /* =========================
       Region Unlock
       ========================= */

    // 특정 레벨에 도달하면 지역 이동 요청을 발생시킨다.
    private void checkRegionUnlock() {
        if (!movedToLudi && level >= 4) {
            movedToLudi = true;
            requestLudi.set(true);
        }
        if (!movedToLeafre && level >= 7) {
            movedToLeafre = true;
            requestLeafre.set(true);
        }
    }

    /* =========================
       Getter
       ========================= */

    public int getHp() { return hp; }
    public int getMaxHp() { return MAX_HP; }
    public boolean isDead() { return hp <= 0; }

    public int getScore() { return score; }
    public int getCombo() { return combo; }
    public int getLevel() { return level; }

    public int getExpPercent() {
        return (int) ((exp * 100.0) / requiredExp);
    }

    public int getSecondsLeft() { return secondsLeft; }
    public void setRemainSeconds(int sec) { secondsLeft = sec; }

    // 레벨업 이펙트를 한 번만 소비하도록 처리
    public boolean consumeLevelUpEffectFrame() {
        return levelUpEffect.getAndSet(false);
    }

    public void setPlayerName(String name) {
        playerName = (name == null || name.isBlank()) ? "UNKNOWN" : name;
    }

    public String getPlayerName() {
        return playerName == null ? "UNKNOWN" : playerName;
    }

    public void setDifficulty(Difficulty d) { difficulty = d; }
    public Difficulty getDifficulty() { return difficulty; }

    /* =========================
       Word Hit Rule
       ========================= */

    // 난이도에 따라 2타 몬스터가 등장할 확률을 결정
    public int getRequiredHitsPerWord() {

        int chance = switch (difficulty) {
            case EASY -> 10;
            case NORMAL -> 20;
            case HARD -> 30;
        };

        int roll = (int) (Math.random() * 100);

        return (roll < chance) ? 2 : 1;
    }

    // 지역 이동 요청 소비 메서드
    public boolean consumeLudiRequest() {
        return requestLudi.getAndSet(false);
    }

    public boolean consumeLeafreRequest() {
        return requestLeafre.getAndSet(false);
    }

    // 정확도 계산
    public int getAccuracyPercent() {
        if (totalTyped == 0) return 100;
        return (int) ((correctTyped * 100.0) / totalTyped);
    }
}
