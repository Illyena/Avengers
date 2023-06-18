package illyena.gilding.avengers.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import static illyena.gilding.avengers.AvengersInit.translationKeyOf;

public class MjolnirBlock extends Block {
    protected static final VoxelShape SHAPE = composeAxisXShape();

    private static VoxelShape composeAxisXShape(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.34375, 0.0, 0.40625, 0.65625, 0.1875, 0.59375));
        shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.46875, 0.0, 0.46875, 0.53125, 0.625, 0.53125));
        shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.46875, 0.0, 0.46875, 0.53125, 0.0625, 0.53125));
        return shape;
    }

    private static VoxelShape composeEWShape() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.3125, 0.0, 0.375, 0.6875, 0.25, 0.625));
        shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.4375, 0.0, 0.4375, 0.5625, 0.6875, 0.5625));
        return shape;
    }

    public MjolnirBlock(Settings settings) { super(settings); }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) { return SHAPE; }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (player.isSpectator()) {
            return ActionResult.CONSUME;
        } else if(player.experienceLevel>= 30 || player.isCreative()) {
            player.getInventory().insertStack(new ItemStack(this.asItem()));
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            return ActionResult.SUCCESS;
        } else {
            player.sendMessage(translationKeyOf("message", "not_worthy"));
            return ActionResult.FAIL;
        }
    }
}
