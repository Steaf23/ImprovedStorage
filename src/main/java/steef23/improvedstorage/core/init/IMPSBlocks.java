package steef23.improvedstorage.core.init;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.common.block.BluestoneTableBlock;
import steef23.improvedstorage.common.block.BluestoneWireBlock;
import steef23.improvedstorage.common.block.StoneChestBlock;

public class IMPSBlocks
{
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ImprovedStorage.MOD_ID);
	
	public static final RegistryObject<Block> BLUESTONE_BLOCK = BLOCKS.register("bluestone_block", () -> 
	new Block(Block.Properties.create(Material.IRON)
							  .hardnessAndResistance(0.7f,  15.0f)
							  .sound(SoundType.METAL)));
	
	public static final RegistryObject<Block> BLUESTONE_WIRE = BLOCKS.register("bluestone_wire", () -> 
	new BluestoneWireBlock(Block.Properties.create(Material.IRON)
							  			   .hardnessAndResistance(0.0f,  5.0f)
							  			   .doesNotBlockMovement()
							  			   .sound(SoundType.METAL)
							  			   .notSolid()));
	
	public static final RegistryObject<Block> STONE_CHEST = BLOCKS.register("stone_chest", () -> 
	new StoneChestBlock(() -> IMPSTileEntities.STONE_CHEST.get(), Block.Properties.create(Material.ROCK)
																								.hardnessAndResistance(0.6f, 16.0f)
																								.sound(SoundType.STONE)
																								.notSolid()));
	public static final RegistryObject<Block> BLUESTONE_TABLE = BLOCKS.register("bluestone_table", () ->
	new BluestoneTableBlock(() -> IMPSTileEntities.BLUESTONE_TABLE.get(), Block.Properties.from(Blocks.CRAFTING_TABLE)));
	
	@SuppressWarnings("unused")
	private static RotatedPillarBlock createLogBlock(MaterialColor topColor, MaterialColor barkColor)
	{
		return new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, (state) -> {
	        return state.get(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? topColor : barkColor;
	     }).hardnessAndResistance(2.0F).sound(SoundType.WOOD));
	}

}
