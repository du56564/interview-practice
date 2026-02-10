package interview.lld.inmemoryfilesystem.strategy;

import interview.lld.inmemoryfilesystem.composite.Directory;

public class SimpleListingStrategy implements ListingStrategy{

    @Override
    public void list(Directory directory) {
        directory.getChildren().keySet().forEach(name -> System.out.print(name + "  "));
        System.out.println();
    }
}
