package com.db.lnghitemstickers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button addItem, print, okAdd;
    private LinearLayout editLayout, listLayout;
    private Spinner itemName, itemBrand, itemCategory;
    private List<String> itemNameType = new ArrayList<>();
    private List<String> itemBrandType = new ArrayList<>();
    private List<String> itemCategoryType = new ArrayList<>();
    private int totalStickers = 0;
    private String stickerArray[][] = new String[1][6];

    private boolean pressedOkFlag = true;
    public static boolean returnToMainActivityFlag = false;

    Bundle saved = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stickerArray[0][0] = "null";
        stickerArray[0][1] = "";
        stickerArray[0][2] = "";
        stickerArray[0][3] = "";
        stickerArray[0][4] = "";
        stickerArray[0][5] = "";

        itemNameType.add("RD");
        itemNameType.add("MS");
        itemNameType.add("PS");
        itemNameType.add("OV");
        itemNameType.add("OC");

        itemBrandType.add("L  G  H");
        itemBrandType.add("R  M");
        itemBrandType.add("S  D  I");

        itemCategoryType.add("A");
        itemCategoryType.add("AA");
        itemCategoryType.add("AAA");
        itemCategoryType.add("NORMAL");

        editLayout = findViewById(R.id.editListContainer_mainActivity);
        listLayout = findViewById(R.id.editListContainer_mainActivity);

        print = findViewById(R.id.printButton_mainActivity);
        addItem = findViewById(R.id.addItem_mainActivity);

        TextView stickersLeft = findViewById(R.id.totalStickersLeft);
        stickersLeft.setText((44-totalStickers)+"");

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pressedOkFlag) {
                    pressedOkFlag = false;
                    View viewEdit = getLayoutInflater().inflate(R.layout.item_edit, null);


                    okAdd = viewEdit.findViewById(R.id.okButton_itemEdit);
                    EditText size = viewEdit.findViewById(R.id.size_itemEdit);
                    EditText quantity = viewEdit.findViewById(R.id.quantity_itemEdit);
                    EditText stickerQuantity = viewEdit.findViewById(R.id.stickerQuantity_itemEdit);

                    itemName = viewEdit.findViewById(R.id.itemName_itemEdit);
                    ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_spinner_item,
                            itemNameType);

                    itemBrand = viewEdit.findViewById(R.id.itemBrand_itemEdit);
                    ArrayAdapter adapter2 = new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_spinner_item,
                            itemBrandType);

                    itemCategory = viewEdit.findViewById(R.id.itemCategory_itemEdit);
                    ArrayAdapter adapter3 = new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_spinner_item,
                            itemCategoryType);


                    itemName.setAdapter(adapter);
                    itemBrand.setAdapter(adapter2);
                    itemCategory.setAdapter(adapter3);

                    editLayout.addView(viewEdit);

                    okAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(size.getText().toString().equals("") || quantity.getText().toString().equals("")
                                || stickerQuantity.getText().toString().equals("")){
                                Toast.makeText(MainActivity.this, "Enter value first", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                pressedOkFlag = true;

                                totalStickers += Integer.parseInt(stickerQuantity.getText().toString());
                                stickersLeft.setText((44-totalStickers)+"");

                                View viewList = getLayoutInflater().inflate(R.layout.item_list, null);

                                TextView tvBrand = viewList.findViewById(R.id.itemBrand_itemList);
                                TextView tvCategory = viewList.findViewById(R.id.itemCategory_itemList);
                                TextView tvName = viewList.findViewById(R.id.itemName_itemList);
                                TextView tvSize = viewList.findViewById(R.id.size_itemList);
                                TextView tvQuantity = viewList.findViewById(R.id.quantity_itemList);
                                TextView tvStickerQuantity = viewList.findViewById(R.id.stickerQuantity_itemList);
                                Button deleteListItem = viewList.findViewById(R.id.deleteButton_itemList);

                                tvBrand.setText(itemBrand.getSelectedItem().toString());
                                tvCategory.setText(itemCategory.getSelectedItem().toString());
                                tvName.setText(itemName.getSelectedItem().toString());
                                tvSize.setText(size.getText().toString());
                                tvQuantity.setText(quantity.getText().toString());
                                tvStickerQuantity.setText(stickerQuantity.getText().toString());

                                addOrRemoveItem(tvBrand.getText().toString(),
                                        tvCategory.getText().toString(), tvName.getText().toString(), tvSize.getText().toString(),
                                        tvQuantity.getText().toString(), tvStickerQuantity.getText().toString(), true);

                                listLayout.addView(viewList);
                                editLayout.removeView(viewEdit);

                                deleteListItem.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        totalStickers -= Integer.parseInt(tvStickerQuantity.getText().toString());
                                        stickersLeft.setText((44-totalStickers)+"");
                                        addOrRemoveItem(tvBrand.getText().toString(),
                                                tvCategory.getText().toString(), tvName.getText().toString(), tvSize.getText().toString(),
                                                tvQuantity.getText().toString(), tvStickerQuantity.getText().toString(), false);
                                        listLayout.removeView(viewList);
                                    }
                                });
                            }
                        }
                    });

                }
                else{
                    Toast.makeText(MainActivity.this, "Add item details first", Toast.LENGTH_SHORT).show();
                }

            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(totalStickers == 44)
                    printPDF();
                else
                    Toast.makeText(MainActivity.this, "Sticker quantity should be 44", Toast.LENGTH_SHORT).show();
            }
        });

    }

    protected void displayArray(){
        for(int i=0;i<stickerArray.length;i++)
            Log.d("stickerArray", stickerArray[i][0] + " " + stickerArray[i][1] + " " +
                    stickerArray[i][2] + " " + stickerArray[i][3] + " " +
                    stickerArray[i][4]+ " " + stickerArray[i][5]);
    }

    protected void addOrRemoveItem(String itemBrand, String itemCategory,
                                   String itemName, String size, String quantity,
                                   String stickerQuantity, boolean addOrRemoveFlag){

        Log.d("stickerArray", "INITIAL ARRAY:");
        displayArray();

        if(addOrRemoveFlag){
            if(stickerArray[0][0].equals("null")){
                stickerArray[0][0] = itemBrand;
                stickerArray[0][1] = itemCategory;
                stickerArray[0][2] = itemName;
                stickerArray[0][3] = size;
                stickerArray[0][4] = quantity;
                stickerArray[0][5] = stickerQuantity;
            }
            else{
                String temp[][] = new String[stickerArray.length][6];
                for(int i=0;i<stickerArray.length;i++){
                    temp[i][0] = stickerArray[i][0];
                    temp[i][1] = stickerArray[i][1];
                    temp[i][2] = stickerArray[i][2];
                    temp[i][3] = stickerArray[i][3];
                    temp[i][4] = stickerArray[i][4];
                    temp[i][5] = stickerArray[i][5];
                }
                stickerArray = new String[temp.length+1][6];
                for(int i=0;i<temp.length;i++){
                    stickerArray[i][0] = temp[i][0];
                    stickerArray[i][1] = temp[i][1];
                    stickerArray[i][2] = temp[i][2];
                    stickerArray[i][3] = temp[i][3];
                    stickerArray[i][4] = temp[i][4];
                    stickerArray[i][5] = temp[i][5];
                }
                stickerArray[stickerArray.length-1][0] = itemBrand;
                stickerArray[stickerArray.length-1][1] = itemCategory;
                stickerArray[stickerArray.length-1][2] = itemName;
                stickerArray[stickerArray.length-1][3] = size;
                stickerArray[stickerArray.length-1][4] = quantity;
                stickerArray[stickerArray.length-1][5] = stickerQuantity;
            }
        }
        else{

            int index = 0;
            if(stickerArray.length > 1) {
                String temp[][] = new String[stickerArray.length - 1][6];
                int c = 0;

                for (int i = 0; i < stickerArray.length; i++) {
                    if (stickerArray[i][0].equals(itemBrand) && stickerArray[i][1].equals(itemCategory) &&
                             stickerArray[i][2].equals(itemName) && stickerArray[i][3].equals(size) &&
                            stickerArray[i][4].equals(quantity) && stickerArray[i][5].equals(stickerQuantity) && c == 0) {
                        Log.d("stickerArray", "EQUAL");
                        c++;
                    }
                    else {
                        temp[index][0] = stickerArray[i][0];
                        temp[index][1] = stickerArray[i][1];
                        temp[index][2] = stickerArray[i][2];
                        temp[index][3] = stickerArray[i][3];
                        temp[index][4] = stickerArray[i][4];
                        temp[index][5] = stickerArray[i][5];
                        index++;
                    }
                }
                stickerArray = new String[temp.length][6];
                for (int j = 0; j < temp.length; j++) {
                    stickerArray[j][0] = temp[j][0];
                    stickerArray[j][1] = temp[j][1];
                    stickerArray[j][2] = temp[j][2];
                    stickerArray[j][3] = temp[j][3];
                    stickerArray[j][4] = temp[j][4];
                    stickerArray[j][5] = temp[j][5];
                }
            }
            else
                stickerArray[0][0] = "null";

        }
        Log.d("stickerArray", "UPDATED ARRAY:");
        displayArray();

    }

    protected void printPDF(){

        PdfDocument myPdf = new PdfDocument();
        PdfDocument.PageInfo myPdfPageInfo = new PdfDocument.PageInfo.Builder(2480, 3508, 1).create();
        PdfDocument.Page myPdfPage1 = myPdf.startPage(myPdfPageInfo);
        Canvas canvas = myPdfPage1.getCanvas();
        Paint myPaint = new Paint();

        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(10f);

        for(int i = 1; i <= 11; i++)
            canvas.drawLine(0, 312*i, 2480, 312*i, myPaint);

        for(int i=1;i<=4;i++)
            canvas.drawLine(600*i, 0, 600*i, 3508, myPaint);

        myPaint.setStrokeWidth(7f);
        for(int i = 1; i <= 11; i++)
            canvas.drawLine(0, (312 * (i-1)) + 85, 2480, (312 * (i-1)) + 85, myPaint);


        myPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        int arrIndex = 0;
        int indexX = 1;
        int indexY = 1;
        for(int i=1;i<=44;i++) {

            if(indexX == 5) {
                indexX = 1;
                indexY++;
            }

            if(stickerArray[arrIndex][5].equals("0"))
                arrIndex++;


            myPaint.setStrokeWidth(4f);
            myPaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
            myPaint.setTextSize(50f);
            myPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(stickerArray[arrIndex][0], (600*indexX)-300, (312*indexY) - 242, myPaint);

            myPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            myPaint.setStrokeWidth(2f);
            myPaint.setTextSize(40f);
            canvas.drawText("CATEGORY:", (600*indexX) - 450, (312*indexY)-172, myPaint);
            canvas.drawText(stickerArray[arrIndex][1], (600*indexX) - 150, (312*indexY)-172, myPaint);
            canvas.drawText(stickerArray[arrIndex][2] + ":", (600*indexX) - 450, (312*indexY)-102, myPaint);
            canvas.drawText(stickerArray[arrIndex][3], (600*indexX) - 150, (312*indexY)-102, myPaint);
            canvas.drawText("PCS:", (600*indexX) - 450, (312*indexY)-32, myPaint);
            canvas.drawText(stickerArray[arrIndex][4], (600*indexX) - 150, (312*indexY)-32, myPaint);

            stickerArray[arrIndex][5] = (Integer.parseInt(stickerArray[arrIndex][5]) - 1)+"";
            Log.d("stickerQuantity", stickerArray[arrIndex][0] + " " + stickerArray[arrIndex][1] + " " +
                    stickerArray[arrIndex][2] + " " + stickerArray[arrIndex][3] + " " + stickerArray[arrIndex][4]+ " " + stickerArray[arrIndex][5]);

            indexX++;
        }



        myPdf.finishPage(myPdfPage1);

        File file = new File(Environment.getExternalStorageDirectory(), "/Download/Sticker.pdf");

        try{
            myPdf.writeTo(new FileOutputStream(file));
        }
        catch(Exception e){
            Log.e("error","Error");
        }

        myPdf.close();
        Toast.makeText(this, "PDF MADE", Toast.LENGTH_SHORT).show();

        restartActivity();
    }

    protected void restartActivity(){
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }
}