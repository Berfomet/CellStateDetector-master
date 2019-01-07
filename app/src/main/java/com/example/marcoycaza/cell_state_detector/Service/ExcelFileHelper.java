package com.example.marcoycaza.cell_state_detector.Service;

import android.app.Application;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import java.io.File;
import java.util.Locale;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelFileHelper {

    Application application;

    public ExcelFileHelper (Application application){
        this.application=application;
    }

    public void generateExcelFile(CeldaDb database){
        CeldaDb dbExportar = database;
        Handler handler = new Handler(Looper.getMainLooper());

        final Cursor cursor = dbExportar.celdaRepository().getCelda();

        File sd = Environment.getExternalStorageDirectory();
        String csvFile = "StoredCells.xls";

        File directory = new File(sd.getAbsolutePath()+"/ExcelTestSheets/");
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("cellList", 0);
            // column and row
            sheet.addCell(new Label(0, 0, "Id"));
            sheet.addCell(new Label(1, 0, "CellName"));
            sheet.addCell(new Label(1, 0, "CellId"));
            sheet.addCell(new Label(2, 0, "Technology"));
            sheet.addCell(new Label(3, 0, "Power"));


            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    String cellName = cursor.getString(cursor.getColumnIndex("cell_name"));
                    String cellId = cursor.getString(cursor.getColumnIndex("cell_id"));
                    String technology = cursor.getString(cursor.getColumnIndex("technology"));
                    String power = cursor.getString(cursor.getColumnIndex("potencia"));

                    int i = cursor.getPosition() + 1;
                    sheet.addCell(new Label(0, i, id));
                    sheet.addCell(new Label(1, i, cellName));
                    sheet.addCell(new Label(2, i, cellId));
                    sheet.addCell(new Label(3, i, technology));
                    sheet.addCell(new Label(3, i, power));
                } while (cursor.moveToNext());
            }

            //closing cursor
            cursor.close();
            workbook.write();
            workbook.close();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(application,
                            "Data Exported in a Excel Sheet", Toast.LENGTH_SHORT).show();
                }
            });

        } catch(Exception e){
            e.printStackTrace();
        }
    }

}
