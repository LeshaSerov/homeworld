# Проект телеграмм-бот на языке Java
### Схема Базы Данных
![picture](raw/erdiagram.png)
### 
##### Инструментарий
    База Данных: Postrges
    IDE: InteliJ IDEA
    Язык Программирования: Java 
    Версия: 17.0.2
    Frameworks: 
        Postgresql
        Lombok
        Log4j
        JUnit

##### Scenario: a client is going to sign a contract
    Given: the client is on the client page
    When: he is not authorized
    Then: he is redirected to Authorized page and he should put his personal data. The data will records into the client data service

    When: he is authorized
    Then: the insurance contract service asks the insurance management contract service for history info whether or not accept a contract
    (to prevent malicious actions). Then this service rejects contract bid or requests for an insurance broker
    to organise a meeting to sign the contract

