package steef23.improvedstorage.core.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.common.world.entity.StoneGolem;

public class IMPSEntities
{
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, ImprovedStorage.MOD_ID);
	
	public static final RegistryObject<EntityType<StoneGolem>> STONE_GOLEM = ENTITY_TYPES
			.register("stone_golem_entity", 
					() -> EntityType.Builder.of(StoneGolem::new, MobCategory.CREATURE)
					.sized(0.7f, 1.6f)
					.build(new ResourceLocation(ImprovedStorage.MOD_ID, "stone_golem_entity").toString()));
}
