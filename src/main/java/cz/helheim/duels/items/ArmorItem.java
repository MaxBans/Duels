package cz.helheim.duels.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;

public class ArmorItem {

    private final Material material;
    private final String customName;
    private final Map<Enchantment, Integer> enchantmentToLevelMap = new HashMap<>();
    private ArmorType armorType;

    public ArmorItem(ConfigurationSection section) {
        Material material;

        try {
            material = Material.valueOf(section.getString("material"));
        } catch (Exception e) {
            material = Material.BARRIER;
        }

        this.material = material;
        this.customName = section.getString("name");
        this.armorType = ArmorType.valueOf(section.getString("type"));

        ConfigurationSection enchantmentsSection = section.getConfigurationSection("enchantments");
        if (enchantmentsSection != null) {
            for (String enchantmentKey : enchantmentsSection.getKeys(false)) {
                Enchantment enchantment = Enchantment.getByName(enchantmentKey.toUpperCase());

                if (enchantment != null) {
                    int level = enchantmentsSection.getInt(enchantmentKey);
                    enchantmentToLevelMap.put(enchantment, level);
                }
            }
        }
    }

    public void suitPlayer(Player player) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (customName != null) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', customName));
        }

        if (!enchantmentToLevelMap.isEmpty()) {
            for (Map.Entry<Enchantment, Integer> enchantEntry : enchantmentToLevelMap.entrySet()) {
                itemMeta.addEnchant(
                        enchantEntry.getKey(),
                        enchantEntry.getValue(),
                        true
                );
            }
        }
        itemStack.setItemMeta(itemMeta);
        switch (armorType){
            case BOOTS:
                player.getInventory().setBoots(itemStack);
                break;
            case LEGGINGS:
                player.getInventory().setLeggings(itemStack);
                break;
            case CHESTPLATE:
                player.getInventory().setChestplate(itemStack);
                break;
            case HELMET:
                player.getInventory().setHelmet(itemStack);
                break;
        }
    }
}
