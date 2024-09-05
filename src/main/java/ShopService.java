import lombok.RequiredArgsConstructor;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ShopService {
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;
    private final IdService idService;

    public Order addOrder(List<String> productIds) throws ProductNotAvailableException {
        List<Product> products = new ArrayList<>();
        for (String productId : productIds) {
            Optional<Product> productToOrder = productRepo.getProductById(productId);
            if (productToOrder.isEmpty()) {
                throw new ProductNotAvailableException("Product mit der Id: " + productId + " konnte nicht bestellt werden!");
            }
            products.add(productToOrder.get());
        }
        Order newOrder = new Order(idService.generateId(), products, Instant.now(), OrderStatus.PROCESSING);
        return orderRepo.addOrder(newOrder);
    }

    public void updateOrder(String orderId, OrderStatus newStatus) {
        Order oldOrder = orderRepo.getOrderById(orderId);
        Order newOrder = oldOrder.withStatus(newStatus);
        orderRepo.removeOrder(orderId);
        orderRepo.addOrder(newOrder);
    }
    public List<Order> getAllOrdersWithStatus(OrderStatus status) {
        return orderRepo.getOrders().stream()
                .filter(order -> order.status() == status)
                .collect(Collectors.toList());
    }

    public Map<OrderStatus, Order> getOldestOrderPerStatus() {
        Map<OrderStatus, Order> oldestOrders = new HashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            Order oldestOrder = getAllOrdersWithStatus(status).stream()
                    .min((a,b)->a.createdAt().compareTo(b.createdAt())).orElse(null);
            oldestOrders.put(status, oldestOrder);
        }
        return oldestOrders;
    }
}
