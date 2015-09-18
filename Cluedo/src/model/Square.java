package model;

import java.util.HashSet;
import java.util.Set;

/**
 * A Square is the actual object at row,col of a board
 * @author  Ronni Perez
 *
 */
public class Square {
	// public because final
	public final int row; // coordinates in array
	public final int col;
	public final Type kind; // room or playArea
	public final Set<int[]> neighbours; // COORDINATES of surrounding Squares of
										// this Square in row,col format
	// protected because used by subclasses
	protected String name; // long name of this Square, no name if playArea
	protected String occupied; // String representation of the occupying Player
								// of this Square
	//private because not meant to be edited illegally
	private String code; // String representation of this Square ()

	public Square(int row, int col, Type kind, String code) {
		this.row = row;
		this.col = col;
		this.kind = kind;
		this.name = " ";
		this.code = code;
		this.neighbours = new HashSet<int[]>();
		this.occupied = null;
	}

	/**
	 * Supplementary method used by Board.validNeigbours(row, col) to add
	 * neighbors
	 * 
	 * @param newRow
	 * @param newCol
	 * @param board
	 */
	public void addNeighbour(int newRow, int newCol, Square[][] board) {
		// check for bounds
		if (newRow >= 0 && newRow < board.length && newCol >= 0
				&& newCol < board[0].length) {
			// add if newRow,newCol is playArea ONLY
			if (board[newRow][newCol].kind == Type.PLAYAREA)
				neighbours.add(new int[] { newRow, newCol });
		}
	}

	/**
	 * Supplementary method used by Board.roomNeigbours(row, col) and
	 * Board.connectToDoorways() to add neighbors
	 * 
	 * @param coords
	 *            -- coordinates of neighbors in row,col format (ALWAYS even number of elements) 
	 *            e.g. new int [] {1,4,5,5,8,4,5,4} means neighbors are: {1,4} {5,5} {8,4} {5,4}
	 */
	public void addNeighbour(int[] coords) {
		for (int i = 0; i < coords.length; i += 2) {
			neighbours.add(new int[] { coords[i], coords[i + 1] });
		}
	}
	
	/**
	 * Checks if row,col is a neighbor of this Square
	 * @param row
	 * @param col
	 * @return true iff this square has Square at row,col as it's neighbor
	 */
	public boolean hasNeigbour(int row, int col) {
		for (int[] pair : neighbours) {		//each row,col coordinates
			if (pair[0] == row && pair[1] == col) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if this Square has a "Room" Square neighbor i.e. is a doorway
	 * @param board -- the game board
	 */
	public void changeToDoorway(Board board) {
		for (int[] pair : neighbours) {		//get row,col coordinates
			if (board.getSquare(pair[0], pair[1]).kind == Type.ROOM ) {
				this.code = "dw";		//change code i.e. string output to dw
				return;
			}
		}
	}

	@Override
	public String toString() {
		if (occupied != null) {
			return occupied.toString() + " ";
		}
		
		if (this.kind == Type.ROOM) {		//if room, then use two-letter code for output
			return code + " ";
		} else if (this.kind == Type.NULL) {		//if null type, then use two-letter code for output
			return code + " ";
		} else if (this.code.equals("dw")) {		//if doorway, then use two-letter code for output
			return code + " ";
		} else {			//else  use double underscores ("__")
			return "__ ";
		}
	}

	// GETTERS
	public String getName() {
		return name;
	}

	public String getOccupied() {
		return occupied;
	}
	
	public String getCode () {
		return code;
	}

	//2 kinds of Square, (part of a) Room or (normal) playarea
	public enum Type {
		ROOM, PLAYAREA, NULL
	}
	
}
