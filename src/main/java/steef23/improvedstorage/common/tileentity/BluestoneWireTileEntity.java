package steef23.improvedstorage.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
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
	protected PipeConnectionType setConnectionType(Direction face)
	{
		if (face != Direction.DOWN || face != Direction.UP)
		{	
			TileEntity te = this.world.getTileEntity(this.pos.offset(face));
			if (te != null)
			{
				if (te instanceof BluestoneWireTileEntity)
				{
					return PipeConnectionType.PIPE;
				}
				else if (te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite()).isPresent())
				{
					return PipeConnectionType.INVENTORY;
				}
			}
			else
			{
				if (this.getBlockState().get(BluestoneWireBlock.FACING_PROPERTY_MAP.get(face)) !=  BluestoneSide.NONE)
				{
					return PipeConnectionType.END;
				}
			}
		}
		return PipeConnectionType.NONE;
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
}

