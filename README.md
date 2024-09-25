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
- 해당 뮤지컬의 해당 좌석은 한 번도 판매되지 않았으므로, 예매 가능 => 예매 완료 메시지(SeatSold) Publish 확인
![좌석 상태 확인](https://github.com/user-attachments/assets/873a50a6-080b-406c-83bb-9aff7481247e)
- seatId=1 조회 시 isSold=true로 값이 변경되었음을 확인
### 보상처리(Compensation)
> 이미 예매된 자리를 예매하려고 시도할 경우, 예매할 수 없도록 개발

![예매 요청2](https://github.com/user-attachments/assets/9af324ff-f6de-4cd7-bd6b-f07394f6889a)
- 이미 판매된 musicalId=1 seatId=1로 예매 요청 보냄
![예매 요청 들어옴](https://github.com/user-attachments/assets/1b344a5e-59ff-4738-ab5c-ebe9655e3fa8)
- Kafka를 통해 ReservationPlaced 메시지가 Publish 되었음을 확인
![예매 불가능](https://github.com/user-attachments/assets/c973baa9-3c5b-4a88-a3e1-45e382e9e75a)
- 해당 좌석은 이미 예매된 좌석이므로 예매 불가 메시지(SeatAlreadySold) Publish 확인
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
![cqrs modeling](https://github.com/user-attachments/assets/7c32d147-da78-4c11-bc49-51d3c518881c)
![cqrs](https://github.com/user-attachments/assets/1574a060-62c7-42fa-9706-b887cd82a4c1)
- CQRS MyPage는 ReservationCompleted 메시지를 구독했을 때 생성되게 됨
- 따라서 seatId=1 예매가 성공했을 때 (reservationId=1) MyPage에도 데이터가 생성된 것을 확인할 수 있음

## 클라우드 배포 역량
### CI/CD Pipeline
> github에서 push 할 경우, trigger되어 Jenkins를 통해 배포되도록 CI/CD Pipeline 설정
- azure vm 가상머신 생성 및 접속
  
  ![azurevm](https://github.com/user-attachments/assets/94ff5343-475f-4d8b-8fc4-b9bbac4c8a0c)
- Jenkins Pipeline 생성
  
  ![pipeline](https://github.com/user-attachments/assets/3767197e-8826-4898-bb46-052590e3fa7c)
- reservation 폴더 밑에 Jenkinsfile 생성
  ```
  pipeline {
    agent any

    environment {
        REGISTRY = 'user06.azurecr.io'
        IMAGE_NAME = 'reservation24'
        AKS_CLUSTER = 'user06-aks'
        RESOURCE_GROUP = 'user06-rsrcgrp'
        AKS_NAMESPACE = 'default'
        AZURE_CREDENTIALS_ID = 'Azure-Cred'
        TENANT_ID = 'f46af6a3-e73f-4ab2-a1f7-f33919eda5ac' // Service Principal 등록 후 생성된 ID
    }
 
    stages {
                
        stage('Clone Repository') {
            steps {
                checkout scm
            }
        }
        
        stage('Maven Build') {
            steps {
                dir('reservation') {
                    withMaven(maven: 'Maven') {
                        sh 'mvn package -B -Dmaven.test.skip=true'
                    }
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                dir('reservation') {
                    script {
                    image = docker.build("${REGISTRY}/${IMAGE_NAME}:v${env.BUILD_NUMBER}")
                    }    
                }
            }
        }
        
        stage('Azure Login') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: env.AZURE_CREDENTIALS_ID, usernameVariable: 'AZURE_CLIENT_ID', passwordVariable: 'AZURE_CLIENT_SECRET')]) {
                        sh 'az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET --tenant ${TENANT_ID}'
                    }
                }
            }
        }
        
        stage('Push to ACR') {
            steps {
                script {
                    sh "az acr login --name ${REGISTRY.split('\\.')[0]}"
                    sh "docker push ${REGISTRY}/${IMAGE_NAME}:v${env.BUILD_NUMBER}"
                }
            }
        }
        
        stage('CleanUp Images') {
            steps {
                sh """
                docker rmi ${REGISTRY}/${IMAGE_NAME}:v$BUILD_NUMBER
                """
            }
        }
        
        stage('Deploy to AKS') {
            steps {
                script {
                    sh "az aks get-credentials --resource-group ${RESOURCE_GROUP} --name ${AKS_CLUSTER}"
                    sh """
                    sed 's/latest/v${env.BUILD_ID}/g' reservation/kubernetes/deployment.yaml > output.yaml
                    cat output.yaml
                    kubectl apply -f output.yaml
                    kubectl apply -f reservation/kubernetes/service.yaml
                    rm output.yaml
                    """
                    }
                }
            }
        }
    }
  ```
  - github webhook 설정
    
    ![github webhook](https://github.com/user-attachments/assets/e177abd1-896b-4753-8801-e2f5d7ea92ac)
  - github push => jenkins 빌드 확인
    
    ![github](https://github.com/user-attachments/assets/2a248690-32ab-4e54-9559-0811a4c3ff09)
    ![jenkins](https://github.com/user-attachments/assets/35b370aa-d24f-431b-adcc-7309fe4845dd)
    
## 컨테이너 인프라 설계 및 구성 역량
### 컨테이너 자동 확장 - HPA
> 뮤지컬 예매의 경우 특정 시간에 많은 유저의 예매 요청이 있을 수 있으므로, 해당 경우를 대비하여 Auto Scaling으로 HPA를 적용
- 부하 테스트를 위한 siege pod 생성
  ```
  kubectl apply -f - <<EOF
  apiVersion: v1
  kind: Pod
  metadata:
    name: siege
  spec:
    containers:
    - name: siege
      image: apexacme/siege-nginx
  EOF
  ```
  - 예매 서비스의 deployment.yqml 수정

    ![hpa-deployment](https://github.com/user-attachments/assets/2681c546-dae6-49a6-8465-612de2da6759)

    - deployment.yaml이 수정되었으므로 reservation deploy와 svc 삭제 후 재배포
  - 부하 테스트 전

    ![test before](https://github.com/user-attachments/assets/6e649996-f605-4735-880e-9b1cf78d645f)
  - replica 개수 조정
    ```
    kubectl scale deploy reservation --replicas=3
    ```
  - replicaset 개수 조정 확인

    ![before](https://github.com/user-attachments/assets/45c9c1c9-a9d2-444e-af34-13164cbeaa7d))
    ![after](https://github.com/user-attachments/assets/ba9866b0-8663-4464-b857-125d7dc5d9c1)
    
  - autoscale 적용
    ```
    kubectl autoscale deployment reservation --cpu-percent=50 --min=1 --max=3
    ```
  - 부하 테스트 실행
    ```
    kubectl exec -it siege -- /bin/bash
    siege -c200 -t20S -v 20.249.179.153:8080/reservations
    ```
    - 200명의 유저가 20초 동안 요청을 보냄
  - 결과 확인

    ![결과1](https://github.com/user-attachments/assets/3c02643c-ddcf-45e1-8a6c-2598377254c3)
    ![결과2](https://github.com/user-attachments/assets/0e8c93f7-3cf0-49fd-8c18-6a1e98ed9c6f)
    ![결과3](https://github.com/user-attachments/assets/42e7317d-4505-4080-b0f0-cdddad0eb4eb)
    
### 컨테이너로부터 환경 분리 - Secret
> Dockerhub 이미지를 private으로 설정하여 인증받은 사람만 pull 받을 수 있도록 설정하기
- Dockerhub에 접속하여 reservation 이미지를 private으로 설정하기
  
  ![Dockerhub](https://github.com/user-attachments/assets/1e6b9373-35e3-4a2b-acfc-5ddb623faa1b)
- reservation 서비스 다시 배포하여 status 확인

  ![imagepullerror](https://github.com/user-attachments/assets/5ce817c5-05e7-4d67-b29d-d1a643686b48)
  ![imagepullerror-describe](https://github.com/user-attachments/assets/8a015527-0482-47a6-973e-b4648173ddbd)
  - status가 ErrImagePull이며, `kubectl describe`를 통해 인증 에러가 원인임을 확인
- docker config secret 생성
  ```
  kubectl create secret docker-registry my-docker-cred \
  --docker-server=https://index.docker.io/v1/ \
  --docker-username=gimewn \
  --docker-password='D!Cooncoo@1517' \
  --docker-email=yoonju_1120@naver.com
  ```
  ![secret](https://github.com/user-attachments/assets/c192a82d-efa9-4d42-804b-f6335595b306)

- secret 생성 확인
  ![secret01](https://github.com/user-attachments/assets/740d2720-050c-402b-9c44-27de45ce7d37)
  ![secret02](https://github.com/user-attachments/assets/74563e12-e57e-44c3-9015-42ed20765e21)

- deployment.yaml에 imagePullSecrets 속성을 추가하여 image pull 시에 secret을 활용하게 함
  ![imagePullSecrets](https://github.com/user-attachments/assets/5f80bb59-2485-4bb9-91ea-26d454974cdc)

- 재배포 후 Running status 확인
  ![secret running](https://github.com/user-attachments/assets/a744c83a-712b-4d89-a0c3-4ba752aa1075)
### 클라우드 스토리지 활용 - PVC
> 여러 pod에서 공유할 수 있는 PVC를 설정함
- NFS 생성을 위한 storage class 확인
  ```
  kubectl get storageclass
  ```
  ![storage class](https://github.com/user-attachments/assets/77ef861a-9cce-4016-bf11-efe08aca1f02)

- PVC 생성
  ```
  kubectl apply -f - <<EOF
  apiVersion: v1
  kind: PersistentVolumeClaim
  metadata:
    name: azurefile
  spec:
    accessModes:
    - ReadWriteMany
    storageClassName: azurefile
    resources:
      requests:
        storage: 1Gi
  EOF
  ```
  ![pvc](https://github.com/user-attachments/assets/8809e03a-4957-47d7-bfc4-57077bc7ee49)

- PVC를 적용할 서비스의 `deployment.yaml`에 PVC 설정 추가
  ![deployment-pvc](https://github.com/user-attachments/assets/f2e64231-737c-4c45-a927-e405674cf51b)

- musical pod에 접속하여 테스트용 파일 생성
  ![PVC TEST](https://github.com/user-attachments/assets/f851bc97-fcc4-4a08-b8d6-ed775178f353)
- scale out을 통해 새로운 pod 생성
  ```
  kubectl scale deployment musical --replicas=2
  ```
  ![pvc scale out](https://github.com/user-attachments/assets/35eeeb67-2709-4020-9de2-46945cfa545b)
- 새로 생성된 pod에서도 PVC를 통해 NFS 볼륨 접속 및 테스트 파일 확인 가능
  ![pvc result](https://github.com/user-attachments/assets/d96873ee-3990-4f4c-9f4a-0410dc51785a)
### 무정지배포 - Readiness Probe
> 배포 중에도 사용자가 계속 서비스를 사용할 수 있도록 무정지 배포를 설정함
- Readiness Probe 적용 전
  - siege를 사용해 부하를 준 상태에서 deployment.yaml를 수정해 배포하고 결과 확인
    ![RP 적용 전](https://github.com/user-attachments/assets/432f42d9-b405-40f0-894d-f32fa592292a)
    - Availability가 80.45% => 사용자의 모든 요청에 응답하지 못함
- Readiness Probe 적용
  - deployment.yaml 파일에 Readiness Probe 설정
    ![RP 설정](https://github.com/user-attachments/assets/35f51be3-278f-4e3b-ae99-51d83c36a802)
  - 다시 siege를 사용해 부하를 준 상태에서 배포하고 결과 확인
    ![RP 적용 후](https://github.com/user-attachments/assets/d641b6fe-a401-4609-8cae-c9d8a05b5ffa)
    - Availability가 100% => 사용자의 모든 요청에 응답함
### 서비스 매쉬 응용 - Mesh
> Kubernetes의 기본 Service Mesh인 Istio를 설치하여 활용
- 클러스터 내 Istio 설치
  ```
  export ISTIO_VERSION=1.20.8
  curl -L https://istio.io/downloadIstio | ISTIO_VERSION=$ISTIO_VERSION TARGET_ARCH=x86_64 sh -

  cd istio-$ISTIO_VERSION

  // istioctl 클라이언트를 PATH에 잡아줌
  export PATH=$PWD/bin:$PATH
  ```
- default namespace에서 Pod 내에 Sidecar가 인젝션 하도록 설정
  ```
  kubectl label namespace default istio-injection=enabled
  ```
- default namespace에 속하는 pod 내에 istio-proxy 컨테이너 확인
  ![istio-proxy](https://github.com/user-attachments/assets/dd756e9b-00ea-431b-a3f1-2e5b8fb9c46b)
### 통합 모니터링 - Monitoring
> Prometheus를 활용하여 컨테이너 리소스 활용 현황 모니터링
- siege를 사용하여 요청 발생시킴
  ![siege](https://github.com/user-attachments/assets/056ce557-6fd7-4b87-90e0-63ad0eb8b916)
- 프로메테우스 접속, PromQL 쿼리로 모니터링
  ![Prometheus 모니터링](https://github.com/user-attachments/assets/120b7c54-dd08-4481-9efd-4ba0ac7604fb)
## 트러블슈팅
### Istio Gateway 설정 후 404 Not Found 발생
![gateway 404](https://github.com/user-attachments/assets/445f4637-82c3-49fa-9d78-2c0fdc28b51e)
![kiali01](https://github.com/user-attachments/assets/7e8caeb7-976b-4a75-8fda-c2cbd5fad800)
- kiali를 통해 모니터링, virtualservice.yaml에서 gateway를 인식하지 못하고 있음을 확인

![kiali02](https://github.com/user-attachments/assets/9862e8f2-92e1-422d-9ecf-84975b3d4560)
- virtualservice에서 gateway에 네임스페이스 추가하여 재배포, kiali 모니터링 결과 에러 발생하지 않음

![gateway 200](https://github.com/user-attachments/assets/5bf15c8e-249a-4206-82d2-8bc734904fb7)
- gateway 잘 동작하는 모습 확인

### Jenkins Build 실패
> Jenkinsfile을 reservation 폴더 밑에 두어서 생겼던 문제들
1. Dockerfile을 찾을 수 없음
- 빌드 실패하여 로그 확인 결과 아래와 같이 Dockerfile을 찾지 못함
    ![도커파일 못 찾음](https://github.com/user-attachments/assets/0dc52de5-f76f-4de2-9352-f1bd8c96d515)
- dir steps를 사용하여 디렉토리 경로 변경
    ```
    stage('Docker Build') {
        steps {
            dir('reservation') {
                script {
                image = docker.build("${REGISTRY}/${IMAGE_NAME}:v${env.BUILD_NUMBER}")
                }    
            }
        }
    }
    ```
2. deployment.yaml을 찾을 수 없음
- 빌드 실패하여 로그 확인 결과 아래와 같이 deployment.yaml를 찾지 못함
    ![deployment.yaml 못 찾음](https://github.com/user-attachments/assets/d74b3235-e0d2-4605-8d0e-85b877f15c38)
- reservation/을 붙여 경로를 재지정해줌
  ```
  stage('Deploy to AKS') {
            steps {
                script {
                    sh "az aks get-credentials --resource-group ${RESOURCE_GROUP} --name ${AKS_CLUSTER}"
                    sh """
                    sed 's/latest/v${env.BUILD_ID}/g' reservation/kubernetes/deployment.yaml > output.yaml
                    cat output.yaml
                    kubectl apply -f output.yaml
                    kubectl apply -f reservation/kubernetes/service.yaml
                    rm output.yaml
                    """
                }
            }
        }
    }
  ```
