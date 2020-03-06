#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

JNICALL
Java_com_mymusic_orvai_travel_1with_Login_Activity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
