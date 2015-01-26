package resonantengine.lib.factory.resources

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.oredict.OreDictionary
import resonantengine.core.content.ResonantContent
import resonantengine.lib.factory.resources.block.{TBlockResource, TileOre}
import resonantengine.lib.factory.resources.item.{ItemIngot, TItemResource}
import resonantengine.lib.prefab.tile.spatial.ResonantBlock
import resonantengine.lib.wrapper.StringWrapper._

/**
 * A factor class generates different types of resources based on its material
 *
 * type - Ore? Ingot? Dust? etc.
 *
 * @author Calclavia
 */
object ResourceFactory
{
  /**
   * Reference to color of material
   */
  private var materials = Set.empty[String]
  private var materialColorCache = Map.empty[String, Integer]
  private var resourceBlocks = Map.empty[String, Class[_ <: ResonantBlock]]
  private var resourceItems = Map.empty[String, Class[_ <: Item]]

  private var generatedBlocks = Map.empty[(String, String), Block]
  private var generatedItems = Map.empty[(String, String), Item]

  /**
   * Materials must be first registered before use
   */
  def registerMaterial(material: String)
  {
    materials += material
  }

  def registerMaterialColor(material: String, color: Int)
  {
    if (!materialColorCache.contains(material))
      materialColorCache += material -> color
  }

  def requestBlocks(material: String, except: String*): Map[String, Block] =
  {
    return resourceBlocks.keys.filterNot(except.contains).map(t => (t, requestBlock(t, material))).toMap
  }

  /**
   * Requests a resource block to be generated. Example: dustIron
   * @param material - E.g: iron
   * @param resourceType - E.g: dust
   */
  def requestBlock(resourceType: String, material: String): Block =
  {
    assert(materials.contains(material))
    val newResource = resourceBlocks(resourceType).newInstance()
    newResource.name = resourceType + material.capitalizeFirst
    newResource.asInstanceOf[TBlockResource].resourceMaterial = material

    val result = ResonantContent.manager.newBlock(newResource)
    generatedBlocks += (resourceType, material) -> result

    //Register ore dictionary
    OreDictionary.registerOre(resourceType + material.capitalizeFirst, result)
    return result
  }

  def requestItems(material: String, except: String*): Map[String, Item] =
  {
    return resourceItems.keys.filterNot(except.contains).map(t => (t, requestItem(t, material))).toMap
  }

  def requestItem(resourceType: String, material: String): Item =
  {
    assert(materials.contains(material))
    val newResource = resourceItems(resourceType).newInstance()
    newResource.asInstanceOf[TItemResource].material = material
    val result = ResonantContent.manager.newItem(resourceType + material.capitalizeFirst, newResource)
    generatedItems += (resourceType, material) -> result

    //Register ore dictionary
    OreDictionary.registerOre(resourceType + material.capitalizeFirst, result)
    return result
  }

  def getBlock(resourceType: String, material: String) = generatedBlocks((resourceType, material))

  def getItem(resourceType: String, material: String) = generatedItems((resourceType, material))

  def getMaterial(block: Block) = generatedBlocks.map(keyVal => (keyVal._2, keyVal._1._2)).getOrElse(block, null)

  def getMaterial(item: Item) = generatedItems.map(keyVal => (keyVal._2, keyVal._1._2)).getOrElse(item, null)

  def preInit()
  {
    //By default, we want to register ore resource type and ingot resource type
    registerResourceBlock("ore", classOf[TileOre])
    registerResourceItem("ingot", classOf[ItemIngot])
  }

  def registerResourceBlock(name: String, clazz: Class[_ <: ResonantBlock])
  {
    resourceBlocks += name -> clazz
  }

  def registerResourceItem(name: String, clazz: Class[_ <: Item])
  {
    resourceItems += name -> clazz
  }

  def getColor(name: String): Int = if (materialColorCache.contains(name)) materialColorCache(name) else 0xFFFFFF
}
