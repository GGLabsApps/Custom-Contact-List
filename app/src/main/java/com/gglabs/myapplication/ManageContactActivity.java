package com.gglabs.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ManageContactActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "ManageContactActivity";

    private boolean editMode = false;
    private Intent receivedIntent;

    EditText etName, etPhoneNum;
    ImageView ivMale, ivFemale, ivBird;
    RadioGroup rgColor;
    RadioButton rbRed, rbGreen, rbBlue;
    Button btnOk;

    ImageView selectedImg;
    int imgSelectColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    private void init() {
        etName = (EditText) findViewById(R.id.et_name);
        etPhoneNum = (EditText) findViewById(R.id.et_phone_number);
        ivMale = (ImageView) findViewById(R.id.iv_male);
        ivFemale = (ImageView) findViewById(R.id.iv_female);
        ivBird = (ImageView) findViewById(R.id.iv_bird);
        rgColor = (RadioGroup) findViewById(R.id.rg_color);
        rbRed = (RadioButton) findViewById(R.id.rb_red);
        rbGreen = (RadioButton) findViewById(R.id.rb_green);
        rbBlue = (RadioButton) findViewById(R.id.rb_blue);
        btnOk = (Button) findViewById(R.id.btn_ok);

        ivMale.setTag(R.drawable.ic_male);
        ivFemale.setTag(R.drawable.ic_female);
        ivBird.setTag(R.drawable.ic_bird);

        ivMale.setOnClickListener(this);
        ivFemale.setOnClickListener(this);
        ivBird.setOnClickListener(this);

        rgColor.setOnCheckedChangeListener(this);
        btnOk.setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);

        imgSelectColor = getResources().getColor(R.color.color_selected_img);


        receivedIntent = getIntent();
        loadContact(receivedIntent);
    }

    private void loadContact(Intent intent) {
        Contact contact = (Contact) intent.getSerializableExtra("edit_contact");
        if (contact == null) return;
        editMode = true;

        etName.setText(contact.getName());
        etPhoneNum.setText(contact.getPhone());

        switch (contact.getColor()) {
            case R.color.color_red:
                rbRed.setChecked(true);
                break;

            case R.color.color_green:
                rbGreen.setChecked(true);
                break;

            case R.color.color_blue:
                rbBlue.setChecked(true);
                break;
        }

        switch (contact.getImage()) {
            case R.drawable.ic_male:
                selectImage(ivMale);
                break;

            case R.drawable.ic_female:
                selectImage(ivFemale);
                break;

            case R.drawable.ic_bird:
                selectImage(ivBird);
                break;
        }
    }

    @ColorRes
    int selectedTextColor = R.color.color_black;

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_red:
                selectedTextColor = R.color.color_red;
                break;

            case R.id.rb_green:
                selectedTextColor = R.color.color_green;
                break;

            case R.id.rb_blue:
                selectedTextColor = R.color.color_blue;
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof ImageView) {
            switch (v.getId()) {
                case R.id.iv_male:
                    selectImage(ivMale);
                    break;

                case R.id.iv_female:
                    selectImage(ivFemale);
                    break;

                case R.id.iv_bird:
                    selectImage(ivBird);
                    break;
            }
            return;
        }

        switch (v.getId()) {
            case R.id.btn_ok:
                if (!checkInput(etPhoneNum)) return;

                Intent i = new Intent();
                if (editMode) {
                    i.putExtra("update_contact", composeContact());
                    i.putExtra("update_contact_pos", receivedIntent.getIntExtra("edit_contact_pos", -1));
                    setResult(RESULT_OK, i);
                } else {
                    i.putExtra("new_contact", composeContact());
                    setResult(RESULT_OK, i);
                }
                finish();
                break;

            case R.id.btn_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    private Contact composeContact() {
        int contactImg;
        if (selectedImg == null) contactImg = R.drawable.ic_contact_black;
        else contactImg = (int) selectedImg.getTag();

        return new Contact(
                etName.getText().toString(), etPhoneNum.getText().toString(),
                contactImg, selectedTextColor);
    }

    private void selectImage(ImageView image) {
        if (selectedImg == image) {
            selectedImg.setBackgroundColor(Color.TRANSPARENT);
            selectedImg = null;
        } else {
            image.setBackgroundColor(imgSelectColor);
            if (selectedImg != null) selectedImg.setBackgroundColor(Color.TRANSPARENT);
            selectedImg = image;
        }
    }

    private boolean checkInput(EditText field) {
        if (field.getText().toString().trim().length() < 1) {
            field.setError("Can't be empty");
            return false;
        }
        return true;
    }

}
