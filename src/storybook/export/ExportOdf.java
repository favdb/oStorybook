/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package storybook.export;

import com.itextpdf.text.Font;
import java.io.BufferedWriter;
import java.util.List;

/**
 *
 * @author favdb
 */
class ExportOdf {
	// TODO ExportOdf

	private final Export parent;
	String report;
	String fileName = "";
	List<ExportHeader> headers;
	Font fontHeader, fontBody;
	BufferedWriter outStream;
	String author;

	ExportOdf(Export parent, String report, String fileName, List<ExportHeader> headers,String author) {
		this.parent=parent;	
		this.report=report;
		this.fileName=fileName;
		this.headers=headers;
		this.author=author;
	}
	
	public void open() {
	}
	
	public void writeRow(String data) {
	}
	
	void writeText(String str) {
	}
	
	public void close() {
	}

	void writeRow(String[] body) {
	}

	void writePart(String part) {
		
	}

	void writeChapter(String chapter) {
		
	}

	void writeScene(String scene) {
		
	}

}
