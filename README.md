# Dainius Stock Service

This project is a stock service that allows you to manage stocks and their prices.

## Build

To build the project, run the following command:
```bash
./gradlew clean dockerBuild --console=plain
```
## Run (Start under Docker)

To start the service using Docker, navigate to the project root directory and run the following command:
```bash
./gradlew dockerComposeUp
```
# Test

## Call Dainius Stock Service under docker

### Add a New Stock

To add a new stock, use the following command:
```bash
curl -X POST -H "Content-Type: application/json" -d "{\"ticker\":\"IBM.N\",\"description\":\"International Business Machines\",\"sharesOutstanding\":98000000}" http://localhost:8080/api/v1/stocks
```
### Get All Stocks

To retrieve all stocks, use the following command:
```bash
curl -X GET http://localhost:8080/api/v1/stocks
```
### Get Stocks Matching the Ticker

To retrieve stocks matching a specific ticker, use the following command:
```bash
curl -X GET http://localhost:8080/api/v1/stocks?ticker=IBM.N
```
### Update an Existing Stock

To update an existing stock, use the following command:
```bash
curl -X POST -H "Content-Type: application/json" -d "{\"description\":\"Updated description\",\"sharesOutstanding\":128000000}" http://localhost:8080/api/v1/stocks/IBM.N
```
### Get Stocks Matching the Ticker Pattern

To retrieve stocks matching a ticker pattern, use the following command:
```bash
curl -X GET http://localhost:8080/api/v1/stocks?ticker="IB"
```
## Price Service

### Add a New Price for a Stock

To add a new price for a stock, use the following command:
```bash
curl -X POST -H "Content-Type: application/json" -d "{\"ticker\":\"IBM.N\",\"price\":19.99,\"date\":\"2024-03-10\"}" http://localhost:8080/api/v1/stocks/IBM.N/prices

### Get Prices for a Stock Within a Date Range
```
To retrieve prices for a stock within a specific date range, use the following command:
```bash
curl -X GET "http://localhost:8080/api/v1/stocks/IBM.N/prices?fromDate=2024-03-01&toDate=2029-05-03"
```