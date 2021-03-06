package com.example.myapplication;

import androidx.activity.result.ActivityResult;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.example.myapplication.Services.UserService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Map;

import androidmads.library.qrgenearator.QRGEncoder;

public class UserDashboardActivity extends AppCompatActivity {

    private EditText dataEdt;
    private Button generateQrBtn;
    private String username;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    int currentBarProgress = 0;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        username = getIntent().getStringExtra("username");
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView progressbarInfo = findViewById(R.id.textViewProgressBarInfo);

        progressbarInfo.setText(currentBarProgress + "/" + progressBar.getMax());

        UserService userService = new UserService(UserDashboardActivity.this);
        userService.getOneUserScore(new UserService.VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast toast = Toast.makeText(getBaseContext(), "Failed to load user score", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onResponse(JSONObject user_object) {

            }

            @Override
            public void onResponse(JSONArray user_list) {

            }

            @Override
            public void onResponse(int points) {
                progressBar.setProgress(points);
                progressbarInfo.setText(points + "/" + progressBar.getMax());

            }
        }, username);
    }

    public void onSetGoalsClick(View view) {
        Intent i = new Intent(this, GoalsActivity.class);
        i.putExtra("username", username);
        startActivityForResult(i, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("currentGoal");
                String goalPoints = data.getStringExtra("goalPoints");
                TextView goalInfo = findViewById(R.id.textViewCurrentGoal);
                TextView progressBarInfo = findViewById(R.id.textViewProgressBarInfo);
                ProgressBar progressBar = findViewById(R.id.progressBar);
                int currentProgress = progressBar.getProgress();
                goalInfo.setText(strEditText);
                progressBarInfo.setText(currentProgress + "/" + goalPoints);
            }
        }
    }

    public void onQRcodeOpen(View view) throws WriterException, NoSuchAlgorithmException {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // initializing all variables.
        ImageView qrCodeView = popupView.findViewById(R.id.qrCodeView);

        // below line is for getting
        // the windowmanager service.
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        width = point.x;
        height = point.y;

        // generating dimension from width and height.
        int dimen = Math.min(width, height);
        dimen = dimen * 3 / 4;

        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // H = 30% damage

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

//        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
//        messageDigest.update(username.getBytes());
//        String inputValue = new String(messageDigest.digest());

        String inputValue = username;


        int size = 256;
        BitMatrix bitMatrix = qrCodeWriter.encode(inputValue, BarcodeFormat.QR_CODE, size, size);
        width = bitMatrix.getWidth();
        height = bitMatrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? BLACK : WHITE;
            }
        }

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        qrCodeView.setImageBitmap(bitmap);

        // show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.setElevation(20);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void onHomeButtonClick(View view) {
        Intent intent = new Intent(UserDashboardActivity.this, UserDashboardActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void onCouponButtonClick(View view) {
        Intent intent = new Intent(UserDashboardActivity.this, PrizeActivity.class);
        //TextView userName = findViewById(R.id.welcomeText);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void onMapButtonClick(View view) {
        Intent intent = new Intent(UserDashboardActivity.this, MapActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void onRatingButtonClick(View view) {
        Intent intent = new Intent(UserDashboardActivity.this, ScoreboardActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}