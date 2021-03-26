package org.b2f.ams.client.auth;

import java.util.Map;

public interface Auth {

    void apply(Map<String, String> headers);
}
