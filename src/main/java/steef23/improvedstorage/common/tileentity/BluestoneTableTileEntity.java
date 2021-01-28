package steef23.improvedstorage.common.tileentity;

import java.util.ArrayList;
import java.util.Map;

import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import steef23.improvedstorage.core.init.IMPSTileEntities;

public class BluestoneTableTileEntity extends TileEntity
{

	private ArrayList<TileEntity> connectedTileEntities = new ArrayList<TileEntity>();
	private final int range = 4;

	public BluestoneTableTileEntity(TileEntityType<?> typeIn) 
	{
		super(typeIn);
	}
	
	public BluestoneTableTileEntity() 
	{
		this(IMPSTileEntities.BLUESTONE_TABLE.get());
	}
	
	public void updateConnectedTileEntities()
	{
		this.resetConnectedTileEntitiesList();
		AxisAlignedBB range = new AxisAlignedBB(this.getPos()).grow(this.range, this.range, this.range);
		
		int chunkMinX = MathHelper.floor(range.minX / 16.0D);
		int chunkMaxX = MathHelper.floor(range.maxX / 16.0D);
		int chunkMinZ = MathHelper.floor(range.minZ / 16.0D);
		int chunkMaxZ = MathHelper.floor(range.maxZ / 16.0D);
		System.out.println(chunkMinX + ", " + chunkMaxX + ", " + chunkMinZ + ", " + chunkMaxZ);
		for(int x = chunkMinX; x <= chunkMaxX; x++)
		{
			for(int z = chunkMinZ; z <= chunkMaxZ; z++)
			{
				Chunk chunk = this.world.getChunkProvider().getChunk(x, z, false);
				if (chunk != null)
				{
					System.out.println(chunk.getPos().x + ", " + chunk.getPos().z);
					Map<BlockPos, TileEntity> allTileEntities = chunk.getTileEntityMap();
					for (Map.Entry<BlockPos, TileEntity> entry : allTileEntities.entrySet())
					{
						if (entry.getValue() instanceof LockableTileEntity)
						{
							int tableX = this.getPos().getX();
							int tableY = this.getPos().getY();
							int tableZ = this.getPos().getZ();
							int entryX = entry.getKey().getX();
							int entryY = entry.getKey().getY();
							int entryZ = entry.getKey().getZ();
							
							if (Math.max(tableX, entryX) - Math.min(tableX, entryX) <= this.range && 
								Math.max(tableY, entryY) - Math.min(tableY, entryY) <= this.range && 
								Math.max(tableZ, entryZ) - Math.min(tableZ, entryZ) <= this.range)
							{
								this.connectedTileEntities.add(entry.getValue());
							}
						}
					}
				}
			}
		}
	}
	
	public void resetConnectedTileEntitiesList()
	{
		this.connectedTileEntities = new ArrayList<TileEntity>();
	}
	
	public ArrayList<TileEntity> getConnectedTileEntities()
	{
		return this.connectedTileEntities;
	}

}
