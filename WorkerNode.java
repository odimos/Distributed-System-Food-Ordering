import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Answer;
import data.Product;
import data.Sale;
import data.Store;
import data.StoreParser;
import data.Task;

public class WorkerNode implements RequestHandler, ResponseHandler {

    private final Map<String, Store> stores;
    int id;
    public WorkerNode(int id){
        this.id = id;
        this.stores = new HashMap<>();
    }

    // Operations
    public String addStore(Store store) {
        String name =store.getStoreName();
        synchronized(stores){
            if (stores.containsKey(name)){
                return "Store" + name + " already there";
            }
            stores.put(name, store);
        }  
        return "Store" + name + " added Successfully";
    }

    public synchronized String removeStore(String name) {
        synchronized(stores){
            if (! stores.containsKey(name)){
                return "Store " + name + " doesnt exist";
            }
            stores.remove(name);
        }
        return "Store " + name + " removed Succesfully";
    }

    public Store getStore(String name) {
        synchronized(stores){
            return stores.get(name);
        } 
    }

    public boolean hasStore(String name) {
        synchronized(stores){
            return stores.containsKey(name);
        }
    }

    public List<Store> getAllStores() {
        synchronized (stores){
            return new ArrayList<>(stores.values());
        }
    }

    public String removeProduct(String storeName, String productName){
        synchronized (stores){

            Store store = getStore(storeName);
            if (store == null){
                return "Store " + storeName + " doesnt exist, product removal failed";
            }
            if(! store.removeProduct(productName)){
                return "Product: "+ productName + " not found in Store: " + storeName + " , removal failed";
            }
        }
        return "Product: "+ productName + " removed from Store: " + storeName;
    }
    public String addProduct(String storeName, Product product){
        synchronized (stores){
            Store store = getStore(storeName);
            if (store == null){
                return "Store " + storeName + " doesnt exist, product add failed";
            }
            store.addProduct(product);
        }
        return "Product: "+product.getProductName() + " added to Store: " + storeName;
    }

    public String addJsonFileAsStore(String path) {
        String json=null;
        Store store=null;
        try {
            json = StoreParser.jsonFileToString(path);
            store = StoreParser.createStoreFromJSONString(json);
        } catch (Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null ;
        }
        return addStore(store);
    }

    public Store JSONtoStore(String json){
        try {
            return StoreParser.createStoreFromJSONString(json);
        } catch (Exception e ) {
            e.printStackTrace();
            return null ;
        }
    }

    public String rate(String storeName, int stars){
        synchronized (stores){
            Store store = getStore(storeName);
            if (store == null){
                return "Store " + storeName + " doesnt exist, rate failed";
            }
            store.addReview(stars);
        }
        return "Rate for: "+storeName + " was successfull";
    }

    public Object[] buy(String storeName, String productName, int quantity){
        synchronized (stores){
            Store store = getStore(storeName);
            if (store == null){
                return new Object[]{false, "Store " + storeName + " doesnt exist, buy failed"};
            }
            return store.buy(productName, quantity);
        }
    }

    public List<Store> filter(
        String name,
        List<String> categories,
        List<Integer> stars,
        List<String> price,
        double latitude,
        double longitude
    ){
        List<Store> filteredStores = new ArrayList<>();
        synchronized (stores){
            for (Store store : stores.values()) {
                if (
                    (name.equals("") || store.getStoreName().toLowerCase().contains(name.toLowerCase()) ) &&
                    (categories.isEmpty() || categories.contains(store.getFoodCategory()) ) &&
                    ( stars.isEmpty() || stars.contains((int) Math.round(store.getStars())))  &&
                    ( price.isEmpty() || price.contains(store.getPriceCategoryString())  ) 
                    && ( store.distance(latitude, longitude) < 5000000.00 ) // fix
                ){
                    filteredStores.add(store);
                }
            }
        }
        return filteredStores;
    }

    public List<Sale> getSales(){
        List<Sale> salesList = new ArrayList<>();
        synchronized (stores){
            for (Store store : stores.values()) {
                List<Sale> storeSales = store.sales;
                salesList.addAll(storeSales);
            }
        }
        return salesList;
    }

    public List<Sale> getSalesPerProductCategory(String category){
        List<Sale> filteredSales = new ArrayList<>();
        synchronized (stores){
            for (Store store : stores.values()) {
                List<Sale> salesList = store.sales;
                for (Sale sale : salesList) {
                    if ( (category.equals("")) || category.equals(sale.productCaregory)) {
                        filteredSales.add(sale);
                    }
                }
            }
        }

        return filteredSales;
	}

    public List<Sale> getSalesPerFoodCategory(String category){
        List<Sale> filteredSales = new ArrayList<>();
        synchronized (stores){
            for (Store store : stores.values()) {
                List<Sale> salesList = store.sales;
                for (Sale sale : salesList) {
                    if (category.equals(sale.foodCategory)) {
                        filteredSales.add(sale);
                    }
                }
            }
        }

        return filteredSales;
	}

    // public List<Sale> getSalesPerProcuctName(String productName){

    //     List<Sale> filteredSales = new ArrayList<>();
    //     synchronized (stores){
    //         for (Store store : stores.values()) {
    //             List<Sale> salesList = store.sales;
    //             for (Sale sale : salesList) {
    //                 if (productName.equals(sale.productName)) {
    //                     filteredSales.add(sale);
    //                 }
    //             }
    //         }
    //     }

    //     return filteredSales;
    // }



    private List<Pair<String, Integer>> sortSales(List<Sale> salesList) {
        HashMap<String, Integer> storeName_number_of_sales_map = new HashMap<>();
        for (Sale sale : salesList) {
            String storeName = sale.storename;
            int numberOfSales = storeName_number_of_sales_map.getOrDefault(storeName, 0);
            storeName_number_of_sales_map.put(storeName, numberOfSales + sale.amount);
        }

        List<Pair<String, Integer>> sortedList = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : storeName_number_of_sales_map.entrySet()) {
            sortedList.add(new Pair<>(entry.getKey(), entry.getValue()));
        }

        return sortedList;
    }

    private List<Pair<String, Integer>> sortSalesPerProduct(List<Sale> salesList) {
        HashMap<String, Integer> productName_number_of_sales_map = new HashMap<>();
        for (Sale sale : salesList) {
            String productName = sale.productName;
            int numberOfSales = productName_number_of_sales_map.getOrDefault(productName, 0);
            productName_number_of_sales_map.put(productName, numberOfSales + sale.amount);
        }

        List<Pair<String, Integer>> sortedList = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : productName_number_of_sales_map.entrySet()) {
            sortedList.add(new Pair<>(entry.getKey(), entry.getValue()));
        }

        return sortedList;
    }




    // Connections

    // For Reducer
    @Override
    public void handleResponseFromServer(Answer res) {
        // TODO Auto-generated method stub
        return;
    }

    // For Master
    @Override
    public Answer handleRequestFromClient(Task req) {
        System.out.println("Worker: " + id + " Received " + GlobalConfig.getCommandName(req.type) + " request, with id:"+req.ID+", from Master");
        if (! req.imediateAnswer){
            Task taskToSend = null;

            switch (req.type) {
                case GlobalConfig.FILTER_STORES:
                @SuppressWarnings("unchecked")
                List<Store> storesList =  filter(
                    (String) req.arguments.get("name"),
                    (List<String>) req.arguments.get("categories"),
                    (List<Integer>) req.arguments.get("stars"),
                    (List<String>) req.arguments.get("price"),
                    (double) req.arguments.get("latitude"),
                    (double) req.arguments.get("longitude")
                );

                taskToSend = new Task(req.clientTaskID, req.type, false, 
                Map.of(
                    "result", (Serializable) storesList
                )
                , req.ID);
                break;

                case GlobalConfig.GET_SALES_PER_PRODUCT_CATEGORY:
                    List<Sale> salesList = getSalesPerProductCategory(
                        (String) req.arguments.get("category")
                    );

                    List<Pair<String, Integer>> sortedList = sortSales(salesList);

                    taskToSend = new Task(req.clientTaskID, req.type, false, 
                    Map.of(
                        "result", (Serializable) sortedList
                    )
                    , req.ID);
                    break;
                
                case GlobalConfig.GET_SALES_PER_FOOD_CATEGORY:
                    List<Sale> salesList2 = getSalesPerFoodCategory(
                        (String) req.arguments.get("category")
                    );

                    List<Pair<String, Integer>> sortedList2 = sortSales(salesList2);

                    taskToSend = new Task(req.clientTaskID, req.type, false, 
                    Map.of(
                        "result", (Serializable) sortedList2
                    )
                    , req.ID);
                    break;
                
                case GlobalConfig.GET_SALES_PER_PRODUCT:
                    List<Sale> salesList3 = getSales();
                    //System.out.println("Sales List: "+salesList3);
                    List<Pair<String, Integer>> sortedList3 = sortSalesPerProduct(salesList3);

                    taskToSend = new Task(req.clientTaskID, req.type, false, 
                    Map.of(
                        "result", (Serializable) sortedList3
                    )
                    , req.ID);
                    break;
                default:
                    return new Answer(req,"default answer");
                
            }   
            
            System.out.println("Sending to reducer: "+GlobalConfig.getCommandName(req.type)+" request, with id:"+req.ID+", from Worker "+this.id);
            (new Client<WorkerNode>(taskToSend, GlobalConfig.REDUCER_HOST_IP, GlobalConfig.REDUCER_PORT_WORKER_AS_CLIENT, this))
            .start();

            return new Answer(
                new Task(req.clientTaskID, req.type, false, null, req.ID),
            ""
            );
        }
        
        switch (req.type) {
            case GlobalConfig.ADD_STORE:
                //Store store = JSONtoStore( (String) req.arguments.get("json") );
                //if (store==null) return new Answer(req, "Error in the json to store proccess") ;
                Store store = (Store) req.arguments.get("store");
                String message = addStore(store);
                return new Answer(req,message);

            case GlobalConfig.ADD_PRODUCT:
                return new Answer(req,
                        addProduct(
                            (String) req.arguments.get("storeName"),
                            (Product)  req.arguments.get("product")
                    )
                );

            case GlobalConfig.REMOVE_PRODUCT:
                return new Answer(req,
                        removeProduct(
                            (String) req.arguments.get("storeName"),
                            (String) req.arguments.get("productName")
                    )
                );
            case GlobalConfig.RATE:
                return new Answer(req,
                        rate(
                            (String) req.arguments.get("storeName"),
                            (int) req.arguments.get("stars")
                    )
                );
            
            case GlobalConfig.BUY:
                Object[] buyResult = buy(
                            (String) req.arguments.get("storeName"),
                            (String) req.arguments.get("productName"),
                            (int) req.arguments.get("quantity")
                    );
                String status = (Boolean) buyResult[0] ? "Success" : buyResult[1].toString();
                return new Answer(req,
                        status
                    )
                ;
            case GlobalConfig.CHANGE_STOCK:
                return new Answer(req,
                        changeStock(
                            (String) req.arguments.get("storeName"),
                            (String)  req.arguments.get("productName"),
                            (int) req.arguments.get("quantity")
                    )
                );

            default:
                return new Answer(req,"default answer");
        }
    }

    private String changeStock(String storeName, String productName, int quantity) {
        String result="";
        synchronized (stores){
            Store store = getStore(storeName);
            if (store == null){
                return "Store " + storeName + " doesnt exist, stock change failed";
            }
            result= store.changeStock(productName, quantity);
        }
        return "Product: "+productName + " stock changed in Store: " + storeName + ", from " + result;
    }

    public void createServer(int port){
        new Server<WorkerNode>()
        .openServer(this, port, ("WorkerNode "+this.id));
    }

    public static void main(String[] args) throws Exception {
        // for (int i = 0; i < GlobalConfig.WORKERS_NUMBER; i++) {
        //     WorkerNode node = new WorkerNode(i);
        //     new Thread(() -> {
        //         node.createServer();
        //     }).start();
        // }   
        // for (int i = 0; i < GlobalConfig.WORKERS_NUMBER; i++) {
        //     ProcessBuilder pb = new ProcessBuilder(
        //         "java", "WorkerNode", String.valueOf(i)
        //     );
        //     pb.inheritIO(); // optional: to inherit stdout/stderr
        //     try {
        //         pb.start(); // starts a new OS-level process
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //     }
        // }    
        int worketID = Integer.parseInt(args[0]);
        int workerPort = Integer.parseInt(args[1]);
       WorkerNode node = new WorkerNode(worketID);
       node.createServer(workerPort);
    }
    
}
