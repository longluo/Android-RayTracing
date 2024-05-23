package me.longluo.raytracing.chapter11;

public abstract class Material {

    public abstract boolean scatter(Ray r, HitRecord rec, Wrapper wrapper);

    /**
     * 生成一个单位球内的随机坐标
     *
     * @return 单位球内的随机坐标
     */
    public Vec3 randomInUnitSphere() {
        Vec3 p;
        do {
            //随机坐标 区间[-1,+1]
            p = new Vec3(Math.random(), Math.random(), Math.random()).Scale(2.0f).Subtract(new Vec3(1.0f, 1.0f, 1.0f));
        } while (p.dot(p) >= 1.0f);  //如果坐标在球内则采用，否则再次生成

        return p;
    }
}

