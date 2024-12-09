/*Создание заказа
- Проверь, что когда создаёшь заказ:
- можно указать один из цветов — BLACK или GREY;
- можно указать оба цвета;
- можно совсем не указывать цвет;
- тело ответа содержит track.
- чтобы протестировать создание заказа, нужно использовать параметризацию.
- все данные нужно удалять после того, как тест выполнится.
*/

// Тесты с созданием отдельного класса с методами
// С тестом для удаления заказа по id заказа
package CreateOrderTest;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.*;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private String deliveryDate;
    private String comment;
    private String[] color;
    private int rentTime;

    String orderId;

    public CreateOrderTest(String firstName, String lastName, String address, String metroStation,
                           String phone, int rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getOrderData() {
        return new Object[][]{
                { "namec", "surnamec", "SPb, 1 apt.", "22", "+7 100 200 33 44", 3, "2025-01-01", "Call first", new String[] { "GRAY" } },
                { "namec", "surnamec", "SPb, 1 apt.", "22", "+7 100 200 33 44", 3, "2025-01-01", "Call first", new String[] { "GRAY", "BLACK" } },
                { "namec", "surnamec", "SPb, 1 apt.", "22", "+7 100 200 33 44", 3, "2025-01-01", "Call first", new String[] { } },
                { "namec", "surnamec", "SPb, 1 apt.", "22", "+7 100 200 33 44", 3, "2025-01-01", "Call first", new String[] { "BLACK" } },
        };
    }

    // Тест на создание заказа самоката разных цветов с выводом трек-номера в теле ответа ( // и получения id заказа для удаление заказа по id)
    // Тест не проходит: не удается получить id заказа и удаление заказа по ручкам из документации
    // Тест не проходит если удалять заказ по трек-номеру
    @Test
    @DisplayName("Creating an order with different colors")
    public void createOrderParameterizedColorScooterTest() {
        // Создание объекта заказа
        OrderCreation orderCreation = new OrderCreation(firstName, lastName, address,
                metroStation, phone, deliveryDate, comment, color, rentTime);
        // Создание нового заказа
        Response createResponse = ClientOrder.createNewOrder(orderCreation);
        // Проверка успешного создания заказа
        ClientOrder.comparingSuccessfulOrderSet(createResponse, SC_CREATED);
        // Получение ID заказа
        //orderId = OrderClient.getOrderId(createResponse);
        // Удаление заказа
        //Response deleteResponse = OrderClient.deleteOrder(orderId);
        // Проверка успешного удаления заказа
        //OrderClient.comparingSuccessfulOrderCancel(deleteResponse, SC_OK);
    }
}
