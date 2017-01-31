package com.bypassmobile.octo.account;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * User preferences that persist for as long as the app is installed
 */

@SharedPref(value=SharedPref.Scope.APPLICATION_DEFAULT)
public interface Preferences {

    /**
     * the last time the cache was cleared
     */
    long lastClearedCache();

}
