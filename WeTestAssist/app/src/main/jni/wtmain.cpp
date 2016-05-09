#include <jni.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include <sys/socket.h>
#include<string.h>
#include <sys/un.h>
#include <GLES/gl.h>
#include <elf.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <sys/time.h>
#include <sys/atomics.h>
#include <errno.h>
#include <string>
#include <sys/time.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <ProcMemory.h>
#include <ProcStatus.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/wait.h>
using namespace std;

#define LOG_TAG "wetest"
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##args)
#define LOG_INFO(fmt, args...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##args)
#define SELECT_TIMEOUT   100
#define FILESYSTEM_SOCKET_PREFIX          "/tmp/"
#define MAX_FD   10
int fds[MAX_FD] = { -1 };
int serve       = 1;
int current_fps = 1;
#define ROOT_SERVER_NAME "com.tencent.wetested"
#define BUFFER_SIZE 1024
#define FPS_SERVER_NAME  "com.wetest.fps_server"
#define FPSCONNECTTRYTIME 3
#define FPS_CMD_GETFPS  1

int fps_conn_fd = -1;
int fps = -1;
int server_sockfd = -1;
char * fps_process = NULL;


void stop_root_server();

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

//	sprintf(sopath, "/data/data/%s/libfps0.so",
//			"com.tencent.wefpmonitor/files");

	sprintf(sopath, "/data/data/%s/libfps0.so",
				"com.tencent.wetest/files");
	char cmd[256] = { 0 };

	sprintf(cmd,
			"/data/data/com.tencent.wefpmonitor/files/inject %s %s %s %s > /data/local/tmp/inj.log 2>&1",
			install ? "install" : "uninstall", fps_process,
			sopath, install ? "Y" : "N");

//	sprintf(cmd,
//				"/data/data/com.tencent.wetest/files/inject %s %s %s %s > /data/local/tmp/inj.log 2>&1",
//				install ? "install" : "uninstall", fps_process,
//				sopath, install ? "Y" : "N");

//		sprintf(cmd,
//				"/system/xbin/inject %s %s %s %s > /data/local/tmp/inj.log 2>&1",
//				install ? "install" : "uninstall", fps_process,
//				sopath, install ? "Y" : "N");



	sprintf(cmd,
					"/data/data/com.tencent.wefpmonitor/files/inject %s %s %s %s > /data/local/tmp/inj.log 2>&1",
					install ? "install" : "uninstall", "system_server",
					sopath, install ? "Y" : "N");

	LOG_INFO("%s",cmd);

	int r = system(cmd);

	if (WIFEXITED(r)) {

		int res = WEXITSTATUS(r);

		if (res == EPERM || res == EACCES) // û��Ȩ��
		{

			char sucmd[256];
			sprintf(sucmd, "su -c \"%s\"", cmd);
			system(sucmd);


		}
	}

	return 0;
}


int connectFpsServer() {

	system("service call SurfaceFlinger 1008 i32 1");

	int fd = -1;

	int retry_times = 0;

	while (retry_times < 3) {

		fd = android_local_client(FPS_SERVER_NAME);

		//LOG_INFO("connect fd:%d", fd);

		if (fd == -1) {

			//LOG_INFO("service call SurfaceFlinger 1008 i32 1");

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

		//LOG_INFO("the fps is %d",__fps);


		return getFPS();
}

int make_sockaddr(const char *name, struct sockaddr_un *p_addr, socklen_t *alen, int have_local_nm)
{
	memset (p_addr, 0, sizeof (*p_addr));
	size_t namelen;

	if ( have_local_nm != 0 )
	//if ( false )
	{
		namelen  = strlen(name);

        // Test with length +1 for the *initial* '\0'.
        if ((namelen + 1) > sizeof(p_addr->sun_path)) {
            return -1;
        }

        /*
            * Note: The path in this case is *not* supposed to be
            * '\0'-terminated. ("man 7 unix" for the gory details.)
            */

        p_addr->sun_path[0] = 0;
        memcpy(p_addr->sun_path + 1, name, namelen);
        LOG_INFO( "Has abstract namespace");
	}
	else
	{
		/* this OS doesn't have the Linux abstract namespace */

		namelen = strlen(name) + strlen(FILESYSTEM_SOCKET_PREFIX);
		/* unix_path_max appears to be missing on linux */
		if (namelen > sizeof(*p_addr) - offsetof(struct sockaddr_un, sun_path) - 1) {
			return -1;
		}

		strcpy(p_addr->sun_path, FILESYSTEM_SOCKET_PREFIX);
		strcat(p_addr->sun_path, name);

		LOG_INFO( "Has not abstract namespace");
	}

	p_addr->sun_family = AF_LOCAL;
	*alen = namelen + offsetof(struct sockaddr_un, sun_path) + 1;

	return 0;
}

void fds_init()
{
	int i = 0;
	for ( i=0; i<MAX_FD; ++i )
	{
		fds[i] = -1;
	}
}

void fds_clean()
{
	int i = 0;
	for ( i=0; i<MAX_FD; ++i )
	{
		if ( fds[i] != -1)
		{
			close(fds[i]);
			fds[i] = -1;
		}
	}
}

int prepare( fd_set* readset, fd_set* writeset, fd_set* exceptset )
{
	FD_ZERO(readset);
	FD_ZERO(writeset);
	FD_ZERO(exceptset);

	int fdSelect = -1;

	int i = 0;
	for ( i=0; i<MAX_FD; ++i )
	{
		int fd = fds[i];

		if ( fd == -1 )
		{
			continue;
		}

		FD_SET(fd, readset);
		//FD_SET(fd, writeset);
		FD_SET(fd, exceptset);

		if( fd > fdSelect )
		{
			fdSelect = fd;
		}
	}

	return fdSelect;
}

int  root_add_client(int fd)
{
	int i = 0;
	for ( i=0; i<MAX_FD; ++i )
	{
		if ( fds[i] == -1 )
		{
			fds[i] = fd;
			return fd;
		}
	}

	return -1;
}

int  root_close_client(int index)
{
	if ( index > 0 && index < MAX_FD )
	{
		if ( fds[index] != -1 )
		{

			close(fds[index]);
			fds[index] = -1;
		}
	}
}

int  root_indexfd(int index)
{
	if ( index > 0 && index < MAX_FD )
	{
		return fds[index];
	}

	return -1;
}

void root_accept(int index)
{
	struct sockaddr_un client_addr;
	socklen_t len = sizeof(client_addr);

	int fdClient = accept(fds[index], (struct sockaddr*)&client_addr, &len);

	if ( fdClient == -1  )
	{
		LOGD("accept client error: %s", strerror(errno));
		return;
	}

	if ( root_add_client(fdClient) == -1 )
	{
		close(fdClient);
		LOGD("fail recv fps client for max fds.");
	}

	//LOG_INFO("new client fd is %d",fdClient);

}

int sendRootData(int fd, string res)
{
	int total = 0;

	char buffer[BUFFER_SIZE];

    bzero(buffer, BUFFER_SIZE);

    strncpy(buffer, res.c_str(),strlen(res.c_str()) > BUFFER_SIZE ? BUFFER_SIZE : strlen(res.c_str()));

    int r = send(fd, buffer, strlen(res.c_str()), 0);

    //LOG_INFO( "send res to client : %s the fd is %d",buffer,fd);

	if ( r <= 0 )
	{
		if ( errno == EAGAIN || errno == EINTR )
			LOG_INFO("error sending data");

		char* message;
		message = strerror(errno);

		LOG_INFO("error return -1 info is %s",message);
		return -1;
	}

	//LOG_INFO("sending res total is %d",total + r);

	return total;
}

void root_handle_cmd(int index, char cmd[],int n)
{

		if (strcmp(cmd, "end") != 0) {


						int pid = atoi(cmd);

						pm_memusage_t usage;

						//ProcMemory g_memory;

						//g_memory.GetMemory(pid, &usage);

						//LOG_INFO( "Now fps_fd is: %d ",fps_conn_fd);

						int tmp_fps = runFPSClient(fps_conn_fd);

						int i = 0;

						while( tmp_fps == -1  && i++ < FPSCONNECTTRYTIME)//�쳣����
						{

							sleep(1);

							if(fps_conn_fd != -1)

									close(fps_conn_fd);

							fps_conn_fd = connectFpsServer();

							LOG_INFO( "reconnect to fps_fd: %d ",fps_conn_fd);

							tmp_fps = runFPSClient(fps_conn_fd);

						}

						tmp_fps = i >= FPSCONNECTTRYTIME ? -2 :  tmp_fps;

						string tag = "|";

//						string res = getstring(usage.vss) + tag + getstring(usage.rss) + tag
//								+ getstring(usage.uss) + tag + getstring(usage.pss) + tag + getstring(tmp_fps);

						string res = getstring(0) + tag + getstring(0) + tag
								+ getstring(0) + tag + getstring(0) + tag + getstring(tmp_fps);

						int fd = root_indexfd(index);

						//LOG_INFO("handling cmd fd is %d",fd);

						if ( fd == -1 )
						{
							LOG_INFO("handling fd -1");
							return;
						}

						if ( sendRootData(fd, res) == -1 )
						{
							//LOG_INFO("close in cmd");
							root_close_client(index);
							return;
						}


		} else {

						//LOG_INFO("handling cmd ROOT_CMD_CLOSE");

						stop_root_server();

						if(fps_conn_fd != -1)
							  close(fps_conn_fd);

						if(server_sockfd != -1)
							  close(server_sockfd);
		}

}

void  stop_root_server()
{
	serve = 0;
}

void root_recv(int index)
{
	int fd = root_indexfd(index);

	if ( fd == -1 )
	{
		return;
	}

	char buffer[BUFFER_SIZE];

	bzero(buffer, BUFFER_SIZE);

	int r = recv(fd, buffer, BUFFER_SIZE, 0);

	char cmd[BUFFER_SIZE + 1];

	bzero(cmd, BUFFER_SIZE + 1);

	strncpy(cmd, buffer,strlen(buffer) > BUFFER_SIZE ? BUFFER_SIZE : strlen(buffer));

	//LOG_INFO("accept from client:%s",cmd);

	if ( r > 0 )
	{
		//LOG_INFO("cmd is %s",cmd);
		//LOG_INFO("handling cmd");
		root_handle_cmd(index, cmd , BUFFER_SIZE + 1);
	}
	else if ( r == 0 )
	{
		//LOG_INFO("close in recv == 0");
		root_close_client(index);
		return;
	}
	else
	{
		if ( errno == EAGAIN ) return;
		if ( errno == EINTR  ) return;

		LOG_INFO("close in error");
		root_close_client(index);
	}
}

void root_server(const char* name)
{
	LOGD("Server Initing");

	fds_init();

	server_sockfd = socket(AF_UNIX, SOCK_STREAM, 0);

	if ( server_sockfd < 0 )
	{
		LOGD("fail to create fps unix socket server. %s", strerror(errno));
		return;
	}

	//LOGD("Server fd is create");

	struct sockaddr_un server_addr;
	socklen_t slen;

	make_sockaddr(name, &server_addr, &slen, 1);

	if ( bind(server_sockfd, (struct sockaddr *)&server_addr, slen) < 0 )
	{
		if ( errno == ENOENT )
		LOGD("try tmp namespace.");

		make_sockaddr(name, &server_addr, &slen, 0);

		if ( bind(server_sockfd, (struct sockaddr *)&server_addr, slen) < 0 )
		{
			LOGD("fail to bind fps unix socket server. %s", strerror(errno));
			return;
		}
	}

	LOGD("Server binding");

	/* listen */
	listen(server_sockfd, 3);
	//LOGD("%s is started",ROOT_SERVER_NAME);
	root_add_client(server_sockfd);

	fd_set readset, writeset, exceptset;

	while (serve)
	{
		//LOG_INFO("selecting");
		int fdSelect = prepare(&readset, &writeset, &exceptset);

		if( fdSelect == -1 )
		{
			LOGD("unexpected error: no handlers!");
			break;
		}

		struct timeval tv;

		tv.tv_sec = 0;
		tv.tv_usec = SELECT_TIMEOUT*1000;

		int result = select(fdSelect+1, &readset, &writeset, &exceptset, NULL);

		if ( result < 0 )
		{
			if ( errno == EINTR ) continue;

			// the sets and timeout become undefined, so do not rely on their contents after an error.
			LOGD("select error: %s", strerror(errno));
			break;
		}
		else if ( result > 0 )
		{
			int i = 0;
			for ( i=0; i<MAX_FD; ++i )
			{
				int fd = fds[i];
				if ( fd == -1 )
				{
					continue;
				}

				if( FD_ISSET(fd, &readset) )
				{
					if ( fd == server_sockfd )
					{
						//LOG_INFO("accepting fd is %d",fd);
						root_accept(i);
					}
					else
					{
						//LOG_INFO("recving fd is %d",fd);
						root_recv(i);
					}
				}

				if( fds[i] != -1 && FD_ISSET(fd, &exceptset) )
				{
					//LOG_INFO("closing fd is %d",i);
					root_close_client(i);
				}
			}
		}
	}

	fds_clean();
}

int main(int argc, char *argv[]) {

	system("service call SurfaceFlinger 1008 i32 1");

	//LOGD("service call SurfaceFlinger 1008 i32 1");

	fps_process = argv[1];

	//����FPS server
	int fps_conn_fd = connectFpsServer();

	//LOGD("This is WeTest Server");
	root_server(ROOT_SERVER_NAME);

	return 0;
}

