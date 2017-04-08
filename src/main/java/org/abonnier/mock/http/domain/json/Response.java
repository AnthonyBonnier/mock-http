package org.abonnier.mock.http.domain.json;

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

    /**
     * @return true if this Response has a sleep value > 0
     */
    public boolean hasSleep() {
        return sleep > 0;
    }
}
