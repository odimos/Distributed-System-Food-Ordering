package data;

import java.io.Serializable;

public final class Sale implements Serializable {
    private static final long serialVersionUID = 5L;

    public final String foodCategory;
    public final String productCaregory;
    public final int amount;
    public final String storename;
    public final String productName;
    
    public  Sale(String foodCategory, String productCaregory, int amount, String storename, String productName) {
        this.foodCategory = foodCategory;
        this.productCaregory = productCaregory;
        this.amount = amount;
        this.storename = storename;
        this.productName = productName;
    }
    public int getAmount(){
        return amount;
    }

    public String getStorename(){
        return storename;
    }
    
    @Override
    public String toString() {
        return "Sale{\n" +
            "foodCategory='" + foodCategory + '\'' +
            ", productCaregory='" + productCaregory + '\'' +
            ", amount=" + amount +
            ", storename='" + storename + '\'' +
            "\n}";
    }
}
