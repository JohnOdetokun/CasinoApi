# Spring Boot "Casino" POC Project

This is a simple Java / Maven / Spring Boot Proof of Concept REST API for a games provider.

## How to Run

This application is packaged as a jar which has Tomcat embedded. No Tomcat or JBoss installation is necessary. You run it using the ```java -jar``` command.

* Clone this repository
* Make sure you are using JDK 17 and Maven 4.x
* You can build the project and run the tests by running ```mvn clean package```
* Once successfully built, you can run the service by one of these two methods:
```
        java -jar -Dspring.profiles.active=test target/CasinoApi-0.0.1-SNAPSHOT.jar
or
        mvn spring-boot:run -Drun.arguments="spring.profiles.active=test"
```
* Check the stdout or boot_example.log file to make sure no exceptions are thrown

Once the application runs you should see something like this

```
2023-09-06T18:51:25.001+02:00  INFO 14352 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2023-09-06T18:51:25.009+02:00  INFO 14352 --- [           main] c.a.casinoapi.CasinoApiApplication       : Started CasinoApiApplication in 5.458 seconds (process running for 5.901)
```

## About the Service

The service is an initial casino player transaction REST service POC. It uses an in-memory database (H2) to store the data.

Here are some endpoints you can call:

Base path: `http://localhost:8080/casino`

### Get a players balance

```
GET /player/{playerId}/balance

RESPONSE: HTTP 200 (OK)
{
    "playerId": 1234,
    "balance": 550.5
}
```

### Update Balance

```
POST /player/1234/balance/update
Accept: application/json
Content-Type: application/json

{
  "amount": 250,
  "transactionType": "WIN"
}


RESPONSE: HTTP 200 (OK)
{
    "transactionId": 1,
    "balance": 800.5
}
```

### Last 10 Transactions

```
POST /admin/player/transactions
Accept: application/json
Content-Type: application/json

{
    "username": "BobTheBuilder22"
}


RESPONSE: HTTP 200 (OK)
{
    "transactionId": 1,
    "balance": 800.5
}
```

The database is initialized with the following dummy data as a Player creation API has not yet been added:

```
-- Players
INSERT INTO player (player_id,username,balance) VALUES (1234,'BobTheBuilder22',550.50);
INSERT INTO `player` (player_id,username,balance) VALUES (12345,'Jackson23',1550.50);
INSERT INTO `player` (player_id,username,balance) VALUES (123456,'Honey15',100);
INSERT INTO `player` (player_id,username,balance) VALUES (1234567,'User21',450550.50);
INSERT INTO `player` (player_id,username,balance) VALUES (12345678,'TheRealOne',55550.50);

-- Transactions
INSERT INTO `transaction` (transaction_id,player_id,amount,running_balance,transaction_type,transaction_date_time) VALUES (55432,12345678,500.0,1550.50,'WAGER','2023-09-05T19:44:35.377579Z');
INSERT INTO `transaction` (transaction_id,player_id,amount,running_balance,transaction_type,transaction_date_time) VALUES (57732,12345678,200.0,1000.00,'WIN','2023-08-05T19:44:35.377579Z');
```


# Comments

### Transaction Handling
* I have opted to make use of Optimistic Locking by using database transactions. This should be sufficient for the POC. 
Pessimistic locking is an option that could be explored if any long-running operations are introduced. 
* It could be useful to store failed transactions for debugging and auditing reasons.

### Improvements/Future Additions
* Database indexes on the timestamp field, player id fields may be useful for optimized queries when scale increases.
* Further normalization is possible but not needed for this POC.
* I have used JsonView in order to re-use the same models to create various request/response json bodies. In expanding this logic we could use further DTOs/POJOs for better separation of concerns.
* Projections are also an option that can be used so that we limit data retrieved from the DB rather than filtering post read with JsonView
