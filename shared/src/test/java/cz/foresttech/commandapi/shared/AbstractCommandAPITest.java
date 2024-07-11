package cz.foresttech.commandapi.shared;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractCommandAPITest {


    @Test
    void onCommand() {
        TestCommandAPI testCommandAPI = new TestCommandAPI();
        assert (testCommandAPI.registerCommand(new TestCommand()));

        TestCommandSenderWrapper testCommandSenderWrapper = new TestCommandSenderWrapper("apik007");

        assertTrue (testCommandAPI.onCommand(testCommandSenderWrapper, "non_exist_cmd", new String[]{}));
        assertTrue (testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{}));
        assertTrue (testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"rng"}));
        assertTrue (testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"10.5", "rng"}));
        assertTrue (testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"10.5", "rng", "message"}));
        assertTrue (testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"test"}));
        assertTrue (testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"test_alias"}));
        assertTrue (testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"test_alias2"}));
        assertTrue (testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"arg"}));
        assertTrue (testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"arg", "rng"}));
        assertTrue (testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"non-required", "rng"}));
        assertTrue (testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"non-required", "rng", "unneeded"}));
        assertTrue (testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"required", "rng"}));
        assertTrue (testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"required", "rng", "needed"}));

    }

    @Test
    void tabComplete() {
        TestCommandAPI testCommandAPI = new TestCommandAPI();
        assert (testCommandAPI.registerCommand(new TestCommand()));

        TestCommandSenderWrapper testCommandSenderWrapper = new TestCommandSenderWrapper("apik007");

        List<String> autoComplete = testCommandAPI.tabComplete(testCommandSenderWrapper, "cmd", new String[]{});

        assertTrue(autoComplete.contains("test"));
        assertTrue(autoComplete.contains("arg"));
        assertTrue(autoComplete.contains("non-required"));
        assertTrue(autoComplete.contains("required"));
        assertTrue(autoComplete.contains("test_alias"));
        assertTrue(autoComplete.contains("test_alias2"));

        autoComplete = testCommandAPI.tabComplete(testCommandSenderWrapper, "cmd", new String[]{"req"});

        assertTrue(autoComplete.contains("required"));
        assertTrue(autoComplete.size() == 1);

        autoComplete = testCommandAPI.tabComplete(testCommandSenderWrapper, "cmd", new String[]{"required", "test", "t"});
        assertTrue(autoComplete.contains("test2"));
        assertTrue(autoComplete.size() == 1);

    }
}