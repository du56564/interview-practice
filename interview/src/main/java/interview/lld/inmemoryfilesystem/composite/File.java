package interview.lld.inmemoryfilesystem.composite;



public class File extends FileSystemNode {
    private String content;
    public File(String name, Directory parent) {
        super(name, parent);
        this.content = "";
    }
    public String getContent() { return content; }

    public void setContent(String content) {
        this.content = content;
    }
}