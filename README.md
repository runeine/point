# point API

적립 : http://localhost:8080/api/points/save
사용 : http://localhost:8080/api/points/use
적립취소 : http://localhost:8080/api/points/save/cancel
사용취소 : http://localhost:8080/api/points/use/cancel


# Table scheme

table 정보는 아래 파일에 DDL이 작성되어 있으며, H2 메모리로 application 실행 시 생성됩니다.
point/src/main/resources/schema.sql

## Example curl command
## 테스트 시나리오
## 1. 1000 적립 -> 잔액 1000원
## 2. 500 적립 -> 잔액 1500원
## 3. 1200원 사용(주문번호 1) -> 잔액 300원
## 4. 300원 관리자 적립 -> 잔액 600원(일반 300, 관리자 300)
## 5. 400원 사용(주문번호 2) -> 잔액 300원(일반 200원)
## 6. 주문번호 2 사용 취소 -> 잔액 600원(일반 300, 관리자 300) 
## 7. 500원 적립 -> 잔액 1100원
## 8. 7번 적립 취소 -> 잔액 600원
```sh
curl -X POST http://localhost:8080/api/points/save \
     -H "Content-Type: application/json" \
     -d '{
           "pointKey": "",
           "userKey": "A1234",
           "amount": 1000,
           "transactionTypeDetail": "NORMAL_SAVE"
         }'

curl -X POST http://localhost:8080/api/points/save \
     -H "Content-Type: application/json" \
     -d '{
           "pointKey": "",
           "userKey": "A1234",
           "amount": 500,
           "transactionTypeDetail": "NORMAL_SAVE"
         }'

curl -X POST http://localhost:8080/api/points/use \
     -H "Content-Type: application/json" \
     -d '{
           "pointKey": "",
           "userKey": "A1234",
           "amount": 1200,
           "orderKey": "1",
           "transactionTypeDetail": "NORMAL_USE"
         }'

curl -X POST http://localhost:8080/api/points/save \
     -H "Content-Type: application/json" \
     -d '{
           "pointKey": "",
           "userKey": "A1234",
           "amount": 300,
           "transactionTypeDetail": "ADMIN_SAVE"
         }'    

curl -X POST http://localhost:8080/api/points/use \
     -H "Content-Type: application/json" \
     -d '{
           "pointKey": "",
           "userKey": "A1234",
           "amount": 400,
           "orderKey": "2",
           "transactionTypeDetail": "NORMAL_USE"
         }'   

curl -X POST http://localhost:8080/api/points/use/cancel \
     -H "Content-Type: application/json" \
     -d '{
           "pointKey": "",
           "userKey": "A1234",
           "amount": 400,
           "orderKey": "2",
           "transactionTypeDetail": "CANCEL_USE"
         }'  

curl -X POST http://localhost:8080/api/points/save/cancel \
     -H "Content-Type: application/json" \
     -d '{
           "pointKey": "1",
           "userKey": "A1234",
           "amount": 400,
           "orderKey": "2",
           "transactionTypeDetail": "CANCEL_SAVE"
         }'    

curl -X POST http://localhost:8080/api/points/save \
     -H "Content-Type: application/json" \
     -d '{
           "pointKey": "",
           "userKey": "A1234",
           "amount": 500,
           "transactionTypeDetail": "NORMAL_SAVE"
         }'

curl -X POST http://localhost:8080/api/points/save/cancel \
     -H "Content-Type: application/json" \
     -d '{
           "pointKey": "4",
           "userKey": "A1234",
           "amount": 400,
           "transactionTypeDetail": "CANCEL_SAVE"
         }'  
```

## point config(a)
point.min-amount : 최소적립금액
point.max-amount : 최대적립금액

## 빌드 방법

이 프로젝트는 Gradle을 사용하여 빌드됩니다. 다음 명령어를 사용하여 프로젝트를 빌드할 수 있습니다:

```sh
./gradlew build
```