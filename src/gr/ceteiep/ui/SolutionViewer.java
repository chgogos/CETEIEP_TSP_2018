package gr.ceteiep.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.moeaframework.problem.tsplib.TSPInstance;
import org.moeaframework.problem.tsplib.TSPPanel;
import org.moeaframework.problem.tsplib.Tour;

public class SolutionViewer {

	private TSPInstance instance;
	private TSPPanel panel;
	private JTextArea progressText;
	private JFrame frame;

	public SolutionViewer(TSPInstance instance) {
		this.instance = instance;
		panel = new TSPPanel(instance);
		panel.setAutoRepaint(false);
		progressText = new JTextArea();
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(panel);
		splitPane.setBottomComponent(new JScrollPane(progressText));
		splitPane.setDividerLocation(700);
		splitPane.setResizeWeight(1.0);
		frame = new JFrame(instance.getName());
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(1000, 800);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void setTour(Tour tour) {
		panel.displayTour(tour, Color.RED, new BasicStroke(2.0f));
		progressText.setText(String.format("Tour length = %.2f", tour.distance(instance)));
		panel.repaint();
	}

	public void displaySolution(String tourfn) {
		try {
			Tour tour = new Tour();
			BufferedReader br = new BufferedReader(new FileReader(tourfn));
			String s;
			int i = 0;
			boolean f = false;
			int array[] = new int[instance.getDimension()];
			while ((s = br.readLine()) != null) {
				if (f) {
					array[i] = Integer.parseInt(s);
					i++;
					if (i == array.length)
						break;
				}
				if (s.trim().equalsIgnoreCase("TOUR_SECTION"))
					f = true;
			}
			br.close();
			tour.fromArray(array);
			setTour(tour);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
