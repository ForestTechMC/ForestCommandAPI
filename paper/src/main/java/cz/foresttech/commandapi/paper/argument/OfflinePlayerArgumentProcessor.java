package cz.foresttech.commandapi.paper.argument;

import cz.foresttech.commandapi.shared.ArgumentTypeProcessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OfflinePlayerArgumentProcessor implements ArgumentTypeProcessor<OfflinePlayer> {

    @Override
    public OfflinePlayer get(String argument) {
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

}
