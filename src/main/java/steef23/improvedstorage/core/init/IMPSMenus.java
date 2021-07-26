package steef23.improvedstorage.core.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.common.world.inventory.StoneChestMenu;
import steef23.improvedstorage.common.world.inventory.StoneGolemMenu;

public class IMPSMenus
{
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, ImprovedStorage.MOD_ID);
	
	public static final RegistryObject<MenuType<StoneChestMenu>> STONE_CHEST = MENU_TYPES
			.register("stone_chest", () -> IForgeContainerType.create(StoneChestMenu::new));
	
	public static final RegistryObject<MenuType<StoneGolemMenu>> STONE_GOLEM = MENU_TYPES
			.register("golem_container", () -> IForgeContainerType.create(StoneGolemMenu::new));
}
