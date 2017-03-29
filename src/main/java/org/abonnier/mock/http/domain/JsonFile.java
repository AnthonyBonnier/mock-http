package org.abonnier.mock.http.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * Created by abonnier on 09/03/2017.
 */
@Getter
public class JsonFile {

    @JsonProperty(value = "mock-http")
    private List<Entry> entries;
}
