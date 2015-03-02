package de.softwertiger.filewatch;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ThreadFactory;

import static org.testng.Assert.assertEquals;

public class StreamUtilTest {
    private static final int BUFFER_SIZE = 16;
    private static final ThreadFactory NO_THREAD_FACTORY = runnable -> new Thread() {
        @Override
        public void start() {
            runnable.run();
        }
    };

    private final StreamUtil streamUtil = new StreamUtil(NO_THREAD_FACTORY, BUFFER_SIZE);

    @Test
    public void forwardStream_copiesStreamToTarget() throws Exception {
        // setup
        final byte[] data = new byte[]{1, 2, 3};
        final ByteArrayInputStream in = new ByteArrayInputStream(data);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        // execution
        streamUtil.forwardStream(in, new PrintStream(out));

        // evaluation
        assertEquals(out.toByteArray(), data);
    }

    @Test
    public void forwardStream_forwardsStreamLargerThanBuffer() throws Exception {
        // setup
        final byte[] data = new byte[BUFFER_SIZE * 2];
        for(byte i = 0; i < BUFFER_SIZE * 2; ++i) {
            data[i] = i;
        }
        final ByteArrayInputStream in = new ByteArrayInputStream(data);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        // execution
        streamUtil.forwardStream(in, new PrintStream(out));

        // evaluation
        assertEquals(out.toByteArray(), data);
    }
}