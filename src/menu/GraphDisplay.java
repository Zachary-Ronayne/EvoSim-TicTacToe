package menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import menu.component.graph.LineGraph;
import menu.component.graph.LineGraphDetail;

/**
 * handles a JFrame that displays graphs, this is really only used for this particular project
 */
public class GraphDisplay extends JFrame{
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 1000;
	public static final int HEIGHT = 600;
	
	private JPanel screen;
	
	private LineGraph mainGraph;
	
	public GraphDisplay(){
		super("Graphs");
		setVisible(false);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		screen = new JPanel(){
			private static final long serialVersionUID = 1L;
			@Override
			public void paint(Graphics g){
				render(g);
			}
		};
		
		add(screen);
		
		mainGraph = new LineGraph(10, 40, 700, 400, 40, 20, new LineGraphDetail[]{new LineGraphDetail(2f, Color.RED), new LineGraphDetail(2f, Color.BLUE)}){
			@Override
			public void redrawGraph(){
				super.redrawGraph();
				screen.repaint();
			}
		};
		
		mainGraph.link(this);
		
		pack();
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
	}
	
	public void addGraphData(ArrayList<double[]> data){
		mainGraph.updateGraphImage(data);
	}
	
	/**
	 * If the frame is visible, hide it, otherwise make it visisble
	 */
	public void toggleOn(){
		setVisible(!isVisible());
		if(isVisible()) requestFocus();
	}
	
	/**
	 * Redraw the entire screen based on the current data
	 */
	public void reDraw(){
		mainGraph.redrawGraph();
	}
	
	private void render(Graphics g){
		//background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		//graph
		mainGraph.render(g);
		
		//instructions on how to use key controls for the graphs
		g.setColor(Color.BLACK);
		g.setFont(new Font(Main.FONT, Font.PLAIN, 16));
		int y = 50;
		int x = 730;
		g.drawString("Right click and hold to pan camera", x, y+=20);
		g.drawString("Scroll wheel to zoom in and out", x, y+=20);
		g.drawString("Hold shift to zoom only on the x axis", x, y+=20);
		g.drawString("Hold ctrl to zoom only on the y axis", x, y+=20);
		g.drawString("Left click to reset graph", x, y+=20);
		
		//draw information for the graph
		g.setColor(Color.RED);
		g.drawString("Fitness", x, y+=40);
		g.setColor(Color.BLUE);
		g.drawString("Mutability", x, y+=40);
	}
	
}
