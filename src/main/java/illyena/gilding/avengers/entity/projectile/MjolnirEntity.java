package illyena.gilding.avengers.entity.projectile;

import com.google.common.collect.Lists;
import illyena.gilding.avengers.block.MjolnirBlock;
import illyena.gilding.avengers.block.blockentity.MjolnirBlockEntity;
import illyena.gilding.avengers.entity.AvengersEntities;
import illyena.gilding.avengers.item.AvengersItems;
import illyena.gilding.avengers.item.custom.MjolnirItem;
import illyena.gilding.core.enchantment.GildingEnchantmentHelper;
import illyena.gilding.core.entity.projectile.ILoyalty;
import illyena.gilding.core.entity.projectile.IRicochet;
import illyena.gilding.mixin.entity.PersistentProjectileEntityAccessor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class MjolnirEntity extends PersistentProjectileEntity implements IRicochet, ILoyalty {
    private static final TrackedData<Integer> RICOCHET;
    private static final TrackedData<Integer> LOYALTY;
    private static final TrackedData<Boolean> ENCHANTED;
    private ItemStack mjolnirStack;

    private int bounces;
    private List<Entity> ricochetHitEntities = Lists.newArrayListWithCapacity(bounces);
    private int remainingBounces;
    private int hangTime;

    private boolean dealtDamage;
    private boolean blockHit;
    private int returnTimer;
    private int wait;

    public MjolnirEntity(EntityType<? extends MjolnirEntity> entityType, World world) {
        super(entityType, world);
        this.mjolnirStack = new ItemStack(AvengersItems.MJOLNIR);
        this.bounces = GildingEnchantmentHelper.getRicochet(this.mjolnirStack) * 2;
        this.blockHit = false;
    }

    public MjolnirEntity(World world, LivingEntity owner, ItemStack stack) {
        super(AvengersEntities.MJOLNIR_ENTITY_TYPE, owner, world);
        this.mjolnirStack = new ItemStack(AvengersItems.MJOLNIR);
        this.mjolnirStack = stack.copy();
        this.bounces = GildingEnchantmentHelper.getRicochet(this.mjolnirStack) * 2;
        this.remainingBounces = this.bounces;
        this.blockHit = false;
        this.dataTracker.set(RICOCHET, GildingEnchantmentHelper.getRicochet(stack));
        this.dataTracker.set(LOYALTY, EnchantmentHelper.getLoyalty(stack));
        this.dataTracker.set(ENCHANTED, stack.hasGlint());
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(RICOCHET, 0);
        this.dataTracker.startTracking(LOYALTY, 0);
        this.dataTracker.startTracking(ENCHANTED, false);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Mjolnir", 10)) {
            this.mjolnirStack = ItemStack.fromNbt(nbt.getCompound("Mjolnir"));
        }

        this.dealtDamage = nbt.getBoolean("DealtDamage");
        this.dataTracker.set(LOYALTY, EnchantmentHelper.getLoyalty(this.mjolnirStack));
        this.dataTracker.set(RICOCHET, GildingEnchantmentHelper.getRicochet(this.mjolnirStack));
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("Mjolnir", this.mjolnirStack.writeNbt(new NbtCompound()));
        nbt.putBoolean("DealtDamage", this.dealtDamage);
    }

    @Override
    public void tick() {
        if (this.getBlockPos().getY() <= this.world.getBottomY()) {
            this.toBlock(new BlockPos(this.getBlockPos().getX(), this.world.getBottomY() +1, this.getBlockPos().getZ()));
        }
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        if (!this.getLoyalty().equals(0)) {
            ILoyalty.tick(this);
        }
        if (!this.getRicochet().equals(0) && !ILoyalty.shouldReturn(this)) {
            IRicochet.tick(this);
        }

        super.tick();
    }

    protected void age() {
        if (((PersistentProjectileEntityAccessor)this).getLife() >= 600) {
            ((MjolnirItem)this.asItemStack().getItem()).toBlock(this.mjolnirStack, this.world, (LivingEntity) this.getOwner(), this.getBlockPos(), this.getHorizontalFacing());
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();

        float damage = (float)this.getVelocity().length();
        int i = MathHelper.ceil(MathHelper.clamp((double)damage * getDamage(), 0.0, 2.147483647E9));
        if(this.isCritical()) {
            long l = this.random.nextInt(i/2 +2);
            i = (int)Math.min(l + (long)i, 2147483647L);
        }
        if(entity instanceof LivingEntity livingEntity) {
            i += EnchantmentHelper.getAttackDamage(this.mjolnirStack, livingEntity.getGroup());
        }
        Entity owner = this.getOwner();

        if (isOwner(entity)
                && this.dataTracker.get(RICOCHET) > 0
                && this.random.nextInt(this.dataTracker.get(RICOCHET) * 2 - 1) > 0) {
            return;
        }

        DamageSource damageSource = DamageSource.thrownProjectile(this, owner == null ? this : owner);
        this.dealtDamage = true;
        SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_HIT; //todo SOUNDS

        boolean isEnderman = entity.getType() == EntityType.ENDERMAN;
        if(this.isOnFire() && !isEnderman) {
            entity.setOnFireFor(5);
        }

        if(entity.damage(damageSource, (float)i)) {
            if(isEnderman) {
                return;
            }
            if(entity instanceof LivingEntity livingEntity) {
                if(this.getPunch() > 0) {
                    double d = Math.max(0.0, 1.0 - livingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                    Vec3d vec3d = this.getVelocity().multiply(1.0, 0.0, 1.0).normalize().multiply((double)this.getPunch() * 0.6 * d);
                    if(vec3d.lengthSquared() > 0.0) {
                        livingEntity.addVelocity(vec3d.x, 0.1, vec3d.z);
                    }
                }
                if(owner instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(livingEntity, owner);
                    EnchantmentHelper.onTargetDamaged((LivingEntity)owner, livingEntity);
                }
                this.onHit(livingEntity);
            }
        }
        if (entity instanceof LivingEntity) {
            IRicochet.onEntityHit(this, entity);
        }
        float g = 1.0f;
        if (this.world instanceof ServerWorld && this.world.isThundering() && this.hasChanneling()) {
            BlockPos blockPos = entity.getBlockPos();
            if (this.world.isSkyVisible(blockPos)) {
                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(this.world);
                lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
                lightningEntity.setChanneler(owner instanceof ServerPlayerEntity ? (ServerPlayerEntity)owner : null);
                this.world.spawnEntity(lightningEntity);
                soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER;
                g = 5.0F;
            }
        }
        this.playSound(soundEvent, g, 1.0f);

    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        this.blockHit = true;
        IRicochet.onBlockHit(this, blockHitResult);
        super.onBlockHit(blockHitResult);
    }

    public boolean damage(DamageSource source, float amount) {
        if (this.getDamage() >= this.mjolnirStack.getMaxDamage() -1) {
            this.setDamage(this.mjolnirStack.getMaxDamage() -1);
        }
        if (source == DamageSource.CACTUS) {
            this.world.breakBlock(this.getBlockPos(), true, this);
            this.toBlock(this.getBlockPos());
        }
        return true;

    }

    private void toBlock(BlockPos pos) {
        this.world.setBlockState(pos, ((MjolnirItem)this.mjolnirStack.getItem()).getBlock().getDefaultState().with(MjolnirBlock.FACING, this.getHorizontalFacing()));
        if (this.world.getBlockEntity(this.getBlockPos()) instanceof MjolnirBlockEntity blockEntity) {
            blockEntity.setDamage((int) this.getDamage());
        }
        this.discard();
    }

    public boolean hasChanneling() { return EnchantmentHelper.hasChanneling(this.mjolnirStack); }

    public double getRicochetRange() { return 2 + this.getDataTracker().get(RICOCHET) * 3; }

    public int getBounces() { return this.bounces; }

    public List<Entity> getRicochetHitEntities() { return this.ricochetHitEntities; }

    public int getRemainingBounces() { return this.remainingBounces; }

    public void setRemainingBounces(int value) { this.remainingBounces = value; }

    public boolean getBlockHit() { return this.blockHit; }

    public int getHangTime() { return this.hangTime; }

    public void setHangTime(int value) { this.hangTime = value; }


    public int getInGroundTime() { return this.inGroundTime; }

    public void setInGroundTime(int value) { this.inGroundTime = value; }

    public boolean getDealtDamage() { return this.dealtDamage; }

    public DataTracker getDataTracker() { return dataTracker; }

    public TrackedData<Integer> getLoyalty() { return LOYALTY; }

    public TrackedData<Integer> getRicochet() { return RICOCHET; }

    public int getReturnTimer() { return this.returnTimer; }

    public void setReturnTimer(int value) { this.returnTimer = value; }

    public int getWait() { return this.wait; }

    public void setWait(int value) { this.wait =value; }


    @Override
    public ItemStack asItemStack() { return this.mjolnirStack.copy(); }

    public boolean isEnchanted() { return this.dataTracker.get(ENCHANTED); }

    static {
        RICOCHET = DataTracker.registerData(MjolnirEntity.class, TrackedDataHandlerRegistry.INTEGER);
        LOYALTY = DataTracker.registerData(MjolnirEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ENCHANTED = DataTracker.registerData(MjolnirEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
