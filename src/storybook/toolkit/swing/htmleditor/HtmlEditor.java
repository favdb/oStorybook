/*
 Storybook: Open Source software for novelists and authors.
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
package storybook.toolkit.swing.htmleditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.UndoManager;

import net.atlanticbb.tantlinger.i18n.I18n;
import net.atlanticbb.tantlinger.ui.DefaultAction;
import net.atlanticbb.tantlinger.ui.UIUtils;
import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.atlanticbb.tantlinger.ui.text.Entities;
import net.atlanticbb.tantlinger.ui.text.HTMLUtils;
import net.atlanticbb.tantlinger.ui.text.IndentationFilter;
import net.atlanticbb.tantlinger.ui.text.SourceCodeEditor;
import net.atlanticbb.tantlinger.ui.text.WysiwygHTMLEditorKit;
import net.atlanticbb.tantlinger.ui.text.actions.ClearStylesAction;
import net.atlanticbb.tantlinger.ui.text.actions.FindReplaceAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLEditorActionFactory;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLElementPropertiesAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLFontAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLFontColorAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLHorizontalRuleAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLImageAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLInlineAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLLineBreakAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLLinkAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLTableAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLTextEditAction;
import net.atlanticbb.tantlinger.ui.text.actions.SpecialCharAction;
import net.miginfocom.swing.MigLayout;
import novaworx.syntax.SyntaxFactory;
import novaworx.textpane.SyntaxDocument;
import novaworx.textpane.SyntaxGutter;
import novaworx.textpane.SyntaxGutterBase;

import org.bushe.swing.action.ActionList;
import org.bushe.swing.action.ActionManager;
import org.bushe.swing.action.ActionUIFactory;
import storybook.SbConstants.PreferenceKey;
import storybook.SbConstants.Spelling;
import storybook.model.hbn.entity.Preference;
import storybook.toolkit.I18N;
import storybook.toolkit.PrefUtil;
import storybook.toolkit.html.HtmlUtil;

import com.inet.jortho.SpellChecker;
import java.io.IOException;
import javax.swing.text.BadLocationException;

/**
 * Based on HTMLEditorPane by SHEF / Bob Tantlinger.<br>
 * http://shef.sourceforge.net
 *
 * @author martin
 * @author Bob Tantlinger
 */
@SuppressWarnings("serial")
public class HtmlEditor extends JPanel {

    private static final I18n i18n = I18n.getInstance("net.atlanticbb.tantlinger.shef");

    private static final String INVALID_TAGS[] = {"html", "head", "body", "title"};

    private int maxLength = -1;
    private boolean showFullToolbar = true;

    private JEditorPane wysEditor;
    private SourceCodeEditor srcEditor;
    private JEditorPane focusedEditor;
    private JComboBox fontFamilyCombo;
    private JComboBox paragraphCombo;
    private JTabbedPane tabs;
    // private JMenuBar menuBar;
    private JToolBar formatToolBar;
    private JLabel lbMessage;

    private JMenu editMenu;
    private JMenu formatMenu;
    private JMenu insertMenu;

    private JPopupMenu wysPopupMenu, srcPopupMenu;

    private ActionList actionList;

    private final FocusListener focusHandler = new FocusHandler();
    private final DocumentListener textChangedHandler = new TextChangedHandler();
    private final ActionListener fontChangeHandler = new FontChangeHandler();
    private final ActionListener paragraphComboHandler = new ParagraphComboHandler();
    private final CaretListener caretHandler = new CaretHandler();
    private final MouseListener popupHandler = new PopupHandler();

    private boolean isWysTextChanged;

    public HtmlEditor() {
	initUI();
    }

    public HtmlEditor(boolean showFullToolbar) {
	this.showFullToolbar = showFullToolbar;
	initUI();
    }

    public boolean getShowSimpleToolbar() {
	return showFullToolbar;
    }

    public void setMaxLength(int maxLength) {
	this.maxLength = maxLength;
    }

    public int getMaxLength() {
	return maxLength;
    }

    public void setCaretPosition(int pos) {
	if (tabs.getSelectedIndex() == 0) {
	    wysEditor.setCaretPosition(pos);
	    wysEditor.requestFocusInWindow();
	} else if (tabs.getSelectedIndex() == 1) {
	    srcEditor.setCaretPosition(pos);
	    srcEditor.requestFocusInWindow();
	}
    }

    public void setSelectedTab(int i) {
	tabs.setSelectedIndex(i);
    }

    private void initUI() {
	createEditorTabs();
	createEditorActions();
		// old
	// setLayout(new BorderLayout());
	// add(formatToolBar, BorderLayout.NORTH);
	// add(tabs, BorderLayout.CENTER);
	setLayout(new MigLayout("fill,wrap,ins 0", "", "[][grow][]"));
	add(formatToolBar);
	lbMessage = new JLabel("", JLabel.RIGHT);
	add(lbMessage, "shrink, pos null null 100% 100%");

	add(tabs, "grow, id tabs");
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		wysEditor.requestFocusInWindow();
	    }
	});
    }

    public JMenu getEditMenu() {
	return editMenu;
    }

    public JMenu getFormatMenu() {
	return formatMenu;
    }

    public JMenu getInsertMenu() {
	return insertMenu;
    }

    private void createEditorActions() {
	actionList = new ActionList("editor-actions");

	ActionList paraActions = new ActionList("paraActions");
	ActionList fontSizeActions = new ActionList("fontSizeActions");
	ActionList editActions = HTMLEditorActionFactory.createEditActionList();
	Action objectPropertiesAction = new HTMLElementPropertiesAction();

	// create popup menu
	wysPopupMenu = ActionUIFactory.getInstance().createPopupMenu(editActions);
	wysPopupMenu.addSeparator();
	wysPopupMenu.add(objectPropertiesAction);
	wysPopupMenu.addSeparator();

	// spell checker
	Preference pref = PrefUtil.get(PreferenceKey.SPELLING, Spelling.none.toString());
	Spelling spelling = Spelling.valueOf(pref.getStringValue());
	if (Spelling.none != spelling) {
	    wysPopupMenu.add(SpellChecker.createCheckerMenu());
	    wysPopupMenu.add(SpellChecker.createLanguagesMenu());
	    wysPopupMenu.addSeparator();
	}

	// open URL
	wysPopupMenu.add(new OpenUrlAction(wysEditor));

	srcPopupMenu = ActionUIFactory.getInstance().createPopupMenu(editActions);

	// create file menu
	JMenu fileMenu = new JMenu(i18n.str("file"));

	// create edit menu
	ActionList lst = new ActionList("edits");
	Action act = new ChangeTabAction(0);
	lst.add(act);
	act = new ChangeTabAction(1);
	lst.add(act);
	lst.add(null);// separator
	lst.addAll(editActions);
	lst.add(null);
	lst.add(new FindReplaceAction(false));
	actionList.addAll(lst);
	editMenu = ActionUIFactory.getInstance().createMenu(lst);
	editMenu.setText(i18n.str("edit"));

	// create format menu
	formatMenu = new JMenu(i18n.str("format"));
	lst = HTMLEditorActionFactory.createFontSizeActionList();
	// HTMLEditorActionFactory.createInlineActionList();
	actionList.addAll(lst);
	formatMenu.add(createMenu(lst, i18n.str("size")));
	fontSizeActions.addAll(lst);

	lst = HTMLEditorActionFactory.createInlineActionList();
	actionList.addAll(lst);
	formatMenu.add(createMenu(lst, i18n.str("style")));

	act = new HTMLFontColorAction();
	actionList.add(act);
	formatMenu.add(act);

	act = new HTMLFontAction();
	actionList.add(act);
	formatMenu.add(act);

	act = new ClearStylesAction();
	actionList.add(act);
	formatMenu.add(act);
	formatMenu.addSeparator();

	lst = HTMLEditorActionFactory.createBlockElementActionList();
	actionList.addAll(lst);
	formatMenu.add(createMenu(lst, i18n.str("paragraph")));
	paraActions.addAll(lst);

	lst = HTMLEditorActionFactory.createListElementActionList();
	actionList.addAll(lst);
	formatMenu.add(createMenu(lst, i18n.str("list")));
	formatMenu.addSeparator();
	paraActions.addAll(lst);

	lst = HTMLEditorActionFactory.createAlignActionList();
	actionList.addAll(lst);
	formatMenu.add(createMenu(lst, i18n.str("align")));

	JMenu tableMenu = new JMenu(i18n.str("table"));
	lst = HTMLEditorActionFactory.createInsertTableElementActionList();
	actionList.addAll(lst);
	tableMenu.add(createMenu(lst, i18n.str("insert")));

	lst = HTMLEditorActionFactory.createDeleteTableElementActionList();
	actionList.addAll(lst);
	tableMenu.add(createMenu(lst, i18n.str("delete")));
	formatMenu.add(tableMenu);
	formatMenu.addSeparator();

	actionList.add(objectPropertiesAction);
	formatMenu.add(objectPropertiesAction);

	// create insert menu
	insertMenu = new JMenu(i18n.str("insert"));
	act = new HTMLLinkAction();
	actionList.add(act);
	insertMenu.add(act);

	act = new HTMLImageAction();
	actionList.add(act);
	insertMenu.add(act);

	act = new HTMLTableAction();
	actionList.add(act);
	insertMenu.add(act);
	insertMenu.addSeparator();

	act = new HTMLLineBreakAction();
	actionList.add(act);
	insertMenu.add(act);

	act = new HTMLHorizontalRuleAction();
	actionList.add(act);
	insertMenu.add(act);

	act = new SpecialCharAction();
	actionList.add(act);
	insertMenu.add(act);

	createFormatToolBar(paraActions, fontSizeActions);
    }

    @SuppressWarnings("unchecked")
    private void createFormatToolBar(ActionList blockActs, ActionList fontSizeActs) {
	formatToolBar = new JToolBar();
	formatToolBar.setFloatable(false);
	formatToolBar.setFocusable(false);
	formatToolBar.setLayout(new MigLayout("ins 0,flowx"));
	formatToolBar.setOpaque(false);
	Font comboFont = new Font("Dialog", Font.PLAIN, 11);

	// paragraphs
	PropertyChangeListener propLst = new PropertyChangeListener() {
	    @Override
	    public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("selected")) {
		    if (evt.getNewValue().equals(Boolean.TRUE)) {
			paragraphCombo.removeActionListener(paragraphComboHandler);
			paragraphCombo.setSelectedItem(evt.getSource());
			paragraphCombo.addActionListener(paragraphComboHandler);
		    }
		}
	    }
	};
	for (Iterator it = blockActs.iterator(); it.hasNext();) {
	    Object o = it.next();
	    if (o instanceof DefaultAction) {
		((DefaultAction) o).addPropertyChangeListener(propLst);
	    }
	}
	paragraphCombo = new JComboBox(toArray(blockActs));
	paragraphCombo.setFont(comboFont);
	paragraphCombo.addActionListener(paragraphComboHandler);
	paragraphCombo.setRenderer(new ParagraphComboRenderer());
	if (showFullToolbar) {
	    formatToolBar.add(paragraphCombo, "split 6");
	    formatToolBar.addSeparator();
	}

	// fonts
	Vector fonts = new Vector();
	fonts.add("Default");
	fonts.add("serif");
	fonts.add("sans-serif");
	fonts.add("monospaced");
	GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
	fonts.addAll(Arrays.asList(gEnv.getAvailableFontFamilyNames()));

	fontFamilyCombo = new JComboBox(fonts);
	fontFamilyCombo.setFont(comboFont);
	fontFamilyCombo.addActionListener(fontChangeHandler);
	formatToolBar.add(fontFamilyCombo);
	formatToolBar.addSeparator();

	final JButton fontSizeButton = new JButton(UIUtils.getIcon(UIUtils.X16, "fontsize.png"));
	final JPopupMenu sizePopup = ActionUIFactory.getInstance().createPopupMenu(fontSizeActs);
	ActionListener al = new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		sizePopup.show(fontSizeButton, 0, fontSizeButton.getHeight());
	    }
	};
	fontSizeButton.addActionListener(al);
	configToolbarButton(fontSizeButton);
	if (showFullToolbar) {
	    formatToolBar.add(fontSizeButton);
	}

	Action act;
	act = new HTMLFontColorAction();
	actionList.add(act);
	if (showFullToolbar) {
	    addToToolBar(formatToolBar, act);
	    formatToolBar.addSeparator();
	}

	act = new HTMLInlineAction(HTMLInlineAction.BOLD);
	act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
	actionList.add(act);
	String opts = "";
	if (showFullToolbar) {
	    opts = "newline,split 20";
	}
	addToToolBar(formatToolBar, act, opts);

	act = new HTMLInlineAction(HTMLInlineAction.ITALIC);
	act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
	actionList.add(act);
	addToToolBar(formatToolBar, act);

	act = new HTMLInlineAction(HTMLInlineAction.UNDERLINE);
	act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
	actionList.add(act);
	addToToolBar(formatToolBar, act);

	formatToolBar.addSeparator();

	List alst = HTMLEditorActionFactory.createListElementActionList();
	for (Iterator it = alst.iterator(); it.hasNext();) {
	    act = (Action) it.next();
	    act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
	    actionList.add(act);
	    if (showFullToolbar) {
		addToToolBar(formatToolBar, act);
	    }
	}

	if (showFullToolbar) {
	    formatToolBar.addSeparator();
	}

	alst = HTMLEditorActionFactory.createAlignActionList();
	for (Iterator it = alst.iterator(); it.hasNext();) {
	    act = (Action) it.next();
	    act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
	    actionList.add(act);
	    addToToolBar(formatToolBar, act);
	}

	if (showFullToolbar) {
	    formatToolBar.addSeparator();
	}

	if (showFullToolbar) {
	    act = new HTMLLinkAction();
	    actionList.add(act);
	    addToToolBar(formatToolBar, act);

	    act = new SbHTMLImageAction();
	    actionList.add(act);
	    addToToolBar(formatToolBar, act);

	    act = new HTMLTableAction();
	    actionList.add(act);
	    addToToolBar(formatToolBar, act);
	}
    }

    private void addToToolBar(JToolBar toolbar, Action act) {
	addToToolBar(toolbar, act, "");
    }

    private void addToToolBar(JToolBar toolbar, Action act, String options) {
	AbstractButton button = ActionUIFactory.getInstance().createButton(act);
	configToolbarButton(button);
	toolbar.add(button, options);
    }

    /**
     * Converts an action list to an array. Any of the null "separators" or sub ActionLists are omitted from the array.
     *
     * @param lst
     * @return
     */
    @SuppressWarnings("unchecked")
    private Action[] toArray(ActionList lst) {
	List acts = new ArrayList();
	for (Iterator it = lst.iterator(); it.hasNext();) {
	    Object v = it.next();
	    if (v != null && v instanceof Action) {
		acts.add(v);
	    }
	}

	return (Action[]) acts.toArray(new Action[acts.size()]);
    }

    private void configToolbarButton(AbstractButton button) {
	button.setText(null);
	button.setMnemonic(0);
	button.setMargin(new Insets(1, 1, 1, 1));
	button.setMaximumSize(new Dimension(22, 22));
	button.setMinimumSize(new Dimension(22, 22));
	button.setPreferredSize(new Dimension(22, 22));
	button.setFocusable(false);
	button.setFocusPainted(false);
	// button.setBorder(plainBorder);
	Action a = button.getAction();
	if (a != null) {
	    button.setToolTipText(a.getValue(Action.NAME).toString());
	}
    }

    private JMenu createMenu(ActionList lst, String menuName) {
	JMenu m = ActionUIFactory.getInstance().createMenu(lst);
	m.setText(menuName);
	return m;
    }

    private void createEditorTabs() {
	tabs = new JTabbedPane(SwingConstants.BOTTOM);
	wysEditor = createWysiwygEditor();
	srcEditor = createSourceEditor();

	tabs.addTab("Edit", new JScrollPane(wysEditor));

	JScrollPane scrollPane = new JScrollPane(srcEditor);
	SyntaxGutter gutter = new SyntaxGutter(srcEditor);
	SyntaxGutterBase gutterBase = new SyntaxGutterBase(gutter);
	scrollPane.setRowHeaderView(gutter);
	scrollPane.setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, gutterBase);

	tabs.addTab("HTML", scrollPane);
	tabs.addChangeListener(new ChangeListener() {
	    @Override
	    public void stateChanged(ChangeEvent e) {
		updateEditView();
	    }
	});
    }

    private SourceCodeEditor createSourceEditor() {
	SourceCodeEditor ed = new SourceCodeEditor();
	SyntaxDocument doc = new SyntaxDocument();
	doc.setSyntax(SyntaxFactory.getSyntax("html"));
	CompoundUndoManager cuh = new CompoundUndoManager(doc, new UndoManager());

	doc.addUndoableEditListener(cuh);
	doc.setDocumentFilter(new IndentationFilter());
	doc.addDocumentListener(textChangedHandler);
	ed.setDocument(doc);
	ed.addFocusListener(focusHandler);
	ed.addCaretListener(caretHandler);
	ed.addMouseListener(popupHandler);

	return ed;
    }

    private JEditorPane createWysiwygEditor() {
	JEditorPane ed = new JEditorPane();

	ed.setEditorKitForContentType("text/html", new WysiwygHTMLEditorKit());
	ed.setContentType("text/html");
	insertHTML(ed, "<p></p>", 0);

	ed.addCaretListener(caretHandler);
	ed.addFocusListener(focusHandler);
	// spell checker, must be before the popup handler
	Preference pref = PrefUtil.get(PreferenceKey.SPELLING, Spelling.none.toString());
	Spelling spelling = Spelling.valueOf(pref.getStringValue());
	if (Spelling.none != spelling) {
	    SpellChecker.register(ed);
	}
	ed.addMouseListener(popupHandler);

	HTMLDocument document = (HTMLDocument) ed.getDocument();
	CompoundUndoManager cuh = new CompoundUndoManager(document, new UndoManager());
	document.addUndoableEditListener(cuh);
	document.addDocumentListener(textChangedHandler);

	return ed;
    }

    // inserts html into the wysiwyg editor
    private void insertHTML(JEditorPane editor, String html, int location) {
	try {
	    HTMLEditorKit kit = (HTMLEditorKit) editor.getEditorKit();
	    Document doc = editor.getDocument();
	    StringReader reader = new StringReader(HTMLUtils.jEditorPaneizeHTML(html));
	    kit.read(reader, doc, location);
	} catch (IOException | BadLocationException ex) {
	    ex.printStackTrace();
	}
    }

    // called when changing tabs
    private void updateEditView() {
	if (tabs.getSelectedIndex() == 0) {
	    String topText = removeInvalidTags(srcEditor.getText());
	    wysEditor.setText("");
	    insertHTML(wysEditor, topText, 0);
	    CompoundUndoManager.discardAllEdits(wysEditor.getDocument());
	} else {
	    String topText = removeInvalidTags(wysEditor.getText());
	    if (isWysTextChanged || srcEditor.getText().equals("")) {
		String t = deIndent(removeInvalidTags(topText));
		t = Entities.HTML40.unescapeUnknownEntities(t);
		srcEditor.setText(t);
	    }
	    CompoundUndoManager.discardAllEdits(srcEditor.getDocument());
	}
	isWysTextChanged = false;
	paragraphCombo.setEnabled(tabs.getSelectedIndex() == 0);
	fontFamilyCombo.setEnabled(tabs.getSelectedIndex() == 0);
	updateState();
    }

    public void setText(String text) {
	String topText = removeInvalidTags(text);
	if (tabs.getSelectedIndex() == 0) {
	    wysEditor.setText("");
	    insertHTML(wysEditor, topText, 0);
	    CompoundUndoManager.discardAllEdits(wysEditor.getDocument());
	} else {
	    {
		String t = deIndent(removeInvalidTags(topText));
		t = Entities.HTML40.unescapeUnknownEntities(t);
		srcEditor.setText(t);
	    }
	    CompoundUndoManager.discardAllEdits(srcEditor.getDocument());
	}
    }

    public String getText() {
	String topText = "";
	// return only body content
	try {
	    if (tabs.getSelectedIndex() == 0) {
		HTMLDocument doc = (HTMLDocument) wysEditor.getDocument();
		// HTMLWriter htmlWriter = new HtmlBodyWriter(writer, doc);
		// htmlWriter.write();
		// StringWriter writer = new StringWriter();
		// topText = writer.toString();
		topText = HtmlUtil.getContent(doc);
		topText = removeInvalidTags(topText);
	    } else {
		topText = removeInvalidTags(srcEditor.getText());
		topText = deIndent(removeInvalidTags(topText));
		topText = Entities.HTML40.unescapeUnknownEntities(topText);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return topText;
	// OLD
//		String topText;
//		if (tabs.getSelectedIndex() == 0) {
//			topText = removeInvalidTags(wysEditor.getText());
//		} else {
//			topText = removeInvalidTags(srcEditor.getText());
//			topText = deIndent(removeInvalidTags(topText));
//			topText = Entities.HTML40.unescapeUnknownEntities(topText);
//		}
//		return topText;
    }

    /* *******************************************************************
     * Methods for dealing with HTML between wysiwyg and source editors
     * *****************************************************************
     */
    private String deIndent(String html) {
	String ws = "\n    ";
	StringBuilder sb = new StringBuilder(html);
	while (sb.indexOf(ws) != -1) {
	    int s = sb.indexOf(ws);
	    int e = s + ws.length();
	    sb.delete(s, e);
	    sb.insert(s, "\n");
	}
	return sb.toString();
    }

    private String removeInvalidTags(String html) {
	for (String invalid_tag : INVALID_TAGS) {
	    html = deleteOccurance(html, '<' + invalid_tag + '>');
	    html = deleteOccurance(html, "</" + invalid_tag + '>');
	}
	return html.trim();
    }

    private String deleteOccurance(String text, String word) {
	StringBuilder sb = new StringBuilder(text);
	int p;
	while ((p = sb.toString().toLowerCase().indexOf(word.toLowerCase())) != -1) {
	    sb.delete(p, p + word.length());
	}
	return sb.toString();
    }

    private void updateState() {
	if (focusedEditor == wysEditor) {
	    fontFamilyCombo.removeActionListener(fontChangeHandler);
	    String fontName = HTMLUtils.getFontFamily(wysEditor);
	    if (fontName == null) {
		fontFamilyCombo.setSelectedIndex(0);
	    } else {
		fontFamilyCombo.setSelectedItem(fontName);
	    }
	    fontFamilyCombo.addActionListener(fontChangeHandler);
	}
	actionList.putContextValueForAll(HTMLTextEditAction.EDITOR, focusedEditor);
	actionList.updateEnabledForAll();
    }

    private class CaretHandler implements CaretListener {
	@Override
	public void caretUpdate(CaretEvent e) {
	    if (maxLength > 0) {
		int len = maxLength - getText().length() - 1;
		if (len < 0) {
		    lbMessage.setForeground(Color.red);
		} else {
		    lbMessage.setForeground(Color.black);
		}
		lbMessage.setText(I18N.getMsg("msg.editor.letters.left", len));
	    }
	    updateState();
	}
    }

    private class PopupHandler extends MouseAdapter {

	@Override
	public void mousePressed(MouseEvent e) {
	    checkForPopupTrigger(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	    checkForPopupTrigger(e);
	}

	private void checkForPopupTrigger(MouseEvent e) {
	    if (e.isPopupTrigger()) {
		JPopupMenu p;
		if (e.getSource() == wysEditor) {
		    p = wysPopupMenu;
		} else if (e.getSource() == srcEditor) {
		    p = srcPopupMenu;
		} else {
		    return;
		}
		p.show(e.getComponent(), e.getX(), e.getY());
	    }
	}
    }

    private class FocusHandler implements FocusListener {

	@Override
	public void focusGained(FocusEvent e) {
	    if (e.getComponent() instanceof JEditorPane) {
		JEditorPane ed = (JEditorPane) e.getComponent();
		CompoundUndoManager.updateUndo(ed.getDocument());
		focusedEditor = ed;

		updateState();
		// updateEnabledStates();
	    }
	}

	@Override
	public void focusLost(FocusEvent e) {

	    if (e.getComponent() instanceof JEditorPane) {
				// focusedEditor = null;
		// wysiwygUpdated();
	    }
	}
    }

    private class TextChangedHandler implements DocumentListener {

	@Override
	public void insertUpdate(DocumentEvent e) {
	    textChanged();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
	    textChanged();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	    textChanged();
	}

	private void textChanged() {
	    if (tabs.getSelectedIndex() == 0) {
		isWysTextChanged = true;
	    }
	}
    }

    private class ChangeTabAction extends DefaultAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	int tab;

	public ChangeTabAction(int tab) {
	    super((tab == 0) ? i18n.str("rich_text") : i18n.str("source"));
	    this.tab = tab;
	    putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_RADIO);
	}

	@Override
	protected void execute(ActionEvent e) {
	    tabs.setSelectedIndex(tab);
	    setSelected(true);
	}

	@Override
	protected void contextChanged() {
	    setSelected(tabs.getSelectedIndex() == tab);
	}
    }

    private class ParagraphComboHandler implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
	    if (e.getSource() == paragraphCombo) {
		Action a = (Action) (paragraphCombo.getSelectedItem());
		a.actionPerformed(e);
	    }
	}
    }

    private class ParagraphComboRenderer extends DefaultListCellRenderer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
		int index, boolean isSelected, boolean cellHasFocus) {
	    if (value instanceof Action) {
		value = ((Action) value).getValue(Action.NAME);
	    }

	    return super.getListCellRendererComponent(list, value, index,
		    isSelected, cellHasFocus);
	}
    }

    private class FontChangeHandler implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
	    if (e.getSource() == fontFamilyCombo && focusedEditor == wysEditor) {
		// MutableAttributeSet tagAttrs = new SimpleAttributeSet();
		HTMLDocument document = (HTMLDocument) focusedEditor.getDocument();
		CompoundUndoManager.beginCompoundEdit(document);

		if (fontFamilyCombo.getSelectedIndex() != 0) {
		    HTMLUtils.setFontFamily(wysEditor, fontFamilyCombo.getSelectedItem().toString());
		} else {
		    HTMLUtils.setFontFamily(wysEditor, null);
		}
		CompoundUndoManager.endCompoundEdit(document);
	    }
	}

	public void itemStateChanged(ItemEvent e) {
	}
    }
}
