/*
 * Copyright (C) 2014-2015 The CyanogenMod Project
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

package com.vortex.settings.fragments;

import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import com.android.settings.R;
import android.support.annotation.NonNull;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.SettingsPreferenceFragment;
import com.vortex.settings.preferences.Utils;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import com.vortex.settings.preferences.Utils;
import com.vortex.settings.preferences.CustomSeekBarPreference;

public class PowerMenuSettings extends SettingsPreferenceFragment
                implements Preference.OnPreferenceChangeListener {


    private static final String KEY_POWERMENU_TORCH = "powermenu_torch";

    private SwitchPreference mPowermenuTorch;
    private static final String PREF_ON_THE_GO_ALPHA = "on_the_go_alpha";
    private static final String SCREEN_OFF_ANIMATION = "screen_off_animation";

    private CustomSeekBarPreference mOnTheGoAlphaPref;

    private ListPreference mScreenOffAnimation;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.vortex_settings_power);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        mPowermenuTorch = (SwitchPreference) findPreference(KEY_POWERMENU_TORCH);
        mPowermenuTorch.setOnPreferenceChangeListener(this);
        if (!Utils.deviceSupportsFlashLight(getActivity())) {
            prefScreen.removePreference(mPowermenuTorch);
        } else {
        mPowermenuTorch.setChecked((Settings.System.getInt(resolver,
                Settings.System.POWERMENU_TORCH, 0) == 1));
        }
		
        mScreenOffAnimation = (ListPreference) findPreference(SCREEN_OFF_ANIMATION);
        int screenOffAnimation = Settings.Global.getInt(getContentResolver(),
                Settings.Global.SCREEN_OFF_ANIMATION, 0);
        mScreenOffAnimation.setValue(Integer.toString(screenOffAnimation));
        mScreenOffAnimation.setSummary(mScreenOffAnimation.getEntry());
        mScreenOffAnimation.setOnPreferenceChangeListener(this);

        mOnTheGoAlphaPref = (CustomSeekBarPreference) findPreference(PREF_ON_THE_GO_ALPHA);
        float otgAlpha = Settings.System.getFloat(getContentResolver(),
                Settings.System.ON_THE_GO_ALPHA, 0.5f);
        final int alpha = ((int) (otgAlpha * 100));
        mOnTheGoAlphaPref.setValue(alpha);
        mOnTheGoAlphaPref.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference == mPowermenuTorch) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWERMENU_TORCH, value ? 1 : 0);
            return true;
        }
		
        ContentResolver resolver = getActivity().getContentResolver();
		if (preference == mScreenOffAnimation) {
            int value = Integer.valueOf((String) newValue);
            int index = mScreenOffAnimation.findIndexOfValue((String) newValue);
            mScreenOffAnimation.setSummary(mScreenOffAnimation.getEntries()[index]);
            Settings.Global.putInt(getContentResolver(), Settings.Global.SCREEN_OFF_ANIMATION, value);
            return true;
        } else if (preference == mOnTheGoAlphaPref) {
            float val = (Integer) newValue;
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.ON_THE_GO_ALPHA, val / 100);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.VORTEX_SETTINGS;
    }

}
