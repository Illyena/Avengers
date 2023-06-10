package illyena.gilding.avengers.block.blockentity;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class MjolnirBlockEntity extends BlockEntity {
    private int damage;

    public MjolnirBlockEntity(BlockPos pos, BlockState state) {
        super(AvengersBlockEntities.MJOLNIR_BLOCK_ENTITY, pos, state);
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.damage = nbt.getInt("Damage");
    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("Damage", this.damage);
    }

    public void setDamage(int i) { this.damage = i; }

    public int getDamage() { return this.damage; }

}
