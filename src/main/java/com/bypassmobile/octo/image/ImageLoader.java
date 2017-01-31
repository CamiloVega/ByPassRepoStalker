package com.bypassmobile.octo.image;


import android.content.Context;
import android.widget.ImageView;

import com.bypassmobile.octo.account.Preferences_;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Calendar;

//Android Annotation that automates the use of the class as a singleton.
@EBean (scope = EBean.Scope.Singleton)
public class ImageLoader {
    private static int MAX_CACHE_DAYS = 2;

    private static int MAX_CACHE_SIZE = 2048;

    private Picasso singleton;

    private ImageCache imageCache;

    //Annotation that automates the access to the root context(aliviates the update of the context)
    @RootContext
    Context context;

    //Annotation that automates the access to the preferences file which persists properties
    @Pref
    Preferences_ preferences;

    public Picasso createImageLoader(Context context){
        if(singleton == null){
            singleton = new Picasso.Builder(context).memoryCache(getCache()).build();
        }
        return singleton;
    }

    /**
     * Convience method that takes an url and an image view and loads the image
     * using a circle transformation to round the image's edges
     * @param url the url where the image is located
     * @param imageView the image view to be populated.
     */
    public void loadCircularImage(String url, ImageView imageView) {
        RequestCreator rc = createImageLoader(context).load(url);
        rc.transform(new CircleTransformation());
        rc.into(imageView);
        shouldClearCache();

    }

    public ImageCache getCache() {
        if (imageCache == null) {
            imageCache = new ImageCache();
            long now = System.currentTimeMillis();
            preferences.lastClearedCache().put(now);
        }
        return imageCache;
    }

    /**
     * Convenience method to determine if the Size of the cache exceeds MAX_CACHE_SIZE or if
     * the cache has not been cleared for MAX_CACHE_DAYS
     */
    public void shouldClearCache() {
        Calendar lastClearedTime = Calendar.getInstance();
        lastClearedTime.setTimeInMillis(preferences.lastClearedCache().getOr((long) 0));
        Calendar currentTime = Calendar.getInstance();
        lastClearedTime.add(Calendar.DATE, MAX_CACHE_DAYS);
        if (getCache().size() > MAX_CACHE_SIZE || currentTime.after(lastClearedTime)){
            clearImageCache();
        }
    }

    /**
     * Clears the image cache as well as set the lastClearDate to the current time
     */
    private void clearImageCache() {
        getCache().clear();
        preferences.lastClearedCache().put(System.currentTimeMillis());
    }
}
