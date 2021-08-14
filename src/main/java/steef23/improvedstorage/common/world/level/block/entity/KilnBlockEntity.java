package steef23.improvedstorage.common.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import steef23.improvedstorage.ImprovedStorage;
import steef23.improvedstorage.common.world.inventory.KilnMenu;
import steef23.improvedstorage.core.init.IMPSBlockEntities;

public class KilnBlockEntity extends AbstractFurnaceBlockEntity
{
    public KilnBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, ImprovedStorage.FIRING_RECIPE_TYPE);
    }

    public KilnBlockEntity(BlockPos pos, BlockState state)
    {
        this(IMPSBlockEntities.KILN.get(), pos, state);
    }

    @Override
    protected Component getDefaultName()
    {
        return new TranslatableComponent("container.kiln");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory)
    {
        return new KilnMenu(id, inventory, this, this.dataAccess);
    }
}
