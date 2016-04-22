package wblut.Render2016;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Voronoi;
import wblut.geom.WB_VoronoiCell2D;
import wblut.hemesh.HEC_Polygon;
import wblut.hemesh.HE_Mesh;

public class AnimTitleSlide extends Slide {

	PImage img = null;
	String subtitle;
	List<WB_Polygon> text;
	WB_AABB AABB;
	List<WB_Point> points;
	List<WB_VoronoiCell2D> voronoiXY;
	WB_Polygon boundary;
	List<HE_Mesh> columns;

	public AnimTitleSlide(final RTO home, final String title, final String subtitle, final PImage img) {
		super(home, title);
		this.subtitle = subtitle;
		if (img != null) {
			this.img = img;
		}
	}

	public AnimTitleSlide(final RTO home, final String title, final String subtitle) {
		super(home, title);
		this.subtitle = subtitle;
		img = null;

	}

	@Override
	public void setup() {
		home.cam.setDistance(935, 500);
		text = gf.createTextWithTTFFont(title, home.sketchPath("data/fonts/SourceSansPro-Light.ttf"), 0, home.bigfont,
				1);// text;
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
		points = new ArrayList<WB_Point>();
		for (final WB_Polygon poly : text) {
			for (int i = 0; i < poly.getNumberOfPoints(); i++) {
				points.add(poly.getPoint(i));
			}
		}
		createAABB();
		points.add(new WB_Point(-home.width / 2, -home.height / 2).addSelf(AABB.getCenter()));
		points.add(new WB_Point(-home.width / 2, home.height / 2).addSelf(AABB.getCenter()));
		points.add(new WB_Point(home.width / 2, home.height / 2).addSelf(AABB.getCenter()));
		points.add(new WB_Point(home.width / 2, -home.height / 2).addSelf(AABB.getCenter()));

		voronoiXY = WB_Voronoi.getClippedVoronoi2D(points);
		columns = new ArrayList<HE_Mesh>();
		for (final WB_VoronoiCell2D vor : voronoiXY) {
			columns.add(new HE_Mesh(new HEC_Polygon(vor.getPolygon(), home.random(-100, 100))));
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

	}

	@Override
	void glowDraw() {
		home.scale(1 + (0.001f * slideCounter), -1 - (0.001f * slideCounter));

		home.translate(-AABB.getCenter().xf(), -AABB.getCenter().yf() + 60);
		home.pushMatrix();
		home.rotateX(slideCounter * 0.001f);
		home.noFill();

		home.strokeWeight(1);
		home.stroke(255, 255, 0, PApplet.min(slideCounter * 0.2f, 50));
		for (final HE_Mesh mesh : columns) {
			home.pushMatrix();
			home.translate(0, 0, mesh.getCenter().zf() * PApplet.min(6, 0.002f * slideCounter)
					* PApplet.sin((slideCounter * 0.004f) + (mesh.getCenter().xf() * 0.008f)));
			render.drawEdges(mesh);
			home.popMatrix();
		}
		home.rotateX(slideCounter * 0.001f);

		home.stroke(255, 0, 255, PApplet.min(slideCounter * 0.2f, 50));
		for (final HE_Mesh mesh : columns) {
			home.pushMatrix();
			home.translate(0, 0, mesh.getCenter().zf() * PApplet.min(6, 0.002f * slideCounter)
					* PApplet.sin((slideCounter * 0.004f) + (mesh.getCenter().xf() * 0.008f)));
			render.drawEdges(mesh);
			home.popMatrix();
		}
		home.rotateX(slideCounter * 0.001f);

		home.stroke(0, 255, 255, PApplet.min(slideCounter * 0.2f, 50));
		for (final HE_Mesh mesh : columns) {
			home.pushMatrix();
			home.translate(0, 0, mesh.getCenter().zf() * PApplet.min(6, 0.002f * slideCounter)
					* PApplet.sin((slideCounter * 0.004f) + (mesh.getCenter().xf() * 0.008f)));
			render.drawEdges(mesh);
			home.popMatrix();
		}
		home.popMatrix();
	}

	@Override
	public void hudDraw() {
		if (slideCounter < 256) {
			home.fill(200, 60, 60, 255 - slideCounter);
			home.stroke(120);
			home.textFont(home.fontsans, home.bigfont);
			home.textAlign(PConstants.CENTER);
			home.text(title, home.width / 2, (home.height / 2) + home.titleoffset);
			home.fill(200, 255 - slideCounter);
			home.textFont(home.fontsans, home.mediumfont);
			home.text(subtitle, home.width / 2, (home.height / 2) + home.titleoffset + home.bigfont);
		}
	}

	@Override
	public void updatePost() {

	}

	@Override
	void shutdown() {

	}

	void createAABB() {
		AABB = new WB_AABB();
		for (final WB_Polygon poly : text) {
			for (int i = 0; i < poly.getNumberOfShellPoints(); i++) {
				AABB.expandToInclude(poly.getPoint(i));
			}
		}
	}

}
