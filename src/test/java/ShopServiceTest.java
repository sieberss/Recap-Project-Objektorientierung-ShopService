import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShopServiceTest {

    @Test
    void addOrderTest() throws ProductNotAvailableException {
        //GIVEN
        ShopService shopService = new ShopService();
        List<String> productsIds = List.of("1");

        //WHEN
        Order actual = shopService.addOrder(productsIds);

        //THEN
        Order expected = new Order("-1", List.of(new Product("1", "Apfel")));
        assertEquals(expected.products(), actual.products());
        assertEquals(expected.status(), actual.status());
        assertNotNull(expected.id());
        //assert that timestamp for new order is within previous second
        assertTrue(Instant.now().isAfter(actual.createdAt()));
        assertTrue(Instant.now().minus(1, ChronoUnit.SECONDS).isBefore(actual.createdAt()));
    }

    @Test
    void addOrderTest_whenInvalidProductId_expectProductNotAvailableException() {
        //GIVEN
        ShopService shopService = new ShopService();
        List<String> productsIds = List.of("1", "2");

        //WHEN
        //THEN
        assertThrows(ProductNotAvailableException.class, ()->shopService.addOrder(productsIds));
    }

    @Test
    void updateOrderTest() throws ProductNotAvailableException {
        //GIVEN
        ShopService shopService = new ShopService();
        List<String> productsIds = List.of("1");

        //WHEN
        Order old = shopService.addOrder(productsIds);
        shopService.updateOrder(old.id(), OrderStatus.COMPLETED);
        List<Order> completed = shopService.getAllOrdersWithStatus(OrderStatus.COMPLETED);

        //THEN
        Order actual = completed.get(0);
        assertEquals(old.products(), actual.products());
        assertEquals(old.id(), actual.id());

    }
}
