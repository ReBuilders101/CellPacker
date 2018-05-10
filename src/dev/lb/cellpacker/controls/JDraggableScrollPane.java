package dev.lb.cellpacker.controls;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class JDraggableScrollPane extends JScrollPane implements MouseMotionListener, MouseListener{
	private static final long serialVersionUID = -3429376543623295066L;

	private Point currentDrag;

	public JDraggableScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
		super(view, vsbPolicy, hsbPolicy);
		this.getViewport().getView().addMouseMotionListener(this);
		this.getViewport().getView().addMouseListener(this);
	}

	public JDraggableScrollPane(Component view) {
		super(view);
		if(this.getViewport().getView() != null){
			this.getViewport().getView().addMouseMotionListener(this);
			this.getViewport().getView().addMouseListener(this);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (currentDrag != null) {
            JViewport viewPort = getViewport();
            if (viewPort != null) {
                int deltaX = currentDrag.x - e.getX();
                int deltaY = currentDrag.y - e.getY();

                Rectangle view = viewPort.getViewRect();
                view.x += deltaX;
                view.y += deltaY;

                ((JComponent) getViewport().getView()).scrollRectToVisible(view);
            }
        }
	}

	@Override
	public void mousePressed(MouseEvent e) {
		currentDrag = new Point(e.getPoint());
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
}
