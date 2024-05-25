package me.longluo.raytracing.chapter12;

public class Wrapper {

    Ray scattered;

    Vec3 refracted;

    Vec3 attenuation;

    public Wrapper() {
        scattered = new Ray();
        attenuation = new Vec3();
        refracted = new Vec3();
    }
}