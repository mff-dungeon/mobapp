package cz.mff.mobapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class ShareTicketActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_ticket);

        BitMatrix matrix = null;
        try {
            matrix = QrCodeGenerator.generateMatrix("http://mobapp-server.herokuapp.com/subscribe/33319b2f-f891-40b0-a23f-bcdaa9b71857/", 1024);
        } catch (WriterException e) {
            e.printStackTrace();
            return;
        }
        Bitmap bitmap = QrCodeGenerator.drawMatrix(matrix, Color.WHITE, Color.BLACK);

        ImageView imageView = findViewById(R.id.qrCodeView);
        imageView.setImageBitmap(bitmap);
    }
}
