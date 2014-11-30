import java.util.HashSet;
import java.util.Set;
public class Cell {
	Set<Integer> domain;
	int value;
	
	//initializes a cell for every square in the sodoku game
	public Cell(int a){
		domain = new HashSet<Integer>();
		value = a;
	}
	
	//gets the value of the cell
	public int getVal(){
		return value;
	}
	
	//updates the cell with a proper value
	public void updateCell(int a){
		value = a;
	}
	
	//updates the domain after elimination
	public void updateDomain(Set<Integer> input){
		domain = input;
	}
	
	//returns a set of int domains for a cell
	public Set<Integer> getDomain(){
		return domain;
	}
	
}
