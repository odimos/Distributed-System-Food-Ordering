package data;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;

public class ImageLoader {
    public static byte[] imageUriToByteArray(String path, String formatName) throws Exception {
        // Convert URI string to URI
        String localpath = "res/" + path;
        File file = new File(localpath);
        

        // Read the image
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new IllegalArgumentException("Unsupported image type or file not found");
        }

        // Convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, baos);
        return baos.toByteArray();
    }

    public static String getImageExtension(String uriString) {
    // Remove query or fragment if any
    int queryIndex = uriString.indexOf('?');
    if (queryIndex != -1) {
        uriString = uriString.substring(0, queryIndex);
    }

    // Get the part after the last dot
    int dotIndex = uriString.lastIndexOf('.');
    if (dotIndex != -1 && dotIndex < uriString.length() - 1) {
        return uriString.substring(dotIndex + 1).toLowerCase();
    }

    return null; // No extension found
    }

    public static byte[] imagePathToByteArray(String path) throws Exception {
        String formatName = getImageExtension(path);
        return imageUriToByteArray(path, formatName);
    }
}


