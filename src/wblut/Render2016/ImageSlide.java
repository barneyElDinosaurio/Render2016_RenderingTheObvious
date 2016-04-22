package wblut.Render2016;

import processing.core.PConstants;
import processing.core.PImage;

public class ImageSlide extends Slide {

	PImage img = null;

	public ImageSlide(final RTO home, final PImage img) {
		super(home, "");
		if (img != null) {
			this.img = img;
		}

	}

	@Override
	public void setup() {
		home.fill(0);
	}

	@Override
	public void updatePre() {

	}

	@Override
	void backgroundDraw() {
		home.background(20);
		if (img != null) {
			home.imageMode(PConstants.CORNER);

			home.image(img, 0, 0, home.width, (int) ((img.height * home.width) / (float) img.width), 0, 0, img.width,
					img.height);
		}

	}

	@Override
	void transformAndLights() {

	}

	@Override
	void normalDraw() {

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

}
