/*
 * Copyright (C) 2018 The Potato Open Sauce Project
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
 *
 *
 */

package com.potato.wedges.updates;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.VisibleForTesting;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.PreferenceScreen;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.applications.LayoutPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.Utils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;

public class UpdatesHeaderPreferenceController extends AbstractPreferenceController
        implements PreferenceControllerMixin, LifecycleObserver, OnStart {
    @VisibleForTesting
    static final String KEY_UPDATES_HEADER = "updates_entity_header";

    @VisibleForTesting
    TextView mDeviceName;
    @VisibleForTesting
    TextView mSummary1;
    @VisibleForTesting
    TextView mSummary2;

    private final Activity mActivity;
    private final PreferenceFragment mHost;
    private final Lifecycle mLifecycle;

    private LayoutPreference mUpdatesLayoutPref;

    public UpdatesHeaderPreferenceController(Context context, Activity activity,
            PreferenceFragment host, Lifecycle lifecycle) {
        super(context);
        mActivity = activity;
        mHost = host;
        mLifecycle = lifecycle;
        if (mLifecycle != null) {
            mLifecycle.addObserver(this);
        }
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mUpdatesLayoutPref = (LayoutPreference) screen.findPreference(KEY_UPDATES_HEADER);
        mDeviceName = mUpdatesLayoutPref.findViewById(R.id.device_name);
        mSummary1 = mUpdatesLayoutPref.findViewById(R.id.text1);
        mSummary2 = mUpdatesLayoutPref.findViewById(R.id.text2);

        quickUpdateHeaderPreference();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_UPDATES_HEADER;
    }

    @Override
    public void onStart() {
        EntityHeaderController.newInstance(mActivity, mHost,
                mUpdatesLayoutPref.findViewById(R.id.updates_entity_header))
                .setRecyclerView(mHost.getListView(), mLifecycle)
                .styleActionBar(mActivity);
    }

    public void updateHeaderPreference() {
        mDeviceName.setText(Build.DEVICE);
        mSummary1.setText(Build.BRAND);
        // Clear this just to be sure we don't get UI jank on re-entering this view from another
        // activity.
        mSummary2.setText("");

    }

    public void quickUpdateHeaderPreference() {
        mDeviceName.setText(Build.DEVICE);

        // clear all the summaries
        mSummary1.setText(Build.BRAND);
        mSummary2.setText("");
    }
}
