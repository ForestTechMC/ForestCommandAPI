package cz.foresttech.commandapi.shared;

import cz.foresttech.commandapi.shared.annotation.Arg;
import cz.foresttech.commandapi.shared.annotation.Command;
import cz.foresttech.commandapi.shared.annotation.SubCommand;
import cz.foresttech.commandapi.shared.processor.ArgumentTypeProcessor;
import cz.foresttech.commandapi.shared.processor.CommandProcessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Abstract command API handler ready to be platform-independent. Handles incoming
 * commands and tab completion requests + keeps registered commands and argument
 * processors.
 *
 * @param <T> Subclass of {@link AbstractCommandSenderWrapper} wrapping command sender object.
 */
public abstract class AbstractCommandAPI<T extends AbstractCommandSenderWrapper<?>> {

    // Stored commands (by name in lowercase)
    private final Map<String, CommandProcessor> commandMap;
    // Available argument type processors (by class)
    private final Map<Class<?>, ArgumentTypeProcessor<?>> argumentTypeProcessorMap;

    public AbstractCommandAPI() {
        this.commandMap = new HashMap<>();
        this.argumentTypeProcessorMap = new HashMap<>();

        registerDefaultArgumentTypeProcessors();

        setup();
    }

    protected abstract boolean registerToPlatform(String cmdName);

    /**
     * Method serving as platform-specific initiator, used especially for registering
     * platform-specific argument type processors.
     */
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

    /**
     * Registers a {@link CommandProcessor} instance to the system. Allows to override
     * already-registered command only if the {@link #registerToPlatform} method allows to.
     *
     * @param command {@link CommandProcessor} instance
     * @return true if the registration was successful
     */
    public boolean registerCommand(CommandProcessor command) {
        if (command == null) {
            return false;
        }

        if (!command.getClass().isAnnotationPresent(Command.class)) {
            return false;
        }

        Command commandAnnotation = command.getClass().getAnnotation(Command.class);
        String commandName = commandAnnotation.name();
        if (commandName == null || commandName.isBlank()) {
            return false;
        }

        if (commandMap.containsKey(commandName.toLowerCase())) {
            return false;
        }

        commandMap.put(commandName.toLowerCase(), command);
        return registerToPlatform(commandName.toLowerCase());
    }

    /**
     * Registers an {@link ArgumentTypeProcessor} for specific class.
     *
     * @param clazz                 Class of the argument type
     * @param argumentTypeProcessor {@link ArgumentTypeProcessor} instance
     * @return true if the registration was successful
     */
    public <A> boolean registerArgumentTypeProcessor(Class<A> clazz, ArgumentTypeProcessor<A> argumentTypeProcessor) {
        if (argumentTypeProcessor == null) {
            return false;
        }

        argumentTypeProcessorMap.put(clazz, argumentTypeProcessor);
        return true;
    }

    /**
     * Handles tab completion request for the given command.
     *
     * @param commandSender {@link T} instance
     * @param cmd           Command name
     * @param args          Command arguments
     * @return List of suggestions
     */
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
            extractSuggestions(commandSender, method, subCommand, argsTogether, list);
        }

        return list;
    }

    private void extractSuggestions(T commandSender, Method method, SubCommand subCommand, String argsTogether, List<String> list) {
        for (String name : subCommand.names()) {

            if (!argsTogether.toLowerCase().startsWith(name.toLowerCase()) && !argsTogether.equalsIgnoreCase(name)) {
                if (name.startsWith(argsTogether)) {
                    list.add(name);
                }
                continue;
            }

            String subString = argsTogether.substring(name.length()).trim();
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
                if (processor == null) {
                    continue;
                }

                List<String> result = processor.tabComplete(commandSender, argsToCheck[argsToCheck.length - 1]);
                if (result != null) {
                    list.addAll(result);
                } else {
                    Arg arg = parameter.getAnnotation(Arg.class);
                    list.add(arg.name());
                }
            }

        }
    }

    /**
     * Handles incoming command.
     *
     * @param commandSender {@link T} instance
     * @param cmd           Command name
     * @param args          Command arguments
     * @return true if the command was handled
     */
    public boolean onCommand(T commandSender, String cmd, String[] args) {
        CommandProcessor command = commandMap.get(cmd.toLowerCase());
        if (command == null) {
            return false;
        }

        Method methodToInvoke;

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

        String argsTogether = String.join(" ", args) + " ";
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

    private String[] extractArguments(Method method, String[] args, String argsTogether) {
        if (args.length == 0 || argsTogether.isBlank()) {
            return new String[0];
        }

        SubCommand subCommand = method.getAnnotation(SubCommand.class);
        String availablePrefix = namesCheck(subCommand.names(), args);
        if (availablePrefix == null) {
            return new String[0];
        }

        String remainingPart = argsTogether.substring(availablePrefix.length()).trim();
        if (remainingPart.startsWith(" ")) {
            remainingPart = remainingPart.substring(1);
        }

        if (remainingPart.isBlank()) {
            return new String[0];
        }

        // Remove ending whitespaces
        if (remainingPart.endsWith(" ")) {
            remainingPart = remainingPart.substring(0, remainingPart.length() - 1);
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

            Object value = parseArgument(commandSender, parameter.getType(), arg);
            if (value == null) {
                return null;
            }

            parameters[i] = value;
        }
        return parameters;
    }

    private String namesCheck(String[] names, String[] args) {
        String mergedArgs = String.join(" ", args) + " ";
        for (String name : names) {
            if (mergedArgs.toLowerCase().startsWith(name.toLowerCase() + " ")) {
                return name;
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

    private Object parseArgument(T commandSender, Class<?> clazz, String argument) {
        ArgumentTypeProcessor<?> argumentTypeProcessor = argumentTypeProcessorMap.get(clazz);
        if (argumentTypeProcessor == null) {
            return null;
        }
        return argumentTypeProcessor.get(commandSender, argument);
    }

}
