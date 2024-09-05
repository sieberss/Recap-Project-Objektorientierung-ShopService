import java.util.List;

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

        try{
            shopService.addOrder(List.of("2", "4", "5") );
            shopService.addOrder(List.of("3", "6", "7") );
            shopService.addOrder(List.of("4", "8", "1") );
        }
        catch(ProductNotAvailableException e){
            System.out.println(e.getMessage());;
        }

        System.out.println(shopService.getAllOrdersWithStatus(OrderStatus.PROCESSING));
        System.out.println(shopService.getOldestOrderPerStatus());
    }
}
