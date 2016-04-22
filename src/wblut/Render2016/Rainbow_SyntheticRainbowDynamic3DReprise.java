package wblut.Render2016;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;
import wblut.hemesh.HEC_Grid;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render3D;

public class Rainbow_SyntheticRainbowDynamic3DReprise extends Slide {

	WB_GeometryFactory gf = WB_GeometryFactory.instance();
	WB_Render3D render;
	int numdrops;
	int phase = 0;
	WB_Point eye, drop;
	PImage img;
	double sunangle;
	double psunangle;
	HE_Mesh ground, grid1, grid2;

	public Rainbow_SyntheticRainbowDynamic3DReprise(final RTO home, final String title) {
		super(home, title);
		drawhud = true;
	}

	@Override
	public void setup() {
		home.background(0);
		home.smooth(8);
		render = new WB_Render3D(home);
		home.noFill();
		numdrops = 2000;

		eye = new WB_Point(700, 400);
		drop = new WB_Point();
		sunangle = -1;
		home.strokeWeight(1.0f);

		ground = new HE_Mesh(new HEC_Grid(20, 30, 2000, 3000).setCenter(-250, 400, 0).setZAxis(0, 1, 0));
		grid1 = new HE_Mesh(new HEC_Grid(20, 12, 2000, 1200).setCenter(-250, -200, -1500).setZAxis(0, 0, 1));
		grid2 = new HE_Mesh(new HEC_Grid(30, 12, 3000, 1200).setCenter(-1250, -200, 0).setZAxis(1, 0, 0));

	}

	@Override
	void normalDraw() {

		psunangle = sunangle;
		sunangle = PApplet.map(home.mouseY, 0, home.height, 60, 0);

		if ((psunangle != sunangle)) {
			home.background(0);

		}
		home.cam.beginHUD();
		home.translate(400, 0);
		home.rotateY(PConstants.PI / 3);

		home.translate(home.width / 2, home.height / 2);
		// if (slideCounter == 1) {
		home.fill(110);
		home.stroke(255);

		if ((psunangle != sunangle)) {
			home.ellipse(eye.xf(), eye.yf(), 20, 20);

			home.stroke(255, 200);
			render.drawEdges(ground);
			render.drawEdges(grid1);

			home.pushMatrix();
			home.translate(0, 400);
			home.rotateZ(-PApplet.radians((float) sunangle));
			for (int j = 0; j < 5; j++) {
				home.pushMatrix();
				home.translate(0, -(j * 50));
				home.line((home.width / 2) - 150, 0, home.width / 2, 0);
				home.line((home.width / 2) - 150, 0, (home.width / 2) - 125, 10);
				home.line((home.width / 2) - 150, 0, (home.width / 2) - 125, -10);
				home.popMatrix();
			}
			home.popMatrix();
		}
		final WB_Vector sun = new WB_Vector(1, 0, 0);
		sun.rotateAboutAxisSelf(-PApplet.radians((float) sunangle), 0, 0, 0, 0, 0, 1);
		for (int i = 0; i < numdrops; i++) {
			drop.set(home.random((-home.width / 4) - 10, (-home.width / 4) + 10), home.random(-800, 400),
					home.random(-1500, 1500));

			final WB_Vector v = new WB_Vector(drop, eye);
			int index = (int) (2 * PApplet.degrees((float) sun.getAngle(v)));
			if (index < 0) {
				index += 720;
			}
			if (index >= 720) {
				index -= 720;
			}
			final double r = home.red[index];
			final double g = home.green[index];
			final double b = home.blue[index];
			home.stroke((int) r, (int) g, (int) b, 160);
			home.fill((int) r, (int) g, (int) b, 160);
			home.pushMatrix();
			home.translate(drop.xf(), drop.yf(), drop.zf());
			home.ellipse(0, 0, 4, 4);
			home.popMatrix();

		}

		home.cam.endHUD();
	}

	@Override
	void glowDraw() {

	}

	@Override
	void shutdown() {

	}

	@Override
	void keyPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	void mousePressed() {
		phase++;

	}

	@Override
	public void hudDraw() {
		if ((psunangle != sunangle)) {
			home.noLights();
			home.textAlign(PConstants.LEFT);
			home.cam.beginHUD();
			home.pushStyle();
			home.fill(255);
			home.textFont(home.fontsansxpt, home.smallfont);
			home.text(title, home.textpadding, home.height - home.textpadding);
			home.popStyle();
			home.cam.endHUD();
		}
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
