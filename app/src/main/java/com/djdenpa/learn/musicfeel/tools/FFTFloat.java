package com.djdenpa.learn.musicfeel.tools;

/**
 * Created by H on 2017/02/04.
 */

public class FFTFloat {

    int n, m;

    // Lookup tables. Only need to recompute when size of FFTDouble changes.
    float[] cos;
    float[] sin;

    public FFTFloat(int n) {
        this.n = n;
        this.m = (int) (Math.log(n) / Math.log(2));

        // Make sure n is a power of 2
        if (n != (1 << m))
            throw new RuntimeException("FFTfloat length must be power of 2");

        // precompute tables
        cos = new float[n / 2];
        sin = new float[n / 2];

        for (int i = 0; i < n / 2; i++) {
            cos[i] = (float) Math.cos(-2 * Math.PI * i / n);
            sin[i] = (float) Math.sin(-2 * Math.PI * i / n);
        }

    }

    public void fft(float[] input, float[] output) {
        int i, j, k, n1, n2, a;
        float c, s, t1, t2;

        // Bit-reverse
        j = 0;
        n2 = n / 2;
        for (i = 1; i < n - 1; i++) {
            n1 = n2;
            while (j >= n1) {
                j = j - n1;
                n1 = n1 / 2;
            }
            j = j + n1;

            if (i < j) {
                t1 = input[i];
                input[i] = input[j];
                input[j] = t1;
                t1 = output[i];
                output[i] = output[j];
                output[j] = t1;
            }
        }

        // FFTDouble
        n1 = 0;
        n2 = 1;

        for (i = 0; i < m; i++) {
            n1 = n2;
            n2 = n2 + n2;
            a = 0;

            for (j = 0; j < n1; j++) {
                c = cos[a];
                s = sin[a];
                a += 1 << (m - i - 1);

                for (k = j; k < n; k = k + n2) {
                    t1 = c * input[k + n1] - s * output[k + n1];
                    t2 = s * input[k + n1] + c * output[k + n1];
                    input[k + n1] = input[k] - t1;
                    output[k + n1] = output[k] - t2;
                    input[k] = input[k] + t1;
                    output[k] = output[k] + t2;
                }
            }
        }
    }
}