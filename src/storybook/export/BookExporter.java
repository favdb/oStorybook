/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package storybook.export;

import java.awt.HeadlessException;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.DateUtil;
import storybook.toolkit.BookUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.LangUtil;
import storybook.toolkit.html.HtmlSelection;
import storybook.ui.MainFrame;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import storybook.SbApp;

/**
 *
 * @author favdb
 */
public class BookExporter extends AbstractExporter {

	boolean isUseHtmlScenes;
	boolean isExportChapterNumbers;
	boolean isExportRomanNumerals;
	boolean isExportChapterTitles;
	boolean isExportChapterDatLoc;
	boolean isExportSceneTitle;
	boolean isExportSceneSeparator;
	boolean isExportPartTitles;
	boolean tHtml = true;
	boolean isBookHtmlMulti;

	private boolean exportForOpenOffice = false;
	private boolean exportOnlyCurrentPart = false;
	private boolean exportTableOfContentsLink = false;
	private HashSet<Long> strandIdsToExport = null;
	private final String bH1 = "<h1>", eH1 = "</h1>\n\n",
			bH2 = "<h2>", eH2 = "</h2>\n",
			bH3 = "<h3>", eH3 = "</h3>\n",
			bH4 = "<h4>", eH4 = "</h4\n",
			bTx = "<p>", eTx = "</p";

	public BookExporter(MainFrame m) {
		super(m);
		setFileName(m.getDbFile().getName());
		getParam();
		SbApp.trace("BookExporter(" + m.getName() + ")");
	}

	public boolean exportToClipboard() {
		SbApp.trace("BookExporter.exportToClipboard()");
		try {
			StringBuffer str = getContent();
			HtmlSelection html = new HtmlSelection(str.toString());
			Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
			clip.setContents(html, html);
			JOptionPane.showMessageDialog(this.mainFrame,
					I18N.getMsg("msg.book.copy.confirmation"),
					I18N.getMsg("msg.copied.title"), 1);
		} catch (HeadlessException exc) {
			return false;
		}
		return true;
	}

	private void getParam() {
		isUseHtmlScenes = BookUtil.isUseHtmlScenes(mainFrame);
		isExportChapterNumbers = BookUtil.isExportChapterNumbers(mainFrame);
		isExportRomanNumerals = BookUtil.isExportRomanNumerals(mainFrame);
		isExportChapterTitles = BookUtil.isExportChapterTitles(mainFrame);
		isExportChapterDatLoc = BookUtil.isExportChapterDatesLocations(mainFrame);
		isExportSceneTitle = BookUtil.isExportSceneTitle(mainFrame);
		isExportSceneSeparator = BookUtil.isExportSceneSeparator(mainFrame);
		isExportPartTitles = BookUtil.isExportPartTitles(mainFrame);

		tHtml = !((!isUseHtmlScenes) && (exportForOpenOffice == true)); //buf.append(getHtmlHead());
		isBookHtmlMulti = BookUtil.isExportBookHtmlMulti(mainFrame);
	}

	@Override
	public StringBuffer getContent() {
		// warning : getContent ne retourne que le contenu du body en mode HTML
		SbApp.trace("BookExporter.getContent()");
		Part Part1 = mainFrame.getCurrentPart();
		StringBuffer buf = new StringBuffer();
		getParam();
		try {
			BookModel model = mainFrame.getBookModel();
			Session session = model.beginTransaction();
			PartDAOImpl PartDAO = new PartDAOImpl(session);
			ChapterDAOImpl ChapterDAO = new ChapterDAOImpl(session);
			SceneDAOImpl SceneDAO = new SceneDAOImpl(session);
			List<Part> listParts;
			if (exportOnlyCurrentPart) {
				listParts = new ArrayList<>();
				listParts.add(Part1);
			} else {
				listParts = PartDAO.findAll();
			}
			if (tHtml) {// export en HTML
				for (Part part : listParts) {
					buf.append(getPartAsHtml(part));
					List<Chapter> chapters = ChapterDAO.findAll(part);
					for (Chapter chapter : chapters) {
						buf.append(getChapterAsHtml(chapter, ChapterDAO));
						List<Scene> scenes = SceneDAO.findByChapter(chapter);
						for (Scene scene : scenes) {
							buf.append(getSceneAsHtml(scene));
						}
					}
				} // fin export HTML
			} else {// export en TXT
				for (Part part : listParts) {
					buf.append(getPartAsTxt(part));
					List<Chapter> chapters = ChapterDAO.findAll(part);
					for (Chapter chapter : chapters) {
						buf.append(getChapterAsTxt(chapter, ChapterDAO));
						List<Scene> scenes = SceneDAO.findByChapter(chapter);
						for (Scene scene : scenes) {
							buf.append(getSceneAsTxt(scene));
						}
					}
				}
			} // fin export TXT
			model.commit();
		} catch (Exception exc) {
			SbApp.error("BookExport.getContent()", exc);
		}
		SbApp.trace("getContent returns bufsize=" + buf.length());
		return buf;
	}

	public boolean isExportOnlyCurrentPart() {
		return exportOnlyCurrentPart;
	}

	public void setExportOnlyCurrentPart(boolean b) {
		exportOnlyCurrentPart = b;
	}

	public boolean isExportTableOfContentsLink() {
		return exportTableOfContentsLink;
	}

	public void setExportTableOfContentsLink(boolean b) {
		exportTableOfContentsLink = b;
	}

	public HashSet<Long> getStrandIdsToExport() {
		return strandIdsToExport;
	}

	public void setStrandIdsToExport(HashSet<Long> p) {
		strandIdsToExport = p;
	}

	public boolean isExportForOpenOffice() {
		return exportForOpenOffice;
	}

	public void setExportForOpenOffice(boolean b) {
		exportForOpenOffice = b;
	}

	public String getPartAsTxt(Part part) {
		String buf = "";
		if (isExportPartTitles) {
			buf += I18N.getMsg("msg.common.part") + ": " + part.getNumber();
		}
		return (buf);
	}

	@SuppressWarnings("unchecked")
	public String getChapterAsTxt(Chapter chapter, ChapterDAOImpl ChapterDAO) {
		String buf = "";
		buf += chapter.getChapternoStr() + "\n";
		if (isExportChapterNumbers) {
			if (isExportRomanNumerals) {
				buf += (String) LangUtil.intToRoman(chapter.getChapterno());
			} else {
				buf += chapter.getChapternoStr();
			}
		}
		if (isExportChapterTitles) {
			buf += ": " + chapter.getTitle();
		}
		buf += "\n";
		if (isExportChapterDatLoc) {
			buf += DateUtil.getNiceDates((List) ChapterDAO.findDates(chapter));
			if (!((List) ChapterDAO.findLocations(chapter)).isEmpty()) {
				buf += ": " + StringUtils.join((Iterable) ChapterDAO.findLocations(chapter), ", ");
			}
		}
		return (buf);
	}

	public String getSceneAsTxt(Scene scene) {
		String buf = "";
		boolean bx = true;
		if (strandIdsToExport != null) {
			long l = scene.getStrand().getId();
			if (!strandIdsToExport.contains(l)) {
				return ("");
			}
		}
		if (bx) {
			if (!scene.getInformative()) {
				if (isExportSceneTitle) {
					buf += scene.getTitle();
				}
				String str = scene.getText();
				buf += str + "\n";
			}
		}
		return (buf);
	}

	public String getPartAsHtml(Part part) {
		String buf = "";
		if (isExportPartTitles) {
			buf = bH1 + I18N.getMsg("msg.common.part") + ": " + part.getNumber() + eH1;
		}
		return (buf);
	}

	@SuppressWarnings("unchecked")
	public String getChapterAsHtml(Chapter chapter, ChapterDAOImpl ChapterDAO) {
		String buf = "<a name='" + chapter.getChapternoStr() + "'>";
		buf += bH2;
		if (isExportChapterNumbers) {
			if (isExportRomanNumerals) {
				buf += (String) LangUtil.intToRoman(chapter.getChapterno());
			} else {
				buf += Integer.toString(chapter.getChapterno());
			}
			if (isExportChapterTitles) {
				buf += ": " + chapter.getTitle();
			}
		} else {
			if (isExportChapterTitles) {
				buf += chapter.getTitle();
			}
		}
		buf += eH2 + "</a>";
		if (isExportChapterDatLoc) {
			buf += bH3;
			buf += DateUtil.getNiceDates((List) ChapterDAO.findDates(chapter));
			if (!((List) ChapterDAO.findLocations(chapter)).isEmpty()) {
				buf += ": " + StringUtils.join((Iterable) ChapterDAO.findLocations(chapter), ", ");
			}
			buf += eH3;
		}
		return (buf);
	}

	public String getChapterAsHtml(Chapter chapter) { // strict chapter without dates and locations with scenes
		String buf = "<a name='" + chapter.getChapternoStr() + "'>";
		buf += bH2;
		if (isExportChapterNumbers) {
			if (isExportRomanNumerals) {
				buf += (String) LangUtil.intToRoman(chapter.getChapterno());
			} else {
				buf += Integer.toString(chapter.getChapterno());
			}
			if (isExportChapterTitles) {
				buf += ": " + chapter.getTitle();
			}
		} else {
			if (isExportChapterTitles) {
				buf += chapter.getTitle();
			}
		}
		buf += eH2;
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl SceneDAO = new SceneDAOImpl(session);
		List<Scene> scenes = SceneDAO.findByChapter(chapter);
		for (Scene scene : scenes) {
			buf += getSceneAsHtml(scene);
		}
		model.commit();
		return (buf);
	}

	public String getSceneAsHtml(Scene scene) {
		String buf = "";
		if (strandIdsToExport != null) {
			long l = scene.getStrand().getId();
			if (!strandIdsToExport.contains(l)) {
				return ("");
			}
		}
		if (!scene.getInformative()) {
			if (isExportSceneTitle) {
				buf += scene.getTitle();
			}
			buf += scene.getText() + "\n";
			if (isExportSceneSeparator) {
				buf += "<p style=\"text-align:center\">.oOo.</p>";
			}
		}
		if (exportTableOfContentsLink) {
			buf += "<p style='font-size:8px;text-align:left;'><a href='#toc'>"
					+ I18N.getMsg("msg.table.of.contents")
					+ "</a></p>";
		}
		return (buf);
	}
}
