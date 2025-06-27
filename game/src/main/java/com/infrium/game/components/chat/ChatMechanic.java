package com.infrium.game.components.chat;


import com.infrium.core.api.components.Component;
import com.infrium.game.Settlements;
import com.infrium.game.components.chat.commands.GlobalChatCommand;
import com.infrium.game.components.chat.listeners.GlobalChatListener;

public class ChatMechanic extends Component<Settlements> {
    @Override
    public void enable(Settlements plugin) {
    }

    @Override
    public void disable(Settlements plugin) {
    }

    @Override
    public void registerListener(Settlements plugin) {
        new GlobalChatListener(plugin);
    }

    @Override
    public void registerCommands(Settlements plugin) {
        new GlobalChatCommand();
    }
}
