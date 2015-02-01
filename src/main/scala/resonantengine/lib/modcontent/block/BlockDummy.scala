package resonantengine.lib.modcontent.block

import java.util.{ArrayList, List, Random}

import nova.core.util.transform.{Cuboid, Vector3d}
import resonantengine.lib.render.wrapper.BlockRenderHandler
import resonantengine.lib.utility.inventory.InventoryUtility

class BlockDummy(val modPrefix: String, val defaultTab: CreativeTabs, val dummyTile: ResonantBlock) extends Block(dummyTile.material) with ITileEntityProvider
{
  dummyTile.domain = modPrefix
  setBlockName(modPrefix + dummyTile.name)
  setBlockTextureName(modPrefix + dummyTile.textureName)

  if (dummyTile.isCreativeTabSet)
  {
    setCreativeTab(dummyTile.creativeTab)
  }
  else
  {
    setCreativeTab(defaultTab)
  }

  dummyTile.bounds.setBounds(this)
  opaque = isOpaqueCube
  setLightOpacity(if (isOpaqueCube) 255 else 0)
  setHardness(dummyTile.blockHardness)
  setResistance(dummyTile.blockResistance)
  setTickRandomly(dummyTile.tickRandomly)
  setStepSound(dummyTile.stepSound)

  override def hasTileEntity(metadata: Int): Boolean =
  {
    return dummyTile.tile != null
  }

  override def createNewTileEntity(var1: World, var2: Int): TileEntity =
  {
    try
    {
      val tile = dummyTile.tile.getClass.newInstance
      tile.block = this
      return tile
    }
    catch
      {
        case e: Exception =>
        {
          e.printStackTrace
        }
      }
    return null
  }

  override def fillWithRain(world: World, x: Int, y: Int, z: Int)
  {
    inject(world, x, y, z)
    getTile(world, x, y, z).onFillRain
    eject
  }

  override def getExplosionResistance(entity: Entity): Float = dummyTile.getExplosionResistance(entity)

  override def getExplosionResistance(entity: Entity, world: World, x: Int, y: Int, z: Int, explosionX: Double, explosionY: Double, explosionZ: Double): Float =
  {
    inject(world, x, y, z)
    val resistance = getTile(world, x, y, z).getExplosionResistance(entity, new Vector3d(explosionX, explosionY, explosionZ))
    eject

    return resistance
  }

  /**
   * Injects and ejects data from the TileEntity.
   */
  def inject(access: IBlockAccess, x: Int, y: Int, z: Int)
  {
    if (access.isInstanceOf[World])
    {
      dummyTile.world(access.asInstanceOf[World])
    }

    dummyTile._access = access
    dummyTile.xCoord = x
    dummyTile.yCoord = y
    dummyTile.zCoord = z

    val tile: TileEntity = access.getTileEntity(x, y, z)

    if (tile.isInstanceOf[ResonantBlock])
    {
      (tile.asInstanceOf[ResonantBlock]).block = this
    }
  }

  def eject()
  {
    dummyTile.world(null)
    dummyTile.xCoord = 0
    dummyTile.yCoord = 0
    dummyTile.zCoord = 0
  }

  def getTile(world: IBlockAccess, x: Int, y: Int, z: Int): ResonantBlock =
  {
    val tile: TileEntity = world.getTileEntity(x, y, z)
    if (tile.isInstanceOf[ResonantBlock])
    {
      return tile.asInstanceOf[ResonantBlock]
    }
    return dummyTile
  }

  override def onBlockClicked(world: World, x: Int, y: Int, z: Int, player: EntityPlayer)
  {
    inject(world, x, y, z)
    getTile(world, x, y, z).click(player)
    eject
  }

  override def onBlockAdded(world: World, x: Int, y: Int, z: Int)
  {
    inject(world, x, y, z)
    getTile(world, x, y, z).onAdded
    eject
  }

  override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, entityLiving: EntityLivingBase, itemStack: ItemStack)
  {
    inject(world, x, y, z)
    getTile(world, x, y, z).onPlaced(entityLiving, itemStack)
    eject()
  }

  override def onPostBlockPlaced(world: World, x: Int, y: Int, z: Int, metadata: Int)
  {
    inject(world, x, y, z)
    getTile(world, x, y, z).onPostPlaced(metadata)
    eject()
  }

  /**
   * Called upon the block being destroyed by an explosion
   */
  override def onBlockDestroyedByExplosion(world: World, x: Int, y: Int, z: Int, ex: Explosion)
  {
    inject(world, x, y, z)
    getTile(world, x, y, z).onDestroyedByExplosion(ex)
    eject()
  }

  override def breakBlock(world: World, x: Int, y: Int, z: Int, block: Block, par6: Int)
  {
    inject(world, x, y, z)
    getTile(world, x, y, z).onRemove(block, par6)
    eject
    super.breakBlock(world, x, y, z, block, par6)
  }

  override def quantityDropped(meta: Int, fortune: Int, random: Random): Int =
  {
    return dummyTile.quantityDropped(meta, fortune)
  }

  override def onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block)
  {
    inject(world, x, y, z)
    getTile(world, x, y, z).onNeighborChanged(block)
    eject
  }

  override def onNeighborChange(world: IBlockAccess, x: Int, y: Int, z: Int, tileX: Int, tileY: Int, tileZ: Int)
  {
    inject(world, x, y, z)
    getTile(world, x, y, z).onNeighborChanged(new Vector3d(tileX, tileY, tileZ))
    eject
  }

  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean =
  {
    inject(world, x, y, z)
    val value: Boolean = getTile(world, x, y, z).activate(player, side, new Vector3d(hitX, hitY, hitZ))
    eject
    return value
  }

  override def updateTick(world: World, x: Int, y: Int, z: Int, par5Random: Random)
  {
    inject(world, x, y, z)
    getTile(world, x, y, z).blockUpdate()
    eject
  }

  @SideOnly(Side.CLIENT)
  override def randomDisplayTick(world: World, x: Int, y: Int, z: Int, par5Random: Random)
  {
    inject(world, x, y, z)
    getTile(world, x, y, z).randomDisplayTick()
    eject
  }

  override def onEntityCollidedWithBlock(world: World, x: Int, y: Int, z: Int, entity: Entity)
  {
    inject(world, x, y, z)
    getTile(world, x, y, z).collide(entity)
    eject
  }

  override def addCollisionBoxesToList(world: World, x: Int, y: Int, z: Int, aabb: AxisAlignedBB, list: List[_], entity: Entity)
  {
    inject(world, x, y, z)

    val bounds: Iterable[Cuboid] = getTile(world, x, y, z).getCollisionBoxes(if (aabb != null) (new Cuboid(aabb) - new Vector3d(x, y, z)) else null, entity)

    if (bounds != null)
    {
      for (cuboid <- bounds)
      {
        list.add((cuboid + new Vector3d(x, y, z)).toAABB)
      }
    }

    eject
  }

  @SideOnly(Side.CLIENT)
  override def getSelectedBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB =
  {
    inject(world, x, y, z)
    val value = getTile(world, x, y, z).getSelectBounds + new Vector3d(x, y, z)
    eject
    return value.toAABB
  }

  override def getCollisionBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB =
  {
    inject(world, x, y, z)
    val value = getTile(world, x, y, z).getCollisionBounds + new Vector3d(x, y, z)
    eject
    return value.toAABB
  }

  override def shouldSideBeRendered(access: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Boolean =
  {
    return dummyTile.shouldSideBeRendered(access, x, y, z, side)
  }

  override def isBlockSolid(access: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Boolean =
  {
    inject(access, x, y, z)
    val value: Boolean = getTile(access, x, y, z).isSolid(access, side)
    eject
    return value
  }

  override def getLightValue(access: IBlockAccess, x: Int, y: Int, z: Int): Int =
  {
    var value: Int = 0
    if (access != null)
    {
      inject(access, x, y, z)
      value = getTile(access, x, y, z).getLightValue(access)
      eject
    }
    return value
  }

  override def hasComparatorInputOverride: Boolean = dummyTile.isInstanceOf[ResonantBlock.IComparatorInputOverride]

  override def isOpaqueCube: Boolean = dummyTile == null || dummyTile.isOpaqueCube

  override def renderAsNormalBlock: Boolean = dummyTile.normalRender

  @SideOnly(Side.CLIENT)
  override def getRenderType: Int = if (!dummyTile.normalRender) BlockRenderHandler.ID else 0

  @SideOnly(Side.CLIENT)
  override def getIcon(access: IBlockAccess, x: Int, y: Int, z: Int, side: Int): IIcon =
  {
    inject(access, x, y, z)
    val value: IIcon = getTile(access, x, y, z).getIcon(access, side)
    eject
    return value
  }

  @SideOnly(Side.CLIENT)
  override def getIcon(side: Int, meta: Int): IIcon = dummyTile.getIcon(side, meta)

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(iconRegister: IIconRegister)
  {
    dummyTile.registerIcons(iconRegister)
  }

  @SideOnly(Side.CLIENT)
  override def colorMultiplier(access: IBlockAccess, x: Int, y: Int, z: Int): Int =
  {
    inject(access, x, y, z)
    val value = getTile(access, x, y, z).colorMultiplier
    eject()
    return value
  }

  override def getPickBlock(target: MovingObjectPosition, world: World, x: Int, y: Int, z: Int): ItemStack =
  {
    inject(world, x, y, z)
    val value: ItemStack = getTile(world, x, y, z).getPickBlock(target)
    eject()
    return value
  }

  override def getDrops(world: World, x: Int, y: Int, z: Int, metadata: Int, fortune: Int): ArrayList[ItemStack] =
  {
    inject(world, x, y, z)
    val value: ArrayList[ItemStack] = getTile(world, x, y, z).getDrops(metadata, fortune)
    eject()
    return if (value != null) value else new ArrayList[ItemStack]
  }

  override def getSubBlocks(item: Item, creativeTabs: CreativeTabs, list: List[_])
  {
    dummyTile.getSubBlocks(item, creativeTabs, list)
  }

  /**
   * Redstone interaction
   */
  override def canProvidePower: Boolean = dummyTile.providePower

  override def isProvidingWeakPower(access: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Int =
  {
    inject(access, x, y, z)
    val value: Int = getTile(access, x, y, z).getWeakRedstonePower(access, side)
    eject
    return value
  }

  override def isProvidingStrongPower(access: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Int =
  {
    inject(access, x, y, z)
    val value: Int = getTile(access, x, y, z).getStrongRedstonePower(access, side)
    eject
    return value
  }

  override def getRenderBlockPass: Int = dummyTile.getRenderBlockPass

  override def tickRate(world: World): Int = dummyTile.tickRate(world)

  override def setBlockBoundsBasedOnState(access: IBlockAccess, x: Int, y: Int, z: Int)
  {
    inject(access, x, y, z)
    getTile(access, x, y, z).setBlockBoundsBasedOnState()
    eject()
  }

  override def setBlockBoundsForItemRender() =
  {
    dummyTile.setBlockBoundsForItemRender()
  }

  protected override def dropBlockAsItem(world: World, x: Int, y: Int, z: Int, itemStack: ItemStack)
  {
    if (!world.isRemote && world.getGameRules.getGameRuleBooleanValue("doTileDrops"))
    {
      InventoryUtility.dropItemStack(world, new Vector3d(x, y, z) + 0.5, itemStack)
    }
  }
}