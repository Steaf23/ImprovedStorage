package steef23.improvedstorage.common.block;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

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
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.World;
import steef23.improvedstorage.common.tileentity.AbstractItemPipeTileEntity;
import steef23.improvedstorage.common.tileentity.BluestoneWireTileEntity;
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
	private static final VoxelShape BASE_SHAPE = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D);
	private static final Map<Direction, VoxelShape> SIDE_TO_SHAPE = Maps.newEnumMap(ImmutableMap.of(
			Direction.NORTH, Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), 
			Direction.SOUTH, Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), 
			Direction.EAST, Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), 
			Direction.WEST, Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D)));
	
	private static final Map<Direction, VoxelShape> SIDE_TO_ASCENDING_SHAPE = Maps.newEnumMap(ImmutableMap.of(
			Direction.NORTH, VoxelShapes.or(SIDE_TO_SHAPE.get(Direction.NORTH), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 16.0D, 1.0D)), 
			Direction.SOUTH, VoxelShapes.or(SIDE_TO_SHAPE.get(Direction.SOUTH), Block.makeCuboidShape(3.0D, 0.0D, 15.0D, 13.0D, 16.0D, 16.0D)), 
			Direction.EAST, VoxelShapes.or(SIDE_TO_SHAPE.get(Direction.EAST), Block.makeCuboidShape(15.0D, 0.0D, 3.0D, 16.0D, 16.0D, 13.0D)), 
			Direction.WEST, VoxelShapes.or(SIDE_TO_SHAPE.get(Direction.WEST), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 1.0D, 16.0D, 13.0D))));
	private final Map<BlockState, VoxelShape> stateToShapeMap = Maps.newHashMap();
	
	private final BlockState sideBaseState;

    public BluestoneWireBlock(Block.Properties properties) 
    {
    	super(properties);
    	this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, BluestoneSide.NONE)
    		  												   .with(EAST, BluestoneSide.NONE)
    		  												   .with(SOUTH, BluestoneSide.NONE)
    		  												   .with(WEST, BluestoneSide.NONE));
    	this.sideBaseState = this.getDefaultState().with(NORTH, BluestoneSide.SIDE)
    		  									   .with(EAST, BluestoneSide.SIDE)
    		  									   .with(SOUTH, BluestoneSide.SIDE)
    		  									   .with(WEST, BluestoneSide.SIDE);
    	this.getStateContainer().getValidStates().forEach((state) -> { 
    		this.stateToShapeMap.put(state, this.getShapeForState(state));
    	});
    }
   
    private VoxelShape getShapeForState(BlockState state)
    {
    	VoxelShape voxelShape = BASE_SHAPE;
    	for(Direction direction : Direction.Plane.HORIZONTAL) 
    	{
    		BluestoneSide bluestoneSide = state.get(FACING_PROPERTY_MAP.get(direction));
            if (bluestoneSide == BluestoneSide.SIDE)
            {
            	voxelShape = VoxelShapes.or(voxelShape, SIDE_TO_SHAPE.get(direction));
            } else if (bluestoneSide == BluestoneSide.UP)
            {
            	voxelShape = VoxelShapes.or(voxelShape, SIDE_TO_ASCENDING_SHAPE.get(direction));
            }
         }

         return voxelShape;
    }
    
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) 
    {
    	return this.stateToShapeMap.get(state);
    }
    
    public BlockState getStateForPlacement(BlockItemUseContext context) 
   	{
    	return this.getUpdatedState(context.getWorld(), this.sideBaseState, context.getPos());
   	}
    
    private BlockState getUpdatedState(IBlockReader reader, BlockState state, BlockPos pos) 
   	{
    	boolean areAllSidesInvalid = areAllSidesInvalid(state);
    	state = this.recalculateFacingState(reader, this.getDefaultState(), pos);
    	if (areAllSidesInvalid && areAllSidesInvalid(state))
    	{
    		return state;
    	}
    	else
    	{
			boolean isConnectedToNorth = state.get(NORTH).isValid();
			boolean isConnectedToEast = state.get(EAST).isValid();
			boolean isConnectedToSouth = state.get(SOUTH).isValid();
			boolean isConnectedToWest = state.get(WEST).isValid();
			if (isConnectedToNorth) 
			{
				state = state.with(NORTH, BluestoneSide.SIDE);
			}
			else if (isConnectedToSouth && !isConnectedToNorth && !isConnectedToEast && !isConnectedToWest)
			{
				state = state.with(NORTH, BluestoneSide.END);
			}
		
			if (isConnectedToEast) 
			{
				state = state.with(EAST, BluestoneSide.SIDE);
			}
			else if (isConnectedToWest && !isConnectedToNorth && !isConnectedToEast && !isConnectedToSouth) 
			{
				state = state.with(EAST, BluestoneSide.END);
			}
		
			if (isConnectedToSouth) 
			{
				state = state.with(SOUTH, BluestoneSide.SIDE);
			}
			else if (isConnectedToNorth && !isConnectedToEast && !isConnectedToSouth && !isConnectedToWest)
			{
				state = state.with(SOUTH, BluestoneSide.END);
			}
		
			if (isConnectedToWest) 
			{
				state = state.with(WEST, BluestoneSide.SIDE);
			}
			else if (isConnectedToEast && !isConnectedToNorth && !isConnectedToSouth && !isConnectedToWest)
			{
				state = state.with(WEST, BluestoneSide.END);
			}
		
			return state;
    	}
    	
   	}
    
    private BlockState recalculateFacingState(IBlockReader reader, BlockState state, BlockPos pos) 
    {
    	boolean nonNormalCubeAbove = !reader.getBlockState(pos.up()).isNormalCube(reader, pos);

        for(Direction direction : Direction.Plane.HORIZONTAL) 
        {
        	if (!state.get(FACING_PROPERTY_MAP.get(direction)).isValid()) 
        	{
        		BluestoneSide bluestoneSide = this.recalculateSide(reader, pos, direction, nonNormalCubeAbove);
        		state = state.with(FACING_PROPERTY_MAP.get(direction), bluestoneSide);
        	}
        }
        return state;
    }
    
    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) 
    {
    	if (facing == Direction.DOWN) 
     	{
    		return stateIn;
     	} 
    	else if (facing == Direction.UP)
     	{
    		return this.getUpdatedState(worldIn, stateIn, currentPos);
     	}
    	else
    	{
    		BluestoneSide bluestoneSide = this.getSide(worldIn, currentPos, facing);
    		return bluestoneSide.isValid() == stateIn.get(FACING_PROPERTY_MAP.get(facing)).isValid() && 
    				!areAllSidesValid(stateIn) ? stateIn.with(FACING_PROPERTY_MAP.get(facing), bluestoneSide) 
    						: this.getUpdatedState(worldIn, this.sideBaseState.with(FACING_PROPERTY_MAP.get(facing), bluestoneSide), currentPos);
    	}
    }
    
    private static boolean areAllSidesValid(BlockState state)
    {
    	return state.get(NORTH).isValid() && 
    			state.get(SOUTH).isValid() && 
    			state.get(EAST).isValid() && 
    			state.get(WEST).isValid();
    }
    
    private static boolean areAllSidesInvalid(BlockState state)
    {
    	return !state.get(NORTH).isValid() && 
    			!state.get(SOUTH).isValid() && 
    			!state.get(EAST).isValid() && 
    			!state.get(WEST).isValid();
    }
    
    public static ArrayList<Direction> getvalidSides(BlockState state)
    {
    	ArrayList<Direction> sides = new ArrayList<>();
    	FACING_PROPERTY_MAP.forEach((direction,bluestoneSide) ->
    	{
    		if (state.get(bluestoneSide).isValid())
    		{
        		sides.add(direction);
    		}
    	});
    	return sides;
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
			BluestoneSide bluestoneSide = state.get(FACING_PROPERTY_MAP.get(direction));
			if (bluestoneSide != BluestoneSide.NONE && worldIn.getBlockState(mutableBlockPos.setPos(pos).move(direction)).getBlock() != this) 
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
    
    public BluestoneSide getSide(IBlockReader worldIn, BlockPos pos, Direction face) 
    {
    	return this.recalculateSide(worldIn, pos, face, !worldIn.getBlockState(pos.up()).isNormalCube(worldIn, pos));
    }
    
    private BluestoneSide recalculateSide(IBlockReader reader, BlockPos pos, Direction direction, boolean nonNormalCubeAbove) 
    {
    	BlockPos blockpos = pos.offset(direction);
        BlockState blockstate = reader.getBlockState(blockpos);
        if (nonNormalCubeAbove) 
        {
        	boolean flag = this.canPlaceOnTopOf(reader, blockpos, blockstate);
        	if (flag && canConnectTo(reader.getBlockState(blockpos.up()), reader, blockpos.up(), null) ) 
        	{
        		if (blockstate.isSolidSide(reader, blockpos, direction.getOpposite())) 
        		{
        			return BluestoneSide.UP;
        		}

        		return BluestoneSide.SIDE;
        	}
        }
        
        BlockState wireState = reader.getBlockState(pos);
        return !canConnectTo(blockstate, reader, blockpos, direction) && (blockstate.isNormalCube(reader, blockpos) || 
        		!canConnectTo(reader.getBlockState(blockpos.down()), reader, blockpos.down(), null)) ? 
        				this.getEndConnection(direction, wireState) ? BluestoneSide.END : BluestoneSide.NONE : BluestoneSide.SIDE;
    }
    
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) 
    {
    	BlockPos blockpos = pos.down();
    	BlockState blockstate = worldIn.getBlockState(blockpos);
    	return this.canPlaceOnTopOf(worldIn, blockpos, blockstate);
    }
    
    //TODO CWASHY WASHY
    public boolean getEndConnection(Direction face, BlockState state)
    {
    	try 
    	{
			boolean isConnectedToThis = state.get(FACING_PROPERTY_MAP.get(face)).isValid();
			boolean isConnectedToOpposite = state.get(FACING_PROPERTY_MAP.get(face.getOpposite())).isValid();
			boolean isConnectedToLeft = state.get(FACING_PROPERTY_MAP.get(face.rotateY())).isValid();
			boolean isConnectedToRight = state.get(FACING_PROPERTY_MAP.get(face.rotateYCCW())).isValid();
		
			if (isConnectedToThis && !isConnectedToOpposite && !isConnectedToLeft && !isConnectedToRight)
			{
				return true;
			}
			return false;
    	}
    	catch(IllegalArgumentException e)
    	{
    		return false;
    	}
    }
    
    private boolean canPlaceOnTopOf(IBlockReader reader, BlockPos pos, BlockState state) 
    {
        return state.isSolidSide(reader, pos, Direction.UP) || state.isIn(Blocks.HOPPER);
    }

    /**
     * Calls World.notifyNeighborsOfStateChange() for all neighboring blocks, but only if the given block is a bluestone
     * wire.
     */
    private void notifyWireNeighborsOfStateChange(World worldIn, BlockPos pos)  	
    {
    	if (worldIn.getBlockState(pos).isIn(this)) 
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
    	if (!oldState.isIn(state.getBlock()) && !worldIn.isRemote) 
    	{
    		for(Direction direction : Direction.Plane.VERTICAL) 
    		{
    			worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
    		}
    	}
   	}
    
    @SuppressWarnings("deprecation")
	@Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    	if (!isMoving && !state.isIn(newState.getBlock())) 
    	{
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof AbstractItemPipeTileEntity) 
            {
            	((AbstractItemPipeTileEntity)te).dropInventory();
            }
            
    		super.onReplaced(state, worldIn, pos, newState, isMoving);
    		if (!worldIn.isRemote) 
    		{
    			for(Direction direction : Direction.values()) 
    			{
    				worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
    			}

    			this.updateNeighborsStateChange(worldIn, pos);
    		}
    	}
    }
    
    private void updateNeighborsStateChange(World worldIn, BlockPos pos)
    {
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

    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) 
    {
    	if (!worldIn.isRemote) 
    	{
    		if (!state.isValidPosition(worldIn, pos))
    		{
    			spawnDrops(state, worldIn, pos);
            	worldIn.removeBlock(pos, false);
    		}
    	}
    }

   	public static boolean canConnectTo(BlockState blockState, IBlockReader world, BlockPos pos, @Nullable Direction side) 
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
   	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
   			Hand handIn, BlockRayTraceResult hit) 
   	{
   		if (!worldIn.isRemote)
   		{
   			TileEntity te = worldIn.getTileEntity(pos);
	   		if (te instanceof BluestoneWireTileEntity)
	   		{
	   			BluestoneWireTileEntity wireTE = (BluestoneWireTileEntity)te;
	   			if (player.getHeldItem(handIn) == ItemStack.EMPTY)
	   			{
	   				if (player.isSneaking())
	   				{
	   					wireTE.setRenderDebug(!wireTE.getRenderDebug());
	   					System.out.format("Render debug enabled: %b\n", wireTE.getRenderDebug());
	   				}
	   				else
	   				{
	   					wireTE.dropItem(null, null, null);
	   					return ActionResultType.SUCCESS;
	   				}
   	   			}
	   			else
	   			{
	   				wireTE.receiveItemStack(player.getHeldItem(handIn), null);
	   				player.setHeldItem(handIn, ItemStack.EMPTY);
	   				return ActionResultType.SUCCESS;
	   			}
	   		}
   			
   		}
   		
   		if (!player.abilities.allowEdit)
   		{
   			return ActionResultType.SUCCESS;
   		}
   		else
   		{
   			if (areAllSidesValid(state) || areAllSidesInvalid(state))
   	   		{
   	   			BlockState blockstate = areAllSidesValid(state) ? this.getDefaultState() : this.sideBaseState;
   	            if (blockstate != state) {
   	               worldIn.setBlockState(pos, blockstate, 3);
   	               this.updateChangedConnections(worldIn, pos, state, blockstate);
   	               return ActionResultType.SUCCESS;
   	            }
   	   		}
   	   		
   	   		return ActionResultType.SUCCESS;
   		}
   	}
   	
   	private void updateChangedConnections(World world, BlockPos pos, BlockState prevState, BlockState newState) {
        for(Direction direction : Direction.Plane.HORIZONTAL) 
        {
           BlockPos blockpos = pos.offset(direction);
           if (prevState.get(FACING_PROPERTY_MAP.get(direction)).isValid() != newState.get(FACING_PROPERTY_MAP.get(direction)).isValid() && 
        		   world.getBlockState(blockpos).isNormalCube(world, blockpos)) {
              world.notifyNeighborsOfStateExcept(blockpos, newState.getBlock(), direction.getOpposite());
           }
        }
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
}