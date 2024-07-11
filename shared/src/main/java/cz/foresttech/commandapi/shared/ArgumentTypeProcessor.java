package cz.foresttech.commandapi.shared;

import java.util.List;

public interface ArgumentTypeProcessor<T> {

    T get(String argument);

    List<String> tabComplete(String argument);

    ArgumentTypeProcessor<String> STRING = new ArgumentTypeProcessor<String>() {
        @Override
        public String get(String argument) {
            return argument;
        }

        @Override
        public List<String> tabComplete(String argument) {
            return null;
        }
    };

    ArgumentTypeProcessor<Integer> INTEGER = new ArgumentTypeProcessor<Integer>() {
        @Override
        public Integer get(String argument) {
            try {
                return Integer.parseInt(argument);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public List<String> tabComplete(String argument) {
            return null;
        }
    };

    ArgumentTypeProcessor<Double> DOUBLE = new ArgumentTypeProcessor<Double>() {
        @Override
        public Double get(String argument) {
            try {
                return Double.parseDouble(argument);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public List<String> tabComplete(String argument) {
            return null;
        }
    };

    ArgumentTypeProcessor<Boolean> BOOLEAN = new ArgumentTypeProcessor<Boolean>() {
        @Override
        public Boolean get(String argument) {
            return Boolean.parseBoolean(argument);
        }

        @Override
        public List<String> tabComplete(String argument) {
            return null;
        }
    };

    ArgumentTypeProcessor<Long> LONG = new ArgumentTypeProcessor<Long>() {
        @Override
        public Long get(String argument) {
            try {
                return Long.parseLong(argument);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public List<String> tabComplete(String argument) {
            return null;
        }
    };

    ArgumentTypeProcessor<Float> FLOAT = new ArgumentTypeProcessor<Float>() {
        @Override
        public Float get(String argument) {
            try {
                return Float.parseFloat(argument);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public List<String> tabComplete(String argument) {
            return null;
        }
    };

    ArgumentTypeProcessor<Short> SHORT = new ArgumentTypeProcessor<Short>() {
        @Override
        public Short get(String argument) {
            try {
                return Short.parseShort(argument);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public List<String> tabComplete(String argument) {
            return null;
        }
    };

    ArgumentTypeProcessor<Byte> BYTE = new ArgumentTypeProcessor<Byte>() {
        @Override
        public Byte get(String argument) {
            try {
                return Byte.parseByte(argument);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public List<String> tabComplete(String argument) {
            return null;
        }
    };

    ArgumentTypeProcessor<Character> CHARACTER = new ArgumentTypeProcessor<Character>() {
        @Override
        public Character get(String argument) {
            return argument.charAt(0);
        }

        @Override
        public List<String> tabComplete(String argument) {
            return null;
        }
    };

}
