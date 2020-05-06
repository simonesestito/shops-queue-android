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

import com.simonesestito.shopsqueue.Constants;

public class NumberUtils {
    /**
     * Round a number to the amount of digits after comma required
     */
    public static double roundDigits(double number, int digits) {
        double pow = Math.pow(10, digits);
        return Math.floor(number * pow) / pow;
    }

    public static double roundCoordinate(double coordinate) {
        return roundDigits(coordinate, Constants.COORDINATES_DIGITS_PRECISION);
    }
}
