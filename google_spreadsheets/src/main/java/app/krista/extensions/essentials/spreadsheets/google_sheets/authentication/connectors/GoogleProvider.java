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

package app.krista.extensions.essentials.spreadsheets.google_sheets.authentication.connectors;

import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extensions.essentials.spreadsheets.google_sheets.GoogleAttributes;
import app.krista.extensions.essentials.spreadsheets.google_sheets.authentication.store.RefreshTokenStore;
import app.krista.extensions.essentials.spreadsheets.google_sheets.constants.AuthConstants;
import app.krista.extensions.essentials.spreadsheets.google_sheets.constants.SpreadsheetConstants;
import app.krista.ksdk.context.AuthorizationContext;
import app.krista.ksdk.context.RequestContext;
import app.krista.model.field.NamedValuedField;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GoogleProvider {

    static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleProvider.class);

    private final RefreshTokenStore refreshTokenStore;
    private final GoogleAttributes gmailAttributes;
    private final RequestContext requestContext;
    private final AuthorizationContext authorizationContext;
    private final String authContextId;

    @Inject
    public GoogleProvider(RefreshTokenStore refreshTokenStore, GoogleAttributes googleAttributes, RequestContext requestContext, AuthorizationContext authorizationContext) {
        this(refreshTokenStore, googleAttributes, requestContext, authorizationContext, null);
    }

    public GoogleProvider(RefreshTokenStore refreshTokenStore, GoogleAttributes googleAttributes, RequestContext requestContext, AuthorizationContext authorizationContext, String authContextId) {
        this.refreshTokenStore = refreshTokenStore;
        this.gmailAttributes = googleAttributes;
        this.requestContext = requestContext;
        this.authorizationContext = authorizationContext;
        this.authContextId = authContextId;
    }

    public GoogleAttributes getGoogleAttributes() {
        return gmailAttributes;
    }

    public Drive getGoogleDriveClient() {
        return getGoogleDriveClient(!requestContext.invokeAsUser());
    }

    public Drive getGoogleClientForAdmin() {
        return getGoogleDriveClient(true);
    }


    public Sheets getGoogleSheetClient(){
        return getGoogleSheetClient(!requestContext.invokeAsUser());
    }

    private Sheets getGoogleSheetClient(boolean useEmail) {
        try {
            String userId = getUserId(useEmail);
            String refreshToken = refreshTokenStore.get(userId);
            if (refreshToken == null) {
                throw createMustAuthorizationException(userId, false);
            }
            return getGoogleSheet(userId, refreshToken);
        } catch (RuntimeException cause) {
            LOGGER.error(AuthConstants.FAILED_TO_GET_GRAPH_CLIENT + "{} {}", cause.getClass().getName(), cause.getMessage());
            throw cause;
        }
    }

    private Drive getGoogleDriveClient(boolean useEmail) {
        try {
            String userId = getUserId(useEmail);
            String refreshToken = refreshTokenStore.get(userId);
            if (refreshToken == null) {
                throw createMustAuthorizationException(userId, false);
            }
            return getGoogleDrive(userId, refreshToken);
        } catch (RuntimeException cause) {
            LOGGER.error(AuthConstants.FAILED_TO_GET_GRAPH_CLIENT + "{} {}", cause.getClass().getName(), cause.getMessage());
            throw cause;
        }
    }

    private Drive getGoogleDrive(String userId, String refreshToken) {
        try {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleCredential credential = getGoogleCredential(refreshToken, httpTransport);

            return getDriveService(httpTransport, credential);
        } catch (IOException | GeneralSecurityException cause) {
            if (!requestContext.invokeAsUser()) {
                throw new IllegalStateException("You are not authorized. Please ask admin to validate the attributes.");
            } else {
                throw createMustAuthorizationException(userId, true);
            }
        }
    }

    private Sheets getGoogleSheet(String userId, String refreshToken) {
        try {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleCredential credential = getGoogleCredential(refreshToken, httpTransport);

            return getSheetsService(httpTransport, credential);
        } catch (IOException | GeneralSecurityException cause) {
            if (!requestContext.invokeAsUser()) {
                throw new IllegalStateException("You are not authorized. Please ask admin to validate the attributes.");
            } else {
                throw createMustAuthorizationException(userId, true);
            }
        }
    }

    private Sheets getSheetsService(NetHttpTransport httpTransport, GoogleCredential credential) {
        // Create custom request initializer with increased timeouts for large operations
        // Default Java client timeout is 20 seconds, which is too short for operations with 1000+ rows
        // Reference: https://developers.google.com/display-video/api/guides/best-practices/timeouts
        HttpRequestInitializer requestInitializer = new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                credential.initialize(request);
                //request.setConnectTimeout(20_000);   // 20 seconds - connection timeout (standard)
                request.setReadTimeout(25_000);     // 120 seconds - read timeout for large operations
                //request.setWriteTimeout(20_000);     // 60 seconds - write timeout for uploads
            }
        };

        return new Sheets
                .Builder(httpTransport, JSON_FACTORY, requestInitializer)
                .setApplicationName(SpreadsheetConstants.APP_NAME_GOOGLE_SPREADSHEET)
                .build();
    }

    private Drive getDriveService(NetHttpTransport httpTransport, GoogleCredential credential) {
        return new Drive.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(SpreadsheetConstants.APP_NAME_GOOGLE_DRIVE)
                .build();
    }

    private GoogleCredential getGoogleCredential(String refreshToken, NetHttpTransport httpTransport) throws IOException {
        // validate if extn attributes has the required values else return err
        GoogleCredential credential =
                new GoogleCredential.Builder()
                        .setClientSecrets(gmailAttributes.getClientId(), gmailAttributes.getClientSecret())
                        .setJsonFactory(JacksonFactory.getDefaultInstance())
                        .setTransport(httpTransport)
                        .build();
        credential.setRefreshToken(refreshToken);
        credential.refreshToken();
        return credential;
    }

    public MustAuthorizeException createMustAuthorizationException(String userId, boolean reAuthentication) {
        LOGGER.error(reAuthentication ? AuthConstants.GOT_ERROR_FOR_AUTHENTICATION_SO_SENDING_FOR_RE_AUTHENTICATION : AuthConstants.GOT_ERROR_FOR_AUTHENTICATION_SO_SENDING_FOR_AUTHENTICATION);
        List<NamedValuedField> details = new ArrayList<>();
        NamedValuedField userIdField = new NamedValuedField(AuthConstants.USER_ID, AuthConstants.TEXT, userId, new HashMap<>(), new HashMap<>());
        details.add(userIdField);
        if (authContextId != null) {
            NamedValuedField contextIdField = new NamedValuedField(AuthConstants.AUTH_CONTEXT_ID, AuthConstants.TEXT, authContextId, new HashMap<>(), new HashMap<>());
            details.add(contextIdField);
        }
        return new MustAuthorizeException(reAuthentication ? AuthConstants.REFRESH_TOKEN_EXPIRED_PLEASE_REAUTHORIZE_YOURSELF : AuthConstants.AUTHORIZATION_PROMPT, details);
    }

    public String getUserId(boolean calledFromValidateAttributes) {
        String userId = calledFromValidateAttributes || !requestContext.invokeAsUser()
                ? gmailAttributes.getMailId()
                : authorizationContext.getAuthorizedAccount().getAccountId();
        if (userId == null) {
            throw new IllegalStateException(AuthConstants.FAILED_TO_GET_ACCOUNT);
        }
        return userId + AuthConstants.HASH + gmailAttributes.getClientId() + gmailAttributes.getClientSecret();
    }

    /**
     * This function is used to test if the connection with gmail is successfully authenticated.
     * If there is an exception we are parsing to check if it Unauthorised. If so creating must authorise exception.
     */
    public void testConnection() {
        try {
            getGoogleClientForAdmin().files().list().execute();
        } catch (IOException exception) {
            if (((GoogleJsonResponseException) exception).getStatusCode() == 401) {
                throw createMustAuthorizationException(getUserId(true), true);
            }
        }
    }
}
