package wblut.Render2016;

import java.util.Iterator;
import java.util.List;

import javolution.util.FastTable;
import processing.core.PApplet;
import processing.core.PConstants;

public class Closer extends Slide {
	List<square> squares = new FastTable<square>();
	float weightFactor;
	float maximumRatio;
	float zoomFactor;
	int resetCount;
	boolean bw;
	boolean constrained;
	float constantRatio;
	boolean rotating;
	boolean grid;
	float angle;
	float currentRotationSpeed;
	float targetRotationSpeed;
	float zoomCenterX;
	float zoomCenterY;
	int alfa;
	String options;
	String parameters;
	int rbias, gbias, bbias, rrange, grange, brange, rrbias, grbias, brbias;

	public Closer(final RTO home, final String title) {
		super(home, title);

	}

	@Override
	public void setup() {
		home.colorMode(PConstants.RGB);
		angle = 0.0f;
		currentRotationSpeed = 0.004f;
		targetRotationSpeed = home.random(-0.002f, 0.002f);
		options = "0011";
		parameters = "994020202802028a8080143218";
		setOptions(options);
		setParameters(parameters);
		zoomFactor = 1.002f;
		zoomCenterX = 0.0f;
		zoomCenterY = 0.0f;
		resetCount = (int) (PApplet.log(2.0f) / PApplet.log(zoomFactor));
		final square s = new square(-home.width / 2, -home.width / 2, home.width / 2, home.width / 2, rbias, gbias,
				bbias, 1.0f);
		squares.add(s);
		home.frameRate(30);

	}

	void setOptions(final String options) {
		bw = PApplet.unbinary(options.substring(0, 1)) > 0; // t/f
		constrained = PApplet.unbinary(options.substring(1, 2)) > 0;// t/f
		rotating = PApplet.unbinary(options.substring(2, 3)) > 0;// t/f
		grid = PApplet.unbinary(options.substring(3, 4)) > 0;// t/f
	}

	void setParameters(final String parameters) {
		alfa = PApplet.constrain(PApplet.unhex(parameters.substring(0, 2)), 1, 255);// 1-255
		rbias = PApplet.unhex(parameters.substring(2, 4));// 0-255
		gbias = PApplet.unhex(parameters.substring(4, 6));// 0-255
		bbias = PApplet.unhex(parameters.substring(6, 8));// 0-255
		rrange = PApplet.unhex(parameters.substring(8, 10));// 0-255
		grange = PApplet.unhex(parameters.substring(10, 12));// 0-255
		brange = PApplet.unhex(parameters.substring(12, 14));// 0-255
		rrbias = PApplet.unhex(parameters.substring(14, 16)) - 128;// -128-128
		grbias = PApplet.unhex(parameters.substring(16, 18)) - 128;// -128-128
		brbias = PApplet.unhex(parameters.substring(18, 20)) - 128;// -128-128
		weightFactor = PApplet.constrain(PApplet.unhex(parameters.substring(20, 22)) / 10.0f, 0.1f, 20.f);// 0.1f-20.0f
		// (0-255)/10.0f
		constantRatio = PApplet.constrain(PApplet.unhex(parameters.substring(22, 24)) / 100.0f, 0.01f, 0.99f);// 0.01f-0.99f
		// (0-255)/100.0f
		maximumRatio = PApplet.unhex(parameters.substring(24, 26));// 0-255
	}

	@Override
	void normalDraw() {

		if (grid) {
			home.stroke(0, alfa);
		} else {
			home.noStroke();
		}
		for (final square s : squares) {
			s.draw(bw, alfa);
		}
		divide(constrained);
		divide(constrained);
		zoomCenterX = (0.9f * zoomCenterX) + (0.1f * (home.mouseX - (home.width / 2)));
		zoomCenterY = (0.9f * zoomCenterY) + (0.1f * (home.mouseY - (home.height / 2)));
		zoomSquares(zoomCenterX, zoomCenterY, zoomFactor);
		cullSquares();
		if ((slideCounter % resetCount) == 0) {
			reweightSquares();
		}
	}

	@Override
	void glowDraw() {
	}

	void divide(final boolean constrained) {
		final float wf = (constrained) ? ((home.random(100.0f) < 50.0f) ? constantRatio : 1.0f - constantRatio)
				: home.random(0.2f, 0.8f);
		final float hf = (constrained) ? ((home.random(100.0f) < 50.0f) ? constantRatio : 1.0f - constantRatio)
				: home.random(0.2f, 0.8f);

		final int selectedSquare = selectSquare();
		final square s = squares.get(selectedSquare);
		final float r = s.right;
		final float t = s.top;
		final float l = s.left;
		final float b = s.bottom;
		final float mr = l + ((r - l) * wf);
		final float mt = b + ((t - b) * hf);
		final int ccr = s.cr;
		final int ccg = s.cg;
		final int ccb = s.cb;
		final float w = PApplet.constrain(s.weight / weightFactor, 0.0f, 1e10f);

		if (((r - l) / (t - b)) > maximumRatio) {
			squares.set(selectedSquare,
					new square(l, t, mr, b,
							PApplet.constrain(ccr + (int) home.random(-rrange + rrbias, rrange + rrbias), 0, 255),
							PApplet.constrain(ccg + (int) home.random(-grange + grbias, grange + grbias), 0, 255),
							PApplet.constrain(ccb + (int) home.random(-brange + brbias, brange + brbias), 0, 255), w));
			squares.add(new square(mr, t, r, b,
					PApplet.constrain(ccr + (int) home.random(-rrange + rrbias, rrange + rrbias), 0, 255),
					PApplet.constrain(ccg + (int) home.random(-grange + grbias, grange + grbias), 0, 255),
					PApplet.constrain(ccb + (int) home.random(-brange + brbias, brange + brbias), 0, 255), w));
		} else if (((t - b) / (r - l)) > maximumRatio) {
			squares.set(selectedSquare,
					new square(l, t, r, mt,
							PApplet.constrain(ccr + (int) home.random(-rrange + rrbias, rrange + rrbias), 0, 255),
							PApplet.constrain(ccg + (int) home.random(-grange + grbias, grange + grbias), 0, 255),
							PApplet.constrain(ccb + (int) home.random(-brange + brbias, brange + brbias), 0, 255), w));
			squares.add(new square(l, mt, r, b,
					PApplet.constrain(ccr + (int) home.random(-rrange + rrbias, rrange + rrbias), 0, 255),
					PApplet.constrain(ccg + (int) home.random(-grange + grbias, grange + grbias), 0, 255),
					PApplet.constrain(ccb + (int) home.random(-brange + brbias, brange + brbias), 0, 255), w));
		} else {
			squares.set(selectedSquare,
					new square(l, t, mr, mt,
							PApplet.constrain(ccr + (int) home.random(-rrange + rrbias, rrange + rrbias), 0, 255),
							PApplet.constrain(ccg + (int) home.random(-grange + grbias, grange + grbias), 0, 255),
							PApplet.constrain(ccb + (int) home.random(-brange + brbias, brange + brbias), 0, 255), w));
			squares.add(new square(mr, t, r, mt,
					PApplet.constrain(ccr + (int) home.random(-rrange + rrbias, rrange + rrbias), 0, 255),
					PApplet.constrain(ccg + (int) home.random(-grange + grbias, grange + grbias), 0, 255),
					PApplet.constrain(ccb + (int) home.random(-brange + brbias, brange + brbias), 0, 255), w));
			squares.add(new square(l, mt, mr, b,
					PApplet.constrain(ccr + (int) home.random(-rrange + rrbias, rrange + rrbias), 0, 255),
					PApplet.constrain(ccg + (int) home.random(-grange + grbias, grange + grbias), 0, 255),
					PApplet.constrain(ccb + (int) home.random(-brange + brbias, brange + brbias), 0, 255), w));
			squares.add(new square(mr, mt, r, b,
					PApplet.constrain(ccr + (int) home.random(-rrange + rrbias, rrange + rrbias), 0, 255),
					PApplet.constrain(ccg + (int) home.random(-grange + grbias, grange + grbias), 0, 255),
					PApplet.constrain(ccb + (int) home.random(-brange + brbias, brange + brbias), 0, 255), w));
		}
	}

	int selectSquare() {
		float totalWeight = 0.0f;
		for (final square s : squares) {
			totalWeight += s.weight;
		}
		final float selection = home.random(totalWeight);
		float cumulativeWeight = 0.0f;
		int selectedSquare = 0;
		for (final square s : squares) {

			cumulativeWeight += s.weight;
			if (cumulativeWeight <= selection) {
				selectedSquare++;
			} else {
				return selectedSquare;

			}

		}
		return (selectedSquare);
	}

	void cullSquares() {

		final Iterator<square> sitr = squares.iterator();

		while (sitr.hasNext()) {

			if (!visible(sitr.next())) {
				sitr.remove();
			}
		}
	}

	void zoomSquares(final float centerX, final float centerY, final float factor) {
		for (final square s : squares) {
			s.left = zoom(s.left, centerX, factor);
			s.right = zoom(s.right, centerX, factor);
			s.top = zoom(s.top, centerY, factor);
			s.bottom = zoom(s.bottom, centerY, factor);
		}
	}

	void reweightSquares() {
		for (final square s : squares) {

			s.weight *= weightFactor;

		}
	}

	boolean visible(final square s) {
		final float tmp = PApplet.max(home.width, home.height);
		return ((s.right > (-tmp / 1.4)) && (s.left < (tmp / 1.4)) && (s.top > (-tmp / 1.4))
				&& (s.bottom < (tmp / 1.4)));

	}

	float zoom(final float value, final float center, final float factor) {
		return (center + ((value - center) * factor));
	}

	@Override
	void shutdown() {

	}

	class square {
		float left;
		float top;
		float right;
		float bottom;
		int cr, cg, cb;
		float weight;

		square() {
			left = right = top = bottom = 0.0f;
			cr = cg = cb = 0;
			weight = 1.0f;

		}

		square(final float l, final float t, final float r, final float b, final int ccr, final int ccg, final int ccb,
				final float w) {
			left = PApplet.min(l, r);
			right = PApplet.max(l, r);
			bottom = PApplet.min(t, b);
			top = PApplet.max(t, b);
			cr = ccr;
			cg = ccg;
			cb = ccb;
			weight = w;
		}

		void draw(final boolean bw, final int alfa) {

			if (bw) {
				home.fill(cr, cr, cr, alfa);
			} else {

				home.fill(cr, cg, cb, alfa);
			}
			home.rect(left, bottom, right - left, top - bottom);
		}

	}

	@Override
	public void hudDraw() {
		if (slideCounter < 512) {
			home.noLights();
			home.textAlign(PConstants.LEFT);
			home.cam.beginHUD();
			home.pushStyle();
			home.fill(255, 256 - (slideCounter / 2));
			home.textFont(home.fontsanspt, 36);
			home.text(title, 25, home.height - 25);
			home.popStyle();
			home.cam.endHUD();
		}
	}

	@Override
	void updatePre() {
		currentRotationSpeed = (0.99f * currentRotationSpeed) + (0.01f * targetRotationSpeed);
		if ((PApplet.abs(currentRotationSpeed) > (0.99f * PApplet.abs(targetRotationSpeed)))
				&& (PApplet.abs(currentRotationSpeed) < (1.01f * PApplet.abs(targetRotationSpeed)))) {
			targetRotationSpeed = home.random(-0.002f, 0.002f);

		}
		if (rotating) {
			home.rotate(angle += currentRotationSpeed);
		}

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

	}

}
