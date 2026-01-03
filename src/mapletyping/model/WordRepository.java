package mapletyping.model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

// 게임에서 사용되는 단어 목록을 관리하는 클래스
// 단어 로딩, 저장, 랜덤 추출 기능을 담당한다.
public class WordRepository {

    // 여러 스레드에서 동시에 접근할 수 있으므로 스레드 안전한 리스트 사용
    private final List<String> words = new CopyOnWriteArrayList<>();

    // 랜덤 단어 선택을 위한 객체
    private final Random random = new Random();

    // 기본 생성자
    // 리소스 폴더에 포함된 단어 파일을 로드한다.
    public WordRepository() {
        System.out.println("WordRepository 생성됨");

        // 리소스 경로 확인용 테스트
        InputStream test =
                getClass().getClassLoader().getResourceAsStream("words/words.txt");
        System.out.println("words.txt stream = " + test);

        // 기본 단어 파일 로드
        loadFromResource("words/words.txt");

        System.out.println("로드된 단어 수: " + words.size());

        // 단어 파일을 불러오지 못했을 경우를 대비한 기본 단어 설정
        if (words.isEmpty()) {
            words.addAll(List.of(
                    "maple", "typing", "adventure", "level", "combo"
            ));
        }
    }

    // 클래스패스(resource)에 포함된 단어 파일을 읽어오는 메서드
    public void loadFromResource(String path) {
        try (InputStream is =
                     getClass().getClassLoader().getResourceAsStream(path)) {

            // 리소스가 존재하지 않는 경우
            if (is == null) {
                System.out.println("단어 리소스 없음: " + path);
                return;
            }

            // UTF-8 인코딩으로 한 줄씩 단어를 읽어 리스트에 저장
            try (BufferedReader br =
                         new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

                String line;
                while ((line = br.readLine()) != null) {
                    String word = line.trim();
                    if (!word.isEmpty()) {
                        words.add(word);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 외부 파일을 통해 단어 목록을 다시 로드하는 메서드
    // 단어 관리 화면에서 사용된다.
    public void loadFromFile(File file) {
        words.clear();

        try (BufferedReader br =
                     new BufferedReader(new InputStreamReader(
                             new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                String word = line.trim();
                if (!word.isEmpty()) {
                    words.add(word);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 단어 목록에서 무작위로 하나를 반환
    public String randomWord() {
        if (words.isEmpty()) return "empty";
        return words.get(random.nextInt(words.size()));
    }

    // 현재 등록된 단어 수 반환
    public int size() {
        return words.size();
    }
}
