package org.b2f.ams.model.request;

public class AccessReview {

    String accountUsername;
    Action action;
    String resourceType;

    public enum Action {
        GET, LIST, CREATE, DELETE, UPDATE;
    }
}
