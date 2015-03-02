package de.softwertiger.filewatch.cli;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.*;

public class OptionsTest {
    private static final Path CWD = Paths.get(System.getProperty("user.dir"));
    private static final Path WATCH_FILE = Paths.get("watchFile");
    private static final Path WATCH_FILE_ABSOLUTE = CWD.resolve(WATCH_FILE);
    private static final String COMMAND = "command";

    @Test(expectedExceptions = IllegalCommandLineOptionsException.class, dataProvider = "provide_invalidArguments")
    public void parseCommandLineOptions_throwsException_ifArgumentsAreInvalid(@SuppressWarnings("UnusedParameters")
                                                                                  final String description,
                                                                              final String args[]) throws Exception {

        // execution
        Options.parseCommandLineOptions(args);

        // evaluation performed by expected exception
    }

    @DataProvider
    public Object[][] provide_invalidArguments() {
        return new Object[][]{
                {"too few: zero args", new String[0]},
                {"too few: one arg", new String[]{"arg1"}},
                {"too few: two args, but one is -v", new String[]{"-v", "arg1"}},
                {"too many args", new String[]{"arg1", "arg2", "one arg too much"}},
        };
    }

    @Test
    public void parseCommandLineOptions_parsesMinimalAsExpected() throws Exception {
        // execution
        final Options actual = Options.parseCommandLineOptions(new String[]{WATCH_FILE.toString(), COMMAND});

        // evaluation
        assertEquals(actual.getCommand(), COMMAND);
        assertEquals(actual.getFileToWatch(), WATCH_FILE_ABSOLUTE);
        assertEquals(actual.isVerbose(), false);
    }

    @Test
    public void parseCommandLineOptions_parsesAllArgsAsExpected() throws Exception {
        // execution
        final Options actual = Options.parseCommandLineOptions(new String[]{"-v", WATCH_FILE.toString(), COMMAND});

        // evaluation
        assertEquals(actual.getCommand(), COMMAND);
        assertEquals(actual.getFileToWatch(), WATCH_FILE_ABSOLUTE);
        assertEquals(actual.isVerbose(), true);
    }
}