/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package storybook.export;

import java.io.File;
import java.util.List;
import javax.swing.JOptionPane;

import storybook.SbConstants;
import storybook.SbApp;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Idea;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Tag;
import storybook.toolkit.BookUtil;
import storybook.toolkit.I18N;
import static storybook.toolkit.TextUtil.truncateText;
import storybook.toolkit.html.HtmlUtil;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
@SuppressWarnings("deprecation")
public class Export {

	public MainFrame mainFrame;
	public String format;
	public String directory;
	public File file;
	public Object object;
	public int zoom;
	ExportData exportData;
	DlgExport parent;
	String bookTitle;
	String author;

	public Export(DlgExport m, String f, int x) {
		parent = m;
		mainFrame = m.mainFrame;
		format = f;
		zoom = x;
		author = BookUtil.get(mainFrame, SbConstants.BookKey.AUTHOR, "").getStringValue();
		bookTitle = BookUtil.get(mainFrame, SbConstants.BookKey.TITLE, "").getStringValue();
	}

	public MainFrame getMainFrame() {
		return (mainFrame);
	}

	public void setFormat(String f) {
		format = f;
	}

	public void setZoom(int x) {
		zoom = x;
	}

	/**
	 *
	 * @param dir : directory to export
	 * @param xp : the object type to export
	 * @param object : the object to export, null for list all
	 * @param silent
	 * @return if type==html then the export string, else if error the error message
	 */
	public String fill(String dir, ExportData xp, Object object, boolean silent) {
		SbApp.trace("Report.fill(\"" + dir + "\","
				+ xp.getExportName() + ","
				+ (object == null ? "null" : object.getClass().getName()) + ")");
		String ret = "";
		exportData = xp;
		directory = dir;
		file = new File(dir + File.separator + xp.getKey() + "." + format);
		if (format.equals("preview")) {
			format = "html";
		}
		switch (xp.getExportName()) {
			case "book":
				ExportBook exportBook = new ExportBook(this);
				ret = exportBook.get();
				JOptionPane.showMessageDialog(parent,
						I18N.getMsg("msg.common.export.success") + "\n" + xp.getKey(),
						I18N.getMsg("msg.common.export"), JOptionPane.INFORMATION_MESSAGE);
				return (ret);
			case "summary":
				ExportBookSummary exp = new ExportBookSummary(this);
				if (format.equals("csv")) {
					format = "txt";
				}
				ret = exp.get();
				break;
			case "part":
				ExportParts expPart = new ExportParts(this);
				ret = expPart.get((Part) object);
				break;
			case "chapter":
				ExportChapters expChapter = new ExportChapters(this);
				ret = expChapter.get((Chapter) object);
				break;
			case "scene":
				ExportScenes expScene = new ExportScenes(this);
				ret = expScene.get((Scene) object);
				break;
			case "person":
				ExportPersons expPerson = new ExportPersons(this);
				ret = expPerson.get((Person) object);
				break;
			case "location":
				ExportLocations expLocation = new ExportLocations(this);
				ret = expLocation.get((Location) object);
				break;
			case "tag":
				ExportTags expTag = new ExportTags(this);
				ret = expTag.get((Tag) object);
				break;
			case "item":
				ExportItems expItem = new ExportItems(this);
				ret = expItem.get((Item) object);
				break;
			case "idea":
				ExportIdeas expIdea = new ExportIdeas(this);
				ret = expIdea.get((Idea) object);
				break;
		}
		if (!silent) {
			JOptionPane.showMessageDialog(parent,
					I18N.getMsg("msg.common.export.success") + "\n" + dir + File.separator + xp.getKey() + "." + format,
					I18N.getMsg("msg.common.export"), JOptionPane.INFORMATION_MESSAGE);
		}
		return (ret);
	}

	public String getColon(String n, int x) {
		String ret = "";
		switch (format) {
			case "html":
				if (x > 0) {
					ret = "    <td width=\"" + x + "%\">" + ("".equals(n) ? "&nbsp" : n) + "</td>\n";
				} else {
					ret = "    <td>" + n + "</td>\n";
				}
				break;
			case "csv":
				ret = "\"" + n + "\";";
				break;
			case "text":
				ret = "\t" + n + "";
				break;
		}
		return (ret);
	}

	public String getText(String x, boolean verbose) {
		String str = x;
		if (!"html".equals(format)) {
			str = HtmlUtil.htmlToText(x);
		}
		if (!verbose) {
			str = truncateText(str, 25);
		}
		return (str);
	}

	public String ligne(ExportPDF pdf, List<ExportHeader> headers, String[] body) {
		String str = "";
		String tr = "", ftr = "";
		switch (format) {
			case "html":
				tr = "<tr>";
				ftr = "</tr>\n";
				break;
			case "csv":
				tr = "";
				ftr = "\n";
				break;
			case "txt":
				tr = "";
				ftr = "\n";
				break;
			case "pdf":
				pdf.writeRow(body);
				return ("");
		}
		str += tr;
		int index = 0;
		for (String s : body) {
			str += getColon(s, headers.get(index).getSize());
			index++;
		}
		str += ftr;
		return (str);
	}

	public void createHtmlIndex(String dir) {
		exportData = new ExportData("all", "msg.export.all.list");
		ExportHtml html = new ExportHtml(this, "", dir + File.separator + "index.html", (List<ExportHeader>) null, "");
		html.open(true);
		html.writeText("<p>Lists index</p>", false);
		String str = "<ul>";
		for (ExportData rep : parent.exports) {
			if (rep.getExportName().contentEquals("all")) {
				break;
			}
			str += "<li>";
			str += "<a href=\"" + rep.getKey() + ".html\">";
			str += rep.getKey();
			str += "</a></li>";
		}
		str += "</ul>";
		html.writeText(str, false);
		html.close(true);
		JOptionPane.showMessageDialog(parent,
				I18N.getMsg("msg.common.export.success") + "\n",
				I18N.getMsg("msg.common.export"), JOptionPane.INFORMATION_MESSAGE);
	}
}
