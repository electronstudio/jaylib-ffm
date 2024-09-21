package uk.co.electronstudio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Util {

    public static final String OS_ARCH = System.getProperty("os.arch");
    public static final String OS_NAME = System.getProperty("os.name");
    public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
    public static final boolean IS_OS_LINUX = OS_NAME.startsWith("Linux") || OS_NAME.startsWith("LINUX");
    public static final boolean IS_OS_LINUX_AMD64 = IS_OS_LINUX && OS_ARCH.startsWith("amd64");
    public static final boolean IS_OS_MAC = OS_NAME.startsWith("Mac");
    public static final boolean IS_OS_WINDOWS = OS_NAME.startsWith("Windows");

    public static String extractDLLforOS(){
        if(IS_OS_LINUX_AMD64) {
            return extractFileFromResources("libraylib", ".so");
        }else if(IS_OS_MAC){
            return extractFileFromResources("libraylib", ".dylib");
        }else if(IS_OS_WINDOWS){
            return extractFileFromResources("raylib", ".dll");
        }else{
            return "libraylib.so";
        }
    }

    public static String extractFileFromResources(String name, String extension) {
        try {
            // ensure that the library's temp directory exists in the system temp dir
            Path libraryTempDir = Paths.get(TMP_DIR, "jaylib-ffm");
            Files.createDirectories(libraryTempDir);

            // get specified file resource from library jar
            String path = "/" + name + extension;
            InputStream source = null;
            try {
                source = Util.class.getResourceAsStream(path);
            } catch (Exception e){
                e.printStackTrace(System.err);
            }
            if(source==null) source = ClassLoader.getSystemResourceAsStream(path);
            if(source==null){
                throw new RuntimeException("Couldn't extract "+name+extension+" from resources");
            }

            // copy file into libraryTempDir, overwriting if it already exists
            Path extractedLoc = libraryTempDir.resolve(name+extension);
            Files.copy(source, extractedLoc, StandardCopyOption.REPLACE_EXISTING);

            // absolute path to extracted file
            return extractedLoc.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
