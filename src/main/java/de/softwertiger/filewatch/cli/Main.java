package de.softwertiger.filewatch.cli;

import de.softwertiger.filewatch.DelayCallMerger;
import de.softwertiger.filewatch.OnFileChangeRunner;
import de.softwertiger.filewatch.StreamUtil;

import java.io.IOException;

public class Main {
    private static final StreamUtil streamUtil = new StreamUtil();
    private final Options options;

    public Main(final Options options) throws IOException {
        this.options = options;
        if (options.isVerbose()) {
            System.out.println("Watching <" + options.getFileToWatch() + "> for changes");
        }
        OnFileChangeRunner
            .registerForFile(options.getFileToWatch())
            .runOnFileChange(new DelayCallMerger(this::tryRunCommand));
    }

    public static void main(String[] args) throws Exception {
        try {
            new Main(Options.parseCommandLineOptions(args));
        } catch (IllegalCommandLineOptionsException e) {
            System.out.println("Usage: fileWatcher [-v] <fileToWatch> <commandToExecute>");
        }
    }

    private void tryRunCommand() {
        if (options.isVerbose()) {
            System.out.println("File has changed. Executing...");
        }
        try {
            final Process process = Runtime.getRuntime().exec(options.getCommand());
            streamUtil.forwardStream(process.getInputStream(), System.out);
            streamUtil.forwardStream(process.getErrorStream(), System.err);
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}
