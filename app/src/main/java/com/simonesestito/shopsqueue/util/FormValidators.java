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

import android.content.Context;
import android.util.Patterns;

import com.google.android.material.textfield.TextInputLayout;
import com.simonesestito.shopsqueue.R;

/**
 * Utility class to execute validation on input fields.
 * All methods here, set or remove the error message from the fields
 */
@SuppressWarnings("WeakerAccess")
public class FormValidators {
    public static final int DEFAULT_MIN_STRING_LENGTH = 3;
    public static final int DEFAULT_MAX_STRING_LENGTH = 255;
    public static final int DEFAULT_MIN_PASSWORD_LENGTH = 8;

    /**
     * Ensure the length of the input text is in a given range
     */
    @SuppressWarnings("ConstantConditions")
    public static boolean hasLength(TextInputLayout inputLayout, int min, int max) {
        String input = inputLayout.getEditText().getText().toString().trim();
        Context context = inputLayout.getContext();

        if (input.isEmpty()) {
            inputLayout.setError(context.getString(R.string.form_field_required));
            return false;
        } else if (input.length() < min) {
            inputLayout.setError(context.getString(R.string.form_field_too_short));
            return false;
        } else if (input.length() > max) {
            inputLayout.setError(context.getString(R.string.form_field_too_long));
            return false;
        } else {
            inputLayout.setError("");
            return true;
        }
    }

    /**
     * Ensure the input text has the required length.
     * It uses the default minimum string length
     */
    public static boolean isString(TextInputLayout inputLayout) {
        return hasLength(inputLayout, DEFAULT_MIN_STRING_LENGTH, DEFAULT_MAX_STRING_LENGTH);
    }

    /**
     * Ensure the input text has the minimum required length
     */
    public static boolean hasMinLength(TextInputLayout inputLayout, int min) {
        return hasLength(inputLayout, min, DEFAULT_MAX_STRING_LENGTH);
    }

    /**
     * Checks if the input text has the length of a valid password
     */
    public static boolean isPassword(TextInputLayout inputLayout) {
        return hasLength(inputLayout, DEFAULT_MIN_PASSWORD_LENGTH, Integer.MAX_VALUE);
    }

    /**
     * Ensure the input text is a valid email address
     */
    @SuppressWarnings("ConstantConditions")
    public static boolean isEmail(TextInputLayout inputLayout) {
        if (!isString(inputLayout))
            return false;

        String input = inputLayout.getEditText().getText().toString().trim();
        Context context = inputLayout.getContext();

        if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            inputLayout.setError("");
            return true;
        } else {
            inputLayout.setError(context.getString(R.string.form_invalid_email_field));
            return false;
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean optional(TextInputLayout inputLayout, Validator validator) {
        String input = inputLayout.getEditText().getText().toString().trim();
        if (input.isEmpty())
            return true;
        else
            return validator.validate(inputLayout);
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isEan(TextInputLayout inputLayout) {
        Context context = inputLayout.getContext();
        String input = inputLayout.getEditText().getText().toString().trim();
        if (input.length() != 13) {
            inputLayout.setError(context.getString(R.string.invalid_ean_input));
            return false;
        }

        char[] chars = input.toCharArray();
        for (char c : chars) {
            if (!Character.isDigit(c)) {
                inputLayout.setError(context.getString(R.string.invalid_ean_input));
                return false;
            }
        }

        inputLayout.setError("");
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isPrice(TextInputLayout inputLayout) {
        Context context = inputLayout.getContext();
        String input = inputLayout.getEditText().getText().toString().trim();
        double price = Double.parseDouble(input);
        price = Math.round(price * 100) / 100.0;
        inputLayout.getEditText().setText(String.valueOf(price));

        if (price <= 0) {
            inputLayout.setError(context.getString(R.string.invalid_price_input));
            return false;
        }

        return true;
    }

    public interface Validator {
        boolean validate(TextInputLayout inputLayout);
    }
}
