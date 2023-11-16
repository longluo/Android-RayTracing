LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

APP_ABI := all

LOCAL_MODULE    := raytracer

LOCAL_SRC_FILES := raytracer.cpp

APP_OPTIM       := release

LOCAL_CFLAGS := -march=armv8 \
                -mfloat-abi=softfp \
                -mfpu=vfp \
                -ffast-math \
                -O1

LOCAL_LDLIBS := -lm \
                -llog \
                -ljnigraphics

include $(BUILD_SHARED_LIBRARY)
