package me.firephoenix.rapidreport.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.inventory.InventoryType;
import me.firephoenix.rapidreport.RapidReport;


/**
 * @author NieGestorben
 * Copyright© (c) 2024, All Rights Reserved.
 */
public class ReportGUICommand implements SimpleCommand {

    @Override
    public void execute(final Invocation invocation) {
        CommandSource commandSource = invocation.source();

        if (!(commandSource instanceof Player)) {
            commandSource.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<red>This command can only be executed by a player.");
            return;
        }


        Inventory inventory = new Inventory(InventoryType.GENERIC_9X4);
        inventory.title(ChatElement.ofLegacyText("§9Inventory"));

        ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(((Player) commandSource).getUniqueId());

        protocolizePlayer.openInventory(inventory);
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("rapidreport.gui");
    }

}
