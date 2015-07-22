/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package storybook.export;

import com.itextpdf.text.*;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import storybook.SbApp;

/**
 *
 * @author favdb
 */
public class ExportPDF {

	private final Export parent;
	private Document outDoc;
	String report;
	String fileName = "";
	List<ExportHeader> headers;
	Font fontHeader, fontBody;
	FileOutputStream fop;
	PdfPTable table;
	String author;
	private PdfWriter writer;

	public ExportPDF(Export parent, String report, String fileName, List<ExportHeader> headers, String author) {
		this.parent = parent;
		this.report = report;
		this.fileName = fileName;
		this.headers = headers;
		this.author = author;
	}

	public void writeRow(String[] strings) {
		SbApp.trace("ExportPDF.writeRow()");
		for (String str : strings) {
			table.addCell(str);
		}
	}

	private void addMetaData() {
		SbApp.trace("ExportPDF.addMetaData()");
		outDoc.addTitle(report);
		outDoc.addSubject("Base list");
		outDoc.addKeywords("oStoryBook");
		outDoc.addAuthor(author);
		outDoc.addCreator(System.getProperty("user.name"));
	}

	public void open() {
		SbApp.trace("ExportPDF.open()");
		outDoc = new Document();
		Rectangle rectangle=new Rectangle(PageSize.getRectangle(parent.parent.paramExport.pdfPageSize));
		if (parent.parent.paramExport.pdfLandscape) {
			rectangle=new Rectangle(PageSize.getRectangle(parent.parent.paramExport.pdfPageSize).rotate());
		}
		outDoc.setPageSize(rectangle);
		try {
			writer=PdfWriter.getInstance(outDoc, new FileOutputStream(fileName));
		} catch (FileNotFoundException | DocumentException ex) {
			SbApp.error(ExportPDF.class.getName(), ex);
		}
		outDoc.open();

		addMetaData();
		try {
			outDoc.add(new Phrase(parent.bookTitle+" - "+parent.exportData.getKey()+"\n"
					, FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD)));
		} catch (DocumentException ex) {
			SbApp.error("ExportPDF.open()", ex);
		}
		if (headers == null)
			return;
		float hsize[] = new float[headers.size()];
		int i = 0;
		for (ExportHeader header : headers) {
			hsize[i] = header.getSize();
			i++;
		}

		table = new PdfPTable(hsize);

		for (ExportHeader header : headers) {
			table.addCell(new Phrase(header.getName(), FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD)));
		}
	}

	void writeText(String str) {
		SbApp.trace("ExportPDF.writeText("+str+")");
		try {
			outDoc.add(new Phrase(str+"\n", FontFactory.getFont(FontFactory.HELVETICA, 10)));
		} catch (DocumentException ex) {
			SbApp.error("ExportPDF.writeText(" + str + ")", ex);
		}
	}

	public void close() {
		SbApp.trace("ExportPDF.close()");
		try {
			if (headers != null)
				outDoc.add(table);
		} catch (DocumentException ex) {
			SbApp.error("ExportPDF.close()", ex);
		}
		outDoc.close();
	}
	
	@SuppressWarnings("deprecation")
	public void HtmlToPdf(String source) {
		StyleSheet styles=null;
		try {
			List<Element> elements = (List<Element>) HTMLWorker.parseToList(new FileReader(source), styles);
			for (Element el : elements) {
				outDoc.add(el);
			}
			File wx=new File(source);
			wx.delete();
		} catch (IOException | DocumentException ex) {
			SbApp.error("ExportPDF.HtmlToPdf("+source+")", ex);
		}
	}

}
