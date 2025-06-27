package com.infrium.game.components.testing.commands;


import com.infrium.api.data.Rank;
import com.infrium.core.api.command.Command;
import com.infrium.core.api.command.CommandArgs;
import com.infrium.game.BaseCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TestCommand extends BaseCommand {

//    private Set<Settlement> settlements = new HashSet<>();

    @Command(name = "test", description = "TestCommand command for quick development")
    public void onTest(CommandArgs args) {
        Player player = args.getPlayer();
        Location loc = new Location(player.getWorld(), -254.5, 78.0,246.5);

//        NPC npc = Settlements.getInstance().getLibrary().createNPC(Arrays.asList(ChatColor.GRAY.toString() + ChatColor.ITALIC + "NPC", "Guide"));
//        npc.setLocation(loc);
//        npc.setSkin(new Skin("eyJ0aW1lc3RhbXAiOjE1NzQ4MjA1NzI0MzUsInByb2ZpbGVJZCI6IjdkYTJhYjNhOTNjYTQ4ZWU4MzA0OGFmYzNiODBlNjhlIiwicHJvZmlsZU5hbWUiOiJHb2xkYXBmZWwiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzRmN2U3ZGUyNjk4MTk3N2Q1NTVkZmY3MDM0MGU1NWYyNDJhZDQ3NjI5MzFjOGExZmYwNjBhNTUwYzRhNzUzNWQifX19", "bSoWDs+yVLPxeUUz/71ibhXuTVosgwABbfS/azzBlahgd8x58GhK9CH+eaO5ut6YrWeBN3q9oZVKJW9hK5omSh1XlsDlzWfBpOMILSqKQ5p19sdoWds+Gnti7KrVdkxBlqLj3+Fbqn5LF58cpOIBUcDkK9wKQldpEqCC5vPDnvwmw70+vpusvjrxgQQXJSh8dAySs070WiVkAw5VX/siUxCVudx7uqLnyzx25UW6hK4PFZPI53XtevXmK+0puIuFB98hKTQIVMe+8onXR5c1RY4xv5iOe4a6DgSmAfFTjvEzcHaua8JOEC0V4Hr7MEwFKRmjz1BPkDkwdZ6ScbC3lR4+f0tu6dCrI6XnJq+hLH2kgpij/YT4AenCwb9h96UOCYNsvAIhkpnsdVwO11cdrjuZIYIPAuMFQYNFDzDA3jjj162/hldMutTUOawvDC7szMMmmHDc9QqKVPKvdjewuVTUGwhfI4eP/nf45zUfdN1t3A9IWVVTtBTn1bfz1Hcvvud3mPnMkN2Tg7dxkL++0mgMOIxEIT0HPK0wLeQ7BiKhaACk0ucL7mKa4VqNTB4JGY3b8E8+jHpxpFf5pfMLra4xIZctxqvoBN/KthV85yY3LGZ49gi1GMUQMhA/RDMLYPf2O51Uf2Hwb6c1k1OeOF2tL1GAZyIfnvOAsxqbYDM="));
//        player.sendMessage(npc.getId());
//        npc.create();
//        Bukkit.getOnlinePlayers().forEach(p -> npc.show(p));
    }

//    @Command(name = "t2", description = "t2 <uuidtostringname> <name> <player>")
//    public void onT2(CommandArgs args) {
//
//        SettlementPlayer sp = Settlements.getInstance().getPm().getOnlinePlayer(UUIDFetcher.getUUID(args.getArgs(2)));
//
//        Settlement st = new Settlement(UUID.fromString(args.getArgs(0)), args.getArgs(1),sp);
//
//        if (!settlements.contains(st))
//            settlements.add(st);
//
//        if (!st.getSettlementPlayers().contains(sp) || st.getSettlementLeader() != sp)
//            st.addPlayer(sp);
//
//        args.getPlayer().sendMessage(st.getSettlementName() + "");
//        args.getPlayer().sendMessage(st.getId() + "");
//        args.getPlayer().sendMessage(st.getSettlementLeader().getName() + "");
//        st.getSettlementPlayers().forEach(m -> args.getPlayer().sendMessage(m.getSettlementPlayer().getName()));
//
//    }


/*    @Command(name = "rank.add.shadow", description = "Add a shadow rank to a player")
    public void rankshadowAdd(CommandArgs args) {
        Player player = Bukkit.getPlayer(args.getArgs(0));
        GenericPlayer gp = Settlements.getInstance().getGpm().getOnlinePlayer(player.getUniqueId());
        gp.setRank(Rank.get(args.getArgs(1)));
        Settlements.getInstance().getGpm().updatePlayerData(player.getUniqueId(), gp);
        player.sendMessage("Changing Rank of " + player.getDisplayName() + " to " + Settlements.getInstance().getGpm().getOnlinePlayer(player.getUniqueId()).getRank().getEnumName());
    }

    @Command(name = "rank.add", description = "Add a rank to a player")
    public void rankAdd(CommandArgs args) {
        Player player = Bukkit.getPlayer(args.getArgs(0));
        NetworkPlayer gp = Settlements.getInstance().getGpm().getOnlinePlayer(player.getUniqueId());
        gp.setShownRank(Rank.get(args.getArgs(1)));
        Settlements.getInstance().getGpm().updatePlayerData(player.getUniqueId(), gp);
        player.sendMessage("Changing Rank of " + player.getDisplayName() + " to " + Settlements.getInstance().getGpm().getOnlinePlayer(player.getUniqueId()).getRank().getEnumName());
    }*/

    @Command(name = "ranks", description = "TestCommand command for quick development")
    public void listRanks(CommandArgs args) {
        Arrays.stream(Rank.values()).iterator().forEachRemaining(rank -> args.getPlayer().sendMessage(rank.getName() + " " + rank.getPrefix()));
    }



}
