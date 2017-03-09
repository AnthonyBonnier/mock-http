package org.abonnier.mock.http.domain;

import lombok.Data;

/**
 * Created by abonnier on 09/03/2017.
 */
@Data
public class Response {

    private int status;

    private int times;

    private long sleep;

    private String output;
}
