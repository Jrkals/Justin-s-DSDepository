/**
 * DSNode class
 *
 * Holds some fields like winner, children
 *
 * used to build a game tree, and maybe other uses, too.
 *
 * Author DS2016
 * Justin Kalan 
 */

// package ds2016;

class DSNode< E >{
	// The winner of the tree that has this node as root
	private int winner;	// 0 = tie, 1, 2, 3, ... = the wining player		
	private E   board;
	private DSArrayList<DSNode> children = new DSArrayList<DSNode>();
	private int      numChildren = 0;

	public void setBoard(E b){
		board = b;
	}

	public DSArrayList<DSNode> getChildren(){
		return children;
	}

	public void setChildren(DSArrayList<DSNode> ch){
		children = ch;
	}

	public void addChild(DSNode ch){
		children.add(ch);
		numChildren++;
	}

	// a getter for the numChildren variable
	public int getNumChildren(){
		return numChildren;
	}

	// a getter for the board
	public E getBoard(){
		return board;
	}

	public int getWinner(){
		return winner;
	}


	public void setWinner(int w){
		this.winner = w;
	}

	public int numLeaves(DSNode tree, int count){
		if(tree.getNumChildren() == 0)
			count++;
		else {
			for(int i = 0; i < tree.getNumChildren(); i++)
				numLeaves(children.get(i), count);
		}
		return count;
	}

	public int numLeaves2(){
		int rv = 0;
		if(this.numChildren == 0)
			rv = 1;
		else{
			for(int i = 0; i < this.numChildren; i++){
				rv += children.get(i).numLeaves2();
			}
		}
		return rv;
	} 
	
	public int numNodes(){
		int numNodes = 0;
		if(this.numChildren == 0)
			return 0;
		else
			for(int i = 0; i < this.numChildren; i++)
				numNodes = numNodes + this.getNumChildren()+ children.get(i).numNodes(); 
			// adds the number of children for each child
		
		return numNodes;
	}

}
