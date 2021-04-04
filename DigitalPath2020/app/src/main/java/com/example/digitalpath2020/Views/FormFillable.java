package com.example.digitalpath2020.Views;

import android.widget.EditText;

public interface FormFillable {
    boolean checkValidity(String[] formInputs, EditText[] forms, String[] errorMessages);
    void inputForm(String[] formInputs);
}
