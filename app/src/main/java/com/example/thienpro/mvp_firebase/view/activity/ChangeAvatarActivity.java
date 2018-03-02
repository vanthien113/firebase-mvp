package com.example.thienpro.mvp_firebase.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.thienpro.mvp_firebase.R;
import com.example.thienpro.mvp_firebase.databinding.ActivityChangeAvatarBinding;
import com.example.thienpro.mvp_firebase.presenter.ChangeAvatarPresenter;
import com.example.thienpro.mvp_firebase.presenter.Impl.ChangeAvatarPresenterImpl;
import com.example.thienpro.mvp_firebase.ultils.LoadingDialog;
import com.example.thienpro.mvp_firebase.ultils.LogUltil;
import com.example.thienpro.mvp_firebase.view.ChangeAvatarView;
import com.google.firebase.database.DatabaseError;

import java.io.IOException;

public class ChangeAvatarActivity extends AppCompatActivity implements ChangeAvatarView {
    private static final int REQUEST_CODE_IMAGE = 4;
    private Uri filePath;
    private ActivityChangeAvatarBinding binding;
    private ChangeAvatarPresenter presenter;
    private LoadingDialog loadingDialog;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ChangeAvatarActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_avatar);
        presenter = new ChangeAvatarPresenterImpl(this, this);
        loadingDialog = new LoadingDialog(this);

        binding.setEvent(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                LogUltil.log(getClass(), filePath);

                binding.ivAvatar.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onChangeAvatarClick() {
        if (filePath == null) {
            Toast.makeText(this, R.string.hay_chon_anh_dai_dien, Toast.LENGTH_SHORT).show();
        } else {
            presenter.changeAvatar(filePath);
        }
    }

    @Override
    public void onAvatar() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_IMAGE);
    }

    @Override
    public void changeAvatarError(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void changeAvatarError(DatabaseError e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigationToHome() {
        HomeActivity.startActiviry(this);
    }

    @Override
    public void showLoading() {
        loadingDialog.show();
    }

    @Override
    public void hideLoading() {
        loadingDialog.dismiss();
    }
}
