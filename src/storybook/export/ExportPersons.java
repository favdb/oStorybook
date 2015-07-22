/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package storybook.export;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.entity.Person;
import storybook.toolkit.I18N;

/**
 *
 * @author favdb
 */
public class ExportPersons {
	private final Export parent;
	private ExportPDF pdf;
	private ExportHtml html;
	private ExportCsv csv;
	private ExportTxt txt;
	private ExportOdf odf;
	private List<ExportHeader> headers;
	
	ExportPersons(Export m) {
		parent=m;
		headers=new ArrayList<>();
		headers.add(new ExportHeader(I18N.getMsg("msg.common.id"),5));
		headers.add(new ExportHeader(I18N.getMsg("msg.dlg.person.abbr"), 5));
		headers.add(new ExportHeader(I18N.getMsg("msg.dlg.person.firstname"), 25));
		headers.add(new ExportHeader(I18N.getMsg("msg.dlg.person.lastname"), 25));
		headers.add(new ExportHeader(I18N.getMsg("msg.dlg.person.color"), 10));
		headers.add(new ExportHeader(I18N.getMsg("msg.dlg.person.occupation"), 30));
	}

	public String get(Person obj) {
		if (obj!=null) return(EntityUtil.getInfo(parent.mainFrame, obj));
		String str = debut(obj);
		BookModel model = parent.mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PersonDAOImpl dao = new PersonDAOImpl(session);
		List<Person> persons = dao.findAll();
		for (Person person : persons) {
			str += ligne(person, true, true);
		}
		model.commit();
		end();
		return (str);
	}

	public String debut(Person obj) {
		String rep="Persons";
		switch(parent.format) {
			case "pdf":
				pdf=new ExportPDF(parent,rep,parent.file.getAbsolutePath(),headers,parent.author);
				pdf.open();
				break;
			case "html":
				html=new ExportHtml(parent,rep,parent.file.getAbsolutePath(),headers,parent.author);
				html.open(true);
				break;
			case "csv":
				csv=new ExportCsv(parent,rep,parent.file.getAbsolutePath(),headers,parent.author);
				csv.open();
				break;
			case "txt":
				txt=new ExportTxt(parent,rep,parent.file.getAbsolutePath(),headers,parent.author);
				txt.open();
				break;
			case "odf":
				odf=new ExportOdf(parent,rep,parent.file.getAbsolutePath(),headers,parent.author);
				odf.open();
				break;
		}
		return ("");
	}

	public String ligne(Person obj, boolean verbose, boolean list) {
		String body[]={
			Long.toString(obj.getId()),
			obj.getAbbreviation(),
			obj.getFirstname(),
			obj.getLastname(),
			obj.getHTMLColor(),
			obj.getOccupation(),
		};
		switch(parent.format) {
			case "pdf":
				pdf.writeRow(body);
				break;
			case "html":
				html.writeRow(body);
				break;
			case "csv":
				csv.writeRow(body);
				break;
			case "txt":
				txt.writeRow(body);
				break;
			case "odf":
				odf.writeRow(body);
				break;
		}
		return("");
	}

	public void end() {
		switch(parent.format) {
			case "html":
				html.close(true);
				break;
			case "pdf":
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
		}
	}

}
