package org.abonnier.mock.http.domain.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

/**
 * Json File of the http Mock.
 * Created by abonnier on 09/03/2017.
 */
@Getter
public class HttpJsonFile {

    @JsonProperty(value = "mock-http")
    private List<Entry> entries;
}
