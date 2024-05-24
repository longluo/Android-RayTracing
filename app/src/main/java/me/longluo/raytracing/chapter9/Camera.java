package me.longluo.raytracing.chapter9;

public class Camera {
    public Camera() {
        lower_left = new Vec3(-2.0f, -1.0f, -1.0f);
        horizontal = new Vec3(4.0f, 0.0f, 0.0f);
        vertical = new Vec3(0.0f, 2.0f, 0.0f);
        origin = new Vec3(0.0f, 0.0f, 0.0f);
    }

    /**
     * @param lookfrom 相机位置
     * @param lookat   观察点
     * @param vup      相机的倾斜方向 view up
     * @param vfov     视野 field of view
     * @param aspect   宽高比
     */
    public Camera(Vec3 lookfrom, Vec3 lookat, Vec3 vup, double vfov, double aspect) {

        Vec3 u, v, w;
        double theta = vfov * Math.PI / 180;
        double half_height = Math.tan(theta / 2);
        double half_width = aspect * half_height;
        origin = lookfrom;
        w = lookfrom.Subtract(lookat).normalize();      //相当于新的z
        u = vup.cross(w).normalize();                   //相当于新的x
        v = w.cross(u).normalize();                     //相当于新的y
        lower_left = origin.Subtract(u.Scale(half_width)).Subtract(v.Scale(half_height)).Subtract(w);
        horizontal = u.Scale(2 * half_width);
        vertical = v.Scale(2 * half_height);
    }

    public Ray GetRay(double u, double v) {
        return new Ray(origin, lower_left.Add(horizontal.Scale(u)).Add(vertical.Scale(v)).Subtract(origin));
    }

    private Vec3 lower_left;
    private Vec3 horizontal;
    private Vec3 vertical;
    private Vec3 origin;
}