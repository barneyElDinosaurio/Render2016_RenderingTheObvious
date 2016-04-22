package wblut.Render2016;

import java.util.List;

import processing.core.PApplet;
import processing.core.PImage;
import wblut.geom.WB_Point;

public class RTOGlobals {
	public static List<WB_Point> globalpoints;
	public static PImage[] img;

	public static void setupintro(final PApplet home) {
		img = new PImage[48];
		for (int i = 1; i < 49; i++) {
			img[i - 1] = home.loadImage("data/images/Anomaly 2-HD " + String.format("%02d", i) + ".jpg");
		}

	}
}
