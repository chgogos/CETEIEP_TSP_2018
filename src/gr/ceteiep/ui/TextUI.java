package gr.ceteiep.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.problem.tsplib.TSPExample;
import org.moeaframework.problem.tsplib.TSPExample.TSPProblem;
import org.moeaframework.problem.tsplib.TSPInstance;
import org.moeaframework.problem.tsplib.Tour;

public class TextUI {

	HashMap<String, File> tspMap = new HashMap<>();

	public static void main(String[] args) {
		TextUI app = new TextUI();
		app.menu();

	}

	void getEUC2DFiles() {
		File dir = new File("data//tsp");
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".tsp");
			}
		});
		for (File f : files) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String s;
				while ((s = br.readLine()) != null) {
					if (s.contains("EDGE_WEIGHT_TYPE")) {
						String v[] = s.split(":");
						if (v[1].trim().equalsIgnoreCase("EUC_2D"))
							tspMap.put(f.getName().substring(0, f.getName().length() - 4), f);

						break;
					}
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void menu() {
		System.out.println("1. List of all EUC_2D problems");
//		System.out.println("2. Load eil51 and display optimal solution");
		System.out.println("2. Load gr9882 and display optimal solution");
		System.out.println("3. Solve using moea framework");
		getEUC2DFiles();
		System.out.print("Enter choice: ");
		Scanner in = new Scanner(System.in);
		int choice = in.nextInt();
		if (choice == 1)
			scenario1();
		else if (choice == 2) {
//			scenario2("eil51", ".//data//tsp//eil51.opt.tour");
			scenario2("gr9882", ".//data//tsp//gr9882.opt.tour");
		}
		else if (choice == 3)
			scenario3();
		else
			System.err.println("Invalid option");
	}

	void scenario1() {
		List<String> aList = new ArrayList<>(tspMap.keySet());
		aList.sort(null);
		int i = 0;
		for (String fn : aList) {
			System.out.printf("%2d %s\n", ++i, fn);
		}
	}

	void scenario2(String tsp, String solution_full_path_name) {
		try {
			TSPInstance instance = new TSPInstance(tspMap.get(tsp));
			System.out.printf("%s %s %d\n", instance.getName(), instance.getComment(), instance.getDimension());
			SolutionViewer solutionviewer = new SolutionViewer(instance);
			solutionviewer.displaySolution(solution_full_path_name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void scenario3() {
		try {
			listOfTSPs();
			Scanner in = new Scanner(System.in);
			System.out.print("\nSelect problem: ");
			String fn = in.next();
			TSPInstance instance = new TSPInstance(tspMap.get(fn));
			System.out.printf("%s %s %d\n", instance.getName(), instance.getComment(), instance.getDimension());
			Problem problem = new TSPProblem(instance);
			Properties properties = new Properties();
			properties.setProperty("swap.rate", "0.7");
			properties.setProperty("insertion.rate", "0.9");
			properties.setProperty("pmx.rate", "0.4");
			Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm("NSGAII", properties, problem);
			for (int i = 0; i < 10; i++) {
				System.out.println("Round " + (i + 1));
				algorithm.step();
			}
			Tour best = TSPExample.toTour(algorithm.getResult().get(0));
			SolutionViewer solutionviewer = new SolutionViewer(instance);
			solutionviewer.setTour(best);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void listOfTSPs() {
		List<String> aList = new ArrayList<>(tspMap.keySet());
		aList.sort(null);

		System.out.println();
		int i = 0;
		for (String s : aList) {
			i++;
			System.out.format("%10s", s);
			if (i % 10 == 0)
				System.out.println();
		}
	}
}
