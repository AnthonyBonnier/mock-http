package org.abonnier.mock.http.domain.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

/**
 * Json File of the Bluetooth Mock.
 * Created by Anthony on 03/05/2017.
 */
@Getter
public class BluetoothJsonFile {
    @JsonProperty(value = "mock-bluetooth")
    private List<Entry> entries;
}
