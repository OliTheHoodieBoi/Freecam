package FreecamUtils;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.nbtapi.NBTEntity;
import lunarfreecam.freecam.Commands;
import lunarfreecam.freecam.Main;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Objects;


public class NpcManager {
    /**
     * Spawn the Zombie-NPC
     */
    public void createNpc(Player player) {
        // Create a normal zombie
        Zombie zombie = player.getWorld().spawn(player.getLocation(), Zombie.class);
        zombie.setBaby(false);
        zombie.setVillager(false);
        zombie.setPassenger(null);
        // Modify zombie NBT
        NBTEntity zombieNBT = new NBTEntity(zombie);
        zombieNBT.setByte("Silent", (byte) 1);
        zombieNBT.setByte("NoAI", (byte) 1);
        zombieNBT.setByte("NoGravity", (byte) 0);
        zombieNBT.setByte("CustomNameVisible", (byte) 1);
        zombieNBT.setByte("PersistenceRequired", (byte) 1);
        // Make zombie resemble player
        zombie.setCustomName(player.getDisplayName());

        ItemStack playerhead = new ItemStack(Objects.requireNonNull(XMaterial.PLAYER_HEAD.parseMaterial()), 1, (byte) 3);
        SkullMeta meta = (SkullMeta) playerhead.getItemMeta();
        meta.setOwner(player.getName());
        meta.setDisplayName(player.getDisplayName());
        playerhead.setItemMeta(meta);
        zombie.getEquipment().setHelmet(playerhead);
        // Copy player equipment to zombie
        zombie.getEquipment().setItemInHand(player.getItemInHand());
        zombie.getEquipment().setChestplate(player.getEquipment().getChestplate() != null && !player.getEquipment().getChestplate().getType().equals(Material.AIR) ? player.getEquipment().getChestplate() : XMaterial.LEATHER_CHESTPLATE.parseItem());
        zombie.getEquipment().setLeggings(player.getEquipment().getLeggings() != null && !player.getEquipment().getLeggings().getType().equals(Material.AIR) ? player.getEquipment().getLeggings() : XMaterial.LEATHER_LEGGINGS.parseItem());
        zombie.getEquipment().setBoots(player.getEquipment().getBoots() != null && !player.getEquipment().getBoots().getType().equals(Material.AIR) ? player.getEquipment().getBoots() : XMaterial.LEATHER_BOOTS.parseItem());
        Main.npcs.put(player.getUniqueId(), zombie);
    }

    /**
     * Remove the npc for a player
     *
     * @param player
     */
    public void deleteNpc(Player player) {
        Main.npcs.get(player.getUniqueId()).remove();
    }

    public void exitFreecam(Player player, GameMode mode) {
        player.setGameMode(mode);
        player.teleport(Main.npcs.get(player.getUniqueId()).getLocation());
        this.deleteNpc(player);
        Main.npcs.remove(player.getUniqueId());
        Commands.previousGamemode.remove(player);
        Main.playersInFreecam.remove(player);
    }
}
