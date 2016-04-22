package wblut.Render2016;

import processing.core.PConstants;

public class Disclaimer extends Slide {

	int frame;
	int turn;

	public Disclaimer(final RTO home, final String title) {
		super(home, title);

	}

	@Override
	public void setup() {

		frame = 0;
	}

	@Override
	void normalDraw() {
		home.background(20);

	}

	@Override
	void glowDraw() {
		home.noLights();
		home.cam.beginHUD();
		home.translate(home.width / 2, home.height / 2);
		home.imageMode(PConstants.CENTER);
		home.scale(1.3f);
		home.tint(255, 255, 0, 100);
		home.image(RTOGlobals.img[frame], 0, 0);
		home.translate(10, 5);
		home.tint(255, 0, 255, 100);
		home.image(RTOGlobals.img[(frame + 1) % 48], 0, 0);
		home.translate(-20, -10);
		home.tint(0, 255, 255, 100);
		home.image(RTOGlobals.img[(frame + 2) % 48], 0, 0);

		home.cam.endHUD();
	}

	@Override
	void shutdown() {

	}

	@Override
	public void hudDraw() {

	}

	@Override
	void updatePre() {

		frame = ((slideCounter / 3) % 48);

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
