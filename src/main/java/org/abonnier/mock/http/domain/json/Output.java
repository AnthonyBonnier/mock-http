package org.abonnier.mock.http.domain.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by abonnier on 09/03/2017.
 */
@Getter
public class Output {

    private boolean repeat;

    private Mode mode;

    @JsonProperty(value = "content-type")
    private String contentType;

    private List<Response> responses;

    @JsonIgnore
    private int currentResponse = 0;

    @JsonIgnore
    private List<Integer> reponsesIndexesList = new ArrayList<>();

    @JsonIgnore
    private boolean initialized = false;

    /**
     * It generates the responses indexes list according to the current {@link Mode}.
     * @return itself to make it fluent.
     */
    public Output initialize() {
        if (!initialized) {
            int responseIndex = 0;
            for (Response r : responses) {
                switch (mode) {
                    case SORTED:
                    case RANDOM:
                        for (int i = 0; i < r.getTimes(); i++) {
                            reponsesIndexesList.add(responseIndex);
                        }
                        break;

                    case SWITCH:
                        throw new IllegalArgumentException("Not implemented !");
                    default:
                        throw new IllegalArgumentException("Unmanaged mode : " + mode);
                }

                responseIndex++;
            }

            if (Mode.RANDOM == mode) {
                shuffleIndexes();
            }
            initialized = true;
        }

        return this;
    }

    private void shuffleIndexes() {
        Collections.shuffle(reponsesIndexesList, new SecureRandom(String.valueOf(System.nanoTime()).getBytes()));
    }

    public Response getNextResponse() {
        Response next = null;

        if (currentResponse >= 0) {
            next = responses.get(reponsesIndexesList.get(currentResponse));

            switch (mode) {
                case SORTED:
                case RANDOM:
                    currentResponse++;
                    break;

                case SWITCH:
                    throw new IllegalArgumentException("Not implemented !");

                default:
                    throw new IllegalArgumentException("Unmanaged mode : " + mode);
            }

            if (currentResponse >= reponsesIndexesList.size()) {
                if (repeat) {
                    currentResponse = 0;
                    if (Mode.RANDOM == mode) {
                        shuffleIndexes();
                    }
                } else {
                    currentResponse = -1;
                }
            }
        }

        return next;
    }
}
