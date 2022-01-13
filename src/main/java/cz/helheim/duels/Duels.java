package cz.helheim.duels;

import cz.helheim.duels.commands.MenuCommand;
import cz.helheim.duels.listeners.GameListener;
import cz.helheim.duels.listeners.JoinListener;
import cz.helheim.duels.listeners.PlayerDeathListener;
import cz.helheim.duels.commands.DuelCommand;
import cz.helheim.duels.commands.QueueCommand;
import cz.helheim.duels.maps.MapManager;
import cz.helheim.duels.utils.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Duels extends JavaPlugin {
    private static Duels instance;

    public Duels(){
        instance = this;
    }

    public static Duels getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().severe("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        Bukkit.getLogger().severe("Duels | Developed by: Maxbans9");
        Bukkit.getLogger().severe("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        getConfig();
        saveDefaultConfig();
        FileUtil fileUtil = new FileUtil(this);
        fileUtil.setupFiles();

        //mapManager = new MapManager(fileUtil.getMapsYAML()); //TODO
        register();

    }

    @Override
    public void onDisable() {

    }

    public void register(){
        getCommand("duel").setExecutor(new DuelCommand());
        getCommand("queue").setExecutor(new QueueCommand());
        getCommand("menu").setExecutor(new MenuCommand());
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
    }

}

