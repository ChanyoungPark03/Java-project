package mapletyping.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum Theme {

    // 인트로 화면에서 사용하는 테마
    INTRO(
            "images/maplestory_bg.png",
            List.of(),
            "sound/maplestory.wav",
            Color.BLACK,
            Color.BLACK,
            Color.WHITE
    ),

    // 초반 지역 테마 (헤네시스)
    // 기본적인 난이도와 속도로 게임이 진행됨
    HENESYS(
            "images/henesys_bg.png",
            List.of(
                    "images/monsters/h1.png",
                    "images/monsters/h2.png",
                    "images/monsters/h3.png",
                    "images/monsters/h4.png",
                    "images/monsters/h5.png",
                    "images/monsters/h6.png",
                    "images/monsters/h7.png",
                    "images/monsters/h8.png",
                    "images/monsters/h9.png",
                    "images/monsters/h10.png"
            ),
            "sound/henesys.wav",
            new Color(30, 120, 60),
            new Color(60, 220, 120),
            Color.WHITE
    ),

    // 중반 지역 테마 (루디브리엄)
    // 몬스터 수와 속도가 증가하여 난이도가 상승함
    LUDIBRIUM(
            "images/ludi_bg.png",
            List.of(
                    "images/monsters/l1.png",
                    "images/monsters/l2.png",
                    "images/monsters/l3.png",
                    "images/monsters/l4.png",
                    "images/monsters/l5.png",
                    "images/monsters/l6.png",
                    "images/monsters/l7.png",
                    "images/monsters/l8.png",
                    "images/monsters/l9.png",
                    "images/monsters/l10.png"
            ),
            "sound/ludi.wav",
            new Color(60, 40, 140),
            new Color(160, 120, 255),
            Color.WHITE
    ),

    // 후반 지역 테마 (리프레)
    // 가장 빠른 속도와 많은 몬스터가 등장함
    LEAFRE(
            "images/leafre_bg.png",
            List.of(
                    "images/monsters/f1.png",
                    "images/monsters/f2.png",
                    "images/monsters/f3.png",
                    "images/monsters/f4.png",
                    "images/monsters/f5.png",
                    "images/monsters/f6.png",
                    "images/monsters/f7.png",
                    "images/monsters/f9.png",
                    "images/monsters/f10.png"
            ),
            "sound/leafre.wav",
            new Color(120, 70, 20),
            new Color(240, 180, 80),
            Color.WHITE
    );

    /* =========================
       Fields
       ========================= */

    // 배경 이미지 경로
    private final String backgroundPath;

    // 해당 테마에서 사용되는 몬스터 이미지 목록
    private final List<String> monsterPaths;

    // 테마별 배경음악 경로
    private final String bgmPath;

    // 이미지 로딩 실패 시 사용되는 대체 색상
    private final Color fallbackBg;
    private final Color fallbackMonster;

    // 단어 출력에 사용되는 기본 색상
    private final Color wordColor;

    // 몬스터 이미지 중복 출현을 방지하기 위한 셔플 리스트
    private final List<String> shuffledBag = new ArrayList<>();

    Theme(String bg,
          List<String> monsters,
          String bgmPath,
          Color fallbackBg,
          Color fallbackMonster,
          Color wordColor) {

        this.backgroundPath = bg;
        this.monsterPaths = monsters;
        this.bgmPath = bgmPath;
        this.fallbackBg = fallbackBg;
        this.fallbackMonster = fallbackMonster;
        this.wordColor = wordColor;
    }

    /* =========================
       Monster Pick
       ========================= */

    // 몬스터 이미지를 중복 없이 랜덤으로 선택
    // 모든 몬스터가 한 번씩 사용된 후 다시 셔플됨
    public synchronized String getRandomMonsterPath() {
        if (monsterPaths == null || monsterPaths.isEmpty()) return null;

        // 셔플 리스트가 비어 있으면 다시 초기화
        if (shuffledBag.isEmpty()) {
            shuffledBag.clear();
            shuffledBag.addAll(monsterPaths);
            Collections.shuffle(shuffledBag);
        }

        return shuffledBag.remove(0);
    }

    /* =========================
       Getter
       ========================= */

    public String getBackgroundPath() {
        return backgroundPath;
    }

    public String getBgmPath() {
        return bgmPath;
    }

    public Color getFallbackBg() {
        return fallbackBg;
    }

    public Color getFallbackMonster() {
        return fallbackMonster;
    }

    public Color getWordColor() {
        return wordColor;
    }

    /* =========================
       Level → Theme
       ========================= */

    // 플레이어 레벨에 따라 자동으로 테마를 결정
    public static Theme byLevel(int level) {
        if (level >= 7) return LEAFRE;
        if (level >= 4) return LUDIBRIUM;
        return HENESYS;
    }
}
