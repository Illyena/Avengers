package illyena.gilding.avengers.block.blockentity;

import illyena.gilding.core.client.render.ColorAssist;
import illyena.gilding.core.util.time.GildingCalendar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.awt.*;

public class TeleportAnchorBlockEntity extends BlockEntity {
    private static final float BEAM_TIME = 40.0f;
    private final ColorAssist colorAssist;
    public long age;
    public long beamAge;

    public TeleportAnchorBlockEntity(BlockPos pos, BlockState state) {
        super(AvengersBlockEntities.TELEPORT_ANCHOR_BLOCK_ENTITY, pos, state);
        this.colorAssist = createColorAssist();
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
        boolean bl = blockEntity.shouldRenderBeam();
        ++blockEntity.age;
        if (blockEntity.shouldRenderBeam()) {
            ++blockEntity.beamAge;
        }
        if (!world.isClient() && bl != blockEntity.shouldRenderBeam()) {

            markDirty(world, pos, state);
        }
    }

    public static float getBeamTime() { return BEAM_TIME; }

    public boolean shouldRenderBeam() { return this.age < BEAM_TIME; }

    public float getBeamHeight(float tickDelta) {
        return MathHelper.clamp(((float) this.age + tickDelta) / BEAM_TIME, 0.0F, 1.0F);
    }

    private static ColorAssist createColorAssist() {
        switch (GildingCalendar.checkHolidays()) {
            case NEW_YEARS -> { return new ColorAssist(BEAM_TIME, true, new Color(182, 144, 0), Color.BLACK); }
            case CHRISTMAS -> { return new ColorAssist(BEAM_TIME / 2.0f, false, ColorAssist.toColor(DyeColor.RED), Color.BLACK, new Color(0, 73, 0)); }
            case BIRTHDAY -> { return new ColorAssist(25.0f, true, ColorAssist.JEB_COLORS); }
            default -> { return new ColorAssist(BEAM_TIME, true, ColorAssist.toColor(DyeColor.PURPLE), ColorAssist.toColor(DyeColor.MAGENTA)); }
        }
    }

    public ColorAssist getColorAssist() { return this.colorAssist; }

    public BlockEntityUpdateS2CPacket toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }

    public NbtCompound toInitialChunkDataNbt() { return this.createNbt(); }

    public static void startTeleportCooldown(World world, BlockPos pos, BlockState state, TeleportAnchorBlockEntity blockEntity) {
        if (!world.isClient) {
            blockEntity.age = 0;
            if (!blockEntity.colorAssist.isContinuous) {
                blockEntity.beamAge = 0;
            }
            world.addSyncedBlockEvent(pos, state.getBlock(), 1, 0);
            markDirty(world, pos, state);
        }
    }

    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.age = 0;
            if (!this.colorAssist.isContinuous) {
                this.beamAge = 0;
            }
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
