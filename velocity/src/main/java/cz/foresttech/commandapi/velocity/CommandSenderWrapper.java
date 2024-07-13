package cz.foresttech.commandapi.velocity;

import com.velocitypowered.api.command.CommandSource;
import cz.foresttech.commandapi.shared.AbstractCommandSenderWrapper;

public class CommandSenderWrapper extends AbstractCommandSenderWrapper<CommandSource> {

    public CommandSenderWrapper(CommandSource sender) {
        super(sender);
    }

    @Override
    public void sendMessageColored(String message) {
        sender.sendRichMessage(message);
    }
}
