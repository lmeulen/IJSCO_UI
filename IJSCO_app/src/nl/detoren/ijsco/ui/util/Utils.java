/**
 * Copyright (C) 2018 Lars Dam
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.0
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * See: http://www.gnu.org/licenses/gpl-3.0.html
 *  
 * Problemen in deze code:
 * - ... 
 * - ...
 */

package nl.detoren.ijsco.ui.util;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import nl.detoren.ijsco.ui.Mainscreen;

/**
 *
 * @author Lars Dam
 */
public class Utils {

	private final static Logger logger = Logger.getLogger(Mainscreen.class.getName());
	
    public static void fixedComponentSize(Component c, int width, int height) {
        c.setMinimumSize(new Dimension(width, height));
        c.setMaximumSize(new Dimension(width, height));
        c.setPreferredSize(new Dimension(width, height));
        c.setSize(new Dimension(width, height));
    }
    
    public static void fixedColumSize(TableColumn c, int width) {
        c.setMinWidth(width);
        c.setMaxWidth(width);
    }
    
  //Displays a 2d array in the console, one line per row.
    public static void printMatrix(ArrayList<ArrayList<Integer>> grid) {
        for(int r=0; r<grid.size(); r++) {
           for(int c=0; c<grid.get(r).size(); c++)
               System.out.print(grid.get(r).get(c) + "\t");
           System.out.println();
        }
    }

    public static void printMatrix(int grid[][]) {
        for(int r=0; r<grid.length; r++) {
           for(int c=0; c<grid[0].length; c++)
               System.out.print(grid[r][c] + "\t");
           System.out.println();
        }
    }

    public static void printMatrix(int grid[]) {
        for(int r=0; r<grid.length; r++) {
               System.out.print(grid[r] + " \n");
        }
    }

    public static int[][] add2DArrays(int A[][], int B[][]){
    	// Just for cubic equal size arrays!
    	if (A.length==0) return A;
    	int C[][] = new int[A.length][A.length];
    	for (int i=0;i<A.length;i++) {
    		for (int j=0;j<B.length;j++) {
    			C[i][j]=A[i][j]+B[i][j];
    		}
    	}
    	return C;
    }

    public static int[][] add2DArrays(double mf1, int A[][], double mf2, int B[][]){
    	// Just for [X+1][X] arrays! with index in first row.
    	if (A.length==0) return A;
    	int C[][] = new int[A.length][A[0].length];
    	for (int i=0;i<A.length;i++) {
    		for (int j=0;j<A[0].length;j++) {
    			if (j==0) {
    				// C[i][j]=(int) (A[i][j]+B[i][j]);
    				C[i][j]=(int) (A[i][j]);
    			} else {
    				C[i][j]=(int) (mf1*A[i][j]+mf2*B[i][j]);
    			}
    		}
    	}
    	return C;
    }

    public static int triagonalsum(int A[][]){
    	// Just for [X][X] arrays!
    	if (A.length==0) return 0;
    	int sum = 0;
    	for (int i=0;i<A.length;i++) {
    		for (int j=Math.max(0, i-1);j<Math.min(i+2, A.length);j++) {
    				sum += A[i][j];
    		}
    	}
    	return sum;
    }

    public static int triagonalsum(int A[][], int indexrow){
    	// Just for [X+1][X] arrays! with index (indexrow=1) in first row.
    	int sum = 0;
    	for (int i=0;i<A.length;i++) {
    		for (int j=Math.max(0, i-1);j<Math.min(i+2, A.length);j++) {
    				sum += A[i][indexrow+j];
    		}
    	}
    	return sum;
    }
    
    public static boolean containing(int[] haystack, int needle) {
    	for(int hay: haystack){
    		if(hay == needle)
    			return true;
    	}
    	return false;
    }

    public static boolean containing(ArrayList<Integer> haystack, int needle) {
    	for(int hay: haystack){
    		if(hay == needle)
    			return true;
    	}
    	return false;
    }

    public static int[][] removerowandcolumnfrom2D(int A[][], int[] B, int indexrow) {
    	// Just for cubic equal size arrays!
    	int C[][] = new int[A.length-B.length][A[0].length-B.length];
    	int p = 0;
    	for (int i=0;i<A.length;i++) {
    		int q = 0;
			if (!(Utils.containing(B,A[i][indexrow-1]))) {
				C[p][q]=A[i][0];
				q++;
				for (int j=1;j<A[0].length;j++) {
					if (!(Utils.containing(B,A[j-1][indexrow-1]))) {
						C[p][q]=A[i][j];
						q++;
					}
   				}
				p++;
   			}
    	}
    	return C;
    }

    public static int[][] removerowandcolumnfrom2D(int A[][], ArrayList<Integer> B, int indexrow) {
    	// Just for cubic equal size arrays!
    	int C[][] = new int[A.length-B.size()][A[0].length-B.size()];
    	int p = 0;
    	for (int i=0;i<A.length;i++) {
    		int q = 0;
			if (!(Utils.containing(B,A[i][indexrow-1]))) {
				C[p][q]=A[i][0];
				q++;
				for (int j=1;j<A[0].length;j++) {
					if (!(Utils.containing(B,A[j-1][indexrow-1]))) {
						C[p][q]=A[i][j];
						q++;
					}
   				}
				p++;
   			}
    	}
    	return C;
    }

	/**
	 * Bepaal de nieuwe integer waarde voor een textfield Als het tekstveld een
	 * geldig getal bevat, wordt deze waarde geretourneerd anders de oude waarde
	 *
	 * @param text
	 * @param oldValue
	 * @return
	 */
	public static int newIntegerValue(JTextField text, int oldValue) {
		int value;
		try {
			value = Integer.parseInt(text.getText());
		} catch (Exception e) {
			value = oldValue;
		}
		text.setText(Integer.toString(value));
		return value;
	}

	/**
	 * Bepaal de nieuwe comma seperated waarde voor een textfield Als het tekstveld een
	 * geldig getal bevat, wordt deze waarde geretourneerd anders de oude waarde
	 *
	 * @param text
	 * @param oldValue
	 * @return
	 */
	public static String newCSValue(JTextField text, String oldValue) {
		String value;
		try {
			logger.log(Level.INFO, "text : " + text.getText());
			Scanner scanner = new Scanner(text.getText());
			scanner.useDelimiter(",");
			List<Integer> list = new ArrayList<Integer>();
			while (scanner.hasNextInt()) {
				int next = scanner.nextInt();
				logger.log(Level.INFO, "nextInt : " + next);
			    list.add(next);
			}
			scanner.close();
			value = list.stream()
			     .map(i -> i.toString())
			     .collect(Collectors.joining(","));
			logger.log(Level.INFO, "Value : " + value);
			}
		catch (Exception e) {
			value = oldValue;
			logger.log(Level.INFO, "Exception: Rest Value : " + value);			
		}
		text.setText(value);
		return value;
	}

	public static String listToString(List<Integer> nobyes) {	
		String value;
		value = nobyes.stream()
		     .map(i -> i.toString())
		     .collect(Collectors.joining(", "));
		return value;
	}

	public static List<Integer> stringToList(String nobyes) {	
		Scanner scanner = new Scanner(nobyes);
		scanner.useDelimiter(",");
		List<Integer> list = new ArrayList<Integer>();
		while (scanner.hasNextInt()) {
		    list.add(scanner.nextInt());
		}
		scanner.close();
		return list;
	}

	public static int toMask(List<Integer> nobyes) {
		int value = 0;
		for (int groep : nobyes) {
			value += 2^groep;
		}
		return value;
	}

	public static String stackTraceToString(Throwable e) {
	    StringBuilder sb = new StringBuilder();
	    for (StackTraceElement element : e.getStackTrace()) {
	        sb.append(element.toString());
	        sb.append("\n");
	    }
	    return sb.toString();
	}
}
