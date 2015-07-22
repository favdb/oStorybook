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

package storybook.ui.dialog.file;

import javax.swing.JCheckBox;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.io.FileUtils;
import storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class NewFileDialog extends AbstractFileDialog {

	private JCheckBox cbUseHtmlScenes;
	private JCheckBox cbUseHtmlDescr;

	public NewFileDialog() {
		super(null);
		setTitle(I18N.getMsg("msg.welcome.new.project"));
		setDir(FileUtils.getUserDirectoryPath());
	}

	@Override
	protected void initOptionsPanel() {
		optionsPanel.setLayout(new MigLayout("wrap"));

		cbUseHtmlScenes = new JCheckBox();
		cbUseHtmlScenes.setText(I18N.getMsg("msg.document.preference.use.html.scenes"));
		cbUseHtmlScenes.setSelected(true);

		cbUseHtmlDescr = new JCheckBox();
		cbUseHtmlDescr.setText(I18N.getMsg("msg.document.preference.use.html.descr"));
		cbUseHtmlDescr.setSelected(true);

		optionsPanel.add(cbUseHtmlScenes);
		optionsPanel.add(cbUseHtmlDescr);
	}

	public boolean getUseHtmlScenes() {
		return cbUseHtmlScenes.isSelected();
	}

	public boolean getUseHtmlDescr() {
		return cbUseHtmlDescr.isSelected();
	}
}
