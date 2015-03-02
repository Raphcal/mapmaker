package fr.rca.mapmaker.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation à 2 dimensions d'un arbre k-d.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class KdTree {
	private final int numberOfDimensions = 2;
	private final Element root;
	
	public KdTree(List<Point> points) {
		root = add(points, 0);
	}
	
	public List<Point> get(Point point, float distance) {
		final ArrayList<Point> points = new ArrayList<Point>();
		
		// TODO: Rechercher le 1er élément avec la même méthode que l'insertion.
		// Calculer la distance
		// si la distance x,y mélangée à la valeur du noeud recherché (x si 
		// l'axe est x, y sinon) est à distance, vérifier le nœud suivant
		// Remonter jusqu'à ce que le nœud soit à une distance superieur.
		
		return points;
	}
	
	private Element add(List<Point> points, int depth) {
		if(points.size() == 1) {
			return new Leaf(points.get(0));
			
		} else {
			final Axis axis = Axis.values()[depth % numberOfDimensions];

			final float median = median(points, axis);

			final ArrayList<Point> left = new ArrayList<Point>();
			final ArrayList<Point> right = new ArrayList<Point>();

			for(final Point point : points) {
				if(axis.get(point) < median) {
					left.add(point);
				} else {
					right.add(point);
				}
			}

			return new Node(median, 
				add(left, depth + 1), 
				add(right, depth + 1));
		}
	}
	
	private float median(List<Point> points, Axis axis) {
		float total = 0;
		for(Point point : points) {
			total += axis.get(point);
		}
		return total / (float)points.size();
	}
	
	private enum Axis {
		X {
			@Override
			public float get(Point point) {
				return (float) point.getX();
			}
		}, 
		Y {
			@Override
			public float get(Point point) {
				return (float) point.getY();
			}
		};
		
		public abstract float get(Point point);
	}
	
	private interface Element {
		int getDepth();
		Element getParent();
		boolean isLeaf();
	}
	
	private abstract class BaseElement implements Element {
		private int depth;
		private Element parent;

		@Override
		public int getDepth() {
			return depth;
		}

		public void setDepth(int depth) {
			this.depth = depth;
		}

		@Override
		public Element getParent() {
			return parent;
		}

		public void setParent(Element parent) {
			this.parent = parent;
		}
	}
	
	private class Node extends BaseElement {
		private final float location;
		private final Element left;
		private final Element right;

		public Node(float location, Element left, Element right) {
			this.location = location;
			this.left = left;
			this.right = right;
		}

		@Override
		public boolean isLeaf() {
			return false;
		}

		public float getLocation() {
			return location;
		}
	}
	
	private class Leaf extends BaseElement {
		private final Point value;

		public Leaf(Point value) {
			this.value = value;
		}

		@Override
		public boolean isLeaf() {
			return true;
		}

		public Point getValue() {
			return value;
		}
	}
}
