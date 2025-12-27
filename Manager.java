import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Arrays;

import data.Answer;
import data.Product;
import data.Store;
import data.StoreParser;
import data.Task;

public class Manager implements ResponseHandler {

    @Override
    public void handleResponseFromServer(Answer res) {
        // TODO Auto-generated method stub
        System.out.println("Response from Master: "+
            GlobalConfig.getCommandName(res.type)+", id:"+ res.ID+"\n" + res.message+"\n"+res.arguments
        );
    }

    public void sendToMaster(Task task)  {
       (new Client<Manager>(task, GlobalConfig.MASTER_HOST_IP, GlobalConfig.MASTER_PORT_FOR_CLIENTS, this))
       .start();
    }

    private void loadStoresFromJSONFiles(String[] jsonFileNames) {
        for (String jsonFile : jsonFileNames) {
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

    private void applyTasks(Task[] tasks) {
        for (Task task : tasks) {
            this.sendToMaster(task);
        }
        
    }


    public void test() throws Exception{
       loadStoresFromJSONFiles(new String[] {
            "res/store1.json",
            "res/store2.json",
            "res/store3.json",
            "res/store4.json",
            "res/store5.json",
            "res/store6.json",
            "res/store7.json",
            "res/store8.json",
            "res/store9.json",
            "res/store10.json",
            "res/store11.json"
        });
        Thread.sleep(1000); // Wait for stores to be added

        System.out.println("-------------------------------\n\n");

        applyTasks(new Task[] {
                // new Task(7, GlobalConfig.BUY, true,
                //     Map.of(
                //         "storeName", "Burger Palace",
                //         "productName", "Double Cheeseburger",
                //         "quantity", 2
                //     )
                // ),
                // new Task(7, GlobalConfig.BUY, true,
                //     Map.of(
                //         "storeName", "Burger Palace",
                //         "productName", "Double Cheeseburger",
                //         "quantity", 2
                //     )
                // ),
                // new Task(7, GlobalConfig.BUY, true,
                //     Map.of(
                //         "storeName", "Sushi Spot",
                //         "productName", "Salmon Nigiri",
                //         "quantity", 10
                //     )
                // ),
                // new Task(7, GlobalConfig.BUY, true,
                //     Map.of(
                //         "storeName", "Sushi Spot",
                //         "productName", "Salmon Nigiri",
                //         "quantity", 10
                //     )
                // ),
                // new Task(7, GlobalConfig.BUY, true,
                //     Map.of(
                //         "storeName", "Sus Spot",
                //         "productName", "Salmon Nigiri",
                //         "quantity", 10
                //     )
                // ),
                // new Task(7, GlobalConfig.BUY, true,
                //     Map.of(
                //         "storeName", "Sushi Spot",
                //         "productName", "Salmoigiri",
                //         "quantity", 10
                //     )
                // ),
        });

        Thread.sleep(1000); // Wait for purchases to be processed

         applyTasks(new Task[] {
            // new Task(0, GlobalConfig.CHANGE_STOCK, true,
            //         Map.of(
            //             "storeName", "Burger Palace",
            //             "productName", "Crispy Fries",
            //             "quantity", -3
            //         )
            //     ),
            //     new Task(5, GlobalConfig.REMOVE_PRODUCT, true,
            //         Map.of(
            //             "storeName", "Coffee Hub",
            //             "productName", "Blueberry Muffin"
            //         )
            //     ),
            //     new Task(6, GlobalConfig.ADD_PRODUCT, true,
            //         Map.of(
            //             "storeName", "Burger Palace",
            //             "product", new Product("Dedicon", "Food", 10, 7.5)
            //         )
            //     ),

                // new Task(0, GlobalConfig.FILTER_STORES, false,
                //     Map.of(
                //         "name", "Burger Palace",
                //         "categories", (Serializable) List.of(),
                //         "stars", (Serializable) List.of(),
                //         "price", (Serializable) List.of(),
                //         "latitude", 0.0,
                //         "longitude", 0.0
                //     )
                // ),

         });
        //  Thread.sleep(1000); // Wait for tasks to be processed
        //  applyTasks(new Task[]{
        //         new Task(9, GlobalConfig.GET_SALES_PER_PRODUCT, false, null),
        //         new Task(9, GlobalConfig.GET_SALES_PER_PRODUCT_CATEGORY, false,
        //             Map.of("category", "Food")
        //         ),
        //         new Task(9, GlobalConfig.GET_SALES_PER_FOOD_CATEGORY, false,
        //             Map.of("category", "Burger")
        //         )
        //  });

        

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
        System.out.print("\nEnter command (ADD_STORE, FILTER, ADD_PRODUCT, REMOVE_PRODUCT, GET_STORE_SALES_PER_PRODUCT_CATEGORY, GET_STORE_SALES_PER_FOOD_CATEGORY, GET_SALES_PER_PRODUCT, ADD_STOCK, EXIT ): ");
        String command = scanner.nextLine().trim();
        switch (command.toUpperCase()) {
            case "ADD_STORE" -> handleInsertStore(scanner);
            case "FILTER" -> handleFilterStores(scanner);
            case "ADD_PRODUCT" -> handleAddProduct(scanner);
            case "REMOVE_PRODUCT" -> handleRemoveProduct(scanner);
            case "GET_SALES_PER_PRODUCT" -> handleGetProductsSales();
            case "GET_STORE_SALES_PER_PRODUCT_CATEGORY" -> handleGetSalesPerProductCategory(scanner);
            case "GET_STORE_SALES_PER_FOOD_CATEGORY" -> handleGetSalesPerFoodCategory(scanner);
            case "ADD_STOCK" -> handleChangeStock(scanner);
            case "EXIT" -> System.exit(0);
            default -> System.out.println("Invalid command.");
        }
    }

    private void handleFilterStores(Scanner scanner) {
        System.out.print("Enter store name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter categories (comma separated): ");
        String categories = scanner.nextLine().trim();

        System.out.print("Enter stars (comma separated): ");
        String stars = scanner.nextLine().trim();

        System.out.print("Enter price categories (comma separated): ");
        String price = scanner.nextLine().trim();

        List<String> categoryList = categories.isBlank()
                ? List.of()
                : Arrays.stream(categories.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();

        List<Integer> starList = stars.isBlank()
                ? List.of()
                : Arrays.stream(stars.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .toList();

        List<Integer> priceList = price.isBlank()
                ? List.of()
                : Arrays.stream(price.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .toList();

        this.sendToMaster(
            new Task(0, GlobalConfig.FILTER_STORES, false,
                Map.of(
                    "name", name,
                    "categories", (Serializable) categoryList,
                    "stars", (Serializable) starList,
                    "price", (Serializable) priceList,
                    "latitude", 0.0,
                    "longitude", 0.0
                )
            )
        );
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

    private void setupDummyUserInterface() {
       Scanner scanner = new Scanner(System.in);
        while (true) {
            waitForUserInput(scanner);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) throws Exception {
        Manager manager = new Manager();
        manager.test();

        manager.setupDummyUserInterface();

        
	}

}
