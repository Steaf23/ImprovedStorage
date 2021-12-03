package steef23.improvedstorage.common.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import steef23.improvedstorage.common.world.level.block.BluestoneSide;
import steef23.improvedstorage.common.world.level.block.BluestoneWireBlock;
import steef23.improvedstorage.core.init.IMPSBlockEntities;

public class BluestoneWireBlockEntity extends AbstractItemPipeBlockEntity
{
	public static final int SPEED = 20;
	private boolean renderDebug = false;
	
	protected BluestoneWireBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state)
	{
		super(tileEntityTypeIn, pos, state);
	}
	
	public BluestoneWireBlockEntity(BlockPos pos, BlockState state)
	{
		this(IMPSBlockEntities.BLUESTONE_WIRE.get(), pos, state);
	}
	
	@Override
	public boolean canBeBlocked()
	{
		return true;
	}
	
	@Override
	public boolean isSideConnected(Direction direction)
	{
		if (direction == Direction.UP || direction == Direction.DOWN)
		{
			return false;
		}
		Block wireBlock = this.getBlockState().getBlock();
		return wireBlock instanceof BluestoneWireBlock && BluestoneWireBlock.getSideValue(direction, this.getBlockState()).isConnected();
	}
	
	@Override
	public Direction getTargetFace(Direction source)
	{

		Block block = this.getBlockState().getBlock();
		if (block instanceof BluestoneWireBlock)
		{
			if (BluestoneWireBlock.getSideValue(source.getOpposite(), this.getBlockState()).isEnd())
			{
				return source.getOpposite();
			}
		}
		return super.getTargetFace(source);
	}
	
	@Override
	public int getSpeed()
	{
		return SPEED;
	}
	
	public void setRenderDebug(boolean value)
	{
		assert level != null;
		if (!level.isClientSide)
		{
			this.renderDebug = value;
			this.setChanged();
		}
	}
	
	public boolean getRenderDebug()
	{
		return this.renderDebug;
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt)
	{
		super.saveAdditional(nbt);
		nbt.putBoolean("Debug", this.renderDebug);
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		this.renderDebug = nbt.getBoolean("Debug");
	}

	@Override
	public void bounceItem(PipeItem item, Direction target, AbstractItemPipeBlockEntity blockEntity)
	{
		Block b = blockEntity.getBlockState().getBlock();
		if (b instanceof BluestoneWireBlock && BluestoneWireBlock.getSideValue(target, getBlockState()).isEnd())
		{
			blockEntity.dropItem(item, null);
		}
		super.bounceItem(item, target, blockEntity);
	}
}

