package me.firephoenix.rapidreport.ui;

import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.data.inventory.InventoryType;
import lombok.Getter;
import me.firephoenix.rapidreport.RapidReport;

import java.util.HashMap;

/**
 * @author NieGestorben
 * Copyright© (c) 2024, All Rights Reserved.
 */
public class CustomInventory extends Inventory {
    @Getter
    public HashMap<Integer, UIComponent> slotToComponentMap = new HashMap<>();

    public CustomInventory(InventoryType type) {
        super(type);
    }

    public void item(int slot, UIComponent uiComponent) {
        slotToComponentMap.put(slot, uiComponent);
        item(slot, uiComponent.getItemStack());
    }
}
