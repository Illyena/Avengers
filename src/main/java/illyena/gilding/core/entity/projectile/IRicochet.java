package illyena.gilding.core.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Predicate;

/** <pre>
 * INSERT
 *
 * insert fields:
 *      {@code private static final TrackedData<Byte> RICOCHET;
 * private static final TrackedData<Boolean> ENCHANTED;
 *
 * private int bounces;
 * private List<Entity> ricochetHitEntities = Lists.newArrayListWithCapacity(bounces);
 * private int remainingBounces;}
 *
 * insert at init:
 *      {@code this.bounces = GildingEnchantmentHelper.getRicochet(this.capShieldStack) * 2;
 *          this.dataTracker.set(RICOCHET,
 *              (byte)GildingEnchantmentHelper.getRicochet(stack));
 *          this.dataTracker.set(LOYALTY, (byte) EnchantmentHelper.getLoyalty(stack));
 *          this.dataTracker.set(ENCHANTED, stack.hasGlint());}
 *
 * insert at #initDataTracker
 *          {@code this.dataTracker.startTracking(RICOCHET, (byte) 0);
 * this.dataTracker.startTracking(LOYALTY, (byte) 0);
 * this.dataTracker.startTracking(ENCHANTED, false);}
 *
 * insert at end of #onEntityHit:
 *      {@code if (IRicochet.nextRicochetTarget(this, (LivingEntity) entity)
 *                  != null && this.remainingBounces > 0) {
 *             IRicochet.ricochet(this, (LivingEntity) entity);
 *         } else {
 *             this.setNoGravity(false);
 *             this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
 *         }}
 * </pre>
 */

public interface IRicochet {
    abstract double getRicochetRange();

    abstract int getBounces();

    abstract List<Entity> getRicochetHitEntities();

    abstract int getRemainingBounces();

    abstract void setRemainingBounces(int value);

    abstract boolean getBlockHit();

    public abstract DataTracker getDataTracker();

    public abstract TrackedData<Byte> getRicochet();

    abstract int getHangTime();

    public abstract void setHangTime(int value);



    static PersistentProjectileEntity getProjectile (PersistentProjectileEntity projectile) {
        if (projectile instanceof IRicochet) {
            return projectile;
        }
        return null;
    }

    static void tick(PersistentProjectileEntity projectile) {
        int i = ((IRicochet)getProjectile(projectile)).getDataTracker().get((((IRicochet)getProjectile(projectile)).getRicochet()));
        if (i > 0 && ((IRicochet)getProjectile(projectile)).getRicochetHitEntities().size() > 0 && !projectile.isOnGround()) {
            ((IRicochet)getProjectile(projectile)).setHangTime(((IRicochet)getProjectile(projectile)).getHangTime() + 1);
            if (((IRicochet)getProjectile(projectile)).getHangTime() > 90) {
                projectile.setNoClip(false);
                projectile.setNoGravity(false);
                boolean bl = MathHelper.sign(projectile.getVelocity().getY()) == -1;
                projectile.setVelocity(projectile.getVelocity().multiply(-0.01, bl ? 1 : -1.0, -0.01));
            }

        }
    }

    static void ricochet(PersistentProjectileEntity projectile, LivingEntity entity) {
        float k = 0.1f; //1.0f; todo velocity multiplier
        LivingEntity nextTarget = nextRicochetTarget(projectile, entity);
        Vec3d nextTargetPos = nextTarget.getPos();
        Vec3d vec3d = new Vec3d(nextTargetPos.x - projectile.getPos().x, nextTargetPos.y - projectile.getPos().y, nextTargetPos.z - projectile.getPos().z);
        vec3d = vec3d.multiply(k);
        projectile.setVelocity(vec3d);
        projectile.setNoGravity(true);
    }

    static LivingEntity nextRicochetTarget(PersistentProjectileEntity projectile, LivingEntity livingEntity) {
        return projectile.world.getClosestEntity(LivingEntity.class, TargetPredicate.createAttackable().setPredicate(Predicate.not(Predicate.isEqual(projectile.getOwner()))), livingEntity,
                livingEntity.getPos().x, livingEntity.getPos().y, livingEntity.getPos().z, livingEntity.getBoundingBox().expand(((IRicochet)getProjectile(projectile)).getRicochetRange()));
    }

    static void onEntityHit (PersistentProjectileEntity projectile, Entity entity) {
        ((IRicochet)getProjectile(projectile)).getRicochetHitEntities().add(entity);
        ((IRicochet)getProjectile(projectile)).setRemainingBounces(((IRicochet)getProjectile(projectile)).getBounces() - ((IRicochet)getProjectile(projectile)).getRicochetHitEntities().size());
        if (IRicochet.nextRicochetTarget(projectile, (LivingEntity) entity) != null && ((IRicochet)getProjectile(projectile)).getRemainingBounces() > 0) {
            IRicochet.ricochet(projectile, (LivingEntity) entity);
        } else {
            projectile.setNoGravity(false);
            projectile.setVelocity(projectile.getVelocity().multiply(-0.01, -0.1, -0.01));
        }
    }

    static void onBlockHit(PersistentProjectileEntity projectile, BlockHitResult blockHitResult) {
        ((IRicochet)getProjectile(projectile)).setHangTime(0);
    }

}
