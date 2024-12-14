package org.courier.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
    public class CourierMethods {
        private Gson gson;

        public CourierMethods() {
            gson = new GsonBuilder().setPrettyPrinting().create();
        }

        // Метод для проверки соответствия кода ответа ожидаемому значению
        @Step("Check status code")
        public void checkStatusCode(Response response, int expectedStatusCode) {
            assertThat(response.getStatusCode(), is(expectedStatusCode));
        }

        // Метод для проверки сообщения об ошибке
        @Step("Check error message")
        public void checkErrorMessage(Response response, String expectedMessage) {
            assertThat(response.jsonPath().getString("message"), is(expectedMessage));
        }

        // Метод создания тела запроса
        public String createRequestBody(String login, String password, String firstName) {
            return String.format("{\"login\":\"%s\", \"password\":\"%s\", \"firstName\":\"%s\"}", login, password, firstName);
        }

        // Метод отправки POST запроса
        @Step("Create courier")
        public Response createCourier(String body) {
            return RestAssured.given()
                    .header("Content-Type", "application/json")
                    .body(body)
                    .when()
                    .post(Constants.BASE_URI + "/api/v1/courier");
        }

        // Метод для авторизации
        @Step("Authorize courier")
        public int authorizeCourier(String login, String password) {
            int courierId = getCourierId(login, password);
            assertThat(courierId, is(not(-1))); // Убедитесь, что ID корректен
            return courierId;
        }

        // Метод для получения ID курьера по логину и паролю
        @Step("Get courier ID")
        public int getCourierId(String login, String password) {
            Response response = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .body(createRequestBody(login, password, "")) // Используем новый метод
                    .when()
                    .post(Constants.BASE_URI + "/api/v1/courier/login");

            if (response.getStatusCode() == 200) {
                return response.jsonPath().getInt("id");
            } else {
                return -1; // Если авторизация не удалась
            }
        }

        // Метод для удаления курьера по его ID
        @Step("Delete courier by ID")
        public void deleteCourier(int courierId) {
            RestAssured.given()
                    .header("Content-Type", "application/json")
                    .when()
                    .delete(Constants.BASE_URI + "/api/v1/courier/" + courierId)
                    .then()
                    .statusCode(200); // Ожидаем успешное удаление курьера
        }
    }
