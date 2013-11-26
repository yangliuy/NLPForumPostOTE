package edu.pku.yangliu.nlp.pdt.common;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class MatrixUtil {
	// irregular array
	public static int[][] getArray() {
		int [][] num={{1,2,3},{4,5},{2}};
		for(int i = 0; i < num.length; i++) {
			for(int j = 0; j < num[i].length; j++)
				System.out.println(num[i][j]);
		}
		return num;
	}
	public static void printArray(int [][] num) {
		//int [][] num={{1,2,3},{4,5},{2}};
		for(int i = 0; i < num.length; i++) {
			for(int j = 0; j < num[i].length; j++)
				System.out.print(num[i][j] + "\t");
			System.out.println();
		}
	}
	public static void printArray(short [][] num) {
		//int [][] num={{1,2,3},{4,5},{2}};
		for(int i = 0; i < num.length; i++) {
			for(int j = 0; j < num[i].length; j++)
				System.out.print(num[i][j] + "\t");
			System.out.println();
		}
	}
	
	public static void printArray(int[] num) {
		for(int i = 0; i < num.length; i++) {
			System.out.print(num[i] + "\t");
		}
		System.out.println();
	}
	
	public static void printArray(long[] num) {
		for(int i = 0; i < num.length; i++) {
			System.out.print(num[i] + "\t");
		}
		System.out.println();
	}
	
	public static void printArray(double[] num) {
		for(int i = 0; i < num.length; i++) {
			System.out.print(num[i] + "\t");
		}
		System.out.println();
	}
	public static void printArray(boolean[][] bs) {
		for(int i = 0; i < bs.length; i++) {
			for(int j = 0; j < bs[i].length; j++) {
				if(bs[i][j])
					System.out.print("1\t");
				else
					System.out.print("0\t");
			}
			System.out.println();
		}
	}
	public static double sumRow(int[][] matrix, int u) {
		double a = 0.0D;
		for(int m = 0; m < matrix[u].length; m++) {
			a += matrix[u][m];
		}
		return a;
	}
	public static float sum(int[] nW) {
		long a = 0l;
		for(int i = 0; i < nW.length; i++) {
			a += nW[i];
		}
		return a;
	}
	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param ds
	 * @return
	 */
	public static double sum(double[] nW) {
		double a = 0l;
		for(int i = 0; i < nW.length; i++) {
			a += nW[i];
		}
		return a;
	}
	public static int max(int[] flag) {
		int max = flag[0];
		for(int i = 1; i < flag.length; i++) {
			if(flag[i] > max)
				max = flag[i];
		}
		return max;
	}
	public static int min(int[] flag) {
		int min = flag[0];
		for(int i = 1; i < flag.length; i++) {
			if(flag[i] < min)
				min = flag[i];
		}
		return min;
	}
	/**
	 * Unique items in an array
	 * @param values 
	 * @param uniItems
	 * @param counts
	 */
	public static void uniqe(int[] values, ArrayList<Integer> uniItems,
			ArrayList<Integer> counts) {
		uniItems.clear();
		counts.clear();
		for (int i = 0; i < values.length; i++) {
			int index = uniItems.indexOf(values[i]);
			if(index > -1) {
				counts.set(index, counts.get(index) + 1);
			} else {
				uniItems.add(values[i]);
				counts.add(1);
			}
		}
	}
	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param p2
	 * @return 
	 */
	public static boolean checkDouble(double p2) {
		if(p2 < 1E-320 || p2 > 1E304) {
			System.err.println("Double value may overflow or be negative! value: " + p2);
			return true;
		}
		return false;
	}
	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param p
	 * @return
	 */
	public static int sample(double[] p) {
		// cummulate multinomial parameters
		int n = p.length;
		double pt[] = new double[n];
		pt[0] = p[0];
		for (int i = 1; i < n; i++) {
			pt[i] = pt[i - 1] + p[i];
		}

		// scaled sample because of unnormalized p[]
		double rouletter = Math.random() * pt[n - 1];
		int sample = 0;
		for (; sample < n; sample++) {
			if (pt[sample] >= rouletter)
				break;
		}
		
		return sample;
	}
	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param i
	 * @param j
	 * @return
	 */
	public static int min(int i, int j) {
		if(i <= j)
			return i;
		else
			return j;
	}
	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param i
	 * @param j 
	 * @return
	 */
	public static int max(int i, int j) {
		if(i >= j)
			return i;
		else
			return j;
	}
}