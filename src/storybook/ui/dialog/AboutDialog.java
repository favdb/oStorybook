/*
 Storybook: Scene-based software for novelists and authors.
 Copyright (C) 2008 - 2011 Martin Mustun

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
import java.awt.Container;
import java.awt.Dimension;
import java.util.Properties;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import storybook.SbConstants;
import storybook.toolkit.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;

import net.miginfocom.swing.MigLayout;

/**
 * The about dialog shows the copyright, credits and some internal information that may help to support clients.
 *
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class AboutDialog extends AbstractDialog {

	private final String gpl = "<html><body>" +
		"<p>" + I18N.getMsg("msg.dlg.about.gpl.intro") + "</p>" +
		"<p>" + I18N.getMsg("msg.dlg.about.gpl.copyright") + SbConstants.Storybook.COPYRIGHT_YEAR + "</p>" +
		"<p>" + I18N.getMsg("msg.dlg.about.gpl.homepage") + SbConstants.URL.HOMEPAGE + "</p>" +
		"<p>" + I18N.getMsg("msg.dlg.about.gpl.distribution") + "</p>" +
		"<p>" + I18N.getMsg("msg.dlg.about.gpl.gpl") + "</p>" +
		"<p>" + I18N.getMsg("msg.dlg.about.gpl.license") + "</p>" +
		"</body></html>";

	private final String credits = "<html><body>"
			+ "<h2>Developers</h2>"
			+ "The oStorybook Developer (FaVdB)"
			+ "<h2>Logo Designer</h2>"
			+ "Jose Campoy, mod FaVdB"
			+ "<h2>Translators</h2>"
			+ "<p>Brazilian Portuguese: <i>vacant</i>"
			+ "<br>Czech: <i>vacant</i>"
			+ "<br>Dutch: <i>vacant</i>"
			+ "<br>Danish: <i>vacant</i>"
			+ "<br>English: The Storybook Developer Crew"
			+ "<br>English GB: <b>Ben Dawson</b>"
			+ "<br>English Proof-Reading: <b>Segal Kergaerig, Mark Coolen</b>"
			+ "<br>Finnish: <i>vacant</i>"
			+ "<br>French: <i>vacant</i>"
			+ "<br>German: <b>Frankie Johnson</b>"
			+ "<br>Greek: <i>vacant</i>"
			+ "<br>Hebrew: <i>vacant</i>"
			+ "<br>Italian: <b>Gian Paolo Renello</b>"
			+ "<br>Japanese: <b>Asuka Yuki (飛香宥希/P.N.)</b>"
			+ "<br>Polish: <i>vacant</i>"
			+ "<br>Russian: <i>vacant</i>"
			+ "<br>Simplified Chinese: <i>vacant</i>"
			+ "<br>Spanish: <b>Gustavo Hernandez</b>"
			+ "<br>Swedish: <i>vacant</i>"
			+ "<br>简体字 (Simplified Chinese) :<i>vacant</i>"
			+ "<br>繁體中文 (Traditional Chinese): <i>vacant</i>"
			+ "</p>"
			+ "</body></html>";

	public AboutDialog(MainFrame mainFrame) {
		super(mainFrame);
		initAll();
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		super.initUi();

		MigLayout layout = new MigLayout(
				"flowy",
				"[center]",
				"[]10[]10[]10[]");
		setLayout(layout);
		Container cp = getContentPane();
		cp.setBackground(Color.white);
		setPreferredSize(new Dimension(680, 650));
		// logo
		JLabel lbLogo = new JLabel((ImageIcon) I18N.getIcon("icon.logo.500"));
		lbLogo.setOpaque(true);
		lbLogo.setBackground(Color.WHITE);
		// application info
		JLabel lbInfo = new JLabel();
		JLabel lbReview = new JLabel("");
		StringBuilder buf = new StringBuilder();
		buf.append(SbConstants.Storybook.PRODUCT_NAME);
		buf.append(" - Version ").append(SbConstants.Storybook.PRODUCT_VERSION);
		buf.append(" - Released on ").append(SbConstants.Storybook.PRODUCT_RELEASE_DATE);
		lbInfo.setText(buf.toString());
		JTabbedPane pane = new JTabbedPane();
		// licenses
		pane.addTab("Copyright (GPL)", createGplScrollPane());
		// credits
		pane.addTab("Credits", createCreditsScrollPane());
		// system properties
		pane.addTab("System Properties", createPropertiesScrollPane());
		// layout
		add(lbLogo);
		add(lbInfo);
		add(lbReview);
		add(pane, "grow");
		add(getCloseButton(), "right");
	}

	private JScrollPane createGplScrollPane() {
		JTextPane taGpl = new JTextPane();
		taGpl.setContentType("text/html");
		taGpl.setEditable(false);
		taGpl.setText(gpl);
		taGpl.setCaretPosition(0);
		JScrollPane scroller1 = new JScrollPane(taGpl);
		scroller1.setBorder(SwingUtil.getBorderEtched());
		return (scroller1);
	}

	private JScrollPane createCreditsScrollPane() {
		JTextPane taCredits = new JTextPane();
		taCredits.setContentType("text/html");
		taCredits.setEditable(false);
		taCredits.setText(credits);
		JScrollPane scroller2 = new JScrollPane(taCredits);
		scroller2.setBorder(SwingUtil.getBorderEtched());
		taCredits.setCaretPosition(0);
		return (scroller2);
	}

	private JScrollPane createPropertiesScrollPane() {
		JTextArea ta = new JTextArea();
		ta.setEditable(false);
		ta.setLineWrap(true);
		Properties props = System.getProperties();
		Set<Object> keys = props.keySet();
		for (Object key : keys) {
			ta.append(key.toString());
			ta.append(": ");
			ta.append(props.getProperty(key.toString()));
			ta.append("\n");
		}
		ta.setCaretPosition(0);
		return new JScrollPane(ta);
	}
}
