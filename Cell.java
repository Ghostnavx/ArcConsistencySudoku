import java.util.HashSet;
import java.util.Set;
public class Cell {
	Set<Integer> domain;
	int value;
	
	public Cell(int a){
		domain = new HashSet<Integer>();
		domain.add(a);
		if(a == 0)
			value = 0;
		else
			value = a;
	}
	
	public int getVal(){
		return value;
	}
	public void updateCell(int a){
		domain.clear();
		domain.add(a);
		if(a == 0)
			value = 0;
		else
			value = a;
	}
	
	public void updateDomain(Set<Integer> input){
		domain = input;
	}
	
	public Set<Integer> getDomain(){
		return domain;
	}
	
}
