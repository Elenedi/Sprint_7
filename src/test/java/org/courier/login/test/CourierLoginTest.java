/*Логин курьера
Проверь:
- курьер может авторизоваться;
- для авторизации нужно передать все обязательные поля;
- система вернёт ошибку, если неправильно указать логин или пароль;
- если какого-то поля нет, запрос возвращает ошибку;
- если авторизоваться под несуществующим пользователем, запрос возвращает ошибку;
- успешный запрос возвращает id
- все данные нужно удалять после того, как тест выполнится.
*/

package org.courier.login.test;
import org.courier.test.Constants;
import org.courier.test.CourierMethods;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.courier.test.CourierMethods.LOGIN_COURIER_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;

//Тесты без создания отдельного класса с методами
@Epic("Courier Management")
@Feature("Courier Login")
public class CourierLoginTest {

    private CourierMethods courierMethods = new CourierMethods();
    private Gson gson;
    private int courierId = -1;

    @After
    public void tearDown() {
        if (courierId != -1) {
            courierMethods.deleteCourier(courierId);
        }
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URI;
    }

    @Test
    @DisplayName("Courier can be created and login")
    @Step("Create courier and verify login")
    public void testCourierCanBeCreatedAndLogin() {
        String login = "client";
        String password = "1234";
        String body = courierMethods.createRequestBody(login, password, "clipa");

        Response createResponse = courierMethods.createCourier(body);
        assertThat(createResponse.getStatusCode(), is(SC_CREATED));

        String loginBody = courierMethods.createRequestBody(login, password, "");

        Response loginResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(loginBody)
                .when()
                .post(Constants.BASE_URI + LOGIN_COURIER_PATH);

        assertThat(loginResponse.getStatusCode(), is(SC_OK));
        assertThat(loginResponse.jsonPath().get("id"), is(notNullValue()));

        courierId = courierMethods.getCourierId(login, password);
        assertThat(courierId, is(not(-1)));
    }

    @Test
    @DisplayName("Login with wrong login")
    @Step("Test courier login with wrong login")
    public void testWithWrongLogin() {
        String body = courierMethods.createRequestBody("client", "1234", "clipa");
        Response createResponse = courierMethods.createCourier(body);

        assertThat(createResponse.getStatusCode(), is(SC_CREATED));

        String bodyWrongLogin = courierMethods.createRequestBody("wrong", "1234", "");
        Response responseWrongLogin = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWrongLogin)
                .when()
                .post(Constants.BASE_URI + LOGIN_COURIER_PATH);

        assertThat(responseWrongLogin.getStatusCode(), is(SC_NOT_FOUND));
        assertThat(responseWrongLogin.jsonPath().getString("message"), is("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Login with wrong password")
    @Step("Test courier login with wrong password")
    public void testWithWrongPassword() {
        String body = courierMethods.createRequestBody("client", "1234", "clipa");
        Response createResponse = courierMethods.createCourier(body);

        assertThat(createResponse.getStatusCode(), is(SC_CREATED));

        String bodyWrongPassword = courierMethods.createRequestBody("client", "wrong", "");
        Response responseWrongPassword = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWrongPassword)
                .when()
                .post(Constants.BASE_URI + LOGIN_COURIER_PATH);

        assertThat(responseWrongPassword.getStatusCode(), is(SC_NOT_FOUND));
        assertThat(responseWrongPassword.jsonPath().getString("message"), is("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Missing required fields returns error for missing login")
    @Step("Test missing login during courier login")
    public void testMissingLogin() {
        String body = courierMethods.createRequestBody("client", "1234", "clipa");
        Response createResponse = courierMethods.createCourier(body);
        assertThat(createResponse.getStatusCode(), is(SC_CREATED));

        String bodyWithoutLogin = "{ \"password\": \"1234\" }";
        Response responseWithoutLogin = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWithoutLogin)
                .when()
                .post(Constants.BASE_URI + LOGIN_COURIER_PATH);

        assertThat(responseWithoutLogin.getStatusCode(), is(SC_BAD_REQUEST));
        assertThat(responseWithoutLogin.jsonPath().getString("message"), is("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Missing required fields returns error for missing password")
    @Step("Test missing password during courier login")
    public void testMissingPassword() {
        String body = courierMethods.createRequestBody("client", "1234", "clipa");
        Response createResponse = courierMethods.createCourier(body);
        assertThat(createResponse.getStatusCode(), is(SC_CREATED));

        String bodyWithoutPassword = "{ \"login\": \"client\" }";
        Response responseWithoutPassword = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWithoutPassword)
                .when()
                .post(Constants.BASE_URI + LOGIN_COURIER_PATH);

        assertThat(responseWithoutPassword.getStatusCode(), is(SC_BAD_REQUEST));
        assertThat(responseWithoutPassword.jsonPath().getString("message"), is("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Login non-existent user returns error")
    @Step("Test login for non-existent courier")
    public void testLoginNonExistentUser() {
        String bodyNonExistentUser = "{ \"login\": \"giga\", \"password\": \"chad1\" }";
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyNonExistentUser)
                .when()
                .post(Constants.BASE_URI + LOGIN_COURIER_PATH);

        assertThat(response.getStatusCode(), is(SC_NOT_FOUND));
        assertThat(response.jsonPath().getString("message"), is("Учетная запись не найдена"));
    }
}


