package cz.foresttech.commandapi.paper;

import cz.foresttech.commandapi.shared.AbstractCommandSenderWrapper;
import org.bukkit.command.CommandSender;

public class CommandSenderWrapper extends AbstractCommandSenderWrapper<CommandSender> {

    public CommandSenderWrapper(CommandSender sender) {
        super(sender);
    }

    @Override
    public void sendMessageColored(String message) {
        sender.sendRichMessage(message);
    }
}
