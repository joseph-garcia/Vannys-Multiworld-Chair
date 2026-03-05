package net.vanny.vannysmultiworldpapasan.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.vanny.vannysmultiworldpapasan.VannysMultiworldPapasan;
import net.vanny.vannysmultiworldpapasan.block.ModBlocks;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, VannysMultiworldPapasan.MODID);

    // links item to Block
    public static final RegistryObject<Item> PAPASAN_CHAIR_ITEM = ITEMS.register("papasan_chair",
            () -> new BlockItem(ModBlocks.PAPASAN_CHAIR.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}