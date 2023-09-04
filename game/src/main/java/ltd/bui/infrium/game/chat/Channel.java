package ltd.bui.infrium.game.chat;

public abstract class Channel {

    ChannelType channelType;

    protected abstract void processMessage(String message);


    public Channel(ChannelType channelType) {
        this.channelType = channelType;
    }




}
