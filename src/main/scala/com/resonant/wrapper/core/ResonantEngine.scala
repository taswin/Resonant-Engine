package com.resonant.wrapper.core

import com.resonant.core.graph.api.{NodeElectric, NodeRegistry}
import com.resonant.core.graph.internal.electric.NodeElectricComponent
import com.resonant.core.graph.internal.frequency.GridFrequency
import com.resonant.core.graph.internal.thermal.GridThermal
import com.resonant.core.prefab.modcontent.ContentLoader
import com.resonant.core.resources.ResourceFactory
import com.resonant.wrapper.core.content.{BlockCreativeBuilder, ItemScrewdriver}
import com.resonant.wrapper.lib.utility.PotionUtility
import nova.core.block.Block
import nova.core.event.EventListener
import nova.core.event.EventManager.EmptyEvent
import nova.core.game.Game
import nova.core.item.Item
import nova.core.loader.{Loadable, NovaMod}
import nova.core.render.texture.ItemTexture
import nova.internal.tick.UpdateTicker

/**
 * Mob class for Resonant Engine that handles common loading
 *
 * @author Calclavia, DarkGuardsman
 */
@NovaMod(id = Reference.id, name = Reference.name, version = Reference.version, novaVersion = "1.0.0")
object ResonantEngine extends Loadable with ContentLoader {

	var blockCreativeBuilder: Block = classOf[BlockCreativeBuilder]
	var itemScrewdriver: Item = classOf[ItemScrewdriver]
	var textureScrewdriver: ItemTexture = new ItemTexture(Reference.id, "screwdriver")

	override def preInit() {
		Reference.config.load()
		PotionUtility.resizePotionArray()

		/**
		 * Register events 
		 */
		Game.instance.get().eventManager.serverStopping.add(new EventListener[EmptyEvent] {
			override def onEvent(event: EmptyEvent): Unit = serverStopped()
		})

		ResourceFactory.preInit()

		/**
		 * Register graphs 
		 */
		NodeRegistry.instance.register(classOf[NodeElectric], classOf[NodeElectricComponent])
	}

	def serverStopped() {
		GridFrequency.client = new GridFrequency
		GridFrequency.server = new GridFrequency
		GridThermal.clear()
	}

	override def postInit() {
		UpdateTicker.ThreadTicker.ticker.add(GridThermal)

		/*
		Game.instance.get().itemDictionary.add("ingotGold", Items.gold_ingot)
		Game.instance.get().itemDictionary.add("ingotIron", Items.iron_ingot) 
		Game.instance.get().itemDictionary.add("oreGold", Blocks.gold_ore)
		Game.instance.get().itemDictionary.add("oreIron", Blocks.iron_ore)
		Game.instance.get().itemDictionary.add("oreLapis", Blocks.lapis_ore)
		

		MachineRecipes.instance.addRecipe(RecipeType.SMELTER.name, new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(Blocks.stone))
		MachineRecipes.instance.addRecipe(RecipeType.GRINDER.name, Blocks.cobblestone, Blocks.gravel)
		MachineRecipes.instance.addRecipe(RecipeType.GRINDER.name, Blocks.stone, Blocks.cobblestone)
		MachineRecipes.instance.addRecipe(RecipeType.GRINDER.name, Blocks.chest, new ItemStack(Blocks.planks, 7, 0))
		MachineRecipes.instance.addRecipe(RecipeType.SIFTER.name, Blocks.cobblestone, Blocks.sand)
		MachineRecipes.instance.addRecipe(RecipeType.SIFTER.name, Blocks.gravel, Blocks.sand)
		MachineRecipes.instance.addRecipe(RecipeType.SIFTER.name, Blocks.glass, Blocks.sand)
		*/
		Reference.config.save()
	}

	/**
	 * Default handler.

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