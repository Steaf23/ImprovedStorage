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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;
import steef23.improvedstorage.common.world.level.block.entity.AbstractItemPipeBlockEntity;
import steef23.improvedstorage.common.world.level.block.entity.BluestoneWireBlockEntity;
import steef23.improvedstorage.core.init.IMPSBlockEntities;
import steef23.improvedstorage.core.init.IMPSBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;

public class BluestoneWireBlock extends Block implements EntityBlock
{

	public static final EnumProperty<BluestoneSide> NORTH = EnumProperty.create("north", BluestoneSide.class);
	public static final EnumProperty<BluestoneSide> EAST = EnumProperty.create("east", BluestoneSide.class);
	public static final EnumProperty<BluestoneSide> SOUTH = EnumProperty.create("south", BluestoneSide.class);
	public static final EnumProperty<BluestoneSide> WEST = EnumProperty.create("west", BluestoneSide.class);
	public static final Map<Direction, EnumProperty<BluestoneSide>> DIRECTION_TO_SIDE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH,
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
    		this.SHAPES_CACHE.put(state, this.calculateShape(state));
    	});
    }

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(NORTH, EAST, SOUTH, WEST);
	}

    private VoxelShape calculateShape(BlockState state)
    {
    	VoxelShape voxelShape = BASE_SHAPE;

    	for(Direction direction : Direction.Plane.HORIZONTAL)
    	{
    		BluestoneSide bluestoneSide = getSideValue(direction, state);
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

    //DONE
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
    	return this.SHAPES_CACHE.get(state);
    }

    //DONE
    public BlockState getStateForPlacement(BlockPlaceContext context)
   	{
   		BlockState state = updateState(this.defaultBlockState(), context.getClickedPos(), context.getLevel());
   		state = setEnds(state);
   		return state;
   	}

   	private BlockState updateState(BlockState oldState, BlockPos pos, Level level)
	{
		return getUpdatedSides(level, oldState, pos);
	}

//    private BlockState getNewState(BlockGetter reader, BlockState state, BlockPos pos)
//   	{
//    	boolean isNotConnectedOld = isNotConnected(state);
//    	BlockState updatedState = this.getUpdatedSides(reader, this.defaultBlockState(), pos);
//		if (!isNotConnectedOld || !isNotConnected(updatedState))
//		{
//			boolean isConnectedToNorth = updatedState.getValue(NORTH).isConnected();
//			boolean isConnectedToEast = updatedState.getValue(EAST).isConnected();
//			boolean isConnectedToSouth = updatedState.getValue(SOUTH).isConnected();
//			boolean isConnectedToWest = updatedState.getValue(WEST).isConnected();
//			if (isConnectedToNorth)
//			{
//				updatedState = updatedState.setValue(NORTH, BluestoneSide.SIDE);
//			}
//			else
//				if (isConnectedToSouth && !isConnectedToEast && !isConnectedToWest)
//				{
//					updatedState = updatedState.setValue(NORTH, BluestoneSide.END);
//				}
//
//			if (isConnectedToEast)
//			{
//				updatedState = updatedState.setValue(EAST, BluestoneSide.SIDE);
//			}
//			else
//				if (isConnectedToWest && !isConnectedToNorth && !isConnectedToSouth)
//				{
//					updatedState = updatedState.setValue(EAST, BluestoneSide.END);
//				}
//
//			if (isConnectedToSouth)
//			{
//				updatedState = updatedState.setValue(SOUTH, BluestoneSide.SIDE);
//			}
//			else
//				if (isConnectedToNorth && !isConnectedToEast && !isConnectedToWest)
//				{
//					updatedState = updatedState.setValue(SOUTH, BluestoneSide.END);
//				}
//
//			if (isConnectedToWest)
//			{
//				updatedState = updatedState.setValue(WEST, BluestoneSide.SIDE);
//			}
//			else
//				if (isConnectedToEast && !isConnectedToNorth && !isConnectedToSouth)
//				{
//					updatedState = updatedState.setValue(WEST, BluestoneSide.END);
//				}
//
//		}
//		return updatedState;
//
//	}

	//DONE
    private BlockState getUpdatedSides(BlockGetter reader, BlockState state, BlockPos pos)
    {
		BlockState newState = state;
        for(Direction direction : Direction.Plane.HORIZONTAL)
        {
        	BluestoneSide newSideValue = getNewSideDefinition(reader, pos, direction);
			newState = setSideValue(direction, newState, newSideValue);

			updateNeighborShape(direction, pos, (LevelAccessor) reader);
        }
        return newState;
    }

    private void updateNeighborShape(Direction toDirection, BlockPos pos, LevelAccessor level)
	{
		BlockPos targetPos = pos.relative(toDirection);
		BlockState targetState = level.getBlockState(targetPos);
		Direction targetFaceToUpdate = toDirection.getOpposite();

		if (!targetState.is(Blocks.OBSERVER))
		{
			targetState.updateShape(targetFaceToUpdate, level.getBlockState(pos), (LevelAccessor)level, pos.relative(toDirection), pos);
		}
	}

	private BluestoneSide getNewSideDefinition(BlockGetter reader, BlockPos pos, Direction direction)
	{
		BlockPos targetPos = pos.relative(direction);
		BlockState targetState = reader.getBlockState(targetPos);

		boolean shouldBreak = !this.canSurvive(reader.getBlockState(pos), (LevelReader)reader, pos);
		boolean connect = canConnectTo(reader.getBlockState(targetPos), reader, targetPos, direction);
		if (!shouldBreak && connect)
		{
//			if (targetState.isFaceSturdy(reader, targetPos, direction.getOpposite()))
//			{
//				return BluestoneSide.UP;
//			}

			if (targetState.getBlock() instanceof BluestoneTableBlock)
			{
				return BluestoneSide.END;
			}

			return BluestoneSide.SIDE;
		}

		return BluestoneSide.NONE;


//		BlockState wireState = reader.getBlockState(pos);
//		return !canConnectTo(targetState, reader, targetPos, direction) && (targetState.isRedstoneConductor(reader, targetPos) ||
//				!canConnectTo(reader.getBlockState(targetPos.below()), reader, targetPos.below(), null)) ?
//				this.getEndConnection(direction, wireState) ? BluestoneSide.END : BluestoneSide.NONE : BluestoneSide.SIDE;
	}

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor levelAccessor, BlockPos currentPos, BlockPos facingPos)
    {
    	BlockState state = setSideValue(facing, stateIn, getNewSideDefinition(levelAccessor, currentPos, facing));
    	state = setEnds(state);
		return state;
    }

    //DONE
    private static boolean isFullyConnected(BlockState state)
    {
    	return state.getValue(NORTH).isConnected() &&
    			state.getValue(SOUTH).isConnected() &&
    			state.getValue(EAST).isConnected() &&
    			state.getValue(WEST).isConnected();
    }

    //DONE
    private static boolean isNotConnected(BlockState state)
    {
    	return !state.getValue(NORTH).isConnected() &&
    			!state.getValue(SOUTH).isConnected() &&
    			!state.getValue(EAST).isConnected() &&
    			!state.getValue(WEST).isConnected();
    }

//    public void updateDiagonalNeighbors(BlockState state, LevelAccessor worldIn, BlockPos pos, int flags)
//    {
//    	BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
//		for(Direction direction : Direction.Plane.HORIZONTAL)
//     	{
//			BluestoneSide bluestoneSide = getSideValue(direction, state);
//			if (bluestoneSide != BluestoneSide.NONE && worldIn.getBlockState(mutableBlockPos.set(pos).move(direction)).getBlock() != this)
//			{
//				mutableBlockPos.move(Direction.DOWN);
//				BlockState blockstate = worldIn.getBlockState(mutableBlockPos);
//				if (blockstate.getBlock() != Blocks.OBSERVER)
//				{
//					BlockPos blockpos = mutableBlockPos.relative(direction.getOpposite());
//					BlockState blockstate1 = blockstate.updateShape(direction.getOpposite(), worldIn.getBlockState(blockpos), worldIn, mutableBlockPos, blockpos);
//					updateOrDestroy(blockstate, blockstate1, worldIn, mutableBlockPos, flags);
//				}
//
//				mutableBlockPos.set(pos).move(direction).move(Direction.UP);
//				BlockState blockstate3 = worldIn.getBlockState(mutableBlockPos);
//				if (blockstate3.getBlock() != Blocks.OBSERVER)
//				{
//					BlockPos blockpos1 = mutableBlockPos.relative(direction.getOpposite());
//					BlockState blockstate2 = blockstate3.updateShape(direction.getOpposite(), worldIn.getBlockState(blockpos1), worldIn, mutableBlockPos, blockpos1);
//					updateOrDestroy(blockstate3, blockstate2, worldIn, mutableBlockPos, flags);
//				}
//			}
//    	}
//    }

    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
    	BlockPos blockpos = pos.below();
    	BlockState blockstate = worldIn.getBlockState(blockpos);
    	return this.canSurviveOn(worldIn, blockpos, blockstate);
    }

    private boolean canSurviveOn(BlockGetter reader, BlockPos pos, BlockState state)
    {
        return state.isFaceSturdy(reader, pos, Direction.UP);
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

   	public static boolean canConnectTo(BlockState blockState, BlockGetter world, BlockPos pos, @Nonnull Direction side)
   	{
	   	Block block = blockState.getBlock();
	   	if (block == IMPSBlocks.BLUESTONE_WIRE.get())
	   	{
	   		return true;
	   	}
	   	else if (block == IMPSBlocks.BLUESTONE_TABLE.get() || world.getBlockEntity(pos) instanceof BaseContainerBlockEntity)
	   	{
	   		return side != Direction.UP && side != Direction.DOWN;
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
	   					wireTE.setChanged();
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
//   			if (isFullyConnected(state) || isNotConnected(state))
//   	   		{
//   	   			BlockState blockstate = isFullyConnected(state) ? this.defaultBlockState() : this.crossState;
//   	            if (blockstate != state) {
//   	               worldIn.setBlock(pos, blockstate, 3);
//   	               this.updateChangedConnections(worldIn, pos, state, blockstate);
//   	               return InteractionResult.SUCCESS;
//   	            }
//   	   		}

   	   		return InteractionResult.SUCCESS;
   		}
   	}

   	private void updateChangedConnections(Level world, BlockPos pos, BlockState prevState, BlockState newState)
	{
		for (Direction direction : Direction.Plane.HORIZONTAL)
		{
			BlockPos blockpos = pos.relative(direction);
			if (getSideValue(direction, prevState).isConnected() != newState.getValue(DIRECTION_TO_SIDE.get(direction)).isConnected() &&
					world.getBlockState(blockpos).isRedstoneConductor(world, blockpos))
			{
				world.updateNeighborsAtExceptFromFacing(blockpos, newState.getBlock(), direction.getOpposite());
			}
		}
	}

	//DONE
	public static BluestoneSide getSideValue(Direction direction, BlockState state)
	{
		if (direction != Direction.UP && direction != Direction.DOWN)
		{
			return state.getValue(DIRECTION_TO_SIDE.get(direction));
		}
		return BluestoneSide.NONE;
	}

	//DONE
	public static BlockState setSideValue(Direction direction, BlockState oldState, BluestoneSide value)
	{
		if (direction != Direction.UP && direction != Direction.DOWN)
		{
			return oldState.setValue(DIRECTION_TO_SIDE.get(direction), value);
		}
		return oldState;
	}

	// set ends retroactively after every update
	private BlockState setEnds(BlockState state)
	{
		BlockState newState = state;

		for (Direction d : Direction.Plane.HORIZONTAL)
		{
			if (getSideValue(d, newState) == BluestoneSide.END) //set old ends to side
			{
				newState = setSideValue(d, newState, BluestoneSide.NONE);
			}
		}

		Direction connectedSide = null;
		for (Direction direction : Direction.Plane.HORIZONTAL)
		{
			if (getSideValue(direction, newState) == BluestoneSide.SIDE)
			{
				if (connectedSide == null) // found first connected side
				{
					connectedSide = direction;
				}
				else // more than one side is connected, so there are no ends
				{
					return newState;
				}
			}
		}

		if (connectedSide != null) // if one side is connected, make it an end
		{
			newState = setSideValue(connectedSide.getOpposite(), newState, BluestoneSide.END);
		}
		else // if no sides are connected, make all of them ends :D
		{
			for (Direction d : Direction.Plane.HORIZONTAL)
			{
				newState = setSideValue(d, newState, BluestoneSide.END);
			}
		}

		return newState;
	}

	//DONE
   	@Override
   	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
   	{
   		return IMPSBlockEntities.BLUESTONE_WIRE.get().create(pos, state);
   	}

   	//DONE
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState state, BlockEntityType<T> entityType)
	{
		return (level, blockPos, blockState, be) -> {
			BluestoneWireBlockEntity.tick(level, blockPos, blockState, (BluestoneWireBlockEntity)be);
		};
	}
}