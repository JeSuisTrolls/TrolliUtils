package fr.jesuistrolls.commands.player;

import fr.jesuistrolls.TrolliUtils;
import fr.jesuistrolls.configurations.Messages;
import fr.jesuistrolls.managers.LeaderBoardRewardsManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderBoardRewardsCommand implements TabExecutor {
    FileConfiguration config = TrolliUtils.getInstance().getConfig();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            Messages.PLAYER_ONLY.send(sender);
            return true;
        }

        if (!player.hasPermission(Messages.LEADERBOARD_PERMISSION.getString())) {
            Messages.NO_PERMISSION.send(sender);
            return true;
        }

        if(args.length != 3) {
            Messages.sendBrute(sender, "<red>Usage: /leaderboardrewards <leaderboard_type> <leaderboard_rank> <player_name>");
            return true;
        }

        String leaderboardType = args[0];
        int leaderboardRank;
        String playerArg = args[2];

        List<String> validTypes = getLeaderboardTypes();
        if (!validTypes.contains(leaderboardType.toLowerCase())) {
            Messages.sendBrute(sender, "<red>Type de classement invalide. Types disponibles: " + String.join(", ", validTypes));
            return true;
        }

        try {
            leaderboardRank = Integer.parseInt(args[1]);
            if (leaderboardRank < 1 || leaderboardRank > 10) {
                Messages.sendBrute(sender, "<red>Le rang doit être entre 1 et 10");
                return true;
            }
        } catch (NumberFormatException e) {
            Messages.sendBrute(sender, "<red>Le rang doit être un nombre");
            return true;
        }

        try {
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(playerArg);

            if (!targetPlayer.hasPlayedBefore() && !targetPlayer.isOnline()) {
                Messages.sendBrute(sender, "<red>Le joueur <white>" + playerArg + "</white> n'existe pas");
                return true;
            }

            boolean success = LeaderBoardRewardsManager.GiveRewards(leaderboardType, leaderboardRank, targetPlayer);
            if (success) {
                Map<String, String> replacements = new HashMap<>();
                String leaderboardName = config.getString("leaderboard-rewards.rewards-commands." + leaderboardType + ".name");
                replacements.put("%leaderboard_type%", leaderboardName);
                replacements.put("%leaderboard_rank%", String.valueOf(leaderboardRank));

                if (targetPlayer.isOnline()) {
                    Messages.LEADERBOARD_SUCCESS.sendReplace(targetPlayer.getPlayer(), replacements);
                }
                Messages.sendBrute(sender, "<green>Récompenses distribuées à " + targetPlayer.getName());
            } else {
                Messages.ERROR_COMMAND.send(sender);
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe(e.getMessage());
            Messages.sendBrute(sender, "<red>Une erreur est survenue");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tempArgs = new ArrayList<>();

        if (args.length == 1) {
            tempArgs.addAll(getLeaderboardTypes());
        }
        else if (args.length == 2) {
            tempArgs.addAll(List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
        }
        else if (args.length == 3) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                    tempArgs.add(player.getName());
                }
            }
        }

        return tempArgs;
    }

    private List<String> getLeaderboardTypes() {
        return new ArrayList<>(Bukkit.getServer().getPluginManager().getPlugin("TrolliUtils")
                .   getConfig().getConfigurationSection("leaderboard-rewards.rewards-commands").getKeys(false));
    }
}