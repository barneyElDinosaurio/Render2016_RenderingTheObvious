package wblut.Render2016;

import java.util.ArrayList;
import java.util.List;

import javolution.util.FastTable;
import processing.core.PConstants;
import wblut.geom.WB_AABB;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Voronoi;
import wblut.geom.WB_VoronoiCell2D;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render3D;

public class RecurVor extends Slide {
	List<WB_Point> points;
	List<WB_Point> boundary;
	List<WB_VoronoiCell2D> voronoiXY;
	List<WB_VoronoiCell2D> cells;
	List<Column> columns;
	WB_Render3D render;
	WB_GeometryFactory gf = WB_GeometryFactory.instance();

	public RecurVor(final RTO home, final String title) {
		super(home, title);

	}

	@Override
	public void setup() {
		render = new WB_Render3D(home);
		home.colorMode(PConstants.HSB);

		points = new FastTable<WB_Point>();
		boundary = new FastTable<WB_Point>();
		// add points to collection
		for (int i = 0; i < 4; i++) {
			points.add(new WB_Point(home.random(-840, 840), home.random(-800, 600), 0));
		}

		boundary.add(new WB_Point(-840, -800));
		boundary.add(new WB_Point(-840, 600));
		boundary.add(new WB_Point(840, 600));
		boundary.add(new WB_Point(840, -800));
		voronoiXY = WB_Voronoi.getClippedVoronoi2D(points, boundary, gf.createEmbeddedPlane());

		cells = new FastTable<WB_VoronoiCell2D>();
		columns = new ArrayList<Column>();
		final float baseh = home.random(255.0f);
		int cid = 0;
		for (final WB_VoronoiCell2D vorcell : voronoiXY) {
			float nh = baseh + home.random(-25, 25);
			if (nh > 255) {
				nh -= 255;
			}
			if (nh < 0) {
				nh += 255;
			}
			if (vorcell.getPolygon().getNumberOfPoints() > 0) {
				cells.add(vorcell);
				columns.add(new Column(vorcell.getPolygon(), nh, 128, 150, 0, ((cid++) * 30) + home.random(30), 20));
			}
		}

		home.stroke(0);
	}

	@Override
	void normalDraw() {
		home.background(20);
		home.lights();

		home.rotateX(0.3f * PConstants.PI);
		// rotateY(1.0/width*mouseX*TWO_PI-PI);
		home.translate(0, -120, 0);
		home.scale(0.6f);
		for (final Column col : columns) {
			col.update();

			if (col.mesh != null) {
				home.fill(col.h, col.s, 1.5f * col.b);
				home.noStroke();
				render.drawFaces(col.mesh);

			}
		}
		if (((slideCounter % 20) == 1) && ((slideCounter / 20) < 4)) {
			divide(slideCounter / 20);
		}
		if ((slideCounter % 180) == 0) {
			mousePressed();
		}
		hudDraw();

	}

	@Override
	void shutdown() {
		home.colorMode(PConstants.RGB);
	}

	@Override
	void keyPressed() {
		// TODO Auto-generated method stub

	}

	void divide(final int rep) {
		final List<WB_VoronoiCell2D> subcells = new ArrayList<WB_VoronoiCell2D>();
		final List<Column> subcolumns = new ArrayList<Column>();
		int cid = 0;

		for (final WB_VoronoiCell2D cell : cells) {
			final Column col = columns.get(cid);
			if (home.random(100) < ((rep < 4) ? 20 : 80)) {

				subcells.add(cell);
				subcolumns.add(col);

			} else {
				final List<WB_VoronoiCell2D> tmp = getSub(cell, 4);
				int id = 0;
				for (final WB_VoronoiCell2D subcell : tmp) {
					if (home.random(100) < 120) {
						subcells.add(tmp.get(id));
						float nh = col.h + home.random(-5, 5);
						if (nh > 255) {
							nh -= 255;
						}
						if (nh < 0) {
							nh += 255;
						}
						float ns = col.s + home.random(-25, 25);
						if (ns > 255) {
							ns = 255;
						}
						if (ns < 0) {
							ns = 0;
						}
						float nb = col.b + home.random(-25, 25);
						if (nb > 255) {
							nb = 255;
						}
						if (nb < 0) {
							nb = 0;
						}
						subcolumns.add(new Column(subcell.getPolygon(), nh, ns, nb, col.vheight,
								(0.8f + (0.25f * id)) * col.theight, 20));

					}
					id++;
				}
			}
			cid++;
		}

		cells = subcells;
		columns = subcolumns;

	}

	List<WB_VoronoiCell2D> getSub(final WB_VoronoiCell2D cell, final int n) {
		final WB_Point[] ppoints = new WB_Point[cell.getPolygon().getNumberOfPoints()];
		final WB_AABB aabb = new WB_AABB();
		for (int i = 0; i < cell.getPolygon().getNumberOfPoints(); i++) {
			aabb.expandToInclude(ppoints[i] = new WB_Point(cell.getPolygon().getPoint(i)));
		}

		final WB_Point[] points = new WB_Point[n];
		for (int i = 0; i < n; i++) {
			points[i] = new WB_Point(home.random((float) aabb.getMinX(), (float) aabb.getMaxX()),
					home.random((float) aabb.getMinY(), (float) aabb.getMaxY()), 0);
		}
		final List<WB_VoronoiCell2D> vorcells = WB_Voronoi.getClippedVoronoi2D(points, ppoints,
				gf.createEmbeddedPlane());
		final List<WB_VoronoiCell2D> cells = new ArrayList<WB_VoronoiCell2D>();
		for (final WB_VoronoiCell2D vorcell : vorcells) {
			if (vorcell.getPolygon() != null) {
				if (vorcell.getPolygon().getNumberOfPoints() > 0) {
					cells.add(vorcell);
				}
			}
		}
		return cells;
	}

	@Override
	void mousePressed() {
		points = new FastTable<WB_Point>();
		for (int i = 0; i < 4; i++) {
			points.add(new WB_Point(home.random(-840, 840), home.random(-800, 600), 0));
		}

		voronoiXY = WB_Voronoi.getClippedVoronoi2D(points, boundary, gf.createEmbeddedPlane());
		cells = new ArrayList<WB_VoronoiCell2D>();
		columns = new ArrayList<Column>();
		final float baseh = home.random(255.0f);
		int cid = 0;
		for (final WB_VoronoiCell2D vorcell : voronoiXY) {
			float nh = baseh + home.random(-25, 25);
			if (nh > 255) {
				nh -= 255;
			}
			if (nh < 0) {
				nh += 255;
			}
			if (vorcell.getPolygon().getNumberOfPoints() > 0) {
				cells.add(vorcell);
				columns.add(new Column(vorcell.getPolygon(), nh, 128, 150, 0, ((cid++) * 20) + home.random(20), 20));
			}
		}
		slideCounter = 0;

	}

	class Column {
		float h, s, b;
		float cheight;
		float theight;
		float vheight;
		int steps;
		int counter;
		WB_Polygon points;
		HE_Mesh mesh;

		Column(final WB_Polygon points, final float h, final float s, final float b, final float cheight,
				final float theight, final int steps) {
			this.points = points;
			this.h = h;
			this.s = s;
			this.b = b;
			this.cheight = vheight = cheight;
			this.theight = theight;
			this.steps = steps;
			mesh = new HE_Mesh(gf.createPrism(this.points, this.cheight));
			counter = 0;
		}

		void update() {
			if (counter <= steps) {
				counter++;
				vheight = cheight + (((counter * 1.0f) / steps) * (theight - cheight));
				mesh = new HE_Mesh(gf.createPrism(points, vheight));
			}
		}

	}

	@Override
	void glowDraw() {
		// TODO Auto-generated method stub

	}

	@Override
	void updatePre() {
		// TODO Auto-generated method stub

	}

	@Override
	void backgroundDraw() {
		// TODO Auto-generated method stub

	}

	@Override
	void transformAndLights() {
		// TODO Auto-generated method stub

	}

	@Override
	void updatePost() {
		// TODO Auto-generated method stub

	}
}
