// Тест на проверку отсутствия поля логина/пароля при авторизации сущетсвующего курьера
package org.courier.login.test;

import org.courier.test.Constants;
import org.courier.test.CourierMethods;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@Epic("Courier Management")
@Feature("Courier Login")
public class LoginFieldMissingTest {
    private CourierMethods courierMethods = new CourierMethods();
    private int courierId = -1;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URI;
    }

    @Test
    @DisplayName("Missing required fields returns error")
    @Step("Test missing login or password during courier login")
    public void testMissingRequiredFields() {
        String login = "client";
        String password = "1234";
        String body = courierMethods.createRequestBody(login, password, "clipa");
        Response createResponse = courierMethods.createCourier(body);
        assertThat(createResponse.getStatusCode(), is(201));

        // Включаем авторизацию и получение ID курьера
        courierId = courierMethods.getCourierId(login, password);
        assertThat(courierId, is(not(-1)));

        // Тест 1: Отсутствует поле "login"
        Response responseWithoutLogin = RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"password\": \"" + password + "\"}")
                .when()
                .post("/api/v1/courier/login");
        assertThat(responseWithoutLogin.getStatusCode(), is(400));
        assertThat(responseWithoutLogin.jsonPath().getString("message"), is("Недостаточно данных для входа"));

        // Тест 2: Отсутствует поле "password"
        Response responseWithoutPassword = RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"login\": \"" + login + "\"}")
                .when()
                .post("/api/v1/courier/login");
        assertThat(responseWithoutPassword.getStatusCode(), is(400));
        assertThat(responseWithoutPassword.jsonPath().getString("message"), is("Недостаточно данных для входа"));
    }
}
