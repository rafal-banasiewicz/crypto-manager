## Crypto manager

## Table of contents
+ [Use case](#use-case)
+ [Technologies](#technologies)
+ [Setup](#setup)
+ [TODO](#todo)

### Use case:
+ calculating total money spent on buying/selling cryptocurrencies on Zonda(BitBay) exchange
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
+ before using calculating feature you need to generate public and private keys on API of Zonda exchange with privileges "History" or "Transaction History"
+ when you have your keys typed you can select FIAT currency, years, transaction type (Buy/Sell) 
+ after couple seconds (due to Zonda api) your spending will show off under the form

#### TODO
+ [ ] add portfolio with manual transaction adding/deleting
+ [ ] add portfolio integration with Zonda exchange (whole transaction history with current holdings)
+ [ ] add containerization so app can be launched through docker
+ [ ] add other db connection (e.g. H2 for dev and postgreSQL/mySQL for prod)