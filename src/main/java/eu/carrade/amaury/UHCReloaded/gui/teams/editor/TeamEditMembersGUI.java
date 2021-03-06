/*
 * Copyright or © or Copr. Amaury Carrade (2014 - 2016)
 *
 * http://amaury.carrade.eu
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */
package eu.carrade.amaury.UHCReloaded.gui.teams.editor;

import eu.carrade.amaury.UHCReloaded.UHCReloaded;
import eu.carrade.amaury.UHCReloaded.misc.OfflinePlayersLoader;
import eu.carrade.amaury.UHCReloaded.teams.UHTeam;
import eu.carrade.amaury.UHCReloaded.utils.OfflinePlayersComparator;
import fr.zcraft.zlib.components.gui.ExplorerGui;
import fr.zcraft.zlib.components.gui.GuiAction;
import fr.zcraft.zlib.components.gui.GuiUtils;
import fr.zcraft.zlib.components.i18n.I;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;


public class TeamEditMembersGUI extends ExplorerGui<OfflinePlayer>
{
    private final UHTeam team;

    public TeamEditMembersGUI(UHTeam team)
    {
        this.team = team;
    }


    @Override
    protected void onUpdate()
    {
        /// The title of the edit team members GUI. {0} = team name (raw).
        setTitle(I.t("{0} » {black}Members", team.getName()));
        setKeepHorizontalScrollingSpace(true);

        Set<OfflinePlayer> players = new TreeSet<>(new OfflinePlayersComparator());
        players.addAll(OfflinePlayersLoader.getOfflinePlayers());
        setData(players.toArray(new OfflinePlayer[players.size()]));

        action("back", getSize() - 5, GuiUtils.makeItem(
                Material.EMERALD,
                I.t("{green}« Go back")
        ));
    }

    @Override
    protected ItemStack getViewItem(OfflinePlayer player)
    {
        ItemStack button = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) button.getItemMeta();

        String displayName = player instanceof Player ? ((Player) player).getDisplayName() : player.getName();
        UHTeam team = UHCReloaded.get().getTeamManager().getTeamForPlayer(player);

        Boolean inThisTeam = this.team.equals(team);

        meta.setOwner(player.getName());
        /// The title of a button to select a player (a skull button). {0} = player's display name.
        meta.setDisplayName(I.t("{reset}{0}", displayName));
        meta.setLore(Arrays.asList(
                player.isOnline() ? I.t("{gray}Online") : I.t("{gray}Offline"),
                team != null ? I.t("{gray}Current team: {0}", team.getDisplayName()) : I.t("{gray}Current team: none"),
                "",
                inThisTeam ? I.t("{darkgray}» {white}Click {gray}to remove this player") : I.t("{darkgray}» {white}Click {gray}to add this player")
        ));

        button.setItemMeta(meta);
        return button;
    }

    @Override
    protected ItemStack getPickedUpItem(OfflinePlayer player)
    {
        if (team.containsPlayer(player.getUniqueId()))
            team.removePlayer(player);
        else
            team.addPlayer(player);

        update();

        return null;
    }

    @GuiAction ("back")
    protected void back()
    {
        close();
    }
}
