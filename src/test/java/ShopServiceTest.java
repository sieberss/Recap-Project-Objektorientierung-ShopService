import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShopServiceTest {

    @Test
    void addOrderTest() throws ProductNotAvailableException {
        //GIVEN
        ShopService shopService = new ShopService(new ProductRepo(), new OrderMapRepo(), new IdService());
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
        ShopService shopService = new ShopService(new ProductRepo(), new OrderMapRepo(), new IdService());
        List<String> productsIds = List.of("1", "2");

        //WHEN
        //THEN
        assertThrows(ProductNotAvailableException.class, ()->shopService.addOrder(productsIds));
    }

    @Test
    void updateOrderTest() throws ProductNotAvailableException {
        //GIVEN
        ShopService shopService = new ShopService(new ProductRepo(), new OrderMapRepo(), new IdService());
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

    @Test
    void getAllOrdersWithStatus() throws ProductNotAvailableException {
        ShopService shopService = new ShopService(new ProductRepo(), new OrderMapRepo(), new IdService());
        List<String> productsIds = List.of("1");

        //WHEN
        Order order1 = shopService.addOrder(productsIds);
        Order order2 = shopService.addOrder(productsIds);
        List<Order> processing = shopService.getAllOrdersWithStatus(OrderStatus.PROCESSING);

        //THEN after creation
        assertEquals(List.of(order1,order2), processing);
        assertEquals(List.of(), shopService.getAllOrdersWithStatus(OrderStatus.COMPLETED));
        assertEquals(List.of(), shopService.getAllOrdersWithStatus(OrderStatus.IN_DELIVERY));

        //update status
        order1 = shopService.updateOrder(order1.id(), OrderStatus.IN_DELIVERY);
        List<Order> inDelivery = shopService.getAllOrdersWithStatus(OrderStatus.IN_DELIVERY);
        processing = shopService.getAllOrdersWithStatus(OrderStatus.PROCESSING);
        //compare result after update
        assertEquals(List.of(order2), processing);
        assertEquals(List.of(order1), inDelivery);
    }

    @Test
    void getOldestOrderPerStatus() throws ProductNotAvailableException {
        ShopService shopService = new ShopService(new ProductRepo(), new OrderMapRepo(), new IdService());
        List<String> productsIds = List.of("1");

        //WHEN
        Order order1 = shopService.addOrder(productsIds);
        Order order2 = shopService.addOrder(productsIds);
        Order order3 = shopService.addOrder(productsIds);
        Order order4 = shopService.addOrder(productsIds);
        Order order5 = shopService.addOrder(productsIds);
        Order order6 = shopService.addOrder(productsIds);
        order1 = shopService.updateOrder(order1.id(), OrderStatus.IN_DELIVERY);
        order2 = shopService.updateOrder(order2.id(), OrderStatus.IN_DELIVERY);
        order3 = shopService.updateOrder(order3.id(), OrderStatus.COMPLETED);
        order4 = shopService.updateOrder(order4.id(), OrderStatus.COMPLETED);
        Map<OrderStatus,Order> oldestOrders = shopService.getOldestOrderPerStatus();

        //THEN
        assertEquals(oldestOrders.get(OrderStatus.COMPLETED), order3);
        assertEquals(oldestOrders.get(OrderStatus.IN_DELIVERY), order1);
        assertEquals(oldestOrders.get(OrderStatus.PROCESSING), order5);
    }

}
