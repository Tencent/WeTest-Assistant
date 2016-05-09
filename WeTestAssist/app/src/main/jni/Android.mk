LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

APP_STL := stlport_static
APP_STL := gnustl_static
LOCAL_C_INCLUDES:= $(ANDROID_NDK)/sources/cxx-stl/stlport/stlport/ $(LOCAL_PATH)/../

#LOCAL_CERTIFICATE := platform


LOCAL_MODULE    := WeTestForAndroid
LOCAL_SRC_FILES := \
WeTestForAndroid.cpp\
ProcStatus.cpp

LOCAL_SHARED_LIBRARIES := libcutils liblog
LOCAL_LDLIBS    := -llog 
#LOCAL_ALLOW_UNDEFINED_SYMBOLS := true 
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)


APP_STL := stlport_static
APP_STL := gnustl_static
LOCAL_C_INCLUDES := \$(ANDROID_NDK)/sources/cxx-stl/stlport/stlport/ $(LOCAL_PATH)/../ 

LOCAL_SHARED_LIBRARIES := libcutils liblog

#LOCAL_CFLAGS    := -pie -fPIE
#LOCAL_LDFLAGS   := -pie -fPIE
LOCAL_MODULE    := wetested
LOCAL_SRC_FILES := \
    ProcMemory.cpp\
    wtmain.cpp
    #LocalServer.cpp
    #main.cpp
     
     
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
#LOCAL_ALLOW_UNDEFINED_SYMBOLS := true 

include $(BUILD_EXECUTABLE)


include $(CLEAR_VARS)

APP_STL := stlport_static
APP_STL := gnustl_static
LOCAL_C_INCLUDES := \$(ANDROID_NDK)/sources/cxx-stl/stlport/stlport/ $(LOCAL_PATH)/../ 


LOCAL_SHARED_LIBRARIES := libcutils liblog


LOCAL_CFLAGS    := -pie -fPIE
LOCAL_LDFLAGS   := -pie -fPIE
LOCAL_MODULE    := wetestedL
LOCAL_SRC_FILES := \
    ProcMemory.cpp\
    wtmain.cpp
    #LocalServer.cpp
    #main.cpp
     
     
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
#LOCAL_ALLOW_UNDEFINED_SYMBOLS := true 

include $(BUILD_EXECUTABLE)
