#include <ProcMemory.h>
#include <ProcStatus.h>
#include <vector>
#include <iostream>
#include <fstream>
#include <sstream>
#include <fcntl.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <stdlib.h>
#include <android/log.h>
using namespace std;

#define _BITS(x, offset, bits) (((x) >> offset) & ((1LL << (bits)) - 1))

#define PM_PAGEMAP_PRESENT(x)     (_BITS(x, 63, 1))
#define PM_PAGEMAP_SWAPPED(x)     (_BITS(x, 62, 1))
#define PM_PAGEMAP_SHIFT(x)       (_BITS(x, 55, 6))
#define PM_PAGEMAP_PFN(x)         (_BITS(x, 0, 55))
#define PM_PAGEMAP_SWAP_OFFSET(x) (_BITS(x, 5, 50))
#define PM_PAGEMAP_SWAP_TYPE(x)   (_BITS(x, 0,  5))

#define PM_MAP_READ  1
#define PM_MAP_WRITE 2
#define PM_MAP_EXEC  4

#define MAX_FILENAME 128

typedef vector<pm_map_t> MapVec;

ProcMemory::ProcMemory() {
}

ProcMemory::~ProcMemory() {
}

int ProcMemory::GetProcMem(int pid) {
	// get kernel page info

	m_stKernel.kpagecount_fd = open("/proc/kpagecount", O_RDONLY);

	if (m_stKernel.kpagecount_fd < 0) {
		__android_log_print(ANDROID_LOG_INFO, "wetest", "1");
		return -1;
	}
	m_stKernel.pagesize = getpagesize();

	// get process map info
	char filename[MAX_FILENAME];
	snprintf(filename, MAX_FILENAME, "/proc/%d/pagemap", pid);
	m_iPageMapFd = open(filename, O_RDONLY);
	if (m_iPageMapFd < 0) {
		close(m_stKernel.kpagecount_fd);
		__android_log_print(ANDROID_LOG_INFO, "wetest", "2");
		return -1;
	}

	snprintf(filename, MAX_FILENAME, "/proc/%d/maps", pid);

//	int fd = open(filename, O_RDONLY);
//	unsigned char src_buff[100];
//	read(fd, src_buff, sizeof(src_buff));
//	__android_log_print(ANDROID_LOG_INFO, "wetest"," %s",src_buff);
//	__android_log_print(ANDROID_LOG_INFO, "wetest","system hello: %s",  strerror(errno));

	ifstream fin(filename);
	if (!fin) {
		close(m_stKernel.kpagecount_fd);
		__android_log_print(ANDROID_LOG_INFO, "wetest", " 3");
		return -1;
	}

	//__android_log_print(ANDROID_LOG_INFO, "wetest","system: %s",  strerror(errno));

	MapVec stMapVec;
	string line, addr, perms; //, tmpStr;
	//__android_log_print(ANDROID_LOG_INFO, "wetest"," GetMemory: A");
	while (getline(fin, line)) {
		//__android_log_print(ANDROID_LOG_INFO, "wetest"," GetMemory: GetLine");
		pm_map_t stMap;
		stringstream sin(line);
		sin >> addr >> perms >> stMap.offset; // >> tmpStr >> tmpStr >>  stMap.name;
		sscanf(addr.c_str(), "%lx-%lx", &stMap.start, &stMap.end);
		if (perms[0] == 'r')
			stMap.flags |= PM_MAP_READ;
		if (perms[1] == 'w')
			stMap.flags |= PM_MAP_WRITE;
		if (perms[2] == 'x')
			stMap.flags |= PM_MAP_EXEC;
		stMapVec.push_back(stMap);
	}
	//__android_log_print(ANDROID_LOG_INFO, "wetest","system: %s",  strerror(errno));
	fin.close();
	//__android_log_print(ANDROID_LOG_INFO, "wetest"," GetMemory: B");

	//stat mem map
	memset(&m_stUsage, 0, sizeof(pm_memusage_t));

	//__android_log_print(ANDROID_LOG_INFO, "wetest"," GetMemory: G:"+stMapVec.size());
	for (int i = 0; i < stMapVec.size(); i++) {
		if (GetMapUsage(&stMapVec[i])) {
			//__android_log_print(ANDROID_LOG_INFO, "wetest"," 4");
			close(m_iPageMapFd);
			close(m_stKernel.kpagecount_fd);
			return -1;
		}
	}

	close(m_iPageMapFd);
	close(m_stKernel.kpagecount_fd);
	return 0;
}

int ProcMemory::GetMapUsage(pm_map_t* map) {
	//__android_log_print(ANDROID_LOG_INFO, "wetest"," GetMemory: C");
	if (!map)
		return -1;

	uint64_t *pagemap;
	size_t len;
	int iRet = GetMapPageMap(map, &pagemap, &len);
	//__android_log_print(ANDROID_LOG_INFO, "wetest"," GetMemory: D");
	if (iRet)
		return iRet;

	//__android_log_print(ANDROID_LOG_INFO, "wetest","-------------get PID APPLication: %d---------------", len);

	for (int i = 0; i < len; i++) {
		if (!PM_PAGEMAP_PRESENT(pagemap[i]) || PM_PAGEMAP_SWAPPED(pagemap[i]))
			continue;

		uint64_t count;
		iRet = GetKernelCount(PM_PAGEMAP_PFN(pagemap[i]), &count);
		//__android_log_print(ANDROID_LOG_INFO, "wetest","-------------get PID APPLication: %d---------------", count);
		if (iRet) {
			free(pagemap);
			return -1;
		}

		//__android_log_print(ANDROID_LOG_INFO, "wetest"," GetMemory: E");
		m_stUsage.vss += m_stKernel.pagesize;
		m_stUsage.rss += (count >= 1) ? (m_stKernel.pagesize) : (0);
		m_stUsage.pss += (count >= 1) ? (m_stKernel.pagesize / count) : (0);
		m_stUsage.uss += (count == 1) ? (m_stKernel.pagesize) : (0);

		//__android_log_print(ANDROID_LOG_INFO, "wetest","get PID APPLication: %d", m_stUsage.vss);
	}

	//__android_log_print(ANDROID_LOG_INFO, "wetest"," GetMemory: F");
	free(pagemap);
	return 0;
}

int ProcMemory::GetMapPageMap(pm_map_t *map, uint64_t **pagemap_out,
		size_t *len) {
	if (!map || (map->start >= map->end) || !pagemap_out || !len)
		return -1;

	int firstpage = map->start / m_stKernel.pagesize;
	int numpages = (map->end - map->start) / m_stKernel.pagesize;

	uint64_t* range = (uint64_t*) malloc(numpages * sizeof(uint64_t));
	if (!range)
		return -1;

	off_t off = lseek(m_iPageMapFd, firstpage * sizeof(uint64_t), SEEK_SET);
	if (off == (off_t) - 1) {
		free(range);
		return -1;
	}

	int iRet = read(m_iPageMapFd, (char*) range, numpages * sizeof(uint64_t));
	if (iRet == 0) {
		/* EOF, mapping is not in userspace mapping range (probably vectors) */
		*len = 0;
		free(range);
		*pagemap_out = NULL;
		return 0;
	} else if (iRet < 0
			|| (iRet > 0 && iRet < (int) (numpages * sizeof(uint64_t)))) {
		free(range);
		return iRet;
	}

	*pagemap_out = range;
	*len = numpages;
	return 0;
}

int ProcMemory::GetKernelCount(unsigned long pfn, uint64_t *count_out) {
	if (!count_out)
		return -1;

	off_t off = lseek(m_stKernel.kpagecount_fd, pfn * sizeof(uint64_t),
			SEEK_SET);
	if (off == (off_t) - 1)
		return -1;
	if (read(m_stKernel.kpagecount_fd, count_out, sizeof(uint64_t))
			< (ssize_t) sizeof(uint64_t))
		return -1;

	return 0;
}

int ProcMemory::GetMemory(int pid, pm_memusage_t* pUsage) {
	if (pid <= 0)
		return -1;

	//__android_log_print(ANDROID_LOG_INFO, "wetest"," GetMemory: %d", pid);
	memset(pUsage, 0, sizeof(pm_memusage_t));
	GetProcMem(pid);
	//LOG_VERBOSE("GetMemory1");
	//__android_log_print(ANDROID_LOG_INFO, "wetest"," GetMemory: %d", GetProcMem(pid));
	//LOG_VERBOSE("GetMemory2");
//	clock_t start, end;
//	start = clock();
//	DumpMemInfo(pid);
//	end = clock();
	//__android_log_print(ANDROID_LOG_DEBUG,"meminfo","NDK :cost %f: %8u%8u%8u",float(end-start)*1000/CLOCKS_PER_SEC,m_stUsage.native, m_stUsage.dalvik, m_stUsage.total );
	//start = clock();
	//GetMemInfo(pid);
	//end= clock();
	//__android_log_print(ANDROID_LOG_DEBUG,"meminfo","JAVA:cost %f: %8u%8u%8u",float(end-start)*1000/CLOCKS_PER_SEC,m_stUsage.native, m_stUsage.dalvik, m_stUsage.total );
	//LOG_VERBOSE("GetMemory3");

	memcpy(pUsage, &m_stUsage, sizeof(pm_memusage_t));
	return 0;
}

static struct pid {
	struct pid *next;
	FILE *fp;
	pid_t pid;
}*pidlist;

int ProcMemory::DumpMemInfo(int pid) {
	char szCmd[32];
	char line[128];
	sprintf(szCmd, "dumpsys meminfo %d", pid);

	FILE* fp = mypopen(szCmd, "r");
	if (!fp) {
		return -1;
	}

	int type = 0;
	string key, val;
	while (!feof(fp)) {
		if (fgets(line, 128, fp) == NULL) {
			break;
		}

		stringstream ss(line);
		ss >> key >> val;
		if (key == "Pss" && val == "Dirty") {
			type = 1;
			continue;
		}

		if (key == "native" && val == "dalvik") {
			type = 2;
			continue;
		}

		if (type == 1) {
			if (key == "Native") {
				m_stUsage.native = atoi(val.c_str());
			} else if (key == "Dalvik") {
				m_stUsage.dalvik = atoi(val.c_str());
			} else if (key == "TOTAL") {
				m_stUsage.total = atoi(val.c_str());
				break;
			}
		} else if (type == 2) {
			if (key == "(Pss):") {
				m_stUsage.native = atoi(val.c_str());
				string other;
				ss >> m_stUsage.dalvik >> other >> m_stUsage.total;
				break;
			}
		}
	}

	mypclose(fp);
	return 0;
}

