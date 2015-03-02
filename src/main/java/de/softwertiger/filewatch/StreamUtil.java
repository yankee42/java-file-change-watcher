package de.softwertiger.filewatch;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class StreamUtil {
    private final ThreadFactory threadFactory;
    private final int bufferSize;

    public StreamUtil() {
        this(new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);
            @Override
            public Thread newThread(final Runnable r) {
                return new Thread(r, "stream-util-" + threadNumber);
            }
        }, 8192);
    }

    StreamUtil(final ThreadFactory threadFactory, final int bufferSize) {
        this.threadFactory = threadFactory;
        this.bufferSize = bufferSize;
    }

    public Thread forwardStream(final InputStream in, final PrintStream out) {
        Thread thread = threadFactory.newThread(() -> {
            byte[] buffer = new byte[bufferSize];
            int read;
            try {
                while((read = in.read(buffer)) > -1) {
                    out.write(buffer, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        return thread;
    }
}
