package cz.foresttech.commandapi.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

import java.util.List;

public class VelocityCommandHandler implements SimpleCommand {

    private final String commandName;
    private final CommandAPI commandAPI;

    public VelocityCommandHandler(String commandName, CommandAPI commandAPI) {
        this.commandName = commandName;
        this.commandAPI = commandAPI;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource commandSender = invocation.source();
        String[] args = invocation.arguments();
        commandAPI.onCommand(new CommandSenderWrapper(commandSender), commandName, args);
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        CommandSource commandSender = invocation.source();
        String[] args = invocation.arguments();
        return commandAPI.tabComplete(new CommandSenderWrapper(commandSender), commandName, args);
    }
}
