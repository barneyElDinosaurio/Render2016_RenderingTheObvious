package wblut.Render2016;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_RandomDisk;
import wblut.geom.WB_RandomPoint;
import wblut.geom.WB_Triangulate;
import wblut.geom.WB_Triangulation2D;
import wblut.geom.WB_Vector;

public class EndSlide extends Slide {

	WB_RandomPoint source;
	ArrayList<WB_Point> points;
	ArrayList<WB_Point> points2;
	ArrayList<WB_Point> allpoints;
	int numPoints;
	int numPoints2;
	int[] edges;
	int[] edges2;
	int[] alledges;
	float L, L2;
	float L2Cutoff;
	float L2Grey;
	int numBoundary;
	List<WB_Polygon> text;
	WB_AABB AABB;

	public EndSlide(final RTO home) {
		super(home, "");
	}

	@Override
	public void setup() {
		home.cam.setDistance(935, 500);
		numPoints = 2000;
		points = new ArrayList<WB_Point>(numPoints);
		L = 12.0f;
		L2 = L * L;
		L2Cutoff = 25 * L2;
		L2Grey = 4 * L2;
		numBoundary = 200;
		final float radius = (numBoundary * L) / PApplet.TWO_PI;
		for (int i = 0; i < numBoundary; i++) {
			points.add(new WB_Point(radius * PApplet.cos((i * PApplet.TWO_PI) / numBoundary),
					radius * PApplet.sin((i * PApplet.TWO_PI) / numBoundary)));
		}
		source = new WB_RandomDisk().setRadius(0.95 * radius);
		for (int i = numBoundary; i < numPoints; i++) {
			WB_Point p = source.nextPoint();
			while ((p.yf() < 100) && (p.yf() > -100)) {
				p = source.nextPoint();
			}

			points.add(p);
		}

		WB_Triangulation2D triangulation = WB_Triangulate.triangulate2D(points);
		edges = triangulation.getEdges();

		points2 = new ArrayList<WB_Point>(numPoints2);

		text = gf.createTextWithTTFFont("Thank you", home.sketchPath("data/fonts/SourceSansPro-Light.ttf"), 0, 170, .1);// text;
		// font;
		// style:
		// REGULAR:0,
		// BOLD:1,
		// ITALIC:2,
		// BOLD-ITALIC:3
		// ;
		// font
		// size;
		// flatness
		createAABB();

		final List<WB_Polygon> newtext = new ArrayList<WB_Polygon>();
		for (final WB_Polygon poly : text) {
			newtext.addAll(gf.createDensifiedPolygon(poly, 4.0));
		}

		for (final WB_Polygon poly : newtext) {
			for (int i = 0; i < poly.getNumberOfPoints(); i++) {
				poly.getPoint(i).subSelf(AABB.getCenter()).scaleSelf(1, -1, 1);
				points2.add(new WB_Point(poly.getPoint(i)));
			}
		}

		numPoints2 = points2.size();
		triangulation = WB_Triangulate.triangulate2D(points2);
		edges2 = triangulation.getEdges();
		allpoints = new ArrayList<WB_Point>(numPoints + numPoints2);
		allpoints.addAll(points);
		allpoints.addAll(points2);
		triangulation = WB_Triangulate.triangulate2D(allpoints);
		alledges = triangulation.getEdges();
		home.noFill();
	}

	void createAABB() {
		AABB = new WB_AABB();
		for (final WB_Polygon poly : text) {
			for (int i = 0; i < poly.getNumberOfShellPoints(); i++) {
				AABB.expandToInclude(poly.getPoint(i));
			}
		}
	}

	@Override
	public void updatePre() {

	}

	@Override
	void backgroundDraw() {
		home.background(20);

	}

	@Override
	void transformAndLights() {
		home.directionalLight(255, 255, 255, 1, 1, -1);
		home.directionalLight(127, 127, 127, -1, -1, 1);
	}

	@Override
	void normalDraw() {
		for (int i = 0; i < edges.length; i += 2) {
			final double d2 = points.get(edges[i]).getSqDistance2D(points.get(edges[i + 1]));
			if (d2 < L2Cutoff) {
				home.stroke(PApplet.min(255, PApplet.max(0, (int) (255 - (((d2 - L2) / L2Grey) * 255)))));
				render.drawSegment2D(points.get(edges[i]), points.get(edges[i + 1]));
			}
		}

		for (int i = 0; i < edges2.length; i += 2) {
			final double d2 = points2.get(edges2[i]).getSqDistance2D(points2.get(edges2[i + 1]));
			if (d2 < (0.09 * L2Cutoff)) {
				home.stroke(PApplet.max(0, (int) (255 - (((d2 - (0.18 * L2)) / (0.18 * L2Grey)) * 255))), 0, 0);
				render.drawSegment2D(points2.get(edges2[i]), points2.get(edges2[i + 1]));
			}
		}
		// shake();
		for (int r = 0; r < 10; r++) {
			update(numPoints, numBoundary, edges, points, L2, L2Cutoff, L2Grey, 0.05f, true);

			update(numPoints2, 0, edges2, points2, 0.09f * L2,
					PApplet.min(4.0f, (slideCounter * 0.0002f) + 0.09f) * L2Cutoff, 0.09f * L2Grey, 0.02f, false);

			updatemixed(0.05f);
		}
		// rotate();
		WB_Triangulation2D triangulation = WB_Triangulate.triangulate2D(points);
		edges = triangulation.getEdges();
		triangulation = WB_Triangulate.triangulate2D(points2);
		edges2 = triangulation.getEdges();
		triangulation = WB_Triangulate.triangulate2D(allpoints);
		alledges = triangulation.getEdges();

	}

	@Override
	void glowDraw() {

	}

	@Override
	public void hudDraw() {

	}

	@Override
	public void updatePost() {

	}

	@Override
	void shutdown() {

	}

	void update(final int numPoints, final int numBoundary, final int[] edges, final ArrayList<WB_Point> points,
			final float L2, final float L2Cutoff, final float L2Grey, final float strength, final boolean fall) {
		final WB_Vector[] forces = new WB_Vector[numPoints];
		for (int i = 0; i < numPoints; i++) {
			forces[i] = new WB_Vector();
		}
		for (int i = 0; i < edges.length; i += 2) {
			final WB_Point p = points.get(edges[i]);
			final WB_Point q = points.get(edges[i + 1]);

			final double d2 = p.getSqDistance2D(q);
			if (d2 < L2Cutoff) {
				if (d2 > (1.05 * L2)) {
					final WB_Vector v = WB_Vector.subToVector2D(p, q);
					final double d = Math.sqrt(d2);
					if (d > 1) {
						v.mulSelf(-1.0 / d);
					}
					forces[edges[i]].addSelf(v);
					forces[edges[i + 1]].subSelf(v);
				} else if (d2 < (0.95 * L2)) {
					final WB_Vector v = WB_Vector.subToVector2D(p, q);
					v.mulSelf((10.0 / Math.sqrt(d2)) * (1.0 - ((1.0 * d2) / L2)));
					forces[edges[i]].addSelf(v);
					forces[edges[i + 1]].subSelf(v);
				}
			}
		}
		for (int i = numBoundary; i < numPoints; i++) {
			if (fall) {
				forces[i].addSelf(new WB_Vector(0, 1, 0));
			}
			points.get(i).addMulSelf(strength, forces[i]);
			if (points.get(i).yf() > ((home.height / 2) + 50)) {
				points.get(i).subSelf(0, home.height + 100, 0);
			}
		}
	}

	void updatemixed(final float strength) {
		final WB_Vector[] forces = new WB_Vector[numPoints + numPoints2];
		for (int i = 0; i < (numPoints + numPoints2); i++) {
			forces[i] = new WB_Vector();
		}
		for (int i = 0; i < alledges.length; i += 2) {
			final WB_Point p = allpoints.get(alledges[i]);
			final WB_Point q = allpoints.get(alledges[i + 1]);
			if (((alledges[i] < numPoints) && (alledges[i + 1] >= numPoints))
					|| ((alledges[i] >= numPoints) && (alledges[i + 1] < numPoints))) {
				final double d2 = p.getSqDistance2D(q);
				if (d2 < L2Cutoff) {
					if (d2 < (4.0 * L2)) {
						final WB_Vector v = WB_Vector.subToVector2D(p, q);
						v.mulSelf((10.0 / Math.sqrt(d2)) * (4.0 - (d2 / L2)));
						if (alledges[i] < numPoints) {
							forces[alledges[i]].addSelf(v);
						}
						if (alledges[i + 1] < numPoints) {
							forces[alledges[i + 1]].subSelf(v);
						}
					}
				}
			}
		}
		for (int i = numBoundary; i < (numPoints + numPoints2); i++) {
			allpoints.get(i).addMulSelf(strength, forces[i]);
		}
	}

}
