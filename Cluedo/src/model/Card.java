package model;

/**
 * A Card in the game
 * @author Ronni Perez
 *
 */
public class Card {
	public final Type kind;	//character, weapon or room
	public final String name;	//long name of card

	public Card(Type kind, String name) {
		this.kind = kind;
		this.name = name;
	}
	
	@Override
	public String toString() {
		switch (kind) {
		case CHARACTER:
			return "CHRCTR: "+name;
		case ROOM:
			return "RM: "+name;
		default:	// WEAPON:
			return "WPN: "+name;
		}
	}
	
	//3 kinds of cards
	public enum Type {
		CHARACTER, WEAPON, ROOM
	}
}