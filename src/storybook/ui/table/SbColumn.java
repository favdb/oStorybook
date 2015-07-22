package storybook.ui.table;

import java.util.Comparator;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;

import storybook.toolkit.I18N;
import storybook.toolkit.completer.AbstractCompleter;
import storybook.toolkit.swing.verifier.AbstractInputVerifier;
import storybook.ui.edit.CbPanelDecorator;

import com.googlecode.genericdao.search.Search;
import storybook.ui.RadioButtonGroup;

public class SbColumn {

	public enum InputType {
		TEXTFIELD, TEXTAREA, COMBOBOX, CHECKBOX, DATE, COLOR, LIST, ICON, ATTRIBUTES, NONE, SEPARATOR
	}

	private final int colId;
	private final InputType inputType;
	private final String methodName;
	private final String resourceKey;
	private int width = 100;
	private boolean readOnly = false;
	private AbstractInputVerifier verifier = null;
	private boolean showInSeparateTab = false;
	private AbstractCompleter completer = null;
	private boolean hideOnStart = false;
	private boolean hideOnInfo = false;
	private boolean allowNoColor = true;
	private ComboBoxModel comboModel = null;
	private boolean emptyComboItem = false;
	private TableCellRenderer tableCellRenderer = null;
	private Comparator<?> comparator = null;
	private ListCellRenderer listCellRenderer = null;
	private Search search = null;
	private CbPanelDecorator decorator;
	private RadioButtonGroup radioButtonGroup;
	private int radioButtonIndex;
	private boolean growX;
	private boolean autoComplete;
	private String autoCompleteDaoMethod;
	private int maxLength = -1;
	private boolean showDateTime = false;
	private boolean defaultSort = false;
	private int maxChars = 4;

	public SbColumn(int colId, String methodName, String resourceKey) {
		this(colId, methodName, InputType.TEXTFIELD, resourceKey);
	}

	public SbColumn(int colId, String methodName, InputType inputType, String resourceKey) {
		this.colId = colId;
		this.methodName = methodName;
		this.inputType = inputType;
		this.resourceKey = resourceKey;
	}

	@Override
	public String toString() {
		if ((resourceKey != null) && (!resourceKey.isEmpty())) {
		   return I18N.getMsg(resourceKey);
		} else {
			return "";
		}
	}

	public void setVerifier(AbstractInputVerifier verifier) {
		this.verifier = verifier;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getColId() {
		return colId;
	}

	public InputType getInputType() {
		return inputType;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getResourceKey() {
		return resourceKey;
	}

	public int getWidth() {
		return width;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public boolean hasVerifier() {
		return verifier != null;
	}

	public AbstractInputVerifier getVerifier() {
		return verifier;
	}

	public boolean isShowInSeparateTab() {
		return showInSeparateTab;
	}

	public void setShowInSeparateTab(boolean showInSeparateTab) {
		this.showInSeparateTab = showInSeparateTab;
	}

	public boolean hasCompleter() {
		return completer != null;
	}

	public AbstractCompleter getCompleter() {
		return completer;
	}

	public void setCompleter(AbstractCompleter completer) {
		this.completer = completer;
	}

	public boolean isHideOnStart() {
		return hideOnStart;
	}

	public void setHideOnStart(boolean hideOnStart) {
		this.hideOnStart = hideOnStart;
	}

	public boolean isHideOnInfo() {
		return hideOnInfo;
	}

	public void setHideOnInfo(boolean hideOnInfo) {
		this.hideOnInfo = hideOnInfo;
	}

	public boolean isAllowNoColor() {
		return allowNoColor;
	}

	public void setAllowNoColor(boolean allowNoColor) {
		this.allowNoColor = allowNoColor;
	}

	public boolean hasComboModel() {
		return comboModel != null;
	}

	public ComboBoxModel getComboModel() {
		return comboModel;
	}

	public void setComboModel(ComboBoxModel comboModel) {
		this.comboModel = comboModel;
	}

	public boolean isEmptyComboItem() {
		return emptyComboItem;
	}

	public void setEmptyComboItem(boolean emptyComboItem) {
		this.emptyComboItem = emptyComboItem;
	}

	public boolean hasTableCellRenderer() {
		return tableCellRenderer != null;
	}

	public TableCellRenderer getTableCellRenderer() {
		return tableCellRenderer;
	}

	public void setTableCellRenderer(TableCellRenderer renderer) {
		this.tableCellRenderer = renderer;
	}

	public boolean hasListCellRenderer() {
		return listCellRenderer != null;
	}

	public ListCellRenderer getListCellRenderer() {
		return listCellRenderer;
	}

	public void setListCellRenderer(DefaultListCellRenderer listCellRenderer) {
		this.listCellRenderer = listCellRenderer;
	}

	public boolean hasSearch() {
		return search != null;
	}

	public Search getSearch() {
		return search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}

	public boolean hasDecorator() {
		return decorator != null;
	}

	public CbPanelDecorator getDecorator() {
		return decorator;
	}

	public void setCbDecorator(CbPanelDecorator decorator) {
		this.decorator = decorator;
	}

	public boolean hasRadioButtonGroup() {
		return radioButtonGroup != null;
	}

	public RadioButtonGroup getRadioButtonGroup() {
		return radioButtonGroup;
	}

	public void setRadioButtonGroup(RadioButtonGroup radioButtonGroup) {
		this.radioButtonGroup = radioButtonGroup;
	}

	public int getRadioButtonIndex() {
		return radioButtonIndex;
	}

	public void setRadioButtonIndex(int radioButtonIndex) {
		this.radioButtonIndex = radioButtonIndex;
	}

	public boolean isGrowX() {
		return growX;
	}

	public void setGrowX(boolean growX) {
		this.growX = growX;
	}

	public boolean isAutoComplete() {
		return autoComplete;
	}

	public void setAutoComplete(boolean autoComplete) {
		this.autoComplete = autoComplete;
	}

	public String getAutoCompleteDaoMethod() {
		return autoCompleteDaoMethod;
	}

	public void setAutoCompleteDaoMethod(String autoCompleteDaoMethod) {
		this.autoCompleteDaoMethod = autoCompleteDaoMethod;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public boolean hasMaxLength() {
		return maxLength > 0;
	}

	public int getMaxChars() {
		return maxChars;
	}

	public void setMaxChars(int maxChars) {
		this.maxChars = maxChars;
	}

	public void setShowDateTime(boolean showDateTime) {
		this.showDateTime = showDateTime;
	}

	public boolean hasDateTime() {
		return showDateTime;
	}

	public boolean isDefaultSort() {
		return defaultSort;
	}

	public void setDefaultSort(boolean defaultSort) {
		this.defaultSort = defaultSort;
	}

	public boolean hasComparator() {
		return comparator != null;
	}

	public Comparator<?> getComparator() {
		return comparator;
	}

	public void setComparator(Comparator<?> comparator) {
		this.comparator = comparator;
	}
}
