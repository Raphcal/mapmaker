package fr.rca.mapmaker.editor.tool;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface Tool extends MouseListener, MouseMotionListener {

	void reset();
}
