import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;

public class Circle extends PaintingPrimitive implements Serializable {
	private Point center;
	private Point radiusPoint;

	public Circle(Point cent, Point r, Color c) {
		super(c);
		center = cent;
		radiusPoint = r;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void drawGeometry(Graphics g) {
		int radius = (int) Math.abs(center.distance(radiusPoint));
		g.drawOval(center.x - radius, center.y - radius, radius * 2, radius * 2);
	}

}
