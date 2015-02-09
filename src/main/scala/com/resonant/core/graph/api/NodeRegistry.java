package com.resonant.core.graph.api;

import com.resonant.core.graph.internal.Node;

import java.util.HashMap;

/**
 * A dynamic node loader for registering different nodes for different node interfaces.
 * <p/>
 * This is the essential class for loading different nodes.
 *
 * @author Calclavia
 */
public class NodeRegistry {
	public static final NodeRegistry instance = new NodeRegistry();
	private final HashMap<Class<? extends Node>, Class<? extends Node>> interfaceNodeMap = new HashMap<>();

	private NodeRegistry() {
	}

	public void register(Class<? extends Node> nodeInterface, Class<? extends Node> nodeClass) {
		interfaceNodeMap.put(nodeInterface, nodeClass);
	}

	public <N extends Node> N get(Class<N> nodeInterface, Object... args) {
		Class nodeClass = interfaceNodeMap.get(nodeInterface);

		try {
			Class[] classes = new Class[args.length];
			for (int i = 0; i < args.length; i++)
				classes[i] = args[i].getClass();

			return (N) nodeClass.getConstructor(classes).newInstance(args);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}