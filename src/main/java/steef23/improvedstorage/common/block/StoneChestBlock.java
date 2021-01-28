package steef23.improvedstorage.common.block;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPattern.PatternHelper;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.tileentity.TileEntityMerger.ICallback;
import net.minecraft.tileentity.TileEntityMerger.ICallbackWrapper;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import steef23.improvedstorage.common.entity.StoneGolemEntity;
import steef23.improvedstorage.common.tileentity.StoneChestTileEntity;
import steef23.improvedstorage.core.init.IMPSBlocks;
import steef23.improvedstorage.core.init.IMPSEntities;
import steef23.improvedstorage.core.init.IMPSTileEntities;

public class StoneChestBlock extends Block implements IWaterLoggable
{

	private static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	private final Supplier<TileEntityType<? extends StoneChestTileEntity>> tileEntityTypeSupplier;
	
	private BlockPattern stoneGolemPattern;

	public StoneChestBlock(Supplier<TileEntityType<? extends StoneChestTileEntity>> tileEntityTypeSupplierIn, Properties propertiesIn) 
	{
		super(propertiesIn);
		
		this.tileEntityTypeSupplier = tileEntityTypeSupplierIn;
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(WATERLOGGED, Boolean.valueOf(false)));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
			BlockPos currentPos, BlockPos facingPos) 
	{
		if (stateIn.get(WATERLOGGED))
		{
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.FLOWING_WATER.getTickRate(worldIn));
		}
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) 
	{
		Direction direction = context.getPlacementHorizontalFacing().getOpposite();
		FluidState iFluidState = context.getWorld().getFluidState(context.getPos());
		return this.getDefaultState().with(FACING, direction).with(WATERLOGGED, iFluidState.getFluid() == Fluids.WATER);
	}
	

	@SuppressWarnings("deprecation")
	@Override
	public FluidState getFluidState(BlockState state) 
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) 
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) 
	{
		return IMPSTileEntities.STONE_CHEST.get().create();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) 
	{
		if (!worldIn.isRemote)
		{
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof StoneChestTileEntity)
			{
				NetworkHooks.openGui((ServerPlayerEntity)player, (StoneChestTileEntity)tile, pos);
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) 
	{
		if (state.getBlock() != newState.getBlock())
		{
			TileEntity te = worldIn.getTileEntity(pos);
			if (te instanceof StoneChestTileEntity)
			{
				InventoryHelper.dropItems(worldIn, pos, ((StoneChestTileEntity)te).getItems());
			}
		}
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) 
	{
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	

	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) 
	{
		return SHAPE;
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) 
	{
		builder.add(FACING, WATERLOGGED);
		super.fillStateContainer(builder);
	}
	
	public ICallbackWrapper<? extends StoneChestTileEntity> getWrapper(BlockState blockState, World worldIn, BlockPos posIn, boolean canBeOpened)
	{
		BiPredicate<IWorld, BlockPos> biPredicate;
		if (canBeOpened)
		{
			biPredicate = (world, pos) -> false;
		}
		else
		{
			biPredicate = StoneChestBlock::isBlocked;
		}
		return TileEntityMerger.func_226924_a_(this.tileEntityTypeSupplier.get(), 
											   StoneChestBlock::getMergerType, 
											   StoneChestBlock::getDirectionToAttached, 
											   FACING, 
											   blockState, 
											   worldIn, 
											   posIn, 
											   biPredicate);
	}
	
	private static boolean isBlocked(IWorld iWorld, BlockPos blockPos) 
	{
		return isBelowSolidBlock(iWorld, blockPos) || isCatSittingOn(iWorld, blockPos);
	}
	
    private static boolean isBelowSolidBlock(IBlockReader iWorld, BlockPos blockPos) 
    {
        BlockPos blockPosNew = blockPos.up();
        return iWorld.getBlockState(blockPosNew).isNormalCube(iWorld, blockPosNew);
    }

    private static boolean isCatSittingOn(IWorld iWorld, BlockPos blockPos) 
    {
    	List<CatEntity> catList = iWorld.getEntitiesWithinAABB(CatEntity.class, new AxisAlignedBB((double)blockPos.getX(), 
    																						   (double)(blockPos.getY() + 1), 
    																						   (double)blockPos.getZ(), 
    																						   (double)(blockPos.getX() + 1), 
    																						   (double)(blockPos.getY() + 2), 
    																						   (double)(blockPos.getZ() + 1)));
    	if (!catList.isEmpty()) 
    	{
    		for(CatEntity catEntity : catList) 
    		{
    			if (catEntity.isSitting()) 
    			{
    				return true;
    			}
    		}
    	}	
    	return false;
    }

	public static ICallback<StoneChestTileEntity, Float2FloatFunction> getLid(final IChestLid tileEntityIn) 
	{
		return new ICallback<StoneChestTileEntity, Float2FloatFunction>() {
			public Float2FloatFunction func_225539_a_(StoneChestTileEntity tileEntity1, StoneChestTileEntity tileEntity2)
			{
				return (partialTicks) -> {
					return Math.max(tileEntity1.getLidAngle(partialTicks), tileEntity2.getLidAngle(partialTicks));
				};
			}
			
			public Float2FloatFunction func_225538_a_(StoneChestTileEntity tileEntity) 
			{
				return tileEntity::getLidAngle;
	        }

			public Float2FloatFunction func_225537_b_() 
			{
				return tileEntityIn::getLidAngle;
			}
		};
	}
	
	public static TileEntityMerger.Type getMergerType(BlockState blockState)
	{
		return TileEntityMerger.Type.SINGLE;
	}
	
	public static Direction getDirectionToAttached(BlockState blockState)
	{
		Direction direction = blockState.get(FACING);
		return direction.rotateYCCW();
	}
	
	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) 
	{
		float yaw = state.get(FACING).getHorizontalAngle();
		if (oldState.getBlock() != state.getBlock()) 
		{
			this.trySpawnGolem(worldIn, pos, yaw);
	    }
	}
	
	private void trySpawnGolem(World worldIn, BlockPos pos, float yaw) 
	{
		PatternHelper blockPatternHelper = this.getStoneGolemPattern().match(worldIn, pos);
		
		if (blockPatternHelper != null)
		{
			for (int i = 0; i < this.getStoneGolemPattern().getThumbLength(); ++i)
			{
				CachedBlockInfo cachedBlockInfo = blockPatternHelper.translateOffset(0,  i, 0);
				worldIn.setBlockState(cachedBlockInfo.getPos(), Blocks.AIR.getDefaultState(), 2);
				worldIn.playEvent(2001, cachedBlockInfo.getPos(), Block.getStateId(cachedBlockInfo.getBlockState()));
			}
			
			StoneGolemEntity stoneGolemEntity = IMPSEntities.STONE_GOLEM_ENTITY.get().create(worldIn);
			BlockPos blockpos = blockPatternHelper.translateOffset(0, 1, 0).getPos();
			stoneGolemEntity.setLocationAndAngles((double)blockpos.getX() + 0.5D, 
												  (double)blockpos.getY() + 0.05D, 
												  (double)blockpos.getZ() + 0.5D, 
												  yaw, 
												  0.0f);
			worldIn.addEntity(stoneGolemEntity);
			
			for(ServerPlayerEntity serverplayerentity : worldIn.getEntitiesWithinAABB(ServerPlayerEntity.class, stoneGolemEntity.getBoundingBox().grow(5.0D))) {
	            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayerentity, stoneGolemEntity);
	         	}

	        for(int l = 0; l < this.getStoneGolemPattern().getThumbLength(); ++l) {
	            CachedBlockInfo cachedBlockInfo1 = blockPatternHelper.translateOffset(0, l, 0);
	            worldIn.func_230547_a_(cachedBlockInfo1.getPos(), Blocks.AIR);
	        }
		}
	}

	private BlockPattern getStoneGolemPattern() 
	{
	      if (this.stoneGolemPattern == null) 
	      {
	         this.stoneGolemPattern = BlockPatternBuilder.start().aisle("^", "#").where('^', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(IMPSBlocks.STONE_CHEST.get()))).where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.CHISELED_STONE_BRICKS))).build();
	      }

	      return this.stoneGolemPattern;
	}
	
	@Override
	public boolean canCreatureSpawn(BlockState state, IBlockReader world, BlockPos pos, PlacementType type,
			EntityType<?> entityType)
	{
		return true;
	}

}
