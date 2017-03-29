package org.abonnier.mock.http.domain;

import lombok.Data;
import lombok.Getter;

/**
 * Created by abonnier on 09/03/2017.
 */
@Getter
public class Response {

    private int status;

    private int times;

    private long sleep;

    private String output;
}
