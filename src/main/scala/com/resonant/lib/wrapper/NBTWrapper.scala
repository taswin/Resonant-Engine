package com.resonant.lib.wrapper

import net.minecraft.nbt.{NBTTagCompound, NBTTagList}

import scala.reflect.ClassTag

/**
 * @author Calclavia
 */
object NBTWrapper {

	implicit class WrappedNBT(underlying: NBTTagCompound) {
		def get(key: String) = NBTUtility.loadObject(underlying, key)

		def set(key: String, value: Any) {
			NBTUtility.saveObject(underlying, key, value)
		}

		def getArray[T: ClassTag](name: String): Array[T] = {
			val tagList = underlying.getTagList(name, 10)
			var seq = Seq.empty[T]

			for (i <- 0 until tagList.tagCount) {
				val innerTag = tagList.getCompoundTagAt(i)

				if (NBTUtility.loadObject(innerTag, "value").isInstanceOf[T]) {
					seq :+= NBTUtility.loadObject(innerTag, "value").asInstanceOf[T]
				}
				else {
					seq :+= null.asInstanceOf[T]
				}
			}

			return seq.toArray
		}

		def setArray(name: String, arr: Array[_]) {
			val tagList: NBTTagList = new NBTTagList

			for (i <- 0 until arr.length) {
				val innerTag = new NBTTagCompound
				if (arr(i) != null) {
					NBTUtility.saveObject(innerTag, "value", arr(i))
				}
				tagList.appendTag(innerTag)
			}

			underlying.setTag(name, tagList)
		}

		def getMap[K: ClassTag, V: ClassTag](name: String): Map[K, V] = {
			val tagList = underlying.getTagList(name, 10)
			var map = Map.empty[K, V]

			for (i <- 0 until tagList.tagCount) {
				val innerTag = tagList.getCompoundTagAt(i)

				if (NBTUtility.loadObject(innerTag, "k").isInstanceOf[K] && NBTUtility.loadObject(innerTag, "v").isInstanceOf[V]) {
					map += NBTUtility.loadObject(innerTag, "k").asInstanceOf[K] -> NBTUtility.loadObject(innerTag, "v").asInstanceOf[V]
				}
			}

			return map
		}

		def setMap(name: String, map: Map[_, _]) {
			val tagList: NBTTagList = new NBTTagList

			map.foreach(
				keyVal => {
					val innerTag = new NBTTagCompound
					NBTUtility.saveObject(innerTag, "k", keyVal._1)
					NBTUtility.saveObject(innerTag, "v", keyVal._2)
					tagList.appendTag(innerTag)
				}
			)

			underlying.setTag(name, tagList)
		}
	}

}
