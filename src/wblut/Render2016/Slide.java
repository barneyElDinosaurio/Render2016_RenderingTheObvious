package wblut.Render2016;

import processing.core.PApplet;
import processing.core.PConstants;
import wblut.geom.WB_GeometryFactory;
import wblut.processing.WB_Render3D;

public abstract class Slide {
	RTO home;
	String title;
	int slideCounter;
	protected boolean drawhud;
	WB_Render3D render;
	WB_GeometryFactory gf;

	public Slide(final RTO home, final String title) {
		this.home = home;
		this.title = title;
		slideCounter = 0;
		drawhud = true;
		render = home.render;
		gf = home.gf;
	}

	abstract void setup();

	public final void draw() {
		updatePre();

		home.pushStyle();
		{
			home.blendMode(PApplet.BLEND);
			{
				home.hint(PConstants.DISABLE_DEPTH_TEST);
				home.noLights();
				home.cam.beginHUD();
				backgroundDraw();
				home.cam.endHUD();
				home.hint(PConstants.ENABLE_DEPTH_TEST);
				home.pushMatrix();
				transformAndLights();
				normalDraw();
			}
			home.blendMode(PApplet.ADD);
			{
				home.hint(PConstants.DISABLE_DEPTH_TEST);
				glowDraw();
				home.popMatrix();
			}
		}
		home.popStyle();

		if (drawhud) {
			home.blendMode(PApplet.BLEND);
			home.hint(PConstants.ENABLE_DEPTH_TEST);
			home.pushStyle();
			{
				home.noLights();
				home.cam.beginHUD();
				hudDraw();
				home.cam.endHUD();
			}
			home.popStyle();
		}

		home.noTint();
		updatePost();
		slideCounter++;
		final float passed = 0.001f * (home.millis() - home.start);
		final float fraction = (passed / 2400.0f) * home.width;
		home.cam.beginHUD();
		home.pushStyle();
		home.noStroke();
		home.fill(200, 100);
		home.rect(0, home.height, fraction, -2);
		home.popStyle();
		home.cam.endHUD();
	}

	abstract void updatePre();

	abstract void backgroundDraw();

	abstract void transformAndLights();

	abstract void normalDraw();

	abstract void glowDraw();

	public void hudDraw() {
		home.textAlign(PConstants.LEFT);
		home.pushStyle();
		home.fill(255);
		home.textFont(home.fontsansxpt, home.captionfont);
		home.text(title, home.textpadding, home.height - home.textpadding);
		home.popStyle();
	}

	abstract void updatePost();

	abstract void shutdown();

	void keyPressed() {

	}

	void mousePressed() {

	}
}
