package me.longluo.raytracing.base;

import java.util.Arrays;

public class Vec3 {

    // 定义最基本的数据格式：三维的浮点型坐标
    public double[] e = new double[3];

    // 构造函数
    public Vec3() {
        this(0, 0, 0);
    }

    public Vec3(double x, double y, double z) {
        e[0] = x;
        e[1] = y;
        e[2] = z;
    }

    //获取三维坐标任一维度的索引，返回对应坐标的值
    public double x() {
        return e[0];
    }

    public double y() {
        return e[1];
    }

    public double z() {
        return e[2];
    }

    // 向量求和
    public Vec3 Add(Vec3 a, Vec3 b) {
        return new Vec3(a.e[0] + b.e[0], a.e[1] + b.e[1], a.e[2] + b.e[2]);
    }

    public Vec3 Add(Vec3 v) {
        return new Vec3(e[0] + v.e[0], e[1] + v.e[1], e[2] + v.e[2]);
    }

    //向量相减
    public Vec3 Subtract(Vec3 a, Vec3 b) {
        return new Vec3(a.e[0] - b.e[0], a.e[1] - b.e[1], a.e[2] - b.e[2]);
    }

    public Vec3 Subtract(Vec3 v) {
        return new Vec3(e[0] - v.e[0], e[1] - v.e[1], e[2] - v.e[2]);
    }

    //向量数乘
    public Vec3 Scale(Vec3 a, double c) {
        return new Vec3(a.e[0] * c, a.e[1] * c, a.e[2] * c);
    }

    public Vec3 Scale(double t) {
        return new Vec3(e[0] * t, e[1] * t, e[2] * t);
    }

    //求向量的模长
    public double length() {
        return Math.sqrt(Math.pow(e[0], 2.0) +
                Math.pow(e[1], 2.0) +
                Math.pow(e[2], 2.0));
    }

    //模长的平方
    public double sqrLength() {
        return Math.pow(e[0], 2.0) +
                Math.pow(e[1], 2.0) +
                Math.pow(e[2], 2.0);
    }

    //向量归一化
    public Vec3 normalize(Vec3 v) {
        double length = v.length();
        return new Vec3(v.x() / length, v.y() / length, v.z() / length);
    }

    public Vec3 normalize() {
        double length = this.length();
        return new Vec3(e[0] / length, e[1] / length, e[2] / length);
    }

    //点乘
    public double dot(Vec3 a, Vec3 b) {
        return (a.x() * b.x() + a.y() * b.y() + a.z() * b.z());
    }

    public double dot(Vec3 v) {
        return (e[0] * v.e[0] + e[1] * v.e[1] + e[2] * v.e[2]);
    }

    //叉乘
    public Vec3 cross(Vec3 a, Vec3 b) {
        return new Vec3(a.y() * b.z() - a.z() * b.y(),
                a.z() * b.x() - a.x() * b.z(),
                a.x() * b.y() - a.y() * b.x());
    }

    public Vec3 cross(Vec3 v) {
        return new Vec3(e[1] * v.z() - e[2] * v.y(),
                e[2] * v.x() - e[0] * v.z(),
                e[0] * v.y() - e[1] * v.x());
    }

    @Override
    public String toString() {
        return "Vec3{" +
                "e=" + Arrays.toString(e) +
                '}';
    }
}

