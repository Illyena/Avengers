package illyena.gilding.avengers.entity.projectile;

import com.google.common.collect.Lists;
import illyena.gilding.avengers.advancement.AvengersAdvancements;
import illyena.gilding.avengers.entity.AvengersEntities;
import illyena.gilding.avengers.item.AvengersItems;
import illyena.gilding.core.enchantment.GildingEnchantmentHelper;
import illyena.gilding.core.entity.projectile.ILoyalty;
import illyena.gilding.core.entity.projectile.IRicochet;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class CapShieldEntity extends PersistentProjectileEntity implements IRicochet, ILoyalty {
    private static final TrackedData<Integer> RICOCHET;
    private static final TrackedData<Integer> LOYALTY;
    private static final TrackedData<Boolean> ENCHANTED;
    private ItemStack capShieldStack;

    private int bounces;
    private List<Entity> ricochetHitEntities = Lists.newArrayListWithCapacity(bounces);
    private int remainingBounces;
    private int hangTime;

    private boolean dealtDamage;
    private boolean blockHit;
    private int returnTimer;
    private int wait;

    public CapShieldEntity(EntityType<? extends CapShieldEntity> entityType, World world) {
        super(entityType, world);
        this.capShieldStack = new ItemStack(AvengersItems.CAP_SHIELD);
        this.setDamage(this.capShieldStack.getDamage());
        this.bounces = GildingEnchantmentHelper.getRicochet(this.capShieldStack) * 2;
        this.blockHit = false;
    }

    public CapShieldEntity(World world, LivingEntity owner, ItemStack stack) {
        super(AvengersEntities.CAP_SHIELD_ENTITY_TYPE, owner, world);
        this.capShieldStack = new ItemStack(AvengersItems.CAP_SHIELD);
        this.capShieldStack = stack.copy();
        this.bounces = GildingEnchantmentHelper.getRicochet(this.capShieldStack) * 2;
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
        if (nbt.contains("Cap_Shield", 10)) {
            this.capShieldStack = ItemStack.fromNbt(nbt.getCompound("Cap_Shield"));
        }

        this.dealtDamage = nbt.getBoolean("DealtDamage");
        this.dataTracker.set(LOYALTY, EnchantmentHelper.getLoyalty(this.capShieldStack));
        this.dataTracker.set(RICOCHET, GildingEnchantmentHelper.getRicochet(this.capShieldStack));
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("Cap_Shield", this.capShieldStack.writeNbt(new NbtCompound()));
        nbt.putBoolean("DealtDamage", this.dealtDamage);
    }

    @Override
    public void tick() {
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

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();

        float damage = (float)this.getVelocity().length();
        int i = MathHelper.ceil(MathHelper.clamp((double)damage * getDamage(), 0.0, 2.147483647E9));
        if(this.isCritical()) {
            long l = this.random.nextInt(i / 2 + 2);
            i = (int)Math.min(l + (long)i, 2147483647L);
        }
        if(entity instanceof LivingEntity livingEntity) {
            i += EnchantmentHelper.getAttackDamage(this.capShieldStack, livingEntity.getGroup());
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
        this.playSound(soundEvent, 1.0f, 1.0f);

    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        this.blockHit = true;
        IRicochet.onBlockHit(this, blockHitResult);
        super.onBlockHit(blockHitResult);
    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            int i;
            if (amount > 0) {
                i = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, this.capShieldStack);
                int j = 0;
                if (i > 0) {
                    for (int k = 0; k < amount; k++) {
                        if (UnbreakingEnchantment.shouldPreventDamage(this.capShieldStack, i, random)) {
                            j++;
                        }
                    }
                }
                amount -= j;
                if (amount <= 0) {
                    return false;
                }
            }
            i =  this.capShieldStack.getDamage() + (int)amount;
            this.capShieldStack.setDamage(Math.min(i, this.capShieldStack.getMaxDamage() - 1));

            if (source == DamageSource.CACTUS) {
                this.world.breakBlock(this.getBlockPos(), true, this);
            }
            return true;
        }
    }

    protected boolean tryPickup(PlayerEntity player) {
        if (super.tryPickup(player)) {
            if (player instanceof ServerPlayerEntity serverPlayer && !this.ricochetHitEntities.isEmpty()) {
                AvengersAdvancements.RICOCHET_AND_RETURN.trigger(serverPlayer, this.asItemStack(), DamageSource.thrownProjectile(this, player), this.getRicochetHitEntities());
            }
            return true;
        } else return false;
    }

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

    public void setWait(int value) { this.wait = value; }


    @Override
    public ItemStack asItemStack() { return this.capShieldStack.copy(); }

    public boolean isEnchanted() { return this.dataTracker.get(ENCHANTED); }

    static {
        RICOCHET = DataTracker.registerData(CapShieldEntity.class, TrackedDataHandlerRegistry.INTEGER);
        LOYALTY = DataTracker.registerData(CapShieldEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ENCHANTED = DataTracker.registerData(CapShieldEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
//todo Sounds
