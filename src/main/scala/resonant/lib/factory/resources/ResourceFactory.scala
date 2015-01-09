package resonant.lib.factory.resources

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.oredict.OreDictionary
import resonant.engine.ResonantEngine
import resonant.lib.factory.resources.block.{TBlockResource, TileOre}
import resonant.lib.factory.resources.item.{ItemIngot, TItemResource}
import resonant.lib.prefab.tile.spatial.SpatialBlock
import resonant.lib.wrapper.StringWrapper._

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
  private var resourceBlocks = Map.empty[String, Class[_ <: SpatialBlock]]
  private var resourceItems = Map.empty[String, Class[_ <: Item]]

  //Map[(Type, Material), Block/Item]]
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
    newResource.asInstanceOf[TBlockResource].resourceMaterial = material

    val result = ResonantEngine.contentRegistry.newBlock(resourceType + material.capitalizeFirst, newResource)
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
    val result = ResonantEngine.contentRegistry.newItem(resourceType + material.capitalizeFirst, newResource)
    generatedItems += (resourceType, material) -> result

    //Register ore dictionary
    OreDictionary.registerOre(resourceType + material.capitalizeFirst, result)
    return result
  }

  def getBlock(resourceType: String, material: String) = generatedBlocks((resourceType, material))

  def getItem(resourceType: String, material: String) = generatedItems((resourceType, material))

  def preInit()
  {
    //By default, we want to register ore resource type and ingot resource type
    registerResourceBlock("ore", classOf[TileOre])
    registerResourceItem("ingot", classOf[ItemIngot])
  }

  def registerResourceBlock(name: String, clazz: Class[_ <: SpatialBlock])
  {
    resourceBlocks += name -> clazz
  }

  def registerResourceItem(name: String, clazz: Class[_ <: Item])
  {
    resourceItems += name -> clazz
  }

  def getColor(name: String): Int = if (materialColorCache.contains(name)) materialColorCache(name) else 0xFFFFFF
}
