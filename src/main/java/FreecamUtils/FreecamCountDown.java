package FreecamUtils;

import com.cryptomorin.xseries.messages.ActionBar;
import lunarfreecam.freecam.FreecamCommand;
import lunarfreecam.freecam.Main;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FreecamCountDown extends BukkitRunnable {
    private Player player;
    private Integer seconds;
    private Main plugin;
    private GameMode mode;
    NpcManager npcmngr;
    public FreecamCountDown(Player player, Integer seconds,Main main){
        this.player= player;
        this.seconds = seconds;
        this.plugin = main;
        this.mode= FreecamCommand.previousGamemode.get(player);
        this.npcmngr = new NpcManager();
    }
    @Override
    public void run() {
        if(!player.isOnline() || !Main.npcs.containsKey(player.getUniqueId()) || Main.npcs.get(player.getUniqueId()).isDead() || player.isDead()){
            player.sendMessage(utils.Color(plugin.getConfig().getString("freecam-canceled")));
            ActionBar.sendActionBar(player,utils.Color(plugin.getConfig().getString("freecam-canceled")));
            this.cancel();
            return;
        }
        if(plugin.getConfig().getDouble("freecam-max-distance")>0 || seconds>0) {
            if ( !((Entity)Main.npcs.get(player.getUniqueId())).getWorld().equals(player.getWorld()) || getDistanceBetweenEntities(player, (Entity) Main.npcs.get(player.getUniqueId())) > plugin.getConfig().getDouble("freecam-max-distance")) {
                this.cancel();
                npcmngr.exitFreecam(player, mode);
                player.sendMessage(utils.Color(plugin.getConfig().getString("freecam-max-distance-reach")));
                ActionBar.sendActionBar(player, utils.Color(plugin.getConfig().getString("freecam-max-distance-reach")));

            } else if (seconds >= 0) {
                ActionBar.sendActionBar(player, utils.Color(plugin.getConfig().getString("freecam-action-bar").replace("%seconds%", seconds.toString())));
                seconds -= 1;
            } else {
                this.cancel();
                npcmngr.exitFreecam(player, mode);
            }
        }else{
            ActionBar.sendActionBar(player, utils.Color(plugin.getConfig().getString("freecam-action-bar").replace("%seconds%", "")));
        }


    }
    public double getDistanceBetweenEntities(Entity e1, Entity e2){
        return e1.getLocation().distance(e2.getLocation());
    }
}
