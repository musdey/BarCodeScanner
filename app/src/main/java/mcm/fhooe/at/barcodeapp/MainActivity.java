package mcm.fhooe.at.barcodeapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity implements View.OnClickListener{

    private Button scan,generate,modeButton;
    private TextView tv, modeView;
    private EditText editText;
    private ImageView imageView;
    public  com.google.zxing.BarcodeFormat barcodeMode = BarcodeFormat.CODE_128;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView);
        modeView = (TextView)findViewById(R.id.modeView);
        scan= (Button)findViewById(R.id.buttonScan);
        generate = (Button) findViewById(R.id.buttonGenerate);
        modeButton =(Button)findViewById(R.id.changeButton);
        imageView = (ImageView)findViewById(R.id.imageView);
        editText = (EditText) findViewById(R.id.editText);
        scan.setOnClickListener(this);
        generate.setOnClickListener(this);
        modeButton.setOnClickListener(this);
        modeView.setText(barcodeMode.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                //Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                tv.setText("Cancelled");
            } else {
                tv.setText("Scanned: "+result.getContents());
              //  Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void startScan(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setOrientationLocked(true);
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }


    private void initializeAlertDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Set mode");
        alertDialog.setItems(new CharSequence[]
                        {"Code_39", "Code_128", "Code_93", "UPC - A","EAN_8","EAN_13","CODABAR"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                barcodeMode = BarcodeFormat.CODE_39;
                                break;
                            case 1:
                                barcodeMode = BarcodeFormat.CODE_128;
                                break;
                            case 2:
                                barcodeMode = BarcodeFormat.CODE_93;
                                break;
                            case 3:
                                barcodeMode = BarcodeFormat.UPC_A;
                                break;
                            case 4:
                                barcodeMode = BarcodeFormat.EAN_8;
                                break;
                            case 5:
                                barcodeMode = BarcodeFormat.EAN_13;
                                break;
                            case 6:
                                barcodeMode = BarcodeFormat.CODABAR;
                                break;
                        }
                        modeView.setText(barcodeMode.toString());
                        dialog.dismiss();
                    }
                });
        alertDialog.create().show();
    }

    private void generateBarcode(){

        String text = editText.getText().toString();
        if(text.length()<=0){
            Toast.makeText(this,"The textfield is empty!",Toast.LENGTH_SHORT);
        }else{
            com.google.zxing. MultiFormatWriter writer = new MultiFormatWriter();

            String finaldata = Uri.encode(text, "utf-8");
            BitMatrix bm = null;
            try {
                bm = writer.encode(finaldata, barcodeMode,150, 150);
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e){
                Toast.makeText(this,"invalid input",Toast.LENGTH_SHORT).show();
                return;
            }
            Bitmap ImageBitmap = Bitmap.createBitmap(180, 40, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < 180; i++) {//width
                for (int j = 0; j < 40; j++) {//height
                    ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK: Color.WHITE);
                }
            }

            if (ImageBitmap != null) {
                imageView.setImageBitmap(ImageBitmap);
            } else {
                Toast.makeText(getApplicationContext(), "Error",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.buttonScan){
            startScan();
        }else if(view.getId()==R.id.buttonGenerate){
            generateBarcode();
        }else {
            initializeAlertDialog();
        }
    }
}