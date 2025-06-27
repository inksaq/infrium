package com.infrium.game.util;

import com.infrium.api.player.AbstractInfriumPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class Bulletin {
    private final String message;

    public Bulletin(String message, String format) {
        this("", message, format);
    }

    public Bulletin(Chat prefix, String message, String format) {
        this(prefix.toString(), message, format);
    }

    public Bulletin(String prefix, String message, String format) {
        this(prefix + TextHelper.punctuationPattern.matcher(message).replaceAll(format));
    }

    public Bulletin(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String parse(Object... args) {
        return MessageFormat.format(message, args);
    }

    public void send(AbstractInfriumPlayer<Player> player) {
        player.getPlayerObject().sendMessage(message);
    }

    public void send(AbstractInfriumPlayer<Player> player, Object... args) {
        player.getPlayerObject().sendMessage(parse(args));
    }

    public void send(CommandSender target) {
        target.sendMessage(message);
    }

    public void send(CommandSender target, Object... args) {
        target.sendMessage(parse(args));
    }

    public void kick(Player player) {
        player.kickPlayer(message);
    }

    public void kick(Player player, Object... args) {
        player.kickPlayer(parse(args));
    }

    @Override
    public String toString() {
        return message;
    }
}
