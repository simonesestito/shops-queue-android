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

import androidx.annotation.Nullable;

public class Event<T> {
    private final T content;
    private boolean handled = false;

    public Event(T content) {
        this.content = content;
    }

    /**
     * Get the content if not handled
     * and flag it as handled
     */
    @Nullable
    public T handle() {
        if (handled) {
            return null;
        } else {
            handled = true;
            return content;
        }
    }

    /**
     * Get if the current event has already been handled
     */
    public boolean hasBeenHandled() {
        return handled;
    }

    /**
     * Get the content in any case,
     * and DO NOT check it has handled
     */
    public T peek() {
        return content;
    }
}
