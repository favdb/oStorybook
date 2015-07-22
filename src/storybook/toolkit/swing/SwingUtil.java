/*
 Storybook: Scene-based software for novelists and authors.
 Copyright (C) 2008 - 2012 Martin Mustun

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package storybook.toolkit.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.ToolBarUI;
import javax.swing.plaf.basic.BasicToolBarUI;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import net.atlanticbb.tantlinger.ui.text.WysiwygHTMLEditorKit;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import storybook.SbConstants.Language;
import storybook.SbConstants.LookAndFeel;
import storybook.SbConstants.PreferenceKey;
import storybook.SbConstants.Spelling;
import storybook.model.hbn.entity.Preference;
import storybook.toolkit.BookUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.PrefUtil;
import storybook.toolkit.swing.undo.UndoableTextArea;
import storybook.ui.MainFrame;

public class SwingUtil {

	private static Boolean flashIsRunning = false;

	public static void setLookAndFeel() {
		try {
			// get saved look and feel
			Preference pref = PrefUtil.get(PreferenceKey.LAF, LookAndFeel.cross.name());
			LookAndFeel laf = LookAndFeel.valueOf(pref.getStringValue());
			setLookAndFeel(laf);
		} catch (Exception e) {
			setLookAndFeel(LookAndFeel.cross);
		}
	}

	public static void setLookAndFeel(LookAndFeel lookAndFeel) {
		try {
			String lafClassName = UIManager.getCrossPlatformLookAndFeelClassName();
			switch (lookAndFeel) {
				case cross:
					lafClassName = UIManager.getCrossPlatformLookAndFeelClassName();
					break;
//			case system:
//				lafClassName = UIManager.getSystemLookAndFeelClassName();
//				break;
				default:
					lafClassName = UIManager.getCrossPlatformLookAndFeelClassName();
					break;
			}
			PrefUtil.set(PreferenceKey.LAF, lookAndFeel.name());
			UIManager.setLookAndFeel(lafClassName);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
		}
	}

	@SuppressWarnings("unchecked")
	public static JComboBox createSpellingCombo() {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		for (Spelling spelling : Spelling.values()) {
			if (isLanguageOK(spelling.name()))
				model.addElement(spelling.getI18N());
		}
		return new JComboBox(model);
	}

	public static boolean isLanguageOK(String x) {
		boolean rc = false;
		if (x.contentEquals("none"))
			return (true);
		File f = new File("languagetool" + File.separator + "rules" + File.separator + x.substring(0, x.indexOf("_")));
		if (f.isDirectory())
			rc = true;
		return (rc);
	}

	@SuppressWarnings("unchecked")
	public static JComboBox createLanguageCombo() {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		for (Language lang : Language.values()) {
			model.addElement(lang.getI18N());
		}
		return new JComboBox(model);
	}

	public static void setUnitIncrement(JScrollPane scroller) {
		scroller.getVerticalScrollBar().setUnitIncrement(20);
		scroller.getHorizontalScrollBar().setUnitIncrement(20);
	}

	public static int showBetaDialog(MainFrame mainFrame) {
		int n = JOptionPane.showConfirmDialog(mainFrame,
				I18N.getMsg("msg.common.beta"),
				I18N.getMsg("msg.common.question"), JOptionPane.YES_NO_OPTION);
		return n;
	}

	public static void unfloatToolBar(JToolBar tb) {
		ToolBarUI tbUI = tb.getUI();
		if (tbUI instanceof BasicToolBarUI)
			((BasicToolBarUI) tbUI).setFloating(false, null);
	}

	public static void floatToolBar(JToolBar tb, Point p) {
		ToolBarUI tbUI = tb.getUI();
		if (tbUI instanceof BasicToolBarUI)
			((BasicToolBarUI) tbUI).setFloating(false, p);
	}

	public static String getHumanReadableByteCount(long bytes) {
		return getHumanReadableByteCount(bytes, true);
	}

	public static String getHumanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static void printMemoryUsage() {
		System.out.println(getMemoryUsageHr());
	}

	public static String getMemoryUsageSimpleHr() {
		return getHumanReadableByteCount(getMemoryUsed()) + " / " + getHumanReadableByteCount(getMemoryMax());
	}

	public static String getMemoryUsageHr() {
		return "Memory Usage (used/free/total/max): "
				+ getHumanReadableByteCount(getMemoryUsed()) + " / "
				+ getHumanReadableByteCount(getMemoryFree()) + " / "
				+ getHumanReadableByteCount(getMemoryTotal()) + " / "
				+ getHumanReadableByteCount(getMemoryMax());
	}

	public static long getMemoryUsed() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	public static long getMemoryFree() {
		return Runtime.getRuntime().freeMemory();
	}

	public static long getMemoryMax() {
		return Runtime.getRuntime().maxMemory();
	}

	public static long getMemoryTotal() {
		return Runtime.getRuntime().totalMemory();
	}

	public static JTextComponent createTextComponent(MainFrame mainFrame) {
		JTextComponent tc;
		if (BookUtil.isUseHtmlScenes(mainFrame)) {
			tc = new JEditorPane();
			JEditorPane ep = (JEditorPane) tc;
			ep.setEditorKitForContentType("text/html", new WysiwygHTMLEditorKit());
			ep.setContentType("text/html");
		} else {
			tc = new UndoableTextArea();
			UndoableTextArea ta = (UndoableTextArea) tc;
			ta.setLineWrap(true);
			ta.setWrapStyleWord(true);
			ta.getUndoManager().discardAllEdits();
		}
		return tc;
	}

	public static void replaceComponent(Container cont, int index, Component comp) {
		cont.remove(index);
		// cont.validate();
		cont.add(comp, index);
		cont.validate();
		cont.repaint();
	}

	public static void replaceComponent(Container cont, Component comp1, Component comp2) {
		replaceComponent(cont, comp1, comp2, "");
	}

	public static void replaceComponent(Container cont, Component comp1, Component comp2, String constraints) {
		int index = -1;
		int i = 0;
		for (Component comp : cont.getComponents()) {
			if (comp.equals(comp1)) {
				index = i;
				break;
			}
			++i;
		}
		// cont.setIgnoreRepaint(true);
		cont.remove(comp1);
		cont.add(comp2, constraints, index);
		cont.validate();
		// cont.setIgnoreRepaint(false);
		cont.repaint();
	}

	public static void forceRevalidate(Component comp) {
		comp.invalidate();
		comp.validate();
		comp.repaint();
	}

	public static void setMaxPreferredSize(JComponent comp) {
		comp.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	}

	public static void setMaxWidth(JComponent comp, int width) {
		comp.setMaximumSize(new Dimension(width, Short.MAX_VALUE));
	}

	public static void expandRectangle(Rectangle rect) {
		Point p = rect.getLocation();
		p.translate(-5, -5);
		rect.setLocation(p);
		rect.grow(10, 10);
	}

	public static Font getFontBold(int size) {
		return new Font("Dialog", Font.BOLD, size);
	}

	public static void setAccelerator(JMenuItem menuItem, int key, int mask) {
		menuItem.setAccelerator(KeyStroke.getKeyStroke(key, mask));
	}

	public static void addCopyPasteToPopupMenu(JPopupMenu menu, JComponent comp) {
		HashMap<Object, Action> actions = SwingUtil.createActionTable((JTextComponent) comp);
		Action cutAction = actions.get(DefaultEditorKit.cutAction);
		JMenuItem miCut = new JMenuItem(cutAction);
		miCut.setText(I18N.getMsg("msg.common.cut"));
		miCut.setIcon(I18N.getIcon("icon.small.cut"));
		menu.add(miCut);
		Action copyAction = actions.get(DefaultEditorKit.copyAction);
		JMenuItem miCopy = new JMenuItem(copyAction);
		miCopy.setText(I18N.getMsg("msg.common.copy"));
		miCopy.setIcon(I18N.getIcon("icon.small.copy"));
		menu.add(miCopy);
		Action pasteAction = actions.get(DefaultEditorKit.pasteAction);
		JMenuItem miPaste = new JMenuItem(pasteAction);
		miPaste.setText(I18N.getMsg("msg.common.paste"));
		miPaste.setIcon(I18N.getIcon("icon.small.paste"));
		menu.add(miPaste);
	}

	public static void addCopyToPopupMenu(JPopupMenu menu, JComponent comp) {
		HashMap<Object, Action> actions = SwingUtil.createActionTable((JTextComponent) comp);
		Action copyAction = actions.get(DefaultEditorKit.copyAction);
		JMenuItem miCopy = new JMenuItem(copyAction);
		miCopy.setText(I18N.getMsg("msg.common.copy"));
		miCopy.setIcon(I18N.getIcon("icon.small.copy"));
		menu.add(miCopy);
	}

	public static HashMap<Object, Action> createActionTable(JTextComponent textComponent) {
		HashMap<Object, Action> actions = new HashMap<>();
		Action[] actionsArray = textComponent.getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			Action a = actionsArray[i];
			actions.put(a.getValue(Action.NAME), a);
		}
		return actions;
	}

	public static KeyStroke getKeyStrokeInsert() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0, false);
	}

	public static KeyStroke getKeyStrokeEnter() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
	}

	public static KeyStroke getKeyStrokeCopy() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
	}

	public static KeyStroke getKeyStrokeDelete() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
	}

	public static int getPreferredHeight(Component comp) {
		return new Double(comp.getPreferredSize().getHeight()).intValue();
	}

	public static int getPreferredWidth(Component comp) {
		return new Double(comp.getPreferredSize().getWidth()).intValue();
	}

	public static Color getTableSelectionBackgroundColor() {
		return UIManager.getColor("Table.selectionBackground");
	}

	public static Color getTableBackgroundColor() {
		return getTableBackgroundColor(false);
	}

	public static Color getTableBackgroundColor(boolean colored) {
		if (colored)
			return new Color(0xf4f4f4);
		return UIManager.getColor("Table.background");
	}

	public static Color getTableHeaderColor() {
		return UIManager.getColor("TableHeader.background");
	}

	public static void setForcedSize(Component comp, Dimension dim) {
		comp.setMinimumSize(dim);
		comp.setPreferredSize(dim);
		comp.setMaximumSize(dim);
	}

	public static void printUIDefaults() {
		UIDefaults uiDefaults = UIManager.getDefaults();
		Enumeration<Object> e = uiDefaults.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			Object val = uiDefaults.get(key);
			System.out.println("[" + key.toString() + "]:[" + (null != val ? val.toString() : "(null)") + "]");
		}
	}

	public static JLabel createTimestampLabel() {
		Date date = new Date();
		String dateStr = FastDateFormat.getDateInstance(FastDateFormat.MEDIUM).format(date);
		String timeStr = FastDateFormat.getTimeInstance(FastDateFormat.MEDIUM).format(date);
		return new JLabel(dateStr + " - " + timeStr);
	}

	public static void addCtrlEnterAction(JComponent comp, AbstractAction action) {
		InputMap inputMap = comp.getInputMap();
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), action);
	}

	public static JPanel createNotesPanel(JTextArea taNotes) {
		MigLayout layout = new MigLayout("fill", "", "[top]");
		JPanel panel = new JPanel(layout);
		taNotes.setLineWrap(true);
		taNotes.setWrapStyleWord(true);
		taNotes.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		JScrollPane scroller = new JScrollPane(taNotes);
		scroller.setPreferredSize(new Dimension(400, 400));
		panel.add(scroller, "grow");
		return panel;
	}

	/**
	 * Creates a JSlider and ensure the given value is between min and max.
	 *
	 * @param orientation the orientation of the slider
	 * @param min the minimum value of the slider
	 * @param max the maximum value of the slider
	 * @param value the initial value of the slider
	 * @return the JSlider
	 */
	public static JSlider createSafeSlider(int orientation, int min, int max,
			int value) {
		if (value < min) value = min;
		else if (value > max) value = max;
		return new JSlider(orientation, min, max, value);
	}

	/**
	 * Select all text in a {@link JComponent} if it is a {@link JTextField} or {@link JTextArea}.
	 *
	 * @param comp the component
	 */
	public static void selectAllText(JComponent comp) {
		if (comp instanceof JTextField) {
			JTextField tf = (JTextField) comp;
			tf.setSelectionStart(0);
			tf.setSelectionEnd(tf.getText().length());
		} else if (comp instanceof JTextArea) {
			JTextArea ta = (JTextArea) comp;
			ta.setSelectionStart(0);
			ta.setSelectionEnd(ta.getText().length());
		}
	}

	/**
	 * Flashes the given {@link Component} for 250 milliseconds.
	 *
	 * @param comp the component to flash
	 */
	public static void flashComponent(JComponent comp) {
		synchronized (flashIsRunning) {
			if (flashIsRunning)
				return;
			flashIsRunning = true;
			FlashThread flash = new FlashThread(comp);
			SwingUtilities.invokeLater(flash);
			FlashThread flash2 = new FlashThread(comp, true);
			Timer timer = new Timer(1000, flash2);
			timer.setRepeats(false);
			timer.start();
		}
	}

	public static void flashEnded() {
		synchronized (flashIsRunning) {
			flashIsRunning = false;
		}
	}

	/**
	 * Gets a text file chooser. Only files with the extension ".txt" and directories are shown.
	 *
	 * @return the file chooser
	 */
	public static JFileChooser getTextFileChooser() {
		final JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
		chooser.setFileFilter(filter);
		return chooser;
	}

	/**
	 * Gets the dimension of the screen.
	 *
	 * @return the dimension of the screen
	 */
	public static Dimension getScreenSize() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		return d;
	}

	/**
	 * Enables or disables all children of the given container.
	 *
	 * @param container the container
	 * @param enable if true, the components are enabled, otherwise they are disabled
	 */
	public static void enableContainerChildren(Container container, boolean enable) {
		if (container == null)
			return;
		for (Component comp : container.getComponents()) {
			try {
				comp.setEnabled(enable);
				((JComponent) comp).setOpaque(enable);
				if (comp instanceof Container)
					enableContainerChildren((Container) comp, enable);
			} catch (ClassCastException e) {
				// ignore component
				continue;
			}
		}
	}

	public static String getNiceFontName(Font font) {
		if (font == null)
			return "";
		StringBuilder buf = new StringBuilder();
		buf.append(font.getName());
		buf.append(", ");
		switch (font.getStyle()) {
			case Font.BOLD:
				buf.append("bold");
				break;
			case Font.ITALIC:
				buf.append("italic");
				break;
			case Font.PLAIN:
				buf.append("plain");
				break;
		}
		buf.append(", ");
		buf.append(font.getSize());
		return buf.toString();
	}

	public static String getShortenString(String str, int length) {
		if (str.length() > length)
			return StringUtils.left(str, length) + " ...";
		return str;
	}

	public static Color getBackgroundColor() {
		return Color.white;
	}

	public static String getDayName(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
		return sdf.format(date);
	}

	public static String getTimestamp(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		return sdf.format(date);
	}

	public static Stroke getStorke() {
		return new BasicStroke(1);
	}

	public static Stroke getDotStroke() {
		int w = 1;
		float[] dash = {1, 3};
		float dash_phase = 1;
		return new BasicStroke(w, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, dash, dash_phase);
	}

	public static Stroke getDotStroke2() {
		int w = 1;
		float[] dash = {6, 3};
		float dash_phase = 2;
		return new BasicStroke(w, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, dash, dash_phase);
	}

	public static Stroke getDefaultStorke() {
		return new BasicStroke();
	}

	public static void addEnterAction(JComponent comp, Action action) {
		comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), action);
		comp.getActionMap().put(action, action);
	}

	public static void addEscAction(JComponent comp, Action action) {
		comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), action);
		comp.getActionMap().put(action, action);
	}

	public static Border getBorderDefault() {
		return getBorderDefault(1);
	}

	public static Border getBorderDefault(int thickness) {
		return BorderFactory.createLineBorder(Color.black, thickness);
	}

	public static Border getBorderRed() {
		return BorderFactory.createLineBorder(Color.red);
	}

	public static Border getBorderBlue() {
		return getBorderBlue(1);
	}

	public static Border getBorderBlue(int thickness) {
		return BorderFactory.createLineBorder(Color.blue, thickness);
	}

	public static Border getBorderGray() {
		return BorderFactory.createLineBorder(Color.gray);
	}

	public static Border getBorderLightGray() {
		return BorderFactory.createLineBorder(Color.lightGray);
	}

	public static Border getBorderEtched() {
		return BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	}

	public static Border getBorderDot() {
		return new DotBorder();
	}

	public static Border getBorder(Color clr) {
		return BorderFactory.createLineBorder(clr, 1);
	}

	public static Border getBorder(Color clr, int thickness) {
		return BorderFactory.createLineBorder(clr, thickness);
	}

	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}
	}

	public static JPanel createMenuBarSpacer() {
		return createMenuBarSpacer(false);
	}

	public static JPanel createMenuBarSpacer(boolean linie) {
		MigLayout layout = new MigLayout("insets 0", "[1]");
		JPanel panel = new JPanel(layout);
		panel.setOpaque(false);
		JLabel label = new JLabel(" ");
		if (linie) {
			Border border = BorderFactory.createMatteBorder(0, 1, 0, 0, Color.gray);
			label.setBorder(border);
			panel.add(label, "center");
		}
		panel.setMaximumSize(new Dimension(2, 10));
		return panel;
	}

	public static void setWaitingCursor(Component comp) {
		comp.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}

	public static void setDefaultCursor(Component comp) {
		comp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public static JLabel createHorizontalLineLabel() {
		MatteBorder mb = new MatteBorder(0, 0, 1, 0, Color.black);
		JLabel label = new JLabel();
		label.setBorder(mb);
		return label;
	}

	public static JLabel createVerticalLineLabel() {
		MatteBorder mb = new MatteBorder(0, 1, 0, 0, Color.black);
		JLabel label = new JLabel();
		label.setBorder(mb);
		return label;
	}

	public static CompoundBorder getCompoundBorder(String text) {
		return BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(text),
				BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	public static List<Component> findComponentsByClass(
			Container rootComponent, Class<? extends Component> cname,
			List<Component> res) {
		if (rootComponent instanceof Container) {
			Component[] components = ((Container) rootComponent).getComponents();
			for (Component comp : components) {
				if (cname.isInstance(comp))
					res.add(comp);
				if (comp instanceof Container)
					findComponentsByClass((Container) comp, cname, res);
			}
		}
		return res;
	}

	public static List<Component> findComponentsNameStartsWith(
			Container rootComponent, String startsWith, List<Component> res) {
		if (rootComponent instanceof Container) {
			Component[] components = ((Container) rootComponent).getComponents();
			for (Component comp : components) {
				String name = comp.getName();
				if (name != null && name.startsWith(startsWith))
					res.add(comp);
				if (comp instanceof Container)
					findComponentsNameStartsWith((Container) comp, startsWith, res);
			}
		}
		return res;
	}

	public static Component findComponentByName(Component rootComponent, String name) {
		if (rootComponent.getName() != null)
			if (rootComponent.getName().equals(name))
				return rootComponent;
		if (rootComponent instanceof Container) {
			Component[] components = ((Container) rootComponent).getComponents();
			for (int i = 0; i < components.length; ++i) {
				Component comp = findComponentByName(components[i], name);
				if (comp != null)
					return comp;
			}
		}
		return null;
	}

	public static Component printComponentHierarchy(Component rootComponent) {
		return printComponentHierarchy(rootComponent, -1);
	}

	private static Component printComponentHierarchy(Component rootComponent, int level) {
		++level;
		System.out.println(StringUtils.repeat("    ", level) + level + ":"
				+ formateComponentInfosToPrint(rootComponent));
		if (rootComponent instanceof Container) {
			Component[] components = ((Container) rootComponent).getComponents();
			for (int i = 0; i < components.length; ++i) {
				Component comp = printComponentHierarchy(components[i], level);
				if (comp != null)
					return comp;
			}
		}
		return null;
	}

	public static String formateComponentInfosToPrint(Component comp) {
		StringBuilder buf = new StringBuilder();
		buf.append(comp.getClass().getSimpleName());
		buf.append(" [");
		buf.append(comp.getName());
		if (comp instanceof JLabel)
			buf.append(",\"").append(((JLabel) comp).getText()).append("\"");
		buf.append(",");
		buf.append(comp.getClass().getName());
		buf.append(",");
		buf.append(comp.isVisible() ? "visible" : "not visible");
		buf.append(",");
		buf.append(comp.isValid() ? "valid" : "invalid");
		buf.append("]");
		return buf.toString();
	}

	public static int getComponentIndex(Container container, Component component) {
		int index = 0;
		for (Component comp : container.getComponents()) {
			if (comp == component)
				return index;
			++index;
		}
		return -1;
	}

	public static Frame getFrame(String frameName) {
		Frame[] frames = Frame.getFrames();
		for (Frame frame : frames) {
			if (frame.getName().equals(frameName))
				return frame;
		}
		return null;
	}

	public static JFrame findParentJFrame(JComponent c) {
		if (c == null)
			return null;
		Component parent = c.getParent();
		while (!(parent instanceof JFrame) && (parent != null)) {
			parent = parent.getParent();
		}
		return (JFrame) parent;
	}

	public static JTabbedPane findParentJTabbedPane(JComponent c) {
		if (c == null)
			return null;
		Component parent = c.getParent();
		while (!(parent instanceof JTabbedPane) && (parent != null)) {
			parent = parent.getParent();
		}
		return (JTabbedPane) parent;
	}

	public static Component findParent(Component c) {
		if (c == null)
			return null;
		Component parent = c.getParent();
		Component comp = null;
		while (parent != null) {
			comp = parent;
			parent = comp.getParent();
		}
		return comp;
	}

	public static void showFrame(JFrame frame, JFrame parent) {
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setLocation(parent.getX()
				+ (parent.getWidth() - frame.getWidth()) / 2, parent.getY()
				+ (parent.getHeight() - frame.getHeight()) / 2);
		frame.setVisible(true);
	}

	public static void showDialog(JDialog dlg, Component parent) {
		showDialog(dlg, parent, true);
	}

	public static void showDialog(JDialog dlg, Component parent, boolean resizable) {
		dlg.setResizable(resizable);
		dlg.pack();
		dlg.setLocationRelativeTo(parent);
		dlg.setVisible(true);
	}

	public static void showModalDialog(JDialog dlg, Component parent) {
		showModalDialog(dlg, parent, true);
	}

	public static void showModalDialog(JDialog dlg, Component parent, boolean resizable) {
		dlg.setResizable(resizable);
		dlg.setModal(true);
		dlg.pack();
		dlg.setLocationRelativeTo(parent);
		dlg.setVisible(true);
	}

	@SuppressWarnings("unchecked")
	public static JComboBox createDateFormat() {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement("dd-MM-yyyy");
		model.addElement("MM-dd-yyyy");
		model.addElement("dd/MM/yyyy");
		return new JComboBox(model);
	}
}
