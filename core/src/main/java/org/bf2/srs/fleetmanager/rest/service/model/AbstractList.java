package org.bf2.srs.fleetmanager.rest.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class AbstractList<T> {

    /**
     * (Required)
     */
    @NotNull
    protected String kind;

    /**
     * (Required)
     */
    @NotNull
    protected List<T> items;

    /**
     * (Optional)
     */
    @NotNull
    protected Integer page;

    /**
     * (Optional)
     */
    protected Integer size;

    /**
     * (Optional)
     */
    protected Long total;

    protected AbstractList(@NotNull String kind, @NotNull List<T> items, @NotNull Integer page, Integer size, Long total) {
        requireNonNull(kind);
        this.kind = kind;
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
    }
}
