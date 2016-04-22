package wblut.Render2016;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import wblut.geom.WB_GeometryFactory;
import wblut.processing.WB_Render3D;

public class RTO extends PApplet {

	private static final long serialVersionUID = 6692961872219764092L;
	public ArrayList<Slide> slides;
	public Slide currentSlide;
	public Slide previousSlide = null;
	public int currentId;

	public PeasyCam cam;
	float CURRENTPITCH, CURRENTYAW, CURRENTROLL;
	float TARGETPITCH, TARGETYAW, TARGETROLL;
	float CURRENTZOOM, TARGETZOOM;
	float CAMCOUNTER, CAMCOUNTERMAX;
	int viewpoint;
	public float minz, maxz, avgz, rangez, invrangez;

	PFont fontsans;
	PFont fontserif;
	PFont fontsanspt;
	PFont fontsansxpt;
	PFont fontserifpt;
	int bigfont, mediumfont, smallfont, textpadding, titleoffset, captionfont;
	double[] red, blue, green;
	int imgcounter;
	WB_Render3D render;
	WB_GeometryFactory gf = WB_GeometryFactory.instance();
	float start;

	public static void main(final String args[]) {
		final int primary_display = 0; // index into Graphic Devices array...
		int primary_width;
		final GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final GraphicsDevice devices[] = environment.getScreenDevices();
		String location;
		if (devices.length > 1) { // we have a 2nd display/projector
			primary_width = devices[0].getDisplayMode().getWidth();
			location = "--location=" + primary_width + ",0";
		} else {// leave on primary display
			location = "--location=0,0";
		}
		final String display = "--display=" + primary_display + 1; // processing
		PApplet.main(new String[] { location, "--present", "--hide-stop", display, "wblut.Render2016.RTO" });
	}

	@Override
	public void settings() {
		// size(1920, 1080, "processing.opengl.PGraphics3D");
		fullScreen(PApplet.P3D);
		smooth(8);
	}

	@Override
	public void setup() {
		render = new WB_Render3D(this);
		RTOGlobals.setupintro(this);
		CURRENTZOOM = TARGETZOOM = 935;
		cam = new PeasyCam(this, 0, 0, 0, CURRENTZOOM);
		cam.setMinimumDistance(50);
		cam.setMaximumDistance(4000);
		cam.setActive(false);
		noCursor();
		createFonts();
		buildDeck();

		frameRate(30);
		start = millis();
	}

	void createFonts() {
		bigfont = 80;
		mediumfont = 60;
		smallfont = 24;
		captionfont = 36;
		textpadding = 32;
		titleoffset = -40;
		fontsans = createFont("fonts/SourceSansPro-Light.ttf", bigfont);
		fontsanspt = createFont("fonts/SourceSansPro-Light.ttf", 2 * smallfont);
		fontsansxpt = createFont("fonts/SourceSansPro-Light.ttf", smallfont);
		fontserif = createFont("fonts/DroidSerif.ttf", 2 * bigfont);
		fontserifpt = createFont("fonts/DroidSerif.ttf", 2 * smallfont);
	}

	void buildDeck() {
		slides = new ArrayList<Slide>();

		slides.add(new TitleSlide(this, "Rendering the obvious", "Frederik Vanhoutte"));
		slides.add(new AnimTitleSlide(this, "Rendering the obvious", "Frederik Vanhoutte"));
		slides.add(new Disclaimer(this, ""));
		slides.add(new Human(this, ""));
		slides.add(new PoorBeethoven(this, ""));
		slides.add(new RecurVor(this, ""));
		slides.add(new TitleSlide(this, "The thing about the rainbow", ""));
		slides.add(new ImageSlide(this, this.loadImage("images/rainbowHD.jpg")));
		slides.add(new Rainbow_BasicPrism(this, ""));
		slides.add(new Quote(this, 360, "John Keats, 1884", "Lamia", "", "Do not all charms fly",
				"At the mere touch of cold philosophy?", "There was an awful rainbow once in heaven:",
				"We know her woof, her texture; she is given", "In the dull catalogue of common things.",
				"Philosophy will clip an Angel's wings,", "Conquer all mysteries by rule and line,",
				"Empty the haunted air, and gnomèd mine—", "Unweave a rainbow, as it erewhile made",
				"The tender-person\'d Lamia melt into a shade."));
		slides.add(new Rainbow_BasicPrism(this, ""));
		slides.add(new Rainbow_BasicSphereStatic(this, ""));
		slides.add(new Rainbow_BasicSphereDynamic(this, ""));
		slides.add(new Rainbow_PrismWithReflection(this, ""));
		slides.add(new MultiText(this, "Boxes",
				new String[] { "Discrete states, classifications, species, entities,...",
						"are mere artificial constructs we impose on reality.",
						"Process and continuity are key, not state and label." }));
		slides.add(new Closer(this, ""));
		slides.add(new Rainbow_SphereWithReflection(this, ""));
		slides.add(new Rainbow_SphereWithReflectionDynamic(this, ""));
		slides.add(new Rainbow_SphereWithReflectionSweetspot(this, ""));
		slides.add(new Rainbow_SphereFull(this, ""));
		slides.add(new Rainbow_SphereSource(this, ""));
		slides.add(new Rainbow_SphereSourceEye(this, ""));
		slides.add(new Rainbow_SyntheticRainbowDynamic3D(this, ""));
		slides.add(new ImageSlide(this, this.loadImage("images/rainbow.jpg")));
		slides.add(
				new MultiText(this, "Models",
						new String[] { "We need models to understand, to solve, to predict.",
								"But in the end, any understanding, any solution, any prediction",
								"is about the model." }));
		slides.add(new Rainbow_SyntheticRainbowDynamic3DReprise(this, ""));
		slides.add(new Rainbow_SyntheticRainbowDynamicCanyon3D(this, ""));
		slides.add(new ImageSlide(this, this.loadImage("images/fullrainbow.jpg")));
		slides.add(new ImageSlide(this, this.loadImage("images/rainbowHD.jpg")));
		slides.add(new AnimTitleSlide(this, "The prism and the rainbow", "Why it matters"));
		slides.add(new Quote(this, 100, "Antoine de Saint-Exupéry",
				"If you want to build a ship, don’t drum up the men and women",
				"to gather wood, divide the work, and give orders.",
				"Instead, teach them to yearn for the vast and endless sea."));
		slides.add(new Quote(this, 360, "Antoine de Saint-Exupéry", "Celui-là tissera des toiles,",
				"l’autre dans la forêt par l’éclair", "de sa hache couchera l’arbre. L’autre,",
				"encore, forgera des clous, et il en sera quelque", "part qui observeront les étoiles afin d’apprendre",
				"à gouverner. Et tous cependant ne seront qu’un.", "Créer le navire, ce n’est point tisser les toiles,",
				"forger les clous, lire les astres,mais bien donner", "le goût de la mer qui est un, et à la lumière",
				"duquel il n’est plus rien qui soit", "contradictoire mais communauté", "dans l’amour."));
		slides.add(new Rainbow_PrismSpielerei(this, ""));
		slides.add(new EndSlide(this));
		currentSlide = slides.get(0);
		currentSlide.setup();
	}

	@Override
	public void draw() {
		currentSlide.draw();
	}

	@Override
	public void mousePressed() {
		currentSlide.mousePressed();
	}

	@Override
	public void keyPressed() {
		if (key == ' ') {
			if (currentId < (slides.size() - 1)) {
				previousSlide = currentSlide;
				currentId++;
				currentSlide = slides.get(currentId);
				currentSlide.slideCounter = 0;
				currentSlide.setup();

			}

		} else if (key == PConstants.BACKSPACE) {
			if (currentId > 0) {
				previousSlide = currentSlide;
				currentId--;
				currentSlide = slides.get(currentId);
				currentSlide.slideCounter = 0;
				currentSlide.setup();
				previousSlide.shutdown();
			}
		}

		else {
			currentSlide.keyPressed();
		}
	}

	@Override
	public void keyReleased() {

	}

}
