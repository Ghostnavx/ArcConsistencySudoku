
public class SudokuClient {
	public static void main(String [] args){
		System.out.println("Please select the file from which you wish to read from");
		
		int grid[][] = {{3, 0, 6, 5, 0, 8, 4, 0, 0},
				{5, 2, 0, 0, 0, 0, 0, 0, 0},
				{0, 8, 7, 0, 0, 0, 0, 3, 1},
				{0, 0, 3, 0, 1, 0, 0, 8, 0},
				{9, 0, 0, 8, 6, 3, 0, 0, 5},
				{0, 5, 0, 0, 9, 0, 6, 0, 0},
				{1, 3, 0, 0, 0, 0, 2, 5, 0},
				{0, 0, 0, 0, 0, 0, 0, 7, 4},
				{0, 0, 5, 2, 0, 6, 3, 0, 0}};

		Game test = new Game(grid);
		long startTime = System.currentTimeMillis();
		if(test.basicSolve("STANDARD"))
			System.out.println("Victory!");
		else
			System.err.println("Crap");
		test.printBoard();
		long endTime = System.currentTimeMillis();
		System.out.println("Execution time: " + (endTime - startTime) + "ms");
	}
}
