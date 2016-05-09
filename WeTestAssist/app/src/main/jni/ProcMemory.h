#ifndef _MEM_INFO_H_
#define _MEM_INFO_H_

#include <unistd.h>

struct pm_memusage_t
{
	size_t vss;
	size_t rss;
	size_t pss;
	size_t uss;
	size_t native;
	size_t dalvik;
	size_t total;
};

struct pm_kernel_t
{
	int kpagecount_fd;
	int pagesize;
};

struct pm_map_t
{
	unsigned long start;
	unsigned long end;
	unsigned long offset;
	int flags;
	//string name;
};

class ProcMemory
{
public:
	ProcMemory();
	~ProcMemory();
	int GetMemory(int pid, pm_memusage_t* pUsage);
private:
	int GetProcMem(int pid);
	int GetMapUsage(pm_map_t* map);
	int GetMapPageMap(pm_map_t *map, uint64_t **pagemap_out, size_t *len);
	int GetKernelCount(unsigned long pfn, uint64_t *count_out);
	int DumpMemInfo(int pid);
	int GetMemInfo(int pid);
private:
	int m_iPid;
	int m_iPageMapFd;
	pm_kernel_t m_stKernel;
	pm_memusage_t m_stUsage;
};

#endif
