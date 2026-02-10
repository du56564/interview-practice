package interview.lld.inmemoryfilesystem.command;

import interview.lld.inmemoryfilesystem.FileSystem;

public class CdCommand implements Command {
    private final FileSystem fs;
    private final String path;

    public CdCommand(FileSystem fs, String path) {
        this.fs = fs; this.path = path;
    }

    @Override public void execute() {
        fs.changeDirectory(path);
    }
}