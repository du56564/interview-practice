package interview.lld.inmemoryfilesystem.strategy;

import interview.lld.inmemoryfilesystem.composite.Directory;
import interview.lld.inmemoryfilesystem.composite.FileSystemNode;

public class DetailedListingStrategy implements ListingStrategy {

    @Override
    public void list(Directory directory) {
        for (FileSystemNode node : directory.getChildren().values()) {
            char type = (node instanceof Directory) ? 'd' : 'f';
            System.out.println(type + "\t" + node.getName() + "\t" + node.getCreatedTime());
        }
    }
}
