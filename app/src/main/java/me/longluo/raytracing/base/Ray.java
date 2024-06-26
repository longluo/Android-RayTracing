package me.longluo.raytracing.base;


public class Ray {

    public Vec3 origin;  //源点

    public Vec3 direction;  //方向

    public Ray() {
    }

    public Ray(Vec3 origin, Vec3 direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Vec3 origin() {
        return origin;
    }

    public Vec3 direction() {
        return direction;
    }

    //p(t) = A + t*B 即返回t时刻光线的位置
    public Vec3 point_at_parameter(double t) {
        return origin.Add(direction.Scale(t));
    }

    @Override
    public String toString() {
        return "Ray{" + direction.toString() + '}';
    }

}
