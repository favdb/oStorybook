/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package storybook.export;

import storybook.toolkit.I18N;

/**
 *
 * @author favdb
 */
public class ExportData {
	private String reportName;
	private String resourceKey;

	public ExportData(String reportName, String key) {
		this.reportName = reportName;
		resourceKey = I18N.getMsg(key);
	}

	public ExportData() {
	}

	public void setExportName(String reportName) {
		this.reportName = reportName;
	}

	public void setKey(String key) {
		resourceKey = key;
	}

	@Override
	public String toString() {
		return getKey();
	}

	public String getExportName() {
		return reportName;
	}

	public String getKey() {
		return (resourceKey);
	}

		
}
