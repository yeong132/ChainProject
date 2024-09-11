<!-- Heading -->

# CHAIN

> Spring Boot와 Spring MVC 패턴을 기반으로 Spring Security를 활용해 제작한 그룹웨어 프로젝트 CHAIN 입니다.<br>
> 👉 Click [here(링크 준비 중)]()

### ✔️ 개발 동기 및 개요
- 다양한 업무에서 사용할 수 있도록 유동적인 업무 툴을 제작
- 관리자와 사원 페이지로 나누어 체계적인 구현이 목표
- 팀의 효율적인 협업과 생산성 향상을 위한 종합적인 툴 제공
- 인사 관리를 효율적으로 감독하고, 시스템 관리 도구 제공
- 정해진 기간 내에 높은 퀄리티의 웹사이트를 구현하기 위해 부트스트랩 사용

### ✔️ 기간
  `2024.08 ~ 2024.09 (약 2개월)`

***

# 개발 환경
<table>
  <thead>
    <tr>
      <th style="text-align: center;">분류</th>
      <th style="text-align: center;">내용</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>FRAMEWORK</td>
      <td><img src="https://img.shields.io/badge/spring boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" style="border-radius:10px"> <img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" style="border-radius:10px"> </td>
    </tr>
    <tr>
      <td>LIBRARY</td>
      <td><img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" style="border-radius:10px"> <img src="https://img.shields.io/badge/bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white" style="border-radius:10px"></td>
    </tr>
     <tr>
      <td>LANGUAGE</td>
      <td><img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white" style="border-radius:10px"> <img src="https://img.shields.io/badge/css3-1572B6?style=for-the-badge&logo=css3&logoColor=white" style="border-radius:10px"> <img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black" style="border-radius:10px"> <img src="https://img.shields.io/badge/jquery-0769AD?style=for-the-badge&logo=jquery&logoColor=white" style="border-radius:10px"> <img src="https://img.shields.io/badge/java-DE8E2f?style=for-the-badge" style="border-radius:10px"> <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white" style="border-radius:10px"></td>
    </tr>
     <tr>
      <td>VCS</td>
      <td><img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white" style="border-radius:10px"> <img src="https://img.shields.io/badge/sourcetree-0052cc?style=for-the-badge&logo=sourcetree&logoColor=white" style="border-radius:10px"></td>
    </tr>
     <tr>
      <td>IDE</td>
      <td><img src="https://img.shields.io/badge/intellij idea-000000?style=for-the-badge&logo=intellijidea&logoColor=white" style="border-radius:10px"> </td>
    </tr>
  </tbody>
</table>

***
# 페이지 구성
### 🧑‍💼 사원 페이지
![사원1](https://github.com/user-attachments/assets/2aa74abe-c571-48d8-94f7-85cfb44092cc)
![사원2](https://github.com/user-attachments/assets/904bb910-056d-4f49-a68b-8617141ab909)
![사원3](https://github.com/user-attachments/assets/1326bea3-9534-4a19-a7e5-8e5a002d3ee1)

### 🛠️ 관리자 페이지
![관리자](https://github.com/user-attachments/assets/3eaa7e64-d6a3-4460-b702-b31c3a328d85)

***
# DataBase ERD
![chain_ERD](https://github.com/user-attachments/assets/7dda70c3-6811-4de7-9d62-275382e88a5b)

***
# 기능 소개
## 🧑‍💼 사원 페이지
### ✔️ 회원가입
![회원가입](https://github.com/user-attachments/assets/96ac0983-ff70-4b97-a9f3-f61d99fd3589)
### ✔️ 메인페이지
![메인 위젯 설정](https://github.com/user-attachments/assets/5ed10867-d5c9-462b-b1f6-8fea77188e85)
- 상단바와 사이드바, 메신저 아이콘을 고정하여 모든 페이지에서 접근이 가능합니다.
- 위젯 편집에서 드래그 앤 드랍으로 위젯 순서를 변경할 수 있습니다.

### ✔️ 메신저
![메신저](https://github.com/user-attachments/assets/96446aa1-0ffd-4a08-8148-c6d214dee1ae)
- WebSocket를 활용하여 1:1 실시간 메신저를 구현했습니다.
- 메신저에서 조직도 및 채팅방 목록 조회가 가능합니다.
- 채팅방 생성 및 나가기가 가능합니다.
- 채팅 읽음 상태 및 읽지 않은 채팅 수를 확인할 수 있습니다.

### ✔️ 상단바 메뉴

### ✔️ 알림

- 사원별로 맞춤형 알림이 제공되며, 트리거와 이벤트를 사용하여 구현했습니다.
- 상단바 아이콘 : 안 읽은 알림의 개수가 표시되고, 알림이 최신순으로 표시됩니다.
- 알림 전체 페이지
  - 카테고리별 조회 및 읽음 삭제, 전체 삭제가 가능합니다.
  - 각 알림을 클릭하면 해당 페이지로 이동할 수 있습니다.
  - 읽은 알림은 상단바에서 제외되며, 전체 페이지에서도 스타일이 변경되어 표시됩니다.
  - 알림 생성 시간이 지남에 따라 다르게 표시됩니다.
- 시스템 알림 : 관리자는 4개의 카테고리로 시스템 알림을 등록 할 수 있습니다.

### ✔️ 환경설정

- 개인 정보를 수정 할 수 있습니다.
- 카테고리별 알림 설정 변경(on/off)이 가능합니다.

### ✔️ 마이페이지

- 출퇴근 및 근태 기록을 확인할 수 있습니다.

### ✔️ TODO

- TODO 조회, 등록, 삭제가 가능합니다.
- 등록 시, 즐겨찾기 및 카테고리를 설정할 수 있습니다.
- 즐겨찾기/오늘 날짜/완료 여부에 따라 분류하여 TODO를 관리할 수 있습니다.
- 완료된 TODO는 삭제할 수 있습니다.

### ✔️ 프로젝트

- 프로젝트 조회, 등록, 수정, 삭제가 가능합니다.
- 프로젝트 등록
  - 프로젝트 작성자는 로그인한 사원으로 자동 저장됩니다.
  - 프로젝트 참여자 선택 및 기간 지정, 5단계의 진행도를 작성할 수 있습니다.
  - 진행도를 체크하여 차트 페이지에서 프로젝트 진행도를 확인할 수 있습니다.
- 프로젝트 임시 저장 및 즐겨찾기가 가능합니다.
- 즐겨찾기한 프로젝트는 맨 앞에 위치합니다.

### ✔️ 업무

- 업무 문서 조회, 등록, 수정, 삭제가 가능하고, 수정은 작성자만 할 수 있습니다.
- 업무 등록
  - 문서 양식에 따라 '회의록, 업무 보고서, 업무일지' 세 종류의 맞춤형 폼을 작성할 수 있습니다.
  - 작성자는 로그인한 사원으로 자동 저장됩니다.
  - 참여자 선택 및 기간 지정을 할 수 있습니다.
  - 작성 중인 문서는 임시 저장이 가능합니다.

### ✔️ 차트

- 프로젝트 차트
  - 사원별 프로젝트 진행도 통계 조회가 가능합니다.
  - 연도별, 월별 진행도 차트를 조회할 수 있습니다.
- OKR 차트
  - 사원별 목표 진행도 통계 조회가 가능합니다.
  - 목표는 조회, 등록, 수정, 삭제할 수 있습니다.
  - 연도별, 월별, 카테고리별 차트를 조회할 수 있으며, 목표별로 차트를 비교할 수 있습니다.

### ✔️ 전자결재

- 결재 문서 조회, 등록, 수정, 삭제가 가능합니다.
- 상단 카드를 클릭하여 상태별로 조회가 가능합니다.
- 결재 요청 시, 결재자와 참조자를 선택할 수 있고, 결재자는 필수 선택입니다.
- 요청한 문서는 승인되기 전에 철회가 가능합니다.
- 결재자는 요청 받은 문서를 승인 또는 반려할 수 있습니다.
- 승인된 문서는 다음 결재자에게 넘어가고, 최종 결재자까지 승인을 받으면 결재가 완료됩니다.
- 반려한 문서는 반려 사유를 작성할 수 있고, 반려 받은 문서는 재작성이 가능합니다.
- 작성 중인 문서는 임시 저장을 할 수 있고, 임시 저장된 문서는 수정, 요청, 삭제가 가능합니다.

### ✔️ 이메일

- Gmail API를 활용해 웹 메일을 구현했습니다.
- 메일 조회, 송수신, 전달, 답장, 임시저장, 즐겨찾기, 메일함 이동, 삭제가 가능합니다.
- 아이콘 및 상태를 활용해 읽은 메일과 읽지 않은 메일을 한 눈에 파악할 수 있습니다.
- 목록에서 전체 선택 및 선택 해제, 별표 아님만 선택이 가능합니다.
- 메일 작성 시, 여러 명의 수신자를 선택할 수 있고, 메일 주소를 검증하여 올바른 주소인지 확인합니다.
- 즐겨찾기, 중요 메일, 내게 쓴 메일, 임시 보관을 별도로 관리할 수 있습니다.
- 휴지통에서 개별 및 전체 삭제, 복구가 가능합니다.

### ✔️ 공지사항

- 일반 사원은 조회만 가능하고, 권한을 가진 사원만 등록, 수정, 삭제가 가능합니다.
- 공지사항 작성
  - 작성자는 로그인한 사원으로 자동 저장됩니다.
  - 날짜를 지정하여 게시글 상단에 고정할 수 있습니다.

### ✔️ 경조사

- 일반 사원은 조회, 등록만 가능하고, 권한을 가진 사원만 수정, 삭제가 가능합니다.
- 게시글 작성
  - 작성자는 로그인한 사원으로 자동 저장됩니다.
  - '결혼식, 장례식, 기타' 3개의 카테고리 중 선택해 작성할 수 있습니다.
  - 카카오 지도 API를 사용해 장소를 지정할 수 있습니다.

### ✔️ 구내식당

- 일반 사원은 조회만 가능하고, 권한을 가진 사원이 이미지 파일을 업로드하여 식단을 등록, 삭제를 할 수 있습니다.

### ✔️ 고객센터

- 일반 사원
  - 자주 묻는 질문 조회 및 문의글 작성, 조회가 가능합니다.
  - 문의글 작성 시, 작성자는 로그인한 사원으로 자동 저장됩니다.
  - 관리자의 답변이 오기 전까지 문의글을 수정 및 삭제할 수 있습니다.
- 관리자
  - 자주 묻는 질문(FAQ)을 등록, 수정, 삭제할 수 있습니다.
  - 문의글 답변 등록, 수정, 삭제할 수 있습니다.

## 🛠️ 관리자 페이지

### ✔️ 사원 관리

- 사원을 조회, 등록, 수정, 삭제가 가능합니다.
- 사원 등록으로 신규 사원을 추가할 수 있습니다.
- 사원의 부서 및 직급 등 세부 정보 수정이 가능합니다.

### ✔️ 권한 관리

- 부서 등록, 수정, 삭제가 가능합니다.
- 부서별로 사원을 조회하여, 개별 권한을 부여할 수 있습니다.

### ✔️ 근태 관리

- 월 종합 근태를 조회할 수 있습니다.
- 특정 사원 이름을 선택하면 월별로 나뉜 일일 근태를 조회, 수정, 삭제할 수 있습니다.

### ✔️ 연차 관리

- 사원별 연차 현황 조회 및 수정, 삭제가 가능합니다.
- 전자 결재에서 승인된 연차 결재를 조회할 수 있습니다.
