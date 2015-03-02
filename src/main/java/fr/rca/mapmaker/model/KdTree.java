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
			final Leaf leaf = new Leaf();
			leaf.value = points.get(0);
			return leaf;
			
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

			final Node node = new Node();
			node.left = add(left, depth + 1);
			node.right = add(right, depth + 1);
			
			return node;
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
	}
	
	private class Node implements Element {
		private float location;
		private Element left;
		private Element right;
	}
	
	private class Leaf implements Element {
		private Point value;
	}
}
