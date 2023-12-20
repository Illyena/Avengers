package illyena.gilding.avengers.block.blockentity;

import illyena.gilding.avengers.block.MjolnirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class MjolnirBlockEntity extends BlockEntity implements Nameable {
    public static final String DAMAGE = "Damage";
    public static final String ENCHANTMENTS = "Enchantments";
    private int damage;
    private NbtList enchantments;
    @Nullable
    private Text customName;

    public MjolnirBlockEntity(BlockPos pos, BlockState state) {
        super(AvengersBlockEntities.MJOLNIR_BLOCK_ENTITY, pos, state);
    }

    public void readFrom(ItemStack stack) {
        this.damage = stack.getDamage();
        this.enchantments = stack.getEnchantments();
        this.customName = stack.hasCustomName() ? stack.getName() : null;
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(nbt.getString("CustomName"));
        }
        if (nbt.contains(DAMAGE)) {
            this.damage = nbt.getInt(DAMAGE);
        }
        if (nbt.contains(ENCHANTMENTS)) {
            this.enchantments = nbt.getList(ENCHANTMENTS, 10);
        }
    }

    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("Damage", this.damage);
        if (this.enchantments != null) {
            nbt.put("Enchantments", this.enchantments);
        }
        if (this.customName != null) {
            nbt.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
    }

    public void setDamage(int i, ItemStack itemStack) {
        this.damage = i;
        if (this.getWorld() != null) {
            BlockState blockState = this.getWorld().getBlockState(this.getPos());
            if (blockState.getBlock() instanceof MjolnirBlock && this.damage >= itemStack.getMaxDamage() - 1) {
                this.getWorld().setBlockState(this.getPos(), blockState.with(MjolnirBlock.BROKEN, true));
            }
        }

    }

    public int getDamage() { return this.damage; }

    public Text getName() {
        return this.customName != null ? this.customName : Text.translatable("block.avengers.mjolnir_block");
    }

    @Nullable
    public Text getCustomName() { return this.customName; }

    public void setCustomName(Text customName) { this.customName = customName; }

    public void setEnchantments(NbtList nbtList) { this.enchantments = nbtList; }

    public NbtList getEnchantments() { return this.enchantments; }

}