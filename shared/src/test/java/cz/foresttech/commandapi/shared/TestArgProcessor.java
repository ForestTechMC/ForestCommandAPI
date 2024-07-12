package cz.foresttech.commandapi.shared;

import cz.foresttech.commandapi.shared.processor.ArgumentTypeProcessor;

import java.util.ArrayList;
import java.util.List;

public class TestArgProcessor implements ArgumentTypeProcessor<TestObject> {

    @Override
    public <S extends AbstractCommandSenderWrapper<?>> TestObject get(S commandSender, String argument) {
        return new TestObject(argument);
    }

    @Override
    public <S extends AbstractCommandSenderWrapper<?>> List<String> tabComplete(S commandSender, String argument) {
        List<String> fakeList = new ArrayList<>();
        fakeList.add("apik007");
        fakeList.add("zetor");
        fakeList.add("Apik777");
        fakeList.add("8545a");

        // Only those starting with the argument
        fakeList.removeIf(s -> !s.startsWith(argument));
        return fakeList;
    }
}
