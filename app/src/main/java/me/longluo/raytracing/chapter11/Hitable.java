package me.longluo.raytracing.chapter11;

public abstract class Hitable {
    public abstract boolean hit(final Ray r, double t_min, double t_max, HitRecord rec);
}

