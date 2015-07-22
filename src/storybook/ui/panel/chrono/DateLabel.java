package storybook.ui.panel.chrono;

import java.awt.Color;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.time.FastDateFormat;
import storybook.toolkit.I18N;
import storybook.toolkit.swing.SwingUtil;

@SuppressWarnings("serial")
public class DateLabel extends JLabel {

	private Date date;

	public DateLabel(Date date) {
		super();
		this.date = date;
		setText(getDateText());
		setToolTipText(getDateText());
		setIcon(I18N.getIcon("icon.small.chrono.view"));
		setBackground(new Color(240, 240, 240));
		setOpaque(true);
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	public final String getDateText() {
		if (date == null) {
			return "";
		}
		String dateStr = FastDateFormat.getDateInstance(FastDateFormat.MEDIUM)
				.format(date);
		String dayStr = SwingUtil.getDayName(date);
		return dayStr + " - " + dateStr;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
