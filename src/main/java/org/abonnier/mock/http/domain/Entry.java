package org.abonnier.mock.http.domain;

import lombok.Data;

/**
 * Created by abonnier on 09/03/2017.
 */
@Data
public class Entry {

    private boolean repeat;

    private String input;

    private Output output;
}
