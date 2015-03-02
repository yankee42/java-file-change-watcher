package de.softwertiger.filewatch;

import java.util.concurrent.atomic.AtomicInteger;

public class DelayCallMerger implements Runnable {
    private final Runnable delegate;
    private volatile AtomicInteger runCounter = new AtomicInteger(0);

    public DelayCallMerger(final Runnable delegate) {
        this.delegate = delegate;
    }

    @Override
    public void run() {
        int id = runCounter.incrementAndGet();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                synchronized (DelayCallMerger.this) {
                    if (id == runCounter.get()) {
                        delegate.run();
                    }
                }
            }
        }.start();
    }
}
