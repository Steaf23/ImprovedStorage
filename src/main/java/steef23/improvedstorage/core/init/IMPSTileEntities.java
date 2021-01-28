package steef23.improvedstorage.core.init;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.common.tileentity.BluestoneTableTileEntity;
import steef23.improvedstorage.common.tileentity.BluestoneWireTileEntity;
import steef23.improvedstorage.common.tileentity.StoneChestTileEntity;

public class IMPSTileEntities
{
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ImprovedStorage.MOD_ID);
	
	public static final RegistryObject<TileEntityType<StoneChestTileEntity>> STONE_CHEST = TILE_ENTITY_TYPES
			.register("stone_chest", () -> TileEntityType.Builder
					.create(StoneChestTileEntity::new, IMPSBlocks.STONE_CHEST.get()).build(null));
	
	public static final RegistryObject<TileEntityType<BluestoneTableTileEntity>> BLUESTONE_TABLE = TILE_ENTITY_TYPES
			.register("bluestone_table", () -> TileEntityType.Builder
					.create(BluestoneTableTileEntity::new, IMPSBlocks.BLUESTONE_TABLE.get()).build(null));
	
	public static final RegistryObject<TileEntityType<BluestoneWireTileEntity>> BLUESTONE_WIRE = TILE_ENTITY_TYPES
			.register("bluestone_wire", () -> TileEntityType.Builder
					.create(BluestoneWireTileEntity::new, IMPSBlocks.BLUESTONE_WIRE.get()).build(null));
}
