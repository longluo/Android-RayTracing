package me.longluo.raytracing.chapter10;

public class Camera {
    private Vec3 lower_left;
    private Vec3 horizontal;
    private Vec3 vertical;
    private Vec3 origin;
    private double lens_radius;
    private Vec3 u = new Vec3();
    private Vec3 v = new Vec3();
    private Vec3 w = new Vec3();

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
     * @param vfov     角度 field of view
     * @param aspect   宽高比
     */
    public Camera(Vec3 lookfrom, Vec3 lookat, Vec3 vup, double vfov, double aspect, double aperture, double focus_dist) {

        lens_radius = aperture / 2;

        double theta = vfov * Math.PI / 180;
        double half_height = Math.tan(theta / 2);
        double half_width = aspect * half_height;
        origin = lookfrom;
        w = lookfrom.Subtract(lookat).normalize();      //相当于新的z
        u = vup.cross(w).normalize();                   //相当于新的x
        v = w.cross(u).normalize();                     //相当于新的y
        lower_left = origin.Subtract(u.Scale(half_width * focus_dist)).Subtract(v.Scale(half_height * focus_dist)).Subtract(w.Scale(focus_dist));
        horizontal = u.Scale(2 * half_width * focus_dist);
        vertical = v.Scale(2 * half_height * focus_dist);
    }

    public Ray GetRay(double u, double v) {
        Vec3 rd = randomInUnitSphere().Scale(lens_radius);
        Vec3 offset = this.u.Scale(rd.x()).Add(this.v.Scale(rd.y()));
        return new Ray(origin.Add(offset), lower_left.Add(horizontal.Scale(u)).Add(vertical.Scale(v)).Subtract(origin).Subtract(offset));
    }

    /**
     * 生成一个单位球内的随机坐标
     *
     * @return 单位球内的随机坐标
     */
    public Vec3 randomInUnitSphere() {
        Vec3 p;
        do {
            //随机坐标 区间[-1,+1]
            p = new Vec3(Math.random(), Math.random(), 0).Scale(2.0f).Subtract(new Vec3(1.0f, 1.0f, 0.0f));
        } while (p.dot(p) >= 1.0f);  //如果坐标在球内则采用，否则再次生成
        return p;
    }

}