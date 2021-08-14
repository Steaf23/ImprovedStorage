package steef23.improvedstorage.common.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import steef23.improvedstorage.common.world.level.block.entity.KilnBlockEntity;
import steef23.improvedstorage.core.init.IMPSBlockEntities;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class KilnBlock extends AbstractFurnaceBlock
{
    private final Supplier<BlockEntityType<? extends KilnBlockEntity>> blockEntitySupplier;

    public KilnBlock(Supplier<BlockEntityType<? extends KilnBlockEntity>> blockEntitySupplier, Properties properties)
    {
        super(properties);
        this.blockEntitySupplier = blockEntitySupplier;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new KilnBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> entityType)
    {
        return createFurnaceTicker(level, entityType, IMPSBlockEntities.KILN.get());
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player)
    {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof KilnBlockEntity furnace)
        {
            player.openMenu(furnace);
        }
    }
}
