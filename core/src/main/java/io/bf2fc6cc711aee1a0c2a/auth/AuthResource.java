package io.bf2fc6cc711aee1a0c2a.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PACKAGE;

@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
public class AuthResource {

    private String serverUrl;
    private String clientId;
}
