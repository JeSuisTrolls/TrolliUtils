package fr.jesuistrolls.commands.player;

import fr.jesuistrolls.configurations.Messages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BelowCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            Messages.PLAYER_ONLY.send(sender);
            return true;
        }

        if (!player.hasPermission(Messages.BELOW_PERMISSION.getString())) {
            Messages.NO_PERMISSION.send(sender);
            return true;
        }

        if (!Messages.BELOW_WORLD_WHITELIST.getStringList().contains(player.getWorld().getName())) {
            Messages.ERROR_COMMAND.send(sender);
            return true;
        }

        int minY = player.getWorld().getMinHeight();
        int playerY = player.getLocation().getBlockY();
        int x = player.getLocation().getBlockX();
        int z = player.getLocation().getBlockZ();

        for (int y = minY; y <= playerY; y++) {
            Block ground = player.getWorld().getBlockAt(x, y, z);
            Block above1 = player.getWorld().getBlockAt(x, y + 1, z);
            Block above2 = player.getWorld().getBlockAt(x, y + 2, z);

            if (isSafeGround(ground) && isSafeAir(above1) && isSafeAir(above2)) {
                Location tpLocation = new Location(player.getWorld(), x + 0.5, y + 1, z + 0.5);
                player.teleport(tpLocation);
                Messages.BELOW_SUCCESS.send(sender);
                return true;
            }
        }

        Messages.ERROR_COMMAND.send(sender);
        return false;
    }

    private boolean isSafeGround(Block block) {
        Material type = block.getType();
        return !block.isPassable() && type != Material.LAVA && type != Material.FIRE;
    }

    private boolean isSafeAir(Block block) {
        Material type = block.getType();
        return block.isPassable() && type != Material.LAVA && type != Material.FIRE;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
