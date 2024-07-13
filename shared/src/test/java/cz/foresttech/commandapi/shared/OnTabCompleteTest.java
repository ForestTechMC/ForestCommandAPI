package cz.foresttech.commandapi.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OnTabCompleteTest {

    private TestCommandAPI testCommandAPI;
    private TestCommandSenderWrapper testCommandSenderWrapper;

    @BeforeEach
    public void setUp() {
        testCommandAPI = new TestCommandAPI();
        TestTabCommand testCommand = Mockito.mock(TestTabCommand.class);
        testCommandSenderWrapper = new TestCommandSenderWrapper("apik007");

        TestArgProcessor testArgProcessor = new TestArgProcessor();

        assertTrue(testCommandAPI.registerArgumentTypeProcessor(TestObject.class, testArgProcessor));
        assertTrue(testCommandAPI.registerCommand(testCommand));
    }

    @Test
    void tabComplete() {
        List<String> autoComplete = testCommandAPI.tabComplete(testCommandSenderWrapper, "cmd", new String[]{});
        assertTrue(autoComplete.contains("test"));
        assertTrue(autoComplete.contains("arg"));
        assertTrue(autoComplete.contains("non-required"));
        assertTrue(autoComplete.contains("required"));
        assertTrue(autoComplete.contains("test_alias"));
        assertTrue(autoComplete.contains("test_alias2"));
        assertEquals(6, autoComplete.size());

        autoComplete = testCommandAPI.tabComplete(testCommandSenderWrapper, "cmd", new String[]{"req"});
        assertTrue(autoComplete.contains("required"));
        assertEquals(1, autoComplete.size());

        autoComplete = testCommandAPI.tabComplete(testCommandSenderWrapper, "cmd", new String[]{"required", "t"});
        assertTrue(autoComplete.contains("test"));
        assertEquals(1, autoComplete.size());

        autoComplete = testCommandAPI.tabComplete(testCommandSenderWrapper, "cmd", new String[]{"required", "test", "a"});
        assertTrue(autoComplete.contains("apik007"));
        assertEquals(1, autoComplete.size());

        autoComplete = testCommandAPI.tabComplete(testCommandSenderWrapper, "cmd", new String[]{"required", "test", "A"});
        assertTrue(autoComplete.contains("Apik777"));
        assertEquals(1, autoComplete.size());

        autoComplete = testCommandAPI.tabComplete(testCommandSenderWrapper, "cmd", new String[]{"tes"});
        assertTrue(autoComplete.contains("test"));
        assertTrue(autoComplete.contains("test_alias"));
        assertTrue(autoComplete.contains("test_alias2"));
        assertEquals(3, autoComplete.size());
    }
}