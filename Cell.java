import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class Cell {
	public LinkedList<Cell> neighbors;
	public Set<Integer> domain;
	public Set<Integer> oldDomain;
	public boolean activated;
	int x, y;

	public Cell(){
		domain = new HashSet<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));
		activated = false;
	}

	//copy constructor
	public Cell(Cell a){
		domain = new HashSet<Integer>(a.domain);
		oldDomain = new HashSet<Integer>(a.oldDomain);
		activated = a.activated;
		x = a.x;
		y = a.y;
		neighbors = new LinkedList<Cell>();
		
		for(Cell b :a.neighbors){
			neighbors.add(b);
		}
	}

	public Cell(int a, int newX, int newY){
		if(a == 0){
			domain = new HashSet<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));
			activated = false;
		}
		else{
			domain = new HashSet<Integer>(Arrays.asList(a));
			activated = true;
		}
		x = newX;
		y = newY;
		oldDomain = new HashSet<Integer>(domain);
	}

	public void setNeighbors(LinkedList<Cell> newNeighbors){
		neighbors = newNeighbors;
	}

	public void updateDomain(Set<Integer> newDomain){
		oldDomain = new HashSet<Integer>(domain);
		domain = new HashSet<Integer>(newDomain);
	}
	public void saveDomain(){
		oldDomain = new HashSet<Integer>(domain);
	}

	public void revertDomain(){
		domain = new HashSet<Integer>(oldDomain);
	}

	public void printNeighbors(){
		System.out.println("Neighbors of (" + x + "," + y + "):");
		for(Cell a:neighbors){
			System.out.println("(" + a.x + "," + a.y + ")");
		}
	}

	public boolean checkConstraints(){
		for(int i: domain){
			for(Cell a: neighbors){
				for(int q: a.domain){
					if(i == q && a.activated)
						return false;
				}
			}
		}
		return true;
	}

	public int getValue(){
		Integer [] temp;
		if(!activated){
			System.err.println("You're dumb, error in getValue() method in Cell.java");
			System.exit(1);
			return 0;
		}
		else{
			for(Integer a : domain){
				return a;
			}
		}
		return 0;
	}
}
