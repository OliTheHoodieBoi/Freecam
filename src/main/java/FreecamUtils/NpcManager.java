package FreecamUtils;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import lunarfreecam.freecam.Main;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;


public class NpcManager {
    /**
     * Spawn the Zombie-NPC
     */
    public void createNpc(Player player) {
        // Create a zombie
        Zombie zombie = player.getWorld().spawn(player.getLocation(), Zombie.class);
        // Remove vehicle if there is one
        Entity vehicle = zombie.getVehicle();
        if (vehicle != null)
            vehicle.remove();
        // Modify zombie NBT
        NBTEntity zombieNBT = new NBTEntity(zombie);
        zombieNBT.setByte("Silent", (byte) 1);
        zombieNBT.setByte("NoAI", (byte) 1);
        zombieNBT.setByte("IsBaby", (byte) 0);
        zombieNBT.setByte("NoGravity", (byte) 0);
        zombieNBT.setByte("CustomNameVisible", (byte) 1);
        zombieNBT.setString("CustomName", String.format("\"%s\"", player.displayName()));
        zombieNBT.setByte("PersistenceRequired", (byte) 1);
        zombieNBT.setByte("Glowing", (byte) 1);
        zombieNBT.setByte("CanPickUpLoot", (byte) 0);
        zombieNBT.setString("DeathLootTable", "");
        zombieNBT.setObject("ArmorDropChances", new float[]{0.0f, 0.0f, 0.0f, 0.0f});
        zombieNBT.setObject("HandDropChances", new float[]{0.0f, 0.0f});
        zombie.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0.0d);
        // Make zombie resemble player

        ItemStack playerhead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) playerhead.getItemMeta();
        meta.setOwningPlayer(player);
        playerhead.setItemMeta(meta);
        zombie.getEquipment().setHelmet(playerhead);
        // Copy player equipment to zombie
        zombie.getEquipment().setItemInMainHand(player.getInventory().getItemInMainHand());
        zombie.getEquipment().setItemInOffHand(player.getInventory().getItemInOffHand());
        zombie.getEquipment().setChestplate(player.getEquipment().getChestplate());
        zombie.getEquipment().setLeggings(player.getEquipment().getLeggings());
        zombie.getEquipment().setBoots(player.getEquipment().getBoots());
        Main.npcs.put(player.getUniqueId(), zombie);
        // Force load chunk
        Chunk chunk = player.getChunk();
        chunk.setForceLoaded(true);
        Main.forceLoadedChunks.add(chunk);
    }

    /**
     * Remove the npc for a player
     *
     * @param player
     */
    public static void deleteNpc(Player player) {
        LivingEntity npc = Main.npcs.get(player.getUniqueId());
        Chunk chunk = npc.getChunk();
        if (chunk.isForceLoaded() && Main.forceLoadedChunks.contains(chunk))
            chunk.setForceLoaded(false);
        npc.remove();
    }

    public static void exitFreecam(Player player) {
        Main.previousState.get(player.getUniqueId()).apply(player);
        LivingEntity npc = Main.npcs.get(player.getUniqueId());
        player.teleport(npc.getLocation());
        deleteNpc(player);
        Main.npcs.remove(player.getUniqueId());
        Main.previousState.remove(player.getUniqueId());
        Main.playersInFreecam.remove(player);
    }
}
