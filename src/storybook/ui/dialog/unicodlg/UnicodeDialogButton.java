package storybook.ui.dialog.unicodlg;

import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;

/**
 * This class provides the functionality to insert an special character into the
 * document.
 *
 * @author Leighton Weymouth
 * @version $Revision: 1.3 $
 *          <p>
 *          $Id: CharacterButton.java,v 1.3 2003/01/17 10:00:43 glang Exp $
 *          </p>
 */
public class UnicodeDialogButton extends Button implements ActionListener {

	private static final long serialVersionUID = 1L;
	private final String[] labels;
	private JPanel p;
	private String specialChar;

	/**
	 * Constructs this button and sets the icon and action listeners.
	 */
	public UnicodeDialogButton() {
		super("test");
		addActionListener(this);
		labels = new String[117];
		// create the labels of the special characters
		for (int i = 0; i < 96; i++)
			labels[i] = "" + ((char) (160 + i));

		labels[96] = "" + ((char) 338);
		labels[97] = "" + ((char) 339);
		labels[98] = "" + ((char) 352);
		labels[99] = "" + ((char) 353);
		labels[100] = "" + ((char) 376);
		labels[101] = "" + ((char) 402);
		labels[102] = "" + ((char) 8211);
		labels[103] = "" + ((char) 8212);
		labels[104] = "" + ((char) 8216);
		labels[105] = "" + ((char) 8217);
		labels[106] = "" + ((char) 8218);
		labels[107] = "" + ((char) 8220);
		labels[108] = "" + ((char) 8221);
		labels[109] = "" + ((char) 8222);
		labels[110] = "" + ((char) 8224);
		labels[111] = "" + ((char) 8225);
		labels[112] = "" + ((char) 8226);
		labels[113] = "" + ((char) 8230);
		labels[114] = "" + ((char) 8240);
		labels[115] = "" + ((char) 8364);
		labels[116] = "" + ((char) 8482);

		// set up a grid layout
		GridLayout grid = new GridLayout(12, 10);
		grid.setHgap(2);
		grid.setVgap(2);

		p = new JPanel(grid);

		// create a border around the panel
		p.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createBevelBorder(BevelBorder.LOWERED),
				"Special Characters"));

		// add the buttons and their labels
		for (int i = 0; i < labels.length; i++)
			p.add(new MyButton(labels[i]));

	}

	/**
	 * When a user presses the insert special character button this method is
	 * called.
	 */
	public void actionPerformed(ActionEvent e) {
		Object[] messages = { p };

		Object[] options = { new String("Insert Character"),
				new String("Cancel") };

		// show the dialog
		int result = JOptionPane.showOptionDialog(this,
		// The parent that the dialog blocks
				messages, // The dialog message arry
				"Insert Special Character", // The title of the dialog window
				JOptionPane.DEFAULT_OPTION, // option type
				JOptionPane.PLAIN_MESSAGE, // message type
				/* ic */
				null, // option icon, null for none
				options, // options string array, get made into buttons
				options[0]); // option to be made default

		if (result == 0) { // user pressed the insert button
			System.out.println("char : " + specialChar);
//			HTMLDocument doc = (HTMLDocument) textPane.getDocument();
//			final int pos = textPane.getCaretPosition();
//
//			AttributeSet as = textPane.getInputAttributes();
//
//			try {
//				doc.insertString(pos, specialChar, as);
//			} catch (BadLocationException aaa) {
//			}
		}
	}

	/**
	 * Inner class that extends JButton. Each special character button is an
	 * instance of this class. This allows for customized handling of buttons.
	 * Ie. in this case, when a button is clicked, it simple has it's background
	 * colour changed to reflect which special character will be inserted.
	 */
	protected class MyButton extends JButton implements ActionListener,
			FocusListener {
		/**
		 * Construct this button with the given special character as a label.
		 * 
		 * @param label
		 *            The special character to be inserted if this button is
		 *            clicked.
		 */
		public MyButton(String label) {
			super(label);

			this.setMargin(new Insets(0, 0, 0, 0));
			this.setFocusPainted(false);
			addActionListener(this);
			addFocusListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			specialChar = this.getText();
		}

		public void focusGained(FocusEvent e) {
			this.setBackground(Color.yellow);
		}

		public void focusLost(FocusEvent e) {
			this.setBackground(null);
		}

	}

	public String getDescription() {
		return "Special characters";
	}
}