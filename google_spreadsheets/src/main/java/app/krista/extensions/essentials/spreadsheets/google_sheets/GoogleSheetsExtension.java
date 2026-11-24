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

package app.krista.extensions.essentials.spreadsheets.google_sheets;

import app.krista.extension.authorization.RequestAuthenticator;
import app.krista.extension.executor.Invoker;
import app.krista.extension.impl.anno.*;
import app.krista.extensions.essentials.spreadsheets.google_sheets.authentication.connectors.GoogleProviderFactory;
import app.krista.extensions.essentials.spreadsheets.google_sheets.authentication.store.GoogleAttributeStore;
import app.krista.extensions.essentials.spreadsheets.google_sheets.constants.SheetConstants;
import app.krista.ksdk.context.AuthorizationContext;

import javax.inject.Inject;
import java.util.Map;

@Java(version = Java.Version.JAVA_21)
@Field.Text(value = GoogleAttributes.CLIENT_ID)
@Field.Text(value = GoogleAttributes.CLIENT_SECRET, isSecured = true)
@Field.Text(value = GoogleAttributes.EMAIL)
@Extension(jaxrsId = SheetConstants.JAXRS_ID,name = "Google Spreadsheet", version = "2.0.7")
@ChangeLog(file = "confluence/src/main/resources/documentation/releaseNotes.md")
@StaticResource(path = "docs", file = "docs")
public final class GoogleSheetsExtension {

    private final Invoker invoker;
    private final GoogleAttributes gmailAttributes;
    private final GoogleProviderFactory clientProviderFactory;
    private final GoogleSheetsAuthenticator requestAuthenticator;


    @Inject
    public GoogleSheetsExtension(Invoker invoker, GoogleAttributes gmailAttributes, GoogleProviderFactory clientProviderFactory, GoogleAttributeStore gmailAttributeStore, AuthorizationContext context) {
        this(invoker, gmailAttributes,
                clientProviderFactory,
                new GoogleSheetsAuthenticator(gmailAttributeStore, gmailAttributes, context));
    }

    public GoogleSheetsExtension(Invoker invoker, GoogleAttributes gmailAttributes, GoogleProviderFactory clientProviderFactory, GoogleSheetsAuthenticator requestAuthenticator) {
        this.invoker = invoker;
        this.gmailAttributes = gmailAttributes;
        this.clientProviderFactory = clientProviderFactory;
        this.requestAuthenticator = requestAuthenticator;
    }



    @InvokerRequest(InvokerRequest.Type.AUTHENTICATOR)
    public RequestAuthenticator getRequestAuthenticator() {
        return requestAuthenticator;
    }

    @InvokerRequest(InvokerRequest.Type.CUSTOM_TABS)
    public Map<String, String> customTabs() {
        return Map.of("Documentation", "static/docs");
    }

    /**
     * Test google calendar connection by authenticating the user with UNSAVED i.e UI layer Client Id and Client secrete data
     * and fetching the user events info.
     */
    @InvokerRequest(InvokerRequest.Type.VALIDATE_ATTRIBUTES)
    public void validateConnection(Map<String, Object> connectionAttributes)  {
        GoogleAttributes attributes = GoogleAttributes.create(invoker, connectionAttributes);
        clientProviderFactory.create(attributes).testConnection();
    }

    /**
     * Test google calendar connection by authenticating the user with SAVED i.e database layer Client Id and Client secrete data
     * and fetching the user events info.
     */
    @InvokerRequest(InvokerRequest.Type.TEST_CONNECTION)
    public void testConnection()  {
        clientProviderFactory.create().testConnection();
    }

    /**
     * Method to validate attributes when any updates in existing Invoker parameters.
     */
    @InvokerRequest(InvokerRequest.Type.INVOKER_UPDATED)
    public void attributesUpdated(Map<String, Object> oldAttributes, Map<String, Object> newAttributes) {
        gmailAttributes.update(newAttributes);
    }

}
