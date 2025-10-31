package fr.jesuistrolls.commands;

import fr.jesuistrolls.TrolliUtils;
import fr.jesuistrolls.commands.player.BelowCommand;
import fr.jesuistrolls.commands.player.LeaderBoardRewardsCommand;
import fr.jesuistrolls.commands.player.TransfertCommand;
import fr.jesuistrolls.configurations.Messages;
import org.bukkit.Bukkit;

public class CommandLoader {

    private final TrolliUtils plugin;

    public CommandLoader(TrolliUtils plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        if (Messages.BELOW_ENABLE.getBoolean()) {
            plugin.getCommand("below").setExecutor(new BelowCommand());
            Bukkit.getLogger().info("[TrolliUtils] Enable Below Command...");
        }
        if (Messages.TRANSFERT_ENABLE.getBoolean()){
            Bukkit.getLogger().info("[TrolliUtils] Enable Transfert Command...");
            plugin.getCommand("transfert").setExecutor(new TransfertCommand());
            plugin.getCommand("transfert").setTabCompleter(new TransfertCommand());
        }
        if (Messages.LEADERBOARD_ENABLE.getBoolean()){
            Bukkit.getLogger().info("[TrolliUtils] Enable Leaderboardrewards Command...");
            plugin.getCommand("leaderboardrewards").setExecutor(new LeaderBoardRewardsCommand());
            plugin.getCommand("leaderboardrewards").setTabCompleter(new LeaderBoardRewardsCommand());
         }
    }
}
