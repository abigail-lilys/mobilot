package org.mobilot.runtime;

public final class NativeBridge {
    static { System.loadLibrary("mobilot_core"); }

    private NativeBridge() {}
    public static native String nativeVersion();
    public static native void nativeInit(String assetsPath);
}
