/*
 * Google Spreadsheet Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.krista.extensions.essentials.spreadsheets.google_sheets.authentication.store;

import app.krista.extension.executor.Invoker;
import app.krista.extension.request.RoutingInfo;
import app.krista.extension.request.protos.http.HttpProtocol;
import app.krista.extensions.essentials.spreadsheets.google_sheets.GoogleAttributes;
import app.krista.extensions.essentials.spreadsheets.google_sheets.constants.SheetConstants;
import app.krista.extensions.util.KeyValueStore;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class GoogleAttributeStore {
    private final Invoker invoker;
    private final KeyValueStore store;

    @Inject
    public GoogleAttributeStore(Invoker invoker, KeyValueStore store) {
        this.invoker = invoker;
        this.store = store;
    }

    public GoogleAttributes load(String authContextId) throws IOException {
        var attributes = SheetConstants.GSON.fromJson(String.valueOf(store.get(authContextId)), Map.class);
        return new GoogleAttributes(invoker.getRoutingInfo().getRoutingURL(HttpProtocol.PROTOCOL_NAME, RoutingInfo.Type.APPLIANCE),
                (String) attributes.get(GoogleAttributes.CLIENT_ID),
                (String) attributes.get(GoogleAttributes.CLIENT_SECRET),
                (String) attributes.get(GoogleAttributes.EMAIL));
    }

    public String save(GoogleAttributes gmailAttributes) {
        String authContextId = UUID.randomUUID().toString();
        store.put(authContextId, SheetConstants.GSON.toJson(Map.of(GoogleAttributes.CLIENT_ID, gmailAttributes.getClientId(),
                GoogleAttributes.CLIENT_SECRET, gmailAttributes.getClientSecret(),
                GoogleAttributes.EMAIL, gmailAttributes.getMailId())));
        return authContextId;
    }

    /**
     * ¸
     * Removes given authContext id from KeyValueStore
     *
     * @param authContextId authContext id
     */
    public void remove(String authContextId) {
        store.remove(authContextId);
    }

}
