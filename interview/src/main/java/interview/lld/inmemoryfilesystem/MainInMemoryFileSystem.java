package interview.lld.inmemoryfilesystem;

/*
An In-Memory File System is a simplified version of a real file system (like NTFS, ext4, or APFS), designed to run entirely in memory.

Requirements
    - Unix like file systems
    - command line
    - command: mkdir, cd, ls, pwd, touch, cat
    - options ls -l
    - handle path : absolute (/home/user) & relative (documents, ../)
    - file store string based content

Core Entities
    Hierarchical structure
    - FileSystemNode
    - File (Leaf)
    - Directory (Form tree)
    - FileSystem
    Shell Interact command
    - Command : Command, MkdirCommand, CdCommand, Shell
    - Concrete
    - Shell

Design Pattern
    - Strategy
    - Composite (Tree like structure)
    - Command
    - Facade

 */




public class MainInMemoryFileSystem {
    static void main() {
        Shell shell = new Shell();
        String[] commands = {
                "pwd",                          // /
                "mkdir /home",
                "mkdir /home/user",
                "ls -l /home",                  // d user
                "cd /home/user",
                "pwd",                          // /home/user
                "ls",                           // file1.txt
                "cat file1.txt",                // Hello World!
                "cat file1.txt",                // Overwriting content
                "mkdir documents",
                "cd documents",
                "pwd",                          // /home/user/documents
                "ls",                           // report.docx
                "cd ..",
                "pwd",                          // /home/user
                "ls -l",                        // d documents, f file1.txt
                "cd /",
                "pwd",                          // /
                "ls -l",                        // d home
                "cd /nonexistent/path"          // Error: not a directory
        };

        for (String command : commands) {
            System.out.println("\n$ " + command);
            shell.executeCommand(command);
        }

    }
}
