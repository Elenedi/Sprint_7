// Тесты с частично вынесенными методами в отдельный класс

/*Создание курьера
Проверь:
- курьера можно создать;
- нельзя создать двух одинаковых курьеров;
- чтобы создать курьера, нужно передать в ручку все обязательные поля;
- запрос возвращает правильный код ответа;
- успешный запрос возвращает ok: true;
- если одного из полей нет, запрос возвращает ошибку;
- если создать пользователя с логином, который уже есть, возвращается ошибка.
- все данные нужно удалять после того, как тест выполнится.
*/

package org.courier.test;
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
public class CourierTest {

    private Gson gson;
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
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Test
    @Story("Create a new courier")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a new courier is possible and returns the correct response")
    public void testCreateCourierIsPossible() {
        String body = courierMethods.createRequestBody("client", "1234", "clipa");
        Response response = courierMethods.createCourier(body);
        courierMethods.checkStatusCode(response, 201);
        assertThat(response.jsonPath().get("ok"), is(true));
        courierId = courierMethods.getCourierId("client", "1234");
        assertThat(courierId, is(not(-1)));
    }
}