package com.toast.apocalypse.common.util;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

// Uh, I couldn't come up with a better name for this class
public class StorageUtils {

    @Nullable
    public static <T> T getRandomListElementFiltered(Random random, Collection<T> collection, Predicate<T> predicate) {
        final List<T> candidateElements = new ArrayList<>();

        for (T element : collection) {
            if (predicate.test(element))
                candidateElements.add(element);
        }
        if (candidateElements.isEmpty())
            return null;

        return candidateElements.get(random.nextInt(candidateElements.size()));
    }

    @Nullable
    public static <K, V> V getRandomMapElementFiltered(Random random, Map<K, V> map, BiPredicate<K, V> biPredicate) {
        final List<V> candidateElements = new ArrayList<>();

        for (K key : map.keySet()) {
            V value = map.get(key);

            if (biPredicate.test(key, value))
                candidateElements.add(value);
        }
        if (candidateElements.isEmpty())
            return null;

        return candidateElements.get(random.nextInt(candidateElements.size()));
    }

    @Nullable
    public static <K, V> K getRandomMapKeyFiltered(Random random, Map<K, V> map, BiPredicate<K, V> biPredicate) {
        final List<K> candidateKeys = new ArrayList<>();

        for (K key : map.keySet()) {
            V value = map.get(key);

            if (biPredicate.test(key, value))
                candidateKeys.add(key);
        }
        if (candidateKeys.isEmpty())
            return null;

        return candidateKeys.size() == 1 ? candidateKeys.get(0) : candidateKeys.get(random.nextInt(candidateKeys.size()));
    }
}
