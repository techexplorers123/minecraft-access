package com.github.khanshoaib3.minecraft_access.features.InventoryControls;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.mixin.HandledScreenAccessor;
import com.github.khanshoaib3.minecraft_access.mixin.RecipeBookWidgetAccessor;
import com.github.khanshoaib3.minecraft_access.mixin.SlotAccessor;
import com.github.khanshoaib3.minecraft_access.mixin.StonecutterScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.FurnaceFuelSlot;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.TradeOutputSlot;
import net.minecraft.village.MerchantInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GroupGenerator {
    public static List<SlotsGroup> generateGroupsFromSlots(HandledScreenAccessor screen) {
        List<SlotsGroup> foundGroups;

        if (screen instanceof CreativeInventoryScreen creativeInventoryScreen) {
            foundGroups = forCreativeInventoryScreen(creativeInventoryScreen);
        } else if (screen instanceof InventoryScreen inventoryScreen) {
            foundGroups = forPlayerInventoryScreen(inventoryScreen);
        } else {
            foundGroups = forOtherScreens(screen);
        }
        return foundGroups;
    }

    private static @NotNull List<SlotsGroup> forOtherScreens(@NotNull HandledScreenAccessor screen) {
        List<SlotsGroup> foundGroups = new ArrayList<>();

        List<Slot> slots = new ArrayList<>(screen.getHandler().slots);

        SlotsGroup hotbarGroup = new SlotsGroup("Hotbar", null);
        SlotsGroup playerInventoryGroup = new SlotsGroup("Player Inventory", null);
        SlotsGroup armourGroup = new SlotsGroup("Armour", null);
        SlotsGroup offHandGroup = new SlotsGroup("Off Hand", null);
        SlotsGroup itemOutputGroup = new SlotsGroup("Item Output", null);
        SlotsGroup itemInputGroup = new SlotsGroup("Item Input", null);
        SlotsGroup recipesGroup = new SlotsGroup("Recipes", null);
        SlotsGroup fuelInputGroup = new SlotsGroup("Fuel Input", null);
        SlotsGroup craftingOutputGroup = new SlotsGroup("Crafting Output", null);
        SlotsGroup craftingInputGroup = new SlotsGroup("Crafting Input", null);
        SlotsGroup blockInventoryGroup = new SlotsGroup("Block Inventory", null);
        SlotsGroup unknownGroup = new SlotsGroup("Unknown", null);

        for (Slot s : slots) {
            int index = ((SlotAccessor) s).getInventoryIndex();

            if (s.inventory instanceof PlayerInventory && index >= 0 && index <= 8) {
                hotbarGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            if (s.inventory instanceof PlayerInventory && index >= 9 && index <= 35) {
                playerInventoryGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            if (s.inventory instanceof PlayerInventory && index >= 36 && index <= 39) {
                armourGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            if (s.inventory instanceof PlayerInventory && index == 40) {
                offHandGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getHandler() instanceof AbstractFurnaceScreenHandler && index == 2) {
                itemOutputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getHandler() instanceof AbstractFurnaceScreenHandler && index == 1) {
                fuelInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getHandler() instanceof AbstractFurnaceScreenHandler && index == 0) {
                itemInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getHandler() instanceof MerchantScreenHandler && (index==0||index==1)) {
                itemInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getHandler() instanceof MerchantScreenHandler && index==2) {
                itemOutputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getHandler() instanceof StonecutterScreenHandler && index == 0) {
                itemInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getHandler() instanceof StonecutterScreenHandler && index == 1) {
                itemOutputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getHandler() instanceof CartographyTableScreenHandler && (index == 0 || index == 1)) {
                itemInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getHandler() instanceof CartographyTableScreenHandler && index == 2) {
                itemOutputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getHandler() instanceof GenericContainerScreenHandler && s.inventory instanceof SimpleInventory) {
                blockInventoryGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getHandler() instanceof Generic3x3ContainerScreenHandler && s.inventory instanceof SimpleInventory) {
                blockInventoryGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getHandler() instanceof HopperScreenHandler && s.inventory instanceof SimpleInventory) {
                blockInventoryGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (s.inventory instanceof CraftingResultInventory && !(s instanceof FurnaceOutputSlot)) {
                itemOutputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (s.inventory instanceof CraftingInventory) {
                itemInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            unknownGroup.slotItems.add(new SlotItem(s));
        }

        if (armourGroup.slotItems.size() > 0)
            foundGroups.add(armourGroup);

        if (offHandGroup.slotItems.size() > 0)
            foundGroups.add(offHandGroup);

        if (playerInventoryGroup.slotItems.size() > 0) {
            playerInventoryGroup.mapTheGroupList(9);
            foundGroups.add(playerInventoryGroup);
        }

        if (hotbarGroup.slotItems.size() > 0) {
            hotbarGroup.mapTheGroupList(9);
            foundGroups.add(hotbarGroup);
        }

        if (craftingInputGroup.slotItems.size() > 0) {
            craftingInputGroup.setRowColumnPrefixForSlots();
            foundGroups.add(craftingInputGroup);
        }

        if (craftingOutputGroup.slotItems.size() > 0) {
            foundGroups.add(craftingOutputGroup);
        }

        if (itemInputGroup.slotItems.size() > 0) {
            foundGroups.add(itemInputGroup);
        }

        if (fuelInputGroup.slotItems.size() > 0) {
            foundGroups.add(fuelInputGroup);
        }

        if (itemOutputGroup.slotItems.size() > 0) {
            foundGroups.add(itemOutputGroup);
        }

        if (blockInventoryGroup.slotItems.size() > 0) {
            if (screen.getHandler() instanceof Generic3x3ContainerScreenHandler)
                blockInventoryGroup.mapTheGroupList(3);
            else if (screen.getHandler() instanceof GenericContainerScreenHandler)
                blockInventoryGroup.mapTheGroupList(9);
            else if (screen.getHandler() instanceof HopperScreenHandler)
                blockInventoryGroup.mapTheGroupList(5);
            foundGroups.add(blockInventoryGroup);
        }

        if (unknownGroup.slotItems.size() > 0) {
            foundGroups.add(unknownGroup);
        }

        if (MinecraftClient.getInstance().currentScreen instanceof StonecutterScreen stonecutterScreen) {
            // Refer to StonecutterScreen.java -->> renderRecipeIcons()
            int x = ((HandledScreenAccessor) stonecutterScreen).getX() + 52;
            int y = ((HandledScreenAccessor) stonecutterScreen).getY() + 14;
            int scrollOffset = ((StonecutterScreenAccessor) stonecutterScreen).getScrollOffset();
            List<StonecuttingRecipe> list = stonecutterScreen.getScreenHandler().getAvailableRecipes();

            for (int i = scrollOffset; i < scrollOffset + 12 && i < stonecutterScreen.getScreenHandler().getAvailableRecipeCount(); ++i) {
                int j = i - scrollOffset;
                int k = x + j % 4 * 16;
                int l = j / 4;
                int m = y + l * 18 + 2;

                int realX = k - ((HandledScreenAccessor) stonecutterScreen).getX() + 8;
                int realY = m - ((HandledScreenAccessor) stonecutterScreen).getY() + 8;
                recipesGroup.slotItems.add(new SlotItem(realX, realY, list.get(i).getOutput()));
            }

            recipesGroup.isScrollable = true;
            if (recipesGroup.slotItems.size() > 0) {
                recipesGroup.mapTheGroupList(4);
                foundGroups.add(recipesGroup);
            }
        }

        return foundGroups;
    }

    private static @NotNull List<SlotsGroup> forPlayerInventoryScreen(@NotNull InventoryScreen inventoryScreen) {
        List<SlotsGroup> foundGroups = new ArrayList<>();

        MainClass.infoLog("Recipe Book:%s Tab:%s".formatted(inventoryScreen.getRecipeBookWidget().isOpen(), ((RecipeBookWidgetAccessor) inventoryScreen.getRecipeBookWidget()).getCurrentTab().getCategory().name()));

        List<Slot> slots = new ArrayList<>(((HandledScreenAccessor) inventoryScreen).getHandler().slots);

        SlotsGroup hotbarGroup = new SlotsGroup("Hotbar", null);
        SlotsGroup playerInventoryGroup = new SlotsGroup("Player Inventory", null);
        SlotsGroup armourGroup = new SlotsGroup("Armour", null);
        SlotsGroup offHandGroup = new SlotsGroup("Off Hand", null);
        SlotsGroup craftingOutputGroup = new SlotsGroup("Crafting Output", null);
        SlotsGroup craftingInputGroup = new SlotsGroup("Crafting Input", null);

        for (Slot s : slots) {
            int index = ((SlotAccessor) s).getInventoryIndex();

            if (s.inventory instanceof PlayerInventory && index >= 0 && index <= 8) {
                hotbarGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            if (s.inventory instanceof PlayerInventory && index >= 9 && index <= 35) {
                playerInventoryGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            if (s.inventory instanceof PlayerInventory && index >= 36 && index <= 39) {
                armourGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            if (s.inventory instanceof PlayerInventory && index == 40) {
                offHandGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (s.inventory instanceof CraftingResultInventory && index == 0) {
                craftingOutputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (s.inventory instanceof CraftingInventory && index >= 0 && index <= 3) {
                craftingInputGroup.slotItems.add(new SlotItem(s));
            }
        }

        armourGroup.mapTheGroupList(4, true);
        playerInventoryGroup.mapTheGroupList(9);
        hotbarGroup.mapTheGroupList(9);
        craftingInputGroup.mapTheGroupList(2);
        craftingInputGroup.setRowColumnPrefixForSlots();

        foundGroups.add(armourGroup);
        foundGroups.add(offHandGroup);
        foundGroups.add(playerInventoryGroup);
        foundGroups.add(hotbarGroup);
        foundGroups.add(craftingInputGroup);
        foundGroups.add(craftingOutputGroup);


        for (Slot s : slots) {
            int index = ((SlotAccessor) s).getInventoryIndex();
            int centreX = s.x + 9;
            int centreY = s.y + 9;

            MainClass.infoLog("Slot index:%d x:%d y:%d InvClass:%s SlotClass:%s".formatted(index, centreX, centreY, s.inventory.getClass().getName(), s.getClass().getName()));
        }
        int mouseX = (int) ((MinecraftClient.getInstance().mouse.getX() - MinecraftClient.getInstance().getWindow().getX()) / MinecraftClient.getInstance().getWindow().getScaleFactor()) - ((HandledScreenAccessor) inventoryScreen).getX();
        int mouseY = (int) ((MinecraftClient.getInstance().mouse.getY() - MinecraftClient.getInstance().getWindow().getY()) / MinecraftClient.getInstance().getWindow().getScaleFactor()) - ((HandledScreenAccessor) inventoryScreen).getY();
        MainClass.infoLog("Mouse x:%d y:%d".formatted(mouseX, mouseY));
        MainClass.infoLog("\n\n");

        return foundGroups;
    }

    private static @NotNull List<SlotsGroup> forCreativeInventoryScreen(@NotNull CreativeInventoryScreen creativeInventoryScreen) {
        List<SlotsGroup> foundGroups = new ArrayList<>();
        List<Slot> slots = new ArrayList<>(creativeInventoryScreen.getScreenHandler().slots);

        if (creativeInventoryScreen.getSelectedTab() == 11) {
            SlotsGroup deleteItemGroup = new SlotsGroup("Delete Items", null);
            SlotsGroup offHandGroup = new SlotsGroup("Off Hand", null);
            SlotsGroup hotbarGroup = new SlotsGroup("Hotbar", null);
            SlotsGroup armourGroup = new SlotsGroup("Armour", null); //FIXME
            SlotsGroup playerInventoryGroup = new SlotsGroup("Player Inventory", null);

            for (Slot s : slots) {
                if (s.x < 0 || s.y < 0) continue;

                int index = ((SlotAccessor) s).getInventoryIndex();

                if (index == 0) {
                    deleteItemGroup.slotItems.add(new SlotItem(s));
                    continue;
                }

                if (index >= 5 && index <= 8) {
                    armourGroup.slotItems.add(new SlotItem(s));
                    continue;
                }

                if (index >= 9 && index <= 35) {
                    playerInventoryGroup.slotItems.add(new SlotItem(s));
                    continue;
                }

                if (index >= 36 && index <= 44) {
                    hotbarGroup.slotItems.add(new SlotItem(s));
                    continue;
                }

                if (index == 45) {
                    offHandGroup.slotItems.add(new SlotItem(s));
                }
            }

            armourGroup.mapTheGroupList(2, true);
            playerInventoryGroup.mapTheGroupList(9);
            hotbarGroup.mapTheGroupList(9);

            foundGroups.add(armourGroup);
            foundGroups.add(offHandGroup);
            foundGroups.add(playerInventoryGroup);
            foundGroups.add(hotbarGroup);
            foundGroups.add(deleteItemGroup);
        } else {
            SlotsGroup hotbarGroup = new SlotsGroup("Hotbar", null);
            SlotsGroup tabInventoryGroup = new SlotsGroup("Tab Inventory", null);
            tabInventoryGroup.isScrollable = true;

            for (Slot s : slots) {
                if (s.x < 0 || s.y < 0) continue;

                int index = ((SlotAccessor) s).getInventoryIndex();

                if (index >= 0 && index <= 8 && s.inventory instanceof PlayerInventory) {
                    hotbarGroup.slotItems.add(new SlotItem(s));
                    continue;
                }

                if (index >= 0 && index <= 44) {
                    tabInventoryGroup.slotItems.add(new SlotItem(s));
                }
            }

            tabInventoryGroup.mapTheGroupList(9);
            hotbarGroup.mapTheGroupList(9);

            foundGroups.add(tabInventoryGroup);
            foundGroups.add(hotbarGroup);
        }
        return foundGroups;
    }
}
