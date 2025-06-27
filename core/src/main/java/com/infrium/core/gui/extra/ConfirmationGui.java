package com.infrium.core.gui.extra;

import com.infrium.core.gui.AbstractGui;
import com.infrium.core.gui.GuiItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class ConfirmationGui extends AbstractGui {
    private final GuiItem yesButton;
    private final GuiItem noButton;
    private final String confirmationMessage;
    private final Runnable onConfirm;
    private final Runnable onCancel;

    public ConfirmationGui(Component title, JavaPlugin plugin, String confirmationMessage,
                           Runnable onConfirm, Runnable onCancel) {
        super(27, title, plugin);
        this.confirmationMessage = confirmationMessage;
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;

        this.yesButton = createYesButton();
        this.noButton = createNoButton();
    }

    private GuiItem createYesButton() {
        return new GuiItem(11,
                player -> new ItemStack(Material.LIME_WOOL, 1) {{
                    var meta = getItemMeta();
                    meta.displayName(Component.text("§a§lConfirm"));
                    setItemMeta(meta);
                }},
                (player, item, inventory) -> {
                    onConfirm.run();
                    player.closeInventory();
                });
    }

    private GuiItem createNoButton() {
        return new GuiItem(15,
                player -> new ItemStack(Material.RED_WOOL, 1) {{
                    var meta = getItemMeta();
                    meta.displayName(Component.text("§c§lCancel"));
                    setItemMeta(meta);
                }},
                (player, item, inventory) -> {
                    onCancel.run();
                    player.closeInventory();
                });
    }

    @Override
    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, size, title);

        // Add message item
        ItemStack messageItem = new ItemStack(Material.PAPER, 1);
        var meta = messageItem.getItemMeta();
        meta.displayName(Component.text("§f" + confirmationMessage));
        messageItem.setItemMeta(meta);
        inventory.setItem(13, messageItem);

        // Add buttons
        inventory.setItem(yesButton.getSlot(), yesButton.getItemBuilder().build(player));
        inventory.setItem(noButton.getSlot(), noButton.getItemBuilder().build(player));

        // Fill empty slots
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        var fillerMeta = filler.getItemMeta();
        fillerMeta.displayName(Component.text(" "));
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < size; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }

        player.openInventory(inventory);
    }

    @Override
    protected void onItemClick(Player player, ItemStack clickedItem, Inventory inventory) {
        if (clickedItem.equals(yesButton.getItemBuilder().build(player))) {
            yesButton.getOnItemClick().onItemClick(player, clickedItem, inventory);
        } else if (clickedItem.equals(noButton.getItemBuilder().build(player))) {
            noButton.getOnItemClick().onItemClick(player, clickedItem, inventory);
        }
    }
}
