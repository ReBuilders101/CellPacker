package dev.lb.cellpacker.controls;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class CheckBoxList extends JList<JCheckBox>{
	private static final long serialVersionUID = 2796368191296898045L;

	protected static Border noFocusBorder =
			new EmptyBorder(1, 1, 1, 1);

	public CheckBoxList()
	{
		super(new DefaultListModel<>());
		
		setCellRenderer(new CellRenderer());

		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				int index = locationToIndex(e.getPoint());

				if (index != -1) {
					JCheckBox checkbox = (JCheckBox)
							getModel().getElementAt(index);
					checkbox.setSelected(
							!checkbox.isSelected());
					repaint();
				}
			}
		}
				);

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public void addCheckbox(JCheckBox checkBox) {
	    ListModel<JCheckBox> currentList = this.getModel();
	    JCheckBox[] newList = new JCheckBox[currentList.getSize() + 1];
	    for (int i = 0; i < currentList.getSize(); i++) {
	        newList[i] = (JCheckBox) currentList.getElementAt(i);
	    }
	    newList[newList.length - 1] = checkBox;
	    setListData(newList);
	}
	
	protected class CellRenderer implements ListCellRenderer<JCheckBox>
	{
		public Component getListCellRendererComponent(
				JList<? extends JCheckBox> list, JCheckBox value, int index,
				boolean isSelected, boolean cellHasFocus)
		{
			JCheckBox checkbox = (JCheckBox) value;
			checkbox.setBackground(isSelected ?
					getSelectionBackground() : getBackground());
			checkbox.setForeground(isSelected ?
					getSelectionForeground() : getForeground());
			checkbox.setEnabled(isEnabled());
			checkbox.setFont(getFont());
			checkbox.setFocusPainted(false);
			checkbox.setBorderPainted(true);
			checkbox.setBorder(isSelected ?
					UIManager.getBorder(
							"List.focusCellHighlightBorder") : noFocusBorder);
			return checkbox;
		}
	}
}
