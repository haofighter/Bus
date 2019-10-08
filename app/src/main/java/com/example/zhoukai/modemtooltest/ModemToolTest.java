package com.example.zhoukai.modemtooltest;


import android.util.Log;

public class ModemToolTest {

    // Used to load the 'native-lib' library on application startup.
    static {

        try{
            System.loadLibrary("native-lib");
        }catch (Exception e){
          Log.d("ModemToolTest",
              "static initializer(ModemToolTest.java:14)"+e.toString());
        }

    }
   
 

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native String stringFromJNI();

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is used to get the value of nv items listed in {@link NvConstants}.
     */
    public static native String getItem(int nvid);

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is used to set the value of nv items listed in {@link NvConstants}.
     */
    public static native int setItem(int nvid, String value);
}