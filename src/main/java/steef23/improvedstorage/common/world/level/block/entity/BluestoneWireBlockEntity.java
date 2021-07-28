package steef23.improvedstorage.common.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
		return wireBlock instanceof BluestoneWireBlock && ((BluestoneWireBlock) wireBlock).getSide(this.level, this.getBlockPos(), direction).isConnected();
	}
	
	@Override
	public Direction getTargetFace(Direction source)
	{

		Block block = this.getBlockState().getBlock();
		if (block instanceof BluestoneWireBlock)
		{
			if (((BluestoneWireBlock)block).getSide(this.level, this.getBlockPos(), source.getOpposite()).isEnd())
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
	public CompoundTag save(CompoundTag nbt)
	{
		super.save(nbt);
		nbt.putBoolean("Debug", this.renderDebug);
		return nbt;
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		this.renderDebug = nbt.getBoolean("Debug");
	}
}

