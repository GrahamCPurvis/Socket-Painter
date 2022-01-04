import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;

public class Line extends PaintingPrimitive implements Serializable {
	private Point point1;
	private Point point2;

	public Line(Point p1, Point p2, Color c) {
		super(c);
		point1 = p1;
		point2 = p2;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void drawGeometry(Graphics g) {
		// TODO Auto-generated method stub
		g.drawLine(point1.x, point1.y, point2.x, point2.y);
	}

}
