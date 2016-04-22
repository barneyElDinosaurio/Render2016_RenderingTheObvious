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

public class Rainbow_SphereSourceEye extends Slide {

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
	float eyeAngle;

	public Rainbow_SphereSourceEye(final RTO home, final String title) {
		super(home, title);
		drawhud = true;
	}

	@Override
	void setup() {
		maxdepth = 8;
		alpha = 10;
		numrays = 500;
		ignoreI = false;
		ignoreFactor = 0;
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
		eyeAngle = 270;
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
		eyeAngle++;
		int i = (int) (eyeAngle);
		while (i < 0) {
			i += 720;
		}
		while (i >= 720) {
			i -= 720;
		}
		home.fill((float) home.red[i], (float) home.green[i], (float) home.blue[i]);

		final WB_Point eye = new WB_Point(
				ball.getCenter().xf() + (450.0f * PApplet.cos((eyeAngle / 360.0f) * PConstants.PI)),
				ball.getCenter().yf() + (450.0f * PApplet.sin((eyeAngle / 360.0f) * PConstants.PI)));
		home.stroke((float) home.red[i], (float) home.green[i], (float) home.blue[i]);
		home.pushMatrix();
		home.translate(ball.getCenter().xf(), ball.getCenter().yf());
		home.rotateZ((eyeAngle / 360.0f) * PConstants.PI);
		home.stroke(255);
		home.line(210, 0, 390, 0);
		home.line(210, 0, 260, 20);
		home.line(210, 0, 260, -20);
		home.popMatrix();

		home.ellipse(eye.xf(), eye.yf(), 100, 100);
		home.ellipse(0, 0, 160, 160);
		home.noFill();
		home.stroke(255, 0, 0);
		WB_Point p;
		home.beginShape();
		for (i = 0; i < 720; i++) {
			p = gf.createPointFromPolar(210 + home.red[i], PApplet.radians(i * 0.5f));
			home.vertex(p.xf(), p.yf());
		}
		home.endShape(PConstants.CLOSE);
		home.stroke(0, 0, 255);
		home.beginShape();
		for (i = 0; i < 720; i++) {
			p = gf.createPointFromPolar(210 + home.blue[i], PApplet.radians(i * 0.5f));
			home.vertex(p.xf(), p.yf());
		}
		home.endShape(PConstants.CLOSE);

		home.stroke(0, 255, 0);
		home.beginShape();
		for (i = 0; i < 720; i++) {
			p = gf.createPointFromPolar(210 + home.green[i], PApplet.radians(i * 0.5f));
			home.vertex(p.xf(), p.yf());
		}
		home.endShape(PConstants.CLOSE);
		home.strokeWeight(2.0f);
		home.stroke(255);
		home.ellipse(ball.getCenter().xf(), ball.getCenter().yf(), 400, 400);

		home.pushMatrix();
		home.translate(ball.getCenter().xf(), ball.getCenter().yf());
		home.noStroke();
		for (int a = 0; a < Math.min(slideCounter, 720); a++) {
			home.pushMatrix();
			home.rotateZ(((a + 270) / 360.0f) * PConstants.PI);
			home.fill((float) home.red[(a + 270) % 720], (float) home.green[(a + 270) % 720],
					(float) home.blue[(a + 270) % 720]);
			home.rect(90, -2, 100, 4);
			home.popMatrix();
		}
		home.popMatrix();
	}

	@Override
	void glowDraw() {
		home.strokeWeight(2);

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

	int IC(final int c, final float I, final float alpha) {

		return home.color(PApplet.constrain(home.red(c) * I, 0, 255), PApplet.constrain(home.green(c) * I, 0, 255),
				PApplet.constrain(home.blue(c) * I, 0, 255), alpha);
	}

}
