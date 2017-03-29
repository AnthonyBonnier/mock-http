package org.abonnier.mock.http.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

/**
 * Created by abonnier on 09/03/2017.
 */
@Getter
public class Output {

    private Mode mode;

    @JsonProperty(value = "content-type")
    private String contentType;

    private List<Response> responses;
}
