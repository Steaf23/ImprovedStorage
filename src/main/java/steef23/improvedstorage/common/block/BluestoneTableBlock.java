package steef23.improvedstorage.common.block;

import java.util.ArrayList;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import steef23.improvedstorage.common.tileentity.BluestoneTableTileEntity;
import steef23.improvedstorage.core.init.IMPSTileEntities;

public class BluestoneTableBlock extends Block
{

	@SuppressWarnings("unused")
	private final Supplier<TileEntityType<? extends BluestoneTableTileEntity>> tileEntityTypeSupplier;
	
	public BluestoneTableBlock(Supplier<TileEntityType<? extends BluestoneTableTileEntity>> tileEntityTypeSupplierIn, Properties propertiesIn) 
	{
		super(propertiesIn);
		this.tileEntityTypeSupplier = tileEntityTypeSupplierIn;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) 
	{
		if (!worldIn.isRemote)
		{
			TileEntity te = worldIn.getTileEntity(pos);
			if (te instanceof BluestoneTableTileEntity)
			{
				BluestoneTableTileEntity table = (BluestoneTableTileEntity)te;
				table.updateConnectedTileEntities();
				ArrayList<TileEntity> connectedTileEntities = table.getConnectedTileEntities();
				for(TileEntity connectedTE : connectedTileEntities)
				{
					System.out.println(connectedTE.getBlockState().getBlock().getTranslatedName().getString());
				}
			}
			System.out.println("LOL");
		}
		
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) 
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) 
	{
		return IMPSTileEntities.BLUESTONE_TABLE.get().create();
	}

}
