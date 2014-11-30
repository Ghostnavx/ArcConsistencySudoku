import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
public class Game {
	static Cell [][] gameBoard;
	static int primaryGrid[][];
	final static Set<Integer> FULLSET = new HashSet<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));
	int counter;

	//stores the domains
	//will be used to construct the domain for each cell
	//preventing the need to loop through row/col/box to check
	static ArrayList<Set<Integer>> rowVals;
	static ArrayList<Set<Integer>> colVals;
	static ArrayList<Set<Integer>> boxVals;

	//initializes the game
	//TODO: make the domain check faster.  Expands less nodes but takes more time.

	public Game(int [][] grid){
		counter = 0;
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
		generateDomains();
		enforceArc();
		printBoard();
	}

	//backtracking 
	public boolean basicSolve(String arg){
		//printBoard();
		counter++;
		int [] coordinates = findEmpty();
		if(coordinates == null)
			return true;

		int x = coordinates[0];
		int y = coordinates[1];

		switch (arg){
		//attempts to assign numbers 1-9
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

			//only attempts to assign numbers in the domain
		case "DOMAIN":

			calculateDomain(x,y);
			
			//choose from domain values (no wasted time checking bad values)
			for(int i: gameBoard[y][x].getDomain()){

				//assign value
				gameBoard[y][x].updateCell(i);

				//REDO CONSTRAINTS HERE
				addToDomains(x, y, i);

				//checkdomains takes too long, need to speed it up
				if(checkDomains(x,y)){
					if(basicSolve(arg))
						return true;
				}
				removeFromDomains(x,y,i);

				gameBoard[y][x].updateCell(0);
			}
		}
		return false;
	}

	public boolean checkDomains(int x, int y){
		//make sure all cells in the row/col have a value
		for(int i = 0; i < 9; i++){
			if(gameBoard[y][i].getVal() == 0){
				if(!calculateDomain(i,y))
					return false;
			}
			if(gameBoard[i][x].getVal() == 0){
				if(!calculateDomain(x,i))
					return false;
			}
		}

		int startX = x - x%3;
		int startY = y - y%3;

		//make sure stupid boxes have a value
		for(int i = startY; i < startY + 3; i++){
			for(int j = startX; j < startX + 3; j++){
				if(gameBoard[i][j].getVal() == 0){
					if(!calculateDomain(j,i))
						return false;
				}
			}
		}
		return true;

	}
	public boolean calculateDomain(int x, int y){
		Set<Integer> temp = new HashSet<Integer>(FULLSET);
		temp.removeAll(rowVals.get(y));
		temp.removeAll(colVals.get(x));
		temp.removeAll(boxVals.get(findStupidSquare(x,y)));
		if(temp.isEmpty())
			return false;
		gameBoard[y][x].updateDomain(temp);
		return true;
	}

	public void addToDomains(int x, int y, int val){
		rowVals.get(y).add(val);
		colVals.get(x).add(val);
		boxVals.get(findStupidSquare(x,y)).add(val);
	}

	public void removeFromDomains(int x, int y, int val){
		rowVals.get(y).remove(val);
		colVals.get(x).remove(val);
		boxVals.get(findStupidSquare(x,y)).remove(val);
	}

	//loops through all 81 cells to check constraints
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

	//
	public static int findStupidSquare(int x, int y){
		int startX = x - x%3;
		int startY = y - y%3;
		int boxNum = (startX/3) + startY;

		return boxNum;
	}

	//This would be arc elimination in a way
	//it removes all impossible values from the domain
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

	//the union validitor, basically if row, col and box are all good
	//then the solution is valid
	public static boolean isValid(int x, int y, int val){
		return rowGood(y, val) && colGood(x, val) && boxGood(x, y, val);
	}

	//row validator
	public static boolean rowGood(int y, int val){
		for(int i = 0; i < 9; i++){
			if(gameBoard[y][i].getVal() == val)
				return false;
		}
		return true;
	}

	//column validator
	public static boolean colGood(int x, int val){
		for(int i = 0; i < 9; i++){
			if(gameBoard[i][x].getVal() == val)
				return false;
		}
		return true;
	}

	//box validator
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

	//finds empty squares and returns the coords as an int array
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

	//prints the board....
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
	

	
}
