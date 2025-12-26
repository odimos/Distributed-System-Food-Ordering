package data;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import javax.imageio.IIOException;

public class ImageLoader {
    public static byte[] imageUriToByteArray(String path, String formatName)  {
        try {
            // Convert URI string to URI
            String localpath = "res/" + path;
            File file = new File(localpath);
            
            BufferedImage image = ImageIO.read(file);

            if (image == null) {
                //throw new IllegalArgumentException("Unsupported image type or file not found");
                return new byte[0];
            }

            // Convert to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, formatName, baos);

            return baos.toByteArray();
        }  catch (IIOException e) {
        // Image missing, unreadable, or invalid
        // handle gracefully
            return new byte[0];
        } catch (Exception e) {
            return new byte[0];
        }
        
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


