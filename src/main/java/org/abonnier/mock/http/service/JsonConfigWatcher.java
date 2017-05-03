package org.abonnier.mock.http.service;

import lombok.extern.slf4j.Slf4j;
import org.abonnier.mock.http.business.RouteHandler;
import org.abonnier.mock.http.config.JsonConfig;
import org.abonnier.mock.http.domain.json.HttpJsonFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * Watcher Service of the JSON Config file.
 * http://www.codejava.net/java-se/file-io/file-change-notification-example-with-watch-service-api
 * Created by Anthony on 02/05/2017.
 */
@Slf4j
@Service
public class JsonConfigWatcher {

    /**
     * Rate of run method execution.
     */
    private static final long SCHEDULE_RATE = 1000L;

    /**
     * Json Config file to watch.
     */
    private final JsonConfig jsonConfig;

    /**
     * Route handler to update when json file is updated.
     */
    private final RouteHandler routeHandler;

    /**
     * Watcher Service component.
     */
    private final WatchService watcher;

    /**
     * Store last update to avoid multi threaded updates.
     */
    private long lastUpdate = 0L;

    /**
     * Constructor.
     *
     * @param jsCfg to watch
     * @throws IOException        in case of IO Exception from File API.
     * @throws URISyntaxException in case of IO Exception from Watcher Service API.
     */
    @Autowired
    public JsonConfigWatcher(final JsonConfig jsCfg, RouteHandler rHdlr) throws IOException, URISyntaxException {
        jsonConfig = jsCfg;
        routeHandler = rHdlr;
        watcher = FileSystems.getDefault().newWatchService();

        final Path watchedDir = jsCfg.getHttpMockJsonFilePath().getParent();
        watchedDir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);

        log.info("Registered directory: {}", watchedDir);
        lastUpdate = System.currentTimeMillis();
    }

    /**
     * Execution of the Watcher service. Check if the JSON Config file
     * has been created or updated.
     */
    @Scheduled(fixedRate = SCHEDULE_RATE)
    public void run() {
        WatchKey key;
        try {
            // wait for a key to be available
            key = watcher.take();
        } catch (InterruptedException ex) {
            return;
        }

        for (WatchEvent<?> event : key.pollEvents()) {
            // get event type
            final WatchEvent.Kind<?> kind = event.kind();

            // get file name
            @SuppressWarnings("unchecked") final WatchEvent<Path> ev = (WatchEvent<Path>) event;
            final Path fileName = ev.context();

            // Check now
            final long now = System.currentTimeMillis();

            // Handle events (last update > schedule rate && file name is json config && event is create or modify)
            if (now - lastUpdate >= SCHEDULE_RATE &&
                    jsonConfig.getHttpMockConfigFileName().equals(fileName.toString()) &&
                    (kind == ENTRY_CREATE || kind == ENTRY_MODIFY)) {
                log.info("JsonConfigWatcher handling {} {} {}", kind.name(), "on", fileName);

                try {
                    // Update the json file
                    final HttpJsonFile jf = jsonConfig.httpMockConfig();
                    // Update the routes
                    routeHandler.updateRoutes(jf);
                    // Store last update
                    lastUpdate = now;

                } catch (URISyntaxException | IOException e) {
                    log.error("Error while updating json file: ", e);
                }
                break; // Break because we don't need to handle other files
            }
        }

        // IMPORTANT: The key must be reset after processed
        key.reset();
    }
}
