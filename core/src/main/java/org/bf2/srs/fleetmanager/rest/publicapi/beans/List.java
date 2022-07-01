
package org.bf2.srs.fleetmanager.rest.publicapi.beans;

import java.util.ArrayList;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "kind",
    "page",
    "size",
    "total",
    "items"
})
@Generated("jsonschema2pojo")
public class List {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("kind")
    private String kind;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("page")
    private Integer page;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("size")
    private Integer size;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("total")
    private Integer total;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("items")
    private java.util.List<ObjectReference> items = new ArrayList<ObjectReference>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("kind")
    public String getKind() {
        return kind;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("kind")
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("page")
    public Integer getPage() {
        return page;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("page")
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("size")
    public Integer getSize() {
        return size;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("size")
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("total")
    public Integer getTotal() {
        return total;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("total")
    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("items")
    public java.util.List<ObjectReference> getItems() {
        return items;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("items")
    public void setItems(java.util.List<ObjectReference> items) {
        this.items = items;
    }

}
