package interview.lld.inmemoryfilesystem.strategy;


import interview.lld.inmemoryfilesystem.composite.Directory;

public interface ListingStrategy {
    void list(Directory directory);
}
