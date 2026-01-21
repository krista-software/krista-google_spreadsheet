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

package app.krista.extensions.essentials.spreadsheets.google_sheets.authentication;

import app.krista.extensions.essentials.spreadsheets.google_sheets.GoogleAttributes;
import app.krista.extensions.essentials.spreadsheets.google_sheets.authentication.connectors.GoogleProvider;
import app.krista.extensions.essentials.spreadsheets.google_sheets.authentication.connectors.GoogleProviderFactory;
import app.krista.extensions.essentials.spreadsheets.google_sheets.authentication.store.GoogleAttributeStore;
import app.krista.extensions.essentials.spreadsheets.google_sheets.authentication.store.RefreshTokenStore;
import app.krista.extensions.essentials.spreadsheets.google_sheets.constants.AuthConstants;
import app.krista.extensions.essentials.spreadsheets.google_sheets.constants.SheetConstants;
import app.krista.ksdk.authentication.AuthorizationListener;
import app.krista.ksdk.context.AuthorizationContext;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.github.scribejava.core.model.OAuthConstants.CODE;
import static com.github.scribejava.core.model.OAuthConstants.STATE;


@Path("/")
public final class AuthenticationResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationResource.class);
    private final GoogleAttributeStore googleAttributeStore;
    private final RefreshTokenStore refreshTokenStore;
    private final GoogleProviderFactory providerFactory;
    private final AuthorizationListener authorizationListener;
    private final AuthorizationContext context;


    @Inject
    public AuthenticationResource(GoogleAttributeStore googleAttributeStore, RefreshTokenStore refreshTokenStore, GoogleProviderFactory providerFactory, AuthorizationListener authorizationListener, AuthorizationContext context) {
        this.googleAttributeStore = googleAttributeStore;
        this.refreshTokenStore = refreshTokenStore;
        this.providerFactory = providerFactory;
        this.authorizationListener = authorizationListener;
        this.context = context;
    }

    @GET
    @Path("/callback")
    public String getCallBack(@QueryParam(CODE) String code, @QueryParam(STATE) String state) {
        Objects.requireNonNull(code);
        String[] parts = state.split(AuthConstants.HASH);
        if (parts[0].isBlank() || parts.length > 3) {
            throw new BadRequestException("Invalid state parameters!");
        }
        String key = parts[0] + AuthConstants.HASH + parts[1];
        String authContextId = parts.length == 3 ? parts[2] : null;

        try {
            GoogleProvider clientProvider = getGoogleClientProvider(authContextId);
            OAuth2AccessToken accessToken = clientProvider.getGoogleAttributes().getOAuth20Service().getAccessToken(code);
            String refreshTokenString = accessToken.getRefreshToken();
            String accessTokenString = accessToken.getAccessToken();
            String refreshTokenStored = refreshTokenStore.get(key);

            // Google Provides 1 refresh token at a time
            // For studio user key starts with setup emailId
            // For client user key starts with wsContact
            // if the refresh token fetched is for client then store access token
            if (key.startsWith(AuthConstants.WS_CONTACT) && refreshTokenString == null) {
                refreshTokenStore.put(key, accessTokenString); // Storing access token for client
            }
            // if the refresh token stored for studio user is access token then revoke the token
            else if (refreshTokenStored != null && refreshTokenStored.startsWith("ya29")) {
                LOGGER.error("Refresh token access removed. Re-authorization is needed.");
                // Revoking access token to get refresh token for existing studio users
                clientProvider.getGoogleAttributes().getOAuth20Service().revokeToken(accessToken.getAccessToken());
                refreshTokenStore.remove(key);
                return "We need to remove your access token permissions. Please validate again to regain access.";
            }
            // if the refresh token is not null and key doesn't start with wsContact
            // store refresh token for studio user
            else {
                refreshTokenStore.put(key, refreshTokenString);
            }

            if (!key.startsWith(AuthConstants.WS_CONTACT) && !hasUserAccess(clientProvider)) {
                refreshTokenStore.remove(key);
                return "Unauthorised user. User email '" + key.split("#")[0] + "' configured in the setup does not match with authenticated user.";
            }
            if (context.isAuthenticated()) {
                authorizationListener.authorized();
                return "User authenticated successfully.";
            }
            return "User authenticated successfully. Save the changes.";
        } catch (OAuthException cause) {
            throw new IllegalStateException(getErrorDescription(cause), cause.getCause());
        } catch (IOException | ExecutionException cause) {
            throw new IllegalStateException("Error occurred during authorization ", cause.getCause());
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Error occurred during authorization ", interruptedException.getCause());
        } finally {
            if (authContextId != null) {
                googleAttributeStore.remove(authContextId);
            }
        }
    }

    private GoogleProvider getGoogleClientProvider(String authContextId) throws IOException {
        GoogleProvider clientProvider;
        if (authContextId == null) {
            clientProvider = providerFactory.create();
        } else {
            GoogleAttributes effectiveGmailAttributes = googleAttributeStore.load(authContextId);
            clientProvider = providerFactory.create(effectiveGmailAttributes);
        }
        return clientProvider;
    }

    private String getErrorDescription(OAuthException cause) {
        String errorDescription = "Error occurred during authorization. ";
        if (cause.getMessage() != null && !cause.getMessage().isBlank()) {
            var exceptionMap = SheetConstants.GSON.fromJson(cause.getMessage(), Map.class);
            if (exceptionMap.containsKey("error")) {
                errorDescription += exceptionMap.get("error") + ". ";
            }
            if (exceptionMap.containsKey("error_description")) {
                errorDescription += exceptionMap.get("error_description");
            }
        }
        return errorDescription;
    }

    private boolean hasUserAccess(GoogleProvider googleProvider) {
        try {
            googleProvider.getGoogleDriveClient().files().list().execute();
            return true;
        } catch (IOException cause) {
            LOGGER.error("-------IGNORED EXCEPTION-----{}", cause.getMessage());
            return false;
        }
    }

}
