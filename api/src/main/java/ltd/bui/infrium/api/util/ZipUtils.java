package ltd.bui.infrium.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

  ZipUtils() {}

  public static boolean unzip(File directoryTo, File zip) throws IOException {
    if (!zip.exists()) {
      System.out.println("ZIP FILE DOES NOT EXIST " + zip.getAbsoluteFile());
      return false;
    }
    if (!directoryTo.exists()) {
      directoryTo.mkdirs();
    }
    byte[] buffer = new byte[1024];
    FileInputStream fis = new FileInputStream(zip);
    ZipInputStream zis = new ZipInputStream(fis);
    ZipEntry zen = zis.getNextEntry();
    while (zen != null) {
      File newFile = new File(directoryTo, zen.getName());
      if (zen.isDirectory()) {
        if (!newFile.isDirectory() && !newFile.mkdirs()) {
          throw new IOException("Failed to create directory " + newFile);
        }
      } else {
        int len;
        File parent = newFile.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) {
          throw new IOException("Failed to create directory " + parent);
        }
        if (newFile.exists()) {
          newFile.delete();
          newFile.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(newFile);
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }
        fos.close();
      }
      zen = zis.getNextEntry();
    }
    zis.closeEntry();
    zis.close();
    fis.close();
    return true;
  }
}
