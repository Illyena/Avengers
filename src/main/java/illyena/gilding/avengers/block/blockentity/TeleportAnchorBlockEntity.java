package illyena.gilding.avengers.block.blockentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TeleportAnchorBlockEntity extends BlockEntity {
    private static final float BEAM_TIME = 40.0f;
    public long age;

    public TeleportAnchorBlockEntity(BlockPos pos, BlockState state) {
        super(AvengersBlockEntities.TELEPORT_ANCHOR_BLOCK_ENTITY, pos, state);
    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("Age", this.age);
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.age = nbt.getLong("Age");
    }

    public static void tick(World world, BlockPos pos, BlockState state, TeleportAnchorBlockEntity blockEntity) {
        boolean bl = blockEntity.isRecentlyGenerated();
        ++blockEntity.age;
        if (!world.isClient() && bl != blockEntity.isRecentlyGenerated()) {
            markDirty(world, pos, state);
        }
    }

    public float getBeamTime() { return BEAM_TIME; }

    public boolean isRecentlyGenerated() { return this.age < BEAM_TIME; }

    public float getRecentlyGeneratedBeamHeight(float tickDelta) {
        return MathHelper.clamp(((float) this.age + tickDelta) / BEAM_TIME, 0.0F, 1.0F);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }

    public NbtCompound toInitialChunkDataNbt() { return this.createNbt(); }

    public static void startTeleportCooldown(World world, BlockPos pos, BlockState state, TeleportAnchorBlockEntity blockEntity) {
        if (!world.isClient) {
            blockEntity.age = 0;
            world.addSyncedBlockEvent(pos, state.getBlock(), 1, 0);
            markDirty(world, pos, state);
        }
    }

    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.age = 0;
            return true;
        } else {
            return super.onSyncedBlockEvent(type, data);
        }

    }

    public boolean shouldDrawSide(Direction direction) {
        return Block.shouldDrawSide(this.getCachedState(), this.getWorld(), this.getPos(), direction, this.getPos().offset(direction));
    }

    public int getDrawnSidesCount() {
        int i = 0;
        for (Direction direction : Direction.values()) {
            i += this.shouldDrawSide(direction) ? 1 : 0;
        }
        return i;
    }

}
