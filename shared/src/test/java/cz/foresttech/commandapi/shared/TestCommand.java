package cz.foresttech.commandapi.shared;

@Command(name = "cmd")
public class TestCommand implements CommandProcessor{

    @SubCommand
    public void defaultSubCmd(String sender) {
        System.out.println("[" + sender + "] Default sub command with no args!");
    }

    @SubCommand
    public void defaultSubCmd(String sender, @Arg(name = "test") Double arg1, @Arg(name = "test2", multiword = true) String msg) {
        System.out.println("[" + sender + "] Default sub command with double arg1: " + arg1 + " and msg: " + msg);
    }

    @SubCommand(names = "test")
    public void testSubCmd(String sender) {
        System.out.println("[" + sender + "] Test sub command with no args!");
    }

    @SubCommand(names = {"test_alias", "test_alias2"})
    public void testSubCmdAlias(String sender) {
        System.out.println("[" + sender + "] Test sub command with no args and aliases!");
    }

    @SubCommand(names = "arg")
    public void argSubCmd(String sender, @Arg(name = "test") String arg1) {
        System.out.println("[" + sender + "] Arg sub command with arg1: " + arg1);
    }

    @SubCommand(names = "non-required")
    public void argSubCmd(String sender, @Arg(name = "test") String arg1, @Arg(name = "test2", required = false) String arg2) {
        System.out.println("[" + sender + "] Arg sub command with arg1: " + arg1 + " and non-required arg2: " + arg2);
    }

    @SubCommand(names = "required")
    public void argSubCmdReq(String sender, @Arg(name = "test") String arg1, @Arg(name = "test2") String arg2) {
        System.out.println("[" + sender + "] Arg sub command with arg1: " + arg1 + " and required arg2: " + arg2);
    }

}
