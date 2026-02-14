package interview.lld.interviewmain.designpattern;


interface Image {
    void display();
    String getFileName();
}


// We will have proxy of this because directly using this would load image directly. We don't want to touch this class and do lazy loading for image.
class HighResolutionImage implements Image {
    private String fileName;
    private byte[] imageData;

    public HighResolutionImage(String fileName) {
        this.fileName = fileName;
        loadImageFromDisk(); // Expensive operation!
    }

    private void loadImageFromDisk() {
        System.out.println("Loading image: " + fileName + " from disk (Expensive Operation)...");
        try {
            Thread.sleep(2000); // Simulate disk I/O delay
            this.imageData = new byte[10 * 1024 * 1024]; // Simulate 10MB memory usage
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Image " + fileName + " loaded successfully.");
    }

    @Override
    public void display() {
        System.out.println("Displaying image: " + fileName);
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}


// Proxy of HighResolutionImage (Ex: Spring AOP)
class ImageProxy implements Image {

    private String fileName;
    private HighResolutionImage realImage;

    public ImageProxy(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void display() {
        // Lazy initialization: Load only when display() is called
        if (realImage == null) {
            System.out.println("ImageProxy: display() requested for " + fileName + ". Loading high-resolution image...");
            realImage = new HighResolutionImage(fileName);
        } else {
            System.out.println("ImageProxy: Using cached high-resolution image for " + fileName);
        }

        // Delegate the display call to the real image
        realImage.display();
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}

public class ProxyPattern {
    static void main() {
        // Create lightweight proxies instead of full image objects
        Image image1 = new ImageProxy("photo1.jpg");
        Image image2 = new ImageProxy("photo2.png"); // Never displayed
        Image image3 = new ImageProxy("photo3.gif");

        System.out.println("\nGallery initialized. No images actually loaded yet.");
        System.out.println("Image 1 Filename: " + image1.getFileName()); // Does not trigger image load

        // User clicks on image1
        System.out.println("\nUser requests to display " + image1.getFileName());
        image1.display(); // Lazy loading happens here

        // User clicks on image1 again
        System.out.println("\nUser requests to display " + image1.getFileName() + " again.");
        image1.display(); // Already loaded; no loading delay

        // User clicks on image3
        System.out.println("\nUser requests to display " + image3.getFileName());
        image3.display(); // Triggers loading for image3

        System.out.println("\nApplication finished. Note: photo2.png was never loaded.");
    }
}
