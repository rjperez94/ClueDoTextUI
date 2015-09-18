package main;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import view.InputParser;
import view.OutputStream;
import model.Board;
import model.Card;
import model.CentreRoom;
import model.Player;
import model.Square;
import model.Square.Type;

/**
 * Has main logic of ClueDo game
 * <br> -- This places N player tokens depending on how many are playing
 * <br> -- P1 always starts up to P N
 * <br> -- Can move horizontal or vertical
 * <br> -- Moving from one room to another room still REQUIRES 1 MOVE step
 * <br> -- Entering room ends turn
 * <br> -- Can make suggestions as long as you're in a room
 * <br> -- Automatic refute of suggestion
 * <br> -- Cannot forfeit turn
 * <br> -- Disqualified players stay in the game only to prove other’s suggestions wrong 
 * 	with the cards they hold in their hands
 * <br> -- NO 'Detective Pads' , Do one yourself on paper :)))) . It's not required by instructions
 * @author Ronni Perez
 *
 */
public class Cluedo {
	private InputParser parser;	//parse text in system.in
	private Loader loader;		//load board file and game "environment"
	private Board board;
	private final int rows = 18;		//specs says 25x25 but done 18x18 instead, tedious hardcoding
	private final int cols = 18;
	private Map<String, String> weaponsInRoom;	//room name to weapon -- see Section 2.4 of instructions
	private int players;		//number of players at the beginning, this may decrease over time if player is eliminated
	private int current;		//player ID of the one in current turn
	private int remaining;	//remaining MOVES for the current player
	private CentreRoom solution;	//where solution is kept
	private boolean isWon;		//if game over
	
	/**
	 * Main constructor
	 */
	public Cluedo() {
		OutputStream.message("Welcome to Cluedo game", false);
		loader = new Loader (rows, cols);		//parse txt file from assets/board.txt
		parser = new InputParser();	
		
		reset();		//setup game
		turn();		//start play
	}
	
	/**
	 * Constructor used for Testing ONLY
	 * This does not call reset() and turn();
	 * @param players -- number of players
	 */
	public Cluedo(int players) {
		this.players = players;
		loader = new Loader (rows, cols);		//parse txt file from assets/board.txt
		isWon = false;
		List <Card> cards = loader.loadCards();		//game cards
		board = new Board(loader.getCoords(), this.players);		//get board "string" representation
		//pick 'solution' cards and put them in centre room
		solution = new CentreRoom();
		loader.pickSolution (solution,cards);
		
		weaponsInRoom = loader.putInRoom();		//put each weapon 'token'??? in a room-- see Section 2.4 of instructions
		deal(cards);		//give cards to players
		OutputStream.message("You have chosen to play with "+players+" player(s)", false);
		current = 1;		//P1 always goes first
		remaining = 0;
	}

	/**
	 * Setup game of Cluedo
	 * This can be recalled later when the game finishes
	 * if user want to play again
	 */
	private void reset() {
		OutputStream.message("================================", false);
		isWon = false;
		List <Card> cards = loader.loadCards();		//game cards
		players = numPlayers();		//game players
		board = new Board(loader.getCoords(), players);		//get board "string" representation
		//pick 'solution' cards and put them in centre room
		solution = new CentreRoom();
		loader.pickSolution (solution,cards);
		
		weaponsInRoom = loader.putInRoom();		//put each weapon 'token'??? in a room-- see Section 2.4 of instructions
		deal(cards);		//give cards to players
		OutputStream.message("You have chosen to play with "+players+" player(s)", false);
		OutputStream.weaponMappings(weaponsInRoom);		//inform users where each weapon token is
		current = 1;		//P1 always goes first
		remaining = 0;
	}
	
	/**
	 * Ask user how many people are playing
	 * @return valid number i.e. 3...6 players
	 */
	private int numPlayers() {
		OutputStream.message("How many players are playing the game? 3 to 6 players", true);
		return parser.parseNum(3,6);		//accept only 3 to 6 player limit
	}

	/**
	 * Distributes remaining cards to players
	 * @param cards -- unshuffled cards which are left after solution was picked
	 */
	private void deal(List<Card> cards) {
		Collections.shuffle(cards);		//shuffle cards
		int dealTo = 1;
		while (!cards.isEmpty()) {		//deal each card
			Card c = cards.remove(0);
			Player p = board.getPlayer(dealTo);
			p.addCard(c);			//add to player's card
			
			if (dealTo >= players) 	dealTo = 1;
			else 	dealTo++;
		}
	}

	/**
	 * Manages one player turn's cycle and checks if game has been won
	 */
	private void turn() {
		OutputStream.outBoard(board);
		remaining = roll();		//roll the die
		Player p = board.getPlayer(current);		//get token of current player
		//inform of whos's turn it is
		OutputStream.message("It's "+p.character+"(P"+p.ID+")'s turn\nThe dice rolled "+remaining, false);
		OutputStream.sleep(2000);
		
		while (remaining > 0 && !isWon) {		//if player has moves remaining in current turn
			int option = makeChoice();		//get number of choice and execute accordingly
			//option 3 only available when current player is in a room
			switch (option) {
			case 0: OutputStream.printCards(p); break;
			case 1: chooseMove(p); break;
			case 2: chooseAccuse(); break;
			case 3: chooseSuggest(board.getPlayer(current).getLocation().getName()); break;
			}
		}
		evaluate();
	}

	/**
	 * Generates random number from 1....6
	 * 1 Die ONLY, uncomment code for dice
	 * @return
	 */
	private int roll() {
		return ((int)(Math.random()*6) + 1) ;
				//+ ((int)(Math.random()*6) + 1);
	}
	
	/**
	 * Present ONLY valid options to current player
	 * @return number of current player's choosing
	 * 
	 * 1 MOVE one step
	 * 2 Make an ACCUSATION
	 * 3 Announce SUGGESTION (available only if player is in a room)
	 */
	private int makeChoice() {
		String room = board.inRoom(current);	//get room location of current player	
		if (room != null) {		//player is in a room (any room)
			//output options
			OutputStream.printOptions("What do you want to do?", 
			new String[]{"MOVE one step", 
								"Make an ACCUSATION", 
					"Announce SUGGESTION using " + room.toUpperCase() });
			OutputStream.message("You can always enter 0 (zero) to view your cards", false);
			return parser.parseNum(0, 3); 	//accept input from 0 to 3
		} else {
			//output options
			OutputStream.printOptions("What do you want to do?", new String[] {
					"MOVE one step", "Make an ACCUSATION" });
			OutputStream.message("You can always enter 0 (zero) to view your cards", false);
			return parser.parseNum(0, 2);		//accept input 0 to 2
		}
	}
	
	/**
	 * Chooses the next situation based on the game state
	 * i.e. next player moves, game over (no winner), game over (has winner)
	 * and asks user to play again in the latter cases
	 */
	private void evaluate() {
		if (!isWon && !gameOver()) {			//check if any players left or game not won
			nextMove(false);		//call turn()
		} else if (!isWon && gameOver()) {		//game stops, no winner
			OutputStream.message("\n Game OVER! The case has gone cold. \n No players left", false);
			OutputStream.printOptions("Do you want to play again?", new String[]{"Yes", "No"});
			int choice = parser.parseNum(1, 2);
			if (choice == 1) {		//play again
				reset();
			}
			//else...system exits automatically
		} else {
			OutputStream.printOptions("\nDo you want to play again?", new String[]{"Yes", "No"});
			int choice = parser.parseNum(1, 2);
			if (choice == 1) {		//play again
				reset();
			}
			//else...system exits automatically
		}
	}
	
	/**
	 * Increment 'current' player with the ID of next player player
	 * i.e. 
	 * if 4 players playing, then current == from 1 to 4 until game finishes;
	 * if 6 players playing, then current == from 1 to 6 until game finishes;
	 * 
	 * @param isTesting is true iff in 'jUnit testing'. Set to true when testing
	 */
	private void nextMove(boolean isTesting) {
		do {
			if (current >= players)		//inspect limit
				current = 1;
			else
				current++;
		} while (!board.isPlaying(current));	//inspect if player has been disqualified i.e. made false accusation
		
		if (!isTesting) {		//if playing game, i.e. not testing
			turn();		//re-call turn for next player
		}
	}

	/**
	 * Game stop or continue
	 * 
	 * @return true iff there are no players remaining i.e. everyone has been
	 *         disqualified/expelled()
	 */
	private boolean gameOver() {
		for (int i = 1; i <= players; i++) {
			if (board.getPlayer(i).isPlaying()) {		//look for active players
				return false;
			}
		}
		//there are no active players
		return true;
	}
	
	/**
	 * Announce a Suggestion using the room that the current player is in
	 * You can only suggest using the room you are in
	 * @param room -- room that the current player is in
	 * Assume room is not null
	 */
	private void chooseSuggest (String room) {
		OutputStream.printOptions("SUGGEST a suspect: ", Loader.getPeople());
		String person = parser.pickCharacter();		//pick character
		OutputStream.printOptions("SUGGEST a murder weapon: ", Loader.getWeapons());
		String weapon = parser.pickWeapon();		//pick weapon
		
		//inform user of his/her suggestion
		OutputStream.message("You suggested: "+ person+", "+room+", "+weapon, false);
		OutputStream.sleep(2000);
		
		//move player and weapon in room -- via power of suggestion
		moveCharAndWeapon(person, weapon, room);
		
		//refute this suggestion
		checkSuggestion(person, weapon, room);
	}

	/**
	 * Put player that plays the 'person' and the specified weapon in this room
	 * @param person -- name of character
	 * @param weapon -- name of weapon
	 * @param room -- name of room
	 */
	private void moveCharAndWeapon(String person, String weapon, String room) {
		Player p = board.getPlayer(person);
		//update or move player token when possible
		if (p != null) {		//if player that represents character is playing
			outerloop:
			for (int row = 0; row < this.rows; row++) {
				for (int col = 0; col < this.cols; col++) {
					Square sq = board.getSquare(row, col);
					if (sq.getName().equals(room) && sq.getOccupied() == null) {	//if vacant space found
						board.movePlayerViaSuggestion(p.ID, row, col);		//move player token to row,col
						OutputStream.message(p.character+"(P"+p.ID+") was moved to {"+row+", "+col+"} "+sq.getCode(), false);
						OutputStream.outBoard(board);
						break outerloop;		//end loop
					}
				}
			}
		}
		
		//update weapon mappings e.g.
		//kitchen has dagger
		//lounge  has candlestick
		//suggestion is dagger and lounge so:
		//kitchen to lounge               dagger
		String wpn = weaponsInRoom.get(room);	//old weapon, if any		//candlestick
		String rm = getRoom(weapon);	//old room			//kitchen
		//update i.e. swap
		weaponsInRoom.put(rm, wpn);
		weaponsInRoom.put(room, weapon);
		OutputStream.message("The new weapon mappings are:", false);
		OutputStream.weaponMappings(weaponsInRoom);
	}

	/**
	 * Check for room that a given weapon is in
	 * @param weapon -- name of weapon to search for
	 * @return name of room that a weapon is in (will never be null)
	 */
	private String getRoom(String weapon) {
		for (Map.Entry<String, String> entry: weaponsInRoom.entrySet()) {
			if ((weapon).equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		//dead code
		return null;
	}

	/**
	 * Try to refute suggestion of current player  by other players
	 * The card to be shown (if any) is chosen by the computer at random
	 * @param person -- suspect chosen by the current player
	 * @param weapon -- weapon chosen by the current player
	 * @param room -- room that the current player is in
	 */
	private void checkSuggestion(String person, String weapon, String room) {
		int nextPlayer = current +1;		//next player to show if it has one of those cards
		while (nextPlayer != current) {		//go through every player
			//even if player is disqualified, allow show card
			if (nextPlayer > players) 	nextPlayer = 1;
			else 	nextPlayer++;
			
			Player p = board.getPlayer(nextPlayer);		//get next player that is not disqualified
			if (p.hasCard(person, false) || p.hasCard(weapon, false) || p.hasCard(room, false)) {	
				//inspect each card of next player if he has either of the three elements in the suggestion
				//output is handled by the Player class
				//stop this method
				return;
			}
			//this next player cannot refute the current player's suggestion, go to next eligible player
			OutputStream.message("P"+p.ID+" cannot refute the suggestion", false);
		}
		
		//if it gets here, then no one can refute the suggestion
		OutputStream.message("No one can refute the suggestion", false);
		OutputStream.sleep(1000);
	}

	/**
	 * Make an Accusation
	 * At the end of this method, 'current' player can either win or be disqualified
	 */
	private void chooseAccuse() {
		OutputStream.printOptions("Choose the suspect: ", Loader.getPeople());
		String person = parser.pickCharacter();		//pick character
		OutputStream.printOptions("Choose the murder weapon: ", Loader.getWeapons());
		String weapon = parser.pickWeapon();		//pick weapon
		OutputStream.printOptions("Choose the scene of the crime: ", Loader.getRooms());
		String room = parser.pickRoom();		//pick room (any room)
		
		//inform user of his/her accusation
		OutputStream.message("You chose: "+ person+", "+room+", "+weapon, false);
		OutputStream.sleep(2000);
		
		if (person.equals(solution.getSuspect()) 		//if accusation matches solution
				&& weapon.equals(solution.getWeapon()) 
				&& room.equals(solution.getPlace()) ) {
			OutputStream.message("You WON!!!", false);		//player wins
			OutputStream.message(board.getPlayer(current).character+" won the game", false);
			OutputStream.message("The suspect is "+person+". He/She used the "+weapon+" to kill Dr Black in the "+room, false);
			isWon = true;
			remaining = 0;			//set remaining to 0 to stop while loop from turn()
		} else {		//else, disqualify/expel 'current' player
			OutputStream.message("P"+current+"'s accusations are baseless. P"+current+" is expelled from the game", false);
			board.expel(current);
			OutputStream.sleep(1000);
			remaining = 0;		//set remaining to 0 to stop while loop from turn()
		}
	}
	
	/**
	 * Manage moving token one step in the board
	 * @param p -- Player instance of current player. Used by output stream 
	 * to print neighbors of player's location i.e. where can it validly move 
	 */
	private void chooseMove(Player p) {
		OutputStream.printNeighbours(p.getLocation());		//show available move
		int [] coords = parser.parseCoords();			//parse row,col of intended move
		if (move(current,coords[0], coords[1])) {		//if move is valid
			//if  NOT entered room
			Square newSquare = board.getSquare(coords[0], coords[1]);
			if (newSquare.kind != Type.ROOM) {
				remaining--;			//decrement remaining allowed move steps
				//output new board state, and remaining moves
				OutputStream.outBoard(board);
				OutputStream.message("P"+current+" has "+remaining+" moves remaining", false);
			} else {		
				//entered a room...
				remaining = 0;		//set to 0 so 'while loop' above ends
				//output new board state
				OutputStream.outBoard(board);
				OutputStream.message("Your turn has ended because you have entered "	+newSquare.getName(), false);
			}
			
			OutputStream.sleep(100);
		} else {		//invalid row, col
			OutputStream.message("Move invalid", false);
		}
	}

	/**
	 * Helper method which inspects if move by current player is valid
	 * @param ID -- current player uid
	 * @param row	-- new row to move to
	 * @param col -- new col to move to
	 * @return true iff move is valid
	 */
	private boolean move(int ID, int row, int col) {
		return board.movePiece(ID,row,col);
	}
	
	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		new Cluedo();
	}

}