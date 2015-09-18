package view;

import java.util.NoSuchElementException;
import java.util.Scanner;

import main.Loader;

/**
 * Manages the parsing of user input through System.in
 * @author Ronni Perez
 *
 */
public class InputParser {
	private Scanner scanner = new Scanner( System.in );	//lexer used
	
	/**
	 * Returns a number required for the user to enter from low to high
	 * i.e. require a user to enter a number between 3 to 6, then call:
	 * parseNum (3, 6)
	 * @param low -- lower bound (including)
	 * @param high -- upper bound (including)
	 * @return the number that user entered
	 */
	public int parseNum (int low, int high) {
		int result = -1000;		// default value
		while (result < 0) {		//while input invalid
			try {
				String input = scanner.nextLine();	//get input
				result = Integer.parseInt(input);		//parse number
				if (result < low || result > high) {		//check bounds
					OutputStream.message("Invalid. Enter number between (inclusive) "+low+" and  (inclusive) "+high, true);
					result = -1000;
				}
			} catch (NumberFormatException | NoSuchElementException e) {
				//invalid. either string entered or entered blank (no number)
				OutputStream.message("Invalid. Expected number", true);
				result = -1000;		//set to default so loop would repeat
			}
		}
		return result;
	}

	/**
	 * Supplementary Method
	 * Returns a row, col coordinate (used by Cluedo.chooseMove())
	 * This DOES NOT check that move to coordinate is valid - check later
	 * i.e. 4 , 6 means return: new int [] { 4, 6}
	 * @return int [] containing coordinates in {row, col} format
	 */
	public int[] parseCoords() {
		int row = -1000;	//default values
		int col = -1000;
		while (row < 0 || col < 0) {		//while either row or col is invalid
			try {
				String input = scanner.nextLine();	//get input as one line
				String[] tokens = input.split("\\,");	//separate row,col input by the comma
				//require exactly two values -- row, col
				if (tokens.length != 2) {
					throw new NumberFormatException();
				}
				row = Integer.parseInt(tokens[0]);	//parse numbers
				col = Integer.parseInt(tokens[1]);
			} catch (NumberFormatException | NoSuchElementException e) {
				//invalid. either string entered or entered blank (no number) or too many coordinate arguments
				OutputStream.message("Invalid. Expected row, column format", true);
				row = -1000;	//set to default so loop would repeat
				col = -1000;
			}
		}
		return new int [] {row,col};
	}

	/**
	 * Returns name of chosen character:
	 *"Miss Scarlett","Colonel Mustard","Mrs. White","The Reverend Green","Mrs. Peacock","Professor Plum"
	 * @return name of character/suspect corresponding to number form 1 to 6
	 */
	public String pickCharacter() {
		int choice = parseNum(1, 6);
		return Loader.getPeople() [choice-1];
	}

	/**
	 * Returns name of chosen weapon:
	 * "Candlestick","Dagger","Lead Pipe","Revolver","Rope","Spanner"
	 * @return name of weapon corresponding to number form 1 to 6
	 */
	public String pickWeapon() {
		int choice = parseNum(1, 6);
		return Loader.getWeapons() [choice-1];
	}

	/**
	 * Returns name of chosen room:
	 * "Kitchen","Ball Room","Conservatory","Billiard Room","Library","Study","Hall","Lounge","Dining Room"
	 * @return name of room corresponding to number form 1 to 9
	 */
	public String pickRoom() {
		int choice = parseNum(1, 9);
		return Loader.getRooms() [choice-1];
	}
}
