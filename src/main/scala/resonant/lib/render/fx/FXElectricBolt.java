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
import resonant.lib.render.RenderUtility;
import resonant.lib.transform.rotation.Quaternion;
import resonant.lib.transform.vector.Vector3;

import java.util.*;

/**
 * An effect that renders a electrical bolt from one position to another. Inspired by Azanor's
 * lightning wand.
 *
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class FXElectricBolt extends EntityFX
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.domain(), Reference.textureDirectory() + "fadedSphere.png");
	/**
	 * Determines how complex the bolt is.
	 */
	public float complexity;
	/**
	 * The maximum length of the bolt
	 */
	public double length;
	public int segmentCount;
	/**
	 * The width of the electrical bolt.
	 */
	private float boltWidth = 0.05f;
	/**
	 * Electric Bolt's start and end positions;
	 */
	private Vector3 start;
	private Vector3 end;
	/**
	 * An array of the segments of the bolt.
	 */
	private ArrayList<BoltSegment> segments = new ArrayList<BoltSegment>();
	private HashMap<Integer, Integer> splitparents = new HashMap<Integer, Integer>();
	private int maxSplitID = 0;
	private Random rand;
	/**
	 * Are the segments calculated?
	 */
	private boolean isCalculated;

	public FXElectricBolt(World world, Vector3 startVec, Vector3 targetVec, long seed)
	{
		super(world, startVec.x(), startVec.y(), startVec.z(), 0.0D, 0.0D, 0.0D);

		if (seed == 0)
		{
			this.rand = new Random();
		}
		else
		{
			this.rand = new Random(seed);
		}

		this.start = startVec;
		this.end = targetVec;
		/** By default, we do an electrical color */
		this.particleAge = (3 + this.rand.nextInt(3) - 1);
		this.particleRed = 0.55f + (this.rand.nextFloat() * 0.1f);
		this.particleGreen = 0.7f + (this.rand.nextFloat() * 0.1f);
		this.particleBlue = 1f;
		this.segmentCount = 1;
		this.length = this.start.distance(this.end);
		this.particleMaxAge = (3 + this.rand.nextInt(3) - 1);
		this.complexity = 2f;

		/** Calculate all required segments of the entire bolt. */
		this.segments.add(new BoltSegment(this.start, this.end));
		this.recalculateDifferences();
		this.split(2, this.length * this.complexity / 8.0F, 0.7F, 0.1F, 45.0F);
		this.split(2, this.length * this.complexity / 12.0F, 0.5F, 0.1F, 50.0F);
		this.split(2, this.length * this.complexity / 17.0F, 0.5F, 0.1F, 55.0F);
		this.split(2, this.length * this.complexity / 23.0F, 0.5F, 0.1F, 60.0F);
		this.split(2, this.length * this.complexity / 30.0F, 0.0F, 0.0F, 0.0F);
		this.split(2, this.length * this.complexity / 34.0F, 0.0F, 0.0F, 0.0F);
		this.split(2, this.length * this.complexity / 40.0F, 0.0F, 0.0F, 0.0F);
		this.finalizeBolt();
	}

	private static Vector3 getRelativeViewVector(Vector3 pos)
	{
		EntityPlayer renderentity = Minecraft.getMinecraft().thePlayer;
		return new Vector3((float) renderentity.posX - pos.x(), (float) renderentity.posY - pos.y(), (float) renderentity.posZ - pos.z());
	}

	public FXElectricBolt setMultiplier(float m)
	{
		this.complexity = m;
		return this;
	}

	public FXElectricBolt setWidth(float m)
	{
		this.boltWidth = m;
		return this;
	}

	public FXElectricBolt setColor(float r, float g, float b)
	{
		this.particleRed = r;
		this.particleGreen = g;
		this.particleBlue = b;
		return this;
	}

	/**
	 * Slits a large segment into multiple smaller ones.
	 *
	 * @param splitAmount - The amount of splits
	 * @param offset      - The multiplier multiply for the offset.
	 * @param splitChance - The chance of creating a split.
	 * @param splitLength - The length of each split.
	 * @param splitAngle  - The angle of the split.
	 */
	public void split(int splitAmount, double offset, float splitChance, float splitLength, float splitAngle)
	{
		if (!this.isCalculated)
		{
			/** Temporarily store old segments in a new array */
			ArrayList<BoltSegment> oldSegments = this.segments;
			this.segments = new ArrayList();
			/** Previous segment */
			BoltSegment prev = null;

			for (BoltSegment segment : oldSegments)
			{
				prev = segment.prevSegment;
				Vector3 subSegment = segment.difference.multiply(1.0F / splitAmount);

				/** Creates an array of new bolt points. The first and last points of the bolts are
				 * the respected start and end points of the current segment. */
				BoltPoint[] newPoints = new BoltPoint[splitAmount + 1];
				Vector3 startPoint = segment.startBolt.point;
				newPoints[0] = segment.startBolt;
				newPoints[splitAmount] = segment.endBolt;

				for (int i = 1; i < splitAmount; i++)
				{
					//Vector3 offsetVec = segment.difference.perpendicular().rotate(this.rand.nextFloat() * 360.0F, segment.difference).multiply((this.rand.nextFloat() - 0.5F) * offset);
					Vector3 offsetVec = segment.difference.perpendicular().transform(new Quaternion(this.rand.nextFloat() * 360.0F, segment.difference)).multiply((this.rand.nextFloat() - 0.5F) * offset);
					Vector3 basepoint = startPoint.clone().add(subSegment.clone().multiply(i));
					newPoints[i] = new BoltPoint(basepoint, offsetVec);
				}

				for (int i = 0; i < splitAmount; i++)
				{
					BoltSegment next = new BoltSegment(newPoints[i], newPoints[(i + 1)], segment.weight, segment.segmentID * splitAmount + i, segment.splitID);
					next.prevSegment = prev;

					if (prev != null)
					{
						prev.nextSegment = next;
					}

					if ((i != 0) && (this.rand.nextFloat() < splitChance))
					{
						//Vector3 splitrot = next.difference.xCross().rotate(this.rand.nextFloat() * 360.0F, next.difference);
						Vector3 splitrot = next.difference.xCross().transform(new Quaternion(this.rand.nextFloat() * 360.0F, next.difference));
						//Vector3 diff = next.difference.clone().rotate((this.rand.nextFloat() * 0.66F + 0.33F) * splitAngle, splitrot).multiply(splitLength);
						Vector3 diff = next.difference.clone().transform(new Quaternion((this.rand.nextFloat() * 0.66F + 0.33F) * splitAngle, splitrot)).multiply(splitLength);
						this.maxSplitID += 1;
						this.splitparents.put(this.maxSplitID, next.splitID);
						BoltSegment split = new BoltSegment(newPoints[i], new BoltPoint(newPoints[(i + 1)].basePoint, newPoints[(i + 1)].offSet.clone().add(diff)), segment.weight / 2.0F, next.segmentID, this.maxSplitID);
						split.prevSegment = prev;
						this.segments.add(split);
					}

					prev = next;
					this.segments.add(next);
				}

				if (segment.nextSegment != null)
				{
					segment.nextSegment.prevSegment = prev;
				}
			}

			this.segmentCount *= splitAmount;
		}
	}

	public void finalizeBolt()
	{
		if (!this.isCalculated)
		{
			this.isCalculated = true;
			recalculateDifferences();

			Collections.sort(this.segments, new Comparator()
			{
				public int compare(BoltSegment o1, BoltSegment o2)
				{
					return Float.compare(o2.weight, o1.weight);
				}

				@Override
				public int compare(Object obj, Object obj1)
				{
					return compare((BoltSegment) obj, (BoltSegment) obj1);
				}
			});
		}
	}

	private void recalculateDifferences()
	{
		HashMap<Integer, Integer> lastActiveSegment = new HashMap<Integer, Integer>();

		Collections.sort(this.segments, new Comparator()
		{
			public int compare(BoltSegment o1, BoltSegment o2)
			{
				int comp = Integer.valueOf(o1.splitID).compareTo(Integer.valueOf(o2.splitID));
				if (comp == 0)
				{
					return Integer.valueOf(o1.segmentID).compareTo(Integer.valueOf(o2.segmentID));
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
			if (segment != null)
			{
				if (segment.splitID > lastSplitCalc)
				{
					lastActiveSegment.put(lastSplitCalc, lastActiveSeg);
					lastSplitCalc = segment.splitID;
					lastActiveSeg = lastActiveSegment.get(this.splitparents.get(segment.splitID)).intValue();
				}

				lastActiveSeg = segment.segmentID;
			}
		}

		lastActiveSegment.put(lastSplitCalc, lastActiveSeg);
		lastSplitCalc = 0;
		lastActiveSeg = lastActiveSegment.get(0).intValue();
		BoltSegment segment;

		for (Iterator<BoltSegment> iterator = this.segments.iterator(); iterator.hasNext(); segment.calculateEndDifferences())
		{
			segment = iterator.next();

			if (lastSplitCalc != segment.splitID)
			{
				lastSplitCalc = segment.splitID;
				lastActiveSeg = lastActiveSegment.get(segment.splitID);
			}

			if (segment.segmentID > lastActiveSeg)
			{
				iterator.remove();
			}
		}
	}

	/**
	 * Renders the bolts.
	 */
	private void renderBolt(Tessellator tessellator, float partialframe, float cosyaw, float cospitch, float sinyaw, float cossinpitch, int pass)
	{
		Vector3 playerVector = new Vector3(sinyaw * -cospitch, -cossinpitch / cosyaw, cosyaw * cospitch);
		float voltage = this.particleAge >= 0 ? ((float) this.particleAge / (float) this.particleMaxAge) : 0.0F;

		float mainAlpha = 1.0F;

		if (pass == 0)
		{
			mainAlpha = (1.0F - voltage) * 0.4F;
		}
		else
		{
			mainAlpha = 1.0F - voltage * 0.5F;
		}

		int renderlength = (int) ((this.particleAge + partialframe + (int) (this.length * 3.0F)) / (int) (this.length * 3.0F) * this.segmentCount);

		for (BoltSegment renderSegment : this.segments)
		{
			if (renderSegment != null && renderSegment.segmentID <= renderlength)
			{
				float width = (float) (this.boltWidth * (getRelativeViewVector(renderSegment.startBolt.point).magnitude() / 5.0F + 1.0F) * (1.0F + renderSegment.weight) * 0.5F);
				Vector3 diff1 = playerVector.cross(renderSegment.prevDiff).multiply(width / renderSegment.sinPrev);
				Vector3 diff2 = playerVector.cross(renderSegment.nextDiff).multiply(width / renderSegment.sinNext);
				Vector3 startvec = renderSegment.startBolt.point;
				Vector3 endvec = renderSegment.endBolt.point;
				float rx1 = (float) (startvec.x() - interpPosX);
				float ry1 = (float) (startvec.y() - interpPosY);
				float rz1 = (float) (startvec.z() - interpPosZ);
				float rx2 = (float) (endvec.x() - interpPosX);
				float ry2 = (float) (endvec.y() - interpPosY);
				float rz2 = (float) (endvec.z() - interpPosZ);
				tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, mainAlpha * renderSegment.weight);
				tessellator.addVertexWithUV(rx2 - diff2.x(), ry2 - diff2.y(), rz2 - diff2.z(), 0.5D, 0.0D);
				tessellator.addVertexWithUV(rx1 - diff1.x(), ry1 - diff1.y(), rz1 - diff1.z(), 0.5D, 0.0D);
				tessellator.addVertexWithUV(rx1 + diff1.x(), ry1 + diff1.y(), rz1 + diff1.z(), 0.5D, 1.0D);
				tessellator.addVertexWithUV(rx2 + diff2.x(), ry2 + diff2.y(), rz2 + diff2.z(), 0.5D, 1.0D);

				if (renderSegment.nextSegment == null)
				{
					Vector3 roundend = renderSegment.endBolt.point.clone().add(renderSegment.difference.clone().normalize().multiply(width));
					float rx3 = (float) (roundend.x() - interpPosX);
					float ry3 = (float) (roundend.y() - interpPosY);
					float rz3 = (float) (roundend.z() - interpPosZ);
					tessellator.addVertexWithUV(rx3 - diff2.x(), ry3 - diff2.y(), rz3 - diff2.z(), 0.0D, 0.0D);
					tessellator.addVertexWithUV(rx2 - diff2.x(), ry2 - diff2.y(), rz2 - diff2.z(), 0.5D, 0.0D);
					tessellator.addVertexWithUV(rx2 + diff2.x(), ry2 + diff2.y(), rz2 + diff2.z(), 0.5D, 1.0D);
					tessellator.addVertexWithUV(rx3 + diff2.x(), ry3 + diff2.y(), rz3 + diff2.z(), 0.0D, 1.0D);
				}

				if (renderSegment.prevSegment == null)
				{
					Vector3 roundend = renderSegment.startBolt.point.clone().subtract(renderSegment.difference.clone().normalize().multiply(width));
					float rx3 = (float) (roundend.x() - interpPosX);
					float ry3 = (float) (roundend.y() - interpPosY);
					float rz3 = (float) (roundend.z() - interpPosZ);
					tessellator.addVertexWithUV(rx1 - diff1.x(), ry1 - diff1.y(), rz1 - diff1.z(), 0.5D, 0.0D);
					tessellator.addVertexWithUV(rx3 - diff1.x(), ry3 - diff1.y(), rz3 - diff1.z(), 0.0D, 0.0D);
					tessellator.addVertexWithUV(rx3 + diff1.x(), ry3 + diff1.y(), rz3 + diff1.z(), 0.0D, 1.0D);
					tessellator.addVertexWithUV(rx1 + diff1.x(), ry1 + diff1.y(), rz1 + diff1.z(), 0.5D, 1.0D);
				}
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
		EntityPlayer renderentity = Minecraft.getMinecraft().thePlayer;
		int visibleDistance = 100;

		if (!Minecraft.getMinecraft().gameSettings.fancyGraphics)
		{
			visibleDistance /= 2;
		}

		if (renderentity.getDistance(this.posX, this.posY, this.posZ) > visibleDistance)
		{
			return;
		}

		tessellator.draw();
		GL11.glPushMatrix();

		GL11.glDepthMask(false);
		GL11.glEnable(3042);

		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);
		/** Render the actual bolts. */
		tessellator.startDrawingQuads();
		tessellator.setBrightness(15728880);
		this.renderBolt(tessellator, partialframe, cosYaw, cosPitch, sinYaw, cosSinPitch, 0);
		tessellator.draw();

		// GL11.glBlendFunc(770, 771);

		tessellator.startDrawingQuads();
		tessellator.setBrightness(15728880);
		this.renderBolt(tessellator, partialframe, cosYaw, cosPitch, sinYaw, cosSinPitch, 1);
		tessellator.draw();

		GL11.glDisable(3042);
		GL11.glDepthMask(true);
		GL11.glPopMatrix();

		FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderUtility.PARTICLE_RESOURCE);

		tessellator.startDrawingQuads();
	}

	@Override
	public boolean shouldRenderInPass(int pass)
	{
		return pass == 2;
	}

	public class BoltPoint
	{
		Vector3 point;
		Vector3 basePoint;
		Vector3 offSet;

		public BoltPoint(Vector3 basePoint, Vector3 offSet)
		{
			this.point = basePoint.clone().add(offSet);
			this.basePoint = basePoint;
			this.offSet = offSet;
		}
	}

	public class BoltSegment
	{
		public BoltPoint startBolt;
		public BoltPoint endBolt;
		public Vector3 difference;
		public BoltSegment prevSegment;
		public BoltSegment nextSegment;
		public Vector3 nextDiff;
		public Vector3 prevDiff;
		public float sinPrev;
		public float sinNext;
		/**
		 * The order of important
		 */
		public float weight;
		public int segmentID;
		public int splitID;

		public BoltSegment(BoltPoint startBolt, BoltPoint endBolt, float weight, int segmentID, int splitID)
		{
			this.startBolt = startBolt;
			this.endBolt = endBolt;
			this.weight = weight;
			this.segmentID = segmentID;
			this.splitID = splitID;
			this.calculateDifference();
		}

		public BoltSegment(Vector3 start, Vector3 end)
		{
			this(new BoltPoint(start, new Vector3(0.0D, 0.0D, 0.0D)), new BoltPoint(end, new Vector3(0.0D, 0.0D, 0.0D)), 1.0F, 0, 0);
		}

		public void calculateDifference()
		{
			this.difference = this.endBolt.point.clone().subtract(this.startBolt.point);
		}

		public void calculateEndDifferences()
		{
			if (this.prevSegment != null)
			{
				Vector3 prevdiffnorm = this.prevSegment.difference.clone().normalize();
				Vector3 thisdiffnorm = this.difference.clone().normalize();
				this.prevDiff = thisdiffnorm.add(prevdiffnorm).normalize();
				this.sinPrev = ((float) Math.sin(thisdiffnorm.anglePreNorm(prevdiffnorm.multiply(-1.0F)) / 2.0F));
			}
			else
			{
				this.prevDiff = this.difference.clone().normalize();
				this.sinPrev = 1.0F;
			}
			if (this.nextSegment != null)
			{
				Vector3 nextdiffnorm = this.nextSegment.difference.clone().normalize();
				Vector3 thisdiffnorm = this.difference.clone().normalize();
				this.nextDiff = thisdiffnorm.add(nextdiffnorm).normalize();
				this.sinNext = ((float) Math.sin(thisdiffnorm.anglePreNorm(nextdiffnorm.multiply(-1.0F)) / 2.0F));
			}
			else
			{
				this.nextDiff = this.difference.clone().normalize();
				this.sinNext = 1.0F;
			}
		}

		@Override
		public String toString()
		{
			return this.startBolt.point.toString() + " " + this.endBolt.point.toString();
		}
	}

}