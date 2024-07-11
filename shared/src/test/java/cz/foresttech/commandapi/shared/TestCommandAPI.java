package cz.foresttech.commandapi.shared;

public class TestCommandAPI extends AbstractCommandAPI<TestCommandSenderWrapper> {

    @Override
    protected boolean registerToPlatform(String cmdName) {
        return true;
    }

    @Override
    protected void setup() {

    }
}
