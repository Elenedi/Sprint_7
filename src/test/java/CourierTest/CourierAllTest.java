// Тесты со всеми вынесенными методами в отдельный класс
package CourierTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    private Gson gson; // Создаем экземпляр Gson

    private int courierId = -1; // Глобальная переменная для хранения ID курьера
    private CourierMethods courierMethods = new CourierMethods(); // Экземпляр вспомогательного класса

    @After
    public void tearDown() {
        // Удаление курьера после каждого теста, если ID был получен
        if (courierId != -1) {
            courierMethods.deleteCourier(courierId);
        }
    }

    // С вынесенным URI в отдельный класс
    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URI;
        gson = new GsonBuilder().setPrettyPrinting().create(); // Инициализация gson
    }

    // Тест что курьера можно создать
    @Test
    @Story("Create a new courier")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a new courier is possible and returns the correct response")
    public void testCreateCourierIsPossible() {
        String body = courierMethods.createRequestBody(DEFAULT_LOGIN, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);
        Response response = courierMethods.createCourier(body);
        // Форматируем тело ответа с помощью метода из CourierHelper
        String formattedResponseBody = courierMethods.formatResponseBody(response.getBody().asString());
        // Выводим отформатированное тело ответа
        System.out.println("Formatted response body: " + formattedResponseBody);
        // Выводим код ответа на экран
        System.out.println("Status code: " + response.getStatusCode());
        // Проверяем код ответа
        courierMethods.checkStatusCode(response, 201);
        // Проверяем, что в теле ответа поле "ok" равно true
        assertThat(response.jsonPath().get("ok"), is(true));
        // Авторизуемся и сохраняем ID курьера
        courierId = courierMethods.authorizeCourier(DEFAULT_LOGIN, DEFAULT_PASSWORD);
    }

    // Тест - нельзя создать двух одинаковых курьеров
    // и если создать пользователя с существующим логиномц, возвращается ошибка
    @Test
    @Story("Prevent duplicate courier creation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that creating a courier with the same login returns an error")
    public void testErrorCreateTheSameCourier() {
        // Создаем тело запроса для создания курьера с дефолтными данными
        String body = courierMethods.createRequestBody("client", "1234", "clipa");
        // Отправляем первый запрос для создания курьера
        Response response = courierMethods.createCourier(body);
        // Форматируем тело ответа с помощью метода из CourierHelper
        String formattedResponseBody = courierMethods.formatResponseBody(response.getBody().asString());
        // Выводим отформатированное тело ответа
        System.out.println("Formatted response body: " + formattedResponseBody);
        // Проверяем успешное создание курьера с кодом ответа 201
        courierMethods.checkStatusCode(response, 201);
        System.out.println("Курьер успешно создан. Код ответа: " + response.getStatusCode());
        // Отправляем второй запрос с теми же данными, чтобы попытаться создать дубликат курьера
        Response secondResponse = courierMethods.createCourier(body);
        // Форматируем тело ответа второго запроса
        String formattedSecondResponseBody = courierMethods.formatResponseBody(secondResponse.getBody().asString());
        // Выводим отформатированное тело ответа второго запроса
        System.out.println("Formatted response body (duplicate creation attempt): " + formattedSecondResponseBody);
        // Проверяем код ответа 409 для ошибки дубликата
        courierMethods.checkStatusCode(secondResponse, 409);
        // Ожидаемое сообщение об ошибке
        String expectedMessage = "Этот логин уже используется. Попробуйте другой.";
        // Проверяем правильное сообщение об ошибке
        courierMethods.checkErrorMessage(secondResponse, expectedMessage);
        // Авторизуемся и получаем ID курьера для проверки
        courierId = courierMethods.authorizeCourier("client", "1234");
        // Проверяем, что ID курьера получен корректно (ID не должен быть -1)
        assertThat(courierId, is(not(-1)));
    }

    // Тест чтобы создать курьера, нужно передать в ручку все обязательные поля
    @Test
    @Story("Validate required fields for courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that all required fields are present when creating a courier")
    public void testCreateCourierWithAllRequiredFields() {
        // Создаем тело запроса для создания курьера с дефолтными данными
        String body = courierMethods.createRequestBody("client", "1234", "clipa");
        // Отправляем первый запрос для создания курьера
        Response response = courierMethods.createCourier(body);// Отправляем первый запрос для создания курьера
        // Форматируем тело ответа с помощью метода из CourierHelper
        String formattedResponseBody = courierMethods.formatResponseBody(response.getBody().asString());
        // Выводим отформатированное тело ответа
        System.out.println("Formatted response body: " + formattedResponseBody);
        // Выводим код ответа на экран
        System.out.println("Status code: " + response.getStatusCode());
        // Проверяем код ответа
        courierMethods.checkStatusCode(response, 201);
        // Проверяем, что в теле ответа поле "ok" равно true
        assertThat(response.jsonPath().get("ok"), is(true));
        // Авторизуемся и сохраняем ID курьера
        courierId = courierMethods.authorizeCourier(DEFAULT_LOGIN, DEFAULT_PASSWORD);
    }

    // Тест на запрос создание курьера, возвращает правильный код ответа
    @Test
    @Story("Validate status code 201 for successful courier creation")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify that creating a new courier returns status code 201")
    public void testCreateCourierCode201() {
        String body = courierMethods.createRequestBody(DEFAULT_LOGIN, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);
        Response response = courierMethods.createCourier(body);
        // Выводим код ответа на экран
        System.out.println("Status code: " + response.getStatusCode());
        // Проверяем код ответа
        courierMethods.checkStatusCode(response, 201);
        // Авторизуемся и сохраняем ID курьера
        courierId = courierMethods.authorizeCourier(DEFAULT_LOGIN, DEFAULT_PASSWORD);
    }

    // Тест на успешный запрос создания курьера, возвращает ok: true
    @Test
    @Story("Validate 'ok: true' for successful courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a successful courier creation returns 'ok: true' in the response")
    public void testCreateCourierOkTrue() {
        String body = courierMethods.createRequestBody(DEFAULT_LOGIN, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME);
        Response response = courierMethods.createCourier(body);
        // Форматируем тело ответа с помощью метода из CourierHelper
        String formattedResponseBody = courierMethods.formatResponseBody(response.getBody().asString());
        // Выводим отформатированное тело ответа
        System.out.println("Formatted response body: " + formattedResponseBody);
        // Авторизуемся и сохраняем ID курьера
        courierId = courierMethods.authorizeCourier(DEFAULT_LOGIN, DEFAULT_PASSWORD);
    }

    // Тест если одного из полей нет, запрос возвращает ошибку
    // Тест 1: Пропущено поле login
    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a login returns an error")
    public void testCreateCourierWithoutLogin() {
        // Создаем тело запроса без поля login
        String bodyWithoutLogin = "{ \"password\": \"1234\", \"firstName\": \"clipa\" }";
        // Сообщение об ошибке
        String expectedMessage = "Недостаточно данных для создания учетной записи";
        // Отправляем запрос для создания курьера без логина
        Response response = courierMethods.createCourier(bodyWithoutLogin);
        // Форматируем тело ответа с помощью метода из CourierHelper
        String formattedResponseBody = courierMethods.formatResponseBody(response.getBody().asString());
        // Выводим отформатированное тело ответа
        System.out.println("Formatted response body (missing login): " + formattedResponseBody);
        // Проверяем код ответа 400 для ошибки
        courierMethods.checkStatusCode(response, 400);
        // Выводим сообщение об ошибке
        System.out.println("Курьер не создан: пропущено поле login");
        // Проверяем правильное сообщение об ошибке
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    // Тест если одного из полей нет, запрос возвращает ошибку
    // Тест 2: Пропущено поле password
    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a password returns an error")
    public void testCreateCourierWithoutPassword() {

        String bodyWithoutLogin = "{ \"login\": \"client\", \"firstName\": \"clipa\" }";
        // Сообщение об ошибке
        String expectedMessage = "Недостаточно данных для создания учетной записи";
        // Отправляем запрос для создания курьера без логина
        Response response = courierMethods.createCourier(bodyWithoutLogin);
        // Форматируем тело ответа с помощью метода из CourierHelper
        String formattedResponseBody = courierMethods.formatResponseBody(response.getBody().asString());
        // Выводим отформатированное тело ответа
        System.out.println("Formatted response body (missing login): " + formattedResponseBody);
        // Проверяем код ответа 400 для ошибки
        courierMethods.checkStatusCode(response, 400);
        // Выводим сообщение об ошибке
        System.out.println("Курьер не создан: пропущено поле password");
        // Проверяем правильное сообщение об ошибке
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    // Тест 3: Пропущено поле firstName
    // Тест не проходит
    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a first name returns an error")
    public void testCreateCourierWithoutFirstName() {
        // Создаем тело запроса без поля login
        String bodyWithoutLogin = "{ \"login\": \"client\", \"password\": \"1234\" }";
        // Сообщение об ошибке
        String expectedMessage = "Недостаточно данных для создания учетной записи";
        // Отправляем запрос для создания курьера без логина
        Response response = courierMethods.createCourier(bodyWithoutLogin);
        // Форматируем тело ответа с помощью метода из CourierHelper
        String formattedResponseBody = courierMethods.formatResponseBody(response.getBody().asString());
        // Выводим отформатированное тело ответа
        System.out.println("Formatted response body (missing login): " + formattedResponseBody);
        // Проверяем код ответа 400 для ошибки
        courierMethods.checkStatusCode(response, 400);
        // Выводим сообщение об ошибке
        System.out.println("Курьер не создан: пропущено поле firstName");
        // Проверяем правильное сообщение об ошибке
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }
}


