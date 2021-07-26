package steef23.improvedstorage.common.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import steef23.improvedstorage.common.world.level.block.entity.AbstractItemPipeBlockEntity;
import steef23.improvedstorage.common.world.level.block.entity.BluestoneWireBlockEntity;
import steef23.improvedstorage.core.init.IMPSBlockEntities;
import steef23.improvedstorage.core.init.IMPSBlocks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;

public class BluestoneWireBlock extends Block implements EntityBlock
{

	public static final EnumProperty<BluestoneSide> NORTH = EnumProperty.create("north", BluestoneSide.class);
	public static final EnumProperty<BluestoneSide> EAST = EnumProperty.create("east", BluestoneSide.class);
	public static final EnumProperty<BluestoneSide> SOUTH = EnumProperty.create("south", BluestoneSide.class);
	public static final EnumProperty<BluestoneSide> WEST = EnumProperty.create("west", BluestoneSide.class);
	//public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
	public static final Map<Direction, EnumProperty<BluestoneSide>> PROPERTY_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH,
		   																												  Direction.EAST, EAST,
		   																												  Direction.SOUTH, SOUTH,
		   																												  Direction.WEST, WEST));
	private static final VoxelShape BASE_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D);
	private static final Map<Direction, VoxelShape> SHAPES_FLOOR = Maps.newEnumMap(ImmutableMap.of(
			Direction.NORTH, Block.box(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D),
			Direction.SOUTH, Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D),
			Direction.EAST, Block.box(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D),
			Direction.WEST, Block.box(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D)));

	private static final Map<Direction, VoxelShape> SHAPES_UP = Maps.newEnumMap(ImmutableMap.of(
			Direction.NORTH, Shapes.or(SHAPES_FLOOR.get(Direction.NORTH), Block.box(3.0D, 0.0D, 0.0D, 13.0D, 16.0D, 1.0D)),
			Direction.SOUTH, Shapes.or(SHAPES_FLOOR.get(Direction.SOUTH), Block.box(3.0D, 0.0D, 15.0D, 13.0D, 16.0D, 16.0D)),
			Direction.EAST, Shapes.or(SHAPES_FLOOR.get(Direction.EAST), Block.box(15.0D, 0.0D, 3.0D, 16.0D, 16.0D, 13.0D)),
			Direction.WEST, Shapes.or(SHAPES_FLOOR.get(Direction.WEST), Block.box(0.0D, 0.0D, 3.0D, 1.0D, 16.0D, 13.0D))));
	private final Map<BlockState, VoxelShape> SHAPES_CACHE = Maps.newHashMap();

	private final BlockState crossState;

    public BluestoneWireBlock(Block.Properties properties)
    {
    	super(properties);
    	this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, BluestoneSide.NONE)
    		  												   .setValue(EAST, BluestoneSide.NONE)
    		  												   .setValue(SOUTH, BluestoneSide.NONE)
    		  												   .setValue(WEST, BluestoneSide.NONE));
    	this.crossState = this.defaultBlockState().setValue(NORTH, BluestoneSide.SIDE)
    		  									   .setValue(EAST, BluestoneSide.SIDE)
    		  									   .setValue(SOUTH, BluestoneSide.SIDE)
    		  									   .setValue(WEST, BluestoneSide.SIDE);
    	this.getStateDefinition().getPossibleStates().forEach((state) -> {
    		this.SHAPES_CACHE.put(state, this.getShapeForState(state));
    	});
    }

    private VoxelShape getShapeForState(BlockState state)
    {
    	VoxelShape voxelShape = BASE_SHAPE;
    	for(Direction direction : Direction.Plane.HORIZONTAL)
    	{
    		BluestoneSide bluestoneSide = state.getValue(PROPERTY_BY_DIRECTION.get(direction));
            if (bluestoneSide == BluestoneSide.SIDE)
            {
            	voxelShape = Shapes.or(voxelShape, SHAPES_FLOOR.get(direction));
            } else if (bluestoneSide == BluestoneSide.UP)
            {
            	voxelShape = Shapes.or(voxelShape, SHAPES_UP.get(direction));
            }
         }

         return voxelShape;
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
    	return this.SHAPES_CACHE.get(state);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context)
   	{
    	return this.getUpdatedState(context.getLevel(), this.crossState, context.getClickedPos());
   	}

    private BlockState getUpdatedState(BlockGetter reader, BlockState state, BlockPos pos)
   	{
    	boolean areAllSidesInvalid = areAllSidesInvalid(state);
    	state = this.getMissingConnections(reader, this.defaultBlockState(), pos);
		if (!areAllSidesInvalid || !areAllSidesInvalid(state))
		{
			boolean isConnectedToNorth = state.getValue(NORTH).isConnected();
			boolean isConnectedToEast = state.getValue(EAST).isConnected();
			boolean isConnectedToSouth = state.getValue(SOUTH).isConnected();
			boolean isConnectedToWest = state.getValue(WEST).isConnected();
			if (isConnectedToNorth)
			{
				state = state.setValue(NORTH, BluestoneSide.SIDE);
			}
			else
				if (isConnectedToSouth && !isConnectedToEast && !isConnectedToWest)
				{
					state = state.setValue(NORTH, BluestoneSide.END);
				}

			if (isConnectedToEast)
			{
				state = state.setValue(EAST, BluestoneSide.SIDE);
			}
			else
				if (isConnectedToWest && !isConnectedToNorth && !isConnectedToSouth)
				{
					state = state.setValue(EAST, BluestoneSide.END);
				}

			if (isConnectedToSouth)
			{
				state = state.setValue(SOUTH, BluestoneSide.SIDE);
			}
			else
				if (isConnectedToNorth && !isConnectedToEast && !isConnectedToWest)
				{
					state = state.setValue(SOUTH, BluestoneSide.END);
				}

			if (isConnectedToWest)
			{
				state = state.setValue(WEST, BluestoneSide.SIDE);
			}
			else
				if (isConnectedToEast && !isConnectedToNorth && !isConnectedToSouth)
				{
					state = state.setValue(WEST, BluestoneSide.END);
				}

		}
		return state;

	}

    private BlockState getMissingConnections(BlockGetter reader, BlockState state, BlockPos pos)
    {
    	boolean nonNormalCubeAbove = !reader.getBlockState(pos.above()).isRedstoneConductor(reader, pos);

        for(Direction direction : Direction.Plane.HORIZONTAL)
        {
        	if (!state.getValue(PROPERTY_BY_DIRECTION.get(direction)).isConnected())
        	{
        		BluestoneSide bluestoneSide = this.recalculateSide(reader, pos, direction, nonNormalCubeAbove);
        		state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), bluestoneSide);
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
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
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
    		return bluestoneSide.isConnected() == stateIn.getValue(PROPERTY_BY_DIRECTION.get(facing)).isConnected() &&
    				!areAllSidesValid(stateIn) ? stateIn.setValue(PROPERTY_BY_DIRECTION.get(facing), bluestoneSide)
    						: this.getUpdatedState(worldIn, this.crossState.setValue(PROPERTY_BY_DIRECTION.get(facing), bluestoneSide), currentPos);
    	}
    }

    private static boolean areAllSidesValid(BlockState state)
    {
    	return state.getValue(NORTH).isConnected() &&
    			state.getValue(SOUTH).isConnected() &&
    			state.getValue(EAST).isConnected() &&
    			state.getValue(WEST).isConnected();
    }

    private static boolean areAllSidesInvalid(BlockState state)
    {
    	return !state.getValue(NORTH).isConnected() &&
    			!state.getValue(SOUTH).isConnected() &&
    			!state.getValue(EAST).isConnected() &&
    			!state.getValue(WEST).isConnected();
    }

    public static ArrayList<Direction> getvalidSides(BlockState state)
    {
    	ArrayList<Direction> sides = new ArrayList<>();
    	PROPERTY_BY_DIRECTION.forEach((direction, bluestoneSide) ->
    	{
    		if (state.getValue(bluestoneSide).isConnected())
    		{
        		sides.add(direction);
    		}
    	});
    	return sides;
    }

    public void updateDiagonalNeighbors(BlockState state, LevelAccessor worldIn, BlockPos pos, int flags)
    {
    	BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
		for(Direction direction : Direction.Plane.HORIZONTAL)
     	{
			BluestoneSide bluestoneSide = state.getValue(PROPERTY_BY_DIRECTION.get(direction));
			if (bluestoneSide != BluestoneSide.NONE && worldIn.getBlockState(mutableBlockPos.set(pos).move(direction)).getBlock() != this)
			{
				mutableBlockPos.move(Direction.DOWN);
				BlockState blockstate = worldIn.getBlockState(mutableBlockPos);
				if (blockstate.getBlock() != Blocks.OBSERVER)
				{
					BlockPos blockpos = mutableBlockPos.relative(direction.getOpposite());
					BlockState blockstate1 = blockstate.updateShape(direction.getOpposite(), worldIn.getBlockState(blockpos), worldIn, mutableBlockPos, blockpos);
					updateOrDestroy(blockstate, blockstate1, worldIn, mutableBlockPos, flags);
				}

				mutableBlockPos.set(pos).move(direction).move(Direction.UP);
				BlockState blockstate3 = worldIn.getBlockState(mutableBlockPos);
				if (blockstate3.getBlock() != Blocks.OBSERVER)
				{
					BlockPos blockpos1 = mutableBlockPos.relative(direction.getOpposite());
					BlockState blockstate2 = blockstate3.updateShape(direction.getOpposite(), worldIn.getBlockState(blockpos1), worldIn, mutableBlockPos, blockpos1);
					updateOrDestroy(blockstate3, blockstate2, worldIn, mutableBlockPos, flags);
				}
			}
    	}
    }

    public BluestoneSide getSide(BlockGetter worldIn, BlockPos pos, Direction face)
    {
    	return this.recalculateSide(worldIn, pos, face, !worldIn.getBlockState(pos.above()).isRedstoneConductor(worldIn, pos));
    }

    private BluestoneSide recalculateSide(BlockGetter reader, BlockPos pos, Direction direction, boolean nonNormalCubeAbove)
    {
    	BlockPos blockpos = pos.relative(direction);
        BlockState blockstate = reader.getBlockState(blockpos);
        if (nonNormalCubeAbove)
        {
        	boolean flag = this.canSurviveOn(reader, blockpos, blockstate);
        	if (flag && canConnectTo(reader.getBlockState(blockpos.above()), reader, blockpos.above(), null) )
        	{
        		if (blockstate.isFaceSturdy(reader, blockpos, direction.getOpposite()))
        		{
        			return BluestoneSide.UP;
        		}

        		return BluestoneSide.SIDE;
        	}
        }

        BlockState wireState = reader.getBlockState(pos);
        return !canConnectTo(blockstate, reader, blockpos, direction) && (blockstate.isRedstoneConductor(reader, blockpos) ||
        		!canConnectTo(reader.getBlockState(blockpos.below()), reader, blockpos.below(), null)) ?
        				this.getEndConnection(direction, wireState) ? BluestoneSide.END : BluestoneSide.NONE : BluestoneSide.SIDE;
    }

    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
    	BlockPos blockpos = pos.below();
    	BlockState blockstate = worldIn.getBlockState(blockpos);
    	return this.canSurviveOn(worldIn, blockpos, blockstate);
    }

    //TODO CWASHY WASHY
    public boolean getEndConnection(Direction face, BlockState state)
    {
    	try
    	{
			boolean isConnectedToThis = state.getValue(PROPERTY_BY_DIRECTION.get(face)).isConnected();
			boolean isConnectedToOpposite = state.getValue(PROPERTY_BY_DIRECTION.get(face.getOpposite())).isConnected();
			boolean isConnectedToLeft = state.getValue(PROPERTY_BY_DIRECTION.get(face.getClockWise())).isConnected();
			boolean isConnectedToRight = state.getValue(PROPERTY_BY_DIRECTION.get(face.getCounterClockWise())).isConnected();

			return isConnectedToThis && !isConnectedToOpposite && !isConnectedToLeft && !isConnectedToRight;
		}
    	catch(IllegalArgumentException e)
    	{
    		return false;
    	}
    }

    private boolean canSurviveOn(BlockGetter reader, BlockPos pos, BlockState state)
    {
        return state.isFaceSturdy(reader, pos, Direction.UP) || state.is(Blocks.HOPPER);
    }

    /**
     * Calls World.notifyNeighborsOfStateChange() for all neighboring blocks, but only if the given block is a bluestone
     * wire.
     */
    private void notifyWireNeighborsOfStateChange(Level worldIn, BlockPos pos)
    {
    	if (worldIn.getBlockState(pos).is(this))
    	{
    		worldIn.updateNeighborsAt(pos, this);

    		for(Direction direction : Direction.values())
    		{
    			worldIn.updateNeighborsAt(pos.relative(direction), this);
    		}
    	}
    }

    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
   	{
    	if (!oldState.is(state.getBlock()) && !worldIn.isClientSide)
    	{
    		for(Direction direction : Direction.Plane.VERTICAL)
    		{
    			worldIn.updateNeighborsAt(pos.relative(direction), this);
    		}
    	}
   	}

	@Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    	if (!isMoving && !state.is(newState.getBlock()))
    	{
            BlockEntity te = worldIn.getBlockEntity(pos);
            if (te instanceof AbstractItemPipeBlockEntity)
            {
            	((AbstractItemPipeBlockEntity)te).dropInventory();
            }

    		super.onRemove(state, worldIn, pos, newState, isMoving);
    		if (!worldIn.isClientSide)
    		{
    			for(Direction direction : Direction.values())
    			{
    				worldIn.updateNeighborsAt(pos.relative(direction), this);
    			}

    			this.updateNeighborsStateChange(worldIn, pos);
    		}
    	}
    }

    private void updateNeighborsStateChange(Level worldIn, BlockPos pos)
    {
    	for(Direction direction1 : Direction.Plane.HORIZONTAL)
		{
			this.notifyWireNeighborsOfStateChange(worldIn, pos.relative(direction1));
		}

		for(Direction direction2 : Direction.Plane.HORIZONTAL)
		{
			BlockPos blockpos = pos.relative(direction2);
			if (worldIn.getBlockState(blockpos).isRedstoneConductor(worldIn, blockpos))
			{
				this.notifyWireNeighborsOfStateChange(worldIn, blockpos.above());
			} else {
				this.notifyWireNeighborsOfStateChange(worldIn, blockpos.below());
			}
		}
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
    	if (!worldIn.isClientSide)
    	{
    		if (!state.canSurvive(worldIn, pos))
    		{
    			dropResources(state, worldIn, pos);
            	worldIn.removeBlock(pos, false);
    		}
    	}
    }

   	public static boolean canConnectTo(BlockState blockState, BlockGetter world, BlockPos pos, @Nullable Direction side)
   	{
	   	Block block = blockState.getBlock();
	   	if (block == IMPSBlocks.BLUESTONE_WIRE.get())
	   	{
	   		return true;
	   	}
	   	else if (blockState.getBlock() == IMPSBlocks.BLUESTONE_TABLE.get() || world.getBlockEntity(pos) instanceof BaseContainerBlockEntity)
	   	{
	   		return side != Direction.UP && side != Direction.DOWN && side != null;
	   	} else
	   	{
	   		return false;
	   	}
   	}

	@Override
   	public BlockState rotate(BlockState state, Rotation rot)
   	{
		return switch (rot)
				{
					case CLOCKWISE_180 -> state.setValue(NORTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(WEST)).setValue(SOUTH, state.getValue(NORTH)).setValue(WEST, state.getValue(EAST));
					case COUNTERCLOCKWISE_90 -> state.setValue(NORTH, state.getValue(EAST)).setValue(EAST, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(WEST)).setValue(WEST, state.getValue(NORTH));
					case CLOCKWISE_90 -> state.setValue(NORTH, state.getValue(WEST)).setValue(EAST, state.getValue(NORTH)).setValue(SOUTH, state.getValue(EAST)).setValue(WEST, state.getValue(SOUTH));
					default -> state;
				};
   	}

   	@Override
   	public BlockState mirror(BlockState state, Mirror mirrorIn)
   	{
		return switch (mirrorIn)
				{
					case LEFT_RIGHT -> state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
					case FRONT_BACK -> state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
					default -> super.mirror(state, mirrorIn);
				};
   	}

   	@Override
   	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
   	{
   		builder.add(NORTH, EAST, SOUTH, WEST);
   	}

   	@Override
   	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player,
                                 InteractionHand handIn, BlockHitResult hit)
   	{
   		if (!worldIn.isClientSide)
   		{
   			BlockEntity te = worldIn.getBlockEntity(pos);
	   		if (te instanceof BluestoneWireBlockEntity wireTE)
	   		{
                if (player.getItemInHand(handIn) == ItemStack.EMPTY)
	   			{
	   				if (player.isCrouching())
	   				{
	   					wireTE.setRenderDebug(!wireTE.getRenderDebug());
	   					System.out.format("Render debug enabled: %b\n", wireTE.getRenderDebug());
	   				}
	   				else
	   				{
	   					wireTE.dropItem(null, null);
	   					return InteractionResult.SUCCESS;
	   				}
   	   			}
	   			else
	   			{
	   				wireTE.receiveItemStack(player.getItemInHand(handIn), null);
	   				player.setItemInHand(handIn, ItemStack.EMPTY);
	   				return InteractionResult.SUCCESS;
	   			}
	   		}

   		}

   		if (!player.getAbilities().mayBuild)
   		{
   			return InteractionResult.PASS;
   		}
   		else
   		{
   			if (areAllSidesValid(state) || areAllSidesInvalid(state))
   	   		{
   	   			BlockState blockstate = areAllSidesValid(state) ? this.defaultBlockState() : this.crossState;
   	            if (blockstate != state) {
   	               worldIn.setBlock(pos, blockstate, 3);
   	               this.updateChangedConnections(worldIn, pos, state, blockstate);
   	               return InteractionResult.SUCCESS;
   	            }
   	   		}

   	   		return InteractionResult.SUCCESS;
   		}
   	}

   	private void updateChangedConnections(Level world, BlockPos pos, BlockState prevState, BlockState newState)
	{
		for (Direction direction : Direction.Plane.HORIZONTAL)
		{
			BlockPos blockpos = pos.relative(direction);
			if (prevState.getValue(PROPERTY_BY_DIRECTION.get(direction)).isConnected() != newState.getValue(PROPERTY_BY_DIRECTION.get(direction)).isConnected() &&
					world.getBlockState(blockpos).isRedstoneConductor(world, blockpos))
			{
				world.updateNeighborsAtExceptFromFacing(blockpos, newState.getBlock(), direction.getOpposite());
			}
		}
	}

   	@Override
   	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
   	{
   		return IMPSBlockEntities.BLUESTONE_WIRE.get().create(pos, state);
   	}
}