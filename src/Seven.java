import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Seven {

	protected Shell shell;
	Vector<Card> deck = new Vector <Card> (33);
	Vector<Card> player = new Vector<Card>(4);
	Vector<Card> robot = new Vector<Card>(4);
	Card down;
	Card original;
	int score;
	JButton card1, card2, card3, card4; 
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Seven window = new Seven();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	// Define Card class 	
		class Card {
				public int value;
				public char c;
				public Card () {}
				public Card (int nvalue, char nc) {
					this.value = nvalue;
					this.c = nc;
				}
			}
			
	// Generate deck and shuffle
		void generate ( Vector <Card> deck) {
		// Generate deck
		int i;
		for (i = 7; i <= 14; i++)
		{	
			deck.addElement(new Card(i,'c'));
			deck.addElement(new Card(i,'s'));
			deck.addElement(new Card(i,'h'));
			deck.addElement(new Card(i,'d'));
		}	
		//Shuffle deck
		i = 1;
		while (i < 30)
		{
			Random rand = new Random();
			int j = rand.nextInt(31) + 1;
			int k = rand.nextInt(32-j) + 1;
			Card aux = new Card();
			aux = deck.get(k);
			deck.set(k, deck.get(j));
			deck.set(j, aux);
		}
		}
			
	// Distribute cards
		void draw (int nr, Vector<Card> deck, Vector<Card> player, Vector <Card> robot)
		{
		    int n = deck.size();
		    int i, j;
		    if (n < 2*nr)
		    	nr = n/2;
		    for (i = 1; i <= nr; i++)
		    {
		    	player.addElement(deck.elementAt(i));
		    	deck.remove(i);
		    }
		    for (j = 1; j <= nr; j++)
		    {
		    	robot.addElement(deck.elementAt(j));
		    	deck.remove(j);
		    }
		}
		
	// Count how many times the card appears in hand 
		int count( int x, Vector<Card> hand)
		{
			int rez = 0;
			int n = hand.size();
			for (int i = 1; i <= n; i++)
				if (hand.get(i).value == x)
					rez++;
			return rez;
		}

	// Verify if hand has potatoes -> return 0 if not exits, 1 if distinct and x if x has more than one appearance
		int potato (Vector <Card> hand)
		{
			int n = hand.size();
			int i,j;
			for (i = 1; i < n; i++)
				for (j = i+1; j <= n; j++)
					if (hand.get(i).value == hand.get(j).value && hand.get(i).value != 7 && hand.get(i).value != 10 && hand.get(i).value != 14)
						return hand.get(i).value;
			for (i = 1; i < n; i++)
				if (hand.get(i).value != 7 && hand.get(i).value != 10 && hand.get(i).value != 14)
					return 1;
			return 0;
		}

	// Count special cards in hand 
		int count_special (Vector<Card> hand)
		{
			int nr = 0;
			nr += count(7, hand);
			nr += count(10, hand);
			nr += count(14, hand);
			return nr;
		}
	
	// Play card
		void play_card (Vector<Card> hand, int thrw, Card down)
		{
			int n = hand.size();
			int i;
			for (i = 1; i <= n; i++)
				if (hand.get(i).value == thrw)
				{
					down.value = thrw;
					down.c = hand.get(i).c;
					hand.remove(i);
					break;
				}
		}

	//Display new cards 
		void update_table(Vector<Card> player, Vector<Card> robot, Card down)
		{
			
		}
	// START robot strategy 
		void START (Vector<Card> hand, Card original, Card down, int ten_freq, int fteen_freq, int score)
		{
			int i;
			if (count_special(hand) >= 3)
				{
					if (count(10, hand) > count (14, hand))
						{
							play_card(hand, 10, down);
							ten_freq++;
							score++;
							return;
						}
					if (count(14, hand) > count(10, hand))
					{
						play_card(hand, 14, down);
						fteen_freq++;
						score++;
						return;
					}
					if (count(14, hand) == count(10, hand) && count(14,hand) > 0)
					{
						play_card(hand, 10, down);
						ten_freq++;
						score++;
						return;
					}
					play_card(hand, 7, down);
					return;
				}
			if (potato(hand) > 1)
				{
					play_card(hand, potato(hand), down);
					original.value = potato(hand);
					return;
				}
			int n = hand.size();
			for (i = 1; i <= n; i++)
				if (hand.get(i).value != 7 && hand.get(i).value != 10 && hand.get(i).value != 14)
				{
					play_card(hand, hand.get(i).value, down);
					return;
				}
		}
	
	// CONTINUE FREE - returns 1 if robot continues and executes moves, 0 if the robot stops
		boolean CONTINUE_FREE (Vector<Card> hand, Card original, Card down, int ten_freq, int fteen_freq, int score)
		{
			int n = hand.size();
			if (original.value != 10 && original.value != 14)
				return false;
			int point;
			if (original.value == 10)
				point = 14;
			else
				point = 10;
			if (count_special(hand) - count(point,hand) >= n-1)
			{
				if (count(original.value, hand) > 0)
				{
					play_card(hand, original.value, down);
					if (point == 10)
						fteen_freq++;
					else
						ten_freq++;
					score++;
					return true;
				}
			play_card(hand, 7, down);
			return true;
			}
			return false;
		}

	// CONTINUE FORCED - robot answers when forced to play
		
		void CONTINUE_FORCED (Vector<Card> hand, Card original, Card down, int ten_freq, int fteen_freq, int score)
		{
			int i;
			int n = hand.size();
			if (original.value != 10 && original.value !=14)
			{
				if (count(original.value, hand) > 0)
				{
					play_card(hand, original.value, down);
					return;
				}
				if (potato(hand) > 0)
					for (i = 1; i <= n; i++)
						if (hand.get(i).value != 7 && hand.get(i).value != 10 && hand.get(i).value != 14)
						{
							play_card(hand, hand.get(i).value, down);
							return;
						}
				if (count(7, hand) > 0)
				{
					play_card(hand, 7, down);
					return;
				}
				if (count(10, hand) > 0 && ten_freq >= fteen_freq)
				{
					play_card(hand, 10, down);
					ten_freq++;
					score++;
					return;
				}
				if (count(14, hand) > 0 && fteen_freq >= ten_freq)
				{
					play_card(hand, 14, down);
					fteen_freq++;
					score++;
					return;
				}
			}
			int point;
			if (original.value == 10)
				point = 14;
			else 
				point = 10;
			if (count_special(hand) - count(point,hand) >= n-1)
				if (count(original.value, hand) > 0)
				{
					play_card(hand, original.value, down);
					if (point == 10)
						fteen_freq++;
					else
						ten_freq++;
					score++;
					return;
				}
				else
					if (count_special(hand) - count(point,hand) >= n-2) 
						if (count(7, hand) > 0)
						{
							play_card(hand, 7, down);
							return;
						}
						else
						{
							play_card(hand, original.value, down);
							if (point == 14)
								ten_freq++;
							else
								fteen_freq++;
							score++;
							return;
						}
			if (potato(hand) > 0)
			{
				for (i = 1; i <= n; i++)
					if (hand.get(i).value != 7 && hand.get(i).value != 10 && hand.get(i).value != 14)
					{
						play_card(hand, hand.get(i).value, down);
						return;
					}
			}
			if (count(7, hand) > 0)
			{
				play_card(hand, 7, down);
				return;
			}
			play_card(hand, point, down);
			score++;
			if (point == 10)
				ten_freq++;
			else
				fteen_freq++;
			return;
		}
		
	// Player choose card
		void pick (Vector <Card> hand, boolean stop, Card down)
		{
			card1.setEnabled(true);
			card2.setEnabled(true);
			card3.setEnabled(true);
			card4.setEnabled(true);
			if (card1.isEnabled() == true)
				card1.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						down.value = hand.get(1).value;
						down.c = hand.get(1).c;
						hand.get(1).value = -1;
						hand.get(1).c = 'a';
						card1.setEnabled(false);
						card2.setEnabled(false);
						card3.setEnabled(false);
						card4.setEnabled(false);
					}
				});
			if (card2.isEnabled() == true)
				card2.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						down.value = hand.get(2).value;
						down.c = hand.get(2).c;
						hand.get(2).value = -1;
						hand.get(2).c = 'a';
						card1.setEnabled(false);
						card2.setEnabled(false);
						card3.setEnabled(false);
						card4.setEnabled(false);
					}
				});
			if (card3.isEnabled() == true)
				card3.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						down.value = hand.get(3).value;
						down.c = hand.get(3).c;
						hand.get(3).value = -1;
						hand.get(3).c = 'a';
						card1.setEnabled(false);
						card2.setEnabled(false);
						card3.setEnabled(false);
						card4.setEnabled(false);
					}
				});	
			if (card4.isEnabled() == true)
				card4.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						down.value = hand.get(4).value;
						down.c = hand.get(4).c;
						hand.get(4).value = -1;
						hand.get(4).c = 'a';
						card1.setEnabled(false);
						card2.setEnabled(false);
						card3.setEnabled(false);
						card4.setEnabled(false);
					}
				});
		}
	// NEW GAME
		void NEWGAME ()
		{
			//New deck
			
			generate (deck);
			
			// Create player and robot cards
			draw(4, deck, player, robot);
			
			int winner = 1; // we presume that player won last round
			score = 0;
			int ten_freq = 0;
			int fteen_freq = 0;
			do
			{
			down.value = -1;
			down.c = 'a';
			original.value = -1;
			original.c = 'a';
			switch (winner) {
				case 0 : 
				{
					boolean stop = false;
					int k = 0;
					do
					{
						pick(player, stop, down);
						if (k == 0)
							{
								original.value = down.value;
								k++;
							}
						CONTINUE_FORCED(robot, original, down, ten_freq, fteen_freq, score);
					}
					while (stop != true);
					if (down.value == 7 || down.value == original.value)
						winner = 0;
					else
						winner = 1;
					break;
				}
				default : 
				{
					boolean stop = false;
					START(robot, original, down, ten_freq, fteen_freq, score);
					original.value = down.value;
					pick(player, stop, down);
					while (stop == false)
					{
						CONTINUE_FREE(robot, original, down, ten_freq, fteen_freq, score);
						pick(player,stop,down);
					}
					if (down.value == 7 || down.value == original.value)
						winner = 1;
					else
						winner = 0;
					break;
				}
			}
			draw(4 - player.size(), deck, player, robot);
			}
			while (deck.size() > 0);
		}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("Seven");
		
		// Create user buttons
		
		
		//Disable buttons 
		
		card1.setEnabled(false);
		card2.setEnabled(false);
		card3.setEnabled(false);
		card4.setEnabled(false);
		
		// Create table
		
		JFrame table = new JFrame("Seven");
		table.setBackground(new Color(0, 100, 0));
		table.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		table.setVisible(true);
		NEWGAME();
		
	}
}