/*
 * Copyright 2020 Simone Sestito
 * This file is part of Shops Queue.
 *
 * Shops Queue is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shops Queue is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shops Queue.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.simonesestito.shopsqueue.util;

import java.util.List;
import java.util.Objects;

/**
 * Utility functions to use on arrays
 */
public class ArrayUtils {
    /**
     * Check if every element of this array is true.
     */
    public static boolean allTrue(boolean... booleans) {
        for (boolean b : booleans) {
            if (!b)
                return false;
        }
        return true;
    }

    /**
     * Check if every element of this array is equal to the given value
     */
    public static <T> boolean all(T[] array, T value) {
        for (T element : array) {
            if (!Objects.equals(element, value))
                return false;
        }
        return true;
    }

    /**
     * Check if every element of this array is equal to the given value
     */
    public static boolean all(int[] array, int value) {
        for (int element : array) {
            if (!Objects.equals(element, value))
                return false;
        }
        return true;
    }

    public static String join(String delimiter, List<String> elements) {
        if (elements.size() == 0)
            return "";

        StringBuilder builder = new StringBuilder();
        builder.append(elements.get(0));

        for (int i = 1; i < elements.size(); i++) {
            builder.append(delimiter);
            builder.append(elements.get(i));
        }

        return builder.toString();
    }
}
