package cz.foresttech.commandapi.shared.processor;

import cz.foresttech.commandapi.shared.AbstractCommandSenderWrapper;

import java.util.List;

public interface ArgumentTypeProcessor<T> {

    <S extends AbstractCommandSenderWrapper<?>> T get(S commandSender, String argument);

    default <S extends AbstractCommandSenderWrapper<?>> List<String> tabComplete(S commandSender, String argument) {
        return null;
    }

    ArgumentTypeProcessor<String> STRING = new ArgumentTypeProcessor<>() {
        @Override
        public <S extends AbstractCommandSenderWrapper<?>> String get(S commandSender, String argument) {
            return argument;
        }
    };

    ArgumentTypeProcessor<Integer> INTEGER = new ArgumentTypeProcessor<>() {
        @Override
        public <S extends AbstractCommandSenderWrapper<?>> Integer get(S commandSender, String argument) {
            try {
                return Integer.parseInt(argument);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    };

    ArgumentTypeProcessor<Double> DOUBLE = new ArgumentTypeProcessor<>() {
        @Override
        public <S extends AbstractCommandSenderWrapper<?>> Double get(S commandSender, String argument) {
            try {
                return Double.parseDouble(argument);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    };

    ArgumentTypeProcessor<Boolean> BOOLEAN = new ArgumentTypeProcessor<>() {
        @Override
        public <S extends AbstractCommandSenderWrapper<?>> Boolean get(S commandSender, String argument) {
            return Boolean.parseBoolean(argument);
        }
    };

    ArgumentTypeProcessor<Long> LONG = new ArgumentTypeProcessor<>() {
        @Override
        public <S extends AbstractCommandSenderWrapper<?>> Long get(S commandSender, String argument) {
            try {
                return Long.parseLong(argument);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    };

    ArgumentTypeProcessor<Float> FLOAT = new ArgumentTypeProcessor<>() {
        @Override
        public <S extends AbstractCommandSenderWrapper<?>> Float get(S commandSender, String argument) {
            try {
                return Float.parseFloat(argument);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    };

    ArgumentTypeProcessor<Short> SHORT = new ArgumentTypeProcessor<>() {
        @Override
        public <S extends AbstractCommandSenderWrapper<?>> Short get(S commandSender, String argument) {
            try {
                return Short.parseShort(argument);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    };

    ArgumentTypeProcessor<Byte> BYTE = new ArgumentTypeProcessor<>() {
        @Override
        public <S extends AbstractCommandSenderWrapper<?>> Byte get(S commandSender, String argument) {
            try {
                return Byte.parseByte(argument);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    };

    ArgumentTypeProcessor<Character> CHARACTER = new ArgumentTypeProcessor<>() {
        @Override
        public <S extends AbstractCommandSenderWrapper<?>> Character get(S commandSender, String argument) {
            return argument.charAt(0);
        }
    };

}
