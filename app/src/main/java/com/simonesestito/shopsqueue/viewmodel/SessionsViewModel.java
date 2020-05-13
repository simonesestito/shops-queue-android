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

package com.simonesestito.shopsqueue.viewmodel;

import androidx.lifecycle.ViewModel;

import com.simonesestito.shopsqueue.api.dto.Session;
import com.simonesestito.shopsqueue.api.service.SessionService;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;

import java.util.List;

import javax.inject.Inject;

public class SessionsViewModel extends ViewModel {
    private final SessionService sessionService;
    private LiveResource<List<Session>> sessions = new LiveResource<>();

    @Inject
    SessionsViewModel(SessionService sessionService) {
        this.sessionService = sessionService;
        refreshSessions();
    }

    public LiveResource<List<Session>> getSessions() {
        return sessions;
    }

    private void refreshSessions() {
        sessions.emitLoading();
        sessionService.listCurrentUserSessions()
                .onResult(sessions::emitResult)
                .onError(sessions::emitError);
    }

    public void revokeSession(int id) {
        sessions.emitLoading();
        sessionService.revokeSession(id)
                .onResult(a -> refreshSessions())
                .onError(err -> {
                    sessions.emitError(err);
                    refreshSessions();
                });
    }
}
