package org.abonnier.mock.http.domain.json;

import lombok.Getter;

/**
 * Created by abonnier on 09/03/2017.
 */

public class Entry {

    @Getter
    private String input;

    private Output output;

    public Output getOutput() {
        return output.initialize();
    }
}
