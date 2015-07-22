/*
Storybook: Open Source software for novelists and authors.
Copyright (C) 2008 - 2012 Martin Mustun, 2015 FaVdB

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package storybook.ui.table;

import java.util.List;
import java.util.Vector;

import storybook.model.hbn.entity.Person;
import storybook.toolkit.I18N;
import storybook.toolkit.comparator.SafeCategoryComparator;
import storybook.toolkit.comparator.SafeChapterComparator;
import storybook.toolkit.comparator.StringIntegerComparator;
import storybook.toolkit.completer.AbbrCompleter;
import storybook.toolkit.swing.verifier.DateVerifier;
import storybook.toolkit.swing.verifier.IntegerVerifier;
import storybook.toolkit.swing.verifier.LengthVerifier;
import storybook.toolkit.swing.verifier.MultipleNumberVerifier;
import storybook.toolkit.swing.verifier.NotEmptyVerifier;
import storybook.toolkit.swing.verifier.VerifierGroup;
import storybook.ui.RadioButtonGroup;
import storybook.ui.combo.IdeaStateComboModel;
import storybook.ui.combo.RelativeSceneComboModel;
import storybook.ui.combo.SceneStateComboModel;
import storybook.ui.combo.SceneStateListCellRenderer;
import storybook.ui.combo.TimeEventFormatComboModel;
import storybook.ui.combo.TimeEventFormatListCellRenderer;
import storybook.ui.edit.ItemCbPanelDecorator;
import storybook.ui.edit.LocationCbPanelDecorator;
import storybook.ui.edit.PersonCbPanelDecorator;
import storybook.ui.edit.StrandCbPanelDecorator;
import storybook.ui.table.SbColumn.InputType;
import storybook.ui.table.renderer.AttributesTableCellRenderer;
import storybook.ui.table.renderer.BooleanTableCellRenderer;
import storybook.ui.table.renderer.CategoryTableCellRenderer;
import storybook.ui.table.renderer.ChapterTableCellRenderer;
import storybook.ui.table.renderer.GenderTableCellRenderer;
import storybook.ui.table.renderer.HtmlTableCellRenderer;
import storybook.ui.table.renderer.IconTableCellRenderer;
import storybook.ui.table.renderer.ItemsTableCellRenderer;
import storybook.ui.table.renderer.LocationsTableCellRenderer;
import storybook.ui.table.renderer.PartTableCellRenderer;
import storybook.ui.table.renderer.PersonTableCellRenderer;
import storybook.ui.table.renderer.PersonsTableCellRenderer;
import storybook.ui.table.renderer.SceneIdTableCellRenderer;
import storybook.ui.table.renderer.SceneStateTableCellRenderer;
import storybook.ui.table.renderer.StrandTableCellRenderer;
import storybook.ui.table.renderer.StrandsTableCellRenderer;
import storybook.ui.table.renderer.TimeEventFormatTableCellRenderer;

import com.googlecode.genericdao.search.Search;


/**
 * @author martin
 *
 */
public class SbColumnFactory {

	private static SbColumnFactory instance;

	private SbColumnFactory() {
	}

	public static SbColumnFactory getInstance() {
		if (instance == null) {
			instance = new SbColumnFactory();
		}
		return instance;
	}

	public Vector<SbColumn> getChapterColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "Part", InputType.COMBOBOX, "msg.common.part");
		col.setTableCellRenderer(new PartTableCellRenderer());
		col.setWidth(60);
		columns.add(col);

		col = new SbColumn(i++, "Chapterno", "msg.dlg.chapter.number");
		col.setMaxLength(5);
		VerifierGroup group = new VerifierGroup();
		group.addVerifier(new IntegerVerifier(true));
		group.addVerifier(new MultipleNumberVerifier());
		col.setVerifier(group);
		col.setDefaultSort(true);
		columns.add(col);

		col = new SbColumn(i++, "Title", "msg.dlg.chapter.title");
		col.setMaxLength(255);
		col.setGrowX(true);
		VerifierGroup group2 = new VerifierGroup();
		group2.addVerifier(new NotEmptyVerifier());
		group2.addVerifier(new LengthVerifier(col.getMaxLength()));
		col.setVerifier(group2);
		columns.add(col);

		col = new SbColumn(i++, "", InputType.SEPARATOR, "");
		columns.add(col);

		col = new SbColumn(i++, "CreationTime", InputType.DATE, "msg.dlg.mng.date.creation");
		col.setMaxLength(255);
		col.setGrowX(true);
		columns.add(col);

		col = new SbColumn(i++, "ObjectiveTime", InputType.DATE, "msg.dlg.mng.date.objective");
		col.setMaxLength(255);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		columns.add(col);

		col = new SbColumn(i++, "ObjectiveChars", "msg.dlg.mng.size.objective");
		col.setMaxChars(10);
		col.setVerifier(new IntegerVerifier(false, true));
		col.setComparator(new StringIntegerComparator());
		col.setGrowX(false);
		columns.add(col);

		col = new SbColumn(i++, "", InputType.SEPARATOR, "");
		columns.add(col);

		col = new SbColumn(i++, "Description", InputType.TEXTAREA, "msg.dlg.chapter.description");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		columns.add(col);

		col = new SbColumn(i++, "Notes", InputType.TEXTAREA, "msg.common.notes");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setShowInSeparateTab(true);
		col.setHideOnStart(true);
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		columns.add(col);

		return columns;
	}

	public Vector<SbColumn> getPartColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "Number", "msg.dlg.mng.parts.number");
		col.setMaxLength(5);
		VerifierGroup group = new VerifierGroup();
		group.addVerifier(new IntegerVerifier(true));
		group.addVerifier(new MultipleNumberVerifier());
		col.setVerifier(group);
		col.setDefaultSort(true);
		columns.add(col);

		col = new SbColumn(i++, "Name", "msg.dlg.mng.parts.name");
		col.setMaxLength(255);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		col.setVerifier(new NotEmptyVerifier());
		columns.add(col);

		col = new SbColumn(i++, "Superpart", "msg.dlg.mng.parts.superpart");
		col.setMaxLength(255);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		col.setAutoComplete(true);
		col.setAutoCompleteDaoMethod("findAll");
		columns.add(col);

		col = new SbColumn(i++, "", InputType.SEPARATOR, "");
		columns.add(col);

		col = new SbColumn(i++, "CreationTime", InputType.DATE, "msg.dlg.mng.date.creation");
		col.setMaxLength(255);
		col.setGrowX(true);
		columns.add(col);

		col = new SbColumn(i++, "ObjectiveTime", InputType.DATE, "msg.dlg.mng.date.objective");
		col.setMaxLength(255);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		columns.add(col);

		col = new SbColumn(i++, "DoneTime", InputType.DATE, "msg.dlg.mng.date.done");
		col.setMaxLength(255);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		columns.add(col);

		col = new SbColumn(i++, "ObjectiveChars", "msg.dlg.mng.size.objective");
		col.setMaxChars(10);
		col.setVerifier(new IntegerVerifier(false, true));
		col.setComparator(new StringIntegerComparator());
		col.setGrowX(false);
		columns.add(col);

		col = new SbColumn(i++, "", InputType.SEPARATOR, "");
		columns.add(col);

		col = new SbColumn(i++, "Notes", InputType.TEXTAREA, "msg.common.notes");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setShowInSeparateTab(true);
		col.setHideOnStart(true);
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		columns.add(col);
		
		return columns;
	}

	public Vector<SbColumn> getLocationColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "Name", "msg.dlg.location.name");
		col.setMaxLength(255);
		col.setGrowX(true);
		VerifierGroup group = new VerifierGroup();
		group.addVerifier(new NotEmptyVerifier());
		group.addVerifier(new LengthVerifier(col.getMaxLength()));
		col.setVerifier(group);
		col.setDefaultSort(true);
		columns.add(col);
		
		col = new SbColumn(i++, "Address", "msg.dlg.location.address");
		col.setMaxLength(255);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		col.setHideOnStart(true);
		columns.add(col);

		col = new SbColumn(i++, "City", "msg.dlg.location.city");
		col.setMaxLength(255);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		col.setAutoComplete(true);
		col.setAutoCompleteDaoMethod("findCities");
		columns.add(col);

		col = new SbColumn(i++, "Country", "msg.dlg.location.country");
		col.setMaxLength(255);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		col.setAutoComplete(true);
		col.setAutoCompleteDaoMethod("findCountries");
		columns.add(col);

		col = new SbColumn(i++, "Site", "msg.dlg.location.site");
		col.setMaxLength(255);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		col.setAutoComplete(true);
		col.setAutoCompleteDaoMethod("findAll");
		columns.add(col);

		col = new SbColumn(i++, "Altitude", "msg.dlg.location.altitude");
		col.setVerifier(new IntegerVerifier(false, true));
		col.setComparator(new StringIntegerComparator());
		col.setHideOnStart(true);
		columns.add(col);

		col = new SbColumn(i++, "Description", InputType.TEXTAREA, "msg.dlg.location.description");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		columns.add(col);

		col = new SbColumn(i++, "Notes", InputType.TEXTAREA, "msg.common.notes");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setShowInSeparateTab(true);
		col.setHideOnStart(true);
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		columns.add(col);

		return columns;
	}

	public Vector<SbColumn> getPersonColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "Firstname", "msg.dlg.person.firstname");
		col.setMaxLength(255);
		col.setGrowX(true);
		VerifierGroup group = new VerifierGroup();
		group.addVerifier(new NotEmptyVerifier());
		group.addVerifier(new LengthVerifier(col.getMaxLength()));
		col.setVerifier(group);
		col.setDefaultSort(true);
		columns.add(col);

		col = new SbColumn(i++, "Lastname", "msg.dlg.person.lastname");
		col.setMaxLength(255);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		columns.add(col);

		col = new SbColumn(i++, "Abbreviation", "msg.dlg.person.abbr");
		col.setMaxLength(255);
		VerifierGroup group2 = new VerifierGroup();
		group2.addVerifier(new NotEmptyVerifier());
		group2.addVerifier(new LengthVerifier(col.getMaxLength()));
		col.setVerifier(group2);
		AbbrCompleter abbrCompleter = new AbbrCompleter("Firstname", "Lastname");
		col.setCompleter(abbrCompleter);
		columns.add(col);

		col = new SbColumn(i++, "Gender", InputType.COMBOBOX, "msg.dlg.mng.persons.gender");
		col.setTableCellRenderer(new GenderTableCellRenderer());
		columns.add(col);

		col = new SbColumn(i++, "Category", InputType.COMBOBOX, "msg.dlg.mng.persons.category");
		col.setTableCellRenderer(new CategoryTableCellRenderer());
		col.setComparator(new SafeCategoryComparator());
		columns.add(col);

		col = new SbColumn(i++, "Birthday", InputType.DATE, "msg.dlg.mng.persons.birthday");
		col.setVerifier(new DateVerifier(true));
		columns.add(col);

		col = new SbColumn(i++, "Dayofdeath", InputType.DATE, "msg.dlg.person.death");
		col.setVerifier(new DateVerifier(true));
		col.setHideOnStart(true);
		columns.add(col);

		col = new SbColumn(i++, "Occupation", "msg.dlg.person.occupation");
		col.setGrowX(true);
		col.setHideOnStart(true);
		columns.add(col);

		col = new SbColumn(i++, "JColor", InputType.COLOR, "msg.dlg.mng.strands.color");
		columns.add(col);

		col = new SbColumn(i++, "Attributes", InputType.ATTRIBUTES, "msg.common.attributes");
		col.setShowInSeparateTab(true);
		col.setHideOnStart(true);
		col.setTableCellRenderer(new AttributesTableCellRenderer());
		columns.add(col);

		col = new SbColumn(i++, "Description", InputType.TEXTAREA, "msg.dlg.person.descr");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setShowInSeparateTab(true);
		col.setHideOnStart(true);
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		columns.add(col);

		col = new SbColumn(i++, "Notes", InputType.TEXTAREA, "msg.common.notes");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setShowInSeparateTab(true);
		col.setHideOnStart(true);
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		columns.add(col);

		return columns;
	}

	public Vector<SbColumn> getRelationshipColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "Person1", InputType.COMBOBOX, "msg.common.person");
		col.setEmptyComboItem(true);
		col.setTableCellRenderer(new PersonTableCellRenderer());
		columns.add(col);

		col = new SbColumn(i++, "Description", "msg.dlg.relationship.description");
		col.setMaxLength(255);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		columns.add(col);

		col = new SbColumn(i++, "Person2", InputType.COMBOBOX, "msg.common.person");
		col.setEmptyComboItem(true);
		col.setTableCellRenderer(new PersonTableCellRenderer());
		columns.add(col);
		
		col = new SbColumn(i++, "StartScene", InputType.COMBOBOX, "msg.tag.start.scene");
		col.setEmptyComboItem(true);
		columns.add(col);

		col = new SbColumn(i++, "EndScene", InputType.COMBOBOX, "msg.tag.end.scene");
		col.setEmptyComboItem(true);
		columns.add(col);

		col = new SbColumn(i++, "Notes", InputType.TEXTAREA, "msg.common.notes");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setShowInSeparateTab(true);
		col.setHideOnStart(true);
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		columns.add(col);

		return columns;
	}

	public Vector<SbColumn> getGenderColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "Icon", InputType.ICON, "msg.common.icon");
//		col.setReadOnly(true);
		col.setTableCellRenderer(new IconTableCellRenderer());
		columns.add(col);

		col = new SbColumn(i++, "Name", "msg.dlg.mng.persons.gender");
		col.setMaxLength(255);
		col.setGrowX(true);
		VerifierGroup group = new VerifierGroup();
		group.addVerifier(new NotEmptyVerifier());
		group.addVerifier(new LengthVerifier(col.getMaxLength()));
		col.setVerifier(group);
		col.setDefaultSort(true);
		columns.add(col);

		col = new SbColumn(i++, "Childhood", "msg.chart.gantt.childhood");
		col.setVerifier(new IntegerVerifier(true));
		columns.add(col);

		col = new SbColumn(i++, "Adolescence", "msg.chart.gantt.adolescence");
		col.setVerifier(new IntegerVerifier(true));
		columns.add(col);

		col = new SbColumn(i++, "Adulthood", "msg.chart.gantt.adulthood");
		col.setVerifier(new IntegerVerifier(true));
		columns.add(col);

		col = new SbColumn(i++, "Retirement", "msg.chart.gantt.retirement");
		col.setVerifier(new IntegerVerifier(true));
		columns.add(col);

		return columns;
	}

	public Vector<SbColumn> getCategoryColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "Name", "msg.person.category.name");
		col.setMaxLength(255);
		col.setGrowX(true);
		VerifierGroup group = new VerifierGroup();
		group.addVerifier(new NotEmptyVerifier());
		group.addVerifier(new LengthVerifier(col.getMaxLength()));
		col.setVerifier(group);
		columns.add(col);

		col = new SbColumn(i++, "Sort", "msg.person.category.order");
		VerifierGroup group2 = new VerifierGroup();
		group2.addVerifier(new IntegerVerifier(true));
		group2.addVerifier(new MultipleNumberVerifier());
		col.setVerifier(group2);
		col.setDefaultSort(true);
		columns.add(col);

		col = new SbColumn(i++, "Sup", "msg.person.category.category");
		col.setMaxLength(255);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		col.setAutoComplete(true);
		col.setAutoCompleteDaoMethod("findAllOrderBySort");
		columns.add(col);
		return columns;
	}

	public Vector<SbColumn> getAttributesColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "Name", "msg.attribute.key");
		col.setMaxLength(255);
		col.setGrowX(true);
		VerifierGroup group = new VerifierGroup();
		group.addVerifier(new NotEmptyVerifier());
		group.addVerifier(new LengthVerifier(col.getMaxLength()));
		col.setVerifier(group);
		columns.add(col);

		col = new SbColumn(i++, "Value", "msg.attribute.value");
		col.setMaxLength(2048);
		col.setGrowX(true);
		VerifierGroup group2 = new VerifierGroup();
		group2.addVerifier(new LengthVerifier(col.getMaxLength()));
		col.setVerifier(group2);
		columns.add(col);

		return columns;
	}

	public Vector<SbColumn> getStrandColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "Name", "msg.dlg.mng.strands.name");
		col.setMaxLength(255);
		col.setGrowX(true);
		VerifierGroup group = new VerifierGroup();
		group.addVerifier(new NotEmptyVerifier());
		group.addVerifier(new LengthVerifier(col.getMaxLength()));
		col.setVerifier(group);
		columns.add(col);

		col = new SbColumn(i++, "Abbreviation", "msg.dlg.strand.abbr");
		col.setMaxLength(255);
		VerifierGroup group2 = new VerifierGroup();
		group2.addVerifier(new NotEmptyVerifier());
		group2.addVerifier(new LengthVerifier(col.getMaxLength()));
		col.setVerifier(group2);
		columns.add(col);

		col = new SbColumn(i++, "JColor", InputType.COLOR, "msg.dlg.mng.strands.color");
		col.setAllowNoColor(false);
		columns.add(col);

		col = new SbColumn(i++, "Sort", "msg.order");
		VerifierGroup group3 = new VerifierGroup();
		group3.addVerifier(new IntegerVerifier(true));
		group3.addVerifier(new MultipleNumberVerifier());
		col.setVerifier(group3);
		col.setDefaultSort(true);
		columns.add(col);

		col = new SbColumn(i++, "Notes", InputType.TEXTAREA, "msg.common.notes");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setHideOnStart(true);
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		columns.add(col);

		return columns;
	}

	public Vector<SbColumn> getIdeaColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "IdeaState", InputType.COMBOBOX, "msg.idea.table.status");
		col.setComboModel(new IdeaStateComboModel());
		col.setDefaultSort(true);
		columns.add(col);

		col = new SbColumn(i++, "Category", "msg.idea.table.category");
		col.setMaxLength(255);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		col.setAutoComplete(true);
		col.setAutoCompleteDaoMethod("findCategories");
		columns.add(col);

		col = new SbColumn(i++, "Notes", InputType.TEXTAREA, "msg.common.notes");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		columns.add(col);

		return columns;
	}

	public Vector<SbColumn> getTagColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "Name", "msg.item.name");
		col.setMaxLength(255);
		VerifierGroup group = new VerifierGroup();
		group.addVerifier(new NotEmptyVerifier());
		group.addVerifier(new LengthVerifier(col.getMaxLength()));
		col.setVerifier(group);
		col.setGrowX(true);
		col.setDefaultSort(true);
		columns.add(col);

		col = new SbColumn(i++, "Category", "msg.item.category");
		col.setMaxLength(255);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		col.setAutoComplete(true);
		col.setAutoCompleteDaoMethod("findCategories");
		columns.add(col);

		col = new SbColumn(i++, "Description", InputType.TEXTAREA, "msg.dlg.location.description");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		columns.add(col);

		col = new SbColumn(i++, "Notes", InputType.TEXTAREA, "msg.common.notes");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setHideOnStart(true);
		col.setShowInSeparateTab(true);
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		columns.add(col);

		return columns;
	}

	public Vector<SbColumn> getItemColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "Name", "msg.item.name");
		col.setMaxLength(255);
		VerifierGroup group = new VerifierGroup();
		group.addVerifier(new NotEmptyVerifier());
		group.addVerifier(new LengthVerifier(col.getMaxLength()));
		col.setVerifier(group);
		col.setGrowX(true);
		col.setDefaultSort(true);
		columns.add(col);

		col = new SbColumn(i++, "Category", "msg.item.category");
		col.setMaxLength(255);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		col.setAutoComplete(true);
		col.setAutoCompleteDaoMethod("findCategories");
		columns.add(col);

		col = new SbColumn(i++, "Description", InputType.TEXTAREA, "msg.dlg.location.description");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		columns.add(col);

		col = new SbColumn(i++, "Notes", InputType.TEXTAREA, "msg.common.notes");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setHideOnStart(true);
		col.setShowInSeparateTab(true);
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		columns.add(col);

		return columns;
	}

	public Vector<SbColumn> getTagLinkColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "Tag", InputType.COMBOBOX, "msg.tag");
		col.setDefaultSort(true);
		columns.add(col);

		col = new SbColumn(i++, "Person", InputType.COMBOBOX, "msg.common.person");
		col.setEmptyComboItem(true);
		col.setTableCellRenderer(new PersonTableCellRenderer());
		columns.add(col);

		col = new SbColumn(i++, "Location", InputType.COMBOBOX, "msg.common.location");
		col.setEmptyComboItem(true);
		columns.add(col);

		col = new SbColumn(i++, "StartScene", InputType.COMBOBOX, "msg.tag.start.scene");
		col.setEmptyComboItem(true);
		columns.add(col);

		col = new SbColumn(i++, "EndScene", InputType.COMBOBOX, "msg.tag.end.scene");
		col.setEmptyComboItem(true);
		columns.add(col);

		return columns;
	}

	public Vector<SbColumn> getItemLinkColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "Item", InputType.COMBOBOX, "msg.item");
		col.setDefaultSort(true);
		columns.add(col);

		col = new SbColumn(i++, "Person", InputType.COMBOBOX, "msg.common.person");
		col.setEmptyComboItem(true);
		col.setTableCellRenderer(new PersonTableCellRenderer());
		columns.add(col);

		col = new SbColumn(i++, "Location", InputType.COMBOBOX, "msg.common.location");
		col.setEmptyComboItem(true);
		columns.add(col);

		col = new SbColumn(i++, "StartScene", InputType.COMBOBOX, "msg.tag.start.scene");
		col.setEmptyComboItem(true);
		columns.add(col);

		col = new SbColumn(i++, "EndScene", InputType.COMBOBOX, "msg.tag.end.scene");
		col.setEmptyComboItem(true);
		columns.add(col);

		return columns;
	}

	public Vector<SbColumn> getSceneColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "Title", "msg.dlg.chapter.title");
		col.setMaxLength(2048);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		columns.add(col);

		col = new SbColumn(i++, "Strand", InputType.COMBOBOX, "msg.dlg.scene.strand");
		col.setTableCellRenderer(new StrandTableCellRenderer());
		columns.add(col);

		col = new SbColumn(i++, "Chapter", InputType.COMBOBOX, "msg.dlg.scene.chapter");
		col.setEmptyComboItem(true);
		col.setTableCellRenderer(new ChapterTableCellRenderer());
		col.setComparator(new SafeChapterComparator());
		col.setDefaultSort(true);
		columns.add(col);

		col = new SbColumn(i++, "Sceneno", "msg.dlg.scene.scene.no");
		col.setMaxLength(5);
		col.setVerifier(new IntegerVerifier(true, true));
		col.setEmptyComboItem(true);
		col.setHideOnStart(true);
		columns.add(col);

		col = new SbColumn(i++, "SceneState", InputType.COMBOBOX, "msg.status");
		col.setComboModel(new SceneStateComboModel(false));
		col.setListCellRenderer(new SceneStateListCellRenderer());
		col.setTableCellRenderer(new SceneStateTableCellRenderer());
		columns.add(col);

		col = new SbColumn(i++, "Informative", InputType.CHECKBOX, "msg.common.informative");
		col.setTableCellRenderer(new BooleanTableCellRenderer());
		col.setHideOnStart(true);
		columns.add(col);

		RadioButtonGroup rbg = new RadioButtonGroup();
		rbg.add(1, "noSceneTs", I18N.getMsg("msg.scenedialog.nodate"));
		rbg.add(2, "sceneTs", I18N.getMsg("msg.scenedialog.fixeddate"));
		rbg.add(3, "relativeScene", I18N.getMsg("msg.scenedialog.relativedate"));

		col = new SbColumn(i++, "NoSceneTs", InputType.NONE, "msg.scenedialog.nodate");
		col.setRadioButtonGroup(rbg);
		col.setRadioButtonIndex(1);
		col.setHideOnStart(true);
		columns.add(col);

		col = new SbColumn(i++, "SceneTs", InputType.DATE, "msg.dlg.scene.date");
		col.setVerifier(new DateVerifier(false));
		col.setShowDateTime(true);
		col.setRadioButtonGroup(rbg);
		col.setRadioButtonIndex(2);
		columns.add(col);

		col = new SbColumn(i++, "RelativeDateDifference", "msg.scenedialog.relativedate.occurs");
		col.setVerifier(new IntegerVerifier(false));
		col.setRadioButtonGroup(rbg);
		col.setRadioButtonIndex(3);
		col.setHideOnStart(true);
		columns.add(col);

		col = new SbColumn(i++, "RelativeSceneId", InputType.COMBOBOX, "msg.scenedialog.relativedate.after");
		col.setTableCellRenderer(new SceneIdTableCellRenderer());
		col.setComboModel(new RelativeSceneComboModel());
		col.setRadioButtonGroup(rbg);
		col.setRadioButtonIndex(3);
		col.setHideOnStart(true);
		columns.add(col);

		col = new SbColumn(i++, "Strands", InputType.LIST, "msg.dlg.scene.strand.links");
		col.setTableCellRenderer(new StrandsTableCellRenderer());
		col.setCbDecorator(new StrandCbPanelDecorator());
		col.setHideOnStart(true);
		columns.add(col);

		col = new SbColumn(i++, "Summary", InputType.TEXTAREA, "msg.dlg.scene.summary");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setShowInSeparateTab(true);
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		col.setHideOnStart(true);
		columns.add(col);

		col = new SbColumn(i++, "Persons", InputType.LIST, "msg.dlg.scene.person.links");
		col.setShowInSeparateTab(true);
		Search search = new Search(Person.class);
		search.addSort("category.sort", false);
		col.setSearch(search);
		col.setTableCellRenderer(new PersonsTableCellRenderer());
		col.setCbDecorator(new PersonCbPanelDecorator());
		columns.add(col);

		col = new SbColumn(i++, "Locations", InputType.LIST, "msg.dlg.scene.location.links");
		col.setShowInSeparateTab(true);
		col.setTableCellRenderer(new LocationsTableCellRenderer());
		col.setCbDecorator(new LocationCbPanelDecorator());
		columns.add(col);

		col = new SbColumn(i++, "Items", InputType.LIST, "msg.dlg.scene.item.links");
		col.setShowInSeparateTab(true);
		col.setTableCellRenderer(new ItemsTableCellRenderer());
		col.setCbDecorator(new ItemCbPanelDecorator());
		columns.add(col);

		col = new SbColumn(i++, "Notes", InputType.TEXTAREA, "msg.common.notes");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setHideOnStart(true);
		col.setShowInSeparateTab(true);
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		columns.add(col);

		return columns;
	}

	public Vector<SbColumn> getInternalColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "Key", "msg.internal.key");
		col.setVerifier(new NotEmptyVerifier());
		col.setDefaultSort(true);
		columns.add(col);

		col = new SbColumn(i++, "StringValue", "msg.internal.string");
		columns.add(col);

		col = new SbColumn(i++, "IntegerValue", "msg.internal.integer");
		columns.add(col);

		col = new SbColumn(i++, "BooleanValue", "msg.internal.boolean");
		columns.add(col);

		col = new SbColumn(i++, "BinValue", "msg.internal.bin");
		columns.add(col);

		return columns;
	}

	public Vector<SbColumn> getTimeEventColumns() {
		int i=1;
		Vector<SbColumn> columns = new Vector<SbColumn>();
		columns.add(getIdColumn());

		SbColumn col = new SbColumn(i++, "EventTime", InputType.DATE, "msg.timeevent.date");
		col.setMaxLength(255);
		col.setGrowX(true);
		columns.add(col);

		col = new SbColumn(i++, "TimeStepState", InputType.COMBOBOX, "msg.timeevent.combo.label");
		col.setComboModel(new TimeEventFormatComboModel());
		col.setListCellRenderer(new TimeEventFormatListCellRenderer());
		col.setTableCellRenderer(new TimeEventFormatTableCellRenderer());
		col.setHideOnStart(true);
		col.setHideOnInfo(true);
		columns.add(col);

		col = new SbColumn(i++, "Category", "msg.item.category");
		col.setMaxLength(255);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setGrowX(true);
		col.setAutoComplete(true);
		col.setAutoCompleteDaoMethod("findCategories");
		columns.add(col);

		col = new SbColumn(i++, "Title", "msg.dlg.chapter.title");
		col.setMaxLength(255);
		col.setGrowX(true);
		VerifierGroup group2 = new VerifierGroup();
		group2.addVerifier(new NotEmptyVerifier());
		group2.addVerifier(new LengthVerifier(col.getMaxLength()));
		col.setVerifier(group2);
		columns.add(col);

		col = new SbColumn(i++, "Notes", InputType.TEXTAREA, "msg.dlg.chapter.description");
		col.setMaxLength(32768);
		col.setVerifier(new LengthVerifier(col.getMaxLength()));
		col.setTableCellRenderer(new HtmlTableCellRenderer());
		col.setHideOnStart(true);
		columns.add(col);

		return columns;
	}

	private SbColumn getIdColumn() {
		SbColumn col = new SbColumn(0, "Id", "msg.common.id");
		col.setReadOnly(true);
		col.setVerifier(new IntegerVerifier());
		col.setHideOnStart(true);
		return col;
	}

	List<SbColumn> getAttributeColumns() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
