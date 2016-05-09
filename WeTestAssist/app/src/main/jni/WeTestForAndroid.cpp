#include <jni.h>
#include <unistd.h>
#include <stdio.h>
#include <ProcMemory.h>
#include <ProcStatus.h>

#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <sys/wait.h>
#include <errno.h>
#include <sys/atomics.h>
#include <unistd.h>

#include <android/log.h>

#include <iostream>
#include <fstream>
#include <sstream>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/wait.h>

#include <stdlib.h>        // for exit
#include <string.h>
#include <arpa/inet.h>
#include <vector>



#define HELLO_WORLD_SERVER_PORT    6670
#define BUFFER_SIZE 1024
#define FILE_NAME_MAX_SIZE 512
#define LOG_INFO(fmt, args...)  __android_log_print(ANDROID_LOG_INFO, "wetest", fmt, ##args)
#define FPS_SERVER_NAME  "com.wetest.fps_server"
#define FPS_CMD_GETFPS  1
#define RETRYTIME  3

using namespace std;

extern "C"{

	JNIEXPORT jlong JNICALL
		Java_com_tencent_wetest_common_manager_ProcStatusManager_getTotalCpuTime(JNIEnv *,jobject);


	JNIEXPORT jlong JNICALL
		Java_com_tencent_wetest_common_manager_ProcStatusManager_getProcessCpuTime(JNIEnv *,jobject,jint);

	JNIEXPORT jintArray JNICALL
		Java_com_tencent_wetest_common_manager_ProcStatusManager_getNativeNetworkUsage(JNIEnv *,jobject,jint,jintArray);

	JNIEXPORT jint JNICALL
		Java_com_tencent_wetest_common_manager_FPSManager_injectNFps( JNIEnv* , jobject  ) ;

	JNIEXPORT jint JNICALL
		Java_com_tencent_wetest_common_manager_FPSManager_disinjectNFps( JNIEnv* , jobject  ) ;

}

int injectSF(bool install)
{
	char sopath[256];
	sprintf(sopath, "/data/data/%s/libfps0.so", "com.tencent.wefpmonitor/files");

	char cmd[256] = {0};

	// 某些机器上不支持重定向“&>”
	sprintf(cmd, "/data/data/com.tencent.wefpmonitor/files/inject %s %s %s %s > /data/local/tmp/inj.log 2>&1",
			install ? "install":"uninstall", "/system/bin/surfaceflinger", sopath, install ? "Y" : "N");

	//LOG_DEBUG("inject cmd: %s", cmd);

	int r = system(cmd);

	if ( WIFEXITED(r) )
	{
		int res = WEXITSTATUS(r);

		if ( res == EPERM || res == EACCES ) // 没有权限
		{
			char sucmd[256];

			sprintf(sucmd, "su -c \"%s\"", cmd);

			system(sucmd);

		}
	}

	return 0;


}


JNIEXPORT jlong JNICALL
Java_com_tencent_wetest_common_manager_ProcStatusManager_getTotalCpuTime(JNIEnv * pEnv,jobject pThis){
	ProcStatus procStatus;
	return procStatus.GetTotalCpuTime();
}

JNIEXPORT jlong JNICALL
Java_com_tencent_wetest_common_manager_ProcStatusManager_getProcessCpuTime(JNIEnv * pEnv,jobject pThis,jint pid){
	ProcStatus procStatus;
	return procStatus.GetProcessCpuTime(pid);
}

JNIEXPORT jintArray JNICALL
Java_com_tencent_wetest_common_manager_ProcStatusManager_getNativeNetworkUsage(JNIEnv * pEnv,jobject pThis,jint uid,jintArray nums){

	uint32_t netin, netout;
	ProcStatus procStatus;

	procStatus.GetNetworkUsage(uid, &netin, &netout);
	jintArray ret= pEnv->NewIntArray(2);

	//获取传入的数组

	jint *body =  pEnv->GetIntArrayElements(nums,NULL);


	body[0] = netin ;

	body[1] = netout ;

	//将C的数组拷贝给java中的数组
	pEnv->SetIntArrayRegion(ret,0,2,body);
	pEnv->ReleaseIntArrayElements(nums,body,0);

	return ret;

}

JNIEXPORT jint JNICALL
Java_com_tencent_wetest_common_manager_FPSManager_injectNFps( JNIEnv* env, jobject thiz) {

	return 	injectSF(true);
}

JNIEXPORT jint JNICALL
Java_com_tencent_wetest_common_manager_FPSManager_disinjectNFps( JNIEnv* env, jobject  thiz) {
	return 	injectSF(false);
}
