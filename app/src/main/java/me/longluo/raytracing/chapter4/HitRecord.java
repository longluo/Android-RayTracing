package me.longluo.raytracing.chapter4;

import me.longluo.raytracing.base.Ray;
import me.longluo.raytracing.base.Vec3;

public class HitRecord {

    public double time;     //相撞的时间

    public Vec3 point;      //撞击点的坐标

    public Vec3 normal; //撞击点的法向量

    public boolean isFrontFace; // 光线是从物体外部还是内部射向物体表面

    public HitRecord() {
        time = 0;
        point = new Vec3(0, 0, 0);
        normal = new Vec3(0, 0, 0);
    }

    public void setSurfaceNormal(Ray ray) {
        if (ray.direction.dot(normal) < 0.0) {
            isFrontFace = true;
        } else {
            isFrontFace = false;
            normal = normal.reverse();
        }
    }
    
}
