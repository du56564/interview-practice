package interview.lld.bloomfilter.factory;

import interview.lld.bloomfilter.enums.HashType;
import interview.lld.bloomfilter.strategy.DJB2HashStrategy;
import interview.lld.bloomfilter.strategy.FNV1HashStrategy;
import interview.lld.bloomfilter.strategy.HashStrategy;

public class HashStrategyFactory {
    public static HashStrategy createFactory (HashType type) {
        return switch (type) {
            case FNV1A -> new FNV1HashStrategy();
            case DJB2 -> new DJB2HashStrategy();
            default -> throw new IllegalArgumentException("Unsupported hash type: " + type);
        };
    }
}
