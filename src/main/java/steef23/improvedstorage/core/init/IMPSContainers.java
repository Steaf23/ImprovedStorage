package steef23.improvedstorage.core.init;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.common.container.StoneChestContainer;
import steef23.improvedstorage.common.container.StoneGolemContainer;

public class IMPSContainers
{
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, ImprovedStorage.MOD_ID);
	
	public static final RegistryObject<ContainerType<StoneChestContainer>> STONE_CHEST = CONTAINER_TYPES
			.register("stone_chest", () -> IForgeContainerType.create(StoneChestContainer::new));
	
	public static final RegistryObject<ContainerType<StoneGolemContainer>> GOLEM_CONTAINER = CONTAINER_TYPES
			.register("golem_container", () -> IForgeContainerType.create(StoneGolemContainer::new));
}
