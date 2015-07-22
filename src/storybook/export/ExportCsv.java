/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package storybook.export;

import com.itextpdf.text.Font;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import storybook.SbApp;

/**
 *
 * @author favdb
 */
public class ExportCsv {

	String report;
	String fileName = "";
	List<ExportHeader> headers;
	Font fontHeader, fontBody;
	BufferedWriter outStream;
	String author;
	private final Export parent;

	ExportCsv(Export parent, String report, String fileName, List<ExportHeader> headers,String author) {
		this.parent=parent;	
		this.report=report;
		this.fileName=fileName;
		this.headers=headers;
		this.author=author;
	}
	
	public void open() {
		try {
			try {
				outStream = new BufferedWriter(new FileWriter(fileName));
			} catch (IOException ex) {
				SbApp.error("ExportCsv.open()", ex);
			}
			if (headers==null) return;
			String str="";
			for (ExportHeader header : headers) {
				str += "\"" + header.getName() + "\";";
			}
			str+="\n";
			outStream.write(str,0,str.length());
			outStream.flush();
		} catch (IOException ex) {
			SbApp.error("ExportCsv.open()", ex);
		}
	}
	
	public void writeRow(String[] body) {
		try {
			String str="";
			String quotes="'", comma=";";
			if (parent.parent.paramExport.csvDoubleQuotes) quotes="\"";
			if (parent.parent.paramExport.csvNoQuotes) quotes="";
			if (parent.parent.paramExport.csvComma) quotes=",";
			for(String s : body) {
				str += quotes + ("".equals(s)?" ":s) + quotes + comma;
			}
			str += "\n";
			outStream.write(str,0,str.length());
			outStream.flush();
		} catch (IOException ex) {
			SbApp.error("ExportCsv.writeRow()", ex);
		}
	}
	
	void writeText(String str) {
		try {
			outStream.write(str+"\n",0,str.length()+1);
			outStream.flush();
		} catch (IOException ex) {
			SbApp.error("ExportCsv.writeText("+str+")", ex);
		}
	}
	
	public void close() {
		try {
			outStream.flush();
			outStream.close();
		} catch (IOException ex) {
			SbApp.error("ExportCsv.close()", ex);
		}
	}

}
