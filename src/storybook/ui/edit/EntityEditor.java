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
package storybook.ui.edit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;

import org.jopendocument.dom.OOUtils;

import storybook.SbApp;
import storybook.SbConstants;
import storybook.SbConstants.BookKey;
import storybook.SbConstants.ClientPropertyName;
import storybook.SbConstants.ComponentName;
import storybook.controller.BookController;
import storybook.model.EntityUtil;
import storybook.model.handler.AbstractEntityHandler;
import storybook.model.handler.CategoryEntityHandler;
import storybook.model.handler.ChapterEntityHandler;
import storybook.model.handler.GenderEntityHandler;
import storybook.model.handler.IdeaEntityHandler;
import storybook.model.handler.InternalEntityHandler;
import storybook.model.handler.ItemEntityHandler;
import storybook.model.handler.ItemLinkEntityHandler;
import storybook.model.handler.LocationEntityHandler;
import storybook.model.handler.PartEntityHandler;
import storybook.model.handler.PersonEntityHandler;
import storybook.model.handler.RelationshipEntityHandler;
import storybook.model.handler.SceneEntityHandler;
import storybook.model.handler.StrandEntityHandler;
import storybook.model.handler.TagEntityHandler;
import storybook.model.handler.TagLinkEntityHandler;
import storybook.model.handler.TimeEventEntityHandler;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.AbstractTag;
import storybook.model.hbn.entity.Attribute;
import storybook.model.hbn.entity.Category;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Gender;
import storybook.model.hbn.entity.Idea;
import storybook.model.hbn.entity.Internal;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.ItemLink;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Relationship;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;
import storybook.model.hbn.entity.Tag;
import storybook.model.hbn.entity.TagLink;
import storybook.model.hbn.entity.TimeEvent;
import storybook.model.state.IdeaState;
import storybook.model.state.SceneState;
import storybook.model.state.TimeStepState;
import storybook.toolkit.BookUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.completer.AbbrCompleter;
import storybook.toolkit.odt.ODTUtils;
import storybook.toolkit.swing.AutoCompleteComboBox;
import storybook.toolkit.swing.CleverColorChooser;
import storybook.toolkit.swing.ColorUtil;
import storybook.toolkit.swing.FontUtil;
import storybook.toolkit.swing.IconUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.toolkit.swing.htmleditor.HtmlEditor;
import storybook.toolkit.swing.panel.DateChooser;
import storybook.toolkit.swing.panel.PlainTextEditor;
import storybook.toolkit.swing.verifier.AbstractInputVerifier;
import storybook.toolkit.swing.verifier.AbstractInputVerifier.ErrorState;
import storybook.toolkit.swing.verifier.DocumentSizeFilter;
import storybook.ui.MainFrame;
import storybook.ui.RadioButtonGroup;
import storybook.ui.combo.IRefreshableComboModel;
import storybook.ui.dialog.ExceptionDialog;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.panel.attributes.AttributesPanel;
import storybook.ui.table.SbColumn;
import storybook.ui.table.SbColumn.InputType;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class EntityEditor extends AbstractPanel implements ActionListener, ItemListener {

	public static Dimension MINIMUM_SIZE = new Dimension(440, 500);
	private static final String ERROR_LABEL = "error_label";
	private JCheckBox cbLeaveOpen;
	private JTextField tfFile;
	private JButton btChooseFile;

	private enum MsgState {

		ERRORS, WARNINGS, UPDATED, ADDED
	}
	private boolean leaveOpen;
	private AbstractEntityHandler entityHandler;
	private final BookController ctrl;
	private AbstractEntity entity;
	private AbstractEntity origEntity;
	private ErrorState errorState;
	private JTabbedPane tabbedPane;
	private TitlePanel titlePanel;
	private ArrayList<JComponent> inputComponents;
	private ArrayList<JPanel> containers;
	private JButton btAddOrUpdate;
	private JLabel lbMsgState;
	private HashMap<RadioButtonGroup, RadioButtonGroupPanel> rbgPanels;
	private ArrayList<Attribute> attributes;
	private boolean isDialog = false;
	private JDialog fromDialog;

	public EntityEditor(MainFrame m) {
		super(m);
		SbApp.trace("EntityEditor(mainFrame)");
		this.ctrl = mainFrame.getBookController();
	}

	public EntityEditor(MainFrame m, AbstractEntity e, JDialog dlg) {
		super(m);
		SbApp.trace("EntityEditor(mainFrame, " + e.getAbbr() + ")");
		this.ctrl = m.getBookController();
		this.entity = e;
		init();
		initHandler();
		cbLeaveOpen.setVisible(false);
		isDialog = true;
		fromDialog = dlg;
	}

	@Override
	public final void init() {
		SbApp.trace("EntityEditor.init()");
		containers = new ArrayList<>();
		inputComponents = new ArrayList<>();
		rbgPanels = new HashMap<>();
		/*try {
		 Internal internal = BookUtil.get(mainFrame, BookKey.LEAVE_EDITOR_OPEN, SbConstants.DEFAULT_LEAVE_EDITOR_OPEN);
		 if (internal != null) {
		 leaveOpen = internal.getBooleanValue();
		 }
		 } catch (Exception e) {
		 leaveOpen = SbConstants.DEFAULT_LEAVE_EDITOR_OPEN;
		 }*/
	}

	private void initHandler() {
		SbApp.trace("EntityEditor.initHandler()");
		if (entity instanceof Scene) {
			entityHandler = new SceneEntityHandler(mainFrame);
		} else if (entity instanceof Chapter) {
			entityHandler = new ChapterEntityHandler(mainFrame);
		} else if (entity instanceof Part) {
			entityHandler = new PartEntityHandler(mainFrame);
		} else if (entity instanceof Location) {
			entityHandler = new LocationEntityHandler(mainFrame);
		} else if (entity instanceof Person) {
			entityHandler = new PersonEntityHandler(mainFrame);
		} else if (entity instanceof Gender) {
			entityHandler = new GenderEntityHandler(mainFrame);
		} else if (entity instanceof Category) {
			entityHandler = new CategoryEntityHandler(mainFrame);
		} else if (entity instanceof Strand) {
			entityHandler = new StrandEntityHandler(mainFrame);
		} else if (entity instanceof Idea) {
			entityHandler = new IdeaEntityHandler(mainFrame);
		} else if (entity instanceof Tag) {
			entityHandler = new TagEntityHandler(mainFrame);
		} else if (entity instanceof Item) {
			entityHandler = new ItemEntityHandler(mainFrame);
		} else if (entity instanceof TagLink) {
			entityHandler = new TagLinkEntityHandler(mainFrame);
		} else if (entity instanceof ItemLink) {
			entityHandler = new ItemLinkEntityHandler(mainFrame);
		} else if (entity instanceof Internal) {
			entityHandler = new InternalEntityHandler(mainFrame);
		} else if (entity instanceof Relationship) {
			entityHandler = new RelationshipEntityHandler(mainFrame);
		} else if (entity instanceof TimeEvent) {
			entityHandler = new TimeEventEntityHandler(mainFrame);
		}
		if (entity.isTransient()) {
			// new entity
			entity = entityHandler.newEntity(entity);
		}

		// keep origEntity up-to-date
		origEntity = entityHandler.createNewEntity();
		EntityUtil.copyEntityProperties(mainFrame, entity, origEntity);

		initUi();

		// set entity and DAO on input components
		for (JComponent comp : inputComponents) {
			comp.putClientProperty(ClientPropertyName.ENTITY.toString(), entity);
			comp.putClientProperty(ClientPropertyName.DAO.toString(), entityHandler.createDAO());
		}

		if (entity.isTransient()) { // new
			btAddOrUpdate.setText(I18N.getMsg("msg.common.add"));
		} else { // update
			btAddOrUpdate.setText(I18N.getMsg("msg.editor.update"));
		}

		editEntity(/*evt*/);

		if (!entity.isTransient()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					verifyInput();
				}
			});
		}
	}

	/**
	 * Minimum size: see {@link MainFrame#showView(ViewName))}
	 */
	@Override
	public final void initUi() {
		SbApp.trace("EntityEditor.initUi()");
		try {
			setLayout(new MigLayout("fill,wrap"));
			setBackground(Color.white);
			setMinimumSize(MINIMUM_SIZE);

			removeAll();
			containers.clear();
			inputComponents.clear();
			rbgPanels.clear();

			if (entityHandler == null) {
				JLabel lb = new JLabel(I18N.getMsg("msg.editor.nothing.to.edit"), JLabel.CENTER);
				SwingUtil.setMaxPreferredSize(lb);
				add(lb, "top");
				SwingUtil.forceRevalidate(this);
				return;
			}

			titlePanel = new TitlePanel();
			titlePanel.refresh(entity);
			add(titlePanel, "split 2, growx");

			JButton unicodeButton = new JButton();
			Image img = ImageIO.read(getClass().getResource("/storybook/resources/icons/16x16/specchar.png"));
			unicodeButton.setIcon(new ImageIcon(img));
			unicodeButton.setToolTipText(I18N.getMsg("msg.editor.specchar.tooltip"));
			unicodeButton.setMargin(new Insets(0, 0, 0, 0));
			unicodeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					mainFrame.showUnicodeDialog();
				}
			});
			add(unicodeButton, "align right");

			containers.add(new JPanel());
			JPanel container = containers.get(containers.size() - 1);
			container.setLayout(new MigLayout("wrap 2", "[][grow]", ""));
			container.putClientProperty(SbConstants.ClientPropertyName.COMPONENT_TITLE.toString(), I18N.getMsg("msg.common"));

			for (SbColumn col : entityHandler.getColumns()) {
				if (col.isShowInSeparateTab()) {
					containers.add(new JPanel());
					container = containers.get(containers.size() - 1);
					container.setLayout(new MigLayout("fill,wrap", "[grow]", ""));
					container.putClientProperty(SbConstants.ClientPropertyName.COMPONENT_TITLE.toString(), col.toString());
				}

				RadioButtonGroupPanel btgPanel = null;

				// skip label for components shown in a separate tab
				if (!col.isShowInSeparateTab()) {
					// label
					if (col.hasRadioButtonGroup()) {
						// radio button group
						RadioButtonGroup rbg = col.getRadioButtonGroup();
						btgPanel = rbgPanels.get(rbg);
						if (btgPanel == null) {
							btgPanel = new RadioButtonGroupPanel(rbg);
							rbgPanels.put(rbg, btgPanel);
						}
						container.add(btgPanel, "span");
					}
					if (col.getInputType() == InputType.NONE) {
					// skip input type "none"
						// no => used to display information
						//	continue;
					}
					if (col.getInputType() == InputType.SEPARATOR) {
					//JSeparator sep = new JSeparator();
						//container.add(sep, "growx");
					} else {
						JLabel lb = new JLabel();
						lb.setText(col.toString() + ":");
						// make mandatory labels bold, but skip read-only fields
						if (col.hasVerifier()) {
							if (col.getVerifier().isMandatory() && !col.isReadOnly()) {
								lb.setFont(FontUtil.getBoldFont());
							}
						}
						if (col.hasRadioButtonGroup()) {
							btgPanel.getSubPanel(col.getRadioButtonIndex()).add(lb, "top");
						} else {
							container.add(lb, "top");
						}
					}
				}

				// input field
				InputType inputType = col.getInputType();
				JComponent comp = null;
				if (inputType == InputType.NONE) {
					comp = new JLabel();
				} else if (inputType == InputType.SEPARATOR) {
					comp = new JSeparator();
				} else if (inputType == InputType.TEXTFIELD) {
					comp = new JTextField(20);
					if (col.hasVerifier()) {
						if (col.getVerifier().isNumber()) {
							((JTextField) comp).setColumns(col.getMaxChars());
						}
					}
					if (col.isAutoComplete()) {
						comp = new AutoCompleteComboBox();
					} else if (col.hasMaxLength()) {
						AbstractDocument doc = (AbstractDocument) ((JTextField) comp)
								.getDocument();
						doc.setDocumentFilter(new DocumentSizeFilter(col
								.getMaxLength()));
					}
				} else if (inputType == InputType.TEXTAREA) {
					if (col.getMethodName().equals("Description")
							|| col.getMethodName().equals("Notes")) {
						if (BookUtil.isUseHtmlDescr(mainFrame)) {
							comp = new HtmlEditor();
							((HtmlEditor) comp).setMaxLength(col.getMaxLength());
						}
					} else {
						if (BookUtil.isUseHtmlScenes(mainFrame)) {
							comp = new HtmlEditor(BookUtil.isEditorFullToolbar(mainFrame));
							((HtmlEditor) comp).setMaxLength(col.getMaxLength());
						}
					}
					if (comp == null) {
						comp = new PlainTextEditor();
						((PlainTextEditor) comp).setMaxLength(col.getMaxLength());
					}
				} else if (inputType == InputType.CHECKBOX) {
					comp = new JCheckBox();
				} else if (inputType == InputType.COMBOBOX) {
					comp = new JComboBox();
				} else if (inputType == InputType.DATE) {
					comp = new DateChooser(mainFrame, col.hasDateTime());
					comp.setPreferredSize(new Dimension(150, 20));
				} else if (inputType == InputType.COLOR) {
					comp = new CleverColorChooser(I18N.getMsg("msg.dlg.strand.choose.color"), null,
							ColorUtil.getNiceColors(), col.isAllowNoColor());
				} else if (inputType == InputType.ICON) {
					comp = new JLabel();
				} else if (inputType == InputType.LIST) {
					comp = new CheckBoxPanel(mainFrame);
				} else if (inputType == InputType.ATTRIBUTES) {
					comp = new AttributesPanel(mainFrame);
				}

				comp.setName(col.getMethodName());
				comp.putClientProperty(ClientPropertyName.DOCUMENT_MODEL.toString(), mainFrame.getBookModel());
				if (col.isReadOnly()) {
					comp.setEnabled(false);
				}
				if (col.hasVerifier()) {
					comp.setInputVerifier(col.getVerifier());
				}
				if (inputType == InputType.TEXTAREA || inputType == InputType.ATTRIBUTES) {
					if (comp instanceof JTextArea || comp instanceof AttributesPanel) {
						JScrollPane scroller = new JScrollPane(comp);
						SwingUtil.setUnitIncrement(scroller);
						SwingUtil.setMaxPreferredSize(scroller);
						container.add(scroller, "grow,id " + comp.getName());
					} else {
						SwingUtil.setMaxPreferredSize(comp);
						if (comp instanceof HtmlEditor) {
							container.add(new JLabel());
							container.add(comp, "span,grow,id " + comp.getName());
						} else {
							container.add(comp, "grow,id " + comp.getName());
						}
					}
				} else if (inputType == InputType.LIST) {
					JScrollPane scroller = new JScrollPane(comp);
					SwingUtil.setUnitIncrement(scroller);
					SwingUtil.setMaxPreferredSize(scroller);
					container.add(scroller, "grow");
					JButton bt = new JButton(I18N.getMsg("msg.add." + comp.getName()));
					bt.setName("BtAdd" + comp.getName());
					bt.setIcon(I18N.getIcon("icon.small.plus"));
					bt.addActionListener(this);
					container.add(new JLabel());
					container.add(bt, "grow,id " + comp.getName());
				} else if (inputType == InputType.SEPARATOR) {
					container.add(comp, "grow, span");
				} else {
					if (col.hasRadioButtonGroup()) {
						btgPanel.getSubPanel(col.getRadioButtonIndex()).add(comp, "id " + comp.getName());
					} else {
						String growx = "";
						if (col.isGrowX()) {
							growx = "growx";
						}
						if (comp instanceof JSeparator) {
							container.add(comp, "growx");
						} else {
							container.add(comp, growx + ",id " + comp.getName());
						}
					}
				}
				inputComponents.add(comp);
			}

			// handle completer
			for (SbColumn column : entityHandler.getColumns()) {
				if (column.hasCompleter()) {
					if (column.getCompleter() instanceof AbbrCompleter) {
						AbbrCompleter abbrCompleter = (AbbrCompleter) column.getCompleter();
						for (JComponent comp : inputComponents) {
							if (comp.getName().equals(column.getMethodName())) {
								abbrCompleter.setComp((JTextComponent) comp);
							}
							if (comp.getName().equals(abbrCompleter.getCompName1())) {
								comp.addKeyListener(abbrCompleter);
								abbrCompleter.setSourceComp1((JTextComponent) comp);
							}
							if (comp.getName().equals(abbrCompleter.getCompName2())) {
								comp.addKeyListener(abbrCompleter);
								abbrCompleter.setSourceComp2((JTextComponent) comp);
							}
						}
					}
				}
			}

			tabbedPane = new JTabbedPane();
			int i = 0;
			for (JPanel container2 : containers) {
				String title = (String) container2.getClientProperty(SbConstants.ClientPropertyName.COMPONENT_TITLE.toString());
				container2.setName(title);
				if (i == 0) {
					// put the first panel into a scroller (for small screens)
					container2.setPreferredSize(new Dimension(400, 520));
					JScrollPane scroller = new JScrollPane(container2);
					SwingUtil.setUnitIncrement(scroller);
					tabbedPane.addTab(title, scroller);
				} else {
					tabbedPane.addTab(title, container2);
				}
				++i;
			}
			if (entity instanceof Scene) {
				if (BookUtil.isUseLibreOffice(mainFrame)) {
					tabbedPane.add(panelLibreOffice((Scene) entity));
				}
			}
			SwingUtil.setMaxPreferredSize(tabbedPane);
			add(tabbedPane);

			lbMsgState = new JLabel();

			JButton btOk = new JButton(I18N.getMsg("msg.common.ok"));
			btOk.setName(ComponentName.BT_OK.toString());
			btOk.setIcon(I18N.getIcon("icon.small.ok"));
			btOk.addActionListener(this);

			btAddOrUpdate = new JButton(I18N.getMsg("msg.editor.update"));
			btAddOrUpdate.setName(ComponentName.BT_ADD_OR_UPDATE.toString());
			if (I18N.isEnglish()) {
				btAddOrUpdate.setIcon(I18N.getIcon("icon.small.refresh"));
			}
			btAddOrUpdate.addActionListener(this);

			JButton btCancel = new JButton(I18N.getMsg("msg.common.cancel"));
			btCancel.setName(ComponentName.BT_CANCEL.toString());
			if (I18N.isEnglish()) {
				btCancel.setIcon(I18N.getIcon("icon.small.cancel"));
			}
			btCancel.addActionListener(this);
			SwingUtil.addEscAction(btCancel, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					abandonEntityChanges();
					refresh();
					if (!leaveOpen) {
						mainFrame.hideEditor();
					}
					if (isDialog == true) {
						fromDialog.dispose();
					}
				}
			});

			cbLeaveOpen = new JCheckBox();
			cbLeaveOpen.setText(I18N.getMsg("msg.editor.leave.open"));
			cbLeaveOpen.setOpaque(false);
			cbLeaveOpen.addItemListener(this);
			cbLeaveOpen.setSelected(leaveOpen);

			add(lbMsgState, "growx");
			add(btOk, "span,split 4,sg");
			add(btAddOrUpdate, "sg");
			add(btCancel, "sg");
			if (entity instanceof Scene) {
				if (BookUtil.isUseLibreOffice(mainFrame)) {
					JButton btODT = new JButton(I18N.getMsg("msg.libreoffice.ok"));
					btODT.setName(ComponentName.BT_ODT.toString());
					btODT.setIcon(I18N.getIcon("icon.small.ok"));
					btODT.addActionListener(this);
					add(btODT, "sg");
				}
			}
			//add(cbLeaveOpen, "gap push");

			SwingUtil.forceRevalidate(this);
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionDialog dlg = new ExceptionDialog(e);
			SwingUtil.showDialog(dlg, null);
		}
	}

	private void setMsgState(MsgState state) {
		String text = "";
		Icon icon = null;
		switch (state) {
			case ERRORS:
				text = I18N.getMsg("msg.common.error");
				icon = IconUtil.StateIcon.ERROR.getIcon();
				break;
			case WARNINGS:
				text = I18N.getMsg("msg.common.warning");
				icon = IconUtil.StateIcon.WARNING.getIcon();
				break;
			case ADDED:
				text = I18N.getMsg("msg.editor.added");
				icon = IconUtil.StateIcon.OK.getIcon();
				break;
			case UPDATED:
				text = I18N.getMsg("msg.editor.updated");
				icon = IconUtil.StateIcon.OK.getIcon();
				break;
		}
		lbMsgState.setVisible(true);
		lbMsgState.setText(text);
		lbMsgState.setIcon(icon);

		if (state == MsgState.ADDED || state == MsgState.UPDATED) {
			Timer timer = new Timer(1500, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					lbMsgState.setText("");
					lbMsgState.setIcon(null);
				}
			});
			timer.setRepeats(false);
			timer.start();
		}
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		SbApp.trace("EntityEditor.modelPropertyChange(evt)");
		String propName = evt.getPropertyName();
		if (propName.startsWith("Delete")) {
			if (entity != null && entity.equals((AbstractEntity) evt.getOldValue())) {
				entityHandler = null;
				initUi();
			}
			return;
		}

		if (!propName.startsWith("Edit")) {
			return;
		}
		if (BookController.SceneProps.EDIT.check(propName)) {
			entityHandler = new SceneEntityHandler(mainFrame);
		} else if (BookController.ChapterProps.EDIT.check(propName)) {
			entityHandler = new ChapterEntityHandler(mainFrame);
		} else if (BookController.PartProps.EDIT.check(propName)) {
			entityHandler = new PartEntityHandler(mainFrame);
		} else if (BookController.LocationProps.EDIT.check(propName)) {
			entityHandler = new LocationEntityHandler(mainFrame);
		} else if (BookController.PersonProps.EDIT.check(propName)) {
			entityHandler = new PersonEntityHandler(mainFrame);
		} else if (BookController.GenderProps.EDIT.check(propName)) {
			entityHandler = new GenderEntityHandler(mainFrame);
		} else if (BookController.CategoryProps.EDIT.check(propName)) {
			entityHandler = new CategoryEntityHandler(mainFrame);
		} else if (BookController.StrandProps.EDIT.check(propName)) {
			entityHandler = new StrandEntityHandler(mainFrame);
		} else if (BookController.IdeaProps.EDIT.check(propName)) {
			entityHandler = new IdeaEntityHandler(mainFrame);
		} else if (BookController.TagProps.EDIT.check(propName)) {
			entityHandler = new TagEntityHandler(mainFrame);
		} else if (BookController.ItemProps.EDIT.check(propName)) {
			entityHandler = new ItemEntityHandler(mainFrame);
		} else if (BookController.TagLinkProps.EDIT.check(propName)) {
			entityHandler = new TagLinkEntityHandler(mainFrame);
		} else if (BookController.ItemLinkProps.EDIT.check(propName)) {
			entityHandler = new ItemLinkEntityHandler(mainFrame);
		} else if (BookController.InternalProps.EDIT.check(propName)) {
			entityHandler = new InternalEntityHandler(mainFrame);
		} else if (BookController.TimeEventProps.EDIT.check(propName)) {
			entityHandler = new TimeEventEntityHandler(mainFrame);
		}
		entity = (AbstractEntity) evt.getNewValue();
		if (entity.isTransient()) {
			entity = entityHandler.newEntity(entity);
		}
		origEntity = entityHandler.createNewEntity();
		EntityUtil.copyEntityProperties(mainFrame, entity, origEntity);
		initUi();
		for (JComponent comp : inputComponents) {
			comp.putClientProperty(ClientPropertyName.ENTITY.toString(), entity);
			comp.putClientProperty(ClientPropertyName.DAO.toString(), entityHandler.createDAO());
		}
		if (entity.isTransient()) {
			btAddOrUpdate.setText(I18N.getMsg("msg.common.add"));
		} else { // update
			btAddOrUpdate.setText(I18N.getMsg("msg.editor.update"));
		}
		editEntity();
		if (!entity.isTransient()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					verifyInput();
				}
			});
		}
	}

	private void showHasErrorWarning() {
		mainFrame.showEditor();
		JOptionPane.showMessageDialog(this,
				I18N.getMsg("msg.common.editor.has.error"),
				I18N.getMsg("msg.common.warning"), JOptionPane.WARNING_MESSAGE);
	}

	private int showConfirmation() {
		mainFrame.showEditor();
		final Object[] options = {I18N.getMsg("msg.common.save.changes"),
			I18N.getMsg("msg.common.discard.changes"),
			I18N.getMsg("msg.common.cancel")};
		int n = JOptionPane.showOptionDialog(
				mainFrame,
				I18N.getMsg("msg.common.save.or.discard.changes") + "\n\n"
				+ EntityUtil.getEntityTitle(entity) + ": "
				+ entity.toString() + "\n\n",
				I18N.getMsg("msg.common.save.changes.title"),
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[2]);
		return n;
	}

	@SuppressWarnings("unchecked")
	private void editEntity() {
		SbApp.trace("EntityEditor.editEntity()");
		for (SbColumn col : entityHandler.getColumns()) {
			try {
				Object ret = "";
				Method method = null;
				if (!col.getMethodName().isEmpty()) {
					String methodName = "get" + col.getMethodName();
					method = entity.getClass().getMethod(methodName);
					ret = method.invoke(entity);
				}
				for (JComponent comp : inputComponents) {
					if (col.getMethodName().equals(comp.getName())) {
						if (comp instanceof JTextComponent) {
							if (ret == null) {
								continue;
							}
							JTextComponent tf = (JTextComponent) comp;
							tf.setText(ret.toString());
							tf.setCaretPosition(0);
						} else if (comp instanceof PlainTextEditor) {
							if (ret == null) {
								continue;
							}
							PlainTextEditor editor = (PlainTextEditor) comp;
							editor.setText(ret.toString());
						} else if (comp instanceof HtmlEditor) {
							if (ret == null) {
								continue;
							}
							HtmlEditor editor = (HtmlEditor) comp;
							editor.setText(ret.toString());
							editor.setCaretPosition(0);
						} else if (comp instanceof JCheckBox) {
							if (ret == null) {
								continue;
							}
							((JCheckBox) comp).setSelected((Boolean) ret);
						} else if (comp instanceof AutoCompleteComboBox) {
							AbstractEntityHandler eHandler = EntityUtil.getEntityHandler(mainFrame, ret, method, entity);
							AutoCompleteComboBox autoCombo = (AutoCompleteComboBox) comp;
							String chosen = "";
							if (ret != null) {
								chosen = ret.toString();
							}
							EntityUtil.fillAutoCombo(mainFrame, autoCombo, eHandler, chosen, col.getAutoCompleteDaoMethod());
						} else if (comp instanceof JComboBox) {
							boolean isNew = (ret == null);
							final JComboBox<Object> combo = (JComboBox<Object>) comp;
							if (col.hasComboModel()) {
								final ComboBoxModel<Object> model = col.getComboModel();
								if (model instanceof IRefreshableComboModel) {
									IRefreshableComboModel refModel = (IRefreshableComboModel) model;
									refModel.setMainFrame(mainFrame);
									refModel.refresh();
								}
								combo.setModel(model);
								combo.revalidate();
								if (ret == null && combo.getItemCount() > 0) {
									combo.setSelectedIndex(0);
								} else {
									model.setSelectedItem(ret);
								}
								if (col.hasListCellRenderer()) {
									combo.setRenderer(col.getListCellRenderer());
								}
							} else {
								AbstractEntityHandler entityHandler2 = EntityUtil.getEntityHandler(mainFrame, ret, method, entity);
								EntityUtil.fillEntityCombo(mainFrame, combo, entityHandler2, (AbstractEntity) ret, isNew, col.isEmptyComboItem());
							}
						} else if (comp instanceof CheckBoxPanel) {
							CheckBoxPanel cbPanel = (CheckBoxPanel) comp;
							AbstractEntityHandler entityHandler2 = EntityUtil.getEntityHandler(mainFrame, ret, method, entity);
							cbPanel.setEntityHandler(entityHandler2);
							cbPanel.setEntity(entity);
							cbPanel.setEntityList((List) ret);
							cbPanel.setSearch(col.getSearch());
							if (col.hasDecorator()) {
								CbPanelDecorator decorator = col.getDecorator();
								decorator.setPanel(cbPanel);
								cbPanel.setDecorator(decorator);
							}
							cbPanel.initAll();
						} else if (comp instanceof DateChooser) {
							DateChooser dateChooser = (DateChooser) comp;
							dateChooser.setDate((Date) ret);
						} else if (comp instanceof CleverColorChooser) {
							if (ret != null) {
								CleverColorChooser colorChooser = (CleverColorChooser) comp;
								colorChooser.setColor((Color) ret);
							}
						} else if (comp instanceof JLabel) {
							if (ret != null) {
								JLabel lb = (JLabel) comp;
								if (col.getInputType() == SbColumn.InputType.ICON) {
									lb.setIcon((Icon) ret);
								} else if (ret instanceof Timestamp) {
									Timestamp stamp = (Timestamp) ret;
									SimpleDateFormat dateFormat = new SimpleDateFormat(I18N.getMsg("msg.common.dateformat"));
									lb.setText(dateFormat.format(stamp));
								} else {
									lb.setText(ret.toString());
								}
							}
						} else if (comp instanceof AttributesPanel) {
							AttributesPanel attrPanel = (AttributesPanel) comp;
							attrPanel.setAttributes(EntityUtil.getEntityAttributes(mainFrame, entity));
							attrPanel.initAll();
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (col.hasRadioButtonGroup()) {
				RadioButtonGroup rbGroup = col.getRadioButtonGroup();
				RadioButtonGroupPanel rbgPanel = rbgPanels.get(rbGroup);
				int key = col.getRadioButtonIndex();
				AbstractButton bt = rbgPanel.getButton(key);
				if (rbGroup.hasAttr(entity, key)) {
					bt.setSelected(true);
					rbgPanel.enableSubPanel(key);
				} else {
					bt.setSelected(false);
					rbgPanel.disableSubPanel(key);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void updateEntityFromInputComponents() {
		SbApp.trace("EntityEditor.updateEntityFromInputComponents()");
		for (SbColumn col : entityHandler.getColumns()) {
			try {
				if (col.isReadOnly()) {
					continue;
				}
				Object objVal = null;
				for (JComponent comp : inputComponents) {
					if (!col.getMethodName().equals(comp.getName())) {
						continue;
					}
					if (comp instanceof JTextComponent) {
						JTextComponent tf = (JTextComponent) comp;
						objVal = tf.getText();
						break;
					}
					if (comp instanceof PlainTextEditor) {
						PlainTextEditor editor = (PlainTextEditor) comp;
						objVal = editor.getText();
						break;
					}
					if (comp instanceof HtmlEditor) {
						HtmlEditor editor = (HtmlEditor) comp;
						objVal = editor.getText();
						break;
					}
					if (comp instanceof JCheckBox) {
						JCheckBox combo = (JCheckBox) comp;
						objVal = combo.isSelected();
						break;
					}
					if (comp instanceof JComboBox) {
						JComboBox combo = (JComboBox) comp;
						objVal = combo.getSelectedItem();
						break;
					}
					if (comp instanceof AutoCompleteComboBox) {
						AutoCompleteComboBox autoCombo = (AutoCompleteComboBox) comp;
						objVal = autoCombo.getJComboBox().getSelectedItem();
						break;
					}
					if (comp instanceof AttributesPanel) {
						AttributesPanel propPanel = (AttributesPanel) comp;
						objVal = propPanel.getAttributes();
						break;
					}
					if (comp instanceof DateChooser) {
						DateChooser dateChooser = (DateChooser) comp;
						objVal = dateChooser.getTimestamp();
						break;
					}
					if (comp instanceof CleverColorChooser) {
						CleverColorChooser colorChooser = (CleverColorChooser) comp;
						if (colorChooser.getColor() != null) {
							objVal = colorChooser.getColor();
						}
						break;
					}
					if (comp instanceof CheckBoxPanel) {
						CheckBoxPanel cbPanel = (CheckBoxPanel) comp;
						objVal = cbPanel.getSelectedEntities();
						break;
					}
					if (comp instanceof JLabel) {
						JLabel lb = (JLabel) comp;
						objVal = lb.getIcon();
						break;
					}
				}
				String methodName = "get" + col.getMethodName();
				Method method = entity.getClass().getMethod(methodName);
				Type type = method.getReturnType();

				Object val = null;
				Class<?>[] types = null;
				if (type == Long.class) {
					val = (Scene) objVal;
					types = new Class[]{Scene.class};
				} else if (type == Integer.class) {
					if (objVal != null) {
						if (objVal.toString().isEmpty()) {
							val = null;
						} else {
							val = Integer.parseInt(objVal.toString());
						}
					}
					types = new Class[]{Integer.class};
				} else if (type == String.class) {
					val = objVal;
					types = new Class[]{String.class};
				} else if (type == Boolean.class) {
					val = objVal;
					types = new Class[]{Boolean.class};
				} else if (type == Person.class) {
					if (objVal instanceof String) {
						if (((String) objVal).length() == 0) {
							val = null;
						}
					} else {
						val = (Person) objVal;
					}
					types = new Class[]{Person.class};

				} else if (type == Location.class) {
					if (objVal instanceof String) {
						if (((String) objVal).length() == 0) {
							val = null;
						}
					} else {
						val = (Location) objVal;
					}
					types = new Class[]{Location.class};
				} else if (type == Scene.class) {
					if (objVal instanceof String) {
						if (((String) objVal).length() == 0) {
							val = null;
						}
					} else {
						val = (Scene) objVal;
					}
					types = new Class[]{Scene.class};
				} else if (type == Chapter.class) {
					if (objVal instanceof String) {
						if (((String) objVal).length() == 0) {
							val = null;
						}
					} else {
						val = (Chapter) objVal;
					}
					types = new Class[]{Chapter.class};
				} else if (type == Part.class) {
					if (objVal instanceof String) {
						if (((String) objVal).length() == 0) {
							val = null;
						}
					} else {
						val = (Part) objVal;
					}
					types = new Class[]{Part.class};
				} else if (type == Gender.class) {
					val = (Gender) objVal;
					types = new Class[]{Gender.class};
				} else if (type == Category.class) {
					if (objVal instanceof String) {
						if (((String) objVal).length() == 0) {
							val = null;
						}
					} else {
						val = (Category) objVal;
					}
					types = new Class[]{Category.class};
				} else if (type == Strand.class) {
					val = (Strand) objVal;
					types = new Class[]{Strand.class};
				} else if (type == Idea.class) {
					val = (Idea) objVal;
					types = new Class[]{Idea.class};
				} else if (type == Tag.class) {
					val = (Tag) objVal;
					types = new Class[]{Tag.class};
				} else if (type == AbstractTag.class) {
					val = (AbstractTag) objVal;
					types = new Class[]{AbstractTag.class};
				} else if (type == Item.class) {
					val = (Item) objVal;
					types = new Class[]{Item.class};
				} else if (type == TagLink.class) {
					val = (TagLink) objVal;
					types = new Class[]{TagLink.class};
				} else if (type == ItemLink.class) {
					val = (ItemLink) objVal;
					types = new Class[]{ItemLink.class};
				} else if (type == TimeEvent.class) {
					val = (TimeEvent) objVal;
					types = new Class[]{TimeEvent.class};
				} else if (type == Date.class) {
					val = (Date) objVal;
					types = new Class[]{Date.class};
				} else if (type == Timestamp.class) {
					val = (Timestamp) objVal;
					types = new Class[]{Timestamp.class};
				} else if (type == Color.class) {
					val = (Color) objVal;
					types = new Class[]{Color.class};
				} else if (type == SceneState.class) {
					val = (SceneState) objVal;
					types = new Class[]{SceneState.class};
				} else if (type == TimeStepState.class) {
					val = (TimeStepState) objVal;
					types = new Class[]{TimeStepState.class};
				} else if (type == IdeaState.class) {
					val = (IdeaState) objVal;
					types = new Class[]{IdeaState.class};
				} else if (type == List.class) {
					val = (List<?>) objVal;
					types = new Class[]{List.class};
				} else if (type == Icon.class) {
					val = (Icon) objVal;
					types = new Class[]{Icon.class};
				}
				if (col.getInputType() != InputType.ATTRIBUTES && col.getInputType() != InputType.NONE) {
					if (!col.getMethodName().isEmpty()) {
						methodName = "set" + col.getMethodName();
						method = entity.getClass().getMethod(methodName, types);
						method.invoke(entity, val);
					}
				}
				if (col.getInputType() == InputType.ATTRIBUTES) {
					if (entity instanceof Person) {
						// save attributes for later usage
						attributes = (ArrayList<Attribute>) val;
					}
				}
			} catch (NumberFormatException | NullPointerException ex1) {
				ex1.printStackTrace();
				// ignore
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		for (Entry<RadioButtonGroup, RadioButtonGroupPanel> e : rbgPanels.entrySet()) {
			RadioButtonGroup rbg = e.getKey();
			RadioButtonGroupPanel panel = e.getValue();
			ArrayList<AbstractButton> buttons = panel.getNotSelectedButtons();
			for (AbstractButton bt : buttons) {
				Integer key = Integer.parseInt(bt.getName());
				rbg.removeAttr(entity, key);
			}
		}
	}

	private void verifyInput() {
		SbApp.trace("EntityEditor.verifyInput()");
		errorState = ErrorState.OK;
		try {
			for (Container container : containers) {
				ArrayList<Component> components = new ArrayList<>();
				SwingUtil.findComponentsNameStartsWith(container, ERROR_LABEL, components);
				for (Component comp : components) {
					Container cont = comp.getParent();
					cont.remove(comp);
				}
			}
			int i = 0;
			for (JComponent comp : inputComponents) {
				AbstractInputVerifier verifier = (AbstractInputVerifier) comp.getInputVerifier();
				if (verifier == null) {
					continue;
				}
				if (!comp.isEnabled()) {
					// don't check disabled components
					continue;
				}
				if (!verifier.verify(comp)) {
					Icon icon = IconUtil.StateIcon.WARNING.getIcon();
					if (errorState == ErrorState.OK) {
						errorState = ErrorState.WARNING;
					}
					if (verifier.getErrorState() == ErrorState.ERROR) {
						errorState = ErrorState.ERROR;
						icon = IconUtil.StateIcon.ERROR.getIcon();
					}
					JLabel lb = new JLabel(icon);
					lb.setToolTipText(verifier.getErrorText());
					lb.setName(ERROR_LABEL + "_" + i);
					String cn = comp.getName();
					Container container = comp.getParent();
					int x = comp.getWidth() - 18;
					int y = 1;
					if (comp instanceof DateChooser) {
						x = comp.getWidth() - 155;
					}
					if (comp instanceof JComboBox) {
						x = comp.getWidth() - 40;
						y = 3;
					}
					if (comp instanceof HtmlEditor) {
						x = comp.getWidth() - 44;
						y = comp.getHeight() - 70;
					}
					if (comp instanceof PlainTextEditor) {
						x = comp.getWidth() - 44;
						y = comp.getHeight() - 70;
					}
					if (x < 0) {
						x = 0;
					}
					if (y < 0) {
						y = 0;
					}
					container.add(lb, "pos (" + cn + ".x+" + x + ") (" + cn + ".y+" + y + ")");
					int zo = container.getComponentZOrder(comp);
					container.setComponentZOrder(lb, zo > 0 ? zo - 1 : 0);
					++i;
				}
			}
			if (errorState != ErrorState.OK) {
				if (errorState == ErrorState.ERROR) {
					setMsgState(MsgState.ERRORS);
				} else {
					setMsgState(MsgState.WARNINGS);
				}
			}

			for (JPanel container : containers) {
				SwingUtil.forceRevalidate(container);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addOrUpdateEntity() {
		SbApp.trace("EntityEditor.addOrUpdateEntity()");
		//TODO don't save if this new entity is calling from Scene entity
		try {
			updateEntityFromInputComponents();
			if (entity.isTransient()) {
				verifyInput();
				if (errorState == ErrorState.ERROR) {
					return;
				}
				ctrl.newEntity(entity);
				if (errorState == ErrorState.OK) {
					setMsgState(MsgState.ADDED);
				}
				titlePanel.refresh(entity);
				btAddOrUpdate.setText(I18N.getMsg("msg.editor.update"));
			} else {
				verifyInput();
				if (errorState == ErrorState.ERROR) {
					return;
				}
				ctrl.updateEntity(entity);
				if (errorState == ErrorState.OK) {
					setMsgState(MsgState.UPDATED);
				}
			}
			if (entity instanceof Person) {
				EntityUtil.setEntityAttributes(mainFrame, entity, attributes);
			}
			if (entity instanceof Scene) {
				Scene scene = (Scene) entity;
				if (BookUtil.isUseLibreOffice(mainFrame)) {
					scene.setOdf(tfFile.getText());
				}
			}
			origEntity = entityHandler.createNewEntity();
			EntityUtil.copyEntityProperties(mainFrame, entity, origEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void abandonEntityChanges() {
		EntityUtil.abandonEntityChanges(mainFrame, entity);
		unloadEntity();
	}

	private void unloadEntity() {
		SbApp.trace("EntityEditor.unloadEntity()");
		entity = null;
		entityHandler = null;
		containers.clear();
		inputComponents.clear();
		rbgPanels.clear();
	}

	public AbstractEntity getEntity() {
		return entity;
	}

	public boolean isEntityLoaded() {
		return entity != null;
	}

	public boolean hasEntityChanged() {
		if (entity == null) {
			return false;
		}
		return (!entity.equals(origEntity));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SbApp.trace("EntityEditor.actionPerformed(evt)");
		String compName = ((Component) e.getSource()).getName();
		if (ComponentName.BT_ODT.check(compName)) {
			openOdtFile(mainFrame, (Scene) entity);
		} else if (ComponentName.BT_OK.check(compName)) {
			addOrUpdateEntity();
			if (errorState == ErrorState.ERROR) {
				return;
			}
			unloadEntity();
			refresh();
			if (!leaveOpen) {
				mainFrame.hideEditor();
			}
			if (isDialog == true) {
				fromDialog.dispose();
			}
		} else if (ComponentName.BT_ADD_OR_UPDATE.check(compName)) {
			addOrUpdateEntity();
			if (errorState == ErrorState.ERROR) {
			}
		} else if (ComponentName.BT_CANCEL.check(compName)) {
			abandonEntityChanges();
			refresh();
			if (!leaveOpen) {
				mainFrame.hideEditor();
			}
			if (isDialog == true) {
				fromDialog.dispose();
			}
		} else if (compName.equals("BtAddStrands")) {
			mainFrame.showEditorAsDialog(new Strand());
			reloadUi(I18N.getMsg("msg.dlg.scene.strand.links"));
		} else if (compName.equals("BtAddPersons")) {
			mainFrame.showEditorAsDialog(new Person());
			reloadUi("Persons");
		} else if (compName.equals("BtAddLocations")) {
			mainFrame.showEditorAsDialog(new Location());
			reloadUi("Locations");
		}
	}

	private void reloadUi(String focus) {
		AbstractEntity saveEntity = entityHandler.createNewEntity();
		EntityUtil.copyEntityProperties(mainFrame, entity, saveEntity);
		int c = tabbedPane.getSelectedIndex();
		//addOrUpdateEntity();
		init();
		initHandler();
		cbLeaveOpen.setVisible(false);
		EntityUtil.copyEntityProperties(mainFrame, saveEntity, entity);
		tabbedPane.setSelectedIndex(c);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		JCheckBox cb = (JCheckBox) e.getSource();
		leaveOpen = cb.isSelected();
		BookUtil.store(mainFrame, BookKey.LEAVE_EDITOR_OPEN, leaveOpen);
	}

	private void openOdtFile(MainFrame mainFrame, Scene scene) {
		String name;
		if ((tfFile.getText() == null) || (tfFile.getText().isEmpty())) {
			name = ODTUtils.getFilePath(mainFrame, scene);
			scene.setOdf(name);
		} else {
			name = tfFile.getText();
		}
		File file = new File(name);
		if (!file.exists()) {
			if (JOptionPane.showConfirmDialog(null,
					I18N.getMsg("msg.libreoffice.filenotexist"),
					I18N.getMsg("msg.libreoffice.lauching"),
					JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				createOdtFile(file);
				scene.setOdf(file.getName());
			}
		}
		try {
			OOUtils.open(file);
		} catch (IOException e) {
			SbApp.error("execLibreOffice(mainFrame,...)", e);
		}
	}

	private void createOdtFile(File file) {
		SbApp.trace("EntityEditor.createOdtFile(" + file.getAbsolutePath() + ")");
		try {
			String source = "storybook/resources/Empty.odt";
			if (BookUtil.isUseSimpleTemplate(mainFrame)) {
				source = "storybook/resources/Simple.odt";
			}
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(source);;
			if (BookUtil.isUsePersonnalTemplate(mainFrame)) {
				Internal internal = BookUtil.get(mainFrame, BookKey.USE_PERSONNAL_TEMPLATE, "");
				source = internal.getStringValue();
				File f=new File(source);
				if (f.exists()) {
					is = new FileInputStream(source);
				}
			}
			Files.copy(is, file.toPath());
		} catch (IOException ex) {
			SbApp.error("EntityEditor.createOdtFile(...)", ex);
		}
	}

	private JPanel panelLibreOffice(Scene scene) {
		JPanel p = new JPanel();
		p.setLayout(new MigLayout("wrap 2", "[][grow]", ""));
		p.setName(I18N.getMsg("msg.libreoffice.parameters"));
		JLabel l = new JLabel(I18N.getMsg("msg.libreoffice.file"));
		p.add(l);
		JLabel lEmpty = new JLabel(" ");
		p.add(lEmpty);
		tfFile = new JTextField(30);
		tfFile.setName("file");
		tfFile.setText(scene.getOdf());
		p.add(tfFile);

		btChooseFile = new JButton();
		btChooseFile.setAction(getChooseFileAction());
		btChooseFile.setText(I18N.getMsg("msg.libreoffice.open"));
		p.add(btChooseFile);
		JButton btResetFile = new JButton();
		btResetFile.setAction(resetFileAction());
		btResetFile.setText(I18N.getMsg("msg.libreoffice.reset"));
		p.add(btResetFile);
		return (p);
	}

	private AbstractAction resetFileAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				tfFile.setText(ODTUtils.getFilePath(mainFrame, (Scene) entity));
			}
		};
	}

	private AbstractAction getChooseFileAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				final JFileChooser fc = new JFileChooser(tfFile.getText());
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int ret = fc.showOpenDialog(mainFrame);
				if (ret != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File dir = fc.getSelectedFile();
				tfFile.setText(dir.getAbsolutePath());
			}
		};
	}

}
