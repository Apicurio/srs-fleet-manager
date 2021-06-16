package org.bf2.srs.fleetmanager.rest.model;

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

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RegistryRestList.class, name = Kind.REGISTRY_LIST),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "kind",
        "items",
        "page",
        "size",
        "total"
})
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class AbstractList<T> {

    /**
     * (Required)
     */
    @JsonProperty("kind")
    @JsonPropertyDescription("Kind of the service")
    @NotNull
    protected String kind;

    /**
     * (Required)
     */
    @JsonProperty("items")
    @JsonPropertyDescription("")
    @NotNull
    protected List<T> items;

    /**
     * (Optional)
     */
    @JsonProperty("page")
    @JsonPropertyDescription("")
    @NotNull
    protected Integer page;

    /**
     * (Optional)
     */
    @JsonProperty("size")
    @JsonPropertyDescription("Size of the current view of items")
    protected Integer size;

    /**
     * (Optional)
     */
    @JsonProperty("total")
    @JsonPropertyDescription("Total number of items in list")
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
