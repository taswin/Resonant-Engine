package resonant.engine

import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event._
import cpw.mods.fml.common.eventhandler.{Event, SubscribeEvent}
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.{FMLCommonHandler, Mod, ModMetadata, SidedProxy}
import net.minecraft.block.Block
import net.minecraft.block.material.Material
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
import resonant.engine.content.ResonantContent
import resonant.engine.content.tool.{ToolMode, ToolModeGeneral, ToolModeRotation}
import resonant.lib.debug.F3Handler
import resonant.lib.factory.resources.ResourceFactory
import resonant.lib.grid.core.UpdateTicker
import resonant.lib.grid.frequency.GridFrequency
import resonant.lib.grid.thermal.{BoilEvent, EventThermal, ThermalGrid}
import resonant.lib.mod.config.{ConfigHandler, ConfigScanner}
import resonant.lib.mod.loadable.LoadableHandler
import resonant.lib.network.netty.PacketManager
import resonant.lib.prefab.tile.multiblock.synthetic.SyntheticMultiblock
import resonant.lib.transform.vector.{Vector3, VectorWorld}
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
  val packetHandler: PacketManager = new PacketManager(Reference.channel)
  private val loadables: LoadableHandler = new LoadableHandler
  @SidedProxy(clientSide = "resonant.engine.ClientProxy", serverSide = "resonant.engine.CommonProxy")
  var proxy: CommonProxy = null
  @Mod.Metadata(Reference.id)
  var metadata: ModMetadata = null

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
    MinecraftForge.EVENT_BUS.register(SaveManager.instance)
    MinecraftForge.EVENT_BUS.register(new PlayerInteractionHandler)
    MinecraftForge.EVENT_BUS.register(F3Handler)
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
    UpdateTicker.threaded.addUpdater(ThermalGrid)

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

  /**
   * Default handler.
   */
  @SubscribeEvent
  def thermalEventHandler(evt: EventThermal.EventThermalUpdate)
  {
    val pos: VectorWorld = evt.position
    pos.world synchronized
    {
      val block: Block = pos.getBlock
      val mat: Material = pos.getBlock.getMaterial
      if (mat == Material.air)
      {
        evt.heatLoss = 0.15f
      }
      if (block == Blocks.flowing_water || block == Blocks.water)
      {
        if (evt.temperature >= 373)
        {
          if (FluidRegistry.getFluid("steam") != null)
          {
            val volume: Int = (FluidContainerRegistry.BUCKET_VOLUME * (evt.temperature / 373)).toInt
            MinecraftForge.EVENT_BUS.post(new BoilEvent(pos.world, pos, new FluidStack(FluidRegistry.WATER, volume), new FluidStack(FluidRegistry.getFluid("steam"), volume), 2, evt.isReactor))
          }
          evt.heatLoss = 0.2f
        }
      }
      if (block == Blocks.ice)
      {
        if (evt.temperature >= 273)
        {
          UpdateTicker.threaded.enqueue(() => pos.setBlock(Blocks.flowing_water))
          evt.heatLoss = 0.4f
        }
      }
    }
  }
}