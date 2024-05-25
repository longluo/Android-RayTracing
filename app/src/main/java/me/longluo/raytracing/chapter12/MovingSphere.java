package me.longluo.raytracing.chapter12;

public class MovingSphere extends Hitable {

    Vec3 center0, center1;

    double radius;

    Material mat;

    double time0, time1;

    public MovingSphere() {
    }

    public MovingSphere(Vec3 center0, Vec3 center1, double time0, double time1, double radius, Material mat) {
        this.center0 = center0;
        this.center1 = center1;
        this.radius = radius;
        this.mat = mat;
        this.time0 = time0;
        this.time1 = time1;
    }

    @Override
    public boolean hit(Ray r, double t_min, double t_max, HitRecord rec) {
        Vec3 oc = r.origin().Subtract(center(r.time));  //球心都修改成和时间相关的位置
        double a = r.direction().dot(r.direction());
        double b = oc.dot(r.direction());
        double c = oc.dot(oc) - radius * radius;
        double discriminant = b * b - a * c;
        if (discriminant > 0) {
            //优先选取符合范围的根较小的撞击点，若没有再选取另一个根
            double discFactor = (float) Math.sqrt(discriminant);
            double temp = (-b - discFactor) / (2.0f * a);
            if (temp < t_max && temp > t_min) {
                rec.t = temp;
                rec.p = r.point_at_parameter(rec.t);
                rec.normal = (rec.p.Subtract(center(r.time))).Scale(1.0f / radius);
                rec.matPtr = mat;
                return true;
            }
            temp = (-b + discFactor) / (2.0f * a);
            if (temp < t_max && temp > t_min) {
                rec.t = temp;
                rec.p = r.point_at_parameter(rec.t);
                rec.normal = (rec.p.Subtract(center(r.time))).Scale(1.0f / radius);
                rec.matPtr = mat;
                return true;
            }
        }
        return false;
    }

    private Vec3 center(double t) {
        return center0.Add(center1.Subtract(center0).Scale((t - time0) / (time1 - time0)));
    }
}
