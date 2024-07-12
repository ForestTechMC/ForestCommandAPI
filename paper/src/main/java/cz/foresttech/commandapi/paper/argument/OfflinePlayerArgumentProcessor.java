package cz.foresttech.commandapi.paper.argument;

import cz.foresttech.commandapi.shared.AbstractCommandSenderWrapper;
import cz.foresttech.commandapi.shared.processor.ArgumentTypeProcessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class OfflinePlayerArgumentProcessor implements ArgumentTypeProcessor<OfflinePlayer> {

    @Override
    public <S extends AbstractCommandSenderWrapper<?>> OfflinePlayer get(S commandSender, String argument) {
        Player player = Bukkit.getPlayer(argument);
        if (player == null) {
            try {
                UUID uuid = UUID.fromString(argument);
                player = Bukkit.getPlayer(uuid);
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (player != null) {
            return player;
        }

        try {
            UUID uuid = UUID.fromString(argument);
            return Bukkit.getOfflinePlayer(uuid);
        } catch (IllegalArgumentException ignored) {
        }

        return Bukkit.getOfflinePlayer(argument);
    }

    @Override
    public <S extends AbstractCommandSenderWrapper<?>> List<String> tabComplete(S commandSender, String argument) {
        String inLowerCase = argument.toLowerCase();
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(inLowerCase))
                .toList();
    }

}
