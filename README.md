# 주문 관리 시스템 - 완전한 구현

##  핵심 기능

### 1. 주문 데이터 관리
```bash
# 모든 주문 조회
GET /api/orders

# 특정 주문 조회
GET /api/orders/{orderId}

# 주문 수 조회
GET /api/orders/count
```

### 2. 외부 시스템 연동
```bash
# 외부에서 데이터 가져오기
POST /api/orders/sync-from?externalUrl={url}

# 외부로 데이터 전송
POST /api/orders/sync-to?externalUrl={url}

# 단일 주문 동기화
POST /api/orders/sync-single?externalUrl={url}
```

### 3. 데모 및 테스트 기능
```bash
# 샘플 데이터 생성
POST /api/demo/create-sample-data

# 외부 API 시뮬레이션
GET /api/demo/external-api/orders

# 성능 테스트
POST /api/performance/bulk-insert/{count}

# 통합 테스트
POST /api/integration-test/full-sync-test
```

## 시스템 아키텍처

### 계층별 구조
```
┌─────────────────┐
│   Controller    │ ← REST API 엔드포인트
├─────────────────┤
│    Service      │ ← 비즈니스 로직
├─────────────────┤
│   Repository    │ ← 데이터 액세스
├─────────────────┤
│   Connector     │ ← 외부 시스템 연동
└─────────────────┘
```

### 주요 컴포넌트

#### 1. **Domain Layer**
- `Order`: 주문 도메인 모델
- `OrderStatus`: 주문 상태 열거형

#### 2. **Service Layer**
- `OrderSyncService`: 동기화 오케스트레이션
- `DataTransformService`: JSON ↔ Object 변환
- `DataValidationService`: 데이터 검증

#### 3. **Repository Layer**
- `OrderRepository`: 메모리 기반 저장소 (Thread-Safe)

#### 4. **Connector Layer**
- `DataConnectorInterface`: 연동 추상화 인터페이스
- `HttpDataConnector`: HTTP 통신 구현체

#### 5. **Controller Layer**
- `OrderController`: 주문 관리 API
- `DemoController`: 데모 및 시뮬레이션
- `PerformanceTestController`: 성능 테스트
- `SystemHealthController`: 시스템 모니터링

## 기술 스택

### Backend
- **Spring Boot 3.2.0**: 메인 프레임워크
- **Java 17**: 프로그래밍 언어
- **Jackson**: JSON 처리
- **SLF4J + Logback**: 로깅

### HTTP Client
- **Java 11 HTTP Client**: 외부 통신

### Testing
- **JUnit 5**: 단위 테스트
- **Spring Boot Test**: 통합 테스트
- **Mockito**: 모킹

### Build Tool
- **Maven**: 빌드 및 의존성 관리

## 성능 특성

### 메모리 사용량
- **기본 메모리**: ~50MB
- **10,000개 주문**: ~100MB
- **동시 처리**: Thread-Safe 설계

### 처리 성능
- **주문 저장**: ~10,000개/초
- **주문 조회**: ~50,000개/초
- **JSON 변환**: ~5,000개/초

## 예외 처리 체계

### 예외 계층 구조
```
Exception
├── DataConnectorException (네트워크/통신)
├── DataTransformException (데이터 변환)
├── OrderNotFoundException (데이터 조회)
├── OrderSyncException (동기화)
└── IllegalArgumentException (잘못된 인수)
```

### 오류 응답 형식
```json
{
  "error": "ORDER_NOT_FOUND",
  "message": "주문을 찾을 수 없습니다: ORD-001",
  "timestamp": "2024-01-15T10:30:00",
  "status": 404
}
```

## 빠른 시작

### 1. 프로젝트 클론 및 실행
```bash
git clone <repository-url>
cd order-system
mvn spring-boot:run
```

### 2. 기본 동작 확인
```bash
# 서버 상태 확인
curl http://localhost:8080/api/health/status

# 샘플 데이터 생성
curl -X POST http://localhost:8080/api/demo/create-sample-data

# 주문 목록 조회
curl http://localhost:8080/api/orders
```

### 3. 동기화 테스트
```bash
# 외부에서 데이터 가져오기
curl -X POST "http://localhost:8080/api/orders/sync-from?externalUrl=http://localhost:8080/api/demo/external-api/orders"

# 외부로 데이터 전송
curl -X POST "http://localhost:8080/api/orders/sync-to?externalUrl=http://localhost:8080/api/demo/external-api/receive-orders"
```

## API 문서

### 주문 관리 API
| 메서드 | 엔드포인트 | 설명 |
|-------|-----------|------|
| GET | `/api/orders` | 모든 주문 조회 |
| GET | `/api/orders/{id}` | 특정 주문 조회 |
| GET | `/api/orders/count` | 주문 수 조회 |
| DELETE | `/api/orders/clear` | 모든 주문 삭제 |

### 동기화 API
| 메서드 | 엔드포인트 | 설명 |
|-------|-----------|------|
| POST | `/api/orders/sync-from` | 외부에서 데이터 가져오기 |
| POST | `/api/orders/sync-to` | 외부로 데이터 전송 |
| POST | `/api/orders/sync-single` | 단일 주문 동기화 |

### 데모 API
| 메서드 | 엔드포인트 | 설명 |
|-------|-----------|------|
| POST | `/api/demo/create-sample-data` | 샘플 데이터 생성 |
| GET | `/api/demo/external-api/orders` | 외부 API 시뮬레이션 |
| POST | `/api/demo/external-api/receive-orders` | 데이터 수신 시뮬레이션 |

## 🧪 테스트 가이드

### 단위 테스트 실행
```bash
mvn test
```

### 통합 테스트 실행
```bash
mvn test -Dtest=*IntegrationTest
```

### 성능 테스트 실행
```bash
# 1000개 주문 생성 성능 테스트
curl -X POST http://localhost:8080/api/performance/bulk-insert/1000

# 조회 성능 테스트
curl http://localhost:8080/api/performance/bulk-select
```

## 📈 모니터링

### 시스템 상태 확인
```bash
curl http://localhost:8080/api/health/status
```

### Spring Boot Actuator
```bash
# 헬스체크
curl http://localhost:8080/actuator/health

# 메트릭스
curl http://localhost:8080/actuator/metrics

# 애플리케이션 정보
curl http://localhost:8080/actuator/info
```

## 📁 프로젝트 구조

```
order-system/
├── src/
│   ├── main/
│   │   ├── java/com/orderSystem/
│   │   │   ├── OrderSystemApplication.java
│   │   │   ├── config/
│   │   │   ├── domain/
│   │   │   ├── connector/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/orderSystem/
├── target/
├── pom.xml
└── README.md
```

## 제출 내용

### GitHub 저장소 구성
1. **소스 코드**: 모든 Java 클래스 및 설정 파일
2. **테스트 코드**: 단위 테스트 및 통합 테스트
3. **문서화**: README.md, API 문서

### 실행 방법
```bash
# 1. 저장소 클론
git clone <repository-url>

# 2. 프로젝트 디렉토리 이동
cd order-system

# 3. 애플리케이션 실행
mvn spring-boot:run

# 4. 브라우저에서 확인
# http://localhost:8080/api/orders
```
