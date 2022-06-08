## Micro Investor
- Java 17.0.1
- Spring boot 2.7 
- Сборка на Gradle 
- Для взаимодействия с внешними сервисами используется Feign

Сервис обращается к сервису курсов валют, и отображает gif:
- если курс по отношению к USD за сегодня стал выше вчерашнего, то возвращает рандомную отсюда https://giphy.com/search/rich
- если ниже - отсюда https://giphy.com/search/broke
- если не изменился - отсюда https://giphy.com/search/same+as+yesterday

## Доступные endpoints
```
http://<host>:8080/rate/<currency_code>
```
## Пример вызова
```
http://localhost:8080/rate/RUB
```

## Установка и сборка
```console
git clone https://github.com/just-a-tractor/Micro_investor.git
cd Micro_investor
.\gradlew build
```
  
## Запуск
```console
cd build/libs
java -jar Micro_investor-0.0.1-SNAPSHOT.jar
```
