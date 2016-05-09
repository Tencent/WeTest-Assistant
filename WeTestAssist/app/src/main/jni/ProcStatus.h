#ifndef _PROC_STATUS_H_
#define _PROC_STATUS_H_
#include <stdint.h>
#include <stdio.h>

class ProcStatus
{
public:
	ProcStatus();
	~ProcStatus();

public:
	long GetTotalCpuTime();
	long GetProcessCpuTime(int pid);
	long GetBatteryCurrent();
	int GetNetworkUsage(int uid, uint32_t* netin, uint32_t* netout);
};

FILE* mypopen(const char *program, const char *type);
int mypclose(FILE *iop);

#endif
