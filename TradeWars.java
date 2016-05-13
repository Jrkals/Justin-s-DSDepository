/*
 * Final Project May 2016
 * Discrete Structures I
 * A game of trade wars between two people
 * Each player starts with some number of subsidiaries
 * the goal is to kill off all your opponents subsidiaries
 * You collect $2 income each turn for every non-0 subsidiary you have
 * you can merge 2 of your subs or attack an enemy sub once per turn
 * The sub at index 0 cannot be attacked unless it is the only one left
 */

import java.util.Scanner;
import java.util.Random;
import java.util.HashMap; 
import java.util.Arrays;

public class TradeWars extends AlternatingGame {
	final int SIZE = 5;
	int[][]board = new int[3][SIZE];
	static Scanner scanner = new Scanner(System.in);
	Random ran = new Random();

	/*
	 * Constructor
	 * makes a 2-d array of ints
	 * each player gets SIZE # of companies at random value 
	 * the sum of each team is +/- SIZE of each other for fairness
	 * p2 has higher limit because p1 gets the advantage by going first
	 */
	public TradeWars(){
		whoseTurn = 1;
		Random ran = new Random();
		// give each player random values for their companies
		do{
			for(int i = 0; i < SIZE; i++ ){
				int num1 = ran.nextInt(10);
				int num2 = ran.nextInt(12); // advantage p2-compensates for going second
				board[1][i] = num1; 
				board[2][i] = num2; 
			}
		} while(sum(board[1])-sum( board[2]) > (SIZE) || sum(board[2])-sum( board[1]) > (SIZE)); 
		// keep the starting values relatively similar for fairness
		
		sortr(board[1]);
		sortr(board[2]);
	}

	@Override
	void setBoard(Object nb) {
		int[][] newBoard = (int[][])nb;
		for(int j = 1; j <= 2; j++){
			for(int i = 0; i < SIZE; i++){
				board[j][i] = newBoard[j][i];        
			}
		}
	}

	@Override
	Object getBoard() {
		return board;
	}

	@Override
	void drawBoard() {
		System.out.print("Player 1's board: ");
		for(int i = 0; i < SIZE; i++){
			System.out.print(board[1][i] +" ");
		}
		System.out.println();
		System.out.print("Player 2's board: ");
		for(int i = 0; i < SIZE; i++){
			System.out.print(board[2][i]+" ");
		}
		System.out.println();
	}

	// draws a given board
	void drawBoard(int[][] b){
		System.out.print("Player 1's board : ");
		for(int i = 0; i < SIZE; i++){
			System.out.print(b[1][i] +" ");
		}
		System.out.println();
		System.out.print("Player 2's board: ");
		for(int i = 0; i < SIZE; i++){
			System.out.print(b[2][i]+" ");
		}
		System.out.println();
	}

	@Override
	void getHumanMove() {
		//	sortr(board[whoseTurn]); // sort the players companies
		int move = 0;
		int amount = collectIncome();

		System.out.println("player: " +whoseTurn+ " make a move");
		System.out.println("distribute your $" +collectIncome()+ " to which subsidiary? (1-10)");
		move = scanner.nextInt()-1;
		distributeIncome(move);
		drawBoard();
		if(!canAttack() && !canMerge()){
			System.out.println(" you can't move");
			whoseTurn = 3-whoseTurn;
			return;
		}
		boolean notValid = true;
		while(notValid){ // loop until proper move is made by the human
			System.out.println("Now: merge 1) or attack 2)");
			move = scanner.nextInt();
			if(move == 1){
				merge();
				notValid = false;
			}
			if(move == 2 ){
				attack();
				notValid = false;
			}
			else{
				System.out.println("enter a valid move");
				drawBoard();
			}
		}
		// update whose turn
		whoseTurn = 3-whoseTurn;
	}
	
	// for human distribution
	void distributeIncome(int index){
		if(index >= 0 && index < SIZE){ // legal move
			board[whoseTurn][index] += collectIncome();
		}
		else{ // invalid move
			boolean notValid = true;
			while(notValid){
				System.out.println("enter a valid move");
				int move = scanner.nextInt();
				if(move >= 0 && move <= 10){
					notValid = false;
					board[whoseTurn][move-1] += collectIncome(); // legal move
				}

				else // still invalid
					System.out.println("still not valid");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see AlternatingGame#getComputerMove()
	 * gets the computer move
	 */
	void getComputerMove() {
		// getSmartKMComputerMove();
		sortr(board[whoseTurn]);
		sortr(board[3-whoseTurn]);
		getSmartComputerMove();
	}

	/**********************************************
	/*Greedy computer algorithm developed 
	 * in programming club by Brian and me. 
	 **********************************************/

	void getSmartKMComputerMove(){
		sortr(board[whoseTurn]);
		sortr(board[3-whoseTurn]);
		int income = collectIncome();
		// define the main companies for each user
		int cpuMain = board[whoseTurn][0];
		int userMain = board[3-whoseTurn][0];
		int userPmax = 0;
		int cpuPmax = 0;
		/********************************
		 * Distribute Income
		 ********************************/
		if(cpuMain < userMain && (cpuMain + income) > userMain)
			board[whoseTurn][0] += income; // distribute to cpu main
		else if(((board[whoseTurn][1] + income) > board[3-whoseTurn][1])&&
				activeSubs(board[3-whoseTurn]) > 1)
			board[whoseTurn][1] += income;
		else
			board[whoseTurn][0] += income;
		if(!canAttack() && !canMerge()){ // can't make a move
			whoseTurn = 3-whoseTurn;
			System.out.println("computer can't make a move");
			return;
		}
		sortr(board[whoseTurn]); // sort after distributing
		/*************************************
		 * Make Move Attacking or Merging
		 **************************************/
		//	System.out.println("number of a subs cpu has = "+activeSubs(board[whoseTurn]));
		if(activeSubs(board[whoseTurn]) == 1){
			mainAttack(); // only option is to try to attack
			whoseTurn = 3-whoseTurn;
		}
		else{
			userPmax = findPotentialMax();
			cpuPmax = board[whoseTurn][0] + board[whoseTurn][1];
			if(userPmax > cpuPmax)
				if(board[whoseTurn][1] > board[3-whoseTurn][1] &&
						cpuPmax > userPmax - board[3-whoseTurn][1]){
					// attack
					board[3-whoseTurn][1] = 0;
					System.out.println("cpu attacked you sub # 2");
					whoseTurn = 3-whoseTurn;
				}
				else { // attack some other sub if you can
					computerAttack();
					whoseTurn = 3-whoseTurn;
				}
			else{
				if((activeSubs(board[whoseTurn])) > (activeSubs(board[3-whoseTurn]))){
					merge(0, 1);
					System.out.println("cpu merges 1 and 2");
					whoseTurn = 3-whoseTurn;
				}
				else{
					computerAttack();
					whoseTurn = 3-whoseTurn;
				}
			}			
		}

	}

	/*
	 *  cpu attack method when only 1 subsidiary is left
	 */
	private void mainAttack() {
		//	System.out.println("main Attacking");
		boolean attackDone = false;
		if(activeSubs(board[3-whoseTurn]) == 1 && 
				board[whoseTurn][0] > board[3-whoseTurn][0]){
			board[3-whoseTurn][0] = 0; // destroy their main
			attackDone = true;
		}
		else if(activeSubs(board[3-whoseTurn]) > 1){
			for(int i = 1; i < activeSubs(board[3-whoseTurn]); i++){
				if(board[whoseTurn][0] > board[3-whoseTurn][i]){
					board[3-whoseTurn][i] = 0; // attack some subsidiary
					attackDone = true;
				}
			}
		}
		else
			return; // do nothing
	}

	/*
	 * method that returns true if it is possible to attack on this turn
	 */
	private boolean canAttack() {
		boolean rv = false;
		// values of where to start the search for possible moves
		int start;  // own board
		int start2; // opponents board start index
		if(activeSubs(board[3-whoseTurn]) == 1)
			start2 = 0; // if opp has 1 sub, look at it first
		else 
			start2 = 1; // if opp has > 1 sub, start with second
		if(activeSubs(board[whoseTurn]) == 1)
			start = 0; // if you have one sub start with it
		else if(activeSubs(board[whoseTurn]) > 1 && activeSubs(board[3-whoseTurn]) == 1)
			start = 0;
		else
			start = 1;

		for(int i = start; i < activeSubs(board[whoseTurn]); i++){
			for(int j = start2; j < activeSubs(board[3-whoseTurn]); j++){
				if(board[whoseTurn][i] > board[3-whoseTurn][j])
					rv = true;
			}
		}
		return rv;
	}
	/*
	 * returns true if a merge is possible this turn
	 */
	private boolean canMerge(){
		return activeSubs(board[whoseTurn]) > 1;
	}

	// adds the 2 biggest subsidiaries for a given player
	public int findPotentialMax() {
		return board[3-whoseTurn][0] + board[3-whoseTurn][1];
	}

	// makes a random move for the computer
	void getDumbComputerMove(){
		// 
		System.out.println("making a computer move");
		int money = collectIncome();
		sortr(board[whoseTurn]);
		board[whoseTurn][0] += money; // give to main
		// now merge or attack
		int move = ran.nextInt(2);
		int move2 = 0;
		int activeSubs = activeSubs(board[whoseTurn]); // the number of non-zero subsidiaries

		if(move == 0 && activeSubs > 1){ // merge
			while(move2 == move){
				move = ran.nextInt(activeSubs);
				move2 = ran.nextInt(activeSubs);
			}
			merge(move, move2);
			System.out.println("computer mergres "+ (move+1)+ " "+ (move2+1));
		}
		else  // attack
			computerAttack();
		whoseTurn = 3-whoseTurn;
	}

	// gets income based on the number of subsidiaries each turn 
	int collectIncome(){
		int count = 0;
		for(int i = 0; i < SIZE; i++){
			if(board[whoseTurn][i] > 0)
				count++;
		}
		return count*2; // 2 for each subsidiary not counting the main
	}


	@Override
	int whoWon() {
		boolean allZero = true;
		int winner = 0;
		if(board[whoseTurn][0] == 0)
			return 3-whoseTurn; // main taken out
		for(int i = 0; i < SIZE; i++){
			if(board[whoseTurn][i] != 0)
				winner = whoseTurn;
			else
				winner = 3-whoseTurn;
		}
		return winner;
	}

/*
 * (non-Javadoc)
 * @see TurnTakingGame#isGameOver()
 * returns true if one player has no subsidiaries left
 */
	boolean isGameOver() {
		boolean rv = true;
		if(activeSubs(board[whoseTurn]) == 0 || board[whoseTurn][0] == 0)
			return true;
		else
			return false;
	}

	@Override
	int whoseTurn(Object b) {
		int[][] localBoard = (int[][])b;
		return localBoard[0][0]; // whoseTurn is stored in the board
	}

	@Override
	int whoWon(Object b) {
		int[][] localBoard = (int[][])b;
		int turn = localBoard[0][0];
		int winner = 0;
		if(localBoard[turn][0] == 0)
			return 3-turn; // main taken out
		for(int i = 0; i < SIZE; i++){
			if(localBoard[turn][i] != 0)
				winner = turn;
			else
				winner = 3-turn;

		}
		return winner;
	}

	

	/*
	 * @Override(non-Javadoc)
	 * returns the possible children
	 * @see TurnTakingGame#getChildren(java.lang.Object)
	 */
	Object[] getChildren(Object board) {
		HashMap<String, int[][]> mergesMap = new HashMap<String, int[][]>();
		int[][] localBoard = (int[][]) board; 
		localBoard[0][0] = whoseTurn;
		//	int numChildren = getNumChildren(localBoard);
		//	System.out.println("num children is "+ numChildren);
		int turn = whoseTurn; //local whoseTurn basically
		DSArrayList<Object> children = new DSArrayList<Object>();
		int[][][] temp = new int[SIZE][3][SIZE];
		
		// make possible distributions
		int income = collectIncome();
		for(int i = 0; i < activeSubs(localBoard[turn]); i++){
			temp[i] = makePosDis(localBoard, income, i);
			//	temp.set(i, makePosDis(localBoard, collectIncome(), i));
		}
		int size = temp.length;
		/*************************
		 * MERGES
		 *************************/
		// loop over all the boards in temp
		for(int k = 0; k < activeSubs(localBoard[turn]); k++){
			//	System.out.println(k);
			int pm = numPossibleMerges(activeSubs(temp[k][turn])); // possible merges
			//	System.out.println("num possible merges is "+pm);
			int[][][] merges = makePosMerg(pm, temp[k]); // all merges
			for(int i = 0; i < pm; i++){
				String boardString = toString(merges[i]);
				if(mergesMap.containsKey(boardString))
					break;
				else{
					mergesMap.put(toString(merges[i]), merges[i]);
					// store in children array
					children.add(merges[i]); 
				}
			}
			/****************************
			 * ATTACKS
			 ****************************/
			sortr(temp[k][turn]);
			int[][] tempAttack = new int[3][SIZE];
			int start;
			if(activeSubs(temp[k][turn]) == 1)
				start = 0;
			else
				start = 1;
			tempAttack = (makePosAtt(temp[k][turn][start], temp[k]));
			children.add(tempAttack);
		}
		return children.toArray();
	}
	
	/*
	 * Method to make the possible merges for one amount on a given board
	 * returns all the boards
	 */
	private int[][][] makePosMerg(int a, int[][] b) {
		// System.out.println("makeing possible merges");
		int[][] localBoard = b;
		int[][][] boards  = new int[a][3][SIZE]; // a is the number of possible merges
		int turn = localBoard[0][0];
		int count = 0;
		sortr(localBoard[turn]);
		for(int i = 0; i < activeSubs(localBoard[turn]); i++){
			for(int j = i+1; j < activeSubs(localBoard[turn]); j++){
				boards[count] = mergeI(localBoard, i, j);
				count++;
			}
		}
		//	System.out.println("done possible merges");
		return boards;
	}

	// finds the number of attacks for any one subsidiary given a board
	int findNumAttacks(int i, int[][] b) {
		int[][] localBoard = b;
		int attacker = i;
		int count = 0;
		int turn = localBoard[0][0];
		int start;
		if(activeSubs(localBoard[3-turn]) == 1)
			start = 0; // if the enemy has 1 company look at it
		else
			start = 1; // otherwise start with sub # 2
		for(int j = start; j < localBoard[3-turn].length; j++){
			int enemy = localBoard[3-turn][j];
			if(attacker > enemy )
				count++;
		}
		return count;
	}

	/*
	 * method to find the number of children for a given board
	 */
/*	int getNumChildren(int[][] board){
		//	System.out.println(" getting the number of children");
		int[][] localBoard = board;
		int numChildren = 0;
		int turn = localBoard[0][0];
		if(activeSubs(localBoard[1]) == 0 || activeSubs(localBoard[2]) == 0)
			numChildren = 0; // no children
		else{
			int size = activeSubs(localBoard[turn]);
			//	System.out.println("num act subs = "+ size); 
			numChildren = numPossibleMerges(activeSubs(localBoard[turn])) + 
					totalNumAttacks(getPossibleAttackers(localBoard), localBoard);
		}
		return numChildren;
	}*/

	//finds the sum of the entries of an array
	int sum(int[] a){
		int sum = 0;
		for(int i = 0; i < a.length; i++){
			sum += a[i];
		}
		return sum;
	}

	// copies array a into array b
	void copy(int[] a, int[] b){
		for(int i = 0; i < a.length; i++){
			b[i] = a[i];
		}
	}

	// sort function for descending order
	public int[] sortr (int[] a){
		int tempmax = -1;
		int index = 0;
		int[] sorted = new int[a.length];
		for(int i = 0; i < sorted.length; i++){
			for(int j = 0; j < a.length; j++){
				if(a[j] > tempmax){
					tempmax = a[j];
					index = j;
				}
			}
			a[index] = -1;
			sorted[i] = tempmax;
			tempmax = -1;
		}
		//for(int i = 0; i < sorted.length; i++)
		//System.out.print(sorted[i]);
		copy(sorted, a); // copies the sorted back into the original
		return sorted;
	}

	// human attack
	void attack(){
		int attacker;
		int enemy;
		System.out.println("which subsidiary of yours is attacking");
		attacker = scanner.nextInt() -1;
		// if attacker out of bounds or primary company
		while(attacker > board[whoseTurn].length || 
				(attacker == 0 && board[3-whoseTurn][1] != 0)){
			System.out.println("enter a valid move");
			attacker = scanner.nextInt()-1;
		}

		System.out.println(" which enemy are you attacking?");
		enemy = scanner.nextInt() - 1;
		while(enemy < 0 || enemy > board[3-whoseTurn].length){
			System.out.println("enter a valid move");
			enemy = scanner.nextInt();
		}
		while(!isValidAttack(board[whoseTurn][attacker], board[3-whoseTurn][enemy])){
			System.out.println("invalid move try again");
			attacker = scanner.nextInt();
			while(attacker > board[whoseTurn].length){
				System.out.println("enter a valid move");
				attacker = scanner.nextInt();
			}

			System.out.println(" which enemy are you attacking?");
			enemy = scanner.nextInt();
			while(enemy < 0 || enemy > board[3-whoseTurn].length){
				System.out.println("enter a valid move");
				enemy = scanner.nextInt();
			}
		} // end of while
		board[3-whoseTurn][enemy] = 0;
	}

	// Human Merging
	void merge(){
		int s1;
		int s2;
		boolean notValid = true;
		while (notValid){
			System.out.println("which one of your subsidiaries are merging");
			s1 = scanner.nextInt() -1; // -1 because array statrs at 0
			System.out.println(" the other?");
			s2 = scanner.nextInt() -1;
			if(isValidMerge(s1, s2)){
				board[whoseTurn][s1] += board[whoseTurn][s2];
				board[whoseTurn][s2] = 0;
				notValid = false; // valid move
			}
			else
				System.out.println("enter a valid move instead");
		}
	}

	// computer merging
	void merge(int a, int b){
		board[whoseTurn][a] = board[whoseTurn][a] + board[whoseTurn][b];
		board[whoseTurn][b] = 0;	
	}

	// merge that returns the board
	int[][] mergeI(int[][] bo, int a, int b){
		int[][] localBoard = new int[3][SIZE];
		int turn = bo[0][0];
		copy(bo[turn], localBoard[turn]);
		copy(bo[3-turn], localBoard[3-turn]);
		copy(bo[0], localBoard[0]);
		localBoard[turn][a] += localBoard[turn][b];
		localBoard[turn][b] = 0;
		return localBoard;
	}

	// dumb computer attack
	void computerAttack(){
		boolean canAttack = canAttack();
		int attacked = 0;
		int attacker = 0;
		boolean attackDone = false;
		sortr(board[whoseTurn]);
		//	System.out.println("computer attacking");
		if(canAttack()){
			while(!attackDone){
				for(int j = 1; j <= activeSubs(board[whoseTurn]); j++){
					if(attackDone)
						break;
					for(int i = 1; i < SIZE; i++){
						if(board[whoseTurn][j] > board[3-whoseTurn][i] &&
								board[3-whoseTurn][i] != 0){
							board[3-whoseTurn][i] = 0;
							attacked = i;
							attacker = j;
							attackDone = true;
							break;
						}
					}
				}
			}
		}
		if(canAttack)
			System.out.println("attacked your sub " + (attacked+1) +" with " +(attacker+1));
		else{
			//	System.out.println(" computer trying to merge");
			merge(0, firstNonZero(board[whoseTurn])); // merge instead
			//	System.out.println("merged the first cpu sub with another");
		}
	} // end of computerAttack

	public int firstNonZero(int[] b) {
		sortr(b);
		boolean found = false;
		int index = 0;
		for(int i = 1; i < activeSubs(b); i++){
			if(b[i] != 0){
				found = true;
				index = i; // first non-zero index
				//	System.out.println(" found place to merge at "+ index);
				//	System.out.println("[B]oard at "+ index+ " is " +b[index]);
			}
			if(found)
				break;
		}
		if(found){
			return index; 
		}
		else{
			//	System.out.println(" found no place, idx = 1");
			return 1;
		}
	}

	// returns true if the move is valid
	boolean isValidAttack(int attacker, int enemy){
		if(attacker > enemy)
			System.out.println("good move");
		else 
			System.out.println("attacker at value" + attacker +
					"is less than " + enemy);
		return (attacker > enemy);
	}

	//returns true if the move is valid
	boolean isValidMerge(int s1, int s2){
		if (board[whoseTurn][s1] != 0 && board[whoseTurn][s2] != 0)
			System.out.println("acceptable merge");
		else
			System.out.println("s1 or s2 = 0");
		return(board[whoseTurn][s1] != 0 && board[whoseTurn][s2] != 0);
	}

	// a function to find the number of non-zero subs in a set
	int findNonEmptySubs(Object b){
		int[][] board = (int[][]) b;
		drawBoard(board);
		int turn = board[0][0];
		int count = 0;
		for(int i = 0; i < SIZE; i++){
			//	System.out.println(board[turn]);
			if(board[turn][i] != 0){
				count++;
				//	System.out.println(board[turn][i]+ " is not 0");
			}
		}
		System.out.println("number of non-0 subs is "+ count);
		return count;
	}

	// recursive function to count the number of
	// possible merges in any given move based on the number of subsidiaries
	int numPossibleMerges(int subs){
		if(subs == 1 || subs == 0)
			return 0;
		else
			return choose(subs, 2);
	}

	// method to determine the number of possible attacking subsidiaries
	// on one side for any board
	int numOfPosAttSubs(int[][] b){
		//	System.out.println("getting number of possible attacks");
		int[][] localBoard = b;
		int turn = localBoard[0][0];
		int count = 0;
		int start;
		if(activeSubs(localBoard[turn]) == 1)
			start = 0;
		else 
			start = 1;
		for(int i = start; i < SIZE; i++){
			// System.out.println(i);
			// System.out.println(localBoard[turn][i]+ localBoard[3-turn][i]);
			count += findNumAttacks(localBoard[turn][i], localBoard);
		}
		//	System.out.println("number of attacks is: "+count);
		return count;
	}

	/* 
	 * makes the children for possible attacks
	 * there is only one child- a board of destroying the highest opponent possible
	 */

	int[][] makePosAtt(int a, int[][] board){ 
		//	System.out.println("in mpa method");
		int[][] localBoard = board;
		int turn = localBoard[0][0];
		int start;
		if(activeSubs(localBoard[3-turn]) == 1)
			start = 0;
		else
			start = 1;
		for(int i = start; i < activeSubs(localBoard[3-turn]); i++){
			if(a > localBoard[3-turn][i]){
				localBoard[3-turn][i] = 0;
				return localBoard;
			}
		}
		return localBoard;
	}

	// makes the board for a possible distributions
	int[][] makePosDis(int[][] b, int amount, int index){
		int turn = b[0][0];
		int[][] localBoard = new int[3][SIZE];
		copy(b[turn], localBoard[turn]);
		copy(b[3-turn], localBoard[3-turn]);
		copy(b[0], localBoard[0]);
		localBoard[turn][index] += amount;
		return localBoard;
	}

	// find a choose b
	int choose(int a, int b){
		// System.out.println("in choose method");
		return factorial(a)/(factorial(b)*factorial(a-b));
	}

	// factorial
	int factorial(int a){
		if(a == 1 || a == 0 || a < 0)
			return 1;
		else
			return a* factorial(a-1);
	}

	/*
	 * (non-Javadoc)
	 * @see TurnTakingGame#toString(java.lang.Object)
	 * makes boards into strings for hashing purposes
	 */
	String toString(Object b) {
		//	System.out.println("in toString method, the current board is: ");

		int[][] board = (int[][]) b;
		//	drawBoard(board);
		String line0 = Arrays.toString(board[0]);
		String line1 = Arrays.toString(board[1]);
		String line2 = Arrays.toString(board[2]);
		// System.out.println(line0+line1+line2);
		return line0+line1+line2;
	}
	/*
	 * makes int arrays into strings
	 */ 
	String toString(int[][] b) {
		//	System.out.println("in toString method, the current board is: ");
		int[][] board = b;
		//	drawBoard(board);
		String line0 = Arrays.toString(board[0]);
		String line1 = Arrays.toString(board[1]);
		String line2 = Arrays.toString(board[2]);
		// System.out.println(line0+line1+line2);
		return line0+line1+line2;
	}

	/*
	 * count of non-zero subsidiaries
	 */
	int activeSubs(int[] b){
		//	System.out.println("in active subs method");
		int count = 0;
		for(int i = 0; i <b.length; i++){
			if(b[i] > 0){
				count++;
			}	
		}
		return count;
	}
	/*
	 * (non-Javadoc)
	 * @see TurnTakingGame#ScoreBoard(java.lang.Object)
	 * evaluates a board as favorable to one player or the other
	 * a positive value denotes p1 has the advantage, neg for p2
	 */
	int ScoreBoard(Object b){
		int[][] localBoard = (int[][]) b;
		int score = 0;
		int turn = localBoard[0][0];
		int p1main = localBoard[1][0];
		int p2main = localBoard[2][0];
		int p1s = localBoard[1][1];
		int p2s = localBoard[2][1];
		boolean earlyGame;
		if(activeSubs(localBoard[2]) >= 3 || activeSubs(localBoard[1]) >= 3 )
			earlyGame = true;
		else
			earlyGame = false;
		if(earlyGame)
			//EARLY GAME**************************
		{
			if(sum(localBoard[1]) != sum(localBoard[2])) // diff sums of subs
				score += 3*(sum(localBoard[1]) - sum(localBoard[2]));
			if(p1s != p2s && p1s != 0 && p2s != 0) // bigger second sub
				score += 10*(p1s-p2s);
			if(p2s == 0) // no second sub
				score += 100;
			if(p1s == 0) // no second sub
				score -= 100;
		//	if(p1main + p1s != p2main + p2s) // combo of first two
		//		score =+ 2*(p1main + p1s - (p2main + p2s));
			if(activeSubs(localBoard[1]) > activeSubs(localBoard[2])) // more subs for p1
				score += 100*(activeSubs(localBoard[1])- activeSubs(localBoard[2]));
			if(activeSubs(localBoard[2]) > activeSubs(localBoard[1])) // more subs for p2
				score -= 100*(activeSubs(localBoard[2])- activeSubs(localBoard[1]));
		}
		//LATE GAME***************************
		else 
		{
			//	System.out.println("late game");
			// if p1 can defeat opponent
			if(localBoard[2][0] < localBoard[1][0] &&
					(activeSubs(localBoard[2]) == 1))
				score += 1000;
			// if p2 can win immediately
			if(localBoard[1][0] < localBoard[2][0] &&
					(activeSubs(localBoard[1]) == 1))
				score -= 1000;
			//WIN*********************************
			if(activeSubs(localBoard[2]) == 0 || localBoard[2][0] == 0) 
				score += 10000;
			if(activeSubs(localBoard[1]) == 0 || localBoard[1][0] == 0) 
				score -= 10000;
			//************************************
			if(activeSubs(localBoard[1]) <= 2 && p1main > p2main)
				score += 1000; 
			if(activeSubs(localBoard[2]) <= 2 && p2main > p1main)
				score -= 1000;
			if(p1main != p2main) // diff mains
				score += 20*(p1main-p2main);
			//	if(p1main >p2main && p1s > p2s) // bigger main and second sub for p1
			//		score += p1main-p2main + p1s-p2s + 50;
			//	if(p2main >p1main && p2s > p1s) // bigger main and second sub for p2
			//		score += p1main-p2main + p1s-p2s - 50;
		}
		return score;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see AlternatingGame#reset()
	 * resets the board after a game has been played
	 */
	void reset() {
		whoseTurn = 1;
		Random ran = new Random();
		do{
			for(int i = 0; i < SIZE; i++ ){
				int num1 = ran.nextInt(10);
				int num2 = ran.nextInt(10);
				board[1][i] = num1; 
				board[2][i] = num2; 
			}
		} while(sum(board[1])-sum(board[2]) > 3 || sum(board[2])-sum( board[1]) > 3); 
		//	board[3-whoseTurn][SIZE-1] = ran.nextInt(10); // extra for player who doesnt start
		// keep the startin values relatively similar
		sortr(board[1]);
		sortr(board[2]);
	}


}
