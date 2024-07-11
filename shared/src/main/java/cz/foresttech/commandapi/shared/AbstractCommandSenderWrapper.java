package cz.foresttech.commandapi.shared;

public abstract class AbstractCommandSenderWrapper<T> {

    protected final T sender;

    public AbstractCommandSenderWrapper(T sender) {
        this.sender = sender;
    }

    public T getSender() {
        return sender;
    }

    public abstract void sendMessage(String message);
    public abstract void sendMessageColored(String message);

}
