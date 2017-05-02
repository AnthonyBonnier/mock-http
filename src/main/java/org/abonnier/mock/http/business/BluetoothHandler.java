package org.abonnier.mock.http.business;

import lombok.extern.slf4j.Slf4j;
import org.abonnier.mock.http.service.BluetoothService;

import javax.microedition.io.StreamConnection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Handler of the Bluetooth connection from a client.
 * Read the data sent and print it in the console.
 * The data is sent back to the client.
 * Created by anthony on 02/05/2017.
 */
@Slf4j
public class BluetoothHandler implements Runnable {

    private static final int BUFFER_SIZE = 128;

    private BluetoothService bluetoothService;

    public BluetoothHandler(BluetoothService srv) {
        bluetoothService = srv;
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

                    // Reading client input
                    while (dataIn.read(rawData) > 0) {
                        String s = new String(rawData);
                        log.info("Bluetooth client sending data : {} ", s);
                        rawData = new byte[BUFFER_SIZE];

                        // Sending data back to the client
                        log.info("Sending response to the client : {} ", s);
                        dataOut.writeBytes(s);
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
}
