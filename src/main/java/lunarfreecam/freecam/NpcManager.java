package lunarfreecam.freecam;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;


public class NpcManager {

    public static Main plugin;

    public NpcManager(Main plugin) {
        NpcManager.plugin = plugin;
    }

    public static void activateFreecam(Player player) {
        if (Main.npcs.containsKey(player.getUniqueId())) {
            // Exit freecam
            NpcManager.exitFreecam(player);
            return;
        }
        // Enter freecam
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            player.sendMessage(Main.Color(plugin.getConfig().getString("spectator")));
            return;
        }
        createNpc(player);
        Main.previousState.put(player.getUniqueId(), new PlayerState(player));
        player.setGameMode(GameMode.SPECTATOR);
        Main.playersInFreecam.add(player);
    }

    public static void createNpc(Player player) {
        WanderingTrader npc = player.getWorld().spawn(player.getLocation(), WanderingTrader.class);
        // Modify NBT
        NBTEntity nbt = new NBTEntity(npc);
        nbt.setByte("Silent", (byte) 1);
        nbt.setByte("NoAI", (byte) 1);
        nbt.setByte("CustomNameVisible", (byte) 1);
        String jsonName = GsonComponentSerializer.gson().serialize(player.displayName());
        nbt.setString("CustomName", jsonName);
        nbt.setByte("PersistenceRequired", (byte) 1);
        nbt.setByte("Glowing", (byte) 1);
        nbt.setString("DeathLootTable", "");
        nbt.setObject("ArmorDropChances", new float[]{0.0f, 0.0f, 0.0f, 0.0f});
        nbt.setObject("HandDropChances", new float[]{0.0f, 0.0f});
        nbt.getCompound("Offers").removeKey("Recipes");
        npc.addPotionEffects(player.getActivePotionEffects());
        npc.setRemainingAir(player.getRemainingAir());
        if (player.getGameMode().equals(GameMode.CREATIVE))
            nbt.setByte("Invulnerable", (byte) 1);
        // Make npc resemble player
        ItemStack playerhead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) playerhead.getItemMeta();
        meta.setOwningPlayer(player);
        playerhead.setItemMeta(meta);
        npc.getEquipment().setHelmet(playerhead);
        // Copy player equipment to npc
        npc.getEquipment().setItemInMainHand(player.getInventory().getItemInMainHand());
        Main.npcs.put(player.getUniqueId(), npc);
        // Vehicle
        Entity vehicle = player.getVehicle();
        if (vehicle != null) {
            vehicle.removePassenger(player);
            vehicle.addPassenger(npc);
        }
        // Force load chunk
        Chunk chunk = player.getChunk();
        chunk.setForceLoaded(true);
        Main.forceLoadedChunks.add(chunk);
    }


    public static void exitFreecam(Player player) {
        // Restore state
        Main.previousState.get(player.getUniqueId()).apply(player);
        LivingEntity npc = Main.npcs.get(player.getUniqueId());
        player.setRemainingAir(npc.getRemainingAir());
        player.teleport(npc.getLocation());
        // Remount possible vehicle
        Entity vehicle = npc.getVehicle();
        if (vehicle != null) {
            vehicle.removePassenger(npc);
            vehicle.addPassenger(player);
        }
        // Remove freecam
        deleteNpc(player);
        Main.npcs.remove(player.getUniqueId());
        Main.previousState.remove(player.getUniqueId());
        Main.playersInFreecam.remove(player);
    }

    public static void cancelFreecam(Player player) {
        NpcManager.deleteNpc(player);
        Main.npcs.remove(player.getUniqueId());
        Main.previousState.remove(player.getUniqueId());
        Main.playersInFreecam.remove(player);
    }

    public static void deleteNpc(Player player) {
        LivingEntity npc = Main.npcs.get(player.getUniqueId());
        Chunk chunk = npc.getChunk();
        if (chunk.isForceLoaded() && Main.forceLoadedChunks.contains(chunk))
            chunk.setForceLoaded(false);
        npc.remove();
    }
}
