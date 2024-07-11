package cz.foresttech.commandapi.paper;

import cz.foresttech.commandapi.paper.argument.OfflinePlayerArgumentProcessor;
import cz.foresttech.commandapi.paper.argument.PlayerArgumentProcessor;
import cz.foresttech.commandapi.shared.AbstractCommandAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandAPI extends AbstractCommandAPI<CommandSenderWrapper> implements CommandExecutor, TabCompleter {

    private final JavaPlugin javaPlugin;

    public CommandAPI(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        CommandSenderWrapper senderWrapper = new CommandSenderWrapper(commandSender);
        return onCommand(senderWrapper, command.getName(), args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        CommandSenderWrapper senderWrapper = new CommandSenderWrapper(commandSender);
        return tabComplete(senderWrapper, command.getName(), args);
    }

    @Override
    protected void setup() {
        registerArgumentTypeProcessor(Player.class, new PlayerArgumentProcessor());
        registerArgumentTypeProcessor(OfflinePlayer.class, new OfflinePlayerArgumentProcessor());
    }

    @Override
    protected boolean registerToPlatform(String cmdName) {
        PluginCommand pluginCommand = javaPlugin.getCommand(cmdName);
        if (pluginCommand == null) {
            return false;
        }

        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(this);
        return true;
    }
}
