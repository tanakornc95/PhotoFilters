package primum.photofilters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptC;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public Bitmap defaultPic = null;
    public Bitmap filterPic = null;
    public SeekBar seekBar;
    public int bright;
    public double sepia;
    public int blur;

    public BitmapDrawable bd;
    public RelativeLayout ll;

    public String fName = null;
    String filePath = null;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        seekBar = (SeekBar) findViewById(R.id.seek);
        seekBar.setVisibility(View.GONE);

        final ImageButton camera_btn = (ImageButton) findViewById(R.id.camera);
        camera_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "Pic_" + timeStamp + ".jpg";
                fName = imageFileName;
                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageFileName);
                Uri fileUri = Uri.fromFile(f);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, 0);
                filePath = fileUri.toString();
            }

        });

        ImageButton btnSave = (ImageButton)findViewById(R.id.save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(filterPic == null){
                    Toast.makeText(MainActivity.this,"Get picture first",Toast.LENGTH_LONG).show();
                }
                else{
                    Bitmap bmpPic = filterPic;
                    FileOutputStream bmpFile = null;
                    try {
                        bmpFile = new FileOutputStream(filePath.replace("file://", ""));
                        bmpPic = Bitmap.createScaledBitmap(bmpPic, 800, 600, true);

                        bmpPic.compress(Bitmap.CompressFormat.JPEG, 100, bmpFile);
                        bmpFile.flush();
                        bmpFile.close();

                        Toast.makeText(MainActivity.this,"Save Complete",Toast.LENGTH_LONG).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        final ImageButton btnNormal = (ImageButton) findViewById(R.id.normal);
        final ImageButton btnGray = (ImageButton) findViewById(R.id.gray);
        final ImageButton btnBright = (ImageButton) findViewById(R.id.brightness);
        final ImageButton btnSepia = (ImageButton) findViewById(R.id.sepia);
        final ImageButton btnRound = (ImageButton) findViewById(R.id.round);
        final ImageButton btnSharp = (ImageButton) findViewById(R.id.sharp);
        final ImageButton btnEmboss = (ImageButton) findViewById(R.id.emboss);
        final ImageButton btnBlur = (ImageButton) findViewById(R.id.blur);

        btnNormal.setImageResource(R.drawable.normal_icon_pressed);

        btnNormal.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(defaultPic == null){
                    Toast.makeText(MainActivity.this,"Get picture first",Toast.LENGTH_LONG).show();
                }else{
                    btnNormal.setImageResource(R.drawable.normal_icon_pressed);
                    btnGray.setImageResource(R.drawable.grayscale_icon);
                    btnBright.setImageResource(R.drawable.brightness_icon);
                    btnSepia.setImageResource(R.drawable.sepia_icon);
                    btnRound.setImageResource(R.drawable.round_icon);
                    btnSharp.setImageResource(R.drawable.sharp_icon);
                    btnEmboss.setImageResource(R.drawable.emboss_icon);
                    btnBlur.setImageResource(R.drawable.blur_icon);

                    Toast.makeText(MainActivity.this,"Normal",Toast.LENGTH_LONG).show();

                    seekBar = (SeekBar) findViewById(R.id.seek);
                    seekBar.setProgress(0);
                    seekBar.setVisibility(View.GONE);

                    ll = (RelativeLayout) findViewById(R.id.activity_main);
                    bd = new BitmapDrawable(getResources(),defaultPic);
                    ll.setBackground(bd);
                }


            }
        });

        btnGray.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(defaultPic == null){
                    Toast.makeText(MainActivity.this,"Get picture first",Toast.LENGTH_LONG).show();
                }else{
                    btnNormal.setImageResource(R.drawable.normal_icon);
                    btnGray.setImageResource(R.drawable.grayscale_icon_pressed);
                    btnBright.setImageResource(R.drawable.brightness_icon);
                    btnSepia.setImageResource(R.drawable.sepia_icon);
                    btnRound.setImageResource(R.drawable.round_icon);
                    btnSharp.setImageResource(R.drawable.sharp_icon);
                    btnEmboss.setImageResource(R.drawable.emboss_icon);
                    btnBlur.setImageResource(R.drawable.blur_icon);

                    Toast.makeText(MainActivity.this,"Grayscale",Toast.LENGTH_LONG).show();

                    ll = (RelativeLayout) findViewById(R.id.activity_main);
                    bd = new BitmapDrawable(getResources(),defaultPic);
                    ll.setBackground(bd);

                    seekBar = (SeekBar) findViewById(R.id.seek);
                    seekBar.setProgress(0);
                    seekBar.setVisibility(View.GONE);

                    filterPic = doGreyscale(defaultPic);
                    ll = (RelativeLayout) findViewById(R.id.activity_main);
                    bd = new BitmapDrawable(getResources(),filterPic);
                    ll.setBackground(bd);


                }

            }
        });

        btnBright.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(defaultPic == null){
                    Toast.makeText(MainActivity.this,"Get picture first",Toast.LENGTH_LONG).show();
                }else{
                    btnNormal.setImageResource(R.drawable.normal_icon);
                    btnGray.setImageResource(R.drawable.grayscale_icon);
                    btnBright.setImageResource(R.drawable.brightness_icon_pressed);
                    btnSepia.setImageResource(R.drawable.sepia_icon);
                    btnRound.setImageResource(R.drawable.round_icon);
                    btnSharp.setImageResource(R.drawable.sharp_icon);
                    btnEmboss.setImageResource(R.drawable.emboss_icon);
                    btnBlur.setImageResource(R.drawable.blur_icon);

                    Toast.makeText(MainActivity.this,"Bright",Toast.LENGTH_LONG).show();

                    ll = (RelativeLayout) findViewById(R.id.activity_main);
                    bd = new BitmapDrawable(getResources(),defaultPic);
                    ll.setBackground(bd);

                    seekBar = (SeekBar) findViewById(R.id.seek);
                    seekBar.setProgress(0);
                    seekBar.setVisibility(View.VISIBLE);
                    seekBar.setMax(510);


                    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            bright = seekBar.getProgress() - 254;
                            filterPic = doBrightness(defaultPic,bright);
                            ll = (RelativeLayout) findViewById(R.id.activity_main);
                            bd = new BitmapDrawable(getResources(),filterPic);
                            ll.setBackground(bd);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    };

                    seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
                }


            }
        });

        btnSepia.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(defaultPic == null){
                    Toast.makeText(MainActivity.this,"Get picture first",Toast.LENGTH_LONG).show();
                }else{
                    btnNormal.setImageResource(R.drawable.normal_icon);
                    btnGray.setImageResource(R.drawable.grayscale_icon);
                    btnBright.setImageResource(R.drawable.brightness_icon);
                    btnSepia.setImageResource(R.drawable.sepia_icon_pressed);
                    btnRound.setImageResource(R.drawable.round_icon);
                    btnSharp.setImageResource(R.drawable.sharp_icon);
                    btnEmboss.setImageResource(R.drawable.emboss_icon);
                    btnBlur.setImageResource(R.drawable.blur_icon);

                    Toast.makeText(MainActivity.this,"SepiaTone",Toast.LENGTH_LONG).show();

                    ll = (RelativeLayout) findViewById(R.id.activity_main);
                    bd = new BitmapDrawable(getResources(),defaultPic);
                    ll.setBackground(bd);

                    seekBar = (SeekBar) findViewById(R.id.seek);
                    seekBar.setProgress(0);
                    seekBar.setVisibility(View.VISIBLE);
                    seekBar.setMax(1020);

                    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {


                            sepia = (double) seekBar.getProgress();
                            if(sepia <= 255){
                                filterPic = createSepiaToningEffect(defaultPic,1,sepia,0,0);

                                ll = (RelativeLayout) findViewById(R.id.activity_main);
                                bd = new BitmapDrawable(getResources(),filterPic);
                                ll.setBackground(bd);

                            }
                            else if(sepia > 255 && sepia <= 510){
                                filterPic = createSepiaToningEffect(defaultPic,1,sepia - (sepia - 255),sepia - 255,0);

                                ll = (RelativeLayout) findViewById(R.id.activity_main);
                                bd = new BitmapDrawable(getResources(),filterPic);
                                ll.setBackground(bd);

                            }
                            else if(sepia > 510 && sepia <= 765){
                                filterPic = createSepiaToningEffect(defaultPic,1,0,sepia - (sepia - 510),sepia - 510);

                                ll = (RelativeLayout) findViewById(R.id.activity_main);
                                bd = new BitmapDrawable(getResources(),filterPic);
                                ll.setBackground(bd);

                            }
                            else if(sepia > 765){
                                filterPic = createSepiaToningEffect(defaultPic,1,sepia - 765,0,sepia - (sepia - 765));

                                ll = (RelativeLayout) findViewById(R.id.activity_main);
                                bd = new BitmapDrawable(getResources(),filterPic);
                                ll.setBackground(bd);

                            }

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    };

                    seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
                }



            }
        });

        btnRound.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(defaultPic == null){
                    Toast.makeText(MainActivity.this,"Get picture first",Toast.LENGTH_LONG).show();
                }else{
                    btnNormal.setImageResource(R.drawable.normal_icon);
                    btnGray.setImageResource(R.drawable.grayscale_icon);
                    btnBright.setImageResource(R.drawable.brightness_icon);
                    btnSepia.setImageResource(R.drawable.sepia_icon);
                    btnRound.setImageResource(R.drawable.round_icon_pressed);
                    btnSharp.setImageResource(R.drawable.sharp_icon);
                    btnEmboss.setImageResource(R.drawable.emboss_icon);
                    btnBlur.setImageResource(R.drawable.blur_icon);

                    Toast.makeText(MainActivity.this,"Rounder",Toast.LENGTH_LONG).show();

                    ll = (RelativeLayout) findViewById(R.id.activity_main);
                    bd = new BitmapDrawable(getResources(),defaultPic);
                    ll.setBackground(bd);

                    seekBar = (SeekBar) findViewById(R.id.seek);
                    seekBar.setProgress(0);
                    seekBar.setVisibility(View.VISIBLE);
                    seekBar.setMax(90);

                    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            ll = (RelativeLayout) findViewById(R.id.activity_main);
                            bd = new BitmapDrawable(getResources(),defaultPic);
                            ll.setBackground(bd);

                            filterPic = roundCorner(defaultPic,seekBar.getProgress());

                            ll = (RelativeLayout) findViewById(R.id.activity_main);
                            bd = new BitmapDrawable(getResources(),filterPic);
                            ll.setBackground(bd);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    };

                    seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
                }

            }
        });

        btnSharp.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(defaultPic == null){
                    Toast.makeText(MainActivity.this,"Get picture first",Toast.LENGTH_LONG).show();
                }
                else{
                    btnNormal.setImageResource(R.drawable.normal_icon);
                    btnGray.setImageResource(R.drawable.grayscale_icon);
                    btnBright.setImageResource(R.drawable.brightness_icon);
                    btnSepia.setImageResource(R.drawable.sepia_icon);
                    btnRound.setImageResource(R.drawable.round_icon);
                    btnSharp.setImageResource(R.drawable.sharp_icon_pressed);
                    btnEmboss.setImageResource(R.drawable.emboss_icon);
                    btnBlur.setImageResource(R.drawable.blur_icon);

                    Toast.makeText(MainActivity.this,"Sharp",Toast.LENGTH_LONG).show();

                    ll = (RelativeLayout) findViewById(R.id.activity_main);
                    bd = new BitmapDrawable(getResources(),defaultPic);
                    ll.setBackground(bd);

                    seekBar = (SeekBar) findViewById(R.id.seek);
                    seekBar.setProgress(0);
                    seekBar.setVisibility(View.GONE);

                    filterPic = sharp(defaultPic);

                    ll = (RelativeLayout) findViewById(R.id.activity_main);
                    bd = new BitmapDrawable(getResources(),filterPic);
                    ll.setBackground(bd);
                }

            }
        });

        btnEmboss.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(defaultPic == null){
                    Toast.makeText(MainActivity.this,"Get picture first",Toast.LENGTH_LONG).show();
                }else{
                    btnNormal.setImageResource(R.drawable.normal_icon);
                    btnGray.setImageResource(R.drawable.grayscale_icon);
                    btnBright.setImageResource(R.drawable.brightness_icon);
                    btnSepia.setImageResource(R.drawable.sepia_icon);
                    btnRound.setImageResource(R.drawable.round_icon);
                    btnSharp.setImageResource(R.drawable.sharp_icon);
                    btnEmboss.setImageResource(R.drawable.emboss_icon_pressed);
                    btnBlur.setImageResource(R.drawable.blur_icon);

                    Toast.makeText(MainActivity.this,"Emboss",Toast.LENGTH_LONG).show();

                    ll = (RelativeLayout) findViewById(R.id.activity_main);
                    bd = new BitmapDrawable(getResources(),defaultPic);
                    ll.setBackground(bd);

                    seekBar = (SeekBar) findViewById(R.id.seek);
                    seekBar.setProgress(0);
                    seekBar.setVisibility(View.GONE);

                    filterPic = emboss(defaultPic);

                    ll = (RelativeLayout) findViewById(R.id.activity_main);
                    bd = new BitmapDrawable(getResources(),filterPic);
                    ll.setBackground(bd);
                }




            }
        });

        btnBlur.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(defaultPic == null){
                    Toast.makeText(MainActivity.this,"Get picture first",Toast.LENGTH_LONG).show();
                }else{
                    btnNormal.setImageResource(R.drawable.normal_icon);
                    btnGray.setImageResource(R.drawable.grayscale_icon);
                    btnBright.setImageResource(R.drawable.brightness_icon);
                    btnSepia.setImageResource(R.drawable.sepia_icon);
                    btnRound.setImageResource(R.drawable.round_icon);
                    btnSharp.setImageResource(R.drawable.sharp_icon);
                    btnEmboss.setImageResource(R.drawable.emboss_icon);
                    btnBlur.setImageResource(R.drawable.blur_icon_pressed);

                    Toast.makeText(MainActivity.this,"Blur",Toast.LENGTH_LONG).show();

                    ll = (RelativeLayout) findViewById(R.id.activity_main);
                    bd = new BitmapDrawable(getResources(),defaultPic);
                    ll.setBackground(bd);

                    seekBar = (SeekBar) findViewById(R.id.seek);
                    seekBar.setProgress(0);
                    seekBar.setVisibility(View.VISIBLE);
                    seekBar.setMax(24);

                    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            blur = seekBar.getProgress();
                            Bitmap outputBitmap = Bitmap.createBitmap(defaultPic);
                            RenderScript renderScript = RenderScript.create(MainActivity.this);
                            Allocation tmpIn = Allocation.createFromBitmap(renderScript, defaultPic);
                            Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

                            //Intrinsic Gausian blur filter
                            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
                            theIntrinsic.setRadius(blur+1);
                            theIntrinsic.setInput(tmpIn);
                            theIntrinsic.forEach(tmpOut);
                            tmpOut.copyTo(outputBitmap);

                            filterPic = outputBitmap;

                            ll = (RelativeLayout) findViewById(R.id.activity_main);
                            bd = new BitmapDrawable(getResources(),filterPic);
                            ll.setBackground(bd);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    };

                    seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
                }


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap bmpPic = BitmapFactory.decodeFile(filePath.replace("file://", ""));
                FileOutputStream bmpFile = new FileOutputStream(filePath.replace("file://", ""));
                bmpPic = Bitmap.createScaledBitmap(bmpPic, 800, 600, true);

                bmpPic.compress(Bitmap.CompressFormat.JPEG, 100, bmpFile);
                bmpFile.flush();
                bmpFile.close();

                defaultPic = bmpPic;
                filterPic = defaultPic;

                ll = (RelativeLayout) findViewById(R.id.activity_main);
                bd = new BitmapDrawable(getResources(),defaultPic);
                ll.setBackgroundDrawable(bd);

            } catch (Exception e) {
                Log.e("Log", "Error on saving file");
            }
        }
    }


    public static Bitmap doGreyscale(Bitmap src) {
        // constant factors
        final double GS_RED = 0.299;
        final double GS_GREEN = 0.587;
        final double GS_BLUE = 0.114;

        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        // pixel information
        int A, R, G, B;
        int pixel;

        // get image size
        int width = src.getWidth();
        int height = src.getHeight();

        // scan through every single pixel
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get one pixel color
                pixel = src.getPixel(x, y);
                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                R = G = B = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }

    public static Bitmap createSepiaToningEffect(Bitmap src, int depth, double red, double green, double blue) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // constant grayscale
        final double GS_RED = 0.3;
        final double GS_GREEN = 0.59;
        final double GS_BLUE = 0.11;
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                // get color on each channel
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // apply grayscale sample
                B = G = R = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);

                // apply intensity level for sepid-toning on each channel
                R += (depth * red);
                if(R > 255) { R = 255; }

                G += (depth * green);
                if(G > 255) { G = 255; }

                B += (depth * blue);
                if(B > 255) { B = 255; }

                // set new pixel color to output image
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }

    public static Bitmap doBrightness(Bitmap src, int value) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                // increase/decrease each channel
                R += value;
                if(R > 255) { R = 255; }
                else if(R < 0) { R = 0; }

                G += value;
                if(G > 255) { G = 255; }
                else if(G < 0) { G = 0; }

                B += value;
                if(B > 255) { B = 255; }
                else if(B < 0) { B = 0; }

                // apply new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }

    public static Bitmap roundCorner(Bitmap src, float round) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // set canvas for painting
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        // config paint
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        // config rectangle for embedding
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        // draw rect to canvas
        canvas.drawRoundRect(rectF, round, round, paint);

        // create Xfer mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // draw source image to canvas
        canvas.drawBitmap(src, rect, rect, paint);

        // return final image
        return result;
    }

    public static Bitmap sharp(Bitmap src) {
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        double[][] SharpConfig = new double[][] {
                { 0 , -1, -0 },
                { -1 , 5, -1 },
                { 0 , -1, 0 }
        };
        convMatrix.applyConfig(SharpConfig);
        convMatrix.Factor = 1;
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
    }

    public static Bitmap emboss(Bitmap src) {
        double[][] EmbossConfig = new double[][] {
                { -1 ,  0, -1 },
                {  0 ,  4,  0 },
                { -1 ,  0, -1 }
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.applyConfig(EmbossConfig);
        convMatrix.Factor = 1;
        convMatrix.Offset = 127;
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
    }

}

