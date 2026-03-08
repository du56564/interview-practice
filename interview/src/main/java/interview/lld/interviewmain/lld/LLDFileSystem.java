package interview.lld.interviewmain.lld;
/*
Design an in-memory file system that supports creating files and directories, navigating paths, and basic file operations.

1. Hierarchical file system with single root directory
2. Files store string content
3. Folders contain files and other folders
4. Create and delete files and folders
5. List contents of a folder
6. Navigate/resolve absolute paths (e.g., /home/user/docs)
7. Rename and move files and folders
8. Retrieve full path from any file/folder reference
9. Scale to tens of thousands of entries in memory

Out of Scope:
- Search functionality
- Relative path resolution (../ or ./)
- Permissions, ownership, timestamps
- File type-specific behavior
- Persistence / disk storage
- Symbolic links
- UI layer


Requirements:-
    - Single Root
    - path navigation
    - Store content
    - Error case
    - Deep folder hierarchy

Core Entity
    - File (leaf node)
    - Folder (directory: root and child nodes)
    - Path
    - FileSystem (orchestrator: public API)


FileSystem
    └── root: Folder
            ├── Folder ("home")
            │       └── Folder ("user")
            │               ├── File ("notes.txt")
            │               └── Folder ("docs")
            └── File ("readme.txt")


abstract class FileSystemEntry:
    - name: string
    - parent: Folder?
    + FileSystemEntry(name)
    + getName() -> string
    + setName(name)
    + getParent() -> Folder?
    + setParent(Folder?)
    + getPath() -> string
    + isDirectory() -> boolean  // abstract


class File extends FileSystemEntry:
    - content: string
    + File(name, content)
    + getContent() -> string
    + setContent(content)
    + isDirectory() -> false


class Folder extends FileSystemEntry:
    - children: Map<string, FileSystemEntry>
    + Folder(name)
    + isDirectory() -> true
    + addChild(entry) -> boolean
    + removeChild(name) -> FileSystemEntry?
    + getChild(name) -> FileSystemEntry?
    + hasChild(name) -> boolean
    + getChildren() -> List<FileSystemEntry>


class FileSystem:
    - root: Folder
    + FileSystem()
    + createFile(path, content) -> File
    + createFolder(path) -> Folder
    + delete(path)
    + list(path) -> List<FileSystemEntry>
    + get(path) -> FileSystemEntry
    + rename(path, newName)
    + move(srcPath, destPath)

- Important Cycle detection

- Composite Pattern


 */

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class Node {
    protected String name;
    protected Folder parent;

    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public abstract boolean isDirectory();

}

class File extends Node {
    private StringBuilder content;

    public File(String name) {
        super(name);
        this.content = new StringBuilder();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    public void write(String data) {
        this.content.append(data);
    }

    public String read() {
        return content.toString();
    }
}

class Folder extends Node {
    private Map<String, Node> children;
    public Folder(String name) {
        super(name);
        this.children = new HashMap<>();
    }
    @Override
    public boolean isDirectory() {
        return true;
    }

    public void add(Node node) {
        children.put(node.getName(), node);
        node.setParent(this);
    }

    public Node get(String name) {
        return children.get(name);
    }
    public void remove(String name) {
        children.remove(name);
    }

    public Collection<Node> list() {
        return children.values();
    }
}

class FileSystem {
    private final Folder root;

    public FileSystem() {
        root = new Folder("/");
    }
    private Node resolve(String path) {
        if (path.equals("/"))
            return root;
        String[] parts = path.split("/");
        Node current = root;
        for (int i = 1; i < parts.length; i++) {
            if (!(current instanceof Folder folder)) {
                return null;
            }
            current = folder.get(parts[i]);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    public void mkdir(String path) {
        String[] parts = path.split("/");
        Folder current = root;
        for (int i = 1; i < parts.length; i++) {
            Node next = current.get(parts[i]);
            if (next == null) {
                Folder newFolder = new Folder(parts[i]);
                current.add(newFolder);
                current = newFolder;
            } else {
                current = (Folder) next;
            }
        }
    }

    public void createFile(String path) {
        int lastSlash = path.lastIndexOf('/');
        String folderPath = path.substring(0, lastSlash);
        String fileName = path.substring(lastSlash+1);
        Folder folder = (Folder) resolve(folderPath); // correct folder path
        if (folder == null) {
            return;
        }
        folder.add(new File(fileName));
    }

    public void write(String path, String data) {
        Node node = resolve(path);
        if (node instanceof File file) {
            file.write(data);
        }
    }

    public String read(String path) {
        Node node = resolve(path);
        if (node instanceof File file) {
            return file.read();
        }
        return null;
    }

    public List<String> ls(String path) {
        Node node = resolve(path);
        if (node instanceof Folder folder) {
            return folder.list()
                    .stream()
                    .map(Node::getName)
                    .sorted()
                    .toList();
        }
        return List.of(node.getName());
    }
    public void delete(String path) {
        Node node = resolve(path);
        if (node != null && node.parent != null) {
            node.parent.remove(node.getName());
        }
    }

}



public class LLDFileSystem {
    static void main() {
        FileSystem fileSystem = new FileSystem(); // API
        fileSystem.mkdir("/a/b");
        fileSystem.createFile("/a/b/file.txt");
        fileSystem.write("/a/b/file.txt", "Hi I am Pluto.");
        fileSystem.write("/a/b/file.txt", "How are you?");

        System.out.println(fileSystem.read("/a/b/file.txt"));
        System.out.println(fileSystem.ls("/a/b"));


    }
}
