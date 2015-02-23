package com.resonant.core.prefab.block.multiblock;

import nova.core.block.Block;
import nova.core.retention.Storable;
import nova.core.util.transform.Vector3i;

import java.lang.ref.WeakReference;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A reference-based multiblock structure uses a central block as the "primary block" and have all
 * the blocks around it be "dummy blocks". This handler should be extended. Every single block will
 * have a reference of this object.
 *
 * @author Calclavia
 */
public class MultiBlockHandler<W extends IMultiBlockStructure> implements Storable {
	protected final W block;
	/**
	 * The main block used for reference
	 */
	protected WeakReference<W> prim = null;
	/**
	 * The relative primary block position to be loaded in once the block is initiated.
	 */
	protected Vector3i newPrimary = null;
	protected Class<? extends W> wrapperClass;

	public MultiBlockHandler(W wrapper) {
		this.block = wrapper;
		wrapperClass = (Class<? extends W>) wrapper.getClass();
	}

	public void update() {
		if (block.world() != null && newPrimary != null) {
			W checkWrapper = getWrapperAt(newPrimary.add(block.position()));

			if (checkWrapper != null) {
				newPrimary = null;

				if (checkWrapper != getPrimary()) {
					prim = new WeakReference(checkWrapper);
					block.onMultiBlockChanged();
				}
			}
		}
	}

	/**
	 * Try to construct the structure, otherwise, deconstruct it.
	 *
	 * @return True if operation is successful.
	 */
	public boolean toggleConstruct() {
		if (isConstructed()) {
			return deconstruct();
		}
		return construct();
	}

	/**
	 * Gets the structure blocks of the multiblock.
	 *
	 * @return Null if structure cannot be created.
	 */
	public Set<W> getStructure() {
		Set<W> structure = new LinkedHashSet<>();
		Iterable<Vector3i> vectors = block.getMultiBlockVectors();

		for (Vector3i vector : vectors) {
			W checkWrapper = getWrapperAt(vector.add(block.position()));

			if (checkWrapper != null) {
				structure.add(checkWrapper);
			} else {
				return null;
			}
		}

		return structure;
	}

	/**
	 * Called to construct the multiblock structure. Example: Wrenching the center block or checking
	 * if a placement was done correct. Note that this block will become the PRIMARY block.
	 *
	 * @return True if the construction was successful.
	 */
	public boolean construct() {
		if (!isConstructed()) {
			Set<W> structures = getStructure();

			if (structures != null) {
				for (W structure : structures) {
					if (structure.getMultiBlock().isConstructed()) {
						return false;
					}
				}

				prim = new WeakReference(block);
				for (W structure : structures) {
					structure.getMultiBlock().prim = prim;
				}

				for (W structure : structures) {
					structure.onMultiBlockChanged();
				}

				return true;
			}
		}

		return false;
	}

	public boolean deconstruct() {
		if (isConstructed()) {
			if (isPrimary()) {
				Set<W> structures = getStructure();
				if (structures != null) {
					for (W structure : structures) {
						structure.getMultiBlock().prim = null;
					}

					for (W structure : structures) {
						structure.onMultiBlockChanged();
					}
				}
			} else {
				getPrimary().getMultiBlock().deconstruct();
			}

			return true;
		}

		return false;
	}

	public W getWrapperAt(Vector3i position) {
		Optional<Block> block = this.block.world().getBlock(position);

		if (block.isPresent() && wrapperClass.isAssignableFrom(block.get().getClass())) {
			return (W) block.get();
		}

		return null;
	}

	public boolean isConstructed() {
		return getPrimary() != null;
	}

	public boolean isPrimary() {
		return !isConstructed() || getPrimary() == block;
	}

	public W getPrimary() {
		return prim == null ? null : prim.get();
	}

	public W get() {
		return getPrimary() != null ? getPrimary() : block;
	}

	@Override
	public void save(Data data) {
		if (isConstructed()) {
			data.put("primaryMultiBlock", getPrimary().position().subtract(block.position()));
		}
	}

	/**
	 * Only the primary wrapper of the multiblock saves and loads data.
	 */
	@Override
	public void load(Map<String, Object> data) {
		if (data.containsKey("primaryMultiBlock")) {
			Vector3i zero = Vector3i.ZERO;
			zero.load((Map) data.get("primaryMultiBlock"));
			newPrimary = zero;
		} else {
			prim = null;
		}
	}

}
