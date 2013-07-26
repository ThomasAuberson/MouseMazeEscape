import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

/*
 * Java Maze Escape
 * 	
 * Author: Thomas Auberson
 * Version: 0.1*
 *  
 * A JMenuBar which takes arrays of button names
 */

// ***This class is taken from thisLibrary v0.1***
public class MenuBar extends JMenuBar implements ActionListener {

	// FIELDS
	private Display display;

	private int MENUS_NUMBER; 			// *RREQUIRES DEFINITION
	private JMenu[] menus = new JMenu[MENUS_NUMBER];
	
	private String title1 = "Menu", title2; 		// *RREQUIRES DEFINITION
	private String[] menu1 = {"Main Menu"}; 			// *RREQUIRES DEFINITION
	private String[] menu2; 			// *RREQUIRES DEFINITION
		// Menu Commands
		// "***" => JSeparator;
		// "*rad*" => This section will be Radio Buttons
		// "*check*" => This section will be CheckBox Buttons

	
	
	// CONSTRUCTOR
	public MenuBar(Display display) {
		this.display = display;
		//menus[0] = loadMenu(title1, menu1); // *REQUIRES SPECIFYING
		//menus[1] = loadMenu(title2, menu2);
	}

	public JMenu loadMenu(String title, String[] menutitles) {
		JMenu menu = new JMenu(title);
		add(menu);
		boolean radioButtonMode = false;
		boolean checkBoxButtonMode = false;
		ButtonGroup group = new ButtonGroup();

		for (int i = 0; i < menutitles.length; i++) {
			if (menutitles[i].equals("***")) {
				menu.add(new JSeparator());
				radioButtonMode = false;
				checkBoxButtonMode = false;
			} else if (menutitles[i].equals("*rad*")) {
				radioButtonMode = true;
				group = new ButtonGroup();
			} else if (menutitles[i].equals("*check*")) {
				checkBoxButtonMode = true;
			} else {
				if (radioButtonMode) {
					menu.add(radioButton(menutitles[i], group));
				} else if (checkBoxButtonMode) {
					menu.add(checkBoxButton(menutitles[i]));
				} else {
					menu.add(menuItem(menutitles[i]));
				}
			}
		}
		return menu;
	}

	public JMenuItem checkBoxButton(String name) {
		JMenuItem m = new JCheckBoxMenuItem(name);
		m.addActionListener(this);
		m.setActionCommand(name);
		return m;
	}

	public JMenuItem radioButton(String name, ButtonGroup group) {
		JMenuItem m = new JRadioButtonMenuItem(name);
		m.addActionListener(this);
		m.setActionCommand(name);
		group.add(m);
		return m;
	}

	public JMenuItem menuItem(String name) {
		JMenuItem m = new JMenuItem(name);
		m.addActionListener(this);
		m.setActionCommand(name);
		return m;
	}

	// ACTION LISTENER
	public void actionPerformed(ActionEvent e) {
		display.menuButtonClicked(e.getActionCommand());
	}
}
