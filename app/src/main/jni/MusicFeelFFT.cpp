#include "SuperpoweredFFT.h"
#include <jni.h>
#include <stdio.h>


extern "C" {

    JNIEXPORT void JNICALL Java_com_djdenpa_learn_musicfeel_screenDrawer_FFTScreenDrawerRunnable_calcFFTReal
            (JNIEnv *env, jobject obj, jfloatArray real, jfloatArray imaginary, jint size) {


        jfloat* c_real = (env)->GetFloatArrayElements(real, 0);
        // do some exception checking
        if (c_real == NULL) {
            return; /* exception occurred */
        }
        jfloat* c_img = (env)->GetFloatArrayElements(imaginary, 0);
        // do some exception checking
        if (c_img == NULL) {
            return; /* exception occurred */
        }
//        SuperpoweredFFTReal(c_real, c_img, size, true);
        c_img[0] = 0;

        (env)->ReleaseFloatArrayElements(real, c_real, JNI_ABORT);
        (env)->ReleaseFloatArrayElements(imaginary, c_img, JNI_ABORT);

//        env->SetFloatArrayRegion(real, 0, size, c_real);
//        env->SetFloatArrayRegion(imaginary, 0, size, c_img);

//        jfloat *cReal = (env)->GetFloatArrayElements(real, 0);
//        jfloat *cImg = (env)->GetFloatArrayElements(imaginary, 0);
//        SuperpoweredFFTReal(cReal, cImg, size, true);
//        int arrLength = pow(2, size);
//        env->SetFloatArrayRegion(real, 0, size, cReal);
//        env->SetFloatArrayRegion(imaginary, 0, size, cImg);
    }

}