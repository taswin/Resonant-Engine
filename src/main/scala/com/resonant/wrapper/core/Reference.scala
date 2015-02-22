package com.resonant.wrapper.core

/**
 * References too common static objects used by Resonant Engine and its sub mods
 */
object Reference {
	final val id = "resonantengine"
	final val name = "Resonant Engine"
	/**
	 * The configuration file.
	 */
	final val majorVersion = "@MAJOR@"
	final val minorVersion = "@MINOR@"
	final val revisionVersion = "@REVIS@"
	final val version = majorVersion + "." + minorVersion + "." + revisionVersion
	final val buildVersion = "@BUILD@"
	final val domain = id
	final val prefix = domain + ":"
	final val directory = "/assets/" + domain + "/"
	final val channel = "resonantengine"
	final val textureDirectory = "textures/"
	final val guiDirectory = textureDirectory + "gui/"
	final val blockTextureDirectory = textureDirectory + "blocks/"
	final val itemTextureDirectory = textureDirectory + "items/"
	final val modelPath = "models/"
	final val modelDirectory = directory + modelPath
}