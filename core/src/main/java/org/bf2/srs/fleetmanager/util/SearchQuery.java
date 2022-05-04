package org.bf2.srs.fleetmanager.util;

import javax.validation.ValidationException;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Container that transforms query string into hibernate query, for now it only uses AND conditions
 */
public class SearchQuery {

    private String query;
    private Object[] arguments;
    private List<Pair<String, Object>> search;

    public SearchQuery(List<Pair<String, Object>> search) {
        this.search = search;
        this.query = "";
        buildQuery();
    }

    // TODO Use Antlr for full query parsing - current version supports only "and" conditions
    private void buildQuery() {
        if (this.search.isEmpty()) {
            throw new ValidationException("Invalid search query. Search query cannot be empty");
        }

        List<Object> args = new ArrayList<>();
        int index = 1;
        for (Pair<String, Object> pair : search) {
            if (!this.query.isEmpty()) {
                this.query += " and ";
            }
            this.query += pair.getKey() + " = ?" + index;
            args.add(pair.getValue());
            index++;
        }
        this.arguments = args.toArray();
    }

    public String getQuery() {
        return this.query;
    }

    public Object[] getArguments() {
        return this.arguments;
    }
}
