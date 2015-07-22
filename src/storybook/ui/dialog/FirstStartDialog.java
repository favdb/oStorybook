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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import storybook.SbConstants.Language;
import storybook.SbConstants.PreferenceKey;
import storybook.SbConstants.Spelling;
import storybook.toolkit.I18N;
import storybook.toolkit.PrefUtil;
import storybook.toolkit.SpellCheckerUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.toolkit.swing.panel.BackgroundPanel;

import net.miginfocom.swing.MigLayout;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class FirstStartDialog extends AbstractDialog {

	private JComboBox languageCombo;
	private JComboBox spellingCombo;

	public FirstStartDialog() {
		super();
		initAll();
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		super.initUi();
		MigLayout layout = new MigLayout("wrap,fill,ins 0");
		setLayout(layout);
		setTitle(I18N.getMsg("msg.first.start.title"));
		ImageIcon imgIcon = I18N.getImageIcon("icon.options");
		JPanel panel = new BackgroundPanel(imgIcon.getImage(),BackgroundPanel.ACTUAL);
		panel.setLayout(new MigLayout("wrap,fill,ins 10"));
		JLabel lbLogo = new JLabel(I18N.getIcon("icon.logo.250"));
		lbLogo.setOpaque(true);
		lbLogo.setBackground(Color.white);
		JLabel lbText = new JLabel(I18N.getMsg("msg.first.start.text"));
		// language
		JLabel lbLanguage = new JLabel(I18N.getMsgColon("msg.common.language"));
		languageCombo = SwingUtil.createLanguageCombo();
		// spelling
		JLabel lbSpelling = new JLabel(I18N.getMsgColon("msg.pref.spelling"));
		spellingCombo = SwingUtil.createSpellingCombo();
		// layout
		panel.add(lbLogo, "growx,gap bottom 10");
		panel.add(lbText, "gap bottom 10");
		panel.add(lbLanguage);
		panel.add(languageCombo, "gap bottom 10");
		panel.add(lbSpelling);
		panel.add(spellingCombo, "gap bottom 20");
		panel.add(getOkButton(), "pushy 200,al right bottom");
		add(panel);
	}

	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// language
				int i = languageCombo.getSelectedIndex();
				Language lang = Language.values()[i];
				Locale locale = lang.getLocale();
				PrefUtil.set(PreferenceKey.LANG,I18N.getCountryLanguage(locale));
				I18N.initResourceBundles(locale);
				// spell checker
				i = spellingCombo.getSelectedIndex();
				Spelling spelling = Spelling.values()[i];
				PrefUtil.set(PreferenceKey.SPELLING, spelling.name());
				SpellCheckerUtil.registerDictionaries();
				dispose();
			}
		};
	}
}
