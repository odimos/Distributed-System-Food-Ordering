public final class GlobalConfig {

    public static final int WORKERS_NUMBER = 3;

    public static final String MASTER_HOST_IP = "localhost";
    public static final int MASTER_PORT_FOR_CLIENTS = 4440;
    public static final int MASTER_PORT_FOR_REDUCER_AS_CLIENT = 5011;

    public static final String WORKERNODE_HOST_IP = "localhost";
    public static final int INITIAL_PORT_FOR_WORKERS = 4442;

    public static final String REDUCER_HOST_IP = "localhost";
    public static final int REDUCER_PORT_WORKER_AS_CLIENT = 5012;

    public static final int ADD_STORE = 1;
    public static final int ADD_PRODUCT = 2;
    public static final int REMOVE_PRODUCT =3;
    public static final int RATE = 4;
    public static final int BUY = 5;
    public static final int FILTER_STORES = 6;
    public static final int GET_SALES_PER_PRODUCT_CATEGORY = 7;
    public static final int GET_SALES_PER_FOOD_CATEGORY = 8;
    public static final int GET_SALES_PER_PRODUCT = 9;
    public static final int CHANGE_STOCK = 10;

    public static String getCommandName(int command) {
    switch (command) {
        case 1:
            return "ADD_STORE";
        case 2:
            return "ADD_PRODUCT";
        case 3:
            return "REMOVE_PRODUCT";
        case 4:
            return "RATE";
        case 5:
            return "BUY";
        case 6:
            return "FILTER_STORES";
        case 7:
            return "GET_SALES_PER_PRODUCT_CATEGORY";
        case 8:
            return "GET_SALES_PER_FOOD_CATEGORY";
        case 9:
            return "GET_SALES_PER_PRODUCT";
        case 10:
            return "CHANGE_STOCK";
        
        default:
            return "UNKNOWN_COMMAND";
    }

    // Να προσθέτουν/αφαιρούν διαθέσιμα προϊόντα
    // Να εμφανίζουν τις συνολικές πωλήσεις ανα προϊόν
    // πωλήσεις ανα τύπο καταστημάτων και συνολικές πωλήσεις ανα κατηγορία προϊόντος
}

}