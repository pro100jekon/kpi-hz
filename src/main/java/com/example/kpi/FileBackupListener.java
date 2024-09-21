package com.example.kpi;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.MapEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapClearedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;

public class FileBackupListener implements EntryAddedListener<String, Long>, EntryRemovedListener<String, Long>, EntryUpdatedListener<String, Long>, MapClearedListener {

    private static final File BACKUP_FILE = new File("backup.txt");
    Logger log = LoggerFactory.getLogger(FileBackupListener.class);

    @Override
    public void entryAdded(EntryEvent<String, Long> event) {
        if (!BACKUP_FILE.exists()) {
            try {
                if (BACKUP_FILE.createNewFile()) {
                    log.info("Backup file created");
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        try (FileWriter writer = new FileWriter(BACKUP_FILE, true)){
            writer.write(event.getKey() + ": " + event.getValue() + "\n");
            writer.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void entryRemoved(EntryEvent<String, Long> event) {
        if (BACKUP_FILE.exists()) {
            try (var lines = Files.lines(BACKUP_FILE.toPath());
                 FileWriter writer = new FileWriter(BACKUP_FILE)) {
                lines.filter(line -> !line.startsWith(event.getKey()))
                        .forEach(line -> {
                            try {
                                writer.write(line);
                            } catch (IOException e) {
                                log.error(e.getMessage(), e);
                            }
                        });
                writer.flush();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.warn("Backup file does not exist");
        }
    }

    @Override
    public void entryUpdated(EntryEvent<String, Long> event) {
        if (BACKUP_FILE.exists()) {
            try (FileWriter writer = new FileWriter(BACKUP_FILE, true)){
                writer.write(event.getKey() + ": " + event.getValue() + " (" + System.currentTimeMillis() + ")" + "\n");
                writer.flush();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.warn("Backup file does not exist");
        }
    }

    @Override
    public void mapCleared(MapEvent event) {
        if (BACKUP_FILE.delete()) {
            log.info("Backup file deleted: {}", BACKUP_FILE.getAbsolutePath());
        }
    }
}
