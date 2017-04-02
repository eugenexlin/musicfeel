#include "SuperpoweredDecoder.h"
#include "SuperpoweredRecorder.h"
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>

extern "C" {


    JNIEXPORT void JNICALL Java_com_djdenpa_learn_musicfeel_VisualScanMusicActivity_CreateWavFile
            (JNIEnv *env, jobject obj, jstring sInPath, jstring sOutPath) {
        //Get the native string from javaString
        const char *nsPath = env->GetStringUTFChars(sInPath, JNI_FALSE);
        SuperpoweredDecoder *decoder = new SuperpoweredDecoder();
        const char *openError = decoder->open(nsPath, false, 0, 0);
        if (openError) {
            __android_log_print(ANDROID_LOG_VERBOSE, "MusicFeelDecoder", "open error: %s", openError);
            delete decoder;
            return;
        };


        const char *nsOutPath = env->GetStringUTFChars(sOutPath, JNI_FALSE);
        // Create a buffer for the 16-bit integer samples.
        short int *intBuffer = (short int *)malloc(decoder->samplesPerFrame * 2 * sizeof(short int) + 32768);

        FILE *fd = createWAV(nsOutPath, decoder->samplerate, 2); //decoder->samplerate

        // Processing.
        while (true) {
            // Decode one frame. samplesDecoded will be overwritten with the actual decoded number of samples.
            unsigned int samplesDecoded = decoder->samplesPerFrame;
            if (decoder->decode(intBuffer, &samplesDecoded) == SUPERPOWEREDDECODER_ERROR) break;
            if (samplesDecoded < 1) break;

            // Write the audio to disk.
            fwrite(intBuffer, 1, samplesDecoded * 4, fd);

            // Update the progress indicator.
           // float progress;
          //  progress = (double)decoder->samplePosition / (double)decoder->durationSamples;
        };


        closeWAV(fd);

        delete decoder;

        free(intBuffer);

    }

}