#include "procstatus.h"

#include <dirent.h>
#include <stdio.h>
#include <string.h>
#include <string>
#include <sstream>
#include <fstream>
#include <ctype.h>
#include <unistd.h>

#include <errno.h>
#include <sys/wait.h>


#define BUFFSIZE                2560
#define READ_BUF_SIZE 256

#define CMD_GET_CURRENT_REQ  1012
#define CMD_GET_CURRENT_RES  1013

ProcStatus::ProcStatus()
{

}

ProcStatus::~ProcStatus()
{

}

static struct pid
{
	struct pid *next;
	FILE *fp;
	pid_t pid;
}*pidlist;

FILE* mypopen(const char *program, const char *type)
{
	struct pid * volatile cur;
	FILE *iop;
	int pdes[2];
	pid_t pid;

	if ((*type != 'r' && *type != 'w') || type[1] != '\0')
	{
		(*__errno()) = 22;
		return (__null);
	}

	if ((cur = (struct pid*) malloc(sizeof(struct pid))) == __null)
		return (__null);

	if (pipe(pdes) < 0)
	{
		free(cur);
		return (__null);
	}

	switch (pid = fork())
	{
	case -1:
		(void) close(pdes[0]);
		(void) close(pdes[1]);
		free(cur);
		return (__null);

	case 0:
	{
		struct pid *pcur;

		for (pcur = pidlist; pcur; pcur = pcur->next)
			close(((pcur->fp)->_file));

		if (*type == 'r')
		{
			int tpdes1 = pdes[1];

			(void) close(pdes[0]);

			if (tpdes1 != 1)
			{
				(void) dup2(tpdes1, 1);
				(void) close(tpdes1);
				tpdes1 = 1;
			}
		}
		else
		{
			(void) close(pdes[1]);
			if (pdes[0] != 0)
			{
				(void) dup2(pdes[0], 0);
				(void) close(pdes[0]);
			}
		}
		execl("/system/bin/sh", "sh", "-c", program, (char *) __null);
		_exit(127);

	}
	}

	if (*type == 'r')
	{
		iop = fdopen(pdes[0], type);
		(void) close(pdes[1]);
	}
	else
	{
		iop = fdopen(pdes[1], type);
		(void) close(pdes[0]);
	}

	cur->fp = iop;
	cur->pid = pid;
	cur->next = pidlist;
	pidlist = cur;

	return (iop);
}

int mypclose(FILE *iop)
{
	struct pid *cur, *last;
	int pstat;
	pid_t pid;

	for (last = __null, cur = pidlist; cur; last = cur, cur = cur->next)
		if (cur->fp == iop)
			break;

	if (cur == __null)
		return (-1);

	signal(13, ((__sighandler_t ) 1));
	(void) fclose(iop);

	do
	{
		pid = waitpid(cur->pid, &pstat, 0);
	} while (pid == -1 && (*__errno()) == 4);

	if (last == __null)
		pidlist = cur->next;
	else
		last->next = cur->next;
	free(cur);

	return (pid == -1 ? -1 : pstat);
}

long ProcStatus::GetTotalCpuTime()
{
	int user, nice, sys, idle, iowait, irq, softirq;
	char filename[READ_BUF_SIZE] = "/proc/stat";
	std::ifstream fin(filename);
	if (!fin)
	{
		//LOG_ERROR("open file %s failed", filename);
		return -1;
	}
	std::string line;
	std::string token;
	while (getline(fin, line))
	{
		std::stringstream ss(line);
		ss >> token >> user >> nice >> sys >> idle >> iowait >> irq >> softirq;
		if (token == "cpu")
		{
			break;
		}
	}
	fin.close();
//	LOG_INFO(
//			"user:%d, nice:%d, sys:%d, idle:%d, iowait:%d, irq:%d, softirq:%d",
//			user, nice, sys, idle, iowait, irq, softirq);

	return user + nice + sys + idle + iowait + irq + softirq;
}

long ProcStatus::GetProcessCpuTime(int pid)
{
	char filename[READ_BUF_SIZE];
	sprintf(filename, "/proc/%d/stat", pid);
	std::ifstream fin(filename);
	if (!fin)
	{
		//LOG_ERROR("open file %s failed", filename);
		return -1;
	}
	std::string line;
	std::string comm;
	std::string stat;
	long id, ppid, pgrp, session, tty_nr, tpgid, flags, minflt, cminflt, majflt, cmajflt, utime, stime, cutime, cstime;
	getline(fin, line);
	std::stringstream ss(line);
	ss >> id >> comm >> stat >> ppid >> pgrp >> session >> tty_nr >> tpgid >> flags >> minflt >> cminflt >> majflt
			>> cmajflt >> utime >> stime >> cutime >> cstime;
	fin.close();
//	LOG_INFO(
//			"id:%d, comm:%s, stat:%s, ppid:%d, pgrp:%d, session:%d, tty_nr:%d, tpgid:%d, flags:%d, minflt:%d, cminflt:%d, majflt:%d, cmajflt:%d, utime:%d, stime:%d, cutime:%d, cstime",
//			id, comm.c_str(), stat.c_str(), ppid, pgrp, session, tty_nr, tpgid, flags, minflt, cminflt, majflt, cmajflt, utime, stime, cutime, cstime);

	return utime + stime + cutime + cstime;
}

int ProcStatus::GetNetworkUsage(int uid, uint32_t* netin, uint32_t* netout)
{
	char rcvPath[READ_BUF_SIZE];
	char sndPath[READ_BUF_SIZE];

	*netin  = 0;
	*netout = 0;

	sprintf(rcvPath, "/proc/uid_stat/%d/tcp_rcv", uid);
	sprintf(sndPath, "/proc/uid_stat/%d/tcp_snd", uid);
	std::ifstream finRcv(rcvPath);
	if (!finRcv)
	{
		//LOG_ERROR("open file %s failed", rcvPath);
		//return ApkManager::Instance()->getUidRxTxBytes(uid, netin, netout);
		return -1;
	}
	std::string line;
	uint32_t rcvBytes = 0;
	if (getline(finRcv, line))
	{
		std::stringstream ss(line);
		ss >> rcvBytes;
	}
	finRcv.close();

	std::ifstream finSnd(sndPath);
	if (!finSnd)
	{
		//LOG_ERROR("open file %s failed", sndPath);
		return -1;
		//return ApkManager::Instance()->getUidRxTxBytes(uid, netin, netout);
	}
	uint32_t sndBytes = 0;
	if (getline(finSnd, line))
	{
		std::stringstream ss(line);
		ss >> sndBytes;
	}
	finSnd.close();
	*netin = rcvBytes;
	*netout = sndBytes;

	//LOG_DEBUG("rx=%d, tx=%d", *netin, *netout);

	return 0;
}

