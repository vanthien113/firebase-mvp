package com.example.thienpro.mvp_firebase.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.thienpro.mvp_firebase.R;
import com.example.thienpro.mvp_firebase.databinding.ActivityRegisterBinding;
import com.example.thienpro.mvp_firebase.presenter.Impl.RegisterPresenterImpl;
import com.example.thienpro.mvp_firebase.presenter.RegistrerPresenter;
import com.example.thienpro.mvp_firebase.view.RegisterView;

import java.io.IOException;

/**
 * Created by ThienPro on 11/9/2017.
 */

public class RegisterActivity extends AppCompatActivity implements RegisterView {
    private ActivityRegisterBinding binding;
    private RegistrerPresenter presenter;
    private static final int REQUEST_CODE_IMAGE = 2;
    private Uri filePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        binding.setEvent(this);
        presenter = new RegisterPresenterImpl(this);

        binding.sp.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.province_arrays)));
    }

    @Override
    public void navigationToVerifiEmail() {
        Intent intent = new Intent(this, VerifiEmailActivity.class);
        this.startActivity(intent);
    }

    @Override
    public void onRegisterClick() {
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();
        String repassword = binding.etRepassword.getText().toString();
        String name = binding.etName.getText().toString();

        if (email.isEmpty() || password.isEmpty() || repassword.isEmpty() || name.isEmpty())
            Toast.makeText(this, "Không được để trống các trường!", Toast.LENGTH_SHORT).show();
        else {
            if (name.length() >= 30)
                Toast.makeText(this, "Tên có độ dài dưới 30 ký tự!", Toast.LENGTH_SHORT).show();
            else {
                if (password.length() >= 6) {
                    if (password.equals(repassword))
                        presenter.register(email, password, name, binding.sp.getSelectedItem().toString(), binding.rbNam.isChecked(), filePath);
                    else
                        Toast.makeText(this, "Mật khẩu không trùng khớp!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "Mật khẩu phải lớn hơn 6 ký tự!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onRegisterEmailFail() {
        Toast.makeText(this, "Địa chỉ email không dúng!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackClick() {
        navigationToLogin();
    }

    @Override
    public void onAvatarClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_IMAGE);
    }

    void navigationToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                binding.ivAvatar.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
