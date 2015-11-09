package fr.rca.mapmaker.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class LayerLayout implements LayoutManager {
	
	public static enum Disposition {
		TOP_LEFT {

			@Override
			public Rectangle getBounds(Container parent, Component component) {
				final Dimension preferredSize = component.getPreferredSize();
				return new Rectangle(preferredSize.width, preferredSize.height);
			}
			
		},
		CENTER {

			@Override
			public Rectangle getBounds(Container parent, Component component) {
				final Dimension size = parent.getSize();
				final Dimension preferredSize = component.getPreferredSize();
				
				return new Rectangle(
					(size.width - preferredSize.width)/2,
					(size.height - preferredSize.height)/2, 
					preferredSize.width, preferredSize.height);
			}
			
		};
		
		public abstract Rectangle getBounds(Container parent, Component component);
	}
	
	private final Disposition disposition;
	
	public LayerLayout() {
		disposition = Disposition.CENTER;
	}

	public LayerLayout(Disposition disposition) {
		this.disposition = disposition;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		final Dimension size = new Dimension(0, 0);
		
		for(final Component component : parent.getComponents()) {
			final Dimension preferredSize = component.getPreferredSize();
			
			size.width = Math.max(preferredSize.width, size.width);
			size.height = Math.max(preferredSize.height, size.height);
		}
		
		final Insets insets = parent.getInsets();
		size.width += insets.left + insets.right;
		size.height += insets.top + insets.bottom;
		
		return size;
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	@Override
	public void layoutContainer(Container parent) {
		for(final Component component : parent.getComponents()) {
			component.setBounds(disposition.getBounds(parent, component));
		}
	}

	public Disposition getDisposition() {
		return disposition;
	}
	
}
