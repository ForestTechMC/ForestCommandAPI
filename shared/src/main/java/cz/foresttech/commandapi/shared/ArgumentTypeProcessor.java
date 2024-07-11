package cz.foresttech.commandapi.shared;

public interface ArgumentTypeProcessor<T> {

    T get(String argument);


    ArgumentTypeProcessor<String> STRING = argument -> argument;
    ArgumentTypeProcessor<Integer> INTEGER = argument -> {
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            return null;
        }
    };
    ArgumentTypeProcessor<Long> LONG = argument -> {
        try {
            return Long.parseLong(argument);
        } catch (NumberFormatException e) {
            return null;
        }
    };
    ArgumentTypeProcessor<Double> DOUBLE = argument -> {
        try {
            return Double.parseDouble(argument);
        } catch (NumberFormatException e) {
            return null;
        }
    };
    ArgumentTypeProcessor<Float> FLOAT = argument -> {
        try {
            return Float.parseFloat(argument);
        } catch (NumberFormatException e) {
            return null;
        }
    };
    ArgumentTypeProcessor<Byte> BYTE = argument -> {
        try {
            return Byte.parseByte(argument);
        } catch (NumberFormatException e) {
            return null;
        }
    };
    ArgumentTypeProcessor<Short> SHORT = argument -> {
        try {
            return Short.parseShort(argument);
        } catch (NumberFormatException e) {
            return null;
        }
    };
    ArgumentTypeProcessor<Character> CHARACTER = argument -> {
        if (argument.length() != 1) {
            return null;
        }
        return argument.charAt(0);
    };
    ArgumentTypeProcessor<Boolean> BOOLEAN = argument -> {
        try {
            return Boolean.parseBoolean(argument);
        } catch (NumberFormatException e) {
            return null;
        }
    };

}
