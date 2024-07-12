package cz.foresttech.commandapi.shared;

import java.util.ArrayList;
import java.util.List;

public class TestArgProcessor implements ArgumentTypeProcessor<TestObject> {

    @Override
    public TestObject get(String argument) {
        return new TestObject(argument);
    }

    @Override
    public List<String> tabComplete(String argument) {
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
