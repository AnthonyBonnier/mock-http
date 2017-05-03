package org.abonnier.mock.http.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.abonnier.mock.http.business.BluetoothHandler;
import org.abonnier.mock.http.domain.json.BluetoothJsonFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Start the Bluetooth service.
 * http://curiosity-addiction.blogspot.fr/2013/12/client-server-bluetooth-en-java-le-code.html
 * Created by anthony on 02/05/2017.
 */
@Slf4j
@Service
public class BluetoothService {
    private String serviceName = "bthService";

    // https://www.uuidgenerator.net/
    private String myServiceUUID = "768d63e14e7e4206b420a87c3a25d87b";

    private String connURL = null;

    private BluetoothJsonFile jsonFile;

    @Getter
    private final Queue<String> bluetoothQueue = new ArrayBlockingQueue<String>(10);

    @Getter
    private StreamConnectionNotifier scn = null;

    @Autowired
    public BluetoothService(final BluetoothJsonFile jf) throws IOException {
        jsonFile = jf;

        // Service identifier
        final UUID MYSERVICEUUID_UUID = new UUID(myServiceUUID, false);

        // Service connection URL
        connURL = "btspp://localhost:" + MYSERVICEUUID_UUID.toString() + ";name=" + serviceName;

        // Service discovery enabled
        LocalDevice.getLocalDevice().setDiscoverable(DiscoveryAgent.GIAC);

        // Publishing the service to the Service Record Database
        scn = (StreamConnectionNotifier) Connector.open(connURL);
    }

    @PostConstruct
    public void startService() {
        log.info("Starting bluetooth service at {}", connURL);

        // Start the bluetooth handler thread
        final Thread bluetoothThread = new Thread(new BluetoothHandler(this, jsonFile));
        bluetoothThread.start();
    }

    @PreDestroy
    public void closeService() throws IOException {
        // Close the service
        scn.close();
    }
}
