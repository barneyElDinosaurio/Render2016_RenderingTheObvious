package wblut.Render2016;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import wblut.geom.WB_Point;
import wblut.hemesh.HEC_FromFacelist;
import wblut.hemesh.HE_Mesh;

public class Human extends Slide {

	HE_Mesh mesh;

	int[][] targetIndex;
	float[][][] dv;
	float[][] origPos;
	float[][] currentPos;
	float[] origNoise;
	PImage img2, img3;

	public Human(final RTO home, final String title) {
		super(home, title);

	}

	@Override
	void setup() {
		importOBJ(home.sketchPath("/data/makehuman/base.obj"));
		origPos = mesh.getVerticesAsFloat();
		origNoise = new float[origPos.length];
		dv = new float[4][][];
		targetIndex = new int[4][];
		importTarget(home.sketchPath("/data/makehuman/african-male-old.target"), 1);
		importTarget(home.sketchPath("/data/makehuman/female-young-minmuscle-maxweight-averagecup-minfirmness.target"),
				2);
		importTarget(home.sketchPath("/data/makehuman/caucasian-male-baby.target"), 3);
		create(0.0f, 0, 1);
		img2 = home.loadImage("/data/images/CT030006.jpeg");

		img3 = home.loadImage("/data/images/CT040042.jpeg");
	}

	void create(final float phase, final int offset, final int steps) {
		final float noisescale = 0.03f;
		for (int i = offset; i < origPos.length; i += steps) {
			origNoise[i] = PApplet.constrain(-0.5f + (2 * home.noise(noisescale * (origPos[i][0] + phase),
					noisescale * (origPos[i][1] + phase), noisescale * (origPos[i][2] + phase))), 0, 1);
		}
		if (currentPos == null) {
			currentPos = new float[origPos.length][3];
		}
		for (int i = offset; i < origPos.length; i += steps) {
			currentPos[i][0] = origPos[i][0];
			currentPos[i][1] = origPos[i][1];
			currentPos[i][2] = origPos[i][2];
		}
		final float c = 1.0f - (0.01f * slideCounter);
		if (c > 0) {
			for (int i = offset; i < targetIndex[3].length; i += steps) {
				final int n = targetIndex[3][i];

				currentPos[n][0] += c * dv[3][i][0];
				currentPos[n][1] += c * dv[3][i][1];
				currentPos[n][2] += c * dv[3][i][2];
			}
		}
		float exag = 1.25f;
		for (int i = offset; i < targetIndex[1].length; i += steps) {
			final int n = targetIndex[1][i];
			final float f = exag * ((origNoise[n] < 0.5) ? 1 - (2 * origNoise[n]) : 0);// *origNoise[n]*(1-origNoise[n]);
			currentPos[n][0] += f * dv[1][i][0];
			currentPos[n][1] += f * dv[1][i][1];
			currentPos[n][2] += f * dv[1][i][2];
		}
		exag = 1.1f;
		for (int i = offset; i < targetIndex[2].length; i += steps) {
			final int n = targetIndex[2][i];
			final float f = exag * ((origNoise[n] <= 0.5) ? 2 * origNoise[n] : 2 - (2 * origNoise[n]));// (1.0-origNoise[n]);
			currentPos[n][0] += f * dv[2][i][0];
			currentPos[n][1] += f * dv[2][i][1];
			currentPos[n][2] += f * dv[2][i][2];
		}
		exag = 1.25f;
		for (int i = offset; i < targetIndex[3].length; i += steps) {
			final int n = targetIndex[3][i];
			final float f = exag * ((origNoise[n] > 0.5) ? (2 * origNoise[n]) - 1 : 0);// 2*origNoise[n]*(1-origNoise[n]);

			currentPos[n][0] += f * dv[3][i][0];
			currentPos[n][1] += f * dv[3][i][1];
			currentPos[n][2] += f * dv[3][i][2];
		}
		/*
		 * exag =0.2f;
		 *
		 * for (int i = offset; i < targetIndex[0].length; i += steps) { final
		 * int n = targetIndex[0][i]; final float f = exag * ((origNoise[n] <=
		 * 0.5) ? 2 * origNoise[n] : 2 - (2 * origNoise[n]));//
		 * (1.0-origNoise[n]); currentPos[n][0] += f * dv[0][i][0];
		 * currentPos[n][1] += f * dv[0][i][1]; currentPos[n][2] += f *
		 * dv[0][i][2]; }
		 */
		mesh.setVerticesFromFloat(currentPos);
	}

	@Override
	void normalDraw() {
		home.background(20);
		home.hint(PConstants.DISABLE_DEPTH_TEST);
		home.noLights();
		home.imageMode(PConstants.CENTER);
		float f = 1.4f;
		final int offset = 120;
		home.image(img2, -700, offset, ((f * home.height) / img2.height) * img2.width, f * home.height);
		// blendMode(ADD);
		home.image(img3, 700, offset, ((f * home.height) / img3.height) * img3.width, f * home.height);
		home.hint(PConstants.DISABLE_DEPTH_TEST);
		home.hint(PConstants.ENABLE_DEPTH_TEST);
		home.blendMode(PConstants.BLEND);
		home.directionalLight(255, 255, 255, 1, 1, -1);
		home.directionalLight(127, 127, 127, -1, -1, 1);
		home.rotateY(slideCounter * 0.004f);
		home.noStroke();
		home.fill(255);
		f = 1.6f;
		home.pushMatrix();
		home.translate(0, 0, 100);
		home.scale(f);
		home.scale(32, -32, 32);
		if (mesh != null) {
			render.drawFaces(mesh);
		}
		home.popMatrix();
		home.pushMatrix();
		home.translate(0, 0, -100);
		home.scale(f);
		home.scale(32, -32, -32);
		if (mesh != null) {
			render.drawFaces(mesh);
		}
		home.popMatrix();
		create(slideCounter * 0.250f, slideCounter % 4, 4);
	}

	@Override
	void glowDraw() {
		// TODO Auto-generated method stub

	}

	@Override
	void shutdown() {
		home.colorMode(PConstants.RGB);
		mesh = null;
		render = null;

		targetIndex = null;
		dv = null;
		origPos = null;
		currentPos = null;
		origNoise = null;
	}

	// import an OBJ file
	void importOBJ(final String path) {

		// ArrayLists to store the vertex and face data, we keep track of the
		// face count
		final ArrayList<WB_Point> vertexList = new ArrayList<WB_Point>();
		final ArrayList<int[]> faceList = new ArrayList<int[]>();
		int faceCount = 0;
		int stopper = -1;
		// load OBJ file as an array of strings
		final String objStrings[] = home.loadStrings(path);
		for (int i = 0; i < objStrings.length; i++) {

			// split every line in parts divided by spaces
			final String[] parts = PApplet.splitTokens(objStrings[i]);

			// the first part indicates the kind of data that is in that line
			// v stands for vertex data
			if (parts[0].equals("v")) {
				final float x1 = Float.parseFloat(parts[1]);
				final float y1 = Float.parseFloat(parts[2]);
				final float z1 = Float.parseFloat(parts[3]);
				final WB_Point pointLoc = new WB_Point(x1, y1, z1);
				vertexList.add(pointLoc);
			}

			if (parts[0].equals("g")) {
				stopper++;
				if (stopper > 0) {
					break;
				}
			}

			// f stands for facelist data
			// should work for non triangular faces
			if (parts[0].equals("f")) {
				final int[] tempFace = new int[parts.length - 1];
				for (int j = 0; j < (parts.length - 1); j++) {
					final String[] num = PApplet.split(parts[j + 1], '/');
					tempFace[j] = Integer.parseInt(num[0]) - 1;
				}
				faceList.add(tempFace);
				faceCount++;
			}
		}

		// the HEC_FromFacelist wants the face data as int[][]
		final int[][] faceArray = new int[faceCount][];
		for (int i = 0; i < faceCount; i++) {
			final int[] tempFace = faceList.get(i);
			faceArray[i] = tempFace;
		}
		// et voila... add to the creator
		final HEC_FromFacelist creator = new HEC_FromFacelist();
		creator.setVertices(vertexList);
		creator.setFaces(faceArray);
		creator.setDuplicate(true);
		creator.setCheckNormals(false);
		creator.setCleanUnused(false);
		mesh = new HE_Mesh(creator);
	}

	void importTarget(final String path, final int id) {
		final String objStrings[] = home.loadStrings(path);
		int offset = 0;
		for (int i = 0; i < objStrings.length; i++) {
			if (objStrings[i].substring(0, 1).equals("#")) {
				offset++;
			} else {
				break;
			}
		}
		targetIndex[id] = new int[objStrings.length - offset];
		dv[id] = new float[objStrings.length - offset][3];
		for (int i = 0; i < (objStrings.length - offset); i++) {
			final String[] parts = PApplet.splitTokens(objStrings[i + offset]);
			targetIndex[id][i] = Integer.parseInt(parts[0]);
			dv[id][i][0] = Float.parseFloat(parts[1]);
			dv[id][i][1] = Float.parseFloat(parts[2]);
			dv[id][i][2] = Float.parseFloat(parts[3]);
		}
	}

	@Override
	void updatePre() {
		// TODO Auto-generated method stub

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
