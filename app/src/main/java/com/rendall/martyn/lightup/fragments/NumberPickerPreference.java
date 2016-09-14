package com.rendall.martyn.lightup.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

public class NumberPickerPreference extends DialogPreference {

    public static final int DEFAULT_STEPPED_VALUE = 60;
    public static final int MIN_PICKER_VALUE = 0;
    public static final int MAX_PICKER_VALUE = 24;
    public static final int PICKER_STEP_SIZE = 5;

    private int currentValue;
    private NumberPicker numberPicker;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected View onCreateDialogView() {
        numberPicker = new NumberPicker(getContext());

        numberPicker.setMinValue(MIN_PICKER_VALUE);
        numberPicker.setMaxValue(MAX_PICKER_VALUE);
        numberPicker.setDisplayedValues(getDisplayValues(PICKER_STEP_SIZE, MIN_PICKER_VALUE, MAX_PICKER_VALUE));

        onSetInitialValue(true, DEFAULT_STEPPED_VALUE);
        numberPicker.setValue(currentValue);

        // Prevents the user entering a value via keyboard input
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        return numberPicker;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {

        if (positiveResult) {
            numberPicker.clearFocus();
            persistInt(getSteppedValue(numberPicker.getValue()));
            currentValue = numberPicker.getValue();
        }
    }

    private int getSteppedValue(int pickerIndex) {
        return pickerIndex * PICKER_STEP_SIZE;
    }

    private int getPickerIndex(int steppedValue) {
        return steppedValue / PICKER_STEP_SIZE;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            currentValue = getPickerIndex(getPersistedInt(DEFAULT_STEPPED_VALUE));

        } else {
            currentValue = (Integer) defaultValue;
            persistInt(getSteppedValue(currentValue));

        }

        notifyChanged();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_STEPPED_VALUE);
    }

    private String[] getDisplayValues(int stepSize, int minValue, int maxValue) {

        int size = (maxValue - minValue) + 1;

        String[] values = new String[size];

        for (int i=0; i < size; i++) {
            values[i] = String.valueOf(minValue + (i * stepSize));
        }

        return values;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();

        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            // No need to save instance state since its persistent,
            // use superclass state
            return superState;
        }

        final SavedState myState = new SavedState(superState);

        myState.value = numberPicker.getValue();

        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        // Check whether we saved the state in on SaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didnt save the state so call super class
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        numberPicker.setValue(myState.value);
    }

    private static class SavedState extends BaseSavedState {
        int value;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            value = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(value);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
