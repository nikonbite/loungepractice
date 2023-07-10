package me.nikonbite.api.lib.util;

import com.google.common.collect.Sets;

import java.util.Set;

public interface PaginationUtil {
    int[] squarePagination = getSlotsFullPagination(
            "ooooooooo",
                    "oxxxxxxxo",
                    "oxxxxxxxo",
                    "oxxxxxxxo",
                    "oxxxxxxxo",
                    "ooooooooo"
    );

    int[] spectatorPagination = getSlotsFullPagination(
            "xxxxxxxxx",
                    "xxxxxxxxx",
                    "xxxxxxxxx",
                    "xxxxxxxxx",
                    "xxxxxxxxx",
                    "ooooooooo"
    );

    static int[] getSimplePagination(String string) {
        if (string.length() != 9)
            throw new IllegalArgumentException("String must be 9 characters long");

        Set<Integer> integerSet = Sets.newHashSet();

        for (int j = 0; j < string.toCharArray().length; j++) {
            if (string.charAt(j) == 'x') {
                integerSet.add(j + 1);
            }
        }

        return integerSet.stream().mapToInt(i -> i).toArray();
    }

    static int[] getSlotsSimplePagination(String string, int rows) {
        Set<Integer> integerSet = Sets.newHashSet();

        for (int j = 0; j < rows; j++) {
            for (int i : getSimplePagination(string)) {
                integerSet.add(i + (j * 9));
            }
        }

        return integerSet.stream().mapToInt(i -> i).toArray();
    }

    static int[] getSlotsFullPagination(String string) {
        Set<Integer> integerSet = Sets.newHashSet();

        for (int j = 0; j < string.length(); j++) {
            if (string.charAt(j) == 'x') {
                integerSet.add(j + 1);
            }
        }

        return integerSet.stream().mapToInt(i -> i).toArray();
    }

    static int[] getSlotsFullPagination(String... strings) {
        StringBuilder string = new StringBuilder();

        for (String s : strings) {
            string.append(s);
        }

        Set<Integer> integerSet = Sets.newHashSet();

        for (int j = 0; j < string.length(); j++) {
            if (string.charAt(j) == 'x') {
                integerSet.add(j + 1);
            }
        }

        return integerSet.stream().mapToInt(i -> i).toArray();
    }

}
