package wblut.Render2016;

import processing.core.PConstants;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_Point;
import wblut.geom.WB_RandomOnSphere;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Vector;
import wblut.hemesh.HEC_Beethoven;
import wblut.hemesh.HEM_TriSplit;
import wblut.hemesh.HES_CatmullClark;
import wblut.hemesh.HES_TriDec;
import wblut.hemesh.HET_MeshOp;
import wblut.hemesh.HE_FaceIntersection;
import wblut.hemesh.HE_Mesh;

public class PoorBeethoven extends Slide {
	HE_Mesh mesh;
	WB_AABBTree tree;
	WB_RandomOnSphere rnds;
	WB_Ray randomRay;
	WB_Vector bias;
	boolean growing;
	int counter;
	float extrusion;
	int pause;
	int shots;
	int shotsPerFrame;
	int smooth;
	float spread;
	float distance;
	float rotation;

	public PoorBeethoven(final RTO home, final String title) {
		super(home, title);
		drawhud = false;
	}

	@Override
	void setup() {
		home.colorMode(PConstants.RGB);
		rnds = new WB_RandomOnSphere();
		createMesh();

		bias = rnds.nextVector();
		while (bias.zd() < 0.1) {
			bias = rnds.nextVector();
		}
		home.cam.setDistance(750, 1800);
		extrusion = 80.0f;
		shots = 600;
		shotsPerFrame = 3;
		spread = 0.2f;
		distance = 500;
		rotation = (float) (Math.PI / 180.0);

		pause = 50;
	}

	void createMesh() {
		final HEC_Beethoven creator = new HEC_Beethoven();
		creator.setScale(5).setZAngle((2 * Math.PI) / 3).setZAxis(new WB_Point(0, -1, 0));
		mesh = new HE_Mesh(creator);
		mesh.move(0, 20, 0);
		mesh.simplify(new HES_TriDec().setGoal(0.5));
		tree = new WB_AABBTree(mesh, 32);
		growing = false;
		counter = 0;

		slideCounter = 0;
	}

	@Override
	void updatePre() {

	}

	@Override
	void backgroundDraw() {
		home.background(20);

	}

	@Override
	void transformAndLights() {
		home.directionalLight(255, 255, 255, 1, 1, -1);
		home.directionalLight(127, 127, 127, -1, -1, 1);
		if (slideCounter < 240) {
			home.rotateY((-slideCounter * PConstants.PI) / 120.0f);
		}

		if (slideCounter == 480) {
			mousePressed();
		}
	}

	@Override
	void normalDraw() {
		home.hint(PConstants.DISABLE_DEPTH_TEST);
		home.noLights();
		home.fill(255);
		home.noStroke();
		home.pushMatrix();
		home.scale(1.7f);
		render.drawFaces(mesh);
		home.popMatrix();
		home.hint(PConstants.ENABLE_DEPTH_TEST);
		home.directionalLight(255, 255, 255, 1, 1, -1);
		home.directionalLight(127, 127, 127, -1, -1, 1);
		home.scale(1.6f);
		home.fill(255);
		home.noStroke();
		home.render.drawFaces(mesh);
		home.noFill();
		home.stroke(0, 50);
		render.drawEdges(mesh);
		if (slideCounter == pause) {
			growing = true;
		}
		if (growing) {
			for (int i = 0; i < shotsPerFrame; i++) {
				grow();
				counter++;
			}
		}
	}

	@Override
	void glowDraw() {

	}

	@Override
	public void updatePost() {

		if (counter == shots) {
			mesh.subdivide(new HES_CatmullClark());
			growing = false;
			counter++;
		}
	}

	@Override
	void shutdown() {
		mesh = null;
		tree = null;
		rnds = null;
		randomRay = null;
		bias = null;

	}

	void grow() {
		randomRay = new WB_Ray(new WB_Point(bias).mulSelf(-distance),
				bias.add(home.random(-spread, spread), home.random(-spread, spread), home.random(-spread, spread)));
		/*
		 * final WB_Transform T = new WB_Transform().addRotateAboutAxis(rotation
		 * * counter, WB_Point.ORIGIN(),
		 *
		 * WB_Vector.Y()); randomRay = new
		 * WB_Ray(T.applyAsPoint(randomRay.getOrigin()),
		 * T.applyAsVector(randomRay.getDirection()));
		 */
		final HE_FaceIntersection fi = HET_MeshOp.getFurthestIntersection(tree, randomRay);
		WB_Point point;
		if (fi != null) {
			point = fi.point;
			point.addMulSelf(extrusion, randomRay.getDirection());
			HEM_TriSplit.splitFaceTri(mesh, fi.face, point);
			tree = new WB_AABBTree(mesh, 32);
			home.stroke(255, 0, 0);
			render.drawRay(randomRay, 5000);
		}
	}

	@Override
	void mousePressed() {
		createMesh();
		slideCounter = (int) (pause * 0.8);
		bias = rnds.nextVector();
		while (bias.zd() < 0.1) {
			bias = rnds.nextVector();
		}
	}

}
