package me.longluo.raytracing.chapter12;

public class Sphere extends Hitable {
    Vec3 center;
    double radius;
    Material mat;

    public Sphere() {
    }

    public Sphere(Vec3 center, float radius, Material mat) {
        this.center = center;
        this.radius = radius;
        this.mat = mat;
    }

    /**
     * 判断与球体是否有撞击
     *
     * @param r     光线
     * @param t_min 范围
     * @param t_max 范围
     * @param rec   撞击点
     * @return 是否有撞击
     */
    @Override
    public boolean hit(Ray r, double t_min, double t_max, HitRecord rec) {
        Vec3 oc = r.origin().Subtract(center);
        double a = r.direction().dot(r.direction());
        double b = 2.0f * oc.dot(r.direction());
        double c = oc.dot(oc) - radius * radius;
        double discriminant = b * b - 4.0f * a * c;
        if (discriminant > 0) {
            //优先选取符合范围的根较小的撞击点，若没有再选取另一个根
            double discFactor = Math.sqrt(discriminant);
            double temp = (-b - discFactor) / (2.0f * a);
            if (temp < t_max && temp > t_min) {
                rec.t = temp;
                rec.p = r.point_at_parameter(rec.t);
                rec.normal = (rec.p.Subtract(center)).Scale(1.0f / radius);
                rec.matPtr = mat;
                return true;
            }
            temp = (-b + discFactor) / (2.0f * a);
            if (temp < t_max && temp > t_min) {
                rec.t = temp;
                rec.p = r.point_at_parameter(rec.t);
                rec.normal = (rec.p.Subtract(center)).Scale(1.0f / radius);
                rec.matPtr = mat;
                return true;
            }
        }
        return false;
    }

}