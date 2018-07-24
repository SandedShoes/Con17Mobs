package me.sub.common.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class EntityMobA extends EntityMob implements IRangedAttackMob {

    private static final DataParameter<Boolean> HAS_GRABBED = EntityDataManager.createKey(EntityMobA.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(EntityMobA.class, DataSerializers.VARINT);


    public EntityMobA(World worldIn) {
        super(worldIn);
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(9, new EntityAILookIdle(this));

        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 100, true, false, input -> !(input instanceof EntityMobA)));
        this.tasks.addTask(0, new EntityAIAttackRanged(this, 1, 11,30));

        EntityAIMoveTowardsRestriction movingTask = new EntityAIMoveTowardsRestriction(this, 1.0D);
        this.tasks.addTask(5, movingTask);
        movingTask.setMutexBits(3);

    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(HAS_GRABBED, false);
        getDataManager().register(TARGET_ENTITY, -1);
    }


    private void setTarget(int entityId) {
        dataManager.set(TARGET_ENTITY, entityId);
    }

    public int getTarget() {
        return dataManager.get(TARGET_ENTITY);
    }

        @Override
        public void onUpdate() {
        super.onUpdate();

            /*
             * Adding blindness to the player if they within 2 blocks
             */
        for(Entity entity : world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(50))) {
            if(entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if(player.getDistanceSqToEntity(this) < 2 && !player.isCreative()) {
                    player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 600));
                }
            }

        }
    }


    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    @Override
    public boolean getCanSpawnHere()
    {
        return this.posY > 45.0D && this.posY < (double)this.world.getSeaLevel() && super.getCanSpawnHere();
    }


    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

    }

    /**
     * Attack the specified entity using a ranged attack.
     *
     * @param target
     * @param distanceFactor
     */
    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        if (getDistanceSqToEntity(target) < 10) {
            setTarget(target.getEntityId());
            bringInHookedEntity(target);
          } else {
            setTarget(-1);
        }

    }

    public void bringInHookedEntity(Entity caughtEntity)
    {
            double d0 = posX - caughtEntity.posX;
            double d1 = posY - caughtEntity.posY;
            double d2 = posZ - caughtEntity.posZ;
            caughtEntity.motionX += d0 * 0.5D;
            caughtEntity.motionY += d1 * 0.5D;
            caughtEntity.motionZ += d2 * 0.5D;
            System.out.println("tried to move: " + caughtEntity.getName());
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
        /**
         * This is essentially just for mobs swinging their arms, we have no use for this here.
         */
    }
}
