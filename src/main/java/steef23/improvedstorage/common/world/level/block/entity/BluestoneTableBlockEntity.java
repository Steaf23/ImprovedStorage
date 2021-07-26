package steef23.improvedstorage.common.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import steef23.improvedstorage.core.init.IMPSBlockEntities;

import java.util.ArrayList;

public class BluestoneTableBlockEntity extends BlockEntity
{

	private ArrayList<BlockEntity> connectedTileEntities = new ArrayList<BlockEntity>();
	private final int range = 4;

	public BluestoneTableBlockEntity(BlockEntityType<?> typeIn, BlockPos blockPos, BlockState blockState)
	{
		super(typeIn, blockPos, blockState);
	}

	public BluestoneTableBlockEntity(BlockPos blockPos, BlockState blockState)
	{
		this(IMPSBlockEntities.BLUESTONE_TABLE.get(), blockPos, blockState);
	}

	public void updateConnectedTileEntities()
	{
//		this.resetConnectedTileEntitiesList();
//		AABB range = new AABB(this.getBlockPos()).expandTowards(this.range, this.range, this.range);
//
//		int chunkMinX = MathHelper.floor(range.minX / 16.0D);
//		int chunkMaxX = MathHelper.floor(range.maxX / 16.0D);
//		int chunkMinZ = MathHelper.floor(range.minZ / 16.0D);
//		int chunkMaxZ = MathHelper.floor(range.maxZ / 16.0D);
//		System.out.println(chunkMinX + ", " + chunkMaxX + ", " + chunkMinZ + ", " + chunkMaxZ);
//		for(int x = chunkMinX; x <= chunkMaxX; x++)
//		{
//			for(int z = chunkMinZ; z <= chunkMaxZ; z++)
//			{
//				LevelChunk chunk = this.level.getChunk(x, z, false);
//				if (chunk != null)
//				{
//					System.out.println(chunk.getPos().x + ", " + chunk.getPos().z);
//					Map<BlockPos, BlockEntity> allTileEntities = chunk.getBlockEntities();
//					for (Map.Entry<BlockPos, BlockEntity> entry : allTileEntities.entrySet())
//					{
//						if (entry.getValue() instanceof LockableTileEntity)
//						{
//							int tableX = this.getBlockPos().getX();
//							int tableY = this.getBlockPos().getY();
//							int tableZ = this.getBlockPos().getZ();
//							int entryX = entry.getKey().getX();
//							int entryY = entry.getKey().getY();
//							int entryZ = entry.getKey().getZ();
//
//							if (Math.max(tableX, entryX) - Math.min(tableX, entryX) <= this.range &&
//								Math.max(tableY, entryY) - Math.min(tableY, entryY) <= this.range &&
//								Math.max(tableZ, entryZ) - Math.min(tableZ, entryZ) <= this.range)
//							{
//								this.connectedTileEntities.add(entry.getValue());
//							}
//						}
//					}
//				}
//			}
//		}
	}
	
	public void resetConnectedTileEntitiesList()
	{
		this.connectedTileEntities = new ArrayList<BlockEntity>();
	}
	
	public ArrayList<BlockEntity> getConnectedTileEntities()
	{
		return this.connectedTileEntities;
	}

}
