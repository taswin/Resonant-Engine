package com.resonant.core

import com.resonant.core.api.edx.recipe.{MachineRecipes, RecipeType}
import com.resonant.core.api.mffs.fortron.FrequencyGridRegistry
import com.resonant.core.content.{BlockCreativeBuilder, ItemScrewdriver, ResonantContent}
import com.resonant.graph.core.UpdateTicker
import com.resonant.graph.frequency.GridFrequency
import com.resonant.graph.thermal.{GridThermal, ThermalPhysics}
import com.resonant.lib.factory.resources.ResourceFactory
import com.resonant.lib.mod.config.{ConfigHandler, ConfigScanner}
import com.resonant.lib.utility.{PlayerInteractionHandler, PotionUtility}
import com.resonant.prefab.modcontent.ContentLoader
import com.resonant.prefab.network.nbt.SaveManager
import cpw.mods.fml.common.network.NetworkRegistry
import nova.core.block.Block
import nova.core.item.Item
import nova.core.loader.{Loadable, NovaMod}

/**
 * Mob class for Resonant Engine that handles common loading
 *
 * @author Calclavia, DarkGuardsman
 */
@NovaMod(id = Reference.id, name = Reference.name, version = Reference.version)
object ResonantEngine extends Loadable with ContentLoader {

	var blockCreativeBuilder: Block = classOf[BlockCreativeBuilder]
	var itemWrench: Item = classOf[ItemScrewdriver]

	override def preInit() {
		ConfigScanner.instance.generateSets(evt.getAsmData)
		ConfigHandler.sync(Reference.config, Reference.domain)
		Reference.config.load()
		NetworkRegistry.INSTANCE.registerGuiHandler(this, ResonantEngine.proxy)
		loadables.applyModule(ResonantContent)
		PotionUtility.resizePotionArray()
		MinecraftForge.EVENT_BUS.register(ThermalPhysics)
		MinecraftForge.EVENT_BUS.register(SaveManager.instance)
		MinecraftForge.EVENT_BUS.register(new PlayerInteractionHandler)
		ResourceFactory.preInit()
		loadables.preInit()
	}

	override def init(evt: FMLInitializationEvent) {
		FMLCommonHandler.instance.bus.register(UpdateTicker.world)
		loadables.init()
	}

	override def postInit(evt: FMLPostInitializationEvent) {
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

	def serverStopped() {
		FrequencyGridRegistry.CLIENT_INSTANCE = new GridFrequency
		FrequencyGridRegistry.SERVER_INSTANCE = new GridFrequency
		GridThermal.clear()
	}

	def onServerStopping() {
		SaveManager.saveAll()
	}

	/**
	 * Default handler.

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
	} */

}