/*
 * SbApp: Open Source software for novelists and authors.
 * Original idea 2008 - 2012 Martin Mustun
 * Copyrigth (C) Favdb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package storybook.export;

import java.io.File;
import java.util.List;
import org.hibernate.Session;
import storybook.SbConstants;
import storybook.SbApp;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.BookUtil;

/**
 *
 * @author favdb
 */
public class ExportBook {

	private final Export parent;
	private ExportHtml html;
	private ExportPDF pdf;
	private ExportCsv csv;
	private ExportTxt txt;
	private ExportOdf odf;
	private final BookExporter bookExporter;
	private boolean isMultiHtml;
	private String baseFilename;
	private ExportXml xml;

	public ExportBook(Export m) {
		parent = m;
		bookExporter = new BookExporter(m.mainFrame);
		baseFilename = parent.directory + File.separator;
		baseFilename += BookUtil.get(parent.mainFrame, SbConstants.BookKey.TITLE, "").getStringValue();
		isMultiHtml = parent.parent.paramExport.htmlBookMulti;
	}

	public String get() {
		String rc = "";
		switch (parent.format) {
			case "html":
				html = new ExportHtml(parent, "Book", baseFilename + ".html", null, parent.author);
				break;
			case "csv":
				break; // no export csv Book
			case "txt":
				txt = new ExportTxt(parent, "Book", baseFilename + ".txt", null, parent.author);
				break;
			case "pdf":
				html = new ExportHtml(parent, "Book", baseFilename + "workingFile.html", null, parent.author);
				isMultiHtml = false;
				break;
			case "odf":
				odf = new ExportOdf(parent, "Book", baseFilename + ".odt", null, parent.author);
				break;
			case "xml":
				xml = new ExportXml(parent, "Book", baseFilename + ".xml", null, parent.author);
				break;
		}
		if (!isMultiHtml) {
			rc = debut();
			if (!"".equals(rc)) {
				return (rc);
			}
		}
		BookModel model = parent.mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl PartDAO = new PartDAOImpl(session);
		ChapterDAOImpl ChapterDAO = new ChapterDAOImpl(session);
		SceneDAOImpl SceneDAO = new SceneDAOImpl(session);
		List<Part> listParts = PartDAO.findAll();
		for (Part part : listParts) {
			getPart(part);
			List<Chapter> chapters = ChapterDAO.findAll(part);
			for (Chapter chapter : chapters) {
				getChapter(chapter, ChapterDAO);
				List<Scene> scenes = SceneDAO.findByChapter(chapter);
				for (Scene scene : scenes) {
					getScene(scene);
				}
				if ("xml".equals(parent.format)) {
					xml.endChapter();
				}
			}
			if ("xml".equals(parent.format)) {
				xml.endPart();
			}
		}
		fin();
		if (isMultiHtml) {
			html = new ExportHtml(parent, "Book", baseFilename + " index.html", null, parent.author);
			html.open(false);
			for (Part part : listParts) {
				getTocPart(part);
				List<Chapter> chapters = ChapterDAO.findAll(part);
				for (Chapter chapter : chapters) {
					getTocChapter(chapter, ChapterDAO);
				}
			}
			html.close(false);
		}
		model.commit();
		return (rc);
	}

	public String debut() {
		String rc = "";
		switch (parent.format) {
			case "html":
				html.open(false);
				break;
			case "csv":
				csv.open();
				break;//no header
			case "txt":
				txt.open();
				break;//no header
			case "pdf":
				html.open(false);
				break;
			case "odf":
				odf.open();
				break;
			case "xml":
				xml.open();
				break;
		}
		return (rc);
	}

	private void getPart(Part part) {
		SbApp.trace("getPart(" + part.getName() + ")");
		switch (parent.format) {
			case "html":
				if (!isMultiHtml) {
					html.writeText(bookExporter.getPartAsHtml(part), false);
				}
				break;
			case "csv":
				break; // no csv export for book
			case "txt":
				txt.writeText(bookExporter.getPartAsTxt(part));
				break;
			case "pdf":
				html.writeText(bookExporter.getPartAsHtml(part), false);
				break;
			case "odf":
				odf.writePart(bookExporter.getPartAsTxt(part));
				break;
			case "xml":
				xml.writePart(bookExporter.getPartAsTxt(part));
				break;
		}
	}

	private String getChapterId(Chapter chapter) {
		String spart = Integer.toString(chapter.getPart().getNumber());
		String schapter = Integer.toString(chapter.getChapterno());
		if (spart.length() < 2) {
			spart = "0" + spart;
		}
		if (schapter.length() < 2) {
			schapter = "0" + schapter;
		}
		return (spart + "-" + schapter);
	}

	private void getChapter(Chapter chapter, ChapterDAOImpl ChapterDAO) {
		switch (parent.format) {
			case "html":
				if (isMultiHtml) {
					if (html.isOpened) {
						html.close(false);
					}
					html = new ExportHtml(parent, "Book",
							baseFilename + " " + getChapterId(chapter) + ".html",
							null, parent.author);
					html.open(false);
				}
				html.writeText(bookExporter.getChapterAsHtml(chapter, ChapterDAO), false);
				break;
			case "csv":
				break; // no export csv Book
			case "txt":
				txt.writeText(bookExporter.getChapterAsTxt(chapter, ChapterDAO));
				break;
			case "pdf":
				html.writeText(bookExporter.getChapterAsHtml(chapter, ChapterDAO), false);
				break;
			case "odf":
				odf.writeChapter(bookExporter.getChapterAsTxt(chapter, ChapterDAO));
				break;
			case "xml":
				xml.writeChapter(bookExporter.getChapterAsTxt(chapter, ChapterDAO));
				break;
		}
	}

	private void getScene(Scene scene) {
		switch (parent.format) {
			case "html":
				html.writeText(bookExporter.getSceneAsHtml(scene), false);
				break;
			case "csv":
				break; // no export csv Book
			case "txt":
				txt.writeText(bookExporter.getSceneAsTxt(scene));
				break;
			case "pdf":
				html.writeText(bookExporter.getSceneAsHtml(scene), false);
				break;
			case "odf":
				odf.writeScene(bookExporter.getSceneAsHtml(scene));
				break;
			case "xml":
				xml.writeScene(bookExporter.getSceneAsTxt(scene));
				break;
		}
	}

	private void fin() {
		switch (parent.format) {
			case "html":
				html.close(false);
				break;
			case "pdf":
				html.close(false);
				pdf = new ExportPDF(parent, "Book", baseFilename + ".pdf", null, parent.author);
				pdf.open();
				pdf.HtmlToPdf(baseFilename + "workingFile.html");
				pdf.close();
				break;
			case "csv":
				csv.close();
				break;
			case "txt":
				txt.close();
				break;
			case "odf":
				odf.close();
				break;
			case "xml":
				xml.close();
				break;
		}
	}

	private void getTocPart(Part part) {
		html.writeText("<h2>" + part.getNumberName() + "</h2>", false);
	}

	private void getTocChapter(Chapter chapter, ChapterDAOImpl ChapterDAO) {
		String str = "<a href=\""
				+ baseFilename + " "
				+ getChapterId(chapter) + ".html\">"
				+ chapter.getChapternoStr() + " " + chapter.getTitle()
				+ "</a><br>\n";
		html.writeText(str, false);
	}

}
