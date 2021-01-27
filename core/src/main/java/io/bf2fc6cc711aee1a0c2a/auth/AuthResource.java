package io.bf2fc6cc711aee1a0c2a.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResource {

    private String serverUrl;
    private String clientId;
}
