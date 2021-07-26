package steef23.improvedstorage.core.init;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.common.world.level.block.BluestoneTableBlock;
import steef23.improvedstorage.common.world.level.block.BluestoneWireBlock;
import steef23.improvedstorage.common.world.level.block.StoneChestBlock;

public class IMPSBlocks
{
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ImprovedStorage.MOD_ID);
	
	public static final RegistryObject<Block> BLUESTONE_BLOCK = BLOCKS.register("bluestone_block", () ->
	new Block(Block.Properties.of(Material.METAL)
							  .strength(0.7f,  15.0f)
							  .sound(SoundType.METAL)));

	public static final RegistryObject<Block> BLUESTONE_WIRE = BLOCKS.register("bluestone_wire", () ->
	new BluestoneWireBlock(Block.Properties.of(Material.METAL)
							  			   .strength(0.0f,  5.0f)
							  			   .noCollission()
							  			   .sound(SoundType.METAL)));

	public static final RegistryObject<Block> STONE_CHEST = BLOCKS.register("stone_chest", () ->
	new StoneChestBlock(IMPSBlockEntities.STONE_CHEST::get, Block.Properties.of(Material.STONE)
																			.strength(0.6f, 16.0f)
																			.sound(SoundType.STONE)
																			.noOcclusion()));
	public static final RegistryObject<Block> BLUESTONE_TABLE = BLOCKS.register("bluestone_table", () ->
	new BluestoneTableBlock(IMPSBlockEntities.BLUESTONE_TABLE::get, Block.Properties.copy(Blocks.CRAFTING_TABLE)));

	@SuppressWarnings("unused")
	private static RotatedPillarBlock createLogBlock(MaterialColor topColor, MaterialColor barkColor)
	{
		return new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, (state) -> {
	        return state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? topColor : barkColor;
	     }).strength(2.0F).sound(SoundType.WOOD));
	}

}
