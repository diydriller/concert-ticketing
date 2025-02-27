## k6
오픈소스 부하테스트 도구이다.

### executor
* constant-vus
  * 일정한 가상 유저수
* constant-arrival-rate
  * 일정한 초당 요청수
* ramping-vus
  * 점진적으로 가상 유저수를 증가/감소 
* ramping-arrival-rate
  * 점진적으로 초당 요청수를 증가/감소

## 부하 테스트 
### 가정
* DAU는 10만명이라고하고 1명당 1일 평균 접속수는 20번이라고 가정했다.
  평균 RPS는 23으로 계산했고 피크타임 2시간동안 전체 요청의 70퍼센트가 몰린다고 가정해서
  최대 RPS를 97로 산정했다.
* 테스트는 10분동안 진행했다.

### 콘서트 조회
콘서트 조회를 할 때 redis를 사용해서 캐싱을 적용했다.
처음에는 데이터베이스로부터 데이터를 조회하고 캐시에 저장했다가
이후에는 TTL 시간동안 캐시만 조회하는 방식이다.
트래픽이 한번에 몰려서 캐시가 아닌 데이터베이스에 트래픽이 몰리는 현상을 방지하기 위해 
랜덤하게 TTL을 설정하도록 했다. 기본은 200초이고 0~60초 사이의 랜덤한 값을 더해서 TTL을 산정했다.

아래는 테스트 결과이다.
평균 응답 시간은 34.14m이고 상위 90퍼센트 응답 시간은 11.6ms이고 상위 95퍼센트 응답시간이 13.75ms로 측정이 되었다.
최대 응답시간은 7.47ms이다. 실패 횟수가 2개인데 grafana에서 connection timeout으로 발생한 것을 확인할 수 있다.
이는 캐시 미스로 인한 응답시간 지연으로 발생된것으로 추측된다.

```shell
   data_received..................: 29 MB  47 kB/s
   data_sent......................: 5.0 MB 8.3 kB/s
   dropped_iterations.............: 369    0.612211/s
   http_req_blocked...............: avg=24.18µs min=0s     med=0s       max=21.95ms  p(90)=0s       p(95)=0s
   http_req_connecting............: avg=15.27µs min=0s     med=0s       max=21.95ms  p(90)=0s       p(95)=0s
   http_req_duration..............: avg=34.14ms min=1.99ms med=5.59ms   max=7.47s    p(90)=11.6ms   p(95)=13.75ms
     { expected_response:true }...: avg=34.1ms  min=1.99ms med=5.59ms   max=7.47s    p(90)=11.6ms   p(95)=13.75ms
   http_req_failed................: 0.00%  ✓ 2        ✗ 57833
   http_req_receiving.............: avg=8.45ms  min=0s     med=528.69µs max=5.32s    p(90)=741.35µs p(95)=1.04ms
   http_req_sending...............: avg=37.4µs  min=0s     med=0s       max=123.49ms p(90)=0s       p(95)=218.65µs
   http_req_tls_handshaking.......: avg=0s      min=0s     med=0s       max=0s       p(90)=0s       p(95)=0s
   http_req_waiting...............: avg=25.65ms min=1.99ms med=5.03ms   max=6.3s     p(90)=11.05ms  p(95)=13.23ms
   http_reqs......................: 57835  95.95447/s
   iteration_duration.............: avg=34.44ms min=2ms    med=5.89ms   max=7.47s    p(90)=11.89ms  p(95)=14.07ms
   iterations.....................: 57835  95.95447/s
   vus............................: 200    min=200    max=200
   vus_max........................: 200    min=200    max=200
```
![hikaricp](https://github.com/user-attachments/assets/445c75e2-d6c4-45bf-ada9-30b2f5533e53)
![cpu](https://github.com/user-attachments/assets/d64c2b2e-0902-42ee-82ba-d09d5a2ba40f)
![http](https://github.com/user-attachments/assets/193dbc3d-00ed-4024-9c3c-705041785e21)

장애 요인으로 생각할 수 있는 부분은 특정 시점에 캐시 미스로 인해 데이터베이스로 트래픽이 몰려서 응답시간이 길어질 수 있는 부분이다.
자주 조회되는 데이터의 경우 배치 작업을 통해 정기적으로 캐싱을 갱신하는 것을 통해
캐시 미스로 인한 응답식나 지연을 어느정도 줄일 수 있을거라 예상된다. 