package interview.lld.inmemoryfilesystem.composite;



import java.time.Instant;

public abstract class FileSystemNode {
    protected String name;
    protected Directory parent;
    protected Instant createdTime;

    public FileSystemNode(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
        this.createdTime = Instant.now();
    }

    public String getPath() {
        if (parent == null) { // This is the root directory
            return name;
        }
        // Avoid double slash for root's children
        if (parent.getParent() == null) {
            return parent.getPath() + name;
        }
        return parent.getPath() + "/" + name;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Directory getParent() { return parent; }
    public Instant getCreatedTime() { return createdTime; }
}