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
package org.create.order.test;
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
                {"namec", "surnamec", "SPb, 1 apt.", "22", "+7 100 200 33 44", 3, "2025-01-01", "Call first", new String[]{"GRAY"}},
                {"namec", "surnamec", "SPb, 1 apt.", "22", "+7 100 200 33 44", 3, "2025-01-01", "Call first", new String[]{"GRAY", "BLACK"}},
                {"namec", "surnamec", "SPb, 1 apt.", "22", "+7 100 200 33 44", 3, "2025-01-01", "Call first", new String[]{}},
                {"namec", "surnamec", "SPb, 1 apt.", "22", "+7 100 200 33 44", 3, "2025-01-01", "Call first", new String[]{"BLACK"}},
        };
    }

    @Test
    @DisplayName("Creating an order with different colors")
    public void createOrderParameterizedColorScooterTest() {
        OrderCreation orderCreation = new OrderCreation(firstName, lastName, address,
                metroStation, phone, deliveryDate, comment, color, rentTime);

        Response createResponse = ClientOrder.createNewOrder(orderCreation);
        ClientOrder.comparingSuccessfulOrderSet(createResponse, SC_CREATED);
    }
}
