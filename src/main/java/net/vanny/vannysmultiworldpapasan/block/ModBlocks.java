package net.vanny.vannysmultiworldpapasan.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.vanny.vannysmultiworldpapasan.VannysMultiworldPapasan;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, VannysMultiworldPapasan.MODID);

    // Register the Papasan Chair
    // .strength(1.0f) makes it easy to break; .noOcclusion() is good for non-full cube models
    public static final RegistryObject<Block> PAPASAN_CHAIR = registerBlock("papasan_chair",
            () -> new PapasanChairBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(1.0f).noOcclusion()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}