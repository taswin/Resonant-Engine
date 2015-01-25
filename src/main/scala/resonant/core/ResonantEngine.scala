package resonant.core

import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event._
import cpw.mods.fml.common.eventhandler.{Event, SubscribeEvent}
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.{FMLCommonHandler, Mod, SidedProxy}
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{FluidContainerRegistry, FluidRegistry, FluidStack}
import net.minecraftforge.oredict.OreDictionary
import resonant.api.mffs.fortron.FrequencyGridRegistry
import resonant.api.recipe.{MachineRecipes, RecipeType}
import resonant.api.tile.IBoilHandler
import resonant.core.content.ResonantContent
import resonant.core.content.tool.{ToolMode, ToolModeGeneral, ToolModeRotation}
import resonant.lib.factory.resources.ResourceFactory
import resonant.lib.grid.core.UpdateTicker
import resonant.lib.grid.frequency.GridFrequency
import resonant.lib.grid.thermal.{BoilEvent, GridThermal, ThermalPhysics}
import resonant.lib.mod.config.{ConfigHandler, ConfigScanner}
import resonant.lib.mod.loadable.LoadableHandler
import resonant.lib.network.netty.PacketManager
import resonant.lib.prefab.tile.multiblock.synthetic.SyntheticMultiblock
import resonant.lib.transform.vector.Vector3
import resonant.lib.utility.nbt.SaveManager
import resonant.lib.utility.{PlayerInteractionHandler, PotionUtility}

/**
 * Mob class for Resonant Engine that handles common loading
 *
 * @author Calclavia, DarkGuardsman
 */
@Mod(modid = Reference.id, name = Reference.name, version = Reference.version, modLanguage = "scala")
object ResonantEngine
{
  val packetHandler = new PacketManager(Reference.channel)
  private val loadables = new LoadableHandler
  @SidedProxy(clientSide = "resonant.core.ClientProxy", serverSide = "resonant.core.CommonProxy")
  var proxy: CommonProxy = null

  @EventHandler
  def preInit(evt: FMLPreInitializationEvent)
  {
    ConfigScanner.instance.generateSets(evt.getAsmData)
    ConfigHandler.sync(Reference.config, Reference.domain)
    Reference.config.load()
    NetworkRegistry.INSTANCE.registerGuiHandler(this, ResonantEngine.proxy)
    loadables.applyModule(ResonantEngine.proxy)
    loadables.applyModule(packetHandler)
    loadables.applyModule(ResonantContent)
    PotionUtility.resizePotionArray()
    MinecraftForge.EVENT_BUS.register(this)
    MinecraftForge.EVENT_BUS.register(ThermalPhysics)
    MinecraftForge.EVENT_BUS.register(SaveManager.instance)
    MinecraftForge.EVENT_BUS.register(new PlayerInteractionHandler)
    ToolMode.REGISTRY.add(new ToolModeGeneral)
    ToolMode.REGISTRY.add(new ToolModeRotation)
    SyntheticMultiblock.instance = new SyntheticMultiblock
    ResourceFactory.preInit()
    loadables.preInit()
  }

  @EventHandler
  def init(evt: FMLInitializationEvent)
  {
    FMLCommonHandler.instance.bus.register(UpdateTicker.world)
    loadables.init()
  }

  @EventHandler
  def postInit(evt: FMLPostInitializationEvent)
  {
    UpdateTicker.threaded.addUpdater(GridThermal)

    if (!UpdateTicker.threaded.isAlive)
    {
      UpdateTicker.threaded.start()
    }

    loadables.postInit()
    FrequencyGridRegistry.CLIENT_INSTANCE = new GridFrequency
    FrequencyGridRegistry.SERVER_INSTANCE = new GridFrequency
    OreDictionary.registerOre("ingotGold", Items.gold_ingot)
    OreDictionary.registerOre("ingotIron", Items.iron_ingot)
    OreDictionary.registerOre("oreGold", Blocks.gold_ore)
    OreDictionary.registerOre("oreIron", Blocks.iron_ore)
    OreDictionary.registerOre("oreLapis", Blocks.lapis_ore)
    MachineRecipes.instance.addRecipe(RecipeType.SMELTER.name, new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(Blocks.stone))
    MachineRecipes.instance.addRecipe(RecipeType.GRINDER.name, Blocks.cobblestone, Blocks.gravel)
    MachineRecipes.instance.addRecipe(RecipeType.GRINDER.name, Blocks.stone, Blocks.cobblestone)
    MachineRecipes.instance.addRecipe(RecipeType.GRINDER.name, Blocks.chest, new ItemStack(Blocks.planks, 7, 0))
    MachineRecipes.instance.addRecipe(RecipeType.SIFTER.name, Blocks.cobblestone, Blocks.sand)
    MachineRecipes.instance.addRecipe(RecipeType.SIFTER.name, Blocks.gravel, Blocks.sand)
    MachineRecipes.instance.addRecipe(RecipeType.SIFTER.name, Blocks.glass, Blocks.sand)
    Reference.config.save()
  }

  @EventHandler
  def serverStopped(event: FMLServerStoppedEvent)
  {
    FrequencyGridRegistry.CLIENT_INSTANCE = new GridFrequency
    FrequencyGridRegistry.SERVER_INSTANCE = new GridFrequency
    GridThermal.clear()
  }

  @EventHandler
  def onServerStopping(evt: FMLServerStoppingEvent)
  {
    SaveManager.saveAll()
  }

  /**
   * Default handler.
   */
  @SubscribeEvent
  def boilEventHandler(evt: BoilEvent)
  {
    val world: World = evt.world
    val position: Vector3 = evt.position

    for (height <- 1 until evt.maxSpread)
    {
      {
        val tileEntity: TileEntity = world.getTileEntity(position.xi, position.yi + height, position.zi)
        if (tileEntity.isInstanceOf[IBoilHandler])
        {
          val handler: IBoilHandler = tileEntity.asInstanceOf[IBoilHandler]
          val fluid: FluidStack = evt.getRemainForSpread(height)
          if (fluid.amount > 0)
          {
            if (handler.canFill(ForgeDirection.DOWN, fluid.getFluid))
            {
              fluid.amount -= handler.fill(ForgeDirection.DOWN, fluid, true)
            }
          }
        }
      }
    }

    evt.setResult(Event.Result.DENY)
  }

}