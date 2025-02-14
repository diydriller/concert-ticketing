## 주요 기능
* 현재 서비스는 하나의 서버로 구성되어 있고 주요기능은 아래와 같다.
    * 콘서트 예약
        ```kotlin
            @Transactional
            fun 콘서트_예약(){
               콘서트_좌석_임시_예약()
               콘서트_좌석_예약_이벤트_발행() 
            }
            
            @Async
            @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)      
            fun 콘서트_좌석_예약_이벤트_리스너(){
                데이터_플랫폼_데이터_전송()
            }      
        ``` 
    * 결제 
      ```kotlin
            @Transactional
            fun 결제하기(){
                결제내역_생성()
                포인트_차감()
                예약_확정()
            }
      ```
## MSA 환경
* 도메인을 중심으로 서비스를 나눌 수 있다. 각 서비스는 데이터베이스를 자체적으로 가지고 있고 트랜잭션은 독립적이다.
  * concert service
  * payment service
  * reservation service
  * user service
  * point service

## two phase commit
* prepare 단계
  * coordinator는 모든 participant에게 prepare 요청을 보내면
    participant들은 commit 준비를 한다.
* commit 단계
  * coordinator는 모든 participant에게 commit 요청을 보내고 하나라도 
    에러가 발생하면 rollback 요청을 보낸다.

## saga pattern
* choregraphy 기반
  * 서비스가 작업 수행 후 완료 이벤트를 발행하면 이후 다음 서비스가 해당 이벤트를 구독해서 작업을 수행한다.
    실패하면 보상 트랜잭션을 통해 rollback을 한다.
* orchestration 기반
  * orchestrator가 중간에서 각 서비스를 호출한다. 실패하면 보상 트랜잭션을 통해 rollback을 한다.



