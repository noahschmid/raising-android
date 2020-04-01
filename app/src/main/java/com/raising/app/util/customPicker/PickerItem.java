package com.raising.app.util.customPicker;

public interface PickerItem {
    long getId();
    String getName();
    boolean isChecked();
    void setChecked(boolean checked);
    void setName(String name);
    void setId(long id);
    long getParentId();
}
