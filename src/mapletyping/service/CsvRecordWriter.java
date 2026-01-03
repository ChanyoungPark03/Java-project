package mapletyping.service;

import java.io.*;
import java.nio.charset.StandardCharsets;

// 게임 결과를 CSV 파일로 저장하는 역할의 클래스
// 게임 종료 시 플레이어 이름과 점수를 기록하여 랭킹 데이터로 활용한다.
public class CsvRecordWriter {

    // 랭킹 정보를 저장할 CSV 파일
    private static final File RANK_FILE = new File("ranking.csv");

    /**
     * 게임 종료 시 호출되는 메서드
     * 플레이어 이름과 점수를 CSV 파일에 한 줄로 저장한다.
     */
    public static void write(GameStats stats) {
        try {
            // 파일이 이미 존재하는지 확인
            boolean fileExists = RANK_FILE.exists();

            // 기존 파일에 내용을 추가하기 위해 append 모드로 열기
            try (BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(RANK_FILE, true),
                            StandardCharsets.UTF_8))) {

                // 파일이 처음 생성된 경우 헤더를 한 번만 작성
                if (!fileExists) {
                    bw.write("name,score\n");
                }

                // 플레이어 이름과 점수를 CSV 형식으로 기록
                bw.write(
                        stats.getPlayerName() + "," +
                                stats.getScore() + "\n"
                );
            }

        } catch (IOException e) {
            // 랭킹 저장 실패 시 런타임 예외로 처리
            throw new RuntimeException("랭킹 CSV 저장 실패", e);
        }
    }
}
