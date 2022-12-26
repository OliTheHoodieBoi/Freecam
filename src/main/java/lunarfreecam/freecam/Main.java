package lunarfreecam.freecam;

import FreecamUtils.NpcManager;
import com.google.gson.JsonArray;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class Main extends JavaPlugin implements Listener {
    public static HashMap<UUID, LivingEntity> npcs = new HashMap<>();
    public static ArrayList<Player> playersInFreecam = new ArrayList<>();
    public static ArrayList<Chunk> forceLoadedChunks = new ArrayList<>();
    public static String version = null;
    public static int pluginID = 81104;

    public void onEnable() {
        loadConfig();
        new FreecamCommand(this);
        new Handler(this);
        new NpcManager();
    }

    /**
     * Return all players to their last locations if server stops or restarts!
     */
    public void onDisable() {
        npcs.forEach((key, value) -> {
            Player player = Bukkit.getPlayer(key);
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(Main.npcs.get(player.getUniqueId()).getLocation());
            value.remove();
        });
    }

    public void loadConfig() {
        this.getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }
}
