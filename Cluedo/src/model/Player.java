package model;

import java.util.HashSet;
import java.util.Set;

import view.OutputStream;

/**
 * A Player in the game
 * @author Ronni Perez
 *
 */
public class Player {
	// public because final
	public final int ID;		//unique identifier, also the player number
	public final String character;		//character portrayed by this player
	
	private boolean isPlaying;		//disqualified or not?
	private Square location;		//current location in the board
	private Set<Card> cards;	//cards in-hand
	
	public Player(int ID, String character, Square location) {
		this.ID = ID;
		this.isPlaying = true;
		this.character = character;
		this.location = location;
		this.cards = new HashSet<Card>();
	}
	
	/**
	 * Add card to collection of cards in hand
	 * @param cd
	 */
	public void addCard (Card cd) {
		cards.add(cd);
	}
	
	/**
	 * Supplementary method
	 * Checks if Player has Card with the name
	 * Used by Cluedo.checkSuggestion() 
	 * @param name -- long name of card
	 * @param isTesting is true iff in 'jUnit testing'. Set to true when testing
	 * @return true iff player has card
	 */
	public boolean hasCard(String name, boolean isTesting) {
		for (Card c: cards) {
			if (c.name.equals(name)) {
				if (!isTesting) {		//if not testing, output
					OutputStream.message("Player "+ID+" has the card "+name, false);
					OutputStream.sleep(2000);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Supplementary method
	 * Used by Board.movePiece()
	 * @param loc
	 */
	public void setLocation(Square loc) {
		this.location = loc;
	}
	
	/**
	 * Supplementary method
	 * Used by Board.expel()
	 * @param isPlaying
	 */
	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}
	
	//GETTERS
	public Square getLocation() {
		return location;
	}
	
	@Override
	public String toString () {
		return "P"+ID;
	}

	public boolean isPlaying() {
		return isPlaying;
	}
	
	public Set<Card> allCards() {
		return cards;
	}
}
