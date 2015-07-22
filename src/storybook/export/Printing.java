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

import java.awt.print.PrinterException;
import java.text.MessageFormat;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import storybook.toolkit.I18N;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class Printing {

	MainFrame mainFrame;
	private boolean background, interactive;
	private String headerField, footerField;
	private javax.swing.JEditorPane guideText;

	public Printing(MainFrame m) {
		mainFrame=m;
		interactive=false;
		background=false;
	}

	public void init(String str) {
		guideText = new javax.swing.JEditorPane();
        guideText.setContentType("text/html");
        guideText.setEditable(false);
        guideText.setOpaque(true);
		guideText.setText(str);
	}

	public void setInteractive(boolean b) {
		interactive=b;
	}

	public void setBackground(boolean b) {
		background=b;
	}

	public void setHeader(String str) {
		headerField=str;
	}

	public void setFooter(String str) {
		footerField=str;
	}

	public void doPrint() {
		MessageFormat header = createFormat(headerField);
		MessageFormat footer = createFormat(footerField);
		PrintingTask task = new PrintingTask(header, footer, interactive);
		if (background) {
			task.execute();
		} else {
			task.run();
		}
	}

	private class PrintingTask extends SwingWorker<Object, Object> {
		private final MessageFormat headerFormat;
		private final MessageFormat footerFormat;
		private final boolean interactive;
		private volatile boolean complete = false;
		private volatile String message;

		public PrintingTask(MessageFormat header, MessageFormat footer, boolean interactive) {
			this.headerFormat = header;
			this.footerFormat = footer;
			this.interactive = interactive;
		}

		@Override
		protected Object doInBackground() {
			try {
				complete = guideText.print(headerFormat, footerFormat, true, null, null, interactive);
				message = I18N.getMsg("msg.printing") + " "
					+ (complete ? I18N.getMsg("msg.printing.complete") : I18N.getMsg("msg.printing.canceled"));
			} catch (PrinterException ex) {
				message = I18N.getMsg("msg.printing.error");
			} catch (SecurityException ex) {
				message = I18N.getMsg("msg.printing.security");
			}
			return null;
		}

		@Override
		protected void done() {
			message(!complete, message);
		}
	}

	private MessageFormat createFormat(String source) {
        if (source != null && source.length() > 0) {
            try {
                return new MessageFormat(source);
            } catch (IllegalArgumentException e) {
                error(I18N.getMsg("msg.printing.formaterror"));
            }
        }
        return null;
    }

    private void message(boolean error, String msg) {
        int type = (error ? JOptionPane.ERROR_MESSAGE :
                            JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(mainFrame, msg, I18N.getMsg("msg.printing"), type);
    }

    private void error(String msg) {
        message(true, msg);
    }

}
