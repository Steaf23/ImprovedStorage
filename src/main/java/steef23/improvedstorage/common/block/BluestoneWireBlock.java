package steef23.improvedstorage.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import steef23.improvedstorage.common.tileentity.BluestoneWireTileEntity;
import steef23.improvedstorage.common.tileentity.BluestoneWireTileEntity.WireItem;
import steef23.improvedstorage.core.init.IMPSBlocks;
import steef23.improvedstorage.core.init.IMPSTileEntities;

public class BluestoneWireBlock extends Block 
{
	public static final EnumProperty<BluestoneSide> NORTH = EnumProperty.create("north", BluestoneSide.class);
	public static final EnumProperty<BluestoneSide> EAST = EnumProperty.create("east", BluestoneSide.class);
	public static final EnumProperty<BluestoneSide> SOUTH = EnumProperty.create("south", BluestoneSide.class);
	public static final EnumProperty<BluestoneSide> WEST = EnumProperty.create("west", BluestoneSide.class);
	//public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
	public static final Map<Direction, EnumProperty<BluestoneSide>> FACING_PROPERTY_MAP = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, 
		   																												  Direction.EAST, EAST, 
		   																												  Direction.SOUTH, SOUTH, 
		   																												  Direction.WEST, WEST));
	protected static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), 
		   														  Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), 
		   														  Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), 
		   														  Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), 
		   														  Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), 
		   														  Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), 
		   														  Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), 
		   														  Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), 
		   														  Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), 
		   														  Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), 
		   														  Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), 
		   														  Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), 
		   														  Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), 
		   														  Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), 
		   														  Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), 
		   														  Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};
    /** List of blocks to update with redstone. */
    private final Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();

    public BluestoneWireBlock(Block.Properties properties) 
    {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, BluestoneSide.NONE)
    		  												 .with(EAST, BluestoneSide.NONE)
    		  												 .with(SOUTH, BluestoneSide.NONE)
    		  												 .with(WEST, BluestoneSide.NONE));
    }
   
    /*Get the AABB shape for the bluewire instance*/
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) 
    {
    	return SHAPES[getAABBIndex(state)];
    }
   
    /*get the AABB for the bluewire instance*/
    private static int getAABBIndex(BlockState state) 
   	{
    	int i = 0;
    	boolean isConnectedToNorth = state.get(NORTH) != BluestoneSide.NONE;
    	boolean isConnectedToEast = state.get(EAST) != BluestoneSide.NONE;
    	boolean isConnectedToSouth = state.get(SOUTH) != BluestoneSide.NONE;
    	boolean isConnectedToWest = state.get(WEST) != BluestoneSide.NONE;
    	if (isConnectedToNorth || isConnectedToSouth && !isConnectedToNorth && !isConnectedToEast && !isConnectedToWest) 
    	{
    		i |= 1 << Direction.NORTH.getHorizontalIndex();
    	}

    	if (isConnectedToEast || isConnectedToWest && !isConnectedToNorth && !isConnectedToEast && !isConnectedToSouth) 
    	{
    		i |= 1 << Direction.EAST.getHorizontalIndex();
    	}

    	if (isConnectedToSouth || isConnectedToNorth && !isConnectedToEast && !isConnectedToSouth && !isConnectedToWest) 
    	{
    		i |= 1 << Direction.SOUTH.getHorizontalIndex();
    	}

    	if (isConnectedToWest || isConnectedToEast && !isConnectedToNorth && !isConnectedToSouth && !isConnectedToWest) 
    	{
    		i |= 1 << Direction.WEST.getHorizontalIndex();
    	}

    	return i;
   	}
    
    public BlockState getStateForPlacement(BlockItemUseContext context) 
   	{
    	IBlockReader iblockreader = context.getWorld();
    	BlockPos blockpos = context.getPos();
    	return this.getDefaultState().with(WEST, this.getSide(iblockreader, blockpos, Direction.WEST))
    		  					   	 .with(EAST, this.getSide(iblockreader, blockpos, Direction.EAST))
    		  					   	 .with(NORTH, this.getSide(iblockreader, blockpos, Direction.NORTH))
    		  					     .with(SOUTH, this.getSide(iblockreader, blockpos, Direction.SOUTH));
   	}

    /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) 
    {
    	BlockState newState;
    	if (facing == Direction.DOWN) 
    	{
    		newState = stateIn;
    	} else 
    	{
    		newState = facing == Direction.UP ? stateIn.with(WEST, this.getSide(worldIn, currentPos, Direction.WEST))
    											   .with(EAST, this.getSide(worldIn, currentPos, Direction.EAST))
    											   .with(NORTH, this.getSide(worldIn, currentPos, Direction.NORTH))
    											   .with(SOUTH, this.getSide(worldIn, currentPos, Direction.SOUTH)) 
    											   : stateIn.with(FACING_PROPERTY_MAP.get(facing), this.getSide(worldIn, currentPos, facing));
    	}
    	
    	TileEntity te = worldIn.getTileEntity(currentPos);
    	if (te instanceof BluestoneWireTileEntity)
    	{
    		((BluestoneWireTileEntity) te).updateConnectedPositions(newState);
    	}
    	return newState;
    }
    
    /**
     * performs updates on diagonal neighbors of the target position and passes in the flags. The flags can be referenced
     * from the docs for {@link IWorldWriter#setBlockState(IBlockState, BlockPos, int)}.
     * Also don't update Observers :)
     */
    public void updateDiagonalNeighbors(BlockState state, IWorld worldIn, BlockPos pos, int flags) 
    {
    	Mutable mutableBlockPos = new BlockPos.Mutable();
		for(Direction direction : Direction.Plane.HORIZONTAL) 
     	{
			BluestoneSide redstoneside = state.get(FACING_PROPERTY_MAP.get(direction));
			if (redstoneside != BluestoneSide.NONE && worldIn.getBlockState(mutableBlockPos.setPos(pos).move(direction)).getBlock() != this) 
			{
				mutableBlockPos.move(Direction.DOWN);
				BlockState blockstate = worldIn.getBlockState(mutableBlockPos);
				if (blockstate.getBlock() != Blocks.OBSERVER) 
				{
					BlockPos blockpos = mutableBlockPos.offset(direction.getOpposite());
					BlockState blockstate1 = blockstate.updatePostPlacement(direction.getOpposite(), worldIn.getBlockState(blockpos), worldIn, mutableBlockPos, blockpos);
					replaceBlock(blockstate, blockstate1, worldIn, mutableBlockPos, flags);
				}

				mutableBlockPos.setPos(pos).move(direction).move(Direction.UP);
				BlockState blockstate3 = worldIn.getBlockState(mutableBlockPos);
				if (blockstate3.getBlock() != Blocks.OBSERVER) 
				{
					BlockPos blockpos1 = mutableBlockPos.offset(direction.getOpposite());
					BlockState blockstate2 = blockstate3.updatePostPlacement(direction.getOpposite(), worldIn.getBlockState(blockpos1), worldIn, mutableBlockPos, blockpos1);
					replaceBlock(blockstate3, blockstate2, worldIn, mutableBlockPos, flags);
				}
			}
    	}
    }

    private BluestoneSide getSide(IBlockReader worldIn, BlockPos pos, Direction face) 
    {
    	BlockPos blockpos = pos.offset(face);
    	BlockState blockstate = worldIn.getBlockState(blockpos);
    	
    	boolean nonNormalCubeAbove = !worldIn.getBlockState(pos.up()).isNormalCube(worldIn, pos);
    	if (nonNormalCubeAbove) 
    	{
    		boolean flag = blockstate.isSolidSide(worldIn, blockpos, Direction.UP) || blockstate.isIn(Blocks.HOPPER);
    		if (flag && canConnectTo(worldIn.getBlockState(blockpos.up()), worldIn, blockpos.up(), null)) 
    		{
    			if (blockstate.isSolidSide(worldIn, blockpos, face.getOpposite())) 
    			{
    				return BluestoneSide.UP;
    			}

    			return BluestoneSide.SIDE;
    		}
    	}

    	return !canConnectTo(blockstate, worldIn, blockpos, face) && 
    			(blockstate.isNormalCube(worldIn, blockpos) || !canConnectTo(worldIn.getBlockState(blockpos.down()), worldIn, blockpos.down(), null)) 
    			? BluestoneSide.NONE : BluestoneSide.SIDE;
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) 
    {
    	BlockPos blockpos = pos.down();
    	BlockState blockstate = worldIn.getBlockState(blockpos);
    	return blockstate.isSolidSide(worldIn, blockpos, Direction.UP) || blockstate.getBlock() == Blocks.HOPPER;
    }

    private BlockState updateSurroundingRedstone(World worldIn, BlockPos pos, BlockState state) 
    {
    	//state = this.func_212568_b(worldIn, pos, state);
    	List<BlockPos> list = Lists.newArrayList(this.blocksNeedingUpdate);
    	this.blocksNeedingUpdate.clear();

    	for(BlockPos blockpos : list) 
    	{
    		worldIn.notifyNeighborsOfStateChange(blockpos, this);
    	}
    	return state;
    }

//   private BlockState setPowerLevel(World worldIn, BlockPos pos, BlockState state) {
//      BlockState blockstate = state;
//      int power = state.get(POWER);
//      this.canProvidePower = false;
//      int neighborpower = worldIn.getRedstonePowerFromNeighbors(pos);
//      this.canProvidePower = true;
//      int k = 0;
//      if (neighborpower < 15) {
//         for(Direction direction : Direction.Plane.HORIZONTAL) {
//            BlockPos blockpos = pos.offset(direction);
//            BlockState neighborblockstate = worldIn.getBlockState(blockpos);
//            k = this.maxSignal(k, neighborblockstate);
//            BlockPos blockpos_up = pos.up();
//            if (neighborblockstate.isNormalCube(worldIn, blockpos) && !worldIn.getBlockState(blockpos_up).isNormalCube(worldIn, blockpos_up)) {
//               k = this.maxSignal(k, worldIn.getBlockState(blockpos.up()));
//            } else if (!neighborblockstate.isNormalCube(worldIn, blockpos)) {
//               k = this.maxSignal(k, worldIn.getBlockState(blockpos.down()));
//            }
//         }
//      }
//
//      int l = k - 1;
//      if (neighborpower > l) {
//         l = neighborpower;
//      }
//
//      if (power != l) {
//         state = state.with(POWER, Integer.valueOf(l));
//         if (worldIn.getBlockState(pos) == blockstate) {
//            worldIn.setBlockState(pos, state, 2);
//         }
//
//         this.blocksNeedingUpdate.add(pos);
//
//         for(Direction direction1 : Direction.values()) {
//            this.blocksNeedingUpdate.add(pos.offset(direction1));
//         }
//      }
//
//      return state;
//   }

    /**
     * Calls World.notifyNeighborsOfStateChange() for all neighboring blocks, but only if the given block is a redstone
     * wire.
     */
    private void notifyWireNeighborsOfStateChange(World worldIn, BlockPos pos)  	
    {
    	if (worldIn.getBlockState(pos).getBlock() == this) 
    	{
    		worldIn.notifyNeighborsOfStateChange(pos, this);

    		for(Direction direction : Direction.values()) 
    		{
    			worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
    		}
    	}
    }

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) 
   	{
    	if (oldState.getBlock() != state.getBlock() && !worldIn.isRemote) 
    	{
    		this.updateSurroundingRedstone(worldIn, pos, state);

    		for(Direction direction : Direction.Plane.VERTICAL) 
    		{
    			worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
    		}

    		for(Direction direction1 : Direction.Plane.HORIZONTAL) 
    		{
    			this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(direction1));
    		}

    		for(Direction direction2 : Direction.Plane.HORIZONTAL) 
    		{
    			BlockPos blockpos = pos.offset(direction2);
    			if (worldIn.getBlockState(blockpos).isNormalCube(worldIn, blockpos)) 
    			{
    				this.notifyWireNeighborsOfStateChange(worldIn, blockpos.up());
    			} else {
    				this.notifyWireNeighborsOfStateChange(worldIn, blockpos.down());
    			}
    		}
    	}
   	}

//   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
//      if (!isMoving && state.getBlock() != newState.getBlock()) {
//         if (!worldIn.isRemote) {
//            for(Direction direction : Direction.values()) {
//               worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
//            }
//
//            for(Direction direction1 : Direction.Plane.HORIZONTAL) {
//               this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(direction1));
//            }
//
//            for(Direction direction2 : Direction.Plane.HORIZONTAL) {
//               BlockPos blockpos = pos.offset(direction2);
//               if (worldIn.getBlockState(blockpos).isNormalCube(worldIn, blockpos)) {
//                  this.notifyWireNeighborsOfStateChange(worldIn, blockpos.up());
//               } else {
//                  this.notifyWireNeighborsOfStateChange(worldIn, blockpos.down());
//               }
//            }
//         }
//      }
//   }

//    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) 
//    {
//    	if (!worldIn.isRemote) {
//    		if (state.isValidPosition(worldIn, pos)) 
//    		{
//    			this.updateSurroundingRedstone(worldIn, pos, state);
//    		} else {
//    			spawnDrops(state, worldIn, pos);
//            worldIn.removeBlock(pos, false);
//    		}
//
//    	}
//    }

   	protected static boolean canConnectTo(BlockState blockState, IBlockReader world, BlockPos pos, @Nullable Direction side) 
   	{
	   	Block block = blockState.getBlock();
	   	if (block == IMPSBlocks.BLUESTONE_WIRE.get()) 
	   	{
	   		return true;
	   	}
	   	else if (blockState.getBlock() == IMPSBlocks.BLUESTONE_TABLE.get() || world.getTileEntity(pos) instanceof LockableTileEntity)
	   	{
	   		return side != Direction.UP && side != Direction.DOWN && side != null;
	   	} else 
	   	{
	   		return false;
	   	}
   	}

   	@OnlyIn(Dist.CLIENT)
   	public static int colorMultiplier(int p_176337_0_) 
   	{
   		float f = (float)p_176337_0_ / 15.0F;
   		float f1 = f * 0.6F + 0.4F;
   		if (p_176337_0_ == 0) 
   		{
   			f1 = 0.3F;
   		}
   		
   		float f2 = f * f * 0.7F - 0.5F;
   		float f3 = f * f * 0.6F - 0.7F;
   		if (f2 < 0.0F) 
   		{
   			f2 = 0.0F;
   		}
   		
   		if (f3 < 0.0F) {
   			f3 = 0.0F;
   		}

   		int i = MathHelper.clamp((int)(f1 * 255.0F), 0, 255);
   		int j = MathHelper.clamp((int)(f2 * 255.0F), 0, 255);
   		int k = MathHelper.clamp((int)(f3 * 255.0F), 0, 255);
   		return -16777216 | i << 16 | j << 8 | k;
   	}

   	/**
   	 * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
   	 * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
   	 * of whether the block can receive random update ticks
   	 */
//   @OnlyIn(Dist.CLIENT)
//   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
//      int i = stateIn.get(POWER);
//      if (i != 0) {
//         double d0 = (double)pos.getX() + 0.5D + ((double)rand.nextFloat() - 0.5D) * 0.2D;
//         double d1 = (double)((float)pos.getY() + 0.0625F);
//         double d2 = (double)pos.getZ() + 0.5D + ((double)rand.nextFloat() - 0.5D) * 0.2D;
//         float f = (float)i / 15.0F;
//         float f1 = f * 0.6F + 0.4F;
//         float f2 = Math.max(0.0F, f * f * 0.7F - 0.5F);
//         float f3 = Math.max(0.0F, f * f * 0.6F - 0.7F);
//         worldIn.addParticle(new RedstoneParticleData(f1, f2, f3, 1.0F), d0, d1, d2, 0.0D, 0.0D, 0.0D);
//      }
//   }

   	/**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   	public BlockState rotate(BlockState state, Rotation rot) 
   	{
   		switch(rot) 
   		{
   		case CLOCKWISE_180:
   			return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
   		case COUNTERCLOCKWISE_90:
   			return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
   		case CLOCKWISE_90:
   			return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
   		default:
   			return state;
   		}
   	}

   	/**
   	 * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
   	 * blockstate.
   	 * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
   	 */
   	public BlockState mirror(BlockState state, Mirror mirrorIn) 
   	{
   		switch(mirrorIn) 
      	{
      	case LEFT_RIGHT:
    	  return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
      	case FRONT_BACK:
    	  return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
      	default:
      		return super.mirror(state, mirrorIn);
      	}
   	}

   	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) 
   	{
   		builder.add(NORTH, EAST, SOUTH, WEST);
   	}
   	
   	@Override
   	public boolean hasTileEntity(BlockState state) 
   	{
   		return true;
   	}
   	
   	@Override
   	public TileEntity createTileEntity(BlockState state, IBlockReader world) 
   	{
   		return IMPSTileEntities.BLUESTONE_WIRE.get().create();
   	}
   	
   	@Override
   	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
   			Hand handIn, BlockRayTraceResult hit) 
   	{
   		if (!worldIn.isRemote)
   		{
   			if (player.getHeldItem(handIn) == ItemStack.EMPTY)
   	   		{
   	   			TileEntity te = worldIn.getTileEntity(pos);
   	   			if (te instanceof BluestoneWireTileEntity)
   	   			{
   	   				ArrayList<WireItem> items = ((BluestoneWireTileEntity) te).items;
   	   				if (!items.isEmpty())
   	   				{
   		   				((BluestoneWireTileEntity) te).items.forEach((wire) -> {
   		   					System.out.println(wire.getItemStack().getItem() + 
   		   							", Source: " + wire.getSource() + 
   		   							", Target: " + wire.getTarget() + "");
   		   				});
   	   				}
   	   				else
   	   				{
   	   					System.out.println("No Items Found!?");
   	   				}
   	   			}
   	   		}
   		}
   		return ActionResultType.SUCCESS;
   	}
}