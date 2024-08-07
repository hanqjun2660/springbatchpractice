# Spring Batch Practice

### Dependencies

해당 프로젝트에 사용한 의존성은 아래와 같다.
* Java 17
* Gradle
* spring-boot-starter-batch
* spring-boot-starter-data-jpa
* spring-boot-starter-jdbc
* spring-boot-starter-web
* lombok
* mariadb-java-client
* poi-ooxml:5.3.0

### Implementation Goal

* 메타 테이블 DB / 운영 테이블 DB를 분리
* 테이블 -> 테이블 배치
* 엑셀 -> 테이블 배치
* 테이블 -> 엑셀 배치
* 웹 API -> 테이블 배치

* 스케줄(크론식) 기반 실행
* 웹 핸들 기반 실행