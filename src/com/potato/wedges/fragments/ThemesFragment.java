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

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceChangeListener;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.content.Context;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v7.preference.ListPreference;
import java.util.ArrayList;
import java.util.List;
import libcore.util.Objects;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;

public class ThemesFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {


    private static final String KEY_THEME_COLOR = "theme_color";
    private OverlayManager mOverlayService;
    private PackageManager mPackageManager;
    private ListPreference mSystemThemeColor;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.themes_main);
        mSystemThemeColor = (ListPreference) findPreference(KEY_THEME_COLOR);
        mOverlayService = ServiceManager.getService(Context.OVERLAY_SERVICE) != null
                ? new OverlayManager() : null;
        mContext = getContext();
        mPackageManager = mContext.getPackageManager();
        String[] pkgs = getAvailableThemes();
        CharSequence[] labels = new CharSequence[pkgs.length];
        for (int i = 0; i < pkgs.length; i++) {
            try {
                labels[i] = mPackageManager.getApplicationInfo(pkgs[i], 0)
                        .loadLabel(mPackageManager);
            } catch (NameNotFoundException e) {
                labels[i] = pkgs[i];
            }
        }
        mSystemThemeColor.setEntries(labels);
        mSystemThemeColor.setEntryValues(pkgs);
        String theme = getCurrentTheme();
        CharSequence themeLabel = null;

        for (int i = 0; i < pkgs.length; i++) {
            if (TextUtils.equals(pkgs[i], theme)) {
                themeLabel = labels[i];
                break;
            }
        }

        if (TextUtils.isEmpty(themeLabel)) {
            themeLabel = mContext.getString(R.string.default_theme);
        }

        mSystemThemeColor.setSummary(themeLabel);
        mSystemThemeColor.setValue(theme);
        mSystemThemeColor.setOnPreferenceChangeListener(this);
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
        if (preference == mSystemThemeColor)
        {
            String current = getTheme();
            Log.i("FAB_THEMER", " objValue: " + (String) objValue);
            Log.i("FAB_THEMER", " current: " + current);
            if (Objects.equal(objValue, current)) {
                return true;
            }
            try {
                mOverlayService.setEnabledExclusive((String) objValue, true, UserHandle.myUserId());
            } catch (RemoteException e) {
                return false;
            }
            return true;
        }
        return true;
    }


    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.POTATO_WEDGES;
    }

    private boolean isChangeableOverlay(String packageName) {
        try {
            PackageInfo pi = mPackageManager.getPackageInfo(packageName, 0);
            return pi != null && (pi.overlayFlags & PackageInfo.FLAG_OVERLAY_STATIC) == 0;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    String getCurrentTheme() {
        return getTheme();
    }

    private String getTheme() {
        try {
            List<OverlayInfo> infos = mOverlayService.getOverlayInfosForTarget("android",
                    UserHandle.myUserId());
            for (int i = 0, size = infos.size(); i < size; i++) {
                if (infos.get(i).isEnabled() &&
                        isChangeableOverlay(infos.get(i).packageName)) {
                    return infos.get(i).packageName;
                }
            }
        } catch (RemoteException e) {
        }
        return null;
    }

    String[] getAvailableThemes() {
        try {
            List<OverlayInfo> infos = mOverlayService.getOverlayInfosForTarget("android",
                    UserHandle.myUserId());
            List<String> pkgs = new ArrayList(infos.size());
            for (int i = 0, size = infos.size(); i < size; i++) {
                if (isChangeableOverlay(infos.get(i).packageName)) {
                    pkgs.add(infos.get(i).packageName);
                }
            }
            return pkgs.toArray(new String[pkgs.size()]);
        } catch (RemoteException e) {
        }
        return new String[0];
    }

    public static class OverlayManager {
        private final IOverlayManager mService;

        public OverlayManager() {
            mService = IOverlayManager.Stub.asInterface(
                    ServiceManager.getService(Context.OVERLAY_SERVICE));
        }

        public void setEnabledExclusive(String pkg, boolean enabled, int userId)
                throws RemoteException {
            mService.setEnabledExclusive(pkg, enabled, userId);
        }

        public List<OverlayInfo> getOverlayInfosForTarget(String target, int userId)
                throws RemoteException {
            return mService.getOverlayInfosForTarget(target, userId);
        }
    }


}
