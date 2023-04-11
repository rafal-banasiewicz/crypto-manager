## Crypto manager

## Table of contents
+ [Use case](#use-case)
+ [Technologies](#technologies)
+ [Run App](run-app)
+ [Setup](#setup)

### Use case:
+ calculating total money spent on buying/selling cryptocurrencies on Zonda(BitBay) exchange
+ calculating total money spent on buying/selling cryptocurrencies on Binance exchange
#### Technologies
+ spring-boot
+ spring-data-jpa
+ spring-web
+ spring-security
+ spring-validation
+ thymeleaf
+ H2 Database
+ API

## Run app

```
$ docker-compose up -d
```

#### Setup

+ before use Zonda feature generate public and private keys in API tab of Zonda exchange with privileges "History" or "Transaction History"
+ before use Binance feature generate public and secret key in API tab of Binance exchange with default privileges (the one You can't uncheck)
+ when you have your keys typed you can select FIAT currency, years, transaction type (Buy/Sell) 
+ after couple seconds (due to Zonda api) your spending will show off under the form