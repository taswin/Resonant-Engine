package com.resonant.access

import net.minecraft.nbt.{NBTTagCompound, NBTTagList, NBTTagString}

/**
 * The abstract access class.
 * @author Calclavia
 */
abstract class AbstractAccess {
	var permissions = Set.empty[Permission]

	def fromNBT(nbt: NBTTagCompound) {
		val permList = nbt.getTagList("permissions", 8)
		permissions = ((0 until permList.tagCount()) map (i => Permissions.find(permList.getStringTagAt(i)))).toSet
	}

	def toNBT: NBTTagCompound = {
		val nbt = new NBTTagCompound
		val permList = new NBTTagList()
		permissions foreach (x => permList.appendTag(new NBTTagString(x.toString)))
		nbt.setTag("permissions", permList)
		return nbt
	}

	def hasPermission(username: String, permission: Permission): Boolean
}
