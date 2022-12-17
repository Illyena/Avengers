package illyena.gilding.core.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

/**
 * <pre>
 * INSERT
 *
 * insert fields:
 *      {@code private static final TrackedData<Byte> LOYALTY;
 * private static final TrackedData<Boolean> ENCHANTED;
 * private boolean dealtDamage;
 * private int returnTimer;}
 *
 * insert at init:
 *          {@code this.dataTracker.set(LOYALTY, (byte)EnchantmentHelper.getLoyalty(stack));
 * this.dataTracker.set(ENCHANTED, stack.hasGlint());}
 *
 * insert at #initDataTracker():
 *          {@code this.dataTracker.startTracking(LOYALTY, (byte)0);
 * this.dataTracker.startTracking(ENCHANTED, false);}
 *</pre>
 */
public interface ILoyalty {
    public abstract int getInGroundTime();

    public abstract void setInGroundTime(int value);

    public abstract ItemStack asItemStack();

    public abstract DataTracker getDataTracker();

    public abstract TrackedData<Byte> getLoyalty();

    public abstract boolean getDealtDamage();

    public abstract int getReturnTimer();

    public abstract void setReturnTimer(int value);

    public abstract int getWait();

    public abstract void setWait(int value);

    public static PersistentProjectileEntity getProjectile (PersistentProjectileEntity projectile) {
        if (projectile instanceof ILoyalty) {
            return projectile;
        }
        return null;
    }

    public static void tick(PersistentProjectileEntity projectile) {
        Entity entity = projectile.getOwner();
        int i = ((ILoyalty)getProjectile(projectile)).getDataTracker().get((((ILoyalty) getProjectile(projectile)).getLoyalty()));
        if (i > 0 && (((ILoyalty)getProjectile(projectile)).getDealtDamage() || projectile.isNoClip()) && entity != null) {
            if (((IRicochet)getProjectile(projectile)).getBlockHit()) {
                ((ILoyalty) getProjectile(projectile)).setInGroundTime((((ILoyalty) getProjectile(projectile)).getInGroundTime() + 1));
            }

            if (!projectile.world.isClient() && shouldReturn(projectile)){
                if (!isOwnerAlive(projectile)) {
                    if (!projectile.world.isClient && projectile.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
                        projectile.dropStack(((ILoyalty) getProjectile(projectile)).asItemStack(), 0.1F);
                    }

                    projectile.discard();
                } else {
                    projectile.setNoClip(true);
                    Vec3d vec3d = entity.getEyePos().subtract(projectile.getPos());
                    projectile.setPos(projectile.getX(), projectile.getY() + vec3d.y * 0.015 * (double) i, projectile.getZ());
                    if (projectile.world.isClient) {
                        projectile.lastRenderY = projectile.getY();
                    }

                    double d = 0.05 * (double) i;
                    projectile.setVelocity(projectile.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(d)));
                    if (((ILoyalty) getProjectile(projectile)).getReturnTimer() == 0) {
                        projectile.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
                    }

                    ((ILoyalty) getProjectile(projectile)).setReturnTimer(((ILoyalty) getProjectile(projectile)).getReturnTimer() + 1);
                }
            }
        }
    }

    public static boolean isOwnerAlive(PersistentProjectileEntity projectile) {
        Entity entity = getProjectile(projectile).getOwner();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
        } else {
            return false;
        }
    }

    public static boolean shouldReturn(PersistentProjectileEntity projectile) {
        int i = ((ILoyalty) getProjectile(projectile)).getDataTracker().get((((ILoyalty) getProjectile(projectile)).getLoyalty()));
        if (i > 0) {
            ((ILoyalty) getProjectile(projectile)).setWait(((ILoyalty) getProjectile(projectile)).getWait() + 1);
            int delay = 90 / ((ILoyalty) getProjectile(projectile)).getDataTracker().get((((ILoyalty) getProjectile(projectile)).getLoyalty()));
            if (projectile instanceof IRicochet && ((IRicochet) projectile).getRemainingBounces() == 0) return true;
            else if (projectile instanceof IRicochet && !((IRicochet) projectile).getBlockHit()) {
                return ((ILoyalty) getProjectile(projectile)).getWait() > delay + 30 * ((ILoyalty) getProjectile(projectile)).getDataTracker().get(((ILoyalty) getProjectile(projectile)).getLoyalty());

            } else return ((ILoyalty) getProjectile(projectile)).getWait() > delay;
        } else return false;
    }


}
