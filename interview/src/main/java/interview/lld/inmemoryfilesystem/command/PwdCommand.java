package interview.lld.inmemoryfilesystem.command;

import interview.lld.inmemoryfilesystem.FileSystem;

public class PwdCommand implements Command {
    private final FileSystem fs;

    public PwdCommand(FileSystem fs) {
        this.fs = fs;
    }

    @Override
    public void execute() {
        System.out.println(fs.getWorkingDirectory());
    }
}