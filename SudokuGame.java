import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class SudokuGame {
	Cell [][] gameBoard;
	LinkedList<Arc> arcs;
	int counter;
	public final Set<Integer> FULLSET = new HashSet<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));

	public SudokuGame(int [][] inputBoard){
		counter = 0;

		//create the board
		gameBoard = new Cell [9][9];
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				gameBoard[i][j] = new Cell(inputBoard[i][j], j, i);
			}
		}

		//introduce each cell to its neighbors
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				gameBoard[i][j].setNeighbors(getCellNeighbors(j,i));
			}
		}
	}

	public static void main (String [] args) throws FileNotFoundException{				
		int grid1[][] = {{3, 0, 6, 5, 0, 8, 4, 0, 0},
				{5, 2, 0, 0, 0, 0, 0, 0, 0},
				{0, 8, 7, 0, 0, 0, 0, 3, 1},
				{0, 0, 3, 0, 1, 0, 0, 8, 0},
				{9, 0, 0, 8, 6, 3, 0, 0, 5},
				{0, 5, 0, 0, 9, 0, 6, 0, 0},
				{1, 3, 0, 0, 0, 0, 2, 5, 0},
				{0, 0, 0, 0, 0, 0, 0, 7, 4},
				{0, 0, 5, 2, 0, 6, 3, 0, 0}};

		int grid2[][] = {{8,0,0,0,0,0,0,0,0},
				{0,0,3,6,0,0,0,0,0},
				{0,7,0,0,9,0,2,0,0},
				{0,5,0,0,0,7,0,0,0},
				{0,0,0,0,4,5,7,0,0},
				{0,0,0,1,0,0,0,3,0},
				{0,0,1,0,0,0,0,6,8},
				{0,0,8,5,0,0,0,1,0},
				{0,9,0,0,0,0,4,0,0}};

		int grid3[][] = {{0,0,0,0,3,7,6,0,0},
				{0,0,0,6,0,0,0,9,0},
				{0,0,8,0,0,0,0,0,4},
				{0,9,0,0,0,0,0,0,1},
				{6,0,0,0,0,0,0,0,9},
				{3,0,0,0,0,0,0,4,0},
				{7,0,0,0,0,0,8,0,0},
				{0,1,0,0,0,9,0,0,0},
				{0,0,2,5,4,0,0,0,0}};
		
		/* Add in your own sudoku puzzle either manually or using getGrid() which takes a file path as a parameter.
		 * Include your sudoku grid in the game constructor.
		 * I've added a couple test cases for convenience as well.
		 * The parameters for solve() are the search type, and the ordering type.
		 * Your options for search type are: "basic", "lookahead", "arc".
		 * Your options for ordering type are: "static", "backwards", "mostconstrained", "leastconstrained".
		 */

		SudokuGame game = new SudokuGame(grid3);
		game.printBoard();
		game.Solve("arc", "mostconstrained");
	}

	public static int [][] getGrid(String fileName) throws FileNotFoundException{
		File file = new File(fileName);
		Scanner scan = new Scanner(file);
		int [][] newBoard = new int [9][9];
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				newBoard[i][j] = scan.nextInt();
			}
		}
		scan.close();
		return newBoard;
	}

	public Cell[][] copyBoard(Cell [][] oldBoard){
		Cell [][] newBoard = new Cell[9][9];
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				newBoard[i][j] = new Cell(oldBoard[i][j]);
			}
		}
		return newBoard;
	}

	public void revertValues(Cell [][] copy){
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				gameBoard[i][j].updateDomain(copy[i][j].domain);
			}
		}
	}

	public boolean checkConstraints(Cell b){
		if(b.activated){
			for(Cell a: b.neighbors){
				if(a.domain.equals(b.domain) && a.activated)
					return false;
			}
		}
		return true;
	}

	public ArrayList<Cell> backUpNeighbors(Cell a){
		ArrayList<Cell> backUp = new ArrayList<Cell>();
		for(Cell b: a.neighbors)
			backUp.add(new Cell(b));
		backUp.add(new Cell(a));
		return backUp;
	}

	public void revertNeighbors(ArrayList<Cell> a){
		for(Cell b: a){
			gameBoard[b.y][b.x].domain = new HashSet<Integer>(b.domain);
		}
	}

	public boolean checkNeighborDomains(Cell a){
		for(Cell b: a.neighbors){
			b.domain.removeAll(a.domain);
			if(b.domain.isEmpty())
				return false;
		}
		return true;
	}

	public void setInitialDomains(){
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(!gameBoard[i][j].activated){
					for(Cell b: gameBoard[i][j].neighbors){
						if(b.activated)
							gameBoard[i][j].domain.removeAll(b.domain);
					}
				}
			}
		}
	}

	public void Solve(String searchType, String orderType){
		long startTime;
		switch (searchType.toUpperCase()){

		case "BASIC":
			startTime = System.currentTimeMillis();
			if(simpleSolve(gameBoard, orderType)){
				System.out.println("Solution found:");
				printBoard();
			}
			else
				System.out.println("Could not solve");
			System.out.println("Execution Time: " + (System.currentTimeMillis() - startTime) + "ms");
			System.out.println("Nodes Expanded: " + counter);
			break;

		case "LOOKAHEAD":
			setInitialDomains();
			startTime = System.currentTimeMillis();
			if(lookAheadSolve(gameBoard, orderType)){
				System.out.println("Solution Found:");
				printBoard();
			}
			else
				System.out.println("Could not solve");
			System.out.println("Execution Time: " + (System.currentTimeMillis() - startTime) + "ms");
			System.out.println("Nodes Expanded: " + counter);
			break;

		case "ARC":
			arcs = createArcs();
			if(!fixArc(true)){
				System.err.println("Puzzle is impossible");
				System.exit(1);
			}
			startTime = System.currentTimeMillis();
			
			if(arcSolve(gameBoard, orderType)){
				System.out.println("Solution Found:");
				printBoard();
			}
			else
				System.out.println("Could not solve");
			System.out.println("Execution Time: " + (System.currentTimeMillis() - startTime) + "ms");
			System.out.println("Nodes Expanded: " + counter);
			break;
		}
	}

	public boolean lookAheadSolve(Cell [][] board, String arg){
		counter++;
		int [] coordinates = findEmpty(arg);
		if(coordinates == null)
			return true;

		int x = coordinates[0];
		int y = coordinates[1];
		ArrayList<Cell> backUp = backUpNeighbors(board[y][x]);


		Iterator<Integer> it = gameBoard[y][x].domain.iterator();
		while(it.hasNext()){
			int i = it.next();
			board[y][x].domain = new HashSet<Integer>(Arrays.asList(i));
			board[y][x].activated = true;
			if(checkNeighborDomains(board[y][x])){
				if(lookAheadSolve(board, arg))
					return true;
			}
			board[y][x].activated = false;
			revertNeighbors(backUp);
		}
		return false;
	}

	public boolean simpleSolve(Cell [][] board, String arg){
		counter++;
		int [] coordinates = findEmpty(arg);
		if(coordinates == null)
			return true;

		int x = coordinates[0];
		int y = coordinates[1];

		for(int i = 1; i < 10; i++){
			board[y][x].domain = (new HashSet<Integer>(Arrays.asList(i)));
			board[y][x].activated = true;
			//printBoard();

			if(checkConstraints(board[y][x])){
				if(simpleSolve(board, arg))
					return true;
			}

			board[y][x].activated = false;
			board[y][x].domain = new HashSet<Integer>(Arrays.asList(0));
		}
		return false;
	}

	public boolean arcSolve(Cell [][] board, String arg){
		Cell [][] backUp = copyBoard(board);
		counter++;
		int [] coordinates = findEmpty(arg);
		if(coordinates == null)
			return true;

		int x = coordinates[0];
		int y = coordinates[1];


		Iterator<Integer> it = gameBoard[y][x].domain.iterator();
		while(it.hasNext()){
			int i = it.next();
			board[y][x].updateDomain (new HashSet<Integer>(Arrays.asList(i)));
			board[y][x].activated = true;
			//printBoard();
			if(fixArc(false)){
				if(arcSolve(board, arg))
					return true;
			}
			board[y][x].activated = false;
			revertValues(backUp);
		}
		return false;
	}

	public boolean fixArc(boolean printStuff){
		LinkedList<Arc> arc = new LinkedList(arcs);
		Arc temp;
		while(!arc.isEmpty()){
			temp = arc.remove();
			if(removeInconsistent(temp)){
				for(Cell b: temp.a.neighbors){
					arc.add(new Arc(b, temp.a));
				}
			}
		}

		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(gameBoard[i][j].domain.size() == 0){
					if(printStuff)
						System.err.println("Empty domain found at: " + j + "," + i);
					return false;
				}
			}
		}
		return true;
		//System.out.println(county);
	}

	public boolean removeInconsistent(Arc arc){
		boolean returnStuff = false;
		boolean check = true;
		Iterator<Integer> it = arc.a.domain.iterator();
		while(it.hasNext()){
			int a = it.next();
			for(int b: arc.b.domain){
				check = false;
				if(b != a){
					check = true;
					break;
				}
			}
			if(!check){
				it.remove();
				returnStuff = true;
			}
		}
		return returnStuff;
	}

	public LinkedList<Arc> createArcs(){
		LinkedList<Arc> arcs = new LinkedList<Arc>();
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				arcs.addAll(generateNeighbors(j, i));
			}
		}
		return arcs;
	}

	public LinkedList<Arc> generateNeighbors(int x, int y){
		LinkedList<Arc> neighbors = new LinkedList<Arc>();
		for(int i = 0; i < 9; i++){
			if(x!= i){
				neighbors.add(new Arc(gameBoard[y][x], gameBoard[y][i]));
				//arcCounter++;
			}
			if(y!= i){
				neighbors.add(new Arc(gameBoard[y][x], gameBoard[i][x]));
				//arcCounter++;
			}
		}

		int startX = x- x%3;
		int startY = y- y%3;

		for(int i = startY; i < startY + 3; i++){
			for(int j = startX; j < startX + 3; j++){
				if(i!=y && j!=x){
					neighbors.add(new Arc(gameBoard[y][x], gameBoard[i][j]));
					//arcCounter++;
				}
			}
		}
		return neighbors;
	}

	public LinkedList<Cell> getCellNeighbors(int x, int y){
		LinkedList<Cell> neighbors = new LinkedList<Cell>();
		for(int i = 0; i < 9; i++){
			if(x!= i)
				neighbors.add(gameBoard[y][i]);
			if(y!= i)
				neighbors.add(gameBoard[i][x]);
		}

		int startX = x- x%3;
		int startY = y- y%3;

		for(int i = startY; i < startY + 3; i++){
			for(int j = startX; j < startX + 3; j++){
				if(i!=y && j!=x)
					neighbors.add(gameBoard[i][j]);
			}
		}

		return neighbors;
	}

	public int [] findEmpty(String arg){
		long start = System.currentTimeMillis();
		int [] coordinates = new int[2];
		switch(arg.toUpperCase()){
		case "STATIC":
			return standard();

		case "BACKWARDS":
			return backwards();

		case "RANDOM":
			return random();

		case "LEASTCONSTRAINED":
			return leastConstrained();

		case "MOSTCONSTRAINED":
			return mostConstrained();
		}
		System.err.println("Syntax error on ordering parameter");
		System.exit(1);
		return null;
	}

	public int [] mostConstrained(){
		int constraints = 10;
		Cell chosen = null;
		boolean foundCell = false;
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(!gameBoard[i][j].activated){
					//automatically choose it if it has only one option
					if(gameBoard[i][j].domain.size() == 1){
						chosen = gameBoard[i][j];
						foundCell = true;
						break;
					}
					if(gameBoard[i][j].domain.size() < constraints){
						chosen = gameBoard[i][j];
						constraints = chosen.domain.size();
					}

				}
			}
			if(foundCell)
				break;
		}
		if (chosen == null)
			return null;

		int [] coordinates = {chosen.x, chosen.y};
		return coordinates;
	}

	public int [] leastConstrained(){
		int constraints = 0;
		Cell chosen = null;
		boolean foundCell = false;
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(!gameBoard[i][j].activated){
					//automatically choose it if it has a full domain
					if(gameBoard[i][j].domain.size() == 9){
						chosen = gameBoard[i][j];
						foundCell = true;
						break;
					}
					if(gameBoard[i][j].domain.size() > constraints){
						chosen = gameBoard[i][j];
						constraints = chosen.domain.size();
					}

				}
			}
			if(foundCell)
				break;
		}
		if (chosen == null)
			return null;

		int [] coordinates = {chosen.x, chosen.y};
		return coordinates;
	}

	public int [] random(){
		ArrayList<Cell> openCells = new ArrayList<Cell>();
		Random rand = new Random();
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(!gameBoard[i][j].activated)
					openCells.add(gameBoard[i][j]);
			}
		}
		if(openCells.isEmpty())
			return null;
		int randomNum = rand.nextInt(openCells.size());
		Cell chosen = openCells.get(randomNum);
		int [] coordinates = {chosen.x, chosen.y};
		return coordinates;
	}

	public int [] standard(){
		int [] coordinates = new int[2];
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(!gameBoard[i][j].activated){
					coordinates[0] = j;
					coordinates[1] = i;
					return coordinates;
				}	
			}
		}
		return null;
	}

	public int [] backwards(){
		int [] coordinates = new int[2];
		for(int i = 8; i >=0; i--){
			for(int j = 8; j >= 0; j--){
				if(!gameBoard[i][j].activated){
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
				if(!gameBoard[i][j].activated)
					System.out.print("_ ");
				else
					System.out.print(gameBoard[i][j].getValue() + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
}
