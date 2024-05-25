package me.longluo.raytracing.chapter12;

public class Ray {

    public Vec3 o;  //源点

    public Vec3 d;  //方向

    public double time; //光线的时间戳

    public Ray() {
    }

    public Ray(Vec3 origin, Vec3 direction) {
        o = origin;
        d = direction;
    }

    public Ray(Vec3 origin, Vec3 direction, double t) {
        o = origin;
        d = direction;
        time = t;
    }

    public Vec3 origin() {
        return o;
    }

    public Vec3 direction() {
        return d;
    }


    /**
     * p(t)=A+t*B 即返回t时刻光线的位置
     *
     * @param t 时间
     * @return 返回t时刻光线的坐标
     */
    public Vec3 point_at_parameter(double t) {
        return o.Add(d.Scale(t));
    }


}