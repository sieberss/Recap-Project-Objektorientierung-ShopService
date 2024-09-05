import lombok.With;

import java.time.Instant;
import java.util.List;

public record Order(
        String id,
        List<Product> products,
        Instant createdAt,
        @With
        OrderStatus status
) {

    public Order(String id, List<Product> products) {
        this(id, products, Instant.now(), OrderStatus.PROCESSING);
    }

}
