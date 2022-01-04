import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

public class PaintingPanel extends JPanel {
	private ArrayList<PaintingPrimitive> prims = new ArrayList<PaintingPrimitive>();

	public PaintingPanel() {
		setBackground(Color.WHITE);
		prims = new ArrayList<PaintingPrimitive>();
	}

	public void addPrimitive(PaintingPrimitive obj) {
		this.prims.add(obj);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (PaintingPrimitive obj : prims) {
			obj.draw(g);
		}
	}
}
