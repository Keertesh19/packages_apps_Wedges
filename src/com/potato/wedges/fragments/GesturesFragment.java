/*
 * Copyright (C) 2017 The PotatoROM Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.potato.wedges.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceChangeListener;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;

public class GesturesFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String KEY_ONEPLUS_GESTURES = "oneplus_gestures";
    private static final String KEY_ONEPLUS_GESTURES_PACKAGE_NAME = "com.cyanogenmod.settings.device";

    private PreferenceScreen mOneplusGestures;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.gestures_main);

        PreferenceScreen prefSet = getPreferenceScreen();
        mOneplusGestures = (PreferenceScreen) findPreference(KEY_ONEPLUS_GESTURES);
        if (!isAppInstalled(getContext(), KEY_ONEPLUS_GESTURES_PACKAGE_NAME)) {
            prefSet.removePreference(mOneplusGestures);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        return false;
    }


    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.POTATO_WEDGES;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
