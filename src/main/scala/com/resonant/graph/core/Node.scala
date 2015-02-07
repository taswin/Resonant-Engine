package com.resonant.graph.core

import net.minecraft.entity.Entity
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * A node is any single part of a grid.
 * @author Calclavia
 */
abstract class Node(var parent: INodeProvider) extends INode {
	override def reconstruct() {
	}

	override def deconstruct() {
	}

	override def getParent: INodeProvider = parent

	def position = new VectorWorld(world, x, y, z)

	def x: Double = {
		return parent match {
			case x: TileEntity => x.xCoord
			case x: Entity => x.posX
			case _ => 0
		}
	}

	def y: Double = {
		return parent match {
			case x: TileEntity => x.yCoord
			case x: Entity => x.posY
			case _ => 0
		}
	}

	def z: Double = {
		return parent match {
			case x: TileEntity => x.zCoord
			case x: Entity => x.posZ
			case _ => 0
		}
	}

	def world: World = {
		return parent match {
			case x: TileEntity => x.getWorldObj
			case x: Entity => x.worldObj
			case _ => null
		}
	}

	override def toString: String = getClass.getSimpleName + "[" + hashCode + "]"
}