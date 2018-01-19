package io.yoobi;

import java.io.File;

/**
 * Created by GEMVN on 1/19/2018.
 */
public class FileManager
{
    public static File getDesktopFile(String url)
    {
        return new File( System.getProperty("user.home") + "/Desktop/" + url );
    }
}
