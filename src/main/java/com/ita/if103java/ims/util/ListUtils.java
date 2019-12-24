package com.ita.if103java.ims.util;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ListUtils {
    public static <T> List<T> concat(List<T> list1, List<T> list2) {
        return Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList());
    }

    public static <A, B, C> List<C> zip(List<A> list1, List<B> list2, BiFunction<A, B, C> zipper) {
        return IntStream
            .range(0, Math.min(list1.size(), list2.size()))
            .mapToObj(i -> zipper.apply(list1.get(i), list2.get(i)))
            .collect(Collectors.toList());
    }
}
