package com.resonant.prefab.save;

import com.resonant.core.Reference;
import com.resonant.core.api.misc.ISave;
import com.resonant.lib.utility.ReflectionUtility;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.world.WorldEvent;
import nova.core.util.components.Storable;
import nova.wrapper.mc1710.util.NBTUtility;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * Simple manager that handles external file saving.
 * @author Darkguardsman
 */
public class SaveManager {
	/**
	 * Instance of this class
	 */
	private static SaveManager instance;
	/**
	 * Map of save names with there class file
	 */
	private HashMap<String, Class<?>> idToClassMap = new HashMap<>();
	/**
	 * Reverse of the idToClassMap
	 */
	private HashMap<Class<?>, String> classToIDMap = new HashMap<>();
	/**
	 * List of object to save on the next save call
	 */
	private LinkedHashSet<IVirtualObject> saveList = new LinkedHashSet<IVirtualObject>();
	/**
	 * Object that save each time the world saves
	 */
	private LinkedHashSet<IVirtualObject> objects = new LinkedHashSet<IVirtualObject>();

	/**
	 * Last cpu time that the save manager tried to save a file
	 */
	private long lastSaveMills = 0;

	/**
	 * Gets an instance of this class
	 */
	public static SaveManager instance() {
		if (instance == null) {
			instance = new SaveManager();
		}
		return instance;
	}

	/**
	 * Called when the object wants to be save only on the next save call. Will be removed from the
	 * save manager after
	 */
	public static void markNeedsSaved(Object object) {
		synchronized (instance()) {
			if (object instanceof IVirtualObject && !instance().saveList.contains(object)) {
				instance().saveList.add((IVirtualObject) object);
			}
		}
	}

	/**
	 * Registers the object to be saved on each world save event
	 */
	public static void register(Object object) {
		synchronized (instance()) {
			if (object instanceof IVirtualObject && !instance().objects.contains(object)) {
				instance().saveList.add((IVirtualObject) object);
			}
		}
	}

	/**
	 * Call this to register a class with an id to be use in recreating an object from a save. Any
	 * object that is registered to this should use a no parm constructor. Unless the class plans to
	 * construct itself without using the save manager.
	 * @param id - string that will be used to save the class by
	 * @param clazz - class to link with the id
	 */
	public static void registerClass(String id, Class<?> clazz) {
		synchronized (instance()) {
			if (id != null && clazz != null) {
				if (instance().idToClassMap.containsKey(id) && instance().idToClassMap.get(id) != null) {
					System.out.println("[CoreMachine]SaveManager: Something attempted to register a class with the id of another class");
					System.out.println("[CoreMachine]SaveManager: Id:" + id + "  Class:" + clazz.getName());
					System.out.println("[CoreMachine]SaveManager: OtherClass:" + instance().idToClassMap.get(id).getName());
				} else {
					instance().idToClassMap.put(id, clazz);
					instance().classToIDMap.put(clazz, id);
				}
			}
		}
	}

	/**
	 * Creates an object from an NBT save file.
	 * @param file - file
	 * @return the object created from the file
	 */
	public static Object createAndLoad(File file, Object... args) {
		if (file.exists()) {
			Object obj = createAndLoad(NBTUtility.load(file), args);
			if (obj instanceof IVirtualObject) {
				((IVirtualObject) obj).setSaveFile(file);
			}
			return obj;
		}
		return null;
	}

	/**
	 * Loads an object from an NBTTagCompound
	 * @param nbt - NBTTagCompound
	 * @param args - argument that will be used to construct the object's class
	 * @return new object or null if something went wrong
	 */
	public static Object createAndLoad(NBTTagCompound nbt, Object... args) {
		Object obj = null;
		try {
			if (nbt != null && nbt.hasKey("id")) {
				try {
					Class<?> clazz = getClass(nbt.getString("id"));
					if (clazz != null) {
						if (args == null || args.length == 0) {
							Constructor<?> con = ReflectionUtility.getConstructorWithArgs(clazz, args);
							if (con != null) {
								obj = con.newInstance(args);
							}
						} else {
							obj = clazz.newInstance();
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}

				if (obj instanceof ISave) {
					try {
						((ISave) obj).load(nbt);
					} catch (Exception e) {
						Reference.logger().catching(Level.FATAL, e);
						Reference.logger().fatal("SaveManager: An object %s(%s) has thrown an exception during loading, its state cannot be restored. Report this to the mod author", nbt.getString("id"), obj.getClass().getName());
						obj = null;
					}
				} else {
					Reference.logger().warn("SaveManager: Skipping object with id " + nbt.getString("id"));
				}

				return obj;
			}
		} catch (Exception e) {
			FMLLog.fine("[Resonant Engine]SaveManager: Error trying to load object from save");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Called to save all object currently set to save next call
	 */
	public static void saveAll() {
		for (IVirtualObject ref : instance().objects) {
			save(ref);
		}
		for (IVirtualObject ref : instance().saveList) {
			save(ref);
		}
		instance().saveList.clear();
	}

	/**
	 * Saves an object to its preferred save location. Does check for null, registered save class,
	 * and if save file doesn't exist. Redirects to NBTUtility for actual saving of the file itself.
	 * @param storable - instance of @IVirtualObject
	 */
	public static void save(Storable storable) {
		try {
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
				if (storable != null) {
					if (getID(storable.getClass()) != null) {
						if (storable.getSaveFile() != null) {
							/* Get file, and make directories */
							File file = storable.getSaveFile();
							file.mkdirs();

                            /* Create nbt save object */
							NBTTagCompound tag = new NBTTagCompound();
							storable.save(tag);
							tag.setString("id", getID(storable.getClass()));

							saveFile(file, tag);
						} else {
							throw new NullPointerException("SaveManager: Object save file path is null");
						}
					} else {
						throw new Exception("SaveManager: Object does not have a save id");
					}
				} else {
					throw new NullPointerException("SaveManager: Attempted to save a null object");
				}
			}
		} catch (Exception e) {
			FMLLog.fine("[Resonant Engine]SaveManager: Error trying to save object class: " + (storable != null ? storable.getClass() : "null"));
			e.printStackTrace();
		}
	}

	/**
	 * Gets the id that the class will be saved using
	 */
	public static String getID(Class clazz) {
		return instance().classToIDMap.get(clazz);
	}

	/**
	 * Gets the class that was registered with the id
	 */
	public static Class getClass(String id) {
		return instance().idToClassMap.get(id);
	}

	/**
	 * Saves NBT data in the world folder.
	 * @return True on success.
	 */
	public static boolean saveFile(File file, NBTTagCompound data) {
		try {
			File tempFile = new File(file.getParent(), file.getName() + "_tmp.dat");

			CompressedStreamTools.writeCompressed(data, new FileOutputStream(tempFile));

			if (file.exists()) {
				file.delete();
			}

			tempFile.renameTo(file);
			return true;
		} catch (Exception e) {
			Reference.logger().fatal("Failed to save " + file.getName() + ".dat!");
			e.printStackTrace();
			return false;
		}
	}

	public static boolean saveFile(File saveDirectory, String filename, NBTTagCompound data) {
		return saveFile(new File(saveDirectory, filename + ".dat"), data);
	}

	public static boolean saveFile(String filename, NBTTagCompound data) {
		return saveFile(getSaveDirectory(MinecraftServer.getServer().getFolderName()), filename, data);
	}

	public static NBTTagCompound loadFile(File file) {
		try {
			if (file.exists()) {
				return CompressedStreamTools.readCompressed(new FileInputStream(file));
			} else {
				return new NBTTagCompound();
			}
		} catch (Exception e) {
			Reference.logger().fatal("Failed to load " + file.getName() + ".dat!");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Reads NBT data from the world folder.
	 * @return The NBT data
	 */
	public static NBTTagCompound loadFile(File saveDirectory, String filename) {
		return loadFile(new File(saveDirectory, filename + ".dat"));
	}

	public static NBTTagCompound loadFile(String filename) {
		return loadFile(getSaveDirectory(MinecraftServer.getServer().getFolderName()), filename);
	}

	public static File getSaveDirectory() {
		return getSaveDirectory(MinecraftServer.getServer().getFolderName());
	}

	public static File getSaveDirectory(String worldName) {
		File parent = getBaseDirectory();

		if (FMLCommonHandler.instance().getSide().isClient()) {
			parent = new File(getBaseDirectory(), "saves" + File.separator);
		}

		return new File(parent, worldName + File.separator);
	}

	public static File getBaseDirectory() {
		if (FMLCommonHandler.instance().getSide().isClient()) {
			FMLClientHandler.instance().getClient();
			return FMLClientHandler.instance().getClient().mcDataDir;
		} else {
			return new File(".");
		}
	}

	@SubscribeEvent
	public void worldSave(WorldEvent evt) {
		//Current time milli-seconds is used to prevent the files from saving 20 times when the world loads
		if (System.currentTimeMillis() - lastSaveMills > 2000) {
			lastSaveMills = System.currentTimeMillis();
			saveAll();
		}
	}
}
