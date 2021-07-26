package steef23.improvedstorage.core.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.common.world.level.block.entity.BluestoneTableBlockEntity;
import steef23.improvedstorage.common.world.level.block.entity.BluestoneWireBlockEntity;
import steef23.improvedstorage.common.world.level.block.entity.StoneChestBlockEntity;

public class IMPSBlockEntities
{
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ImprovedStorage.MOD_ID);
	
	public static final RegistryObject<BlockEntityType<StoneChestBlockEntity>> STONE_CHEST = BLOCK_ENTITY_TYPES
			.register("stone_chest", () -> BlockEntityType.Builder
					.of(StoneChestBlockEntity::new, IMPSBlocks.STONE_CHEST.get()).build(null));

	public static final RegistryObject<BlockEntityType<BluestoneTableBlockEntity>> BLUESTONE_TABLE = BLOCK_ENTITY_TYPES
			.register("bluestone_table", () -> BlockEntityType.Builder
					.of(BluestoneTableBlockEntity::new, IMPSBlocks.BLUESTONE_TABLE.get()).build(null));

	public static final RegistryObject<BlockEntityType<BluestoneWireBlockEntity>> BLUESTONE_WIRE = BLOCK_ENTITY_TYPES
			.register("bluestone_wire", () -> BlockEntityType.Builder
					.of(BluestoneWireBlockEntity::new, IMPSBlocks.BLUESTONE_WIRE.get()).build(null));
}
