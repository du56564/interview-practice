package interview.lld.inmemoryfilesystem.command;

import interview.lld.inmemoryfilesystem.FileSystem;

public class MkdirCommand implements Command {
    private final FileSystem fs;
    private final String path;

    public MkdirCommand(FileSystem fs, String path) {
        this.fs = fs;
        this.path = path;
    }

    @Override
    public void execute() { fs.createDirectory(path); }
}