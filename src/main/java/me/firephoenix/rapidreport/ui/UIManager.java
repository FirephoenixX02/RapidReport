package me.firephoenix.rapidreport.ui;

import com.velocitypowered.api.command.SimpleCommand;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import me.firephoenix.rapidreport.RapidReport;
import me.firephoenix.rapidreport.utils.Report;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NieGestorben
 * Copyright© (c) 2024, All Rights Reserved.
 */
public class UIManager {

    public CustomInventory createReportListGUI(ArrayList<Report> reports, ProtocolizePlayer protocolizePlayer) {
        CustomInventory inventory = new CustomInventory(InventoryType.GENERIC_9X6);
        inventory.title(ChatElement.ofLegacyText("§cReports"));

        //Top Row of Glass
        for (int i = 0; i < 9; i++) {
            inventory.item(i, new ItemStack(ItemType.GRAY_STAINED_GLASS_PANE));
        }

        // Populate the inventory with reports once they are fetched
        int slot = 10;
        for (Report report : reports) {
            if (slot > 43) break;
            List<ChatElement<?>> lore = new ArrayList<>();
            lore.add(ChatElement.ofLegacyText("§cReported by: §7" + report.reporterPlayerName));
            lore.add(ChatElement.ofLegacyText("§cReason: §7" + report.reason));
            lore.add(ChatElement.ofLegacyText("§cStatus: §7" + report.status));

            ItemStack reportItemStack = new ItemStack(ItemType.PAPER);

            reportItemStack.displayName(ChatElement.ofLegacyText("§c" + report.reportedPlayerName));
            reportItemStack.lore(lore);

            UIComponent reportComponent = new UIComponent(reportItemStack);

            reportComponent.setClickListener(() -> {
                protocolizePlayer.openInventory(createReportEditGUI(report, protocolizePlayer));
            });

            inventory.item(slot, reportComponent);
            slot++;
        }

        // Bottom Row of Glass
        for (int i = 45; i < 54; i++) {
            inventory.item(i, new ItemStack(ItemType.GRAY_STAINED_GLASS_PANE));
        }

        inventory.onClick(click -> {
            if (click.clickType() != ClickType.LEFT_CLICK) return;
            if (inventory.getSlotToComponentMap().containsKey(click.slot())) {
                inventory.getSlotToComponentMap().get(click.slot()).runClickListener(click.player());
            }
        });
        return inventory;
    }

    public CustomInventory createReportEditGUI(Report report, ProtocolizePlayer protocolizePlayer) {
        CustomInventory inventory = new CustomInventory(InventoryType.GENERIC_9X5);
        inventory.title(ChatElement.ofLegacyText("§cEdit-Report"));

        //Top Row of Glass
        for (int i = 0; i < 9; i++) {
            inventory.item(i, new ItemStack(ItemType.GRAY_STAINED_GLASS_PANE));
        }

        ItemStack closeItemStack = new ItemStack(ItemType.BARRIER);

        closeItemStack.displayName(ChatElement.ofLegacyText("§c" + "Close Report"));

        UIComponent closeComponent = new UIComponent(closeItemStack);

        closeComponent.setClickListener(() -> {
            RapidReport.INSTANCE.getDataBaseManager().closeReport(report);
            protocolizePlayer.closeInventory();
        });

        inventory.item(20, closeComponent);


        // Bottom Row of Glass
        for (int i = 36; i < 45; i++) {
            inventory.item(i, new ItemStack(ItemType.GRAY_STAINED_GLASS_PANE));
        }

        inventory.onClick(click -> {
            if (click.clickType() != ClickType.LEFT_CLICK) return;
            if (inventory.getSlotToComponentMap().containsKey(click.slot())) {
                inventory.getSlotToComponentMap().get(click.slot()).runClickListener(click.player());
            }
        });

        return inventory;
    }
}
