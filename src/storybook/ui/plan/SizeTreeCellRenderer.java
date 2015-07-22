package storybook.ui.plan;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import storybook.model.EntityUtil;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Scene;

@SuppressWarnings("serial")
public class SizeTreeCellRenderer extends JPanel implements TreeCellRenderer {
	
	private static final int BARSIZE = 200;
	private static final int DELTA = 20;

	private int maxval;
	private int currentval;
	private JLabel label;
	
	public SizeTreeCellRenderer() {
		setLayout(null);
		setBackground(UIManager.getColor("Tree.textBackground"));
		label = new JLabel("Test");
		label.setFont(UIManager.getFont("Tree.font"));
		add(label);
	}
	
	public Dimension getPreferredSize()
	{
		Dimension size = label.getPreferredSize();
		if (maxval > 0) {
		    size.width += BARSIZE + DELTA;
		}
		return size;
	} 
	
	@Override
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y , w, h);
		label.setBounds(0, 0, label.getPreferredSize().width, label.getPreferredSize().height);
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object userObject = node.getUserObject();
		label.setText(userObject.toString());

		maxval = -1;
		if (userObject instanceof SizedElement) {
			currentval = ((SizedElement)userObject).getSize();
			maxval = -1;
			Object obj = ((SizedElement)userObject).getElement();
			if (obj instanceof Scene) {
				Scene scene = (Scene) obj;
				label.setIcon(scene.getSceneState().getIcon());
			} else if ((leaf) && (obj instanceof AbstractEntity)) {
				Icon icon = EntityUtil
						.getEntityIcon((AbstractEntity) obj);
				label.setIcon(icon);
			} else if (obj instanceof String) {
				// default icon for title
				label.setIcon(UIManager.getIcon("Tree.closedIcon"));
				maxval = 100;
			}
			if (!leaf && obj instanceof AbstractEntity) {
				Icon icon = EntityUtil.getEntityIcon((AbstractEntity) obj);
				label.setIcon(icon);
				if (obj instanceof Part) {
					maxval = Math.max(currentval, ((Part)obj).getObjectiveChars());
				}
				else if (obj instanceof Chapter) {
					maxval = Math.max(currentval, ((Chapter)obj).getObjectiveChars());
				}
			}
		}
		return this;
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		
		if (maxval > 0) {
			Rectangle dimension = getBounds();
            g.setColor(Color.BLUE);
            g.fillRect(dimension.width - BARSIZE , 1, BARSIZE, dimension.height -2);
            g.setColor(Color.GREEN);
            int width = (BARSIZE * currentval) / maxval;
            g.fillRect(dimension.width - BARSIZE +1, 2, width -2, dimension.height -4);
		}

	}
}