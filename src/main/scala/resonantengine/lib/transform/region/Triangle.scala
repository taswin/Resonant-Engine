package resonantengine.lib.transform.region

import io.netty.buffer.ByteBuf
import resonantengine.api.transform.vector.IVector2
import resonantengine.lib.wrapper.ByteBufWrapper._
import net.minecraft.nbt.{NBTTagList, NBTTagCompound}
import resonantengine.lib.transform.vector.Vector2

/** Triangle shape. Assumes that connections go
  * a -> b -> c -> a forming a triangle shape
 * Created by robert on 12/14/2014.
 */
class Triangle(var a: IVector2, var b: IVector2, var c: IVector2) extends Shape[Triangle]
{
  override def getArea: Double = Math.abs(a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y)) /  2

  override def isWithin2D(vec: IVector2): Boolean = ???

  override def set(other: Triangle): Triangle =
  {
    this.a = other.a;
    this.b = other.b;
    this.c = other.c;
    return this
  }


  /** Checks if the point is inside the shape */
  override def isWithin(x: Double, y: Double): Boolean =
  {
    val p = new Vector2(x, y)
    var ab = new Triangle(a, b, p).getArea
    var bc = new Triangle(b, c, p).getArea
    var ca = new Triangle(c, a, p).getArea
    return (ab + bc + ca) <= getArea
  }

  override def +(amount: Double): Triangle = new Triangle(new Vector2(a.x + amount, a.y + amount), new Vector2(b.x + amount, b.y + amount), new Vector2(c.x + amount, c.y + amount))

  override def +(t: Triangle): Triangle =
  {
    val newA = new Vector2(a.x + t.a.x, a.y + t.a.y)
    val newB = new Vector2(b.x + t.b.x, b.y + t.b.y)
    val newC  = new Vector2(b.x + t.b.x, b.y + t.b.y)
    return new Triangle(newA, newB, newC)
  }

  override def writeByteBuf(data: ByteBuf): ByteBuf =
  {
    data <<< a
    data <<< b
    data <<< c
    return data
  }

  override def writeNBT(nbt: NBTTagCompound): NBTTagCompound =
  {
    val list: NBTTagList  = new NBTTagList();
    list.appendTag(new Vector2(a.x, a.y).writeNBT(nbt))
    list.appendTag(new Vector2(b.x, b.y).writeNBT(nbt))
    list.appendTag(new Vector2(c.x, c.y).writeNBT(nbt))
    nbt.setTag("abc", list);
    return nbt
  }

  override def *(amount: Double): Triangle = new Triangle(new Vector2(a.x * amount, a.y * amount), new Vector2(b.x * amount, b.y * amount), new Vector2(c.x * amount, c.y * amount))

  override def *(t: Triangle): Triangle =
  {
    val newA = new Vector2(a.x * t.a.x, a.y * t.a.y)
    val newB = new Vector2(b.x * t.b.x, b.y * t.b.y)
    val newC  = new Vector2(b.x * t.b.x, b.y * t.b.y)
    return new Triangle(newA, newB, newC)
  }

  /** Distance the shape takes in the X axis */
  override def getSizeX: Double =
  {
    var lower : Double = a.x
    var upper : Double = a.x

    if(b.x < lower)
      lower = b.x
    if(c.x < lower)
      lower = c.x

    if(b.x > upper)
      upper = b.x
    if(c.x > upper)
      upper = c.x

    return upper - lower
  }

  /** Distance the shape takes in the Y axis */
  override def getSizeY: Double =
  {
    var lower : Double = a.y
    var upper : Double = a.y

    if(b.y < lower)
      lower = b.y
    if(c.y < lower)
      lower = c.y

    if(b.y > upper)
      upper = b.y
    if(c.y > upper)
      upper = c.y

    return upper - lower
  }

}