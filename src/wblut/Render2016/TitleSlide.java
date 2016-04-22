package wblut.Render2016;

import processing.core.PConstants;
import processing.core.PImage;

public class TitleSlide extends Slide {

	PImage img = null;
	String subtitle;

	public TitleSlide(final RTO home, final String title, final String subtitle, final PImage img) {
		super(home, title);
		this.subtitle = subtitle;
		if (img != null) {
			this.img = img;
		}
	}

	public TitleSlide(final RTO home, final String title, final String subtitle) {
		super(home, title);
		this.subtitle = subtitle;
		img = null;

	}

	@Override
	public void setup() {
		home.colorMode(PConstants.RGB);
		home.fill(0);
	}

	@Override
	public void updatePre() {

	}

	@Override
	void backgroundDraw() {
		home.background(20);
		home.noLights();
		if (img != null) {
			home.imageMode(PConstants.CORNER);
			home.image(img, 0, 0, home.width, home.height, 0, 0, img.width, img.height);
		}
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

	}

	@Override
	public void hudDraw() {
		home.fill(200, 60, 60);
		home.stroke(120);
		home.textFont(home.fontsans, home.bigfont);
		home.textAlign(PConstants.CENTER);
		home.text(title, home.width / 2, (home.height / 2) + home.titleoffset);
		home.fill(200);
		home.textFont(home.fontsans, home.mediumfont);
		home.text(subtitle, home.width / 2, (home.height / 2) + home.titleoffset + home.bigfont);
	}

	@Override
	public void updatePost() {

	}

	@Override
	void shutdown() {

	}

}
