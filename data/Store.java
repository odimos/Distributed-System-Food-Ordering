package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Store implements Serializable {
    private static final long serialVersionUID = 3L;

    public String storeName;
    public double latitude;
    public double longitude;
    public String foodCategory;
    public double stars;
    public int noOfVotes;
    public String priceCategory; // $, $$, $$$
    public String storeLogo;

    public List<Sale> sales;
    public double totalRevenue;
    public int expensivenessCategory;
    public List<Product> products;
    public byte[] imageData;

    public String toString() {
        return "\nStore\n{" +
                "storeName='" + storeName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", foodCategory='" + foodCategory + '\'' +
                ", stars=" + stars +
                ", noOfVotes=" + noOfVotes +
                ", priceCategory='" + priceCategory + '\'' +
                ", storeLogo='" + storeLogo + '\'' +
                ", totalRevenue=" + totalRevenue +
                ", expensivenessCategory=" + expensivenessCategory +
                ", products=" + products +
                "\n}";
    }

    // from JSON
    public Store(String storeName, double latitude, double longitude, String foodCategory,
                 double stars, int noOfVotes, String storeLogo,byte[] imageData, List<Product> products) {
        this.storeName = storeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.foodCategory = foodCategory;
        this.stars = stars;
        this.noOfVotes = noOfVotes;
        this.storeLogo = storeLogo;
        this.products = products;
        this.priceCategory = calculatePriceCategory();
        this.sales= new ArrayList<Sale>();
        this.totalRevenue = 0.0;
        
        this.imageData = imageData;
    }

    public synchronized int getNoOfVotes() {
        return noOfVotes;
    }
    
    public synchronized String getPriceCategoryString() {
        return priceCategory;
    }
    

    public String getStoreName() {
        return storeName;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public String getFoodCategory() {
        return foodCategory;
    }
    public synchronized double getStars() {
        return stars;
    }
    public synchronized int getPriceCategory() {
        return expensivenessCategory;
    }

    public synchronized void addProduct(Product p) {
        products.add(p);
        priceCategory = calculatePriceCategory();
    }

    public synchronized Boolean removeProduct(String productName) {
        Boolean result= products.removeIf(p -> p.getProductName().equals(productName));
        this.priceCategory = calculatePriceCategory();
        return result;
    }

    public synchronized Product getProduct(String name){
        Product product = null;
        for (Product p : products){
            if (p.getProductName().equals(name)) {
                return p;
            }
        }

        return product;
    }

    public synchronized String buyProduct(Product product, int quantity){
        int availableAmount = product.getAvailableAmount();
        if ( availableAmount < quantity ) return "Not enough stock";
        product.purchase(quantity);
        sales.add(new Sale(foodCategory, product.getProductType(), quantity, this.storeName, product.getProductName()));
        System.out.println( "after bought sales: "+sales);
        return "Bought " +quantity+ " "+ product.getProductName()+" from "+ this.storeName + " Successfully";
    }

    public synchronized String buy(String productName, int quantity){
        Product product = getProduct(productName);
        if (product==null) return "Product not Found";
        System.out.println("buying product: "+product);
        return buyProduct(product, quantity);
    }

    // public synchronized String buy(String productName, int quantity) {
    //     for (Product p : products) {
    //         if (p.getProductName().equals(productName)) {
    //             if (p.getAvailableAmount() >= quantity) {
    //                 p.setAvailableAmount(p.getAvailableAmount() - quantity);
    //                 double totalPrice = p.getPrice() * quantity;
    //                 addRevenue(totalPrice);
    //                 // this.sales += 
    //                 System.out.print("Purchased " + quantity + " of " + productName + " for $" + totalPrice);
    //                 return "Purchased " + quantity + " of " + productName + " for $" + totalPrice;
    //             } else {
    //                 System.out.print("Not enough stock for " + productName);
    //                 return "Not enough stock for " + productName;
    //             }
    //         }
    //     }
    //     return "Product: "+ productName+ " not found";
    // }

    // could be one initial and one every more we need it
    public synchronized String calculatePriceCategory() {
        double total = 0;
        for (Product p : products) {
            total += p.getPrice();
        }
        double avg = total / products.size();
        if (avg <= 5) return "$";
        else if (avg <= 15) return "$$";
        else return "$$$";
    }

    public synchronized void addRevenue(double amount) {
        totalRevenue += amount;
    }

    public synchronized void addReview(int stars) {
        this.stars = (this.stars * this.noOfVotes + stars) / (this.noOfVotes + 1);
        this.noOfVotes++;
    }

    public double distance(double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double dLat = Math.toRadians(lat2 - this.latitude);
        double dLon = Math.toRadians(lon2 - this.longitude);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c; // distance in km
    }

    public static void main(String[] args) {

        
    }

    public synchronized String changeStock(String productName, int quantity) {
        Product product = getProduct(productName);
        if (product==null) return "Product not Found";
        return product.changeStock(quantity);
    }


    /*
     * File imgFile = new File("store_photo.jpg");
        byte[] photoBytes = new byte[(int) imgFile.length()];
        FileInputStream fis = new FileInputStream(imgFile);
        fis.read(photoBytes);
        fis.close();

        // Create Store object
        Store store = new Store("ABC Market", "123 Main St", photoBytes);
     * 
     */
}
