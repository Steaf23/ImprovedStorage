package steef23.improvedstorage.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import steef23.improvedstorage.common.block.BluestoneSide;
import steef23.improvedstorage.common.block.BluestoneWireBlock;
import steef23.improvedstorage.core.init.IMPSTileEntities;

public class BluestoneWireTileEntity extends AbstractItemPipeTileEntity
{
	private boolean renderDebug = false;
	
	protected BluestoneWireTileEntity(TileEntityType<?> typeIn) 
	{
		super(typeIn, 20);
	}
	
	public BluestoneWireTileEntity()
	{
		this(IMPSTileEntities.BLUESTONE_WIRE.get());
	}
	
	@Override
	protected boolean doEndsBounceBack()
	{
		return false;
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

	@Override
	public boolean isSideConnected(Direction direction)
	{
		if (direction == Direction.UP || direction == Direction.DOWN)
		{
			return false;
		}
		Block wireBlock = this.getBlockState().getBlock();
		return wireBlock instanceof BluestoneWireBlock ? 
				((BluestoneWireBlock)wireBlock).getSide(this.world, this.pos, direction) != BluestoneSide.NONE : false;
	}
}

