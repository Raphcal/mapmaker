package fr.rca.mapmaker.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Implémentation à 2 dimensions d'un arbre k-d.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class KdTree {
	private final static int NUMBER_OF_DIMENSIONS = 2;
	private Element root;

	public KdTree() {
	}
	
	public KdTree(List<Point2D.Float> points) {
		root = add(points, 0, null);
	}
	
	public List<Point2D.Float> get(Point2D.Float point, float distance) {
		final List<Point2D.Float> points = new ArrayList<Point2D.Float>();
		
		final Leaf nearest = getNearestLeaf(point);
		if(distance(point, nearest) <= distance) {
			points.add(nearest.getValue());
		}
		
		final Set<Element> doneNodes = new HashSet<Element>();
		doneNodes.add(nearest);
		
		testElement(nearest.getParent(), point, distance, points, doneNodes);
		
		return points;
	}
	
	public void add(Point2D.Float point) {
		root = add(point, 0, root);
	}
	
	private void testElement(Element element, Point2D.Float point, float distance, List<Point2D.Float> points, Set<Element> doneNodes) {
		if(element instanceof Leaf) {
			final Leaf leaf = (Leaf)element;
			if(distance(point, leaf) <= distance) {
				points.add(leaf.getValue());
			}
			doneNodes.add(element);
			
		} else if(element instanceof Node) {
			final Node node = (Node)element;
			
			final Axis axis = Axis.values()[node.getDepth() % NUMBER_OF_DIMENSIONS];
			final Point2D.Float nodePoint;
			if(axis == Axis.X) {
				nodePoint = new Point2D.Float(node.getLocation(), (float)point.getY());
			} else {
				nodePoint = new Point2D.Float((float)point.getX(), node.getLocation());
			}
			
			if(point.distance(nodePoint) > distance) {
				return;
			}
			
			if(!doneNodes.contains(node.getLeft())) {
				testElement(node.getLeft(), point, distance, points, doneNodes);
			}
			if(!doneNodes.contains(node.getRight())) {
				testElement(node.getRight(), point, distance, points, doneNodes);
			}
			doneNodes.add(node);
			
			testElement(node.getParent(), point, distance, points, doneNodes);
		}
	}
	
	private float distance(Point2D.Float point, Leaf leaf) {
		return (float) point.distance(leaf.getValue());
	}
	
	private Leaf getNearestLeaf(Point2D.Float point) {
		return getNearestLeaf(point, 0, root);
	}
	
	private Leaf getNearestLeaf(Point2D.Float point, int depth, Element parent) {
		if(parent instanceof Leaf) {
			return (Leaf)parent;
			
		} else if(parent instanceof Node) {
			final Node node = (Node)parent;
			final Axis axis = Axis.values()[depth % NUMBER_OF_DIMENSIONS];
			
			if(axis.get(point) < node.getLocation()) {
				return getNearestLeaf(point, depth + 1, node.getLeft());
			} else {
				return getNearestLeaf(point, depth + 1, node.getRight());
			}
			
		} else {
			return null;
		}
	}
	
	private Element add(Point2D.Float point, int depth, Element parent) {
		final Axis axis = Axis.values()[depth % NUMBER_OF_DIMENSIONS];
		
		if(parent instanceof Leaf) {
			final Leaf leaf = (Leaf)parent;
			final float median = median(Arrays.asList(point, leaf.getValue()), axis);
			
			final Point2D.Float left;
			final Point2D.Float right;
			
			if(axis.get(point) < median) {
				left = point;
				right = leaf.getValue();
			} else {
				left = leaf.getValue();
				right = point;
			}
			
			final Node node = new Node(median, depth, parent);
			node.setLeft(new Leaf(left, depth + 1, node));
			node.setRight(new Leaf(right, depth + 1, node));
			return node;
			
		} else if(parent instanceof Node) {
			final Node node = (Node)parent;
			
			if(axis.get(point) < node.getLocation()) {
				return add(point, depth + 1, node.left);
			} else {
				return add(point, depth + 1, node.right);
			}
		}
		
		return new Leaf(point, depth, parent);
	}
	
	private Element add(List<Point2D.Float> points, int depth, Element parent) {
		if(points.isEmpty()) {
			return null;
			
		} else if(points.size() == 1) {
			return new Leaf(points.get(0), depth, parent);
			
		} else {
			final Axis axis = Axis.values()[depth % NUMBER_OF_DIMENSIONS];

			final float median = median(points, axis);

			final List<Point2D.Float> left = new ArrayList<Point2D.Float>();
			final List<Point2D.Float> right = new ArrayList<Point2D.Float>();

			for(final Point2D.Float point : points) {
				if(axis.get(point) < median) {
					left.add(point);
				} else {
					right.add(point);
				}
			}

			final Node node = new Node(median, depth, parent);
			node.setLeft(add(left, depth + 1, node));
			node.setRight(add(right, depth + 1, node));
			return node;
		}
	}
	
	private float median(List<Point2D.Float> points, Axis axis) {
		float total = 0;
		for(Point2D.Float point : points) {
			total += axis.get(point);
		}
		return total / (float)points.size();
	}
	
	private enum Axis {
		X {
			@Override
			public float get(Point2D.Float point) {
				return (float) point.getX();
			}
		}, 
		Y {
			@Override
			public float get(Point2D.Float point) {
				return (float) point.getY();
			}
		};
		
		public abstract float get(Point2D.Float point);
	}
	
	private interface Element {
		int getDepth();
		Element getParent();
		boolean isLeaf();
	}
	
	private abstract class BaseElement implements Element {
		private int depth;
		private Element parent;

		public BaseElement(int depth, Element parent) {
			this.depth = depth;
			this.parent = parent;
		}
		
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
		private Element left;
		private Element right;

		public Node(float location, int depth, Element parent) {
			super(depth, parent);
			this.location = location;
		}

		@Override
		public boolean isLeaf() {
			return false;
		}

		public float getLocation() {
			return location;
		}

		public Element getLeft() {
			return left;
		}

		public void setLeft(Element left) {
			this.left = left;
		}

		public Element getRight() {
			return right;
		}
		
		public void setRight(Element right) {
			this.right = right;
		}
	}
	
	private class Leaf extends BaseElement {
		private final Point2D.Float value;

		public Leaf(Point2D.Float value, int depth, Element parent) {
			super(depth, parent);
			this.value = value;
		}

		@Override
		public boolean isLeaf() {
			return true;
		}

		public Point2D.Float getValue() {
			return value;
		}
	}
	
	public static void main(String[] args) {
		long iterateMin = Long.MAX_VALUE;
		long iterateMax = 0L;
		long iterateTotal = 0L;
		
		long kdMin = Long.MAX_VALUE;
		long kdMax = 0L;
		long kdTotal = 0L;
		
		int errors = 0;
		
		final Random random = new Random();
		final int tests = 10000;
		
		for(int i = 0; i < tests; i++) {
			final List<Point2D.Float> points = generatePoints(10000);
			final KdTree tree = new KdTree(points);
			
			final Point2D.Float reference = new Point2D.Float(random.nextFloat() * 100.0f, random.nextFloat() * 100.0f);
			
			final long iterateStart = System.nanoTime();
			final List<Point2D.Float> iterateNeighbors = findNeighbors(points, reference, 20.0f);
			final long iterateTime = System.nanoTime() - iterateStart;
			iterateMin = Math.min(iterateMin, iterateTime);
			iterateMax = Math.max(iterateMax, iterateTime);
			iterateTotal += iterateTime;
			
			final long kdStart = System.nanoTime();
			final List<Point2D.Float> kdNeighbors = tree.get(reference, 20.0f);
			final long kdTime = System.nanoTime() - kdStart;
			kdMin = Math.min(kdMin, kdTime);
			kdMax = Math.max(kdMax, kdTime);
			kdTotal += kdTime;
			
			if(iterateNeighbors.size() != kdNeighbors.size()) {
//				System.out.println("Erreur : " + iterateNeighbors.size() + " vs " + kdNeighbors.size());
				errors++;
			}
		}
		
		System.out.println(errors + " errors.");
		System.out.println("Iterate - min : " + iterateMin + ", max : " + iterateMax + ", avg : " + (double)iterateTotal/(tests * 1000000.0) + "ms");
		System.out.println("Kd Tree - min : " + kdMin + ", max : " + kdMax + ", avg : " + (double)kdTotal/(tests * 1000000.0) + "ms");
	}
	
	private static List<Point2D.Float> generatePoints(int length) {
		final Point2D.Float[] points = new Point2D.Float[length];
		final Random random = new Random();
		
		for(int i = 0; i < length; i++) {
			points[i] = new Point2D.Float(random.nextFloat() * 100.0f, random.nextFloat() * 100.0f);
		}
		
		return Arrays.asList(points);
	}
	
	private static List<Point2D.Float> findNeighbors(List<Point2D.Float> points, Point2D.Float reference, float distance) {
		final List<Point2D.Float> neighbors = new ArrayList<Point2D.Float>();
		
		for(final Point2D.Float point : points) {
			if(reference.distance(point) <= distance) {
				neighbors.add(point);
			}
		}
		
		return neighbors;
	}
}
