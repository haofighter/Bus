// IToolInterface.aidl
package com.lilei.tool.tool;

// Declare any non-default types here with import statements

interface IToolInterface {
   void setDateTime(int year, int month, int day, int hour, int minute,int second);
   String apkInstall(String path);
   String apkList();
   String apkUninstall(String sPackage);
   String getForegroundApp();
   void updateSystem(String path);
   void wipe_data();
   void device_reboot();
}
