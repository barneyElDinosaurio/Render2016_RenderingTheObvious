package wblut.Render2016;

import java.util.Arrays;

import processing.core.PConstants;

public class Quote extends Slide {
	String[] lines = null;
	int offset;
	String name;

	public Quote(final RTO home, final String name, final String... lines) {
		super(home, "");
		this.lines = Arrays.copyOf(lines, lines.length);
		this.name = name;
		offset = 80;
	};

	public Quote(final RTO home, final int offset, final String name, final String... lines) {
		super(home, "");
		this.lines = Arrays.copyOf(lines, lines.length);
		this.name = name;
		this.offset = offset;
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
		home.fill(200);
		float m = 0;
		for (int i = 0; i < lines.length; i++) {
			home.text(lines[i], home.width / 2, ((home.height / 2) - offset) + m);
			m += 2.6f * home.smallfont;
		}
		m += 1.4f * home.smallfont;
		home.fill(200, 60, 60);
		home.text(name, home.width / 2, ((home.height / 2) - offset) + m);
	}

	@Override
	public void updatePost() {
	}

	@Override
	void shutdown() {

	}
}
