package resonantengine.lib.poison;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import resonantengine.lib.prefab.potion.CustomPotionEffect;
import resonantengine.lib.transform.vector.Vector3;

public class PoisonRadiation extends Poison
{
	public static final Poison INSTANCE = new PoisonRadiation("radiation");
	public static final DamageSource damageSource = new DamageSource("radiation").setDamageBypassesArmor();
	public static boolean disabled = false;

	public PoisonRadiation(String name)
	{
		super(name);
	}

	@Override
	public boolean isEntityProtected(Vector3 emitPosition, EntityLivingBase entity, int amplifier)
	{
		return (emitPosition != null ? this.getAntiPoisonBlockCount(entity.worldObj, emitPosition, new Vector3(entity)) <= amplifier : false) && super.isEntityProtected(emitPosition, entity, amplifier);
	}

	@Override
	protected void doPoisonEntity(Vector3 emitPosition, EntityLivingBase entity, int amplifier)
	{
		if (!PoisonRadiation.disabled)
		{
			entity.addPotionEffect(new CustomPotionEffect(PotionRadiation.INSTANCE.getId(), 20 * 15 * (amplifier + 1), amplifier, null));
		}
	}

}
