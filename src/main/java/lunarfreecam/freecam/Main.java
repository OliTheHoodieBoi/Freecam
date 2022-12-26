package lunarfreecam.freecam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import FreecamUtils.UpdateChecker;
import FreecamUtils.UpdateChecker.UpdateReason;
import FreecamUtils.UpdateChecker.UpdateResult;
import FreecamUtils.NpcManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin implements Listener {
    public static HashMap<UUID, LivingEntity> npcs = new HashMap<>();
    public static ArrayList<Player> playersInFreecam = new ArrayList<>();
    public static UpdateReason updateResult = null;
    public static String version = null;
    public static int pluginID = 81104;

    public void onEnable() {
        loadConfig();
        UpdateChecker.init(this, pluginID);
        UpdateCheck();
        new Commands(this);
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

    private void UpdateCheck() {
        CompletableFuture<UpdateResult> task = UpdateChecker.get().requestUpdateCheck();
        task.thenAccept(this::make);
    }

    private void make(UpdateResult updateResult) {
        Main.updateResult = updateResult.getReason();
        version = updateResult.getNewestVersion();
    }
}
