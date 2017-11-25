package sd.group3.uams;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Eric James for use in Senior Design @ University of Central Florida.
 * Group 3 Fall 2017
 *
 * Class Utilizes iTextg 5.5.9 Library for generating the PDF.
 *
 * iText Home page
 *  https://itextpdf.com
 *
 * iText Developer Examples
 *  https://developers.itextpdf.com/examples
 *
 * iText Examples Used:
 *      WriteOnFirstPage.java
 *          https://developers.itextpdf.com/examples/miscellaneous-itext5/write-previous-page
 *      RightCornerTable.java
 *          https://developers.itextpdf.com/examples/tables-itext5/cell-and-table-widths
 *      TextFooter.java
 *          https://developers.itextpdf.com/examples/page-events/page-events-headers-and-footers#692-textfooter.java
 *
 * Releases Notes of iText 5.5.9
 *  https://itextpdf.com/release/iText559
 *
 * GitHub with iText Version Releases
 *  https://github.com/itext/itextpdf/releases
 */

public class Report extends Fragment {
    private Button generate;
    private Cursor inventory;
    private String warehouseName;
    private int totalItems;

    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_generate_report, container, false);
        super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Generate Report");
        enableGenerateButton();
    }

    // Method for creating a pdf file from text, saving it then opening it for display
    public void createReport() {

        Document doc = new Document();

        try {
            //Create directory in cache
            File dir = new File(getActivity().getExternalCacheDir(), "Dir");
            if(!dir.exists())
               dir.mkdirs();

            //Create file to write the PDF document into
            File file = new File(dir, warehouseName + " Report.pdf");
            try { file.createNewFile(); }
            catch (IOException e){ System.out.println("Did not create file."); }
            FileOutputStream fOut = new FileOutputStream(file);

            //Create PdfWriter instance for creating the report
            PdfWriter writer = PdfWriter.getInstance(doc, fOut);
            MyFooter event = new MyFooter();
            writer.setPageEvent(event);

            //Open the document for writing
            doc.open();

            //Build the warehouse inventory report
            createReportHeaders(doc, writer);
            doc.add(createReportTable(doc, writer));


        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        }
        finally {
            doc.close();
        }
        viewPdf("Dir/" + warehouseName + " Report.pdf");
    }

    /*
    ==========================================
    =            CREATED METHODS             =
    ==========================================
    */

    private void enableGenerateButton() {
        //Add a button, get the field values on submission,
        generate = getActivity().findViewById(R.id.button_generate_report);

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inventory = getReportInventory();
                totalItems = getFullInventoryQuantity();
                createReport(); }
        });
    }

    // Method for opening and viewing a pdf file
    private void viewPdf(String filePath) {

        File pdfFile = new File(getActivity().getExternalCacheDir() + "/" + filePath);
        Uri path = FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".uams.provider", pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this.getContext(), "Cannot read pdf file", Toast.LENGTH_SHORT).show();
        }
    }

    // Populate the Cursor that holds all item entities for the active warehouse
    private Cursor getReportInventory() {
        warehouseName = ((MainActivity)getActivity()).warehouseName;

        InventoryDBAdapter db = new InventoryDBAdapter(this.getContext());
        db.openToRead();

        return db.getAssociatedItems(((MainActivity)getActivity()).activeWarehouseId);
    }

    // Get total sum of items in Warehouse
    private int getFullInventoryQuantity() {
        int sum = 0;
        if (inventory != null) {
            if (inventory.moveToFirst()){
                do {
                sum += inventory.getInt(inventory.getColumnIndex("Quantity"));
                } while (inventory.moveToNext());
            }
        }
        return sum;
    }

    private void createReportHeaders(Document doc, PdfWriter writer) throws DocumentException {
        // Write Header to top left of page
        PdfContentByte cb = writer.getDirectContent();
        PdfTemplate field = cb.createTemplate(400, 100);
        Image header = Image.getInstance(field);
        doc.add(header);
        ColumnText ct = new ColumnText(field);
        ct.setSimpleColumn(new Rectangle(0, 0, 400, 100));
        ct.addElement(
                new Paragraph(
                        new Phrase(warehouseName,
                                new Font(Font.FontFamily.HELVETICA,20, Font.BOLD))));
        ct.addElement(
                new Paragraph(
                        new Phrase("Inventory Report",
                                new Font(Font.FontFamily.HELVETICA,17))));
        ct.go();

        // Font for column headers
        Font bold = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);
        PdfPTable table = new PdfPTable(1);

        // Create table for displaying asset total quantity
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setWidthPercentage(30);

        // Column Title
        PdfPCell cell = new PdfPCell(new Phrase(" Total Assets" , bold));
        cell.setBorderColor(BaseColor.GRAY);
        cell.setBorderWidth(2f);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        table.addCell(cell);

        // Column Data
        PdfPCell cellTwo = new PdfPCell(new Phrase(Integer.toString(totalItems)));
        cellTwo.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cellTwo.setBorderColor(BaseColor.GRAY);
        cellTwo.setBorderWidth(2f);
        cellTwo.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cellTwo);

        // Write asset total quantity to top right of page
        table.setTotalWidth(90);
        PdfContentByte canvas = writer.getDirectContent();
        table.writeSelectedRows(0, -1, doc.right() - 90, doc.top(), canvas);
    }

    //Create and populate the inventory table
    private PdfPTable createReportTable(Document doc, PdfWriter writer) throws DocumentException{
        PdfContentByte cb = writer.getDirectContent();
        PdfTemplate field = cb.createTemplate(400, 100);
        Image header = Image.getInstance(field);
        doc.add(header);
        //Font for column headers
        Font bold = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);

        // Three columns: | Serial Number/ID | Item Name | Quantity |
        PdfPTable table = new PdfPTable(new float[] { 3, 3, 1 });
        table.setWidthPercentage(100);

        // Cell Object that will be used to populate table
        PdfPCell cell;

        //****** FIRST ROW ******//
        // First row will be Column Headers: | Serial Number/ID | Item Name | Quantity |
        cell = new PdfPCell(new Phrase("Serial #", bold));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setBorderColor(BaseColor.GRAY);
        cell.setBorderWidth(2f);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Asset", bold));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setBorderColor(BaseColor.GRAY);
        cell.setBorderWidth(2f);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Qty", bold));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setBorderColor(BaseColor.GRAY);
        cell.setBorderWidth(2f);
        table.addCell(cell);

        //****** SUBSEQUENT ROWS ******//
        // Iterate through a Cursor with all inventory items in a do-while loop
        /*
            Because cells are populate from left to right, we must follow the order of the headers
            1) Serial Numbers/IDs
            2) Item Name
            3) Quantity
         */
        if (inventory != null) {
            if (inventory.moveToFirst()){
                do {
                    //Item ID
                    cell = new PdfPCell(new Phrase(" " +
                            Integer.toString(inventory.getInt(inventory.getColumnIndex("_id")))));
                    cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT);
                    cell.setBorderColor(BaseColor.GRAY);
                    cell.setBorderWidth(2f);
                    table.addCell(cell);

                    //Serial Number
//                    cell = new PdfPCell(new Phrase(inventory.getString(inventory.getColumnIndex("Serial_Num"))));
//                    cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT);
//                    table.addCell(cell);

                    //Item Name
                    cell = new PdfPCell(new Phrase(" " +
                            inventory.getString(inventory.getColumnIndex("Name"))));
                    cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT);
                    cell.setBorderColor(BaseColor.GRAY);
                    cell.setBorderWidth(2f);
                    table.addCell(cell);

                    //Quantity
                    cell = new PdfPCell(new Phrase(" " +
                            Integer.toString(inventory.getInt(inventory.getColumnIndex("Quantity")))));
                    cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT);
                    cell.setBorderColor(BaseColor.GRAY);
                    cell.setBorderWidth(2f);
                    table.addCell(cell);

                } while (inventory.moveToNext());
                cell = new PdfPCell(new Phrase(""));
                cell.setColspan(3);
                cell.setBorder(PdfPCell.TOP);
                cell.setBorderColor(BaseColor.GRAY);
                cell.setBorderWidth(2f);
                table.addCell(cell);
            }
        }
        return table;
    }

    /*
    ==========================================
    =              INNER CLASSES             =
    ==========================================
    */

    // Custom Class for displaying the page numbers in a footer
    class MyFooter extends PdfPageEventHelper {
        Font ffont = new Font(Font.FontFamily.UNDEFINED, 8, Font.ITALIC);
        PdfTemplate total;

        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(30, 16);
        }

        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase footer = new Phrase(warehouseName + " - Page " + writer.getPageNumber(), ffont);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
        }

        public void onCloseDocument(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(total, Element.ALIGN_LEFT,
                    new Phrase(String.valueOf(writer.getPageNumber() - 1)),
                    2, 2, 0);
        }
    }
}
