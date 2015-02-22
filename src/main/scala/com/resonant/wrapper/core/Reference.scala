package com.resonant.wrapper.core

import java.io.File

import cpw.mods.fml.common.Loader
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.config.Configuration
import org.apache.logging.log4j.LogManager

/**
 * References too common static objects used by Resonant Engine and its sub mods
 */
object Reference {
	final val id = "ResonantEngine"
	final val name = "Resonant Engine"
	/**
	 * The configuration file.
	 */
	final val majorVersion = "@MAJOR@"
	final val minorVersion = "@MINOR@"
	final val revisionVersion = "@REVIS@"
	final val version = majorVersion + "." + minorVersion + "." + revisionVersion
	final val buildVersion = "@BUILD@"
	final val domain = "resonantengine"
	final val prefix = domain + ":"
	final val directory = "/assets/" + domain + "/"
	final val channel = "resonantengine"
	final val textureDirectory = "textures/"
	final val guiDirectory = textureDirectory + "gui/"
	final val guiEmpty = new ResourceLocation(domain, guiDirectory + "gui_empty.png")
	final val guiBase = new ResourceLocation(domain, guiDirectory + "gui_base.png")
	final val guiComponents = new ResourceLocation(domain, guiDirectory + "gui_components.png")
	final val blockTextureDirectory = textureDirectory + "blocks/"
	final val itemTextureDirectory = textureDirectory + "items/"
	final val modelPath = "models/"
	final val modelDirectory = directory + modelPath
}