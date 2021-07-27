package steef23.improvedstorage.common.world.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;
import steef23.improvedstorage.client.renderer.blockentity.StoneChestRenderer;
import steef23.improvedstorage.client.renderer.item.StoneChestItemStackRenderer;
import steef23.improvedstorage.common.world.level.block.StoneChestBlock;
import steef23.improvedstorage.common.world.level.block.entity.StoneChestBlockEntity;

import java.util.function.Consumer;

public class StoneChestItem extends BlockItem
{
    public StoneChestItem(Block block, Item.Properties properties)
    {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer)
    {
        consumer.accept(new IItemRenderProperties()
        {
            final BlockEntityWithoutLevelRenderer chestRenderer = new StoneChestItemStackRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer()
            {
                return chestRenderer;
            }
        });
    }
}
