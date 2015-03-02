package de.softwertiger.filewatch;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OnFileChangeRunnerTest {
    private Path dir;
    private Path fileToWatch;

    @BeforeMethod
    public void setUp() throws Exception {
        dir = Files.createTempDirectory("onFileChangeTest");
        fileToWatch = dir.resolve("fileToWatch");
    }

    @AfterMethod
    public void tearDown() throws Exception {
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Test(timeOut = 10000)
    public void runOnFileChange_firesOnFileCreation() throws Exception {
        // setup
        final CountDownLatch runnableExecuted = watch();

        // execution
        Files.write(fileToWatch, new byte[0]);

        // evaluation
        runnableExecuted.await(); // OK if terminates
    }

    @Test(timeOut = 10000)
    public void runOnFileChange_firesOnFileChange() throws Exception {
        // setup
        Files.write(fileToWatch, new byte[0]);
        final CountDownLatch runnableExecuted = watch();

        // execution
        Files.write(fileToWatch, new byte[]{1});

        // evaluation
        runnableExecuted.await(); // OK if terminates
    }

    @Test(timeOut = 10000)
    public void runOnFileChange_firesAgainOnSecondFileChange() throws Exception {
        // setup
        Files.write(fileToWatch, new byte[0]);
        final CountDownLatch runnableExecuted = watch();
        // First change:
        Files.write(fileToWatch, new byte[]{1});
        runnableExecuted.await(); // OK if terminates

        // execution (second change)
        Files.write(fileToWatch, new byte[]{2});

        // evaluation
        runnableExecuted.await(); // OK if terminates
    }

    private CountDownLatch watch() throws IOException {
        final CountDownLatch runnableExecuted = new CountDownLatch(1);
        final OnFileChangeRunner onChangeRunner = OnFileChangeRunner.registerForFile(fileToWatch);
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> onChangeRunner.runOnFileChange(() -> {
            runnableExecuted.countDown();
            executorService.shutdownNow();
        }));
        return runnableExecuted;
    }
}