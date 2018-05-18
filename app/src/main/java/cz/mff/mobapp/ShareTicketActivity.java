package cz.mff.mobapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class ShareTicketActivity extends Activity {

    public static final String TICKET_ID = "cz.mff.mobapp.ShareTicketActivity.TICKET_ID";
    private static final String QR_TEXT_FORMAT = "http://mobapp-server.herokuapp.com/subscribe/%s/";
    private static final int QR_SIZE = 1024;

    private String ticketId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_ticket);

        Intent intent = getIntent();
        ticketId = intent.getStringExtra(TICKET_ID);

        try {
            generateQrCode();
        } catch (WriterException e) {
            e.printStackTrace();
        }

        NdefUtils.shareUrl(this, Uri.encode(String.format(QR_TEXT_FORMAT, ticketId)));
    }

    private void generateQrCode() throws WriterException {
        BitMatrix matrix = QrCodeGenerator.generateMatrix(String.format(QR_TEXT_FORMAT, ticketId), QR_SIZE);
        Bitmap bitmap = QrCodeGenerator.drawMatrix(matrix, Color.WHITE, Color.BLACK);

        ImageView imageView = findViewById(R.id.qrCodeView);
        imageView.setImageBitmap(bitmap);
    }
}
