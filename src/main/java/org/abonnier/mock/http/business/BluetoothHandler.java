package org.abonnier.mock.http.business;

import lombok.extern.slf4j.Slf4j;
import org.abonnier.mock.http.domain.json.BluetoothJsonFile;
import org.abonnier.mock.http.domain.json.Entry;
import org.abonnier.mock.http.domain.json.HttpJsonFile;
import org.abonnier.mock.http.domain.json.Response;
import org.abonnier.mock.http.service.BluetoothService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.microedition.io.StreamConnection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handler of the Bluetooth connection from a client.
 * Read the data sent and print it in the console.
 * The data is sent back to the client.
 * Created by anthony on 02/05/2017.
 */
@Slf4j
public class BluetoothHandler implements Runnable {

    private static final int BUFFER_SIZE = 128;

    private static final long MINIMUM_SLEEP = 1000L;

    private BluetoothService bluetoothService;

    private Map<String, Entry> entriesMap = new HashMap<>();

    public BluetoothHandler(final BluetoothService srv, final BluetoothJsonFile jf) {
        bluetoothService = srv;
        updateRoutes(jf);
    }

    /**
     * Update the routes from the json file.
     *
     * @param jf containing the config.
     */
    public synchronized void updateRoutes(final BluetoothJsonFile jf) {
        entriesMap = jf.getEntries().parallelStream().collect(Collectors.toMap(Entry::getInput, Function.identity()));
    }

    /**
     * Constant loop to wait client connection then reading the data sent.
     */
    @Override
    public void run() {
        while (true) {
            try {
                // Waiting client connection
                StreamConnection sc = bluetoothService.getScn().acceptAndOpen();

                log.info("Bluetooth connection opened!");

                // Buffer to read data
                byte[] rawData = new byte[BUFFER_SIZE];

                try {
                    // Input stream from connection
                    DataInputStream dataIn = sc.openDataInputStream();
                    DataOutputStream dataOut = sc.openDataOutputStream();

                    // Reading client inputs
                    while (dataIn.read(rawData) >= 0) {
                        long start = System.currentTimeMillis();

                        // Bytes to String + reset buffer
                        String input = new String(rawData);
                        log.info("Bluetooth client sending data : {} ", input);
                        rawData = new byte[BUFFER_SIZE];

                        // Input recieved
                        if (StringUtils.isNotBlank(input)) {
                            // Retrieving entry from the given input
                            final Entry entry = entriesMap.get(input);

                            // Can be null if input did not match
                            if (entry != null) {
                                final Response response = entry.getOutput().getNextResponse();
                                // Can be null if repeat = false
                                if (response != null) {
                                    try {
                                        handleSleep(start, response);
                                    } catch (InterruptedException e) {
                                        log.warn("Interrupted thread in bluetooth.", e);
                                    }

                                    // Sending data back to the client
                                    log.info("Sending response to the client : {} ", response.getOutput());
                                    dataOut.writeBytes(response.getOutput());
                                    continue;
                                }
                            }

                            // Default case, sending unknown to client
                            log.warn("Sending unknown response to the client : UNKNOWN ");
                            dataOut.writeBytes("UNKNOWN");

                        } else {

                            // Checking if the bluetooth service contains queued commands
                            if (!bluetoothService.getBluetoothQueue().isEmpty()) {
                                final String cmd = bluetoothService.getBluetoothQueue().poll();
                                if (StringUtils.isNotBlank(cmd)) {
                                    log.info("Sending queued bluetooth command : {}", cmd);
                                    dataOut.writeBytes(cmd);

                                    try {
                                        Thread.sleep(MINIMUM_SLEEP);
                                    } catch (InterruptedException e) {
                                        log.warn("Minimum sleep between bluetooth commands interrupted.", e);
                                    }

                                    continue;
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    log.warn("Unable to receive data from client anymore.", e);
                    break;
                }

                // Closing stream connection
                sc.close();
                log.info("Bluetooth connection closed!");

            } catch (IOException e) {
                log.error("Error while waiting for bluetooth client.", e);
            }
        }
    }

    private void handleSleep(long start, Response response) throws InterruptedException {
        long sleep = 0L;
        if (response.hasSleep()) {
            sleep = response.getSleep();
        }

        // Sleep management
        if (start > 0 && sleep > 0) {
            // The delta is the time the previous instructions take to be executed
            long delta = System.currentTimeMillis() - start;

            // Only if the delta is lesser than the response sleep, otherwise, the response is returned immediately
            if (delta < sleep) {
                sleep -= delta;
                // Sleep during the found response sleep minus the delta time of the previous instructions
                Thread.sleep(sleep);
            }
        }
    }
}
