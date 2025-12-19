package data;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StoreParser {

    public static String jsonFileToString(String filePath) throws Exception {
         // Διαβάζουμε το JSON αρχείο γραμμή-γραμμή
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder content = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        reader.close();
        // Εκτυπώνουμε το περιεχόμενο του JSON
        //System.out.println("Loaded JSON content:\n" + content.toString());
        return content.toString();
    }

    public static Store createStoreFromJSONString(String jsonContent) throws Exception {

        // Αντιστοίχιση πεδίων του JSON σε αντικείμενα Java
        String StoreName = extractValue(jsonContent, "StoreName");
        String Latitude = extractValue(jsonContent, "Latitude");
        String Longitude = extractValue(jsonContent, "Longitude");
        String FoodCategory = extractValue(jsonContent, "FoodCategory");
        String Stars = extractValue(jsonContent, "Stars");
        String NoOfVotes = extractValue(jsonContent, "NoOfVotes");
        String StoreLogo = extractValue(jsonContent, "StoreLogo");

        // Ανάγνωση των προϊόντων
        List<Product> products = new ArrayList<>();
        String productsJson = jsonContent.substring(jsonContent.indexOf("\"Products\""));
        productsJson = productsJson.substring(productsJson.indexOf("[") + 1, productsJson.indexOf("]"));

        String[] productEntries = productsJson.split("\\},\\s*\\{");

        for (String productEntry : productEntries) {
            productEntry = productEntry.replaceAll("[\\[\\]\\{\\}]", "").trim(); // καθάρισμα συμβόλων

            String pname = extractValue(productEntry, "ProductName");
            String ptype = extractValue(productEntry, "ProductType");
            String amountStr = extractValue(productEntry, "Available Amount");
            String priceStr = extractValue(productEntry, "Price");

            // Εκτύπωση των προϊόντων για debugging
            // System.out.println("Product Name: " + pname);
            // System.out.println("Product Type: " + ptype);
            // System.out.println("Available Amount: " + amountStr);
            // System.out.println("Price: " + priceStr);

            int amount = tryParseInt(amountStr);
            double price = tryParseDouble(priceStr);

            products.add(new Product(pname, ptype, amount, price));
        }

        byte[] imageData = ImageLoader.imagePathToByteArray(StoreLogo);
        
        return new Store(StoreName, tryParseDouble(Latitude), tryParseDouble(Longitude), 
        FoodCategory, tryParseDouble(Stars), tryParseInt(NoOfVotes),StoreLogo ,imageData, products);

    }
 // Βελτιωμένο extractValue που χειρίζεται αριθμούς και strings σωστά
 private static String extractValue(String json, String key) {
    String searchKey = "\"" + key + "\":";
    int startIndex = json.indexOf(searchKey);
    if (startIndex == -1) return "";

    startIndex += searchKey.length();

    // Skip whitespaces
    while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
        startIndex++;
    }

    if (startIndex >= json.length()) return "";

    char firstChar = json.charAt(startIndex);

    // Case 1: String (starts with ")
    if (firstChar == '"') {
        int endIndex = json.indexOf('"', startIndex + 1);
        if (endIndex == -1) return "";
        return json.substring(startIndex + 1, endIndex).trim();
    }

    // Case 2: Number (or boolean/null) - ends at , or }
    int endIndex = startIndex;
    while (endIndex < json.length() && 
          !Character.isWhitespace(json.charAt(endIndex)) &&
          json.charAt(endIndex) != ',' &&
          json.charAt(endIndex) != '}') {
        endIndex++;
    }

    return json.substring(startIndex, endIndex).trim();
}

// Helper function to safely parse integers
    private static int tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("Invalid integer value: " + value);
            return 0;
        }
    }

    // Helper function to safely parse doubles
    private static double tryParseDouble(String value) {
        try {
            // Ρύθμιση τοπικής ρύθμισης για χρήση τελείας ως δεκαδικού διαχωριστή
            Locale.setDefault(Locale.US);
            //System.out.println("Attempting to parse double: " + value);
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.err.println("Invalid double value: " + value);
            return 0.0;
        }
    }



public static void main (String[] args) throws Exception {
        // Example usage of the StoreParser class
        System.out.println("StoreParser is ready to parse store data.");
        String json = StoreParser.jsonFileToString("res/store_data.json");
        Store store = StoreParser.createStoreFromJSONString(json);
        System.out.println(store);
    }
    
}
