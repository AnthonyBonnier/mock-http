package org.abonnier.mock.http.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

/**
 * Created by abonnier on 09/03/2017.
 */
public class JsonFile {

    @JsonProperty(value = "mock-http")
    private List<Entry> entries;
}
