package edu.pku.yangliu.nlp.pdt.common;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ModelComFunc {
	public static void writeData(float[] array, ArrayList<String> strings,
			ArrayList<Integer> rankList, BufferedWriter writer, String prefix) throws Exception {
//		PrintWriter writer2 = new PrintWriter(writer);
		for (int row = 0; row < rankList.size(); row++) {
			writer.write(prefix + "\t" + strings.get(rankList.get(row))
					+ "\t" + array[rankList.get(row)] + "\n");
		}
	}

	public static void writeData(float[] vPhiB2, BufferedWriter writer) throws Exception {
//		PrintWriter writer2 = new PrintWriter(writer);
		for (int row = 0; row < vPhiB2.length; row++) {
			writer.write("\t" + vPhiB2[row] + "\n");
		}
	}

	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param nw
	 * @param writer
	 */
	public static void writeData(int[] nw, BufferedWriter writer) {
		PrintWriter writer2 = new PrintWriter(writer);
		for (int row = 0; row < nw.length; row++) {
			writer2.print("\t" + nw[row] + "\n");
		}
	}

	public static void writeData(double[] vPhiB2, BufferedWriter writer) {
		PrintWriter writer2 = new PrintWriter(writer);
		for (int row = 0; row < vPhiB2.length; row++) {
			writer2.print("\t" + vPhiB2[row] + "\n");
		}
	}
	// public static void writeData(ArrayList<Integer>[] cNP2, BufferedWriter
	// writer) {
	// PrintWriter writer2 = new PrintWriter(writer);
	// writer2 = new PrintWriter(writer);
	// for (int i = 0; i < cNP2.length; i++) {
	// // writer2.printf("%d-th topic:\n", i);
	// for (int j = 0; j < cNP2[i].size(); j++) {
	// // writer2.printf("%s,\t", Doc.getNps().get(cNP2[i].get(j)));
	// }
	// writer2.print("\n\n");
	// }
	// }

	public static void writeData(int[][] phi2, BufferedWriter writer) {
		PrintWriter writer2 = new PrintWriter(writer);
		for (int row = 0; row < phi2.length; row++) {
			// writer2.printf("%d", row);
			for (int col = 0; col < phi2[row].length; col++) {
				writer2.print(phi2[row][col] + "\t");
			}
			writer2.print("\n");
		}
	}

	public static void writeData(float[][] array, BufferedWriter writer) {
		PrintWriter writer2 = new PrintWriter(writer);
		for (int row = 0; row < array.length; row++) {
			// writer2.printf("%d\t", row);
			for (int col = 0; col < array[row].length; col++) {
				writer2.print(array[row][col] + "\t");
			}
			writer2.print("\n");
		}
	}

	public static void writeData(double[][] vph2, PrintWriter writer2) {
		for (int row = 0; row < vph2.length; row++) {
			// writer2.printf("%d", row);
			for (int col = 0; col < vph2[row].length; col++) {
				writer2.print("\t" + vph2[row][col]);
			}
			writer2.print("\n");
		}
	}

	public static void writeData(double[] phi2, PrintWriter writer2) {
		for (int row = 0; row < phi2.length; row++) {
			writer2.print("\t" + phi2[row]);
		}
	}
}
