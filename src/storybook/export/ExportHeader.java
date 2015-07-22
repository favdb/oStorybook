/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package storybook.export;

/**
 *
 * @author favdb
 */
public class ExportHeader {
	private String name;
	private int size;
	
	ExportHeader(String n, int s) {
		name=n;
		size=s;
	}
	
	public String getName() {
		return(name);
	}
	
	public int getSize() {
		return(size);
	}
}
