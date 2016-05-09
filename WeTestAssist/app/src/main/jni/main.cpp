#include <jni.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <sys/wait.h>
#include <errno.h>
#include <sys/atomics.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <fcntl.h>
#include <sys/types.h>
#include <ProcMemory.h>
#include <ProcStatus.h>
#include <android/log.h>
#include <string.h>
#include <arpa/inet.h>
#include <vector>
#include <string>
#include <sys/time.h>
using namespace std;

//#include <opencv/cv.h>
//#include <opencv/highgui.h>

const int MAXLINE = 1024;

#define HELLO_WORLD_SERVER_PORT    6670
#define LENGTH_OF_LISTEN_QUEUE 20
#define BUFFER_SIZE 1024
#define FILE_NAME_MAX_SIZE 512
#define LOG_INFO(fmt, args...)  __android_log_print(ANDROID_LOG_INFO, "wetest", fmt, ##args)
#define FPS_SERVER_NAME  "com.wetest.fps_server"
#define FPS_CMD_GETFPS  1
#define TRYTIME 70
#define FPSCONNECTTRYTIME 5

string getstring(const int n)

{

	std::stringstream newstr;
	newstr << n;
	return newstr.str();

}


int android_local_client(const char* name) {

	int localsocket, len;

	struct sockaddr_un remote;

	if ((localsocket = socket(AF_UNIX, SOCK_STREAM, 0)) == -1) {

		return -1;
	}

	remote.sun_path[0] = '\0'; /* abstract namespace */

	strcpy(remote.sun_path + 1, name);

	remote.sun_family = AF_UNIX;

	int nameLen = strlen(name);

	len = 1 + nameLen + offsetof(struct sockaddr_un, sun_path);

	struct timeval timeout;

	timeout.tv_sec = 0;

	timeout.tv_usec = 1000000;

	setsockopt(localsocket, SOL_SOCKET, SO_SNDTIMEO, (char *) &timeout,
			sizeof(struct timeval));

	if (connect(localsocket, (struct sockaddr *) &remote, len) == -1) {

		close(localsocket);

		return -1;
	}

	return localsocket;
}


int injectSF(bool install) {

	LOG_INFO("injecting service call SurfaceFlinger 1008 i32 1");
	system("service call SurfaceFlinger 1008 i32 1");

	char sopath[256];

	sprintf(sopath, "/data/data/%s/libfps0.so",
			"com.tencent.wefpmonitor/files");

	char cmd[256] = { 0 };

	// 某些机器上不支持重定向“&>”
//	sprintf(cmd,
//			"/data/data/com.tencent.wefpmonitor/files/inject %s %s %s %s > /data/local/tmp/inj.log 2>&1",
//			install ? "install" : "uninstall", "/system/bin/surfaceflinger",
//			sopath, install ? "Y" : "N");


	sprintf(cmd,
				"/data/data/com.tencent.wefpmonitor/files/inject %s %s %s %s > /data/local/tmp/inj.log 2>&1",
				install ? "install" : "uninstall", "system_server",
				sopath, install ? "Y" : "N");


	int r = system(cmd);

	if (WIFEXITED(r)) {

		int res = WEXITSTATUS(r);

		if (res == EPERM || res == EACCES) // 没有权限
		{
			char sucmd[256];
			sprintf(sucmd, "su -c \"%s\"", cmd);


			system(sucmd);


		}
	}

	return 0;
}


int connectFpsServer() {

	int fd = -1;

	int retry_times = 0;

	while (retry_times < 3) {

		fd = android_local_client(FPS_SERVER_NAME);

		LOG_INFO("connect fd:%d", fd);

		if (fd == -1) {

			LOG_INFO("service call SurfaceFlinger 1008 i32 1");

			system("service call SurfaceFlinger 1008 i32 1"); // Disable HW Layer

			injectSF(true);

			retry_times++;

			LOG_INFO( "retry time:%d",retry_times);

		} else {

			break;

		}

	    sleep(1);
	}

	return fd;

}

int readex(int fd, void* buf, int num)
{
	int total = 0;

	while (num > 0)
	{

		int r = read(fd, (char*)buf+total, num);

		if ( r <= 0 )
		{
			return -1;
		}

		total += r;
		num   -= r;
	}

	return total;

}

int fps = -1;

void setFPS( int v )
{

	__atomic_swap(v, &fps);

}

int getFPS()
{

	return __atomic_swap(1, &fps);

}

int runFPSClient(int fd)
{

	char cmd = FPS_CMD_GETFPS;

	if ( send(fd, &cmd, 1, 0) == -1 )
		{
			if ( !(errno == EAGAIN || errno == EINTR )){

				return -1;
			}

				return -1;
		}

		int __fps = -1;

		if ( readex(fd, &__fps, sizeof(__fps)) == sizeof(__fps) )
		{
			setFPS(__fps);

		}else{

			return -1;

		}

		LOG_INFO("the fps is %d",__fps);


		return getFPS();
}

int main(int argc, char *argv[]) {



	system("service call SurfaceFlinger 1008 i32 1");

	//连接FPS server
	int fps_conn_fd = connectFpsServer();

	LOG_INFO("ConnectFpsFd : %d !", fps_conn_fd);

	//wetestd服务端server执行root操作

	struct sockaddr_in server_addr;

	bzero(&server_addr, sizeof(server_addr));

	server_addr.sin_family = AF_INET;

	server_addr.sin_addr.s_addr = htons(INADDR_ANY);

	server_addr.sin_port = htons(HELLO_WORLD_SERVER_PORT);

	int server_socket = socket(PF_INET, SOCK_STREAM, 0);

	if (server_socket < 0) {

		LOG_INFO("Create Socket Failed!");
		exit(1);
	}

	{
		int opt = 1;
		setsockopt(server_socket, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));
	}

	int tryport = HELLO_WORLD_SERVER_PORT;
	int tryTime = 0;
	//把socket和socket地址结构联系起来
	if (bind(server_socket, (struct sockaddr*) &server_addr,
			sizeof(server_addr))) {

		LOG_INFO("Server Bind Port : %d Failed!", HELLO_WORLD_SERVER_PORT);

		exit(1);

	}

	LOG_INFO("Bind Server is  %d ",tryport);

	//server_socket用于监听
	if (listen(server_socket, LENGTH_OF_LISTEN_QUEUE)) {

		LOG_INFO("Server Listen Failed!");

		exit(1);
	}

	int last_server_socket = -1;

		//定义客户端的socket地址结构client_addr
		struct sockaddr_in client_addr;
		socklen_t length = sizeof(client_addr);

		LOG_INFO( "Wetestd is ACCEPTINGING");

		while(1){

			int new_server_socket  = accept(server_socket,
				(struct sockaddr*) &client_addr, &length);

			if (new_server_socket < 0) {

				LOG_INFO("Server Accept Failed!");
				break;
			}

		char buffer[BUFFER_SIZE];

		bzero(buffer, BUFFER_SIZE);

		length = recv(new_server_socket, buffer, BUFFER_SIZE, 0);

				if (length < 0) {
					LOG_INFO("Server Recieve Data Failed!\n");
					break;
				}


				char app_cmd[FILE_NAME_MAX_SIZE + 1];

				bzero(app_cmd, FILE_NAME_MAX_SIZE + 1);

				strncpy(app_cmd, buffer,
						strlen(buffer) > FILE_NAME_MAX_SIZE ?
								FILE_NAME_MAX_SIZE : strlen(buffer));

				LOG_INFO("accept from client:%s",app_cmd);


				if (strcmp(app_cmd, "end") != 0) {


					int pid = atoi(app_cmd);

					pm_memusage_t usage;

					ProcMemory g_memory;

					g_memory.GetMemory(pid, &usage);

					LOG_INFO( "Now fps_fd is: %d ",fps_conn_fd);

					int tmp_fps = runFPSClient(fps_conn_fd);

					int i = 0;

					while( tmp_fps == -1  && i++ < FPSCONNECTTRYTIME)//异常重连
					{

						sleep(1);//注意

						if(fps_conn_fd != -1)

								close(fps_conn_fd);

						fps_conn_fd = connectFpsServer();

						LOG_INFO( "reconnect to fps_fd: %d ",fps_conn_fd);

						tmp_fps = runFPSClient(fps_conn_fd);

					}

					tmp_fps = i >= FPSCONNECTTRYTIME ? -2 :  tmp_fps;

					string tag = "|";

					string res = getstring(usage.vss) + tag + getstring(usage.rss) + tag
							+ getstring(usage.uss) + tag + getstring(usage.pss) + tag + getstring(tmp_fps);

					bzero(buffer, BUFFER_SIZE);

					strncpy(buffer, res.c_str(),strlen(res.c_str()) > BUFFER_SIZE ? BUFFER_SIZE : strlen(res.c_str()));

					LOG_INFO( "send res to client : %s the fd is %d",buffer,new_server_socket);

					send(new_server_socket, buffer, strlen(res.c_str()), 0);

					close(new_server_socket);

				} else {

					close(new_server_socket);

					break;

				}

		}



		//关闭监听用的socket
			if(fps_conn_fd != -1)
				close(fps_conn_fd);

	close(server_socket);
	LOG_INFO("wetestd stoped in process");
	return 0;
}
