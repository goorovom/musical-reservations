# [Cloud Native] Final Project

## 🎟️ 도메인 주제 : 뮤지컬 예매 서비스
### 도메인 시나리오
- **뮤지컬(Musical)**
    - 사용자가 예매한 좌석은 다른 사람이 예매할 수 없다.
    - 사용자가 예매 취소 시 해당 좌석을 다시 다른 사람이 예매할 수 있다.
    - 관리자는 뮤지컬을 등록, 수정, 삭제할 수 있다.
- **예매(Reservation)**
    - 사용자가 뮤지컬을 조회 및 선택한다.
    - 사용자가 예매한 뮤지컬을 결제한다. (결제 ⇒ 외부API)
    - 사용자가 선택한 좌석을 다른 사용자가 이미 예매했을 경우, 예매할 수 없다.
    - 사용자가 예매를 취소한다.
- **알림(Notification)**
    - 사용자가 예매를 완료했을 때, 예매 완료 알림을 보낸다.
    - 사용자가 예매를 취소했을 때, 예매 취소 알림을 보낸다.
- **마이페이지(MyPage)**
    - 사용자가 예매한 뮤지컬 목록이 표시된다.

## 클라우드 아키텍처 설계
![아키텍처 설계도](https://github.com/user-attachments/assets/bdcad687-2a8c-484f-8a04-db411d23f943)

## Data Modeling/서비스 분리/설계 역량
### 도메인 분석 - 이벤트 스토밍
![이벤트 스토밍](https://github.com/user-attachments/assets/4f1fe146-bea5-48b2-a38c-e1f47eb79659)
- 도출된 이벤트와 커멘트를 어그리게이트로 묶고, 이를 각각 BC로 맵핑함
- 총 4개의 BC 도출
  - Reservation (예매)
  - Musical (뮤지컬, 좌석)
  - Notification (알림)
  - MyPage (마이페이지)

## MSA 개발 또는 개발관리 역량
### 분산트랜잭션(SAGA)
> 메세지 채널인 Kafaka를 통해 이벤트를 Pub/Sub하도록 개발
![예매 요청](https://github.com/user-attachments/assets/cc028386-88c4-4700-b508-00b1520958db)
- musicalId=1 seatId=1로 예매 요청 보냄
![예매 요청 들어옴](https://github.com/user-attachments/assets/5affb9fc-ac32-427d-8138-7a8fd82e85ce)
- Kafka를 통해 ReservationPlaced 메시지가 Publish 되었음을 확인
![예매 완료](https://github.com/user-attachments/assets/a7d26634-1f7e-4fa8-b77b-d075daefdd6f)
- 해당 뮤지컬의 해당 좌석은 한 번도 판매되지 않았으므로, 예매 가능 => 예매 완료 메시지 Publish 확인
![좌석 상태 확인](https://github.com/user-attachments/assets/873a50a6-080b-406c-83bb-9aff7481247e)
- seatId=1 조회 시 isSold=true로 값이 변경되었음을 확인
### 보상처리(Compensation)
> 이미 예매된 자리를 예매하려고 시도할 경우, 예매할 수 없도록 개발
![예매 요청2](https://github.com/user-attachments/assets/9af324ff-f6de-4cd7-bd6b-f07394f6889a)
- 이미 판매된 musicalId=1 seatId=1로 예매 요청 보냄
![예매 요청 들어옴](https://github.com/user-attachments/assets/1b344a5e-59ff-4738-ab5c-ebe9655e3fa8)
- Kafka를 통해 ReservationPlaced 메시지가 Publish 되었음을 확인
![예매 불가능](https://github.com/user-attachments/assets/c973baa9-3c5b-4a88-a3e1-45e382e9e75a)
- 해당 좌석은 이미 예매된 좌석이므로 예매 불가 메시지 Publish 확인
### 단말진입점(Gateway)
> Istio를 활용하여 Gateway 설정
- 라우팅을 위한 `gateway.yaml`과 실제 라우팅 정보가 담긴 `virtualservice.yaml` 생성
```
// gateway.yaml
apiVersion: networking.istio.io/v1beta1
kind: Gateway
metadata:
  name: my-gateway
  namespace: istio-system
spec:
  selector: # use Istio's default controller
    istio: ingressgateway
  servers:
    - port:
        number: 80
        name: http
        protocol: HTTP
      hosts:
        - "*"
```
```
// virtualservice.yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: musical-reservation
  namespace: default
spec:
  hosts:
    - "*"
  gateways:
    - my-gateway
  http:
    - match:
        - uri:
            prefix: /items
        - uri:
            prefix: /seats
      route:
        - destination:
            host: musical
            port:
              number: 8080
    - match:
        - uri:
            prefix: /reservations
      route:
        - destination:
            host: reservation
            port:
              number: 8080
    - match:
        - uri:
            prefix: /myPages
      route:
        - destination:
            host: mypage
            port:
              number: 8080
    - match:
        - uri:
            prefix: /
      route:
        - destination:
            host: frontend
            port:
              number: 8080
```
- gateway는 istio의 인프라 관리와 관련있으므로 istio-system 네임스페이스에, virtualService는 실제 애플리케이션이 위치한 default 네임스페이스에 위치함
- gateway와 virtualservice의 생성 및 연결 확인
![gateway](https://github.com/user-attachments/assets/9b713618-c40d-483e-9129-6b2b2765ab31)
![virtualservice](https://github.com/user-attachments/assets/dbf65979-4234-481f-b3f5-7340928b6c8a)

![ingressgateway](https://github.com/user-attachments/assets/8b3a6729-4754-4091-a0ad-d83b85e870ec)
![gateway 200](https://github.com/user-attachments/assets/5bf15c8e-249a-4206-82d2-8bc734904fb7)
- istio-ingressgateway의 ExternalIp/items로 요청 응답 확인

### 분산 데이터 프로젝션(CQRS)

## 클라우드 배포 역량

## 컨테이너 인프라 설계 및 구성 역량
### 컨테이너 자동 확장 - HPA
### 컨테이너로부터 환경 분리 - Secret
### 클라우드 스토리지 활용 - PVC
### 무정지배포 - Readiness Probe
### 서비스 매쉬 응용 - Mesh
### 통합 모니터링 - Monitoring

## 트러블슈팅
### Istio Gateway 설정 후 404 Not Found 발생
![gateway 404](https://github.com/user-attachments/assets/445f4637-82c3-49fa-9d78-2c0fdc28b51e)
![kiali01](https://github.com/user-attachments/assets/7e8caeb7-976b-4a75-8fda-c2cbd5fad800)
- kiali를 통해 모니터링, virtualservice.yaml에서 gateway를 인식하지 못하고 있음을 확인

![kiali02](https://github.com/user-attachments/assets/9862e8f2-92e1-422d-9ecf-84975b3d4560)
- virtualservice에서 gateway에 네임스페이스 추가하여 재배포, kiali 모니터링 결과 에러 발생하지 않음

![gateway 200](https://github.com/user-attachments/assets/5bf15c8e-249a-4206-82d2-8bc734904fb7)
- gateway 잘 동작하는 모습 확인
