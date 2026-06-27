package model;

import java.util.HashMap;
import java.util.Map;

import model.Square.Type;

/**
 * Cluedo Board
 * @author Ronni Perez
 *
 */
public class Board {
	private Square[][] squares;		//internal data representation
	private Map <Integer, Player> characters;

	/**
	 * Constructor
	 * @param arr -- string representation of Board
	 * @param players -- number of players initially playing the game
	 */
	public Board(String[][] arr, int players) {		
		squares = new Square[arr.length][arr[0].length];		
		characters = new HashMap<Integer,Player>();		//holds Player instances
		
		for (int row = 0; row < arr.length; row++) {
			for (int col = 0; col < arr[0].length; col++) {
				String[] tokens = arr[row][col].split("\\.");		//parse board string
				squares[row][col] = parse(row, col, tokens[0], tokens[1]);
			}
		}
		
		//map and add neighbors of every Square - above, below, left, right (EXCEPT room Squares)
		assignNeighbours();
		//add room Squares as neighbors to room
		connectToDoorways();
		//change code of doorways
		setDoorwayCode();
		//put players in starting positions
		positionPlayers(players);
	}

	/**
	 * Create and return a new Square
	 * Each row,col index in the data representation is a Square
	 * @param row
	 * @param col
	 * @param arg1 -- the Type of the Square:  PlayArea or Room
	 * @param arg2 -- the name of a Room
	 * @return
	 */
	private Square parse(int row, int col, String arg1, String arg2) {
		Type kind = parseKind(arg1);		//parse kind/type
		if (kind == Type.ROOM) {		
			String name = parseRoom(arg2);	//parse room name
			return new Room(row, col, Type.ROOM, name, arg2);
		}
		return new Square(row, col, kind, arg2);
	}

	/**
	 * Returns name of the room depending on the String arg passed
	 * @param arg
	 * @return
	 */
	private String parseRoom(String arg) {
		switch (arg) {
		case "KC":
			return "Kitchen";
		case "BR":
			return "Ball Room";
		case "CV":
			return "Conservatory";
		case "BL":
			return "Billiard Room";
		case "LB":
			return "Library";
		case "SD":
			return "Study";
		case "HL":
			return "Hall";
		case "LN":
			return "Lounge";
		case "DR":
			return "Dining Room";
		}
		//dead code
		return null;
	}

	/**
	 * Returns Type of the room depending on the String arg passed
	 * @param arg
	 * @return
	 */
	private Type parseKind(String arg) {
		if (arg.equals("RM")) {
			return Type.ROOM;
		} else if (arg.equals("NL")) {
			return Type.NULL;
		} else {		//"PA":
			return Type.PLAYAREA;
		}
	}
	
	/**
	 * Helper method
	 * Distinguish if Square at row,col is a PLAYAREA or ROOM
	 */
	private void assignNeighbours() {
		for (int row = 0; row < squares.length; row++) {
			for (int col = 0; col < squares[0].length; col++) {
				if (squares[row][col].kind == Type.PLAYAREA) {		//is playarea
					validNeigbours(row, col);
				} else if (squares[row][col].kind == Type.ROOM) {		//is room
					roomNeigbours(row, col);
				}
			}
		}
	}
	
	/**
	 * Assign neighbors of a Square at row,col - above, below, left, right
	 * PROVIDED that neighbor is NOT a room
	 * @param row
	 * @param col
	 */
	private void validNeigbours(int row, int col) {
		int leftCol = col - 1;		int rightCol = col + 1;		int aboveRow = row - 1;		int belowRow = row + 1;
		squares[row][col].addNeighbour(row, leftCol, squares);
		squares[row][col].addNeighbour(row, rightCol, squares);
		squares[row][col].addNeighbour(aboveRow, col, squares);
		squares[row][col].addNeighbour(belowRow, col, squares);
	}

	/**
	 * Assign neighbors of a Room (which extends a Square) at row,col
	 * -- the doorways FROM INSIDE the room TO the OUTSIDE
	 * This also takes care of the stairwells
	 * @param row
	 * @param col
	 */
	private void roomNeigbours(int row, int col) {
		String code = squares[row][col].getCode();
		switch (code) {
		case "KC":	//has stairwell
			squares[row][col].addNeighbour(new int[] {3,3,16,16});	break;
		case "BR":
			squares[row][col].addNeighbour(new int[] {2,5,4,7,4,10,2,12});	break;
		case "CV":	//has stairwell
			squares[row][col].addNeighbour(new int[] {4,14,16,1});	break;
		case "BL":
			squares[row][col].addNeighbour(new int[] {5,13,8,16});	break;
		case "LB":
			squares[row][col].addNeighbour(new int[] {9,15,11,13});	break;
		case "SD":	//has stairwell
			squares[row][col].addNeighbour(new int[] {14,14,1,1});	break;
		case "HL":
			squares[row][col].addNeighbour(new int[] {14,12,12,9});	break;
		case "LN":	//has stairwell
			squares[row][col].addNeighbour(new int[] {13,4,1,16});	break;
		case "DR":
			squares[row][col].addNeighbour(new int[] {11,3,7,5});	break;
		}
	}

	/**
	 * Assign neighbors of a Square
	 * -- the doorways FROM OUTSIDE(i.e. playarea) which leads TO INSIDE of the ROOM
	 * NOTE that validNeigbours(row,col) ignored rooms
	 * @param row
	 * @param col
	 */
	private void connectToDoorways() {
		squares[3][3].neighbours.add(new int []{2,3});
		
		squares[2][5].neighbours.add(new int []{2,6});
		squares[4][7].neighbours.add(new int []{3,7});
		squares[4][10].neighbours.add(new int []{3,10});
		squares[2][12].neighbours.add(new int []{2,11});
		
		squares[4][14].neighbours.add(new int []{3,14});

		squares[5][13].neighbours.add(new int []{5,14});
		squares[8][16].neighbours.add(new int []{7,16});

		squares[9][15].neighbours.add(new int []{10,15});
		squares[11][13].neighbours.add(new int []{11,14});

		squares[14][14].neighbours.add(new int []{15,14});

		squares[14][12].neighbours.add(new int []{14,11});
		squares[12][9].neighbours.add(new int []{13,9});

		squares[13][4].neighbours.add(new int []{14,4});

		squares[11][3].neighbours.add(new int []{10,3});
		squares[7][5].neighbours.add(new int []{7,4});
	}
	
	/**
	 * Set every PLAYAREA Square that has a Room neighbor to code: 'dw'
	 */
	private void setDoorwayCode() {
		for (int row = 0; row < squares.length; row++) {
			for (int col = 0; col < squares[0].length; col++) {
				if (squares[row][col].kind == Type.PLAYAREA) {	//check if playarea
					squares[row][col].changeToDoorway(this);
				}
			}
		}
	}

	/**
	 * Put players in starting positions
	 * @param players -- how many will play
	 */
	private void positionPlayers(int players) {
		int [][] position = new int [] []{
				{0, 0},		//not used
				
				{17,5},		//starting positions for player 1
				{12,0},		//p 2
				{0,5},		//p 3
				{0,12},		//and so on
				{9,17},
				{13,17}	};
		String [] chars = new String []{
				null,		//not used
				"Miss Scarlett",		//characters for player 1
				"Colonel Mustard",		//p 2
				"Mrs. White",		//p 3
				"The Reverend Green",		//and so on
				"Mrs. Peacock",
				"Professor Plum"	};
		for (int ID = 1; ID <= players; ID++) {
			//assume the role of a character
			Player p = new Player (ID, chars[ID], squares[position[ID][0]][position[ID][1]] ) ;
			characters.put(ID, p);			//put in map
			squares[position[ID][0]][position[ID][1]].occupied = "P"+ID; 	//put in Board
		}
	}
	
	/**
	 * Checks for valid move of player with iD in location newRow,newCol
	 * Assume newRow,newCol will NEVER go out of bounds
	 * @param ID	-- current player uID
	 * @param newRow -- row to move to
	 * @param newCol -- column to move to
	 * @return true iff newRow,newCol is a neighbor of current location
	 */
	public boolean movePiece(int ID, int newRow, int newCol) {
		Player p = characters.get(ID);
		Square loc = p.getLocation();
		//check if new coordinates is neighbor of current player's position
		if (squares[loc.row][loc.col].hasNeigbour(newRow, newCol)) {	
			//update board
			squares [newRow][newCol].occupied = "P"+ID;
			squares[loc.row][loc.col].occupied = null;
			//update player location
			p.setLocation(squares [newRow] [newCol]);
			return true;
		}
		return false;
	}
	
	/**
	 * Move a player with ID to newRow,newCol
	 * Assume newRow,newCol is vacant i.e. no occupant
	 * @param ID -- uID of Player chosen in suggestion
	 * @param newRow -- row to move to
	 * @param newCol -- column to move to
	 */
	public void movePlayerViaSuggestion(int ID, int newRow, int newCol) {
		Player p = characters.get(ID);
		Square loc = p.getLocation();
		//update board
		squares [newRow][newCol].occupied = "P"+ID;
		squares[loc.row][loc.col].occupied = null;
		//update player location
		p.setLocation(squares [newRow] [newCol]);
	}
	
	/**
	 * Get Square at row, col
	 * Assume it will NEVER go out of bounds
	 * @param row
	 * @param col
	 * @return
	 */
	public Square getSquare(int row, int col) {
		return squares[row][col];
	}

	/**
	 * Get Player instance with corresponding ID
	 * @param ID -- current player
	 * @return
	 */
	public Player getPlayer(int ID) {
		return characters.get(ID);
	}
	
	/**
	 * Check where player with ID is i.e. Kitchen, Lounge etc
	 * Return NULL if not in any Room i.e. is in playArea
	 * @param ID -- current player
	 * @return name of room
	 */
	public String inRoom(int ID) {
		Player p = characters.get(ID);
		if (p.getLocation().kind == Type.ROOM) {
			return p.getLocation().getName();
		}
		return null;
	}
	
	/**
	 * Remove player with ID from the gameplay
	 * But can still show cards during accusations
	 * When making false accusations
	 * @param ID -- current player
	 */
	public void expel (int ID) {
		characters.get(ID).setPlaying(false);
	}
	
	/**
	 * Check if player with ID has been disqualified/removed/expelled or not
	 * @param ID
	 * @return true if player is still active/playing
	 */
	public boolean isPlaying(int ID) {
		return characters.get(ID).isPlaying();
	}

	/**
	 * Returns the Player that plays this character
	 * @param character -- the name of the character that we want to find the player to
	 * @return
	 */
	public Player getPlayer(String character) {
		for (Map.Entry<Integer, Player> entry: characters.entrySet()) {
			Player p = entry.getValue();
			if (p.character.equals(character)) {
				return p;
			}
		}
		return null;
	}

}
