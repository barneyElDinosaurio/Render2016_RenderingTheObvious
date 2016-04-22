package wblut.Render2016;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import wblut.Render2016.RainbowClasses.Ball;
import wblut.Render2016.RainbowClasses.LightRay;
import wblut.Render2016.RainbowClasses.OpticalObject;
import wblut.Render2016.RainbowClasses.Prism;
import wblut.Render2016.RainbowClasses.RR;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

public class Rainbow_SphereSource extends Slide {
	LightRay startray;
	ArrayList<LightRay> rays;
	ArrayList<RR> events;
	int maxdepth;
	int alpha;
	int numrays;
	boolean ignoreI;
	double ignoreFactor;
	double baseI;
	Prism prism, prism2, prism3;
	Ball ball;
	ArrayList<OpticalObject> OOS;
	boolean reflection;

	public Rainbow_SphereSource(final RTO home, final String title) {
		super(home, title);
		drawhud = true;
	}

	@Override
	void setup() {
		maxdepth = 5;
		alpha = 10;
		numrays = 10000;
		ignoreI = false;
		ignoreFactor = 0.0;
		baseI = 10;
		prism = new Prism(new WB_Point(-300, 250, 0), new WB_Point(300, 250, 0), new WB_Point(0, -269.615, 0));
		prism2 = new Prism(new WB_Point(-700, 250, 0), new WB_Point(-100, 250, 0), new WB_Point(-400, -269.615, 0));
		prism3 = new Prism(new WB_Point(100, 250, 0), new WB_Point(700, 250, 0), new WB_Point(400, -269.615, 0));
		ball = new Ball(new WB_Point(0, 0, 0), 200);
		OOS = new ArrayList<OpticalObject>();
		OOS.add(ball);
		rays = new ArrayList<LightRay>();
		events = new ArrayList<RR>();
		home.noFill();
		reflection = true;
		home.red = traceRays(1.3307, home.color(255, 0, 0), ignoreI, baseI, OOS, true);
		home.green = traceRays(1.3334, home.color(0, 255, 0), ignoreI, baseI, OOS, true);
		home.blue = traceRays(1.3435, home.color(0, 0, 255), ignoreI, baseI, OOS, true);
		numrays = 500;
		ignoreFactor = 1.0;
	}

	@Override
	void updatePre() {

	}

	@Override
	void backgroundDraw() {
		home.background(0);

	}

	@Override
	void transformAndLights() {

	}

	@Override
	void normalDraw() {
		home.noFill();
		home.stroke(255, 0, 0);
		WB_Point p;
		home.beginShape();
		for (int i = 0; i < 720; i++) {
			p = gf.createPointFromPolar(210 + home.red[i], PApplet.radians(i * 0.5f));
			// q = gf.createPointFromPolar(210, home.radians(i * 0.5f));
			// home.line(q.xf(), q.yf(), p.xf(), p.yf());
			home.vertex(p.xf(), p.yf());
		}
		home.endShape(PConstants.CLOSE);
		home.stroke(0, 0, 255);
		home.beginShape();
		for (int i = 0; i < 720; i++) {
			p = gf.createPointFromPolar(210 + home.blue[i], PApplet.radians(i * 0.5f));
			// q = gf.createPointFromPolar(210, home.radians(i * 0.5f));
			// home.line(q.xf(), q.yf(), p.xf(), p.yf());
			home.vertex(p.xf(), p.yf());
		}
		home.endShape(PConstants.CLOSE);

		home.stroke(0, 255, 0);
		home.beginShape();
		for (int i = 0; i < 720; i++) {
			p = gf.createPointFromPolar(210 + home.green[i], PApplet.radians(i * 0.5f));
			// q = gf.createPointFromPolar(210, home.radians(i * 0.5f));
			// home.line(q.xf(), q.yf(), p.xf(), p.yf());
			home.vertex(p.xf(), p.yf());
		}
		home.endShape(PConstants.CLOSE);
		home.strokeWeight(2.0f);
		home.stroke(255);
		home.ellipse(ball.getCenter().xf(), ball.getCenter().yf(), 400, 400);

	}

	@Override
	void glowDraw() {
		home.strokeWeight(2);

		traceRays(1.3307, home.color(255, 0, 0), ignoreI, baseI, OOS, false);
		traceRays(1.3334, home.color(0, 255, 0), ignoreI, baseI, OOS, false);
		traceRays(1.3435, home.color(0, 0, 255), ignoreI, baseI, OOS, false);

		home.stroke(255);
		home.noFill();
		for (final OpticalObject OO : OOS) {
			OO.draw(home);
		}
	}

	@Override
	public void updatePost() {

	}

	@Override
	void shutdown() {

	}

	@Override
	void mousePressed() {

	}

	double[] traceRays(final double index, final int c, final boolean ignoreI, final double baseI,
			final ArrayList<OpticalObject> OOS, final boolean noDraw) {
		final double[] result = new double[720];
		for (int i = 0; i < numrays; i++) {
			startray = new LightRay(
					gf.createRayWithDirection(2000, -200.0 + (i * (400.0 / (numrays + 1))), 0, -1, 0, 0), baseI, 0);
			rays = new ArrayList<LightRay>();
			rays.add(startray);
			events = new ArrayList<RR>();
			do {
				final ArrayList<LightRay> newrays = new ArrayList<LightRay>();
				for (final LightRay ray : rays) {
					final RR event = new RR(ray, OOS, 1.0, index, ignoreFactor);
					if (event.p != null) {
						if ((event.refr != null) && (event.refr.depth <= maxdepth)) {
							newrays.add(event.refr);
						}
						if ((event.refl != null) && (event.refl.depth <= maxdepth) && reflection) {
							newrays.add(event.refl);
						}
					} else {

						float angle = PApplet.degrees((float) WB_Vector.getHeading2D(ray.ray.getDirection()));
						while (angle < 0) {
							angle += 360;
						}
						while (angle >= 360) {
							angle -= 360;
						}
						result[(int) (angle * 2)] += ray.I;

					}
					events.add(event);
				}
				rays = newrays;
			} while (rays.size() > 0);
			if (!noDraw) {
				for (final RR event : events) {
					if (event.in.depth == 0) {
						if (ignoreI) {
							home.stroke(home.red(c), home.green(c), home.blue(c), alpha);
						} else {
							home.stroke(IC(c, (float) event.in.I, alpha));
						}
						event.in.draw(render);
					}
					if ((event.refr != null) && (event.refr.depth < maxdepth)) {
						if (ignoreI) {
							home.stroke(home.red(c), home.green(c), home.blue(c), alpha);
						} else {
							home.stroke(IC(c, (float) event.refr.I, alpha));
						}

						event.refr.draw(render);
					}

					if ((event.refl != null) && (event.refl.depth < maxdepth) && reflection) {
						if (ignoreI) {
							home.stroke(home.red(c), home.green(c), home.blue(c), alpha);
						} else {
							home.stroke(IC(c, (float) event.refl.I, alpha));
						}

						event.refl.draw(render);
					}
				}
			}
		}
		return result;
	}

	int IC(final int c, final float I, final float alpha) {

		return home.color(PApplet.constrain(home.red(c) * I, 0, 255), PApplet.constrain(home.green(c) * I, 0, 255),
				PApplet.constrain(home.blue(c) * I, 0, 255), alpha);
	}

}
