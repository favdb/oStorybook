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
package storybook.ui.dialog;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import storybook.SbConstants;
import storybook.SbApp;
import storybook.SbConstants.Language;
import storybook.SbConstants.LookAndFeel;
import storybook.SbConstants.PreferenceKey;
import storybook.SbConstants.Spelling;
import storybook.model.hbn.entity.Preference;
import storybook.toolkit.I18N;
import storybook.toolkit.PrefUtil;
import storybook.toolkit.SpellCheckerUtil;
import storybook.toolkit.net.NetUtil;
import storybook.toolkit.swing.SwingUtil;

import net.miginfocom.swing.MigLayout;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class PreferencesDialog extends AbstractDialog implements
		ActionListener, CaretListener {

	private AbstractAction fontChooserAction;
	private AbstractAction addSpellingAction;

	private JComboBox languageCombo;
	private JComboBox dateFormatCombo;
	private JComboBox spellingCombo;
	private JCheckBox cbLoadFileOnStart;
	private JCheckBox cbConfirmExit;
	private JLabel lbShowFont;
	private Font font;
	private JComboBox lafCombo;
	private JTabbedPane tabbedPane;
	private JTextField tfGoogleMapsUrl;
	private JCheckBox cbTranslatorMode;
	private JCheckBox cbUpdate;

	public PreferencesDialog() {
		super();
		initAll();
	}

	@Override
	public void init() {
		font = SbApp.getInstance().getDefaultFont();
	}

	@Override
	public void initUi() {
		super.initUi();
		setLayout(new MigLayout("wrap,fill", "", "[grow][]"));
		setTitle(I18N.getMsg("msg.dlg.preference.title"));
		setIconImage(I18N.getIconImage("icon.sb"));

		JPanel panel = new JPanel(new MigLayout("flowy,fill"));
		panel.add(createCommonPanel(), "growx");
		panel.add(createAppearancePanel(), "growx");
		panel.add(createInternetPanel(), "growx");
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(I18N.getMsg("msg.dlg.preference.global"), panel);
		tabbedPane.addTab("Translators", createTranslatorsPanel());

		// layout
		add(tabbedPane, "grow");
		add(getOkButton(), "split 2,sg,right");
		add(getCancelButton(), "sg");
	}

	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applySettings();
				canceled = false;
				dispose();
			}
		};
	}

	private void applySettings() {
		SbApp app = SbApp.getInstance();
		SwingUtil.setWaitingCursor(this);

		PrefUtil.set(PreferenceKey.OPEN_LAST_FILE, cbLoadFileOnStart.isSelected());
		PrefUtil.set(PreferenceKey.CONFIRM_EXIT, cbConfirmExit.isSelected());

		// language
		int i = languageCombo.getSelectedIndex();
		Language lang = Language.values()[i];
		Locale locale = lang.getLocale();
		PrefUtil.set(PreferenceKey.LANG, I18N.getCountryLanguage(locale));
		PrefUtil.set(PreferenceKey.DATEFORMAT, dateFormatCombo.getSelectedItem());
		I18N.initResourceBundles(locale);

		// spell checker
		String ix = (String) spellingCombo.getSelectedItem();
		//Spelling spelling = Spelling.values()[i];
		for (Spelling spelling : Spelling.values()) {
			if (spelling.getI18N().equals(ix))
				PrefUtil.set(PreferenceKey.SPELLING, spelling.name());
		}
		SpellCheckerUtil.registerDictionaries();

		// look and feel
		i = lafCombo.getSelectedIndex();
		LookAndFeel laf = LookAndFeel.values()[i];
		SwingUtil.setLookAndFeel(laf);

		// default font
		app.setDefaultFont(font);

		// Google Maps
		PrefUtil.set(PreferenceKey.GOOGLE_MAPS_URL, tfGoogleMapsUrl.getText());
		NetUtil.setGoogleMapUrl(tfGoogleMapsUrl.getText());

		// translator mode
		PrefUtil.set(PreferenceKey.TRANSLATOR_MODE, cbTranslatorMode.isSelected());

		// refresh
		app.refresh();

		SwingUtil.setDefaultCursor(this);
	}

	private JPanel createCommonPanel() {
		MigLayout layout = new MigLayout("wrap 2", "", "[]10");
		JPanel panel = new JPanel(layout);
		panel.setBorder(BorderFactory.createTitledBorder(I18N.getMsg("msg.common")));

		// language
		JLabel lbLanguage = new JLabel(I18N.getMsgColon("msg.common.language"));
		languageCombo = SwingUtil.createLanguageCombo();
		String currentLangStr = I18N.getCountryLanguage(Locale.getDefault());
		Language lang = Language.valueOf(currentLangStr);
		languageCombo.setSelectedIndex(lang.ordinal());
		// date format
		JLabel lbDateFormat = new JLabel(I18N.getMsgColon("msg.common.dateformatlabel"));
		dateFormatCombo = SwingUtil.createDateFormat();
		Preference prefDateFormat = PrefUtil.get(PreferenceKey.DATEFORMAT, "MM-dd-yyyy");

		// spelling
		JLabel lbSpelling = new JLabel(I18N.getMsgColon("msg.pref.spelling"));
		spellingCombo = SwingUtil.createSpellingCombo();
		Preference pref = PrefUtil.get(PreferenceKey.SPELLING, Spelling.none.toString());
		Spelling spelling = Spelling.valueOf(pref.getStringValue());
		spellingCombo.setSelectedItem(pref.getStringValue());
		JLabel lbAddSpelling = new JLabel(" ");
		JButton addSpelling=new JButton();
		addSpelling.setAction(getSpellingAction());
		addSpelling.setText(I18N.getMsgDot("msg.pref.spelling.download"));

		// start options
		JLabel lbStart = new JLabel(I18N.getMsg("msg.pref.start"));
		cbLoadFileOnStart = new JCheckBox(I18N.getMsg("msg.pref.start.openproject"));
		pref = PrefUtil.get(PreferenceKey.OPEN_LAST_FILE, false);
		cbLoadFileOnStart.setSelected(pref.getBooleanValue());

		// confirm exit
		JLabel lbConfirmExit = new JLabel(I18N.getMsg("msg.pref.exit"));
		cbConfirmExit = new JCheckBox(I18N.getMsg("msg.pref.exit.chb"));
		pref = PrefUtil.get(PreferenceKey.CONFIRM_EXIT, true);
		cbConfirmExit.setSelected(pref.getBooleanValue());

		// layout
		panel.add(lbLanguage); panel.add(languageCombo);
		panel.add(lbDateFormat); panel.add(dateFormatCombo);
		panel.add(lbSpelling); panel.add(spellingCombo);
		panel.add(lbAddSpelling); panel.add(addSpelling);
		panel.add(lbStart); panel.add(cbLoadFileOnStart);
		panel.add(lbConfirmExit); panel.add(cbConfirmExit);

		return panel;
	}

	@SuppressWarnings("unchecked")
	private JPanel createAppearancePanel() {
		MigLayout layout = new MigLayout("wrap 2", "", "[]20[][]");
		JPanel panel = new JPanel(layout);
		panel.setBorder(BorderFactory.createTitledBorder(I18N.getMsg("msg.dlg.preference.appearance")));
		// standard font
		JLabel lbFont = new JLabel(I18N.getMsgColon("msg.pref.font.standard"));
		JButton btFont = new JButton();
		btFont.setAction(getFontChooserAction());
		btFont.setText(I18N.getMsg("msg.pref.font.standard.bt"));
		lbShowFont = new JLabel();
		lbShowFont.setText(SwingUtil.getNiceFontName(font));
		JLabel lbCurrentFont = new JLabel(I18N.getMsgColon("msg.pref.font.standard.current"));
		// look and feel
		JLabel lbLaf = new JLabel(I18N.getMsg("msg.pref.laf") + ": ");
		DefaultComboBoxModel lafModel = new DefaultComboBoxModel();
		for (SbConstants.LookAndFeel laf : SbConstants.LookAndFeel.values()) {
			lafModel.addElement(laf.getI18N());
		}
		/*
		UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
		Map map = new TreeMap();
		for (UIManager.LookAndFeelInfo info1 : info) {
			String nomLF = info1.getName();
			String nomClasse = info1.getClassName();
			//map.put(nomLF,nomClasse); 
			lafModel.addElement(nomLF);
		}
		*/
		lafCombo = new JComboBox(lafModel);
		Preference pref = PrefUtil.get(PreferenceKey.LAF, LookAndFeel.cross.name());
		LookAndFeel laf = LookAndFeel.valueOf(pref.getStringValue());
		lafCombo.setSelectedIndex(laf.ordinal());
		// layout
		panel.add(lbCurrentFont);
		panel.add(lbShowFont);
		panel.add(lbFont);
		panel.add(btFont, "gap bottom 16");
		panel.add(lbLaf);
		panel.add(lafCombo);
		return panel;
	}

	private JPanel createInternetPanel() {
		MigLayout layout = new MigLayout("wrap 2", "[][fill,grow]", "");
		JPanel panel = new JPanel(layout);
		panel.setBorder(BorderFactory.createTitledBorder("Internet"));

		// Google Maps URL
		JLabel lbGoogleMapsUrl = new JLabel("Google Maps URL:");
		tfGoogleMapsUrl = new JTextField();
		tfGoogleMapsUrl.setText(NetUtil.getGoogleMapsUrl());

		// layout
		panel.add(lbGoogleMapsUrl);
		panel.add(tfGoogleMapsUrl);

		return panel;
	}

	private JPanel createTranslatorsPanel() {
		MigLayout layout = new MigLayout("wrap 2", "[][fill,grow]", "");
		JPanel panel = new JPanel(layout);

		// translator mode
		JLabel lbEnableTranslatorMode = new JLabel("Translator Mode:");
		cbTranslatorMode = new JCheckBox("Enable Translator Mode");
		Preference pref = PrefUtil.get(PreferenceKey.TRANSLATOR_MODE, false);
		if (pref != null)
			cbTranslatorMode.setSelected(pref.getBooleanValue());

		// layout
		panel.add(lbEnableTranslatorMode);
		panel.add(cbTranslatorMode);

		return panel;
	}

	public AbstractAction getFontChooserAction() {
		if (fontChooserAction == null)
			fontChooserAction = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					Font newFont = FontChooserDialog.showDialog(null, null, font);
					if (newFont == null)
						return;
					lbShowFont.setFont(newFont);
					lbShowFont.setText(SwingUtil.getNiceFontName(newFont));
					font = newFont;
				}
			};
		return fontChooserAction;
	}

	public AbstractAction getSpellingAction() {
		if (addSpellingAction == null)
			addSpellingAction = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					GetNewSpellingDlg newSpelling = new GetNewSpellingDlg();
					newSpelling.setVisible(true);
				}
			};
		return addSpellingAction;
	}
	@Override
	public void actionPerformed(ActionEvent e) {

	}

	@Override
	public void caretUpdate(CaretEvent e) {
	}
}
