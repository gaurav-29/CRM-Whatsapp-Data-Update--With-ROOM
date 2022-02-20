package com.oceanmtech.crmwhatsappdataupdate;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.room.Room;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.oceanmtech.crmwhatsappdataupdate.Database.DataTable;
import com.oceanmtech.crmwhatsappdataupdate.Database.MyDatabase;
import com.oceanmtech.crmwhatsappdataupdate.databinding.ActivityMainBinding;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.ContentUriUtils;

public class MainActivity extends AppCompatActivity {

    MainActivity mContext = MainActivity.this;
    ActivityMainBinding mBinding;
    String mobile, name, imgpath, path, message;
    List<DataTable> crmList = new ArrayList<>();
    Bitmap selectedImage;
    OutputStream outputStream;
    File filePath;
    MyDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(mContext, R.layout.activity_main);

        myDatabase = Room.databaseBuilder(mContext, MyDatabase.class, "crmdata.db").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        showDataInRecyclerView();
        onClickListeners();
    }

    private void onClickListeners() {
        mBinding.toolbar.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBinding.ivAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile = mBinding.etMobile.getText().toString();
                name = mBinding.etName.getText().toString();
                if (!mobile.equalsIgnoreCase("") && mobile.length() == 10 && !name.equalsIgnoreCase("")) {
                    Dexter.withContext(mContext)
                            .withPermissions(CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                        if (SDK_INT >= android.os.Build.VERSION_CODES.R) {
                                            if (!Environment.isExternalStorageManager()) {
                                                Intent intent = null;
                                                intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                                intent.addCategory("android.intent.category.DEFAULT");
                                                intent.setData(Uri.parse(String.format("package:%s", getPackageName())));
                                                startActivity(intent);
                                            } else {
                                                imagePicker();
                                            }
                                        } else {
                                            imagePicker();
                                        }
                                    }
                                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                        showRationaleDialog();
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                                }
                            }).check();
                } else {
                    name = "";
                    mobile = "";
                    imgpath = "";
                    message = "Please enter valid mobile number & name, then attach image.";

                    DataTable data = new DataTable(name, mobile, imgpath, message);
                    myDatabase.dao().insert_data(data);

                    if (crmList.size() > 0) {
                        showLastDataInRecyclerView();
                    } else {
                        crmList = new ArrayList<>();
                        showDataInRecyclerView();
                    }
                }
            }
        });

        mBinding.ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile = mBinding.etMobile.getText().toString();
                name = mBinding.etName.getText().toString();

                if (mobile.equalsIgnoreCase("") || mobile.length() != 10) {
                    name = "";
                    mobile = "";
                    imgpath = "";
                    message = "Please enter valid mobile number.";

                    DataTable data = new DataTable(name, mobile, imgpath, message);
                    myDatabase.dao().insert_data(data);

                    if (crmList.size() > 0) {
                        showLastDataInRecyclerView();
                    } else {
                        crmList = new ArrayList<>();
                        showDataInRecyclerView();
                    }

                } else if (myDatabase.dao().checkNumber(mobile).size() > 0) {
                    name = "";
                    mobile = "";
                    imgpath = "";
                    message = "Number already exists, so it is not added.";

                    DataTable data = new DataTable(name, mobile, imgpath, message);
                    myDatabase.dao().insert_data(data);

                    if (crmList.size() > 0) {
                        showLastDataInRecyclerView();
                    } else {
                        crmList = new ArrayList<>();
                        showDataInRecyclerView();
                    }

                } else if (name.equalsIgnoreCase("")) {
                    name = "";
                    mobile = "";
                    imgpath = "";
                    message = "Number added successfully. Please enter the name.";

                    DataTable data = new DataTable(name, mobile, imgpath, message);
                    myDatabase.dao().insert_data(data);

                    if (crmList.size() > 0) {
                        showLastDataInRecyclerView();
                    } else {
                        crmList = new ArrayList<>();
                        showDataInRecyclerView();
                    }
                } else {
                    if (photoPaths.size() != 0) {
                        saveImageToFolder();
                        message = "Folder created for this number. \nImage successfully saved in this folder. \nData successfully inserted in sheet.";

                        DataTable data = new DataTable(name, mobile, imgpath, message);
                        myDatabase.dao().insert_data(data);

                        if (crmList != null) {
                            //createOrEditExcelSheet();
                            if (crmList.size() > 0) {
                                showLastDataInRecyclerView();
                                createOrEditExcelSheet();
                            } else {
                                crmList = new ArrayList<>();
                                showDataInRecyclerView();
                            }
                        } else {
                            name = "";
                            mobile = "";
                            imgpath = "";
                            message = "Data not inserted";

                            DataTable data2 = new DataTable(name, mobile, imgpath, message);
                            myDatabase.dao().insert_data(data2);

                            if (crmList.size() > 0) {
                                showLastDataInRecyclerView();
                            } else {
                                crmList = new ArrayList<>();
                                showDataInRecyclerView();
                            }
                        }
                        mBinding.etMobile.setText("");
                        mBinding.etName.setText("");
                        photoPaths.clear();
                    } else {
                        name = "";
                        mobile = "";
                        imgpath = "";
                        message = "Data added successfully. Please attach image.";

                        DataTable data = new DataTable(name, mobile, imgpath, message);
                        myDatabase.dao().insert_data(data);
                        if (crmList.size() > 0) {
                            showLastDataInRecyclerView();
                        } else {
                            crmList = new ArrayList<>();
                            showDataInRecyclerView();
                        }
                    }
                }
            }
        });
    }

    private void showLastDataInRecyclerView() {

        DataTable model = new DataTable(name, mobile, imgpath, message);
        crmList.add(crmList.size(), model);
        adapter.notifyItemInserted(crmList.size());
        adapter.notifyItemRangeChanged(crmList.size() - 1, crmList.size());
        mBinding.recCrmdata.scrollToPosition(crmList.size() - 1);
    }

    private void imagePicker() {
        FilePickerBuilder.getInstance()
                .setMaxCount(5) //optional
                .setActivityTheme(R.style.LibAppTheme) //optional
                .pickPhoto(this, 1);
    }

    ArrayList<Uri> photoPaths = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            photoPaths = new ArrayList<>();
            photoPaths.addAll(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
            if (photoPaths.size() > 0) {
                Uri uri = photoPaths.get(0);
                Log.d("PHOTOPATHS", photoPaths.toString());
                path = ContentUriUtils.INSTANCE.getFilePath(mContext, uri);
                Log.d("SHIV", path);
                //Glide.with(mContext).load(path).placeholder(R.drawable.bg_row).centerCrop().into(mBinding.ivImage);
                name = "";
                mobile = "";
                imgpath = "";
                message = "Image attached";

                DataTable data2 = new DataTable(name, mobile, imgpath, message);
                myDatabase.dao().insert_data(data2);

                if (crmList.size() > 0) {
                    showLastDataInRecyclerView();
                } else {
                    crmList = new ArrayList<>();
                    showDataInRecyclerView();
                }
            }
        }
    }

    private void showRationaleDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Grant Permission");
        builder.setMessage("Permission is required to access images, files, audios & videos from this device");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void saveImageToFolder() {
        // Below code will create a folder.
        filePath = new File(Environment.getExternalStorageDirectory() + "/CRMData.xls");
        File dir = new File(Environment.getExternalStorageDirectory(), mobile);
        if (!dir.exists()) {
            dir.mkdir();
        }
//        ------------------------------------------------------------------------

        File file = new File(dir, System.currentTimeMillis() + ".jpg");  // This will give the name to image in above folder.
        imgpath = String.valueOf(file);
        Log.d("SHIV", String.valueOf(file));

        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        selectedImage = BitmapFactory.decodeFile(path);
        Log.d("SELECTED IMAGE", String.valueOf(selectedImage));
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createOrEditExcelSheet() {

        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("CRMSheet");

        Cursor c = myDatabase.dao().getCRMModel();

        if (c != null) {
            int size = c.getCount();
            if (size == 0) {
                Toast.makeText(mContext, "No data found", Toast.LENGTH_LONG).show();
            } else {
                String mobile, name, imgpath;
                HSSFRow hssfRow = hssfSheet.createRow(0);
                HSSFCell heading1Cell = hssfRow.createCell(0);
                HSSFCell heading2Cell = hssfRow.createCell(1);
                HSSFCell heading3Cell = hssfRow.createCell(2);
                heading1Cell.setCellValue("Name");
                heading2Cell.setCellValue("Mobile Number");
                heading3Cell.setCellValue("Image Location");

                int i = 2;
                while (c.moveToNext() == true) {
                    name = c.getString(c.getColumnIndex("name"));
                    mobile = c.getString(c.getColumnIndex("mobile"));
                    imgpath = c.getString(c.getColumnIndex("imgpath"));
                    hssfRow = hssfSheet.createRow(i);

                    HSSFCell hssfCell1 = hssfRow.createCell(0);
                    HSSFCell hssfCell2 = hssfRow.createCell(1);
                    HSSFCell hssfCell3 = hssfRow.createCell(2);
                    hssfCell1.setCellValue(name);
                    hssfCell2.setCellValue(mobile);
                    hssfCell3.setCellValue(imgpath);
                    if (!imgpath.equalsIgnoreCase("")) {
                        i++;
                    }
                }
            }
            c.close();
        }

        try {
            if (!filePath.exists()) {
                filePath.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);

            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    CRMAdapter adapter;

    private void showDataInRecyclerView() {

        Cursor c = myDatabase.dao().getCRMModel();

        if (c != null) {
            int size = c.getCount();
            if (size == 0) {
                Toast.makeText(mContext, "No data found", Toast.LENGTH_LONG).show();
            } else {
                String name, mobile, imgpath, message;

                while (c.moveToNext() == true) {
                    name = c.getString(c.getColumnIndex("name"));
                    mobile = c.getString(c.getColumnIndex("mobile"));
                    imgpath = c.getString(c.getColumnIndex("imgpath"));
                    message = c.getString(c.getColumnIndex("message"));

                    DataTable model = new DataTable(name, mobile, imgpath, message);
                    crmList.add(model);

                    mBinding.recCrmdata.setLayoutManager(new GridLayoutManager(mContext, 1));
                    adapter = new CRMAdapter(crmList, mContext);
                    mBinding.recCrmdata.setAdapter(adapter);
                    mBinding.recCrmdata.getLayoutManager().scrollToPosition(crmList.size() - 1);
                }
            }
            c.close();
        }
    }
}