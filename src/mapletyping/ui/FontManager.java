package mapletyping.ui;

import java.awt.*;
import java.io.InputStream;

public class FontManager {

    // 메이플 폰트를 한 번만 로드해서 재사용하기 위한 정적 필드
    private static Font mapleFont;

    /**
     * 메이플 폰트를 지정한 크기로 반환한다.
     * 최초 호출 시 리소스에서 폰트를 로드하고,
     * 이후에는 이미 로드된 폰트를 크기만 변경하여 사용한다.
     */
    public static Font getMapleFont(float size) {
        try {
            // 폰트가 아직 로드되지 않은 경우에만 로드
            if (mapleFont == null) {
                InputStream is = FontManager.class
                        .getClassLoader()
                        .getResourceAsStream("fonts/MaplestoryOTFBold.otf");

                // 폰트 파일을 찾지 못한 경우 기본 폰트 사용
                if (is == null) {
                    return new Font("SansSerif", Font.BOLD, (int) size);
                }

                // 폰트 생성 및 시스템에 등록
                mapleFont = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment ge =
                        GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(mapleFont);
            }

            // 요청된 크기로 파생 폰트 반환
            return mapleFont.deriveFont(size);

        } catch (Exception e) {
            // 예외 발생 시 기본 폰트로 대체
            return new Font("SansSerif", Font.BOLD, (int) size);
        }
    }
}
