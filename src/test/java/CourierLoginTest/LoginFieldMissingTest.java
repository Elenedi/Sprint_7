// Тест на проверку отсутствия поля логина/пароля при авторизации сущетсвующего курьера
package CourierLoginTest;

import CourierTest.Constants;
import CourierTest.CourierMethods;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    private Gson gson; // Создаем экземпляр Gson

    private int courierId = -1; // Переменная для хранения ID курьера
    private CourierTest.CourierMethods courierMethods = new CourierMethods(); // Экземпляр вспомогательного класса


    // С вынесенным URI в отдельный класс
    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URI;
        gson = new GsonBuilder().setPrettyPrinting().create(); // Инициализация gson
    }

    @Test
    @DisplayName("Missing required fields returns error")
    @Step("Test missing login or password during courier login")
    public void testMissingRequiredLoginCourier() {
        // Данные для создания курьера
        String login = "client";
        String password = "1234";
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"clipa\" }";
        // Отправляем запрос на создание курьера
        Response createResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");
        // Проверяем код ответа на создание курьера
        assertThat(createResponse.getStatusCode(), is(201));
        System.out.println("Курьер успешно создан. Код ответа: " + createResponse.getStatusCode());
        // Включаем авторизацию и получение ID курьера
        try {
            // Авторизуемся, чтобы получить ID курьера
            courierId = courierMethods.getCourierId(login, password); // Сохраняем ID курьера
            // Проверяем, что ID курьера получен корректно
            assertThat(courierId, is(not(-1))); // Что ID не -1

            // Тест 1: Отсутствует поле "login"
            String bodyWithoutLogin = "{ \"password\": \"1234\" }";
            Response responseWithoutLogin = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .body(bodyWithoutLogin)
                    .when()
                    .post("/api/v1/courier/login");
            // Ожидаемое сообщение об ошибке
            String expectedMessage = "Недостаточно данных для входа";
            // Проверяем код ответа и сообщение
            assertThat(responseWithoutLogin.getStatusCode(), is(400));
            assertThat(responseWithoutLogin.jsonPath().getString("message"), is(expectedMessage));
            System.out.println("Тест на отсутствие логина. Код ответа: " + responseWithoutLogin.getStatusCode());
        } finally {
            // Проверяем, был ли курьер создан и авторизован
            if (courierId != -1) {
                // Удаляем курьера
                Response deleteResponse = RestAssured.given()
                        .header("Content-Type", "application/json")
                        .when()
                        .delete("/api/v1/courier/" + courierId);
                // Проверяем код ответа на удаление курьера
                if (deleteResponse.getStatusCode() == 200) {
                    System.out.println("Курьер удален. Код ответа: " + deleteResponse.getStatusCode());
                } else {
                    System.err.println("Ошибка при удалении курьера. Код ответа: " + deleteResponse.getStatusCode());
                    System.err.println("Тело ответа: " + deleteResponse.asString());
                }
            } else {
                System.err.println("Ошибка: не удалось получить ID курьера, удаление невозможно.");
            }
        }
    }

    @Test
    @DisplayName("Missing required fields returns error")
    @Step("Test missing login or password during courier login")
    public void testMissingRequiredPasswordCourier() {
        // Данные для создания курьера
        String login = "client";
        String password = "1234";
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"clipa\" }";
        // Отправляем запрос на создание курьера
        Response createResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");
        // Ожидаемое сообщение об ошибке
        String expectedMessage = "Недостаточно данных для входа";
        // Проверяем код ответа на создание курьера
        assertThat(createResponse.getStatusCode(), is(201));
        System.out.println("Курьер успешно создан. Код ответа: " + createResponse.getStatusCode());
        // Включаем авторизацию и получение ID курьера
        try {
            // Авторизуемся, чтобы получить ID курьера
            courierId = courierMethods.getCourierId(login, password); // Сохраняем ID курьера
            // Проверяем, что ID курьера получен корректно
            assertThat(courierId, is(not(-1))); // Что ID не -1

            // Тест 2: Отсутствует поле "password"
            String bodyWithoutPassword = "{ \"login\": \"client\" }"; // Существующий логин
            Response responseWithoutPassword = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .body(bodyWithoutPassword)
                    .when()
                    .post("/api/v1/courier/login");
            // Проверяем код ответа и сообщение
            assertThat(responseWithoutPassword.getStatusCode(), is(400));
            assertThat(responseWithoutPassword.jsonPath().getString("message"), is(expectedMessage));
            System.out.println("Тест на отсутствие пароля. Код ответа: " + responseWithoutPassword.getStatusCode());
        } finally {
            // Проверяем, был ли курьер создан и авторизован
            if (courierId != -1) {
                // Удаляем курьера
                Response deleteResponse = RestAssured.given()
                        .header("Content-Type", "application/json")
                        .when()
                        .delete("/api/v1/courier/" + courierId);
                // Проверяем код ответа на удаление курьера
                if (deleteResponse.getStatusCode() == 200) {
                    System.out.println("Курьер удален. Код ответа: " + deleteResponse.getStatusCode());
                } else {
                    System.err.println("Ошибка при удалении курьера. Код ответа: " + deleteResponse.getStatusCode());
                    System.err.println("Тело ответа: " + deleteResponse.asString());
                }
            } else {
                System.err.println("Ошибка: не удалось получить ID курьера, удаление невозможно.");
            }
        }
    }
}
