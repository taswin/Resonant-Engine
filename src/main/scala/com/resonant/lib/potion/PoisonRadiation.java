package com.resonant.lib.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import nova.core.util.transform.Vector3d;

public class PoisonRadiation extends Poison {
	public static final Poison INSTANCE = new PoisonRadiation("radiation");
	public static final DamageSource damageSource = new DamageSource("radiation").setDamageBypassesArmor();
	public static boolean disabled = false;

	public PoisonRadiation(String name) {
		super(name);
	}

	@Override
	public boolean isEntityProtected(Vector3d emitPosition, EntityLivingBase entity, int amplifier) {
		return (emitPosition != null ? this.getAntiPoisonBlockCount(entity.worldObj, emitPosition, new Vector3d(entity)) <= amplifier :
			false) && super.isEntityProtected(emitPosition, entity, amplifier);
	}

	@Override
	protected void doPoisonEntity(Vector3d emitPosition, EntityLivingBase entity, int amplifier) {
		if (!PoisonRadiation.disabled) {
			entity.addPotionEffect(new CustomPotionEffect(PotionRadiation.INSTANCE.getId(), 20 * 15 * (amplifier + 1), amplifier, null));
		}
	}

}
