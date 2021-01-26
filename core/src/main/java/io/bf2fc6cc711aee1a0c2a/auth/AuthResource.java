package io.bf2fc6cc711aee1a0c2a.auth;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResource {

    private String serverUrl;
    private String realm;
    private String clientId;

    public String getServerUrl() {
        return serverUrl + "/" + realm;
    }
}
