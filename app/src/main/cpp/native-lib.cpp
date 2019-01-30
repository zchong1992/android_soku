#include <jni.h>
#include <string>
#include "stdio.h"
#include <ctime>
#include <chrono>
#include <iostream>
using namespace std;
using namespace chrono;
void setPix(int x, int y, int value, int map[9][9])
{
    map[x-1][y-1] = value;
}
int getPix(int x, int y, int map[9][9])
{
    return map[x-1][y-1];
}

void printMap(int map[][9])
{
    int i, j;
    i = j = 0;
    for (j = 1; j <= 9; j++)
    {
        printf("---");
    }
    printf("\n");
    for (i = 1; i <= 9; i++)
    {
        for (j = 1; j <= 9; j++)
        {
            int value=getPix(i, j, map);
            if (value == 0)
                printf("   ");
            else
                printf(" %d ", getPix(i, j, map));
            if (j % 3 == 0)
            {
                printf("|");
            }
        }
        if (i % 3 == 0)
        {
            printf("\n");
            for (j = 1; j <= 9; j++)
            {
                printf("---");
                if (j % 3 == 0)
                {
                    printf("|");
                }
            }
        }
        printf("\n");
    }
}
int LineDup(int value,int x,int y,int map[9][9])
{
    int i = 0;
    for (i=1;i<10;i++)
        if(getPix(i, y, map) == value)
            return 1;
    for (i = 1; i<10; i++)
        if (getPix(x, i, map) == value)
            return 1;
    return 0;
}

int GridDup(int value, int x, int y, int map[9][9])
{
    int i = (x - 1) / 3;
    int j = (y - 1) / 3;
    for (int a = i * 3+1; a<i * 3 + 3 + 1; a++)
        for (int b = j * 3 + 1; b<j * 3 + 3 + 1; b++)
            if (getPix(a, b, map) == value)
                return 1;
    return 0;
}
int IsValid(int value, int x, int y, int map[9][9])
{
    return GridDup(value, x, y, map) || LineDup(value, x, y, map);
}
int calc(int map[9][9])
{
    int i, j;
    i = 0; j = 0 ;

    int as[81][2] = { 0 };
    int step=0;
    for (i = 1; i < 10; i++)
    {
        for (j = 1; j < 10; j++)
        {
            if (getPix(i, j, map) == 0)
            {
                as[step][0] = i;
                as[step][1] = j;
                step++;
            }
        }
    }
    int pos = 0;
    while (as[pos][0]!=0 && pos>=0 && pos<81)
    {
        int x = as[pos][0];
        int y = as[pos][1];
        int cur = getPix(x, y, map);
        if (cur < 9)
        {
            while (IsValid(cur + 1, x, y, map) != 0)
            {
                cur++;
            }
            if(cur<9)
            {
                setPix(x, y, cur + 1, map);
                pos++;
                //printMap(map);
            }
            else
            {
                setPix(x, y, 0, map);
                pos--;
            }
        }
        else
        {
            setPix(x, y, 0, map);
            pos--;
        }
    }
    if (pos<=0)
    {
        printf("decl fail\n");
        return 0;
    }
    return 1;
    /*for (i = 1; i < 10; i++)
    {
        for (j = 1; j < 10; j++)
        {

        }

    }*/

}
int main(int argc,char **argv)
{
    int map[9][9] = { 0 };
    setPix(1, 2, 1, map);
    setPix(1, 3, 2, map);
    setPix(2, 3, 3, map);
    setPix(3, 8, 4, map);
    setPix(4, 9, 5, map);
    setPix(2, 8, 6, map);
    setPix(3, 5, 6, map);
    setPix(1, 1, 6, map);
    setPix(6, 2, 6, map);
    setPix(7, 3, 6, map);
    printMap(map);
    auto start = system_clock::now();
    calc(map);
    auto end = system_clock::now();
    auto duration = duration_cast<microseconds>(end - start);
    printMap(map);
    cout << "花费了"
         << double(duration.count()) * microseconds::period::num / microseconds::period::den
         << "秒" << endl;
    getchar();
}
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_zheng_soku_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_zheng_soku_soku_Gen(JNIEnv *env, jobject instance, jintArray map_) {
    jint *map = env->GetIntArrayElements(map_, NULL);
    // TODO
    int cmap[9][9]={0};
    for(int i=1;i<=9;i++)
    {
        for(int j=1;j<=9;j++)
        {
            setPix(i,j,map[i*9+j-10],cmap);
        }
    }
    int ret=calc(cmap);
    jintArray jarr=env->NewIntArray(82);
    jint *arr=env->GetIntArrayElements(jarr,NULL);
    arr[0]=ret;
    int index=1;
    for(int i=1;i<10;i++)
    {
        for(int j=1;j<10;j++)
        {
            arr[index++]=getPix(i,j,cmap);
        }
    }
    env->ReleaseIntArrayElements(map_, map, 0);
    env->ReleaseIntArrayElements(jarr,arr,0);
    return jarr;
}