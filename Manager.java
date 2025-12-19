import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import data.Answer;
import data.Product;
import data.Store;
import data.StoreParser;
import data.Task;

public class Manager implements ResponseHandler {

    @Override
    public void handleResponseFromServer(Answer res) {
        // TODO Auto-generated method stub
        System.out.println("Response from Master: "+res);
    }

    public void sendToMaster(Task task)  {
       (new Client<Manager>(task, GlobalConfig.MASTER_HOST_IP, GlobalConfig.MASTER_PORT_FOR_CLIENTS, this))
       .start();
    }

    private void loadStoresFromJSONFiles() {
        String[] jsonFiles = {
            "res/store1.json",
            "res/store2.json",
            "res/store3.json",
            "res/store4.json",
            "res/store5.json"
        };

        for (String jsonFile : jsonFiles) {
            try {
                String json = StoreParser.jsonFileToString(jsonFile);
                Store store = StoreParser.createStoreFromJSONString(json);
                this.sendToMaster(
                    new Task(0, GlobalConfig.ADD_STORE, true, 
                    Map.of(
                        "store", store,
                        "storeName", store.getStoreName()
                        ) 
                    )
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void test(Manager manager) throws Exception{
        String json = StoreParser.jsonFileToString("res/store1.json");

        String json2 = StoreParser.jsonFileToString("res/store2.json");
        String json3 = StoreParser.jsonFileToString("res/store3.json");
        String json4 = StoreParser.jsonFileToString("res/store4.json");
        String json5 = StoreParser.jsonFileToString("res/store5.json");

        Store store = StoreParser.createStoreFromJSONString(json);
        Store store2 = StoreParser.createStoreFromJSONString(json2);
        Store store3 = StoreParser.createStoreFromJSONString(json3);
        Store store4 = StoreParser.createStoreFromJSONString(json4);
        Store store5 = StoreParser.createStoreFromJSONString(json5);
        // manager.sendToMaster(new Task(0, GlobalConfig.ADD_STORE, true, Map.of("json", json) ));
        // manager.sendToMaster(new Task(0, GlobalConfig.ADD_STORE, true, Map.of("json", json2)));
        Thread.sleep(1000);

        manager.sendToMaster(
            new Task(0, GlobalConfig.ADD_STORE, true, 
            Map.of(
                "store", store,
                "storeName", store.getStoreName()
                ) 
            )
        );
        manager.sendToMaster(
            new Task(0, GlobalConfig.ADD_STORE, true, 
            Map.of(
                "store", store2,
                "storeName", store2.getStoreName()
                ) 
            )
        ); 
        manager.sendToMaster(
            new Task(0, GlobalConfig.ADD_STORE, true, 
            Map.of(
                "store", store3,
                "storeName", store3.getStoreName()
                ) 
            )
        );

        manager.sendToMaster(
            new Task(0, GlobalConfig.CHANGE_STOCK, true,
            Map.of(
                "storeName", "Burger Palace",
                "productName", "Crispy Fries",
                "quantity", -100
                )
            )
        );
    

        manager.sendToMaster(
            new Task(3, GlobalConfig.ADD_STORE, true, 
            Map.of(
                "store", store4,
                "storeName", store4.getStoreName()
                ) 
            )
        );
        manager.sendToMaster(
            new Task(4, GlobalConfig.ADD_STORE, true, 
            Map.of(
                "store", store5,
                "storeName", store5.getStoreName()
                ) 
            )
        );

        // Thread.sleep(1000);
        // manager.sendToMaster(
        //     new Task(5, GlobalConfig.REMOVE_PRODUCT, true,
        //     Map.of(
        //         "storeName", "Coffee Hub",
        //         "productName", "Blueberry Muffin"
        //         )
        //     )
        // );


        
        // manager.sendToMaster(
        //     new Task(6, GlobalConfig.ADD_PRODUCT, true,
        //     Map.of(
        //         "storeName", "Burger Palace",
        //         "product", new Product("Dedicon", "Food", 10, 7.5)
        //         )
        //     )
        // ); 
        // Thread.sleep(1000);
        // manager.sendToMaster(
        //     new Task(7, GlobalConfig.BUY, true,
        //     Map.of(
        //         "storeName", "Burger Palace",
        //         "productName", "Double Cheeseburger",
        //         "quantity", 2
        //         )
        //     )
        // );
        // manager.sendToMaster(
        //     new Task(7, GlobalConfig.BUY, true,
        //     Map.of(
        //         "storeName", "Burger Palace",
        //         "productName", "Double Cheeseburger",
        //         "quantity", 2
        //         )
        //     )
        // );
       
        // manager.sendToMaster(
        //     new Task(7, GlobalConfig.BUY, true,
        //     Map.of(
        //         "storeName", "Sushi Spot",
        //         "productName", "Salmon Nigiri",
        //         "quantity", 10
        //         )
        //     )
        // );
        // Thread.sleep(1000);
        // manager.sendToMaster(
        //     new Task(0, GlobalConfig.FILTER_STORES, false, 
        //     Map.of(
        //         "category", "",
        //         "stars", 0,
        //         "price", "",
        //         "latitude", 0.0,
        //         "longitude", 0.0
        //         ) 
        //     )
        // );
    //  Thread.sleep(1000);
    //     manager.sendToMaster(
    //         new Task(9, GlobalConfig.GET_SALES_PER_PRODUCT, false, 
    //         null)
    //     );

        // Thread.sleep(1000);
        // manager.sendToMaster(new Task(GlobalConfig.BUY, true, 
        //     Map.of(
        //         "storeName", "Burger Palace",
        //         "productName", "Double Cheeseburger",
        //         "quantity", 2
        //     )
        // ));

        // Thread.sleep(2000);
        // manager.sendToMaster(
        //     new Task(9, GlobalConfig.GET_SALES_PER_PRODUCT_CATEGORY, false, 
        //     Map.of(
        //         "category", "Food"
        //         ) 
        //     )
        // );
        // Thread.sleep(2000);
        // manager.sendToMaster(
        //     new Task(9, GlobalConfig.GET_SALES_PER_FOOD_CATEGORY, false, 
        //     Map.of(
        //         "category", "Burger"
        //         ) 
        //     )
        // );

    }

    public void handleAddProduct(Scanner scanner) {
        System.out.print("Enter store name: ");
        String storeName = scanner.nextLine().trim();

        System.out.print("Enter product name: ");
        String productName = scanner.nextLine().trim();

        System.out.print("Enter product type: ");
        String productType = scanner.nextLine().trim();

        System.out.print("Enter product price: ");
        double price = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Enter available amount: ");
        int availableAmount = Integer.parseInt(scanner.nextLine().trim());

        Product product = new Product(productName, productType, availableAmount, price);
        this.sendToMaster(
            new Task(0, GlobalConfig.ADD_PRODUCT, true, 
            Map.of(
                "storeName", storeName,
                "product", product
                ) 
            )
        );
    }

    private void handleInsertStore(Scanner scanner) {
        System.out.print("Enter JSON filepath: ");
        String path = scanner.nextLine().trim();
        try {
            String json = StoreParser.jsonFileToString(path);
            Store store = StoreParser.createStoreFromJSONString(json);
            this.sendToMaster(
            new Task(1, GlobalConfig.ADD_STORE, true, 
            Map.of(
                "store", store,
                "storeName", store.getStoreName()
                ) 
            )
        ); ;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void handleRemoveProduct(Scanner scanner) {
        System.out.print("Enter store name: ");
        String storeName = scanner.nextLine().trim();

        System.out.print("Enter product name: ");
        String productName = scanner.nextLine().trim();

        this.sendToMaster(
            new Task(2, GlobalConfig.REMOVE_PRODUCT, true, 
            Map.of(
                "storeName", storeName,
                "productName", productName
                ) 
            )
        );

    }

    public void handleGetSalesPerProductCategory(Scanner scanner) {
        System.out.print("Enter product category: ");
        String category = scanner.nextLine().trim();

        this.sendToMaster(
            new Task(3, GlobalConfig.GET_SALES_PER_PRODUCT_CATEGORY, false, 
            Map.of(
                "category", category
                ) 
            )
        );
    }

     public void handleGetSalesPerFoodCategory(Scanner scanner) {
        System.out.print("Enter food category: ");
        String category = scanner.nextLine().trim();

        this.sendToMaster(
            new Task(4, GlobalConfig.GET_SALES_PER_FOOD_CATEGORY, false, 
            Map.of(
                "category", category
                ) 
            )
        );
     }

    public void waitForUserInput(Scanner scanner) {
        System.out.print("\nEnter command (ADD_STORE, ADD_PRODUCT, REMOVE_PRODUCT, GET_SALES_PER_PRODUCT_CATEGORY, GET_SALES_PER_FOOD_CATEGORY, GET_SALES_PER_PRODUCT, CHANGE_STOCK, EXIT ): ");
        String command = scanner.nextLine().trim();
        switch (command.toUpperCase()) {
            case "ADD_STORE" -> handleInsertStore(scanner);
            case "ADD_PRODUCT" -> handleAddProduct(scanner);
            case "REMOVE_PRODUCT" -> handleRemoveProduct(scanner);
            case "GET_SALES_PER_PRODUCT" -> handleGetProductsSales();
            case "GET_SALES_PER_PRODUCT_CATEGORY" -> handleGetSalesPerProductCategory(scanner);
            case "GET_SALES_PER_FOOD_CATEGORY" -> handleGetSalesPerFoodCategory(scanner);
            case "CHANGE_STOCK" -> handleChangeStock(scanner);
            case "EXIT" -> System.exit(0);
            default -> System.out.println("Invalid command.");
        }
    }

    private void handleGetProductsSales() {
         this.sendToMaster(
            new Task(9, GlobalConfig.GET_SALES_PER_PRODUCT, false, 
            null)
        );
        
    }

    private void handleChangeStock(Scanner scanner) {
        System.out.print("Enter store name: ");
        String storeName = scanner.nextLine().trim();

        System.out.print("Enter product name: ");
        String productName = scanner.nextLine().trim();

        System.out.print("Enter quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine().trim());

        this.sendToMaster(
            new Task(0, GlobalConfig.CHANGE_STOCK, true, 
            Map.of(
                "storeName", storeName,
                "productName", productName,
                "quantity", quantity
                ) 
            )
        );
        return;
    }

    public static void main(String args[]) throws Exception {
        Manager manager = new Manager();
        test(manager);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            manager.waitForUserInput(scanner);
        }

        
	}

}
