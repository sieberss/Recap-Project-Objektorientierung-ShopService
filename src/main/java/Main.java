import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        OrderRepo orderRepo = new OrderMapRepo();
        ProductRepo productRepo = new ProductRepo();
        ShopService shopService = new ShopService(productRepo, orderRepo, new IdService());
        productRepo.addProduct(new Product("2", "Banana"));
        productRepo.addProduct(new Product("3", "Cherry"));
        productRepo.addProduct(new Product("4", "Orange"));
        productRepo.addProduct(new Product("5", "Pear"));
        productRepo.addProduct(new Product("6", "Pineapple"));
        productRepo.addProduct(new Product("7", "Mango"));
        productRepo.addProduct(new Product("8", "Plum"));

      /*  try{
            shopService.addOrder(List.of("2", "4", "5") );
            shopService.addOrder(List.of("3", "6", "7") );
            shopService.addOrder(List.of("4", "8", "1") );
        }
        catch(ProductNotAvailableException e){
            System.out.println(e.getMessage());;
        }

        System.out.println(shopService.getAllOrdersWithStatus(OrderStatus.PROCESSING));
        System.out.println(shopService.getOldestOrderPerStatus());*/


        processTransactionFile(shopService, orderRepo);
    }

    private static void processTransactionFile(ShopService shopService, OrderRepo orderRepo) {
        Path  transactionFile = Path.of("transactions.txt");
        Map<String,String> idToAlias = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(transactionFile);
            for (String line : lines) {
                String[] parts = line.split(" ");
                switch (parts[0]) {
                    case "addOrder":
                        String id = getIdForNewOrder(shopService, parts);
                        idToAlias.put(parts[1], id);
                        break;
                    case "setStatus":
                        shopService.updateOrder(idToAlias.get(parts[1]), OrderStatus.valueOf(parts[2]));
                        break;
                    case "printOrders":
                        orderRepo.getOrders().forEach(System.out::println);
                        break;
                }
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());;
        }
    }

    private static String getIdForNewOrder(ShopService shopService, String[] parts) {
        try{
            Order order = shopService.addOrder(Arrays.asList(parts).subList(2, parts.length));
            return order.id();
        }
        catch (ProductNotAvailableException e){
            System.out.println(e.getMessage());;
        }
        return null;
    }
}
