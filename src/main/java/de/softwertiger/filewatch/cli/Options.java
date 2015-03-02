package de.softwertiger.filewatch.cli;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Options {
    private final boolean verbose;
    private final Path fileToWatch;
    private final String command;

    private Options(final boolean verbose, final Path fileToWatch, final String command) {
        if (fileToWatch == null || command == null) {
            throw new IllegalCommandLineOptionsException();
        }
        this.verbose = verbose;
        this.fileToWatch = fileToWatch;
        this.command = command;
    }

    public static Options parseCommandLineOptions(final String[] args) {
        boolean verbose = false;
        Path fileToWatch = null;
        String command = null;
        int unnamedPos = 0;
        for (final String arg : args) {
            if (arg.equals("-v")) {
                verbose = true;
            } else {
                if (unnamedPos == 0) {
                    fileToWatch = Paths.get(System.getProperty("user.dir")).resolve(arg).normalize();
                } else if (unnamedPos == 1) {
                    command = arg;
                } else {
                    throw new IllegalCommandLineOptionsException(); // too many args
                }
                ++unnamedPos;
            }
        }

        return new Options(verbose, fileToWatch, command);
    }

    public String getCommand() {
        return command;
    }

    public Path getFileToWatch() {
        return fileToWatch;
    }

    public boolean isVerbose() {
        return verbose;
    }
}
