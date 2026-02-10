package interview.lld.inmemoryfilesystem.command;


import interview.lld.inmemoryfilesystem.strategy.ListingStrategy;
import  interview.lld.inmemoryfilesystem.FileSystem;


public class LsCommand implements Command {

    private final FileSystem fs;
    private final String path; // Path can be null, meaning "current directory"
    private final ListingStrategy strategy;

    public LsCommand(FileSystem fs, String path, ListingStrategy strategy) {
        this.fs = fs;
        this.path = path;
        this.strategy = strategy;
    }

    @Override
    public void execute() {
        if (path == null) {
            fs.listContents(strategy);
        } else {
            fs.listContents(path, strategy);
        }
    }
}
