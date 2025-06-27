package com.infrium.game.chat;

import lombok.Getter;

public enum ChannelType {

    LOCAL("local", 0, "", 50),
    PARTY("party", 10, "[P]", 100),
    GLOBAL("global", 100, "[G]", 200),
    ECHO("echo", 1000, "[ECHO]", 3000)
    ;

    @Getter private String name;
    @Getter private int ladder;
    @Getter private String prefix;
    @Getter private long cooldown;

    ChannelType(String name, int ladder, String prefix, long cooldown) {
        this.name = name;
        this.ladder = ladder;
        this.prefix = prefix;
        this.cooldown = cooldown;
    }
}
