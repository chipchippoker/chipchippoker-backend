# 칩칩포커

<a name="deploy-url"></a>
## 🕊️ Deploy URL

- ✅ https://chipchippoker.shop

---

## 🕊️ API 명세서

- ✅ [API 명세서](https://political-donut-900.notion.site/API-Sheet-66c4d962ae31441c84930ca84902e946?pvs=4)

---


<a name="service-layout"></a>
## 🏠 Service Layout

| <img src="https://github.com/chipchippoker/chipchippoker-backend/assets/76430979/03aca288-0a63-443a-b87f-2f65013c3397" width="250"> | <img src="https://github.com/chipchippoker/chipchippoker-backend/assets/76430979/f2d8ffba-9cea-448f-a85e-28bc287a8938" width="250">  | <img src="https://github.com/chipchippoker/chipchippoker-backend/assets/76430979/78bc00ce-400f-46ad-9798-1bcc81711ed3" width="250"> |
|:----------------------------------:|:----------------------------------------------:|:------------------------------------------------------------:|
|              게임 화면 1               |                    게임 화면 2                     |                           게임 화면 3                            |

|     <img src="https://github.com/chipchippoker/chipchippoker-backend/assets/76430979/d3df9bbe-05b1-4a56-8024-c5248b021905" width="250">      | <img src="https://github.com/chipchippoker/chipchippoker-backend/assets/76430979/d7a97c29-61b8-4192-812b-1f16fcc8996f" width="250"> | <img src="https://github.com/chipchippoker/chipchippoker-backend/assets/76430979/726aee05-d437-442e-afb9-8c5eaab32350" width="250"> |
|:------------------------------------------------------:|:----------------------------------------------------------:|:-----------------------------------------------------------:|
|                         랭킹 화면                          |                            가이드북                            |                           프로필 화면                            |

<br>

---

## 📚 목차

- [팀원 소개](#backend-teams)
- [역할](#role)
- [기술스택](#skills)
- [시스템 아키텍처](#system-architecture)
- [ERD](#erd)
- [Return Object](#return-object)

---

<a name="backend-teams"></a>

## 🤝 팀원 소개

| <a href="https://github.com/Torres-09"><img src="https://github.com/Torres-09.png" width="120"/></a> | <a href="https://github.com/hgfdsa4320"><img src="https://github.com/hgfdsa4320.png" width="120"/></a> | <a href="https://github.com/SuyeonSun"><img src="https://github.com/SuyeonSun.png" width="120"/></a> |
|:------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------:|
|                                  [임세환](https://github.com/Torres-09)                                   |                                 [권순준](https://github.com/hgfdsa4320)                                 |                                [선수연](https://github.com/SuyeonSun)                                 |

---

<a name="role"></a>
## 🙋‍♂️ 역할

### 권순준

- JWT 기반 인증 및 인가 구현
- REST API 구현
    - 회원 REST API 구현
    - 랭킹 REST API 구현
- WebSocket API 구현
    - 친구 신청 WebSocket API 구현
    - 빠른 게임 매칭 WebSocket API 구현

### 선수연

- 게임 방 REST API 구현
- 관전 REST API 구현
- 친구 REST API 구현
- 로그아웃, 탈퇴 REST API 구현
- 매칭 REST API 구현

### 임세환

- WebSocket API를 이용한 실시간 게임 서비스 개발
    - 인디언 포커 알고리즘 구현
- WebSocket 메시지 및 헤더 기반 인증 및 인가 구현
- HTTP API 구현
    - 온/오프라인 정보 API
- Jenkins & Docker 기반의 자동 빌드 및 배포 인프라 구축
- Grafana & Prometheus 이용한 JVM 모니터링 시스템 구축

---

<a name="skills"></a>
## 🛠️ 기술 스택

### language & framework

- Java 17
- SpringBoot 3.2.1
- Spring Data JPA
- Spring WebSocket
- QueryDSL 5.0.0

### database

- MySQL
- MongoDB

### etc

- AWS EC2
- Nginx
- Jenkins
- Docker & docker-compose
- JWT
- OAuth 2.0
- WebRTC & OpenVidu
- Prometheus & Grafana

---


<a name="system-architecture"></a>

## 🌐 시스템 아키텍처
<img src="https://github.com/chipchippoker/chipchippoker-backend/assets/76430979/9b5b8d54-7fd1-42d7-9d2f-553764cc3258" width="700" alt="system_architecture">

<a name="erd"></a>
## 💾 ERD

<img src="https://github.com/chipchippoker/chipchippoker-backend/assets/76430979/c963b096-f23f-4914-bb9c-99cf8a014374" width="700" alt="erd">

---

<a name="return-object"></a>

## 💾 Return Obj

### 성공 응답

```json
{
    "code": "200",
    "message": "OK",
    "data": {
      ...
    }
}
```

```json
{
    "headers": {},
    "body": {
        "code": "MS001",
        "message": "게임방이 생성되었습니다.",
        "data": {
            ...
        }
    },
    "statusCode": "OK",
    "statusCodeValue": 200
}
```

### 에러 응답

```jsx
{
    "headers": {},
    "body": {
        "code": "MN001",
        "message": "찾을 수 없는 방입니다."
    },
    "statusCode": "OK",
    "statusCodeValue": 200
}
```