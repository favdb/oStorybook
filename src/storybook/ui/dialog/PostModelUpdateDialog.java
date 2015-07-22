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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import storybook.SbConstants.BookKey;
import storybook.model.EntityUtil;
import storybook.toolkit.BookUtil;
import storybook.toolkit.I18N;
import storybook.ui.MainFrame;

import net.miginfocom.swing.MigLayout;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class PostModelUpdateDialog extends AbstractDialog {

	private JCheckBox cbUseHtmlScenes;
	private JCheckBox cbUseHtmlDescr;

	public PostModelUpdateDialog(MainFrame mainFrame) {
		super(mainFrame);
		initAll();
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		super.initUi();

		MigLayout layout = new MigLayout("wrap", "[]", "[]");
		setLayout(layout);
		setTitle(I18N.getMsg("msg.common.question"));

		JLabel lbText = new JLabel(I18N.getMsg("msg.legacy.post.update.text"));

		cbUseHtmlScenes = new JCheckBox();
		cbUseHtmlScenes.setText(I18N.getMsg("msg.document.preference.use.html.scenes"));
		cbUseHtmlScenes.setSelected(true);

		cbUseHtmlDescr = new JCheckBox();
		cbUseHtmlDescr.setText(I18N.getMsg("msg.document.preference.use.html.descr"));
		cbUseHtmlDescr.setSelected(true);

		add(lbText);
		add(cbUseHtmlScenes);
		add(cbUseHtmlDescr);

		add(getOkButton());
	}

	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BookUtil.store(mainFrame,BookKey.USE_HTML_SCENES,cbUseHtmlScenes.isSelected());
				BookUtil.store(mainFrame, BookKey.USE_HTML_DESCR,cbUseHtmlDescr.isSelected());
				mainFrame.setWaitingCursor();
				EntityUtil.convertPlainTextToHtml(mainFrame);
				mainFrame.setDefaultCursor();
				dispose();
				mainFrame.refresh();
			}
		};
	}
}
