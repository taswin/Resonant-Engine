package resonant.lib.render.fx;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import resonant.engine.Reference;
import resonant.lib.transform.rotation.Quaternion;
import resonant.lib.transform.vector.Vector3;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Electric shock Fxs.
 *
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class FXElectricBolt2 extends EntityFX
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.domain(), Reference.modelPath() + "fadedSphere.png");
	public static final ResourceLocation PARTICLE_RESOURCE = new ResourceLocation("textures/particle/particles.png");
	private final Map<Integer, Integer> parentIDMap = new HashMap<Integer, Integer>();
	/**
	 * The maximum length of the bolt
	 */
	public double boltLength;
	/**
	 * Determines how complex the bolt is.
	 */
	public float complexity;
	public int segmentCount;
	/**
	 * The width of the electrical bolt.
	 */
	private float boltWidth;
	/**
	 * Electric Bolt's start and end positions;
	 */
	private BoltPoint start;
	private BoltPoint end;
	/**
	 * An array of the segments of the bolt.
	 */
	private List<BoltSegment> segments = new ArrayList<BoltSegment>();
	private int maxSplitID;
	private Random rand;

	public FXElectricBolt2(World world, Vector3 startVec, Vector3 targetVec, boolean doSplits)
	{
		super(world, startVec.x(), startVec.y(), startVec.z());

		this.rand = new Random();
		this.start = new BoltPoint(startVec);
		this.end = new BoltPoint(targetVec);

		if (this.end.y() == Double.POSITIVE_INFINITY)
		{
			this.end.y(Minecraft.getMinecraft().thePlayer.posY + 30);
		}

		/** By default, we do an electrical color */
		this.segmentCount = 1;
		this.particleMaxAge = (3 + this.rand.nextInt(3) - 1);
		this.complexity = 2f;
		this.boltWidth = 0.05f;
		this.boltLength = this.start.distance(this.end);
		this.setUp(doSplits);
	}

	public FXElectricBolt2(World world, Vector3 startVec, Vector3 targetVec)
	{
		this(world, startVec, targetVec, true);
	}

	/**
	 * Calculate all required segments of the entire bolt.
	 */
	private void setUp(boolean doSplits)
	{
		this.segments.add(new BoltSegment(this.start, this.end));
		this.recalculate();

		if (doSplits)
		{
			double offsetRatio = this.boltLength * this.complexity;
			this.split(2, offsetRatio / 10, 0.7f, 0.1f, 20 / 2);
			this.split(2, offsetRatio / 15, 0.5f, 0.1f, 25 / 2);
			this.split(2, offsetRatio / 25, 0.5f, 0.1f, 28 / 2);
			this.split(2, offsetRatio / 38, 0.5f, 0.1f, 30 / 2);
			this.split(2, offsetRatio / 55, 0, 0, 0);
			this.split(2, offsetRatio / 70, 0, 0, 0);
			this.recalculate();

			Collections.sort(this.segments, new Comparator<BoltSegment>()
			{
				public int compare(BoltSegment bolt1, BoltSegment bolt2)
				{
					return Float.compare(bolt2.alpha, bolt1.alpha);
				}
			});
		}
	}

	public FXElectricBolt2 setColor(float r, float g, float b)
	{
		this.particleRed = r + (this.rand.nextFloat() * 0.1f) - 0.1f;
		this.particleGreen = g + (this.rand.nextFloat() * 0.1f) - 0.1f;
		this.particleBlue = b + (this.rand.nextFloat() * 0.1f) - 0.1f;
		return this;
	}

	/**
	 * Slits a large segment into multiple smaller ones.
	 *
	 * @param splitAmount - The amount of splits
	 * @param offset      - The multiplier scale for the offset.
	 * @param splitChance - The chance of creating a split.
	 * @param splitLength - The length of each split.
	 * @param splitAngle  - The angle of the split.
	 */
	public void split(int splitAmount, double offset, float splitChance, float splitLength, float splitAngle)
	{
		/** Temporarily store old segments in a new array */
		List<BoltSegment> oldSegments = this.segments;
		this.segments = new ArrayList();
		/** Previous segment */
		BoltSegment prev = null;

		for (BoltSegment segment : oldSegments)
		{
			prev = segment.prev;
			/** Length of each subsegment */
			Vector3 subSegment = segment.difference.clone().multiply(1.0F / splitAmount);

			/**
			 * Creates an array of new bolt points. The first and last points of the bolts are the
			 * respected start and end points of the current segment.
			 */
			BoltPoint[] newPoints = new BoltPoint[splitAmount + 1];
			Vector3 startPoint = segment.start;
			newPoints[0] = segment.start;
			newPoints[splitAmount] = segment.end;

			/**
			 * Create bolt points.
			 */
			for (int i = 1; i < splitAmount; i++)
			{
				Vector3 newOffset = segment.difference.perpendicular().transform(new Quaternion(this.rand.nextFloat() * 360, segment.difference)).multiply((this.rand.nextFloat() - 0.5F) * offset);
				Vector3 basePoint = startPoint.clone().add(subSegment.clone().multiply(i));

				newPoints[i] = new BoltPoint(basePoint, newOffset);
			}

			for (int i = 0; i < splitAmount; i++)
			{
				BoltSegment next = new BoltSegment(newPoints[i], newPoints[(i + 1)], segment.alpha, segment.id * splitAmount + i, segment.splitID);
				next.prev = prev;

				if (prev != null)
				{
					prev.next = next;
				}

				if ((i != 0) && (this.rand.nextFloat() < splitChance))
				{
					Vector3 splitrot = next.difference.xCross().transform(new Quaternion(this.rand.nextFloat() * 360, next.difference));
					Vector3 diff = next.difference.clone().transform(new Quaternion((this.rand.nextFloat() * 0.66F + 0.33F) * splitAngle, splitrot)).multiply(splitLength);
					this.maxSplitID += 1;
					this.parentIDMap.put(this.maxSplitID, next.splitID);
					BoltSegment split = new BoltSegment(newPoints[i], new BoltPoint(newPoints[(i + 1)].base, newPoints[(i + 1)].offset.clone().add(diff)), segment.alpha / 2f, next.id, this.maxSplitID);
					split.prev = prev;
					this.segments.add(split);
				}

				prev = next;
				this.segments.add(next);
			}

			if (segment.next != null)
			{
				segment.next.prev = prev;
			}
		}

		this.segmentCount *= splitAmount;

	}

	private void recalculate()
	{
		HashMap<Integer, Integer> lastActiveSegment = new HashMap<Integer, Integer>();

		Collections.sort(this.segments, new Comparator()
		{
			public int compare(BoltSegment o1, BoltSegment o2)
			{
				int comp = Integer.valueOf(o1.splitID).compareTo(Integer.valueOf(o2.splitID));
				if (comp == 0)
				{
					return Integer.valueOf(o1.id).compareTo(Integer.valueOf(o2.id));
				}
				return comp;
			}

			@Override
			public int compare(Object obj, Object obj1)
			{
				return compare((BoltSegment) obj, (BoltSegment) obj1);
			}
		});

		int lastSplitCalc = 0;
		int lastActiveSeg = 0;

		for (BoltSegment segment : this.segments)
		{
			if (segment.splitID > lastSplitCalc)
			{
				lastActiveSegment.put(lastSplitCalc, lastActiveSeg);
				lastSplitCalc = segment.splitID;
				lastActiveSeg = lastActiveSegment.get(this.parentIDMap.get(segment.splitID)).intValue();
			}

			lastActiveSeg = segment.id;
		}

		lastActiveSegment.put(lastSplitCalc, lastActiveSeg);
		lastSplitCalc = 0;
		lastActiveSeg = lastActiveSegment.get(0).intValue();
		BoltSegment segment;

		for (Iterator<BoltSegment> iterator = this.segments.iterator(); iterator.hasNext(); segment.recalculate())
		{
			segment = iterator.next();

			if (lastSplitCalc != segment.splitID)
			{
				lastSplitCalc = segment.splitID;
				lastActiveSeg = lastActiveSegment.get(segment.splitID);
			}

			if (segment.id > lastActiveSeg)
			{
				iterator.remove();
			}
		}
	}

	@Override
	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.particleAge++ >= this.particleMaxAge)
		{
			this.setDead();
		}
	}

	@Override
	public void renderParticle(Tessellator tessellator, float partialframe, float cosYaw, float cosPitch, float sinYaw, float sinSinPitch, float cosSinPitch)
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		tessellator.draw();
		GL11.glPushMatrix();

		GL11.glDepthMask(false);
		GL11.glEnable(3042);

		glShadeModel(GL_SMOOTH);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);
		/**
		 * Render the actual bolts.
		 */
		tessellator.startDrawingQuads();
		tessellator.setBrightness(15728880);
		Vector3 playerVector = new Vector3(sinYaw * -cosPitch, -cosSinPitch / cosYaw, cosYaw * cosPitch);

		int renderlength = (int) ((this.particleAge + partialframe + (int) (this.boltLength * 3.0F)) / (int) (this.boltLength * 3.0F) * this.segmentCount);

		for (BoltSegment segment : this.segments)
		{
			if (segment != null && segment.id <= renderlength)
			{
				double renderWidth = this.boltWidth * ((new Vector3(player).distance(segment.start) / 5f + 1f) * (1 + segment.alpha) * 0.5f);
				renderWidth = Math.min(this.boltWidth, Math.max(renderWidth, 0));

				if (segment.difference.magnitude() > 0 && segment.difference.magnitude() != Double.NaN && segment.difference.magnitude() != Double.POSITIVE_INFINITY && renderWidth > 0 && renderWidth != Double.NaN && renderWidth != Double.POSITIVE_INFINITY)
				{
					Vector3 diffPrev = playerVector.cross(segment.prevDiff).multiply(renderWidth / segment.sinPrev);
					Vector3 diffNext = playerVector.cross(segment.nextDiff).multiply(renderWidth / segment.sinNext);
					Vector3 startVec = segment.start;
					Vector3 endVec = segment.end;
					float rx1 = (float) (startVec.x() - interpPosX);
					float ry1 = (float) (startVec.y() - interpPosY);
					float rz1 = (float) (startVec.z() - interpPosZ);
					float rx2 = (float) (endVec.x() - interpPosX);
					float ry2 = (float) (endVec.y() - interpPosY);
					float rz2 = (float) (endVec.z() - interpPosZ);

					tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, (1.0F - (this.particleAge >= 0 ? ((float) this.particleAge / (float) this.particleMaxAge) : 0.0F) * 0.6f) * segment.alpha);
					tessellator.addVertexWithUV(rx2 - diffNext.x(), ry2 - diffNext.y(), rz2 - diffNext.z(), 0.5D, 0.0D);
					tessellator.addVertexWithUV(rx1 - diffPrev.x(), ry1 - diffPrev.y(), rz1 - diffPrev.z(), 0.5D, 0.0D);
					tessellator.addVertexWithUV(rx1 + diffPrev.x(), ry1 + diffPrev.y(), rz1 + diffPrev.z(), 0.5D, 1.0D);
					tessellator.addVertexWithUV(rx2 + diffNext.x(), ry2 + diffNext.y(), rz2 + diffNext.z(), 0.5D, 1.0D);

					/**
					 * Render the bolts balls.
					 */

					if (segment.next == null)
					{
						Vector3 roundEnd = segment.end.clone().add(segment.difference.clone().normalize().multiply(renderWidth));
						float rx3 = (float) (roundEnd.x() - interpPosX);
						float ry3 = (float) (roundEnd.y() - interpPosY);
						float rz3 = (float) (roundEnd.z() - interpPosZ);
						tessellator.addVertexWithUV(rx3 - diffNext.x(), ry3 - diffNext.y(), rz3 - diffNext.z(), 0.0D, 0.0D);
						tessellator.addVertexWithUV(rx2 - diffNext.x(), ry2 - diffNext.y(), rz2 - diffNext.z(), 0.5D, 0.0D);
						tessellator.addVertexWithUV(rx2 + diffNext.x(), ry2 + diffNext.y(), rz2 + diffNext.z(), 0.5D, 1.0D);
						tessellator.addVertexWithUV(rx3 + diffNext.x(), ry3 + diffNext.y(), rz3 + diffNext.z(), 0.0D, 1.0D);
					}

					if (segment.prev == null)
					{
						Vector3 roundEnd = segment.start.clone().subtract(segment.difference.clone().normalize().multiply(renderWidth));
						float rx3 = (float) (roundEnd.x() - interpPosX);
						float ry3 = (float) (roundEnd.y() - interpPosY);
						float rz3 = (float) (roundEnd.z() - interpPosZ);
						tessellator.addVertexWithUV(rx1 - diffPrev.x(), ry1 - diffPrev.y(), rz1 - diffPrev.z(), 0.5D, 0.0D);
						tessellator.addVertexWithUV(rx3 - diffPrev.x(), ry3 - diffPrev.y(), rz3 - diffPrev.z(), 0.0D, 0.0D);
						tessellator.addVertexWithUV(rx3 + diffPrev.x(), ry3 + diffPrev.y(), rz3 + diffPrev.z(), 0.0D, 1.0D);
						tessellator.addVertexWithUV(rx1 + diffPrev.x(), ry1 + diffPrev.y(), rz1 + diffPrev.z(), 0.5D, 1.0D);
					}
				}
			}
		}

		tessellator.draw();

		GL11.glDisable(3042);
		GL11.glDepthMask(true);
		GL11.glPopMatrix();

		FMLClientHandler.instance().getClient().renderEngine.bindTexture(PARTICLE_RESOURCE);

		tessellator.startDrawingQuads();
	}

	private class BoltPoint extends Vector3
	{
		public Vector3 base;
		public Vector3 offset;

		public BoltPoint(Vector3 base, Vector3 offset)
		{
			super(base.add(offset));
			this.base = base;
			this.offset = offset;
		}

		public BoltPoint(Vector3 base)
		{
			this(base, new Vector3());
		}
	}

	private class BoltSegment
	{
		public BoltPoint start;
		public BoltPoint end;
		public BoltSegment prev;
		public BoltSegment next;
		public float alpha;
		public int id;
		public int splitID;

		/**
		 * All differences are cached.
		 */
		public Vector3 difference;
		public Vector3 prevDiff;
		public Vector3 nextDiff;
		public double sinPrev;
		public double sinNext;

		public BoltSegment(BoltPoint start, BoltPoint end)
		{
			this(start, end, 1, 0, 0);
		}

		public BoltSegment(BoltPoint start, BoltPoint end, float alpha, int id, int splitID)
		{
			this.start = start;
			this.end = end;
			this.alpha = alpha;
			this.id = id;
			this.splitID = splitID;
			this.difference = this.end.clone().subtract(this.start);
		}

		public void recalculate()
		{
			if (this.prev != null)
			{
				Vector3 prevDiffNorm = this.prev.difference.clone().normalize();
				Vector3 diffNorm = this.difference.clone().normalize();
				this.prevDiff = diffNorm.clone().add(prevDiffNorm).normalize();
				this.sinPrev = Math.sin(diffNorm.anglePreNorm(prevDiffNorm.clone().multiply(-1)) / 2);
			}
			else
			{
				this.prevDiff = this.difference.clone().normalize();
				this.sinPrev = 1;
			}

			if (this.next != null)
			{
				Vector3 nextDiffNorm = this.next.difference.clone().normalize();
				Vector3 diffNorm = this.difference.clone().normalize();
				this.nextDiff = diffNorm.clone().add(nextDiffNorm).normalize();
				this.sinNext = Math.sin(diffNorm.anglePreNorm(nextDiffNorm.clone().multiply(-1)) / 2);
			}
			else
			{
				this.nextDiff = this.difference.clone().normalize();
				this.sinNext = 1;
			}
		}
	}
}