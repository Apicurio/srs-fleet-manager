/*
 * Copyright 2021 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bf2.srs.fleetmanager.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ValidationException;

/**
 * Container that transforms query string into a simple hibernate query with only one condition
 */
public class BasicQuery {
    private String query;
    private String column;
    private Object argument;
    private String search;
    private List<String> allowedFields;

    public BasicQuery(String search, List<String> allowedFields) {
        this.search = search;
        this.allowedFields = allowedFields;
        this.query = "";
        buildQuery();
    }

    // TODO Use Antlr for full query parsing - current version supports only single value
    private void buildQuery() {
        var searchExpr = Stream.of(search.split("="))
            .filter(s -> !s.isBlank())
            .map(String::trim)
            .collect(Collectors.toList());
        if (searchExpr.size() != 2) {
            throw new ValidationException("Invalid search query. Currently search supports only single key=value strings pair");
        }
        if (!allowedFields.contains(searchExpr.get(0))) {
            throw new ValidationException(String.format("invalid search query key that is not matching allowed values %s ", this.allowedFields.toString()));
        }

        this.column = searchExpr.get(0);
        this.query = this.column + " = ?1";
        this.argument = searchExpr.get(1);
    }

    public String getQuery() {
        return this.query;
    }

    public String getColumn() {
        return this.column;
    }

    public Object getArgument() {
        return this.argument;
    }
}
