package cz.mff.mobapp;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

public class QrCodeGenerator {
    public static BitMatrix generateMatrix(String qrCodeText, int size) throws WriterException {
        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable hintMap = new Hashtable();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        return qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
    }

    public static Bitmap drawMatrix(BitMatrix byteMatrix, int backgroundColor, int foregroundColor) {
        // Make the BufferedImage that are to hold the QRCode
        int matrixWidth = byteMatrix.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(matrixWidth, matrixWidth, Bitmap.Config.RGB_565);

        for (int x = 0; x < matrixWidth; ++x) {
            for (int y = 0; y < matrixWidth; ++y) {
                int color = byteMatrix.get(x, y) ? foregroundColor : backgroundColor;
                bitmap.setPixel(x, y, color);
            }
        }

        return bitmap;
    }
}
