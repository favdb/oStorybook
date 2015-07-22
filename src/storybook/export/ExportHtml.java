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

import com.itextpdf.text.Font;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import storybook.SbApp;
import storybook.toolkit.TextUtil;

/**
 *
 * @author favdb
 */
public class ExportHtml {

	String report;
	String fileName = "";
	List<ExportHeader> headers;
	Font fontHeader, fontBody;
	BufferedWriter outStream;
	String author;
	private final Export parent;
	private final ParamExport param;
	public boolean isOpened=false;

	ExportHtml(Export parent, String report, String fileName, List<ExportHeader> headers, String author) {
		this.parent = parent;
		this.report = report;
		this.fileName = fileName;
		this.headers = headers;
		this.author = author;
		this.param = parent.parent.paramExport;
		this.isOpened=false;
	}

	public void open(boolean isTable) {
		try {
			try {
				outStream = new BufferedWriter(new FileWriter(fileName));
				String str = "<html>" + getHtmlHead();
				if (isTable)
					if (headers != null)
						str += "<body>"
								+ "<h1>" + parent.bookTitle + " - " + parent.exportData.getKey() + "</h1>"
								+ "<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">";
					else
						str += "<body>"
								+ "<h1>" + parent.bookTitle + " - " + parent.exportData.getKey() + "</h1>";
				else
					str += "<body>";
				outStream.write(str, 0, str.length());
				outStream.flush();
				isOpened=true;
			} catch (IOException ex) {
				SbApp.error("ExportHtml.open()", ex);
			}
			if (isTable)
				if (headers != null) {
					String str = "<tr>\n";
					for (ExportHeader header : headers) {
						str += parent.getColon(header.getName(), header.getSize());
					}
					str += "</tr>\n";
					outStream.write(str, 0, str.length());
					outStream.flush();
				}
		} catch (IOException ex) {
			SbApp.error("ExportHtml.open()", ex);
		}
	}

	public void writeRow(String[] body) {
		try {
			String str = "<tr>\n";
			int index = 0;
			for (String s : body) {
				str += "    <td width=\"" + headers.get(index).getSize() + "%\">";
				str += ("".equals(s) ? "&nbsp" : s);
				str += "</td>\n";
				index++;
			}
			str += "</tr>\n";
			outStream.write(str, 0, str.length());
			outStream.flush();
		} catch (IOException ex) {
			SbApp.error("ExportHtml.writeRow()", ex);
		}
	}

	public String getHtmlHead() {
		String buf = "<head>";
		String rep = parent.exportData.getKey();
		buf += "<title>"+ parent.bookTitle +" - "+parent.author+ "</title>\n";
		buf += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n";
		buf += "<style type='text/css'>\n";
		buf += "<!--\n";
		if (param.htmlUseCss)
			try {
				InputStream ips = new FileInputStream(param.htmlCssFile);
				InputStreamReader ipsr = new InputStreamReader(ips);
				BufferedReader br;
				br = new BufferedReader(ipsr);
				String ligne;
				while ((ligne = br.readLine()) != null) {
					buf += ligne + "\n";
				}
				br.close();
			} catch (IOException e) {

			}
		else {
			// body
			buf += "body {"
					+ "font-family:Arial,sans-serif;"
					//+ "font-size:" + parent.zoom + "px;"
					+ "padding-left:2px;"
					+ "padding-right:2px;"
					+ "}\n";
			//h1
			buf += "h1 {"
					+ "font-family:Arial,sans-serif;"
					+ "font-size:140%;"
					+ "text-align:center;"
					+ "margin-top:15px;"
					+ "margin-bottom:15px;"
					+ "}\n";
			//h2
			buf += "h2 {"
					+ "font-family:Arial,sans-serif;"
					+ "font-size:120%;"
					+ "margin-top:15px;"
					+ "}\n";
			//p
			buf += "p {"
					+ "margin-top:2px;"
					+ "div {"
					+ "padding-left:5px;"
					+ "padding-right:5px;"
					+ "}\n";
			//ul
			buf += "ul {"
					+ "margin-top:2px;"
					+ "margin-left:15px;"
					+ "margin-bottom:2px;"
					+ "}\n";
			// ordered list
			buf += "ol {"
					+ "margin-top:2px;"
					+ "margin-left:15px;"
					+ "margin-bottom:2px;"
					+ "}\n";
			// table
			buf += "table tr {"
					+ "margin:0px;"
					+ "padding:0px;"
					+ "}\n"
					+ "td {"
					+ "margin-right:1px;"
					+ "padding:1px;"
					+ "}\n";
		}
		buf += "-->";
		buf += "</style>\n";
		buf += "</head>\n";
		return (buf);
	}

	public void writeText(String str, boolean withParagraph) {
		if ("".equals(str)) return;
		SbApp.trace("ExportHtml.writeText("+TextUtil.truncateString(str, 32)+")");
		try {
			String s = "<p>" + str + "</p>";
			if (!withParagraph)
				s = str;
			outStream.write(s, 0, s.length());
			outStream.flush();
		} catch (IOException ex) {
			SbApp.error("ExportHtml.writeText(" + str + ")", ex);
		}
	}

	public void close(boolean isTable) {
		try {
			String str = "";
			if (isTable)
				if (headers != null)
					str += "</table></body></html>";
				else
					str += "</body></html>";
			else
				str += "</body></html>";
			outStream.write(str, 0, str.length());
			outStream.flush();
			outStream.close();
			isOpened=false;
		} catch (IOException ex) {
			SbApp.error("ExportHtml.close()", ex);
		}
	}

}
