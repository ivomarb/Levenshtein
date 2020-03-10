
/**
 * Levenshtein Distance
 * 
 * References Used:
 * 
 * - https://en.wikipedia.org/wiki/Levenshtein_distance
 * - https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance
 * - https://www.baeldung.com/java-levenshtein-distance
 * - https://people.cs.pitt.edu/~kirk/cs1501/assignments/editdistance/Levenshtein%20Distance.htm
 * 
 * The Levenshtein distance is a measure of dissimilarity between two Strings. Mathematically, 
 * given two Strings x and y, the distance measures the minimum number of character edits required 
 * to transform x into y.
 * 
 * @author Ivomar Brito Soares, ivomarbsoares@gmail.com
 *
 */
public class Levenshtein_Algorithm {
	
	/**
	 *  
	 * Implementation of the algorithm without allocating the full matrix, but an optimized version, 
	 * which allocates only one column at a time.
	 * 
	 * @param token1
	 * @param token2
	 * @return
	 */
	public static int levenshtein(String token1, String token2) {
	    if (token1 == null || token1.length() == 0) return token2.length();
	    if (token2 == null || token2.length() == 0) return token1.length();
	    
	    int[] column = fillingInitialColumn(token1, token2);
	    
	    // The first column is initialized, the rest of the matrix can now be computed.
	    ComputeRestOfMatrixInfo  info = null;
	    for (int index2 = 1; index2 < token2.length(); index2++) {
	    	info = computeRestOfMatrix( token2.charAt(0), token1, token2,index2, token1.length(), column);
	    }

	    return info.getAbove();
	  }  
	
	/**
	 * 
	 * Modified version of the levenshtein method above which makes an early exit if the distance exceeds a maximum distance. 
	 * Returns maxDist + 1 in this case.
	 * 
	 * @param token1
	 * @param token2
	 * @param maxDist
	 * @return
	 */
	public static int levenshtein(String token1, String token2, int maxDist) {
	    if (token1 == null || token1.length() == 0) return token2.length();
	    if (token2 == null || token2.length() == 0) return token1.length();
	     
	    int[] column = fillingInitialColumn(token1, token2);

	    // The first column is initialized, the rest of the matrix can now be computed.
	    ComputeRestOfMatrixInfo  info = null;
	    for (int index2 = 1; index2 < token2.length(); index2++) {
	    	info = computeRestOfMatrix( token2.charAt(0), token1, token2,index2, token1.length(), column);

	      // Checking if the loop can be stopped because it will be greater than maxDist.
	      if (info.getSmallest() > maxDist) return maxDist + 1;
	    }

	    return info.getAbove();
	  }  
	/**
	 * Allocating just one column instead of the entire matrix, in order to be more efficient and save space.  
	 * This enables the algorithm to be somewhat faster.  The first cell is always the virtual first row.
	 * 
	 * @param token1
	 * @param token2
	 * @return
	 */
	private static int[] fillingInitialColumn(String token1, String token2) {
		
	    
	    int[] column = new int[token1.length() + 1];

	    // Initially, the initial column will be filled. A separate loop is used for this, 
	    // because in this situation the basis for comparison is not the previous column, but a virtual first column.
	    column[0] = 1; // virtual first row
	    for (int index1 = 1; index1 <= token1.length(); index1++) {
	      int cost = token1.charAt(index1 - 1) == token2.charAt(0) ? 0 : 1;

	      // Lowest of three: above (column[index1 - 1]), aboveleft: index1 - 1,
	      // left: index1. Latter cannot possibly be lowest, so is ignored.
	      column[index1] = Math.min(column[index1 - 1], index1 - 1) + cost;
	    }
	    
	    return column;
	}
	
	
	/**
	 * The first column is initialized, and the rest of the matrix can now be computed.
	 * 
	 * @param charToken2
	 * @param token1
	 * @param token2
	 * @param index2
	 * @param token1Length
	 * @param column
	 * @return
	 */
	private static ComputeRestOfMatrixInfo computeRestOfMatrix( char charToken2, 
																String token1, 
																String token2, 
																int index2, 
																int token1Length,
																int[] column) {

		int above = 0;

		charToken2 = token2.charAt(index2);
		above = index2 + 1; // virtual first row

		int smallest = token1Length * 2; // used to implement cutoff
		for (int index1 = 1; index1 <= token1Length; index1++) {
			int cost = token1.charAt(index1 - 1) == charToken2 ? 0 : 1;
			int value = Math.min(Math.min(above, column[index1 - 1]), column[index1]) + cost;
			column[index1 - 1] = above; // write previous
			above = value;           	// keep current
			smallest = Math.min(smallest, value);
		}
		column[token1Length] = above;

		// Setting output values.
		ComputeRestOfMatrixInfo info = new ComputeRestOfMatrixInfo();

		info.setAbove(above);
		info.setSmallest(smallest);

		return info;
	}
	
	/**
	 * Accessory class to return the values computed by method computeRestOfMatrix.
	 * 
	 * @author Ivomar Brito Soares
	 *
	 */
	private static class ComputeRestOfMatrixInfo{
		
		private int smallest;
		private int above;
				
		public int getSmallest() {
			return smallest;
		}
		public void setSmallest(int smallest) {
			this.smallest = smallest;
		}
		public int getAbove() {
			return above;
		}
		public void setAbove(int above) {
			this.above = above;
		}	
	}
	

	public static void main(String[] args) {
		
		// Test case.
		long start = System.currentTimeMillis();
		System.out.println(levenshtein("Haus", "Maus"));
		System.out.println(levenshtein("Haus", "Mausi"));
		System.out.println(levenshtein("Haus", "Häuser"));
		System.out.println(levenshtein("Kartoffelsalat", "Runkelrüben"));
		long finish = System.currentTimeMillis();
		long timeElapsedItem2InMillis = finish - start;
		
		// Calculating used memory.
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        long memoryItem2InBytes = runtime.totalMemory() - runtime.freeMemory();
		
		System.out.println("");
		
		// Test case.
		start = System.currentTimeMillis();
		System.out.println(levenshtein("Haus", "Maus", 2));
		System.out.println(levenshtein("Haus", "Mausi", 2));
		System.out.println(levenshtein("Haus", "Häuser", 2));
		System.out.println(levenshtein("Kartoffelsalat", "Runkelrüben", 2));
		finish = System.currentTimeMillis();		
		long timeElapsedItem3InMillis = finish - start;
		
		// Calculating used memory.
        runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        long memoryItem3InBytes = runtime.totalMemory() - runtime.freeMemory();
		
		// Performance Measurement.
		System.out.println("");
		
		// First Measurement of performance: wall clock time.
		System.out.println("Method item 2 elapsed time in miliseconds for four method calls " + timeElapsedItem2InMillis);
		System.out.println("Method item 3 elapsed time in miliseconds for four method calls " + timeElapsedItem3InMillis);
		
		// Second Measurement of performance: algorithmic complexity.
		// In both methods, we have a nested loop that loops through all the chars of token1 and token2.
		// Considering n1 the length of token 1 and n2 the length of token2, the big O complexity of both 
		// implementations in worst case is O(n1.n2).
		System.out.println("");
		System.out.println("Algorithm complexity is O(n1.n2), see comments for more details.");
		
		// Second Measurement of performance: used memory.
		System.out.println("");
		System.out.println("Method item 2 used memory in bytes after four method calls " + memoryItem2InBytes);
		System.out.println("Method item 3 used memory in bytes after four method calls " + memoryItem3InBytes);
		
	}

}
	

