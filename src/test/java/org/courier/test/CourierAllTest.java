// Тесты со всеми вынесенными методами в отдельный класс
package org.courier.test;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@Epic("Courier Management")
@Feature("Courier Creation")
public class CourierAllTest {

    private static final String DEFAULT_LOGIN = "client";
    private static final String DEFAULT_PASSWORD = "1234";
    private static final String DEFAULT_FIRST_NAME = "clipa";

    private int courierId = -1;
    private CourierMethods courierMethods = new CourierMethods();

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
    @Story("Create a new courier")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a new courier is possible and returns the correct response")
    public void testCreateCourierIsPossible() {
        String body = courierMethods.createRequestBody(DEFAULT_LOGIN, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);
        Response response = courierMethods.createCourier(body);
        courierMethods.checkStatusCode(response, 201);
        assertThat(response.jsonPath().get("ok"), is(true));
        courierId = courierMethods.authorizeCourier(DEFAULT_LOGIN, DEFAULT_PASSWORD);
    }

    @Test
    @Story("Prevent duplicate courier creation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that creating a courier with the same login returns an error")
    public void testErrorCreateTheSameCourier() {
        String body = courierMethods.createRequestBody(DEFAULT_LOGIN, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);
        Response firstResponse = courierMethods.createCourier(body);
        courierMethods.checkStatusCode(firstResponse, 201);

        Response secondResponse = courierMethods.createCourier(body);
        courierMethods.checkStatusCode(secondResponse, 409);
        assertThat(secondResponse.jsonPath().getString("message"), is("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @Story("Validate required fields for courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that all required fields are present when creating a courier")
    public void testCreateCourierWithAllRequiredFields() {
        String body = courierMethods.createRequestBody(DEFAULT_LOGIN, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);
        Response response = courierMethods.createCourier(body);
        courierMethods.checkStatusCode(response, 201);
        assertThat(response.jsonPath().get("ok"), is(true));
        courierId = courierMethods.authorizeCourier(DEFAULT_LOGIN, DEFAULT_PASSWORD);
    }

    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a login returns an error")
    public void testCreateCourierWithoutLogin() {
        String bodyWithoutLogin = "{ \"password\": \"1234\", \"firstName\": \"clipa\" }";
        Response response = courierMethods.createCourier(bodyWithoutLogin);
        courierMethods.checkStatusCode(response, 400);
        assertThat(response.jsonPath().getString("message"), is("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a password returns an error")
    public void testCreateCourierWithoutPassword() {
        String bodyWithoutPassword = "{ \"login\": \"client\", \"firstName\": \"clipa\" }";
        Response response = courierMethods.createCourier(bodyWithoutPassword);
        courierMethods.checkStatusCode(response, 400);
        assertThat(response.jsonPath().getString("message"), is("Недостаточно данных для создания учетной записи"));
    }
}


