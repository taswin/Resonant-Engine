package resonantengine.test.transform.region;

import junit.framework.TestCase;
import resonantengine.lib.transform.region.Rectangle;
import resonantengine.lib.transform.vector.Vector2;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by robert on 12/17/2014.
 */
public class RectangleTest extends TestCase
{
    /**
     * Checks too see if the area math is working :P
     */
    public void testArea()
    {
        //For the hell of it
        Rectangle rect = new Rectangle(new Vector2(), new Vector2());
        assertEquals("Expected zero as both corners are zero zero", 0.0, rect.getArea());
        rect = new Rectangle(new Vector2(0, 1), new Vector2(0, 2));
        assertEquals("Expected zero as both corners are in a strait line", 0.0, rect.getArea());

        //Random are checks
        rect = new Rectangle(new Vector2(0, 0), new Vector2(2, 2));
        assertEquals("Expected an exact match for area check one", 4.0, rect.getArea());

        rect = new Rectangle(new Vector2(0, 0), new Vector2(-2, -2));
        assertEquals("Expected an exact match for area check two", 4.0, rect.getArea());

        rect = new Rectangle(new Vector2(-2, -2), new Vector2(2, 2));
        assertEquals("Expected an exact match for area check three", 16.0, rect.getArea());

        rect = new Rectangle(new Vector2(10, 20), new Vector2(-20, -10));
        assertEquals("Expected an exact match for area check four", 900.0, rect.getArea());
    }

    /**
     * Checks if the basic version of the point bounding box check is working
     */
    public void testIsWithin()
    {
        Rectangle rect = new Rectangle(new Vector2(0, 0), new Vector2(2, 2));

        List<Vector2> points_inside = new LinkedList();
        points_inside.add(rect.cornerA());
        points_inside.add(rect.cornerB());
        points_inside.add(rect.cornerC());
        points_inside.add(rect.cornerD());
        points_inside.add(new Vector2(1, 1));

        List<Vector2> points_outside = new LinkedList();
        points_outside.add(new Vector2(-1, -1));
        points_outside.add(new Vector2(0, -1));
        points_outside.add(new Vector2(-1, 0));
        points_outside.add(new Vector2(3, 0));
        points_outside.add(new Vector2(0, 3));
    }
}
