티켓팅 예약을 선착순으로 하기 위해 대기열 토큰을 발급하는 방법을 알게 되었다.
스케줄링을 통해 토큰의 아이디를 기준으로 오름차순으로 일정 개수씩 활성화하는 방식이다.
아이디를 만들 때 오름차순 숫자를 사용하면 보안에 취약하다는 피드백을 받아서
처음에는 고유 식별자인 uuid를 사용하는 것을 생각하다가 정렬 기능에 있어서 성능이 좋은 시간 기반의
고유식별자인 tsid를 사용하게 되었다.

기능 개발하면서 클린 아키텍처를 적용하고 도메인 중심적으로 개발하는 방법을 알게 되었다.
레이어를 나타내는 패키지는 presentation , application , domain , infrastructure로 구성했고
그 안에 도메인을 나타내는 concert , payment , queue , reservation , point 패키지로 구성했다.
비즈니스 로직은 application 레이어의 service에서 구현하도록 했고
도메인 로직은 domain 레이어의 entity , domain service에서 구현하도록 했다.