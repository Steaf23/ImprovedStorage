package steef23.improvedstorage.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import steef23.improvedstorage.common.block.BluestoneWireBlock;
import steef23.improvedstorage.core.init.IMPSTileEntities;

public class BluestoneWireTileEntity extends AbstractItemPipeTileEntity
{
	public static final int SPEED = 2;
	private boolean renderDebug = false;
	
	protected BluestoneWireTileEntity(TileEntityType<?> tileEntityTypeIn) 
	{
		super(tileEntityTypeIn);
	}
	
	public BluestoneWireTileEntity()
	{
		this(IMPSTileEntities.BLUESTONE_WIRE.get());
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
		return wireBlock instanceof BluestoneWireBlock ? 
				((BluestoneWireBlock)wireBlock).getSide(this.world, this.pos, direction).isValid() : false;
	}
	
	@Override
	public Direction getTargetFace(Direction source)
	{
		
		if (source != Direction.UP || source != Direction.DOWN)
		{
			Block block = this.getBlockState().getBlock();
			if (block instanceof BluestoneWireBlock)
			{
				if (((BluestoneWireBlock)block).getSide(this.getWorld(), this.getPos(), source.getOpposite()).isEnd())
				{
					return source.getOpposite();
				}
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
		if (!world.isRemote)
		{
			this.renderDebug = value;
			this.markDirty();
		}
	}
	
	public boolean getRenderDebug()
	{
		return this.renderDebug;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt)
	{
		super.write(nbt);
		nbt.putBoolean("Debug", this.renderDebug);
		return nbt;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt)
	{
		super.read(state, nbt);
		this.renderDebug = nbt.getBoolean("Debug");
	}
}

