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

import app.krista.extension.authorization.MustAuthenticateException;
import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extension.authorization.RequestAuthenticator;
import app.krista.extension.request.ProtoRequest;
import app.krista.extension.request.ProtoResponse;
import app.krista.extension.request.protos.http.HttpRequest;
import app.krista.extensions.essentials.spreadsheets.google_sheets.authentication.store.GoogleAttributeStore;
import app.krista.extensions.essentials.spreadsheets.google_sheets.constants.AuthConstants;
import app.krista.ksdk.context.AuthorizationContext;
import app.krista.model.field.NamedField;
import app.krista.model.field.NamedValuedField;
import com.github.scribejava.core.oauth.OAuth20Service;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.*;

import static com.github.scribejava.core.model.OAuthConstants.STATE;

public class GoogleSheetsAuthenticator implements RequestAuthenticator {
    private final GoogleAttributeStore googleAttributeStore;
    private final GoogleAttributes googleAttributes;
    private final AuthorizationContext context;

    public GoogleSheetsAuthenticator(GoogleAttributeStore googleAttributeStore, GoogleAttributes gmailAttributes, AuthorizationContext context) {
        this.googleAttributeStore = googleAttributeStore;
        this.googleAttributes = gmailAttributes;
        this.context = context;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public Set<String> getSupportedProtocols() {
        return Set.of();
    }

    @Override
    public String getAuthenticatedAccountId(ProtoRequest protoRequest) {
        try {
            HttpRequest httpRequest = (HttpRequest) protoRequest;
            if (httpRequest.getUri().getPath().equals("/gsextn/callback")) {
                httpRequest.bufferBody();
                MultivaluedMap<String, String> queryParameters = httpRequest.getQueryParameters();
                String state = String.valueOf(queryParameters.get(STATE).getFirst());
                String[] parts = state.split(AuthConstants.HASH);
                return parts[0];
            }
            else {
                throw new RuntimeException("Failed to get authenticated userid.");
            }
        } catch (IOException cause) {
            throw new RuntimeException(AuthConstants.FAILED_TO_AUTHENTICATE_USER + cause.getMessage(), cause.getCause());
        }
    }

    @Override
    public boolean setServiceAuthorization(String s) {
        return false;
    }

    @Override
    public Map<String, NamedField> getAttributeFields() {
        return Map.of();
    }

    @Override
    public ProtoResponse getMustAuthenticateResponse(MustAuthenticateException cause, ProtoRequest request) {
        return null;
    }

    @Override
    public AuthorizationResponse getMustAuthenticateResponse(MustAuthenticateException cause) {
        return null;
    }

    @Override
    public ProtoResponse getMustAuthorizeResponse(MustAuthorizeException cause, ProtoRequest request) {
        return null;
    }

    @Override
    public AuthorizationResponse getMustAuthorizeResponse(MustAuthorizeException cause) {
        try {
            String userId = (String) cause.getDetails().getFirst().getValue();
            Optional<NamedValuedField> authContextIdField = cause.getDetails().stream().filter(f -> Objects.equals(f.getName(), AuthConstants.AUTH_CONTEXT_ID)).findFirst();

            GoogleAttributes effectiveGoogleAttributes;
            String state;
            if (authContextIdField.isPresent()) {
                String authContextId = (String) authContextIdField.get().getValue();
                effectiveGoogleAttributes = googleAttributeStore.load(authContextId);
                state = userId + AuthConstants.HASH + authContextId;
            } else {
                effectiveGoogleAttributes = googleAttributes;
                state = userId;
            }

            OAuth20Service oAuth20Service = effectiveGoogleAttributes.getOAuth20Service();
            String url = oAuth20Service.getAuthorizationUrl(state);
            String urlQueryParam = AuthConstants.ACCESS_TYPE_OFFLINE_APPROVAL_PROMPT_FORCE;
            return new AuthorizationResponse(url + urlQueryParam, List.of());
        } catch (IOException ioException) {
            throw new RuntimeException(AuthConstants.FAILED_TO_GET_AUTHORIZE_RESPONSE, ioException);
        }
    }
}
