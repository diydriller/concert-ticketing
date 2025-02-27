## 대기열 토큰 발급
* 기존 쿼리 
```kotlin
select
    u1_0.id,
    u1_0.created_date,
    u1_0.email,
    u1_0.name,
    u1_0.password,
    u1_0.updated_date
from
    user u1_0
where            
    u1_0.id=?
```
* 기존 쿼리의 실행 계획
```json
{
  "id": 1,
  "select_type": "SIMPLE",
  "table": "u1_0",
  "partitions": null,
  "type": "const",
  "possible_keys": "PRIMARY",
  "key": "PRIMARY",
  "key_len": "1022",
  "ref": "const",
  "rows": 1,
  "filtered": 100,
  "Extra": null
}
```
* 개선한 부분
  * 토큰 발급시 유저를 조회하는데 유저의 존재 유무만 알려는 것이기 때문에 covering index를 적용해서 select 절에서 id만 조회하도록 할 수 있다.
  ```kotlin
    select
        u1_0.id 
    from
        user u1_0 
    where
        u1_0.id=?
  ``` 
  * 10000건 기준으로 10ms에서 2ms로 개선이 되었다.
* 개선 후 실행 계획
```json
{
  "id": 1,
  "select_type": "SIMPLE",
  "table": "u1_0",
  "partitions": null,
  "type": "const",
  "possible_keys": "PRIMARY",
  "key": "PRIMARY",
  "key_len": "1022",
  "ref": "const",
  "rows": 1,
  "filtered": 100,
  "Extra": "Using index"
}
```

## 예약가능한 콘서트 일정 조회 
* 기존 쿼리
  ```kotlin
    select
    cs1_0.id,
    cs1_0.concert_id,
    cs1_0.created_date,
    cs1_0.date,
    cs1_0.reserved_seat_count,
    cs1_0.total_seat_count,
    cs1_0.updated_date 
    from
        concert_schedule cs1_0 
    where
        cs1_0.concert_id=? 
        and cs1_0.reserved_seat_count<cs1_0.total_seat_count 
    order by
        cs1_0.created_date desc 
    limit
        ?
  ```
* 기존 실행 계획
    ```json
      {
      "id": 1,
      "select_type": "SIMPLE",
      "table": "cs1_0",
      "partitions": null,
      "type": "ref",
      "possible_keys": "FK3ry7aiaia6ooa3ajwf6w6soci",
      "key": "FK3ry7aiaia6ooa3ajwf6w6soci",
      "key_len": "1023",
      "ref": "const",
      "rows": 2,
      "filtered": 50,
      "Extra": "Using where; Using filesort"
  }
  ```
* 개선한 부분
    * 인덱스를 사용하지 않고 정렬을 하고 있다. order by 절에 들어가는 컬럼을 인덱스로 설정한다.
    * 10000건 기준으로 5ms에서 1ms로 개선이 되었다.
    ```shell
      CREATE INDEX idx_created_date ON concert_schedule (concert_id, created_date DESC);
    ```
* 개선 후 실행 계획
  ```json
    {
    "id": 1,
    "select_type": "SIMPLE",
    "table": "cs1_0",
    "partitions": null,
    "type": "ref",
    "possible_keys": "idx_created_date",
    "key": "idx_created_date",
    "key_len": "1023",
    "ref": "const",
    "rows": 2,
    "filtered": 50,
    "Extra": "Using where"
    }
  ```