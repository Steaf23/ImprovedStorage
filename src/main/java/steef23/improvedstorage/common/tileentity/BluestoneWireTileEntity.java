package steef23.improvedstorage.common.tileentity;

import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import steef23.improvedstorage.common.block.BluestoneSide;
import steef23.improvedstorage.common.block.BluestoneWireBlock;
import steef23.improvedstorage.core.init.IMPSTileEntities;

//TODO ADD METHOD FOR PLAYER INSERTION USING HAND
//TODO ADD METHOD TO SPIT OUT ITEMS AT THE END IF NO INVENTORY

public class BluestoneWireTileEntity extends AbstractItemPipeTileEntity
{
	protected BluestoneWireTileEntity(TileEntityType<?> typeIn) 
	{
		super(typeIn, 20);
	}
	
	public BluestoneWireTileEntity()
	{
		this(IMPSTileEntities.BLUESTONE_WIRE.get());
	}
	
	@Override
	protected boolean shouldFaceConnect(Direction face)
	{
		EnumProperty<BluestoneSide> property = BluestoneWireBlock.FACING_PROPERTY_MAP.get(face);
		if (property == null)
		{
			return false;
		}
		else
		{
			BluestoneSide side = this.getBlockState().get(property);
			
			return (side != BluestoneSide.NONE);
		}
	}
}

