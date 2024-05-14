package me.longluo.raytracing.listener;

public interface OnRayTracingListener<T> {

    void onRenderSuccess(T result);

    void onConvertSuccess(T result);

    void onFail(Exception e);
}
