package cz.foresttech.commandapi.paper;

import cz.foresttech.commandapi.paper.argument.OfflinePlayerArgumentProcessor;
import cz.foresttech.commandapi.paper.argument.PlayerArgumentProcessor;
import cz.foresttech.commandapi.shared.AbstractCommandAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CommandAPI extends AbstractCommandAPI<CommandSenderWrapper> implements CommandExecutor {

    private final JavaPlugin javaPlugin;

    public CommandAPI(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandSenderWrapper senderWrapper = new CommandSenderWrapper(commandSender);
        return onCommand(senderWrapper, command.getName(), strings);
    }

    @Override
    protected void setup() {
        registerArgumentTypeProcessor(Player.class, new PlayerArgumentProcessor());
        registerArgumentTypeProcessor(OfflinePlayer.class, new OfflinePlayerArgumentProcessor());
    }

    @Override
    protected void register() {
        javaPlugin.getServer().getCommandMap().getKnownCommands().values().forEach(command -> {
            javaPlugin.getCommand(command.getName()).setExecutor(this);
        });
    }
}
