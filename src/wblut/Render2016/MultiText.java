package wblut.Render2016;

import java.util.Arrays;

import processing.core.PConstants;

public class MultiText extends Slide {
	String[] lines = null;
	int offset;

	public MultiText(final RTO home, final int offset, final String title, final String... lines) {
		super(home, title);
		this.lines = Arrays.copyOf(lines, lines.length);
		this.offset = offset;
	};

	public MultiText(final RTO home, final String title, final String... lines) {
		super(home, title);
		this.lines = Arrays.copyOf(lines, lines.length);
		offset = 80;
	};

	@Override
	void setup() {
		home.fill(0);
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

	}

	@Override
	void normalDraw() {

	}

	@Override
	void glowDraw() {
	}

	@Override
	public void hudDraw() {
		home.textFont(home.fontsans, 1.8f * home.smallfont);
		home.textAlign(PConstants.CENTER);
		home.fill(200, 60, 60);
		home.text(title, home.width / 2, (home.height / 2) - offset);
		home.fill(200);
		float m = 2.6f * home.smallfont;
		for (int i = 0; i < lines.length; i++) {
			home.text(lines[i], home.width / 2, ((home.height / 2) - 80) + m);
			m += 2.6f * home.smallfont;
		}
	}

	@Override
	void shutdown() {

	}

	@Override
	public void updatePost() {

	}
}
