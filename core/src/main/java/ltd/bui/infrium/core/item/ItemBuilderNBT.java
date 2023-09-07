package ltd.bui.infrium.core.item;

public class ItemBuilderNBT {
//    private int size;
//    private Component name;
//    private List<Component> lore;
//    private Material material;
//    private boolean unbreakable;
//
//    ItemBuilder() {
//        this.size = 1;
//        this.name = null;
//        this.lore = null;
//        this.material = Material.AIR;
//        this.unbreakable = false;
//    }
//
//    public static ItemBuilder builder() {
//        return new ItemBuilder();
//    }
//
//    public int getSize() {
//        return size;
//    }
//
//    public ItemBuilder setSize(int size) {
//        this.size = size;
//        return this;
//    }
//
//    public Component getName() {
//        return name;
//    }
//
//    public ItemBuilder setName(Component name) {
//        this.name = name;
//        return this;
//    }
//
//    public List<Component> getLore() {
//        return lore;
//    }
//
//    public ItemBuilder setLore(List<Component> lore) {
//        this.lore = lore;
//        return this;
//    }
//
//
//    public Material getMaterial() {
//        return material;
//    }
//
//    public ItemBuilder setMaterial(Material material) {
//        this.material = material;
//        return this;
//    }
//
//    public boolean isUnbreakable() {
//        return unbreakable;
//    }
//
//    public ItemBuilder setUnbreakable(boolean unbreakable) {
//        this.unbreakable = unbreakable;
//        return this;
//    }
//
//    public ItemStack build(boolean glow) {
//        ItemStack itemStack = new ItemStack(material, size);
//        var meta = itemStack.getItemMeta();
//
//        meta.displayName(this.name);
//        meta.lore(this.lore);
//        meta.setUnbreakable(unbreakable);
//        if (glow) {
//            meta.addEnchant(Enchantment.WATER_WORKER, 1, true);
//            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
//        }
//
//        itemStack.setItemMeta(meta);
//        return itemStack;
//    }
}
