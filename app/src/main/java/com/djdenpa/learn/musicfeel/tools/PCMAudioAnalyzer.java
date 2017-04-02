package com.djdenpa.learn.musicfeel.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by H on 2016/02/07.
 */
public class PCMAudioAnalyzer {

    private IPCMDecoder decoder = null;

    public void analyzeMusicFile(String filePath){

        if (filePath.endsWith(".mp3")) {
            // decoder = new MP3PCMDecoder();
        } else if (filePath.endsWith(".wav")) {
            //    decoder = new MP3PCMDecoder();
        } else if (filePath.endsWith(".ogg")) {
            //    decoder = new MP3PCMDecoder();
        }

        if (decoder == null){
            return;
        }

        decoder.OpenFile(filePath);

    }

    public void Terminate(){
        if (decoder != null) {
            decoder.Terminate();
        }
    }

    /**
     * Created by H on 2016/02/12.
     */
    public abstract static class IPCMDecoder {

        static final int MAX_BUFFER_SIZE = 500000;

        protected boolean isRunning = true;

        protected int scrollOffset = 0;
        protected int newScrollOffset = 0;

        protected String audioFilePath = null;
        protected List<List<Double>> decodedData = null;

        private boolean isDoneLoading = false;
        private Object syncLockInit = new Object();
        private Object syncLockScroll = new Object();

        //properties like this so you wont forget to implement these
        public abstract int getFrequency();
        public abstract int getChannels();

        protected Thread asyncThread = null;

        //the moment you open this file, you have committed to loading the entire PCM in.
        public void OpenFile(String pFilePath){
            synchronized(syncLockInit) {
                if (decodedData != null){
                    //called open file more than once??
                    return;
                }
                List<Double> Decoded1 =  Collections.synchronizedList(new ArrayList<Double>());
                List<Double> Decoded2 =  Collections.synchronizedList(new ArrayList<Double>());
                decodedData = Collections.synchronizedList(new ArrayList<List<Double>>());
                decodedData.add(Decoded1);
                decodedData.add(Decoded2);
                audioFilePath = pFilePath;
                StartAsyncLoader();
            }
        }

        public void StartAsyncLoader(){
            Runnable runner = new AsyncDecoderRunner(this);
            Thread thread = new Thread(runner);
            asyncThread = thread;
            thread.setDaemon(true);
            thread.start();
        }

        public class AsyncDecoderRunner implements Runnable {
            public IPCMDecoder decoder;
            public AsyncDecoderRunner(IPCMDecoder pDecoder){
                decoder = pDecoder;
            }
            @Override
            public void run() {
                try
                {
                    decoder.AsyncLoaderWork();
                }
                finally
                {
                    isDoneLoading = true;
                }
            }
        }

        public abstract void AsyncLoaderWork();

        public void appendToData(int channel, double data){
            if (channel > decodedData.size()){
                return;
            }
            List<Double> targetData = decodedData.get(channel);
            while (targetData.size() >= MAX_BUFFER_SIZE){
                if (!isRunning){
                    return;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
            targetData.add(data);
        }


        public double[] ReadDecodedBytes(int index, int count){
            return ReadDecodedBytes(index, count, 0);
        }
        //returns the total number read -1 for end
        // will sleep the calling thread until done :/
        public double[] ReadDecodedBytes(int index, int count, int channel){

            List<Double> targetData = decodedData.get(channel);

            for(;;){
                if (!isRunning){
                    break;
                }
                int requiredIndex;
                synchronized (syncLockScroll) {
                    requiredIndex = index + count - scrollOffset;
                    if (index + count - scrollOffset> targetData.size()){
                        if (isDoneLoading){
                            break;
                        }
                    }else{
                        break;
                    }
                }
                try {
                    Thread.sleep(200);
                    continue;
                } catch (InterruptedException e) {
                    return new double[0];
                }
            }


            synchronized (syncLockScroll) {
                int startingIndex = index - scrollOffset;
                if (startingIndex < 0) {
                    return new double[0];
                }

                //min to size, just in case we are reading a chunk, but it is the last chunk from isDoneLoading, so not full
                int endingIndex = Math.min(startingIndex + count, targetData.size());

                double[] result = new double[endingIndex - startingIndex];

                for (int i = startingIndex; i < endingIndex; i++) {
                    result[i - startingIndex] = targetData.get(i);
                }
                return result;
            }
        }

        public void Terminate(){
            isRunning = false;
        }

        //since our buffer has a max, this function is to state what index we are done with the data for, and thus shoudl be discarded.
        public void scrollData(int minimumIndex){
            int diff = minimumIndex - scrollOffset;
            if (diff <= 0) {
                return;
            }
            synchronized (syncLockScroll) {
                for (int i = 0; i < getChannels(); i++) {
                    decodedData.get(i).subList(0, diff).clear();
                }
                scrollOffset = minimumIndex;
            }
        }
    }
}
