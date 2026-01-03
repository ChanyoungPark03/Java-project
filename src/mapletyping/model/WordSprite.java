package mapletyping.model;

// 화면에 표시되는 하나의 몬스터(단어 객체)를 표현하는 클래스
// 단어, 위치, 이동 속도, 체력 정보를 함께 관리한다.
public class WordSprite {

    // 화면에 출력될 단어
    private final String word;

    // 몬스터 이미지(GIF 또는 PNG) 경로
    private final String monsterGifPath;

    // 화면상의 위치 좌표
    private int x;
    private int y;

    // 기본 이동 속도
    private final int speed;

    // 현재 체력과 최대 체력
    private int hp;
    private final int maxHp;

    // WordSprite 생성자
    // 단어, 몬스터 이미지, 시작 위치, 이동 속도, 필요한 타격 횟수를 설정한다.
    public WordSprite(String word,
                      String monsterGifPath,
                      int x,
                      int y,
                      int speed,
                      int hits) {
        this.word = word;
        this.monsterGifPath = monsterGifPath;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.hp = hits;
        this.maxHp = hits;
    }

    /* =========================
       Position
       ========================= */

    // 현재 X 좌표 반환
    public int getX() {
        return x;
    }

    // 현재 Y 좌표 반환
    public int getY() {
        return y;
    }

    // Y 좌표 강제 변경 (위치 보정용)
    public void setY(int y) {
        this.y = y;
    }

    /* =========================
       Logic (이동 및 상태)
       ========================= */

    // 테마에 따른 속도 배율을 적용하여 아래로 이동
    // 지역별 난이도 체감을 조절하기 위해 사용된다.
    public void fall(double speedMul) {
        y += (int) (speed * speedMul);
    }

    // 기존 코드와의 호환을 위한 기본 이동 방식
    // 별도의 배율이 없을 경우 사용된다.
    public void fall() {
        y += speed;
    }

    // 몬스터가 피격되었을 때 체력 감소
    public void damage() {
        hp--;
    }

    // 몬스터가 아직 살아있는지 여부 반환
    public boolean isAlive() {
        return hp > 0;
    }

    /* =========================
       Getter
       ========================= */

    // 단어 반환
    public String getWord() {
        return word;
    }

    // 몬스터 이미지 경로 반환
    public String getMonsterGifPath() {
        return monsterGifPath;
    }

    // 현재 체력 반환
    public int getHp() {
        return hp;
    }

    // 최대 체력 반환
    public int getMaxHp() {
        return maxHp;
    }
}
