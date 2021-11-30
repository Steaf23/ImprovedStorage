package steef23.improvedstorage.common.world.level.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import steef23.improvedstorage.common.world.entity.StoneGolem;
import steef23.improvedstorage.common.world.level.block.entity.StoneChestBlockEntity;
import steef23.improvedstorage.core.init.IMPSBlockEntities;
import steef23.improvedstorage.core.init.IMPSBlocks;
import steef23.improvedstorage.core.init.IMPSEntities;

import java.util.List;
import java.util.function.Supplier;

public class StoneChestBlock extends AbstractChestBlock<StoneChestBlockEntity> implements SimpleWaterloggedBlock, EntityBlock
{

	private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	private final Supplier<BlockEntityType<? extends StoneChestBlockEntity>> tileEntityTypeSupplier;
	
	private BlockPattern stoneGolemPattern;

	public StoneChestBlock(Supplier<BlockEntityType<? extends StoneChestBlockEntity>> tileEntityTypeSupplierIn, BlockBehaviour.Properties propertiesIn)
	{
		super(propertiesIn, IMPSBlockEntities.STONE_CHEST::get);
		
		this.tileEntityTypeSupplier = tileEntityTypeSupplierIn;
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.FALSE));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn,
										  BlockPos currentPos, BlockPos facingPos)
	{
		if (stateIn.getValue(WATERLOGGED))
		{
			worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.FLOWING_WATER.getTickDelay(worldIn));
		}
		return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		Direction direction = context.getHorizontalDirection().getOpposite();
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState().setValue(FACING, direction).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return IMPSBlockEntities.STONE_CHEST.get().create(pos, state);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player,
											  InteractionHand handIn, BlockHitResult hit)
	{
		if (!worldIn.isClientSide)
		{
			BlockEntity tile = worldIn.getBlockEntity(pos);
			if (tile instanceof StoneChestBlockEntity)
			{
				NetworkHooks.openGui((ServerPlayer)player, (StoneChestBlockEntity)tile, pos);
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if (!state.is(newState.getBlock()))
		{
			BlockEntity te = worldIn.getBlockEntity(pos);
			if (te instanceof StoneChestBlockEntity)
			{
				Containers.dropContents(worldIn, pos, ((StoneChestBlockEntity)te).getItems());
			}
			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
	{
		return SHAPE;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, WATERLOGGED);
		super.createBlockStateDefinition(builder);
	}
	
	private static boolean isBlocked(LevelAccessor iWorld, BlockPos blockPos)
	{
		return isBelowSolidBlock(iWorld, blockPos) || isCatSittingOn(iWorld, blockPos);
	}
	
    private static boolean isBelowSolidBlock(LevelAccessor iWorld, BlockPos blockPos)
    {
        BlockPos blockPosNew = blockPos.above();
        return iWorld.getBlockState(blockPosNew).isRedstoneConductor(iWorld, blockPosNew);
    }

    private static boolean isCatSittingOn(LevelAccessor iWorld, BlockPos blockPos)
    {
    	List<Cat> catList = iWorld.getEntitiesOfClass(Cat.class, new AABB(
				blockPos.getX(),
				blockPos.getY() + 1,
				blockPos.getZ(),
				blockPos.getX() + 1,
				blockPos.getY() + 2,
				blockPos.getZ() + 1
		));
    	if (!catList.isEmpty()) 
    	{
    		for(Cat catEntity : catList)
    		{
    			if (catEntity.isInSittingPose())
    			{
    				return true;
    			}
    		}
    	}	
    	return false;
    }

	@Override
	public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState state, Level level, BlockPos pos, boolean canBeOpened) {
		return DoubleBlockCombiner.Combiner::acceptNone;
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving)
	{
		float yaw = state.getValue(FACING).toYRot();
		if (oldState.getBlock() != state.getBlock())
		{
			this.trySpawnGolem(level, pos, yaw);
	    }
	}

	private void trySpawnGolem(Level level, BlockPos pos, float yaw)
	{
		BlockPattern.BlockPatternMatch patternMatch = this.getStoneGolemPattern().find(level, pos);

		if (patternMatch != null)
		{
			for (int i = 0; i < this.getStoneGolemPattern().getHeight(); ++i)
			{
				BlockInWorld blockInWorld = patternMatch.getBlock(0,  i, 0);
				level.setBlock(blockInWorld.getPos(), Blocks.AIR.defaultBlockState(), 2);
				level.levelEvent(2001, blockInWorld.getPos(), Block.getId(blockInWorld.getState()));
			}

			StoneGolem golem = IMPSEntities.STONE_GOLEM.get().create(level);
			BlockPos blockpos = patternMatch.getBlock(0, 1, 0).getPos();

			assert golem != null;
			golem.moveTo((double)blockpos.getX() + 0.5D,
												  (double)blockpos.getY() + 0.05D,
												  (double)blockpos.getZ() + 0.5D,
												  0.0f,
												  yaw);
			level.addFreshEntity(golem);

			for(ServerPlayer serverPlayer : level.getEntitiesOfClass(ServerPlayer.class, golem.getBoundingBox().inflate(5.0D))) {
	            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, golem);
	         	}

	        for(int l = 0; l < this.getStoneGolemPattern().getHeight(); ++l) {
	            BlockInWorld blockInWorld = patternMatch.getBlock(0, l, 0);
	            level.blockUpdated(blockInWorld.getPos(), Blocks.AIR);
	        }
		}
	}

	private BlockPattern getStoneGolemPattern()
	{
	      if (this.stoneGolemPattern == null)
	      {
	         this.stoneGolemPattern = BlockPatternBuilder.start().aisle("^", "#").where('^', BlockInWorld.hasState(BlockStatePredicate.forBlock(IMPSBlocks.STONE_CHEST.get()))).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.CHISELED_STONE_BRICKS))).build();
	      }

	      return this.stoneGolemPattern;
	}

//	@Override
//	public boolean canCreatureSpawn(BlockState state, IBlockReader world, BlockPos pos, PlacementType type,
//			EntityType<?> entityType)
//	{
//		return true;
//	}

}
