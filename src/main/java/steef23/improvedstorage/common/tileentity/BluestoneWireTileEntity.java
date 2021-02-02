package steef23.improvedstorage.common.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
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
				if (this.world.getTileEntity(this.pos.offset(face.getOpposite())) instanceof BluestoneWireTileEntity)
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
}

