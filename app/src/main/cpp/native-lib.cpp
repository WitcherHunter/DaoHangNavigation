#include <jni.h>
#include <string>



extern "C"
JNIEXPORT jstring JNICALL
Java_com_serenegiant_utils_IUtil_PassWord(
        JNIEnv* env,
        jobject /* this com.serenegiant.utils */) {
//    std::string hello = "6a3775217459";
    std::string hello = "613161326133";
//    std::string hello = "682a56357426";
//    std::string hello = "FFFFFFFFFFFF";
//    四川： 30 77 49 3d 78 61

//    贵阳：  5e 34 41 44 67 6c
//    娄底：	68 2a 56 35 74 26
//    陇南：  6a 37 75 21 74 59
//    白银    6c 66 26 39 69 51
//    汕尾：	31 72 24 48 57 4a
//    甘肃：	28 66 c4 2f e6 a8
//    8ee46d0e4d16 4e9a8b3440e ba2f348d3
    return env->NewStringUTF(hello.c_str());
}
