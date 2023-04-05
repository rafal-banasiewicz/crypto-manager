## Crypto manager

## Table of contents
+ [Use case](#use-case)
+ [Technologies](#technologies)
+ [Setup](#setup)
+ [TODO](#todo)

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

#### Setup

+ clone repository: `$ git clone https://github.com/rafal-banasiewicz/crypto-manager.git`

+ run it on your computer using any IDE e.g. IntelliJ
  + before use Zonda feature generate public and private keys in API tab of Zonda exchange with privileges "History" or "Transaction History"
  + before use Binance feature generate public and secret key in API tab of Binance exchange with default privileges (the one You can't uncheck)
+ when you have your keys typed you can select FIAT currency, years, transaction type (Buy/Sell) 
+ after couple seconds (due to Zonda api) your spending will show off under the form

#### TODO
+ [ ] add portfolio with manual transaction adding/deleting
+ [ ] add portfolio integration with Zonda exchange (whole transaction history with current holdings)
+ [ ] add containerization so app can be launched through docker
+ [ ] add other db connection (e.g. H2 for dev and postgreSQL/mySQL for prod)