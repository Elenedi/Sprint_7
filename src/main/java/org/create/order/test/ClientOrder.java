package org.create.order.test;

import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.courier.test.Constants;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.qameta.allure.internal.shadowed.jackson.databind.SerializationFeature;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;
import static org.apache.http.HttpStatus.*;

public class ClientOrder {
    private static final String CREATE_ORDERS = "api/v1/orders";
    private static final String CANCEL_ORDER = "api/v1/orders/finish";
    private static final String GET_ORDER_BY_TRACK = "api/v1/orders/track";

    @Step("Creating an order")
    public static Response createNewOrder(OrderCreation orderCreation) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(orderCreation)
                .post(Constants.BASE_URI + CREATE_ORDERS);

        assertThat(response.getStatusCode(), is(SC_CREATED));
        JsonPath jsonPath = new JsonPath(response.asString());
        Integer trackNumber = jsonPath.get("track");
        assertThat(trackNumber, is(not(0)));
        return response;
    }

    @Step("Get order ID by track")
    public static String getOrderId(Response response) {
        String trackNumber = response.jsonPath().getString("track");
        Response trackResponse = given()
                .header("Content-type", "application/json")
                .get(Constants.BASE_URI + GET_ORDER_BY_TRACK + "?track=" + trackNumber);

        assertThat(trackResponse.getStatusCode(), is(SC_OK));
        return trackResponse.jsonPath().getString("id");
    }

    @Step("Closing an order by ID")
    public static Response deleteOrder(String id) {
        Response response = given()
                .header("Content-type", "application/json")
                .put(Constants.BASE_URI + CANCEL_ORDER + "?id=" + id);

        assertThat(response.getStatusCode(), is(SC_OK));
        return response;
    }

    @Step("Get Orders List")
    public static Response getAllOrders() {
        Response response = given()
                .header("Content-type", "application/json")
                .get(Constants.BASE_URI + CREATE_ORDERS);

        assertThat(response.getStatusCode(), is(SC_OK));
        return response;
    }

    public static void comparingSuccessfulOrderSet(Response createResponse, int scCreated) {
        assertThat(createResponse.getStatusCode(), is(scCreated));
    }
}

