package fr.xephi.authme.command.executable.authme;

import fr.xephi.authme.command.ExecutableCommand;
import fr.xephi.authme.data.player.NamedIdentifier;
import fr.xephi.authme.datasource.DataSource;
import fr.xephi.authme.service.BukkitService;
import fr.xephi.authme.task.purge.PurgeExecutor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Command to purge a player.
 */
public class PurgePlayerCommand implements ExecutableCommand {

    @Inject
    private PurgeExecutor purgeExecutor;

    @Inject
    private BukkitService bukkitService;

    @Inject
    private DataSource dataSource;

    @Override
    public void executeCommand(CommandSender sender, List<String> arguments) {
        String option = arguments.size() > 1 ? arguments.get(1) : null;
        bukkitService.runTaskOptionallyAsync(
            () -> executeCommand(sender, arguments.get(0), option));
    }

    private void executeCommand(CommandSender sender, String name, String option) {
        NamedIdentifier identifier = new NamedIdentifier(name.toLowerCase(), name);
        if ("force".equals(option) || !dataSource.isAuthAvailable(identifier)) {
            OfflinePlayer offlinePlayer = bukkitService.getOfflinePlayer(name);
            purgeExecutor.executePurge(singletonList(offlinePlayer), singletonList(identifier.getLowercaseName()));
            sender.sendMessage("Purged data for player " + name);
        } else {
            sender.sendMessage("This player is still registered! Are you sure you want to proceed? "
                + "Use '/authme purgeplayer " + name + " force' to run the command anyway");
        }
    }
}
