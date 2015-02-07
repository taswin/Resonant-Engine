package com.resonant.lib.potion;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.resonant.core.api.armor.IAntiPoisonArmor;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import nova.core.util.transform.Vector3d;
import resonantengine.api.tile.IAntiPoisonBlock;
import resonantengine.core.Reference;

import java.util.EnumSet;
import java.util.HashMap;

/**
 * A poison registry class used to register different types of poison effects.
 * @author Calclavia
 */
public abstract class Poison {
	static HashMap<String, Poison> poisons = new HashMap();
	static BiMap<String, Integer> poisonIDs = HashBiMap.create();
	private static int maxID = 0;
	protected final boolean isDisabled;
	protected String name;
	protected EnumSet<ArmorType> armorRequired = EnumSet.range(ArmorType.HELM, ArmorType.BOOTS);

	public Poison(String name) {
		this.name = name;
		poisons.put(name, this);
		poisonIDs.put(name, ++maxID);
		isDisabled = Reference.config().get("Disable Poison", "Disable " + this.name, false).getBoolean(false);
	}

	public static Poison getPoison(String name) {
		return poisons.get(name);
	}

	public static Poison getPoison(int id) {
		return poisons.get(getName(id));
	}

	public static String getName(int fluidID) {
		return poisonIDs.inverse().get(fluidID);
	}

	public static int getID(String name) {
		return poisonIDs.get(name);
	}

	public String getName() {
		return this.name;
	}

	public final int getID() {
		return getID(this.getName());
	}

	public EnumSet<ArmorType> getArmorRequired() {
		return this.armorRequired;
	}

	/**
	 * Called to poison this specific entity with this specific type of poison.
	 * @param entity
	 * @amiplifier - The amplification value.
	 * @armorRequired - The amount of pieces of armor required to be protected.
	 */
	public void poisonEntity(Vector3d emitPosition, EntityLivingBase entity, int amplifier) {
		if (!isEntityProtected(emitPosition, entity, amplifier)) {
			doPoisonEntity(emitPosition, entity, amplifier);
		}
	}

	public void poisonEntity(Vector3d emitPosition, EntityLivingBase entity) {
		this.poisonEntity(emitPosition, entity, 0);
	}

	public boolean isEntityProtected(Vector3d emitPosition, EntityLivingBase entity, int amplifier) {
		EnumSet<ArmorType> armorWorn = EnumSet.noneOf(ArmorType.class);

		if (entity instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer) entity;

			for (int i = 0; i < entityPlayer.inventory.armorInventory.length; i++) {
				if (entityPlayer.inventory.armorInventory[i] != null) {
					if (entityPlayer.inventory.armorInventory[i].getItem() instanceof IAntiPoisonArmor) {
						IAntiPoisonArmor armor = (IAntiPoisonArmor) entityPlayer.inventory.armorInventory[i].getItem();

						if (armor.isProtectedFromPoison(entityPlayer.inventory.armorInventory[i], entity, this.getName())) {
							armorWorn.add(ArmorType.values()[armor.getArmorType() % ArmorType.values().length]);
							// TODO: Consider putting this in another method.
							armor.onProtectFromPoison(entityPlayer.inventory.armorInventory[i], entity, this.getName());
						}
					}
				}
			}
		}

		return armorWorn.containsAll(this.armorRequired);
	}

	public int getAntiPoisonBlockCount(World world, Vector3d startingPosition, Vector3d endingPosition) {
		Vector3d delta = endingPosition.clone().subtract(startingPosition).normalize();
		Vector3d targetPosition = startingPosition.clone();
		double totalDistance = startingPosition.distance(endingPosition);

		int count = 0;

		if (totalDistance > 1) {
			while (targetPosition.distance(endingPosition) <= totalDistance) {
				Block block = targetPosition.getBlock(world);

				if (block instanceof IAntiPoisonBlock) {
					if (((IAntiPoisonBlock) block).isPoisonPrevention(world, targetPosition.xi(), targetPosition.yi(), targetPosition.zi(), this.getName())) {
						count++;

					}
				}

				targetPosition.add(delta);
			}
		}

		return count;
	}

	protected abstract void doPoisonEntity(Vector3d emitPosition, EntityLivingBase entity, int amplifier);

	public enum ArmorType {
		HELM,
		BODY,
		LEGGINGS,
		BOOTS
	}
}