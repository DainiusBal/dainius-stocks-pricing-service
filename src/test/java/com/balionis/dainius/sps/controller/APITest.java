package com.balionis.dainius.sps.controller;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class APITest {

    private static ClientAndServer mockServer;

    @BeforeAll
    public static void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(8080); // Start MockServer on port 8080
    }

    @AfterAll
    public static void stopMockServer() {
        mockServer.stop();
    }

    @Test
    public void testGetAllStocks() {
        // Mocking the API endpoint
        new MockServerClient("localhost", 8080)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/v1/stocks")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("[{\"ticker\":\"AAPL\",\"description\":\"Apple Inc.\",\"sharesOutstanding\":1000000}]")
                                .withHeader("Content-Type", "application/json")
                );

        // Performing the REST Assured test
        given()
                .when()
                .get("http://localhost:8080/api/v1/stocks")
                .then()
                .assertThat()
                .statusCode(200)
                .body("[0].ticker", equalTo("AAPL"))
                .body("[0].description", equalTo("Apple Inc."))
                .body("[0].sharesOutstanding", equalTo(1000000));
    }

    @Test
    public void testGetStocksByTicker() {
        // Mocking the API endpoint
        new MockServerClient("localhost", 8080)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/v1/stocks")
                                .withQueryStringParameter("ticker", "IBM.N")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("[{\"ticker\":\"IBM.N\",\"description\":\"International Business Machines\",\"sharesOutstanding\":98000000}]")
                                .withHeader("Content-Type", "application/json")
                );

        // Performing the REST Assured test
        given()
                .param("ticker", "IBM.N")
                .when()
                .get("http://localhost:8080/api/v1/stocks")
                .then()
                .assertThat()
                .statusCode(200)
                .body("[0].ticker", equalTo("IBM.N"))
                .body("[0].description", equalTo("International Business Machines"))
                .body("[0].sharesOutstanding", equalTo(98000000));
    }

    @Test
    public void testAddNewStock() {
        // Mocking the API endpoint
        new MockServerClient("localhost", 8080)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/api/v1/stocks")
                                .withBody("{\"ticker\":\"IBM.N\",\"description\":\"International Business Machines\",\"sharesOutstanding\":98000000}")
                                .withHeader("Content-Type", "application/json")
                )
                .respond(
                        response()
                                .withStatusCode(201)
                                .withBody("{\"ticker\":\"IBM.N\",\"description\":\"International Business Machines\",\"sharesOutstanding\":98000000}")
                                .withHeader("Content-Type", "application/json")
                );


        given()
                .contentType("application/json")
                .body("{\"ticker\":\"IBM.N\",\"description\":\"International Business Machines\",\"sharesOutstanding\":98000000}")
                .when()
                .post("http://localhost:8080/api/v1/stocks")
                .then()
                .assertThat()
                .statusCode(201)
                .body("ticker", equalTo("IBM.N"))
                .body("description", equalTo("International Business Machines"))
                .body("sharesOutstanding", equalTo(98000000));
    }

    @Test
    public void testUpdateExistingStock() {

        new MockServerClient("localhost", 8080)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/api/v1/stocks/IBM.N")
                                .withBody("{\"description\":\"Updated description\",\"sharesOutstanding\":128000000}")
                                .withHeader("Content-Type", "application/json")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("{\"ticker\":\"IBM.N\",\"description\":\"Updated description\",\"sharesOutstanding\":128000000}")
                                .withHeader("Content-Type", "application/json")
                );


        given()
                .contentType("application/json")
                .body("{\"description\":\"Updated description\",\"sharesOutstanding\":128000000}")
                .when()
                .post("http://localhost:8080/api/v1/stocks/IBM.N")
                .then()
                .assertThat()
                .statusCode(200)
                .body("ticker", equalTo("IBM.N"))
                .body("description", equalTo("Updated description"))
                .body("sharesOutstanding", equalTo(128000000));
    }

    @Test
    public void testAddNewPriceForStock() {

        new MockServerClient("localhost", 8080)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/api/v1/stocks/IBM.N/prices")
                                .withBody("{\"ticker\":\"IBM.N\",\"price\":19.99,\"date\":\"2024-03-10\"}")
                                .withHeader("Content-Type", "application/json")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("{\"ticker\":\"IBM.N\",\"price\":19.99,\"date\":\"2024-03-10\"}")
                                .withHeader("Content-Type", "application/json")
                );


        given()
                .contentType("application/json")
                .body("{\"ticker\":\"IBM.N\",\"price\":19.99,\"date\":\"2024-03-10\"}")
                .when()
                .post("http://localhost:8080/api/v1/stocks/IBM.N/prices")
                .then()
                .assertThat()
                .statusCode(200)
                .body("ticker", equalTo("IBM.N"))
                .body("price", equalTo(19.99f))
                .body("date", equalTo("2024-03-10"));
    }

    @Test
    public void testGetPricesForStockWithinDateRange() {

        new MockServerClient("localhost", 8080)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/v1/stocks/IBM.N/prices")
                                .withQueryStringParameter("fromDate", "2024-03-01")
                                .withQueryStringParameter("toDate", "2024-03-15")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("[{\"ticker\":\"IBM.N\",\"price\":19.99,\"date\":\"2024-03-05\"},{\"ticker\":\"IBM.N\",\"price\":20.50,\"date\":\"2024-03-10\"}]")
                                .withHeader("Content-Type", "application/json")
                );


        given()
                .when()
                .get("http://localhost:8080/api/v1/stocks/IBM.N/prices?fromDate=2024-03-01&toDate=2024-03-15")
                .then()
                .assertThat()
                .statusCode(200)
                .body("[0].ticker", equalTo("IBM.N"))
                .body("[0].price", equalTo(19.99f))
                .body("[0].date", equalTo("2024-03-05"))
                .body("[1].ticker", equalTo("IBM.N"))
                .body("[1].price", equalTo(20.50f))
                .body("[1].date", equalTo("2024-03-10"));
    }


}







