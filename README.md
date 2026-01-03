🍁 MapleStory Typing Game (Java Swing)

MapleStory × 한성대학교 콜라보 콘셉트의 타이핑 게임
Java Swing과 멀티스레드를 활용하여 구현한 학습형 타이핑 게임 프로젝트

📌 프로젝트 소개

MapleStory Typing Game은
넥슨 메이플스토리의 감성과 세계관을 바탕으로 제작한 Java Swing 기반 타이핑 게임입니다.
단어를 정확히 입력하여 몬스터를 처치하고 점수·레벨·콤보·체력을 관리하며
제한 시간 안에 최대 점수를 획득하는 것이 목표입니다.
본 프로젝트는 객체지향 설계, 멀티스레드, 파일 입출력, GUI 구현을 종합적으로 연습하기 위해 제작되었습니다.

🎮 게임 플레이 방식
화면 상단에서 단어 + 몬스터가 낙하
입력창에 단어를 정확히 입력하면 몬스터 처치
오타 또는 바닥에 도달하면 HP 감소
HP가 0이 되면 게임 종료
제한 시간(90초) 종료 시 자동 게임 종료

✨ 주요 기능

🎯 게임 시스템
점수(Score), 콤보(Combo), 정확도(Accuracy)
레벨(Level) & 경험치(EXP) 시스템
HP(체력) 기반 생명 시스템
레벨에 따른 난이도 자동 상승

🌍 테마 시스템
레벨에 따라 자동으로 게임 테마 변경

테마 설명
HENESYS	초반 테마 (기본 난이도)
LUDIBRIUM	중반 테마 (난이도 상승)
LEAFRE	후반 테마 (높은 난이도)
테마별 배경 이미지
테마별 몬스터 이미지
테마별 BGM 자동 전환

🖼️ UI 구성 (CardLayout)
Intro 화면 : 이름 입력, 게임 시작, 단어 관리, 랭킹 보기
Game 화면 : 실제 게임 플레이
Word Manager : 단어 파일 관리
Ranking 화면 : TOP 10 랭킹 표시 (반투명 UI)

🏆 랭킹 시스템
게임 종료 시 CSV 파일 자동 저장
모든 CSV 기록을 불러와 점수 기준 TOP 10 랭킹 생성
인트로 배경 위에 반투명 랭킹 패널로 표시

CSV 예시:
name,score,accuracy,level,seconds_left
CHAN,1520,98.5,6,12

📂 파일 입출력
단어 파일(words.txt) 로드
게임 결과 CSV 저장
여러 CSV 파일 통합 랭킹 처리

🧵 멀티스레드 구조
Thread	역할
AnimatorThread	화면 갱신
SpawnerThread	단어/몬스터 생성
CountdownThread	제한 시간 관리

🛠️ 사용 기술
Language : Java
GUI : Java Swing
Thread : Java Thread
File I/O : CSV / TXT
Audio : WAV 기반 BGM
Design Pattern : MVC 구조 기반 설계

📁 프로젝트 구조
mapletyping
 ├─ model        # 게임 데이터 & 테마
 ├─ service      # 게임 상태, CSV 처리
 ├─ thread       # 멀티스레드
 ├─ sound        # BGM 플레이어
 └─ ui           # Swing UI (GameFrame, Panels)

🎯 프로젝트 특징
- 객체지향 원칙에 따른 책임 분리
- 멀티스레드를 활용한 실시간 게임 처리
- 파일 입출력을 활용한 데이터 영속성
- 게임성과 학습 요소를 결합한 구조
- 실제 게임 느낌의 테마/사운드/UI 구성
