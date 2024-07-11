package cz.foresttech.commandapi.shared;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public abstract class AbstractCommandAPI<T extends AbstractCommandSenderWrapper<?>> {

    private final Map<String, CommandProcessor> commandMap;
    private final Map<Class<?>, ArgumentTypeProcessor<?>> argumentTypeProcessorMap;

    public AbstractCommandAPI() {
        this.commandMap = new HashMap<>();
        this.argumentTypeProcessorMap = new HashMap<>();

        registerDefaultArgumentTypeProcessors();

        setup();
    }

    protected abstract boolean registerToPlatform(String cmdName);

    protected abstract void setup();

    private void registerDefaultArgumentTypeProcessors() {
        registerArgumentTypeProcessor(String.class, ArgumentTypeProcessor.STRING);
        registerArgumentTypeProcessor(Integer.class, ArgumentTypeProcessor.INTEGER);
        registerArgumentTypeProcessor(Double.class, ArgumentTypeProcessor.DOUBLE);
        registerArgumentTypeProcessor(Float.class, ArgumentTypeProcessor.FLOAT);
        registerArgumentTypeProcessor(Long.class, ArgumentTypeProcessor.LONG);
        registerArgumentTypeProcessor(Short.class, ArgumentTypeProcessor.SHORT);
        registerArgumentTypeProcessor(Byte.class, ArgumentTypeProcessor.BYTE);
        registerArgumentTypeProcessor(Character.class, ArgumentTypeProcessor.CHARACTER);
        registerArgumentTypeProcessor(Boolean.class, ArgumentTypeProcessor.BOOLEAN);
    }

    public boolean registerCommand(CommandProcessor command) {
        if (command == null) {
            return false;
        }

        if (!command.getClass().isAnnotationPresent(Command.class)) {
            return false;
        }

        Command commandAnnotation = command.getClass().getAnnotation(Command.class);
        commandMap.put(commandAnnotation.name().toLowerCase(), command);

        return registerToPlatform(commandAnnotation.name().toLowerCase());
    }

    public boolean registerArgumentTypeProcessor(Class<?> clazz, ArgumentTypeProcessor<?> argumentTypeProcessor) {
        if (argumentTypeProcessor == null) {
            return false;
        }

        argumentTypeProcessorMap.put(clazz, argumentTypeProcessor);
        return true;
    }

    public void unregisterCommand(String name) {
        commandMap.remove(name.toLowerCase());
    }

    public void unregisterArgumentTypeProcessor(Class<?> clazz) {
        argumentTypeProcessorMap.remove(clazz);
    }

    public List<String> tabComplete(T commandSender, String cmd, String[] args) {
        List<String> list = new ArrayList<>();
        CommandProcessor command = commandMap.get(cmd.toLowerCase());

        if (command == null) {
            return list;
        }

        String argsTogether = String.join(" ", args);

        for (Method method : command.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(SubCommand.class)) {
                continue;
            }

            SubCommand subCommand = method.getAnnotation(SubCommand.class);

            for (String name : subCommand.names()) {

                if (argsTogether.toLowerCase().startsWith(name.toLowerCase()) || argsTogether.equalsIgnoreCase(name)) {

                    String subString = argsTogether.substring(name.length()).trim();
                    if (subString.isBlank()) {
                        subString = "";
                    }

                    String[] argsToCheck = subString.split(" ");
                    long params = Arrays.stream(method.getParameters())
                            .filter(parameter -> parameter.isAnnotationPresent(Arg.class))
                            .count();

                    if (params < argsToCheck.length) {
                        continue;
                    }

                    Parameter parameter = method.getParameters()[argsToCheck.length];

                    if (parameter.isAnnotationPresent(Arg.class)) {
                        ArgumentTypeProcessor<?> processor = argumentTypeProcessorMap.get(parameter.getType());
                        if (processor != null) {
                            List<String> result = processor.tabComplete(argsToCheck[argsToCheck.length-1]);
                            if (result != null) {
                                list.addAll(result);
                            } else {
                                Arg arg = parameter.getAnnotation(Arg.class);
                                list.add(arg.name());
                            }
                        }
                    }

                } else if (name.startsWith(argsTogether)) {
                    list.add(name);
                }
            }
        }

        return list;
    }

    public boolean onCommand(T commandSender, String cmd, String[] args) {
        CommandProcessor command = commandMap.get(cmd.toLowerCase());
        if (command == null) {
            return true;
        }

        Method methodToInvoke = null;

        // Handle no arguments case
        if (args.length == 0) {
            methodToInvoke = findUniversalSubCommand(command);
            if (methodToInvoke != null) {
                invokeMethod(methodToInvoke, command, commandSender);
                return true;
            }
            // No command found
            return true;
        }

        String argsTogether = String.join(" ", args);
        String[] invokeArgs = null;

        // Check specifically declared subcommands
        methodToInvoke = findMatchingSubCommand(command, args);
        if (methodToInvoke != null) {
            invokeArgs = extractArguments(methodToInvoke, args, argsTogether);
        } else {
            // Handle universal subcommands with arguments
            methodToInvoke = findUniversalSubCommandWithArgs(command, args);
            if (methodToInvoke != null) {
                invokeArgs = args;
            }
        }

        if (methodToInvoke != null) {
            Object[] parameters = prepareParameters(methodToInvoke, commandSender, invokeArgs);
            if (parameters == null) {
                commandSender.sendMessageColored("Failed to parse arguments!");
                return true;
            }

            invokeMethod(methodToInvoke, command, commandSender, parameters);
            return true;
        }


        return true;
    }

    private Method findUniversalSubCommand(CommandProcessor command) {
        for (Method method : command.getClass().getDeclaredMethods()) {
            if (method.getParameters().length != 1 || !method.isAnnotationPresent(SubCommand.class)) {
                continue;
            }

            SubCommand subCommand = method.getAnnotation(SubCommand.class);
            if (subCommand.names().length == 0) {
                return method;
            }
        }
        return null;
    }

    private Method findUniversalSubCommandWithArgs(CommandProcessor command, String[] args) {
        for (Method method : command.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(SubCommand.class)) {
                continue;
            }

            SubCommand subCommand = method.getAnnotation(SubCommand.class);
            if (subCommand.names().length != 0 || method.getParameters().length < 2) {
                continue;
            }

            long requiredParameters = Arrays.stream(method.getParameters())
                    .filter(parameter -> {
                        if (parameter.isAnnotationPresent(Arg.class)) {
                            Arg arg = parameter.getAnnotation(Arg.class);
                            return arg.required();
                        }
                       return false;
                    }).count();

            if (requiredParameters > args.length) {
                continue;
            }

            return method;
        }
        return null;
    }

    private Method findMatchingSubCommand(CommandProcessor command, String[] args) {
        for (Method method : command.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(SubCommand.class)) {
                continue;
            }

            SubCommand subCommand = method.getAnnotation(SubCommand.class);
            if (subCommand.names().length == 0) {
                continue;
            }

            String availablePrefix = namesCheck(subCommand.names(), args);
            if (availablePrefix == null) {
                continue;
            }

            long requiredParameters = Arrays.stream(method.getParameters())
                    .filter(parameter -> {
                        if (parameter.isAnnotationPresent(Arg.class)) {
                            Arg arg = parameter.getAnnotation(Arg.class);
                            return arg.required();
                        }
                        return false;
                    }).count();

            String[] extracted = extractArguments(method, args, String.join(" ", args));

            if (requiredParameters > extracted.length) {
                continue;
            }

            return method;
        }
        return null;
    }

    private List<Method> findMatchingSubCommands(CommandProcessor commandProcessor, String[] args) {
        List<Method> methods = new ArrayList<>();
        for (Method method : commandProcessor.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(SubCommand.class)) {
                continue;
            }

            SubCommand subCommand = method.getAnnotation(SubCommand.class);
            if (subCommand.names().length == 0) {
                continue;
            }

            String availablePrefix = namesCheckTabComplete(subCommand.names(), args);
            if (availablePrefix == null) {
                continue;
            }

            long requiredParameters = Arrays.stream(method.getParameters())
                    .filter(parameter -> {
                        if (parameter.isAnnotationPresent(Arg.class)) {
                            Arg arg = parameter.getAnnotation(Arg.class);
                            return arg.required();
                        }
                        return false;
                    }).count();

            String[] extracted = extractArguments(method, args, String.join(" ", args));

            if (requiredParameters > extracted.length) {
                continue;
            }

            methods.add(method);
        }
        return methods;
    }

    private String[] extractArguments(Method method, String[] args, String argsTogether) {
        if (args.length == 0 || argsTogether.isBlank()) {
            return new String[0];
        }

        SubCommand subCommand = method.getAnnotation(SubCommand.class);
        String availablePrefix = namesCheck(subCommand.names(), args);
        String remainingPart = argsTogether.substring(availablePrefix.length()).trim();
        if (remainingPart.startsWith(" ")) {
            remainingPart = remainingPart.substring(1);
        }

        if (remainingPart.isBlank()) {
            return new String[0];
        }

        return remainingPart.split(" ");
    }

    private Object[] prepareParameters(Method method, T commandSender, String[] args) {
        int usableParameters = method.getParameters().length - 1; // first is sender
        int usedParameters = Math.min(args.length, usableParameters);

        Object[] parameters = new Object[usableParameters];

        for (int i = 0; i < usedParameters; i++) {
            String arg = args[i];
            Parameter parameter = method.getParameters()[i + 1];

            if (i == usableParameters - 1 && parameter.isAnnotationPresent(Arg.class)) {
                Arg argAnnotation = parameter.getAnnotation(Arg.class);
                if (argAnnotation.multiword()) {
                    String[] multiwordArgs = Arrays.copyOfRange(args, i, args.length);
                    arg = String.join(" ", multiwordArgs);
                }
            }

            Object value = parseArgument(parameter.getType(), arg);
            if (value == null) {
                return null;
            }

            parameters[i] = value;
        }
        return parameters;
    }

    private String namesCheck(String[] names, String[] args) {
        String mergedArgs = String.join(" ", args);
        for (String name : names) {
            if (mergedArgs.toLowerCase().startsWith(name.toLowerCase())) {
                return name;
            }
        }
        return null;
    }

    private String namesCheckTabComplete(String[] names, String[] args) {
        List<String> list = new ArrayList<>();
        String mergedArgs = String.join(" ", args);

        for (String name : names) {
            if (name.toLowerCase().startsWith(mergedArgs.toLowerCase())) {
                return mergedArgs;
            }
        }
        return null;
    }

    private void invokeMethod(Method method, CommandProcessor instance, T commandSender, Object... args) {
        try {
            if (args == null || args.length == 0) {
                method.invoke(instance, commandSender.getSender());
                return;
            }

            Object[] values = new Object[args.length + 1];
            values[0] = commandSender.getSender();
            System.arraycopy(args, 0, values, 1, args.length);
            method.invoke(instance, values);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }
    }

    private Object parseArgument(Class<?> clazz, String argument) {
        ArgumentTypeProcessor<?> argumentTypeProcessor = argumentTypeProcessorMap.get(clazz);
        if (argumentTypeProcessor == null) {
            return null;
        }
        return argumentTypeProcessor.get(argument);
    }

}
