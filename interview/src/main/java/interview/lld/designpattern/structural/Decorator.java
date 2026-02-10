package interview.lld.designpattern.structural;

//A decorator adds behavior to an object without changing its class.
// Use it when you need to layer on extra functionality at runtime.
// words like "optional features," "stack behaviors," or "combine multiple enhancements," think Decorator.
//Each decorator adds one piece of functionality. You can stack them in any order
// and add or remove them without touching the base class or other decorators, though in real systems order often affects behavior.
interface DataSource {
    void writeData (String data);
    String readData ();
}

class FileDataSource implements DataSource {

    private String fileName;
    public FileDataSource (String fileName) {
        this.fileName = fileName;
    }

    public void writeData (String data) {
        //write logic
        System.out.printf("writting Data: {}", data);
    }
    public String readData () {
        return "reading file data.";
    }
}

class EncryptionDecorator implements  DataSource {

    private DataSource wrapped;

    public EncryptionDecorator(DataSource source) {
        this.wrapped = source;
    }

    private String encrypt(String data) {
        return "encrypted:" + data;
    }

    private String decrypt(String data) {
        return data.replace("encrypted:", "");
    }

    @Override
    public void writeData(String data) {
        this.wrapped.writeData(data);
    }

    @Override
    public String readData() {
        return this.wrapped.readData();
    }
}

class CompressionDecorator implements DataSource {
    private DataSource wrapped;

    public CompressionDecorator(DataSource source) {
        this.wrapped = source;
    }

    public void writeData(String data) {
        String compressed = compress(data);
        wrapped.writeData(compressed);  // Delegate to wrapped object
    }

    public String readData() {
        String data = wrapped.readData();
        return decompress(data);
    }

    private String compress(String data) {
        return "compressed:" + data;
    }

    private String decompress(String data) {
        return data.replace("compressed:", "");
    }

}
public class Decorator {
    static void main() {
        DataSource source = new FileDataSource("file.txt");
        source = new EncryptionDecorator(source);
        source = new CompressionDecorator(source);
        source.writeData("sensitive info");
        // Data gets compressed, then encrypted, then written to file

    }
}
