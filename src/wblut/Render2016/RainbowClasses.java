package wblut.Render2016;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import wblut.geom.WB_Circle;
import wblut.geom.WB_Coord;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_IntersectionResult;
import wblut.geom.WB_Point;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Triangle;
import wblut.geom.WB_Vector;
import wblut.processing.WB_Render3D;

public class RainbowClasses {
	static WB_GeometryFactory gf = WB_GeometryFactory.instance();

	public static class Ball extends WB_Circle implements OpticalObject {
		Ball(final WB_Coord p1, final double r) {
			super(p1, r);
		}

		@Override
		public void draw(final PApplet home) {

			home.ellipse(getCenter().xf(), getCenter().yf(), 2 * (float) getRadius(), 2 * (float) getRadius());
		}

		@Override
		public Intersection getIntersection(final WB_Ray ray) {
			final ArrayList<WB_Point> I = WB_GeometryOp.getIntersection2D(ray, this);
			WB_Point closest = null;
			if (I.size() == 0) {
				return null;
			} else if (I.size() == 1) {
				closest = I.get(0);
			} else {

				closest = I.get(0);
				final double dc = closest.getDistance2D(ray.getOrigin());
				if (I.get(1).getDistance2D(ray.getOrigin()) < dc) {
					closest = I.get(1);
				}
			}

			final WB_Vector n = WB_Vector.subToVector3D(getCenter(), closest);
			n.normalizeSelf();

			return new Intersection(closest, n);
		}

		@Override
		public boolean contains(final WB_Coord p) {
			return WB_Point.getDistance2D(getCenter(), p) <= getRadius();
		}
	}

	public static class Intersection {
		WB_Point p;
		WB_Vector n;

		Intersection(final WB_Coord p, final WB_Coord n) {
			this.p = new WB_Point(p);
			this.n = new WB_Vector(n);
		}
	}

	public static class LightRay {
		WB_Ray ray;
		double I;
		WB_Point endpoint;
		int depth;

		LightRay(final WB_Ray ray, final double I, final int depth) {
			this.ray = ray;
			this.I = I;
			endpoint = null;
			this.depth = depth;
		}

		void draw(final WB_Render3D render) {
			if (endpoint == null) {
				render.drawRay(ray, 4000);
			} else {
				render.drawSegment(ray.getOrigin(), endpoint);
			}
		}
	}

	public static interface OpticalObject {
		Intersection getIntersection(WB_Ray ray);

		boolean contains(WB_Coord p);

		void draw(PApplet home);
	}

	public static class Prism extends WB_Triangle implements OpticalObject {
		Prism(final WB_Coord p1, final WB_Coord p2, final WB_Coord p3) {
			super(p1, p2, p3);
		}

		@Override
		public void draw(final PApplet home) {
			home.beginShape();
			home.vertex(p1().xf(), p1().yf());
			home.vertex(p2().xf(), p2().yf());
			home.vertex(p3().xf(), p3().yf());
			home.endShape(PConstants.CLOSE);
		}

		@Override
		public Intersection getIntersection(final WB_Ray ray) {
			final WB_Point p12 = getIntersection2D(ray, p1(), p2());
			final WB_Point p23 = getIntersection2D(ray, p2(), p3());
			final WB_Point p31 = getIntersection2D(ray, p3(), p1());

			if ((p12 == null) && (p23 == null) && (p31 == null)) {
				return null;
			} else {
				WB_Point closest = null;
				double d = 0;
				int side = -1;
				if (p12 != null) {
					closest = p12;
					d = closest.getDistance2D(ray.getOrigin());
					side = 1;
				}
				if (p23 != null) {
					if (closest == null) {
						closest = p23;
						side = 2;
						d = closest.getDistance2D(ray.getOrigin());
					} else {
						final double dc = p23.getDistance2D(ray.getOrigin());
						if (dc < d) {
							closest = p23;
							side = 2;
							d = dc;
						}
					}
				}
				if (p31 != null) {
					if (closest == null) {
						closest = p31;
						side = 3;
						d = closest.getDistance2D(ray.getOrigin());
					} else {
						final double dc = p31.getDistance2D(ray.getOrigin());
						if (dc < d) {
							closest = p31;
							side = 3;
							d = dc;
						}
					}
				}
				WB_Vector n = null;
				if (side == 1) {
					final WB_Vector v = WB_Vector.subToVector3D(p1(), p2());
					n = gf.createNormalizedPerpendicularVector(v.xf(), v.yf());
				} else if (side == 2) {
					final WB_Vector v = WB_Vector.subToVector3D(p2(), p3());
					n = gf.createNormalizedPerpendicularVector(v.xf(), v.yf());
				} else if (side == 3) {
					final WB_Vector v = WB_Vector.subToVector3D(p3(), p1());
					n = gf.createNormalizedPerpendicularVector(v.xf(), v.yf());
				}
				return new Intersection(closest, n);
			}
		}

		@Override
		public boolean contains(final WB_Coord p) {
			return WB_GeometryOp.contains(p, this);
		}

	}

	public static class RR {
		LightRay in;
		LightRay refl;
		LightRay refr;
		double anglei;
		double angler;
		WB_Point p;
		WB_Vector n;
		OpticalObject OO;
		double ignoreFactor;

		RR(final LightRay in, final ArrayList<OpticalObject> OOS, final double nair, final double nmedium,
				final double ignoreFactor) {
			this.in = in;
			this.ignoreFactor = ignoreFactor;
			OpticalObject first = null;
			double cd = 10000000;
			Intersection CI = null;
			for (final OpticalObject testOO : OOS) {
				final Intersection I = testOO.getIntersection(in.ray);
				if (I == null) {
					continue;
				} else if (CI == null) {
					CI = I;
					cd = CI.p.getDistance2D(in.ray.getOrigin());
					first = testOO;
				} else {
					final double d = I.p.getDistance2D(in.ray.getOrigin());
					if (d < cd) {
						CI = I;
						cd = d;
						first = testOO;
					}
				}
			}

			OO = first;
			if (CI == null) {
				refl = null;
				refr = null;
				anglei = 0;
				angler = 0;
				p = null;
				n = null;
			} else {
				p = CI.p;
				n = CI.n;
				in.endpoint = p;
				if (!first.contains(in.ray.getOrigin())) {
					createRays(p, n, nair, nmedium);
				} else {
					n.mulSelf(-1);
					createRays(p, n, nmedium, nair);
				}
			}

		}

		void createRays(final WB_Point p, final WB_Vector n, final double ni, final double nr) {
			if (n.isParallelNorm(in.ray.getDirection())) {
				anglei = 0;
				angler = 0;
				refr = new LightRay(gf.createRayWithDirection(p.addMul(0.001, n), in.ray.getDirection()), in.I,
						in.depth + 1);
				refl = null;
			} else {
				final WB_Vector axis = n.cross(in.ray.getDirection());
				final WB_Vector nrefl = gf.createVector(n);
				nrefl.mulSelf(-1);
				final double dn = WB_Vector.dot(in.ray.getDirection(), nrefl);
				final WB_Vector nstar = gf.createVector(n);
				nstar.rotateAboutAxisSelf(Math.PI * 0.5, p, axis);
				nstar.normalizeSelf();

				final double sini = WB_Vector.dot(in.ray.getDirection(), nstar);
				final double sinr = (ni * sini) / nr;
				final double cosi = WB_Vector.dot(in.ray.getDirection(), n);
				anglei = Math.acos(cosi);
				angler = Math.asin(sinr);
				if (Double.isNaN(angler)) {
					refl = new LightRay(gf.createRayWithDirection(p.addMul(0.001, nrefl),
							WB_Vector.addMul(in.ray.getDirection(), -2 * dn, nrefl)), in.I, in.depth + 1);

					refr = null;
				} else {
					final double cosr = Math.cos(angler);
					double Rs = ((ni * cosi) - (nr * cosr)) / ((ni * cosi) + (nr * cosr));
					Rs *= Rs;
					double Rp = ((ni * cosr) - (nr * cosi)) / ((ni * cosr) + (nr * cosi));
					Rp *= Rp;
					final double R = 0.5 * (Rs + Rp);
					final double Ts = 1 - Rs;
					final double Tp = 1 - Rp;
					final double T = 0.5 * (Ts + Tp);
					refl = new LightRay(
							gf.createRayWithDirection(p.addMul(0.001, nrefl),
									WB_Vector.addMul(in.ray.getDirection(), -2 * dn, nrefl)),
							(ignoreFactor * in.I) + ((1.0 - ignoreFactor) * in.I * R), in.depth + 1);

					refr = new LightRay(gf.createRayWithDirection(p.addMul(0.001, n), n.mulAddMul(cosr, sinr, nstar)),
							(ignoreFactor * in.I) + ((1.0 - ignoreFactor) * in.I * T), in.depth + 1);
				}
			}
		}
	}

	static WB_Point getIntersection2D(final WB_Ray ray, final WB_Coord p0, final WB_Coord p1) {
		final WB_Segment segray = new WB_Segment(ray.getOrigin(), ray.getPointOnLine(4000));
		final WB_Segment side = new WB_Segment(p0, p1);
		final WB_IntersectionResult ir = WB_GeometryOp.getIntersection2D(segray, side);
		if (ir.intersection == false) {
			return null;
		}
		return (WB_Point) ir.object;
	}

	static double indexSellMeier(final double wavelength) {
		final double wl = wavelength * 0.001; // nm to µm
		final double wl2 = wl * wl;
		final double B1 = 0.6961663;
		final double B2 = 0.4079426;
		final double B3 = 0.8974794;
		double C1 = 0.0684043;
		C1 *= C1;
		double C2 = 0.1162414;
		C2 *= C2;
		double C3 = 9.896161;
		C3 *= C3;
		return Math.sqrt(1 + ((B1 * wl2) / (wl2 - C1)) + ((B2 * wl2) / (wl2 - C2)) + ((B3 * wl2) / (wl2 - C3)));
	}
}
