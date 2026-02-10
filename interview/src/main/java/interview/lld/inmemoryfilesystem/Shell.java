package interview.lld.inmemoryfilesystem;

import interview.lld.inmemoryfilesystem.command.*;
import interview.lld.inmemoryfilesystem.strategy.DetailedListingStrategy;
import interview.lld.inmemoryfilesystem.strategy.ListingStrategy;
import interview.lld.inmemoryfilesystem.strategy.SimpleListingStrategy;

import java.util.Arrays;

class Shell {
    private final FileSystem fs;

    public Shell() {
        this.fs = FileSystem.getInstance();
    }

    public void executeCommand(String input) {
        String[] parts = input.trim().split("\\s+");
        String commandName = parts[0];

        Command command;

        try {
            switch (commandName) {
                case "mkdir":
                    command = new MkdirCommand(fs, parts[1]);
                    break;
                case "cd":
                    command = new CdCommand(fs, parts[1]);
                    break;
                case "ls":
                    command = new LsCommand(fs, getPathArgumentForLs(parts), getListingStrategy(parts));
                    break;
                case "pwd":
                    command = new PwdCommand(fs);
                    break;
                case "cat":
                    command = new CatCommand(fs, parts[1]);
                    break;
                default:
                    command = () -> System.err.println("Error: Unknown command '" + commandName + "'.");
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: Missing argument for command '" + commandName + "'.");
            command = () -> {}; // No-op command
        }

        command.execute();
    }

    private ListingStrategy getListingStrategy(String[] args) {
        if (Arrays.asList(args).contains("-l")) {
            return new DetailedListingStrategy();
        }
        return new SimpleListingStrategy();
    }

    private String getPathArgumentForLs(String[] parts) {
        // Find the first argument that is not an option flag.
        return Arrays.stream(parts)
                .skip(1) // Skip the command name itself
                .filter(part -> !part.startsWith("-"))
                .findFirst()
                .orElse(null); // Return null if no path argument is found
    }

}