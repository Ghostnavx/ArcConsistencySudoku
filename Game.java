import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
public class Game {
	static Cell [][] gameBoard;
	static int primaryGrid[][];
	final static Set<Integer> FULLSET = new HashSet<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));

	//stores the domains
	static ArrayList<Set<Integer>> rowVals;
	static ArrayList<Set<Integer>> colVals;
	static ArrayList<Set<Integer>> boxVals;


	public Game(int [][] grid){
		rowVals = new ArrayList<Set<Integer>>();
		colVals = new ArrayList<Set<Integer>>();
		boxVals = new ArrayList<Set<Integer>>();
		gameBoard = new Cell[9][9];
		primaryGrid = new int[9][9];
		for(int i = 0; i < 9; i++){
			rowVals.add(new HashSet<Integer>());
			colVals.add(new HashSet<Integer>());
			boxVals.add(new HashSet<Integer>());
			for(int j = 0; j < 9; j++){
				gameBoard[i][j] = new Cell(grid[i][j]);
				primaryGrid[i][j] = grid[i][j];
			}
		}
		printBoard();
		generateDomains();
		enforceArc();
	}

	public boolean basicSolve(String arg){
		int [] coordinates = findEmpty();
		if(coordinates == null)
			return true;

		int x = coordinates[0];
		int y = coordinates[1];

		switch (arg){
		case "STANDARD":
			for(int val = 1; val < 10; val++){
				if(isValid(x, y, val)){
					gameBoard[y][x].updateCell(val);

					if(basicSolve(arg))
						return true;

					gameBoard[y][x].updateCell(0);
				}
			}
			break;

		case "DOMAIN":
			for(int a: gameBoard[y][x].getDomain()){
					gameBoard[y][x].updateCell(a);

					if(basicSolve(arg))
						return true;

					gameBoard[y][x].updateCell(0);
			}
			break;
		}
		return false;
	}

	public void generateDomains(){
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(gameBoard[i][j].getVal() != 0){
					rowVals.get(i).add(gameBoard[i][j].getVal());
					colVals.get(j).add(gameBoard[i][j].getVal());
					boxVals.get(findStupidSquare(j, i)).add(gameBoard[i][j].getVal());
				}
			}
		}
	}

	public static int findStupidSquare(int x, int y){
		int startX = x - x%3;
		int startY = y - y%3;
		int boxNum = (startX/3) + startY;

		return boxNum;
	}

	public static boolean enforceArc(){
		Set<Integer> temp;
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(gameBoard[i][j].getVal() == 0){
					//start off with full set of possible values
					temp = new HashSet<Integer>(FULLSET);
					//remove all impossible moves from set
					temp.removeAll(rowVals.get(i));
					temp.removeAll(colVals.get(j));
					temp.removeAll(boxVals.get(findStupidSquare(j, i)));
					if(temp.isEmpty())
						return false;
					gameBoard[i][j].updateDomain(temp);
				}
			}
		}
		return true;
	}

	public static boolean isValid(int x, int y, int val){
		return rowGood(y, val) && colGood(x, val) && boxGood(x, y, val);
	}

	public static boolean rowGood(int y, int val){
		for(int i = 0; i < 9; i++){
			if(gameBoard[y][i].getVal() == val)
				return false;
		}
		return true;
	}

	public static boolean colGood(int x, int val){
		for(int i = 0; i < 9; i++){
			if(gameBoard[i][x].getVal() == val)
				return false;
		}
		return true;
	}

	public static boolean boxGood(int x, int y, int val){
		int startX = x - x%3;
		int startY = y - y%3;

		for(int i = startY; i < startY + 3; i++){
			for(int j = startX; j < startX + 3; j++){
				if(gameBoard[i][j].getVal() == val)
					return false;
			}
		}
		return true;
	}

	public static int [] findEmpty(){
		int [] coordinates = new int[2];
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(gameBoard[i][j].getVal() == 0){
					coordinates[0] = j;
					coordinates[1] = i;
					return coordinates;
				}	
			}
		}
		return null;
	}

	public void printBoard(){
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(gameBoard[i][j].getVal() == 0)
					System.out.print("_ ");
				else
					System.out.print(gameBoard[i][j].getVal() + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void dummy()
	{
	}
	}
}
