# Water Cooler

**Water Cooler**는 다양한 글로벌 커뮤니티의 이슈를 수집하고, **Google Gemini AI**를 활용해 핵심 내용으로 요약, 이를 클러스터링을 통해 커뮤니티의 이슈로 등록, 이 중 트렌디한 컨텐츠는 `TrendTopic`으로 내세워 각 커뮤니티의 이슈들을 사용자에게 전달하는 것이 목표입니다.

사람들이 정수기(Water Cooler) 앞에서 가볍게 이야기를 나누듯, 세상의 핫한 이슈를 쉽고 빠르게 파악할 수 있도록 돕습니다.

## Tech Stack

### Backend

- **Language**: Kotlin (JDK 21)
- **Framework**: Spring Boot 4.0.1
- **Database**: PostgreSQL 18.1
- **Cache**: Redis 8.4
- **Security**: Spring Security, JWT (jjwt 0.13.0)
- **Crawling**: Jsoup 1.22.1
- **AI**: Google GenAI SDK 1.32.0 (Gemini)

### Infrastructure

- **Docker & Docker Compose**

## Performance Optimization (Dev Log)

### Google GenAI 입력 토큰 최적화

Google Gen Ai 이용한 News 요약 기능 구현 중, 다수의 이미지가 포함된 게시글 처리 시 Input Token의 과다 사용 문제가 발생함.

- **기존 방식**: 본문의 이미지를 다운로드 후, 멀티모달 프롬프트로 직접 업로드.
- **변경 방식**: 이미지를 업로드하지 않고, 본문에 `이미지 경로`만 텍스트 형태로 남겨 처리.
- **결과** : 입력 토큰 수가 획기적으로 줄어듦, 뉴스 요약의 본질을 해치지 않으면서 같은 퀄리티로 비용 효율성 확보.

![Graph](https://drive.google.com/file/d/1Za2X3z_kpwJsziM-vGQEfOVIRbRCW_qP/view?usp=sharing)
