package com.resonant.wrapper.core

import com.resonant.core.graph.api.{NodeElectric, NodeRegistry}
import com.resonant.core.graph.internal.electric.NodeElectricComponent
import com.resonant.core.graph.internal.frequency.GridFrequency
import com.resonant.core.graph.internal.thermal.GridThermal
import com.resonant.core.prefab.modcontent.ContentLoader
import com.resonant.wrapper.core.api.edx.recipe.{MachineRecipes, RecipeType}
import com.resonant.wrapper.core.content.{BlockCreativeBuilder, ItemScrewdriver}
import com.resonant.wrapper.lib.factory.resources.ResourceFactory
import com.resonant.wrapper.lib.utility.PotionUtility
import nova.core.block.Block
import nova.core.item.Item
import nova.core.loader.{Loadable, NovaMod}
import nova.core.util.SaveManager
import nova.internal.tick.UpdateTicker

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
		Reference.config.load()
		PotionUtility.resizePotionArray()
		ResourceFactory.preInit()

		/**
		 * Register graphs 
		 */
		NodeRegistry.instance.register(classOf[NodeElectric], classOf[NodeElectricComponent]);
	}

	override def postInit() {
		UpdateTicker.ThreadTicker.ticker.add(GridThermal)

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