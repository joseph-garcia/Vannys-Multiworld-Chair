package net.vanny.vannysmultiworldpapasan.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.vanny.vannysmultiworldpapasan.VannysMultiworldPapasan;
import net.vanny.vannysmultiworldpapasan.block.ModBlocks;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, VannysMultiworldPapasan.MODID);

    // link PapasanBlockEntity class to PAPASAN_CHAIR block
    public static final RegistryObject<BlockEntityType<PapasanBlockEntity>> PAPASAN_BE =
            BLOCK_ENTITIES.register("papasan_be", () ->
                    BlockEntityType.Builder.of(PapasanBlockEntity::new,
                            ModBlocks.PAPASAN_CHAIR.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}