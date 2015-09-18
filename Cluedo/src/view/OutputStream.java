package view;

import java.util.Map;

import main.Loader;
import model.Board;
import model.Card;
import model.Player;
import model.Square;

/**
 * Manages printing of output and user options through System.out
 * @author Ronni Perez
 *
 */
public class OutputStream {
	/**
	 * Print weapons in rooms (where a room has a weapon in it)
	 * @param weaponsInRoom -- map of room to weapon
	 */
	public static void weaponMappings(Map<String, String> weaponsInRoom) {
		for (Map.Entry<String, String> entry: weaponsInRoom.entrySet()) {
			if (entry.getValue() != null) {		//if has weapon
				System.out.println("A "+entry.getValue()+" was seen in the "+entry.getKey());
			} else {		//else, no weapon
				System.out.println("No weapon seen in the "+entry.getKey());
			}
			sleep(750);	//delay
		}
	}
	
	/**
	 * Print the game board
	 * @param brd -- the game board
	 */
	public static void outBoard (Board brd) {
		System.out.printf("   ");
		for (int i = 0; i < Loader.cols(); i++) {		//print column numbers from 00 to 17
			if (i != Loader.cols()-1)
				System.out.printf("%02d ", i);
			else
				System.out.printf("%02d \n", i);
		}
		
		for (int row = 0; row < Loader.rows(); row++) {
			System.out.printf("%02d ", row);	//print row numbers from 00 to 17
			for (int col = 0; col < Loader.cols(); col++) {	//print string representation of each Square
				System.out.print(brd.getSquare(row, col).toString());
			}
			System.out.println("");
		}
		sleep(750);		//delay
	}
	
	/**
	 * Prints a notification message
	 * @param msg -- the command
	 * @param prompt -- is true iff the program needs to wait for some sort of a user input,
	 * 	it prints "--> Waiting for input"
	 */
	public static void message (String msg, boolean prompt) {
		System.out.println("/** "+msg);
		if (prompt) {		//if required, print "waiting"
			System.out.println("--> Waiting for input");
		}
	}
	
	/**
	 * Prints options to user
	 * @param msg -- the command
	 * @param arr -- array of available options i.e. 	during suggesting, printing weapons:
	 * new String []{
				"Candlestick","Dagger","Lead Pipe","Revolver","Rope","Spanner"
				};
	 */
	public static void printOptions (String msg, String [] arr) {
		System.out.println("/** "+msg);
		for (int i = 0; i < arr.length;i++) {	//one line per each option
			System.out.println(" "+(i+1)+" - "+arr[i]);
		}
		System.out.println("--> Waiting for input");	//print "waiting"
	}

	/**
	 * Prints neighbors of a given Square in row,col format
	 * i.e. all valid Squares that a player can move to
	 * @param location -- Square of a player
	 */
	public static void printNeighbours(Square location) {
		System.out.println("You can move to: ");
		System.out.println("Row, Colunm");
		for (int[] pair :location.neighbours) {		//one line per each neighbors
			System.out.println(pair[0]+" , "+pair[1]);
		}
		System.out.println("--> Waiting for input");	//print "waiting"
	}
	
	/**
	 * Prints all cards held by given player
	 * @param p -- Player's 'instance'
	 */
	public static void printCards(Player p) {
		System.out.println("/**  You have the following cards:");
		for (Card c: p.allCards()) {		//print each card by player
			System.out.println(c.toString());
		}
		sleep(500);		//delay after print
	}

	/**
	 * Causes the currently executing thread to sleep (temporarily cease
     * execution) for the specified number of milliseconds
	 * @param mills -- the length of time to sleep in milliseconds
	 */
	public static void sleep(int mills) {
		try {
			Thread.sleep(mills);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
