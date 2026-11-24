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

package app.krista.extensions.essentials.spreadsheets.google_sheets.constants;

public final class AuthConstants {
    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "clientSecret";
    public static final String SPACE = " ";
    public static final String TEXT = "Text";
    public static final String AUTH_CONTEXT_ID = "authContextId";
    public static final String FAILED_TO_GET_GRAPH_CLIENT = "Failed to get graph client ";
    public static final String ACCESS_TYPE_OFFLINE_APPROVAL_PROMPT_FORCE = "&prompt=consent&access_type=offline";
    public static final String FAILED_TO_GET_AUTHORIZE_RESPONSE = "Failed to get authorize response ";
    public static final String HASH = "#";
    public static final String FAILED_TO_AUTHENTICATE_USER = "Failed to authenticate user ";
    public static final String WS_CONTACT = "wsContact";
    // Authentication Error Messages - User-friendly format with clear action steps
    public static final String FAILED_TO_GET_ACCOUNT = "Account information retrieval failed due to authentication issues. Please re-authorize your Google account and ensure you have the necessary permissions.";
    public static final String USER_ID = "userId";
    public static final String GOT_ERROR_FOR_AUTHENTICATION_SO_SENDING_FOR_RE_AUTHENTICATION = "Authentication verification failed. Your session has expired and requires renewal. Please complete the re-authorization process to continue.";
    public static final String GOT_ERROR_FOR_AUTHENTICATION_SO_SENDING_FOR_AUTHENTICATION = "Authentication required to access Google Sheets. Please complete the authorization process to connect your Google account.";
    public static final String REFRESH_TOKEN_EXPIRED_PLEASE_REAUTHORIZE_YOURSELF = "Your Google account authorization has expired and needs to be renewed. Please click 'Authorize' to reconnect your Google account and restore access to your spreadsheets.";
    public static final String AUTHORIZATION_PROMPT = "Authorization required before proceeding. Please click 'Authorize' to connect your Google account, then click 'Validate Attributes' to verify the connection before saving your configuration.";


    private AuthConstants() {
    }
}
