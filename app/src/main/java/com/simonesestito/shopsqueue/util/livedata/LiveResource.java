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

package com.simonesestito.shopsqueue.util.livedata;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.simonesestito.shopsqueue.util.functional.Handler;

/**
 * A new fully fledged subclass of {@link LiveData}.
 * <p>
 * In addition to the advantages of using LiveData,
 * this implementation provides:
 * - a proper error handling
 * - a way to manage those "single one-shot event" situations
 * - the ability to clear the stored value
 * - indicate the loading status
 *
 * @param <T> Type of object to store
 */
public class LiveResource<T> extends LiveData<Resource<T>> {
    /**
     * Attach an observer which will be called only if the event
     * hasn't been handled yet by another Observer
     */
    public void observeUnhandled(LifecycleOwner lifecycleOwner, Handler<Resource<T>> handler) {
        observe(lifecycleOwner, event -> {
            if (event.hasToBeHandled()) {
                boolean handled = handler.handle(event);
                if (handled)
                    event.handle();
            }
        });
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super Resource<T>> observer) {
        super.observe(owner, eventOrNull -> {
            if (eventOrNull != null)
                observer.onChanged(eventOrNull);
        });
    }

    /**
     * Emit a new event with a result
     */
    public void emitResult(T result) {
        setValue(Resource.successful(result));
    }

    /**
     * Emit a new event reporting an error occurred
     */
    public void emitError(Throwable error) {
        setValue(Resource.error(error));
    }

    /**
     * Emit a new event indicating the task is in progress
     */
    public void emitLoading() {
        if (getValue() == null || !getValue().isLoading())
            setValue(Resource.loading());
    }

    /**
     * Clear the actually stored value
     */
    public void clearValue() {
        setValue(null);
    }
}
