
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
class ExportTxt {

	String report;
	String fileName = "";
	List<ExportHeader> headers;
	Font fontHeader, fontBody;
	BufferedWriter outStream;
	String author;
	private final Export parent;

	ExportTxt(Export parent, String report, String fileName, List<ExportHeader> headers, String author) {
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
				SbApp.error("ExportTxt.open()", ex);
			}
			if (headers==null) return;
			String str="";
			for (ExportHeader header : headers) {
				str += header.getName() + "\t";
			}
			str+="\n";
			outStream.write(str,0,str.length());
			outStream.flush();
		} catch (IOException ex) {
			SbApp.error("ExportTxt.open()", ex);
		}
	}
	
	public void writeRow(String[] body) {
		try {
			String str="", tab="\t";
			if (!parent.parent.paramExport.txtTab) 
				tab=parent.parent.paramExport.txtSeparator;
			for(String s : body) {
				str += ("".equals(s)?" ":s) + tab;
			}
			str += "\n";
			outStream.write(str,0,str.length());
			outStream.flush();
		} catch (IOException ex) {
			SbApp.error("ExportTxt.writeRow(...)", ex);
		}
	}
	
	void writeText(String str) {
		try {
			outStream.write(str+"\n",0,str.length()+1);
			outStream.flush();
		} catch (IOException ex) {
			SbApp.error("ExportTxt.writeText("+str+")", ex);
		}
	}
	
	public void close() {
		try {
			outStream.flush();
			outStream.close();
		} catch (IOException ex) {
			SbApp.error("ExportTxt.close()", ex);
		}
	}

	void writeScene(String text) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
