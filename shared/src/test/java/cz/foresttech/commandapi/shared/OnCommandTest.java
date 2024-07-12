package cz.foresttech.commandapi.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OnCommandTest {

    private TestCommandAPI testCommandAPI;
    private TestCommand testCommand;
    private TestCommandSenderWrapper testCommandSenderWrapper;

    @BeforeEach
    public void setUp() {
        testCommandAPI = new TestCommandAPI();
        testCommand = Mockito.mock(TestCommand.class);
        testCommandSenderWrapper = new TestCommandSenderWrapper("apik007");
        assertTrue(testCommandAPI.registerCommand(testCommand));
    }

    @Test
    void onNonExistingCommand() {
        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "non_exist_cmd", new String[]{}));
        verify(testCommand, never()).defaultSubCmd(any());
        verify(testCommand, never()).defaultSubCmd(any(), any(), any());
        verify(testCommand, never()).testSubCmd(any());
        verify(testCommand, never()).testSubCmdAlias(any());
        verify(testCommand, never()).argSubCmd(any(), any());
        verify(testCommand, never()).argSubCmd(any(), any(), any());
        verify(testCommand, never()).argSubCmdReq(any(), any(), any());
    }

    @Test
    void onDefaultSubCommand() {
        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{}));
        verify(testCommand).defaultSubCmd("apik007");
        reset(testCommand);

        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"rng"}));
        verify(testCommand, never()).defaultSubCmd(any());
        verify(testCommand, never()).defaultSubCmd(any(), any(), any());
        reset(testCommand);

        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"10.5", "rng"}));
        verify(testCommand, never()).defaultSubCmd(any());
        verify(testCommand).defaultSubCmd("apik007", 10.5, "rng");
        reset(testCommand);

        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"10.5", "rng", "message"}));
        verify(testCommand, never()).defaultSubCmd(any());
        verify(testCommand).defaultSubCmd("apik007", 10.5, "rng message");
    }

    @Test
    void parsing() {
        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"10.5s", "rng", "message"}));
        verify(testCommand, never()).defaultSubCmd(any());
        verify(testCommand, never()).defaultSubCmd(any(), any(), any());
        verify(testCommand, never()).testSubCmd(any());
        verify(testCommand, never()).testSubCmdAlias(any());
        verify(testCommand, never()).argSubCmd(any(), any());
        verify(testCommand, never()).argSubCmd(any(), any(), any());
        verify(testCommand, never()).argSubCmdReq(any(), any(), any());

        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"-100.5", "rng", "message"}));
        verify(testCommand, never()).defaultSubCmd(any());
        verify(testCommand, never()).testSubCmd(any());
        verify(testCommand, never()).testSubCmdAlias(any());
        verify(testCommand, never()).argSubCmd(any(), any());
        verify(testCommand, never()).argSubCmd(any(), any(), any());
        verify(testCommand, never()).argSubCmdReq(any(), any(), any());
        verify(testCommand).defaultSubCmd("apik007", -100.5, "rng message");

        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"-100", "rng", "message"}));
        verify(testCommand, never()).defaultSubCmd(any());
        verify(testCommand, never()).testSubCmd(any());
        verify(testCommand, never()).testSubCmdAlias(any());
        verify(testCommand, never()).argSubCmd(any(), any());
        verify(testCommand, never()).argSubCmd(any(), any(), any());
        verify(testCommand, never()).argSubCmdReq(any(), any(), any());
        verify(testCommand).defaultSubCmd("apik007", -100.0, "rng message");
    }

    @Test
    void onSubCommandNoArgs() {
        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"test"}));
        verify(testCommand, never()).defaultSubCmd(any());
        verify(testCommand, never()).defaultSubCmd(any(), any(), any());
        verify(testCommand).testSubCmd("apik007");
        reset(testCommand);

        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"test_alias"}));
        verify(testCommand, never()).defaultSubCmd(any());
        verify(testCommand, never()).defaultSubCmd(any(), any(), any());
        verify(testCommand, never()).testSubCmd(any());
        verify(testCommand).testSubCmdAlias("apik007");
        reset(testCommand);

        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"test_alias2"}));
        verify(testCommand, never()).defaultSubCmd(any());
        verify(testCommand, never()).defaultSubCmd(any(), any(), any());
        verify(testCommand, never()).testSubCmd(any());
        verify(testCommand).testSubCmdAlias("apik007");
    }

    @Test
    void onSubCommandArgs() {
        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"arg"}));
        verify(testCommand, never()).defaultSubCmd(any());
        verify(testCommand, never()).defaultSubCmd(any(), any(), any());
        verify(testCommand, never()).testSubCmd(any());
        verify(testCommand, never()).testSubCmdAlias(any());
        verify(testCommand, never()).argSubCmd(any(), any());
        verify(testCommand, never()).argSubCmd(any(), any(), any());
        verify(testCommand, never()).argSubCmdReq(any(), any(), any());
        reset(testCommand);

        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"arg", "rng"}));
        verify(testCommand, never()).defaultSubCmd(any());
        verify(testCommand, never()).defaultSubCmd(any(), any(), any());
        verify(testCommand, never()).testSubCmd(any());
        verify(testCommand, never()).testSubCmdAlias(any());
        verify(testCommand, never()).argSubCmd(any(), any(), any());
        verify(testCommand, never()).argSubCmdReq(any(), any(), any());
        verify(testCommand).argSubCmd("apik007", "rng");
        reset(testCommand);

        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"non-required", "rng"}));
        verify(testCommand, never()).defaultSubCmd(any());
        verify(testCommand, never()).defaultSubCmd(any(), any(), any());
        verify(testCommand, never()).testSubCmd(any());
        verify(testCommand, never()).testSubCmdAlias(any());
        verify(testCommand, never()).argSubCmd(any(), any());
        verify(testCommand, never()).argSubCmdReq(any(), any(), any());
        verify(testCommand).argSubCmd("apik007", "rng", null);
        reset(testCommand);

        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"non-required", "rng", "unneeded"}));
        verify(testCommand, never()).defaultSubCmd(any());
        verify(testCommand, never()).defaultSubCmd(any(), any(), any());
        verify(testCommand, never()).testSubCmd(any());
        verify(testCommand, never()).testSubCmdAlias(any());
        verify(testCommand, never()).argSubCmd(any(), any());
        verify(testCommand, never()).argSubCmdReq(any(), any(), any());
        verify(testCommand).argSubCmd("apik007", "rng", "unneeded");
        reset(testCommand);

        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"required", "rng"}));
        verify(testCommand, never()).defaultSubCmd(any());
        verify(testCommand, never()).defaultSubCmd(any(), any(), any());
        verify(testCommand, never()).testSubCmd(any());
        verify(testCommand, never()).testSubCmdAlias(any());
        verify(testCommand, never()).argSubCmd(any(), any());
        verify(testCommand, never()).argSubCmd(any(), any(), any());
        verify(testCommand, never()).argSubCmdReq(any(), any(), any());
        reset(testCommand);

        assertTrue(testCommandAPI.onCommand(testCommandSenderWrapper, "cmd", new String[]{"required", "rng", "needed"}));
        verify(testCommand, never()).defaultSubCmd(any());
        verify(testCommand, never()).defaultSubCmd(any(), any(), any());
        verify(testCommand, never()).testSubCmd(any());
        verify(testCommand, never()).testSubCmdAlias(any());
        verify(testCommand, never()).argSubCmd(any(), any());
        verify(testCommand, never()).argSubCmd(any(), any(), any());
        verify(testCommand).argSubCmdReq("apik007", "rng", "needed");
        reset(testCommand);
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

        autoComplete = testCommandAPI.tabComplete(testCommandSenderWrapper, "cmd", new String[]{"required", "test", "t"});
        assertTrue(autoComplete.contains("test2"));
        assertEquals(1, autoComplete.size());
    }
}