package com.example.musicify;

import android.graphics.Bitmap;
import android.util.LruCache;

public class AlbumArtCache {
    //all the numbers are in pixels
    private static int max_width = 128;
    private static int max_height = 128;
    public static int max_album_art_cache_size = 12 * 1024 * 1024;
    private LruCache<String, Bitmap> cache;

    public AlbumArtCache(){
        int maxsize = Math.min(max_album_art_cache_size, (int) (Math.min(Integer.MAX_VALUE, Runtime.getRuntime().maxMemory()/4)));
        cache = new LruCache<String, Bitmap>(maxsize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

}
