package steef23.improvedstorage.common.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import steef23.improvedstorage.common.world.level.block.entity.BluestoneTableBlockEntity;
import steef23.improvedstorage.core.init.IMPSBlockEntities;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Supplier;

public class BluestoneTableBlock extends Block implements EntityBlock
{

	@SuppressWarnings("unused")
	private final Supplier<BlockEntityType<? extends BluestoneTableBlockEntity>> tileEntityTypeSupplier;
	
	public BluestoneTableBlock(Supplier<BlockEntityType<? extends BluestoneTableBlockEntity>> tileEntityTypeSupplierIn, Properties propertiesIn)
	{
		super(propertiesIn);
		this.tileEntityTypeSupplier = tileEntityTypeSupplierIn;
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player,
											  InteractionHand handIn, BlockHitResult hit)
	{
		if (!worldIn.isClientSide)
		{
			BlockEntity te = worldIn.getBlockEntity(pos);
			if (te instanceof BluestoneTableBlockEntity table)
			{
				table.updateConnectedTileEntities();
				ArrayList<BlockEntity> connectedTileEntities = table.getConnectedTileEntities();
				for(BlockEntity connectedTE : connectedTileEntities)
				{
					System.out.println(connectedTE.getBlockState().getBlock().getName().getString());
				}
			}
			System.out.println("LOL");
		}
		
		return InteractionResult.SUCCESS;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return IMPSBlockEntities.BLUESTONE_TABLE.get().create(pos, state);
	}
}
