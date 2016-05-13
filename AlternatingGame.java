/**
 * AlternatingGame class
 *
 * Defines functionality for games where two players take turns
 */
// package ds2016;
abstract class AlternatingGame extends TurnTakingGame {
	int whoseTurn = 1;		// The player whose turn it is.
	int DEPTH = 3;
	// isHuman[1] is true if Player 1 is a human, false otherwise
	boolean[] isHuman = {false, false, false}; // initialize to human vs. computer 

	void playGame(){
		boolean gameOver = false;
		while(!gameOver){
			drawBoard();
			getPlayerMove();
			gameOver = isGameOver();
			if(gameOver)
				doEndgameStuff();
		}
		// drawBoard();
	}

	void getPlayerMove(){
		if(isHuman[whoseTurn] == true)
			getHumanMove();
		else
			getComputerMove();
	}
	/**
	 * Gets the computer player's move
	 * using heuristics
	 * Picks a winning move, if available.
	 * Otherwise, pick a tie move.
	 *
	 */
	void getSmartComputerMove(){
		System.out.println("building tree...");
		Object board = getBoard();
		Object[] children = getChildren(board);
		Object newBoard = null;
		System.out.println("size of children is " +children.length);
		int min = 20;
		int max = -20;
		int indexMax = 0;
		int indexMin = 0;
		// Assume for now 2 players
	//	int winner = 3 - whoseTurn;
		for(int i = 0; i < children.length; i++){
			DSNode childTree = buildTree(children[i], DEPTH);
			int childVal = heuristic(childTree);  // Recursive call
			if(childVal > max){
				max = childVal;
				indexMax = i; // keep track of it.
			}
			 if(childVal < min){
				min = childVal;
				indexMin = i; // keep track of min.
			}
			}  // end of looping over children
		// make move
			if(whoseTurn == 1){
				newBoard = children[indexMax];
			}
			else
				newBoard = children[indexMin];
		setBoard(newBoard);
		whoseTurn = 3 - whoseTurn;
	}

	/**
	 * Simply declares who won, if anybody.
	 */
	void doEndgameStuff(){
		int winner = whoWon();
		if(winner == 0)
			System.out.println("It was a tie!");
		else
			System.out.printf("Player %d won the game\n", winner);
	}

	int ComputerPlaySelf( int numGames){
		int winner = 0;
		int wins1 = 0;
		int wins2 = 0;
		for(int i = 0; i < numGames; i++){
			boolean gameOver = false;
			System.out.println("Game " +(i+1));
			System.out.println("****************************************");
			drawBoard();
			System.out.println("****************************************");
			while(!gameOver){
				drawBoard();
					if(whoseTurn == 1)
						getComputerMove();
					else
						getSmartKMComputerMove();
					gameOver = isGameOver();
				}
			System.out.println("****************************************");
			System.out.println("****************************************");
				winner = whoWon();
				System.out.println("winner of Game "+ (i+1)+ " is "+ winner);
			if(winner == 1)
				wins1++;
			else if(winner == 2)
				wins2++;
			reset();
		//	drawBoard();
		}
		System.out.println("Player 1 won " +wins1 +" out of " +numGames+" games");
		return wins1;
	}
	// methods that need to be implemented
	abstract void drawBoard();
	abstract void getHumanMove();
	abstract void getComputerMove();
	abstract int  whoWon();
	abstract void getSmartKMComputerMove();
	abstract void reset();
	abstract void setBoard(Object nb);
	abstract Object getBoard();
}
