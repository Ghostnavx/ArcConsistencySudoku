import java.util.HashSet;
import java.util.Set;
public class Cell {
	Set<Integer> domain;
	int value;
	
	public Cell(int a){
		domain = new HashSet<Integer>();
		value = a;
	}
	
	public int getVal(){
		return value;
	}
	public void updateCell(int a){
		value = a;
	}
	
	public void updateDomain(Set<Integer> input){
		domain = input;
	}
	
	public Set<Integer> getDomain(){
		return domain;
	}
	
}
