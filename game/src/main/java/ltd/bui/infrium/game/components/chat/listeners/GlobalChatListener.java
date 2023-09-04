package ltd.bui.infrium.game.components.chat.listeners;

import ltd.bui.infrium.api.player.AbstractInfriumPlayer;
import ltd.bui.infrium.core.InfriumCore;
import ltd.bui.infrium.game.Settlements;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class GlobalChatListener implements Listener {

    public GlobalChatListener(Settlements plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onGlobalAbbreviation(AsyncPlayerChatEvent event) {
        AbstractInfriumPlayer<Player> gp = InfriumCore.getInstance().getInfriumProvider().getInfriumPlayer(event.getPlayer()).get();
        switch (event.getMessage().charAt(0)) {
            case '!': {
                event.setCancelled(true);
                if (event.getMessage().length() <= 1) {
                    event.getPlayer().sendMessage(ChatColor.RED + "Please specify a message");
                    return;
                }

                if (false){
//                    CooldownTimer cooldownTimer = new CooldownTimer(config.globalcooldown);
                    int cdTime = 3; //cooldownTimer.getTime();
                    String cdUnit = "s";//cooldownTimer.getUnit();
                    event.getPlayer().sendMessage(ChatColor.GRAY + "Global cooldown, Please wait " + ChatColor.RED + cdTime + ChatColor.YELLOW + cdUnit);
                }

                //TODO: change format and get Custom player with all attributes.
                event.setFormat("§a<§lG§a> " + gp.getPlayerData().getRank() + gp.getPlayerData().getRank().getPrefix() + " §7"+ (gp.getPlayerData().getRank().getLadder() <= 8 ? "§f%s" : "§7%s") + (gp.getPlayerData().getRank().isDefault() ? "§7%s: " : "§f%s: "));
                event.setMessage(event.getMessage().substring(1));
                event.setCancelled(false);
                return;
            }
            case '@': {
                event.setCancelled(true);
                Player sender = event.getPlayer();
                String targetName = event.getMessage().substring(1).split(" ")[0];
                Player target = null;

                try {
                    //TODO send message accross network as packet
                    target = Bukkit.getPlayer(targetName);
                    AbstractInfriumPlayer<Player> gpt = InfriumCore.getInstance().getInfriumProvider().getInfriumPlayer(event.getPlayer()).get();
                    String message = event.getMessage().substring(target.getDisplayName().length() + 2);
                    target.sendMessage( "@" + gpt.getPlayerData().getRank() + gpt.getPlayerData().getRank().getPrefix() + " §7"+ (gpt.getPlayerData().getRank().getLadder() <= 8 ? "§f%s" : "§7%s") + (gpt.getPlayerData().getRank().isDefault() ? "§7%s: " : "§f%s: ") + message);
                } catch (NullPointerException ex) {
                    //TODO will need to implement this into global network structure
                    sender.sendMessage(ChatColor.RED.toString() + ChatColor.UNDERLINE.toString() + "@" + targetName + " is not online!");
                    sender.sendMessage(" ");
                    return;
                }
                return;

            }
            default: {
                event.setFormat(" " + gp.getPlayerData().getRank() + gp.getPlayerData().getRank().getPrefix() + " §7"+ (gp.getPlayerData().getRank().getLadder() <= 8 ? "§f%s" : "§7%s") + (gp.getPlayerData().getRank().isDefault() ? "§7%s: " : "§f%s: "));
                event.setCancelled(false);
            }
        }

    }
}
