package me.longluo.raytracing.chapter7;

import me.longluo.raytracing.base.Ray;
import me.longluo.raytracing.base.Vec3;

public class Wrapper {
    Ray scattered;      //反射光线
    Vec3 attenuation;   //材料系数

    public Wrapper() {
        scattered = new Ray();
        attenuation = new Vec3();
    }
}
