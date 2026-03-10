package org.ecnumc.ecnu.common.registries;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.ecnumc.ecnu.common.blocks.DisgustedEggBlock;

import static org.ecnumc.ecnu.ECNUForge.MODID;

public final class ECNUBlocks {
    private static final DeferredRegister<Block> REGISTER =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<Block> DISGUSTED_STONE =
            REGISTER.register("disgusted_stone", ()-> new Block(Block.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> DISGUSTED_EGG =
            REGISTER.register("disgusted_egg", DisgustedEggBlock::new);

    public static void init(IEventBus modBus)
    {
        REGISTER.register(modBus);
    }

    private ECNUBlocks() {
    }
}
