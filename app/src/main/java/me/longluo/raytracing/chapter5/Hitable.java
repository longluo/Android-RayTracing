package me.longluo.raytracing.chapter5;

import me.longluo.raytracing.base.Ray;

public abstract class Hitable {
    public abstract boolean hit(final Ray r, double t_min, double t_max, HitRecord rec);
}
