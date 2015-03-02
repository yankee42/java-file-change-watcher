package de.softwertiger.filewatch;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class OnFileChangeRunner {
    private final Path watchDir;
    private final Path watchFile;
    private final WatchService watchService;

    private OnFileChangeRunner(final Path watchFile) throws IOException {
        this.watchFile = watchFile;
        watchDir = watchFile.getParent();
        watchService = FileSystems.getDefault().newWatchService();
        watchDir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY);
    }

    public static OnFileChangeRunner registerForFile(final Path watchFile) throws IOException {
        return new OnFileChangeRunner(watchFile);
    }

    public void runOnFileChange(final Runnable runnable) {
        try {
            tryRunOnFileChange(runnable);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    private void tryRunOnFileChange(final Runnable runnable) throws IOException {
        try {
            final WatchKey take = watchService.take();
            while (!Thread.interrupted()) {
                for (final WatchEvent<?> watchEvent : take.pollEvents()) {
                    final WatchEvent<Path> ev = cast(watchEvent);
                    if (watchFile.equals(watchDir.resolve(ev.context()))) {
                        runnable.run();
                    }
                }
            }
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }
}
