package wblut.Render2016;

import java.util.ArrayList;

import processing.core.PApplet;
import wblut.Render2016.RainbowClasses.Ball;
import wblut.Render2016.RainbowClasses.LightRay;
import wblut.Render2016.RainbowClasses.OpticalObject;
import wblut.Render2016.RainbowClasses.Prism;
import wblut.Render2016.RainbowClasses.RR;
import wblut.core.WB_Color;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

public class Rainbow_PrismSpielerei extends Slide {
	LightRay startray;
	ArrayList<LightRay> rays;
	ArrayList<RR> events;
	int maxdepth;
	int alpha;
	int numrays;
	boolean ignoreI;
	double baseI;
	Prism prism, prism2, prism3;
	Ball ball;
	ArrayList<OpticalObject> OOS;
	boolean reflection;

	public Rainbow_PrismSpielerei(final RTO home, final String title) {
		super(home, title);
		drawhud = false;

	}

	@Override
	void setup() {
		maxdepth = 10;
		alpha = 1;
		numrays = 1000;
		ignoreI = true;
		reflection = true;
		baseI = 10;
		prism = new Prism(new WB_Point(0, 250, 0), new WB_Point(600, 250, 0), new WB_Point(300, -269.615, 0));
		prism2 = new Prism(new WB_Point(0, -69.615, 0), new WB_Point(-600, -69.615, 0), new WB_Point(-300, 450, 0));
		prism3 = new Prism(new WB_Point(100, 250, 0), new WB_Point(700, 250, 0), new WB_Point(400, -269.615, 0));
		ball = new Ball(new WB_Point(-300, -10, 0), 250);
		OOS = new ArrayList<OpticalObject>();
		OOS.add(prism);
		OOS.add(ball);
		rays = new ArrayList<LightRay>();
		events = new ArrayList<RR>();
		home.noFill();

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

	}

	@Override
	void glowDraw() {
		home.strokeWeight(2);

		traceRays(1.455, home.color(255, 0, 0), ignoreI, baseI, OOS);
		traceRays(1.460, home.color(0, 255, 0), ignoreI, baseI, OOS);
		traceRays(1.468, home.color(0, 0, 255), ignoreI, baseI, OOS);

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

	void traceRays(final double wavelength, final boolean ignoreI, final double baseI,
			final ArrayList<OpticalObject> OOS) {

		final double index = RainbowClasses.indexSellMeier(wavelength);
		final int c = WB_Color.spectralColor(wavelength);
		traceRays(index, c, ignoreI, baseI, OOS);
	}

	void traceRays(final double index, final int c, final boolean ignoreI, final double baseI,
			final ArrayList<OpticalObject> OOS) {
		for (int i = 0; i < numrays; i++) {
			final WB_Point origin = new WB_Point(2000, -100 + (0.005 * i), 0);
			final WB_Vector direction = new WB_Vector(-1, -0.05, 0);
			startray = new LightRay(gf.createRayWithDirection(origin, direction), baseI, 0);
			rays = new ArrayList<LightRay>();
			rays.add(startray);
			events = new ArrayList<RR>();
			do {
				final ArrayList<LightRay> newrays = new ArrayList<LightRay>();
				for (final LightRay ray : rays) {
					final RR event = new RR(ray, OOS, 1.0, index, 0);
					if (event.p != null) {
						if ((event.refr != null) && (event.refr.depth <= maxdepth)) {
							newrays.add(event.refr);
						}
						if ((event.refl != null) && (event.refl.depth <= maxdepth) && reflection) {
							newrays.add(event.refl);
						}
					}
					events.add(event);
				}
				rays = newrays;
			} while (rays.size() > 0);

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

	int IC(final int c, final float I, final float alpha) {

		return home.color(PApplet.constrain(home.red(c) * I, 0, 255), PApplet.constrain(home.green(c) * I, 0, 255),
				PApplet.constrain(home.blue(c) * I, 0, 255), alpha);
	}

}
