package cz.foresttech.commandapi.shared;

public class TestCommandSenderWrapper extends AbstractCommandSenderWrapper<String> {

    public TestCommandSenderWrapper(String sender) {
        super(sender);
    }

    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void sendMessageColored(String message) {
        System.out.println(message);
    }
}
