package org.bf2.srs.fleetmanager.util;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Scanner;

/**
 * Container that transforms query string into hibernate query
 */
public class SearchQuery {

    private String query;
    private Object[] arguments;
    private String search;
    private List<String> allowedFields;

    public SearchQuery(String search, List<String> allowedFields) {
        this.search = search;
        this.allowedFields = allowedFields;
        this.query = "";
        buildQuery();
    }

    // TODO Use Antlr for full query parsing - current version supports only single value
    private void buildQuery() {
        var searchExpr = search.split("=");
        if (searchExpr.length != 2) {
            throw new ValidationException("Invalid search query. Currently search supports only single key=value strings pair");
        }
        if (!allowedFields.contains(searchExpr[0])) {
            throw new ValidationException(String.format("invalid search query key that is not matching allowed values %s ", this.allowedFields.toString()));
        }

        this.query = searchExpr[0] +" = ?1";
        this.arguments = new Object[]{searchExpr[1]};
    }

    public String getQuery() {
        return this.query;
    }

    public Object[] getArguments() {
        return this.arguments;
    }
}
