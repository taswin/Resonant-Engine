package com.resonant.core

import com.resonant.core.api.edx.recipe.{MachineRecipes, RecipeType}
import com.resonant.core.api.mffs.fortron.FrequencyGridRegistry
import com.resonant.core.api.tile.IBoilHandler
import com.resonant.core.content.ResonantContent
import com.resonant.graph.core.UpdateTicker
import com.resonant.graph.frequency.GridFrequency
import com.resonant.graph.thermal.{GridThermal, ThermalPhysics}
import com.resonant.lib.factory.resources.ResourceFactory
import com.resonant.lib.mod.config.{ConfigHandler, ConfigScanner}
import com.resonant.lib.mod.loadable.LoadableHandler
import com.resonant.lib.utility.nbt.SaveManager
import com.resonant.lib.utility.{PlayerInteractionHandler, PotionUtility}
import nova.core.util.transform.Vector3d

/**
 * Mob class for Resonant Engine that handles common loading
 *
 * @author Calclavia, DarkGuardsman
 */
@Mod(modid = Reference.id, name = Reference.name, version = Reference.version, modLanguage = "scala")
object ResonantEngine {
	val packetHandler = new PacketManager(Reference.channel)
	private val loadables = new LoadableHandler
	@SidedProxy(clientSide = "resonantengine.core.ClientProxy", serverSide = "resonantengine.core.CommonProxy")
	var proxy: CommonProxy = null

	@EventHandler
	def preInit(evt: FMLPreInitializationEvent) {
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
		ResourceFactory.preInit()
		loadables.preInit()
	}

	@EventHandler
	def init(evt: FMLInitializationEvent) {
		FMLCommonHandler.instance.bus.register(UpdateTicker.world)
		loadables.init()
	}

	@EventHandler
	def postInit(evt: FMLPostInitializationEvent) {
		UpdateTicker.threaded.addUpdater(GridThermal)

		if (!UpdateTicker.threaded.isAlive) {
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
	def serverStopped(event: FMLServerStoppedEvent) {
		FrequencyGridRegistry.CLIENT_INSTANCE = new GridFrequency
		FrequencyGridRegistry.SERVER_INSTANCE = new GridFrequency
		GridThermal.clear()
	}

	@EventHandler
	def onServerStopping(evt: FMLServerStoppingEvent) {
		SaveManager.saveAll()
	}

	/**
	 * Default handler.
	 */
	@SubscribeEvent
	def boilEventHandler(evt: BoilEvent) {
		val world: World = evt.world
		val position: Vector3d = evt.position

		for (height <- 1 until evt.maxSpread) {
			{
				val tileEntity: TileEntity = world.getTileEntity(position.xi, position.yi + height, position.zi)
				if (tileEntity.isInstanceOf[IBoilHandler]) {
					val handler: IBoilHandler = tileEntity.asInstanceOf[IBoilHandler]
					val fluid: FluidStack = evt.getRemainForSpread(height)
					if (fluid.amount > 0) {
						if (handler.canFill(Direction.DOWN, fluid.getFluid)) {
							fluid.amount -= handler.fill(Direction.DOWN, fluid, true)
						}
					}
				}
			}
		}

		evt.setResult(Event.Result.DENY)
	}

}