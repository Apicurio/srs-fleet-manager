package org.b2f.ams.client.auth;

import org.keycloak.TokenVerifier;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.common.VerificationException;
import org.keycloak.common.util.Time;
import org.keycloak.representations.AccessToken;

import java.util.HashMap;
import java.util.Map;

public class KeycloakAuth implements Auth {

    private static final String BEARER = "Bearer ";

    private final AuthzClient keycloak;
    private AccessToken accessTokenParsed;
    private String accessToken;

    public KeycloakAuth(String serverUrl, String realm, String clientId, String clientSecret) {

        final HashMap<String, Object> credentials = new HashMap<>();
        credentials.put("secret", clientSecret);
        final Configuration configuration = new Configuration(serverUrl, realm, clientId, credentials, null);
        this.keycloak = AuthzClient.create(configuration);
    }

    @Override
    public void apply(Map<String, String> requestHeaders) {
        try {
            if (isAccessTokenRequired()) {
                this.accessToken = this.keycloak.obtainAccessToken().getToken();
                this.accessTokenParsed = TokenVerifier.create(this.accessToken, AccessToken.class).getToken();
            }
            requestHeaders.put("Authorization", BEARER + accessToken);
        } catch (VerificationException e) {
            throw new IllegalStateException(e);
        }
    }

    private boolean isAccessTokenRequired() {
        return null == accessTokenParsed || isTokenExpired();
    }

    private boolean isTokenExpired() {
        return (accessTokenParsed.getExp() != null && accessTokenParsed.getExp() != 0L) && (long) Time.currentTime() > accessTokenParsed.getExp();
    }

    public static class Builder {
        private String serverUrl;
        private String realm;
        private String clientId;
        private String clientSecret;

        public Builder() {
        }

        public Builder withRealm(String realm) {
            this.realm = realm;
            return this;
        }

        public Builder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder withClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder withServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }

        public KeycloakAuth build() {
            return new KeycloakAuth(this.serverUrl, this.realm, this.clientId, this.clientSecret);
        }
    }

}
