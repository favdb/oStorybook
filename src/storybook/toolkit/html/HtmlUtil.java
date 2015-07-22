package storybook.toolkit.html;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLWriter;

import org.jsoup.Jsoup;

import storybook.SbApp;
import storybook.toolkit.TextUtil;
import storybook.toolkit.swing.ColorUtil;

public class HtmlUtil {

	public static String getCleanHtml(String html) {
		StringBuffer buf = new StringBuffer();
		appendCleanHtml(buf, html);
		return buf.toString();
	}

	public static void appendCleanHtml(StringBuffer buf, String html) {
		// remove new lines
		html = html.replace("\n", "");
		// replace empty div tags with paragraphs: "<div>\s*</div>"
		html = html.replaceAll("<div>\\s*</div>", "<p></p>");
		// body>(.*)</body
		Pattern p = Pattern.compile("body>(.*)</body");
		Matcher m = p.matcher(html);
		if (m.find() == true) {
			html = m.group(1);
		}
		html = html.trim();
		// not in any tag at all, so build one
		boolean addDiv = false;
		if (!html.startsWith("<")) {
			addDiv = true;
			buf.append("<div>");
		}
		buf.append(html);
		if (addDiv) {
			buf.append("</div>");
		}
	}

	public static String getContent(HTMLDocument doc) {
		try {
			StringWriter writer = new StringWriter();
			HTMLWriter htmlWriter = new HtmlBodyWriter(writer, doc);
			htmlWriter.write();
			return writer.toString();
		} catch (IOException | BadLocationException e) {
		}
		return "";
	}

	public static String findHref(String html) {
		Pattern pattern = Pattern
				.compile("(((file|http|https)://)|(www\\.))+(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(/[a-zA-Z0-9\\&amp;%_\\./-~-]*)?");
		Matcher matcher = pattern.matcher(html);
		while (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	public static String wrapIntoTable(String html) {
		return wrapIntoTable(html, 200);
	}

	public static String wrapIntoTable(String html, int width) {
		String ret = "<html>"
			+ "<table width='" + width + "'><tr><td>"
			+ html
			+ "<td></tr><table>";
		return ret;
	}

	public static boolean equalsHtml(String html1, String html2) {
		String s1 = Jsoup.parse(html1).text();
		String s2 = Jsoup.parse(html2).text();
		return (s1 == s2 || (s1 != null && s1.equals(s2)));
	}

	public static String htmlToText(String html) {
		return htmlToText(html, false);
	}

	public static String htmlToText(String html, boolean preserveNewLines) {
		html = getCleanHtml(html);
		if (!preserveNewLines) {
			return Jsoup.parse(html).text();
		}
		html = html.replaceAll("<br>", "__BR__");
		html = html.replaceAll("</br>", "__BR_END__");
		html = html.replaceAll("<p>", "__P__");
		html = html.replaceAll("</p>", "__P_END__");
		html = Jsoup.parse(html).text();
		html = html.replaceAll("__BR__", "\n");
		html = html.replaceAll("__BR_END__", "");
		html = html.replaceAll("__P__", "\n");
		html = html.replaceAll("__P_END__", "\n");
		html = html.replaceAll("<br>", "\n");
		return TextUtil.trimText(html);
	}

	public static String textToHTML(String text) {
		if (text == null) {
			return null;
		}
		int length = text.length();
		boolean prevSlashR = false;
		String out = "";
		for (int i = 0; i < length; i++) {
			char ch = text.charAt(i);
			switch (ch) {
			case '\r':
				if (prevSlashR) {
					out+="<br>";
				}
				prevSlashR = true;
				break;
			case '\n':
				prevSlashR = false;
				out+="<br>";
				break;
			case '"':
				if (prevSlashR) {
					out+="<br>";
					prevSlashR = false;
				}
				out+="&quot;";
				break;
			case '<':
				if (prevSlashR) {
					out+="<br>";
					prevSlashR = false;
				}
				out+="&lt;";
				break;
			case '>':
				if (prevSlashR) {
					out+="<br>";
					prevSlashR = false;
				}
				out+="&gt;";
				break;
			case '&':
				if (prevSlashR) {
					out+="<br>";
					prevSlashR = false;
				}
				out+="&amp;";
				break;
			default:
				if (prevSlashR) {
					out+="<br>";
					prevSlashR = false;
				}
				out+=ch;
				break;
			}
		}
		return(out);
	}

	public static String getRow2Cols(StringBuffer text1, String text2) {
		return getRow2Cols(text1.toString(), text2.toString());
	}

	public static String getRow2Cols(StringBuffer text1, StringBuffer text2) {
		return getRow2Cols(text1.toString(), text2.toString());
	}

	public static String getRow2Cols(String text1, StringBuffer text2) {
		return getRow2Cols(text1.toString(), text2.toString());
	}

	public static String getRow2Cols(String text1, String text2) {
		return "<tr><td>" + text1 + "</td><td>" + text2 + "</td></tr>";
	}

	public static String getTitle(String title) {
		String buf = "<div style='padding-top:2px;padding-bottom:2px;"
			+ "padding-left:4px;padding-right:4px;"
			+ "margin-bottom:2px;'><b>" + title + "</b></div>";
		return(buf);
	}

	public static String getColorSpan(Color clr) {
		String htmlClr = (clr == null ? "white" : ColorUtil.getHexName(clr));
		String buf = "<span style='";
		if (clr != null) {
			buf+="background-color:" + htmlClr + ";";
		}
		buf+="'>";
		buf+="&nbsp; &nbsp; &nbsp; &nbsp; </div>";
		return(buf);
	}

	public static String getColoredTitle(Color clr, String title) {
		String htmlClr = (clr == null ? "white" : ColorUtil.getHexName(clr));
		String buf = "<div style='padding-top:2px;padding-bottom:2px;"
			+ "padding-left:4px;padding-right:4px;margin-bottom:2px;";
		if (clr != null) {
			buf+="background-color:"+htmlClr+";";
		}
		if (clr != null && ColorUtil.isDark(clr)) {
			buf+="color:white;";
		}
		buf+="'><b>"+title+"</b></div>";
		return(buf);
	}

	public static String getHeadWithCSS() {
		return getHeadWithCSS(10);
	}

	public static String getHeadWithCSS(int fontSize) {
		String buf = "<head>" + "<style type='text/css'><!--\n";
		// body
		buf+="body {"
			+ "font-family:Arial,sans-serif;"
			+ "font-size:" + fontSize + "px;"
			+ "padding-left:2px;"
			+ "padding-right:2px;"
			+ "}\n";
		//h1
		buf+="h1 {"
			+ "font-family:Arial,sans-serif;"
			+ "font-size:140%;"
			+ "text-align:center;"
			+ "margin-top:15px;"
			+ "margin-bottom:15px;"
			+ "}\n";
		//h2
		buf+="h2 {"
			+ "font-family:Arial,sans-serif;"
			+ "font-size:120%;"
			+ "margin-top:15px;"
			+ "}\n";
		//p
		buf+="p {"
			+ "margin-top:2px;"
			+ "div {"
			+ "padding-left:5px;"
			+ "padding-right:5px;"
			+ "}\n";
		//ul
		buf+="ul {"
			+ "margin-top:2px;"
			+ "margin-left:15px;"
			+ "margin-bottom:2px;"
			+ "}\n";
		// ordered list
		buf+="ol {"
			+ "margin-top:2px;"
			+ "margin-left:15px;"
			+ "margin-bottom:2px;"
			+ "}\n";
		// table
		buf+="table tr {"
			+ "margin:0px;"
			+ "padding:0px;"
			+ "}\n"
			+ "td {"
			+ "margin-right:5px;"
			+ "padding:2px;"
			+ "}\n";
		buf+="--></style>"
			+ "</head>\n";
		return(buf);
	}

	public static String getBold(String str) {
		return "<b>" + str + "</b>";
	}

	public static String getWarning(String str) {
		return "<font color='red'><b>" + str + "</b></font>";
	}

	public static String getHr() {
		return "<hr style='margin:10px' />";
	}

	public static String appendFormatedWarning(String buf, String warning) {
		String str = warning.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
		if (str.isEmpty()) {
			return(buf);
		}
		return("<div style='color:red'>"+str+"</div>");
	}

	public static String appendFormatedDescr(String buf, String descr, boolean shorten) {
		String str = descr.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
		if (str.isEmpty()) {
			return(buf);
		}
		String ret=buf;
		if (shorten) {
			ret+="<div style='width:300px'>"+TextUtil.truncateString(str, 300);
		} else {
			ret+="<div>"+str;
		}
		return(ret+"</div>");
	}

	public static String appendFormatedNotes(String buf, String notes, boolean shorten) {
		String str = notes.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
		if (str.isEmpty()) {
			return(buf);
		}
		String ret="<hr style='margin:5px'/>";
		if (shorten) {
			ret+="<div style='width:300px'>"+TextUtil.truncateString(str, 300);
		} else {
			ret+="<div>"+str;
		}
		return(ret+"</div>");
	}

	public static String appendFormatedMetaInfos(String buf, String metaInfos) {
		String str = metaInfos.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
		if (str.isEmpty()) {
			return(buf);
		}
		return("<hr style='margin:5px'/>"+"<div>"+str+"</div>");
	}
}
