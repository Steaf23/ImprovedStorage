package steef23.improvedstorage.core.init;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.common.entity.StoneGolemEntity;

public class IMPSEntities
{
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, ImprovedStorage.MOD_ID);
	
	public static final RegistryObject<EntityType<StoneGolemEntity>> STONE_GOLEM_ENTITY = ENTITY_TYPES
			.register("stone_golem_entity", 
					() -> EntityType.Builder.<StoneGolemEntity>create(StoneGolemEntity::new, EntityClassification.CREATURE)
					.size(1.0f, 1.6f)
					.build(new ResourceLocation(ImprovedStorage.MOD_ID, "stone_golem_entity").toString()));
}
