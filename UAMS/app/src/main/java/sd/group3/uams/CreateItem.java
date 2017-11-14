package sd.group3.uams;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ericjames on 10/20/17.
 */

public class CreateItem extends Fragment {
    EditText name, quantity, location, description;
    ImageView imageView;
    String image, serialNum;
    Button submitItem, cancelSubmit;
    Boolean imageSet = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final View contentView = view;
        final TextView appName = getActivity().findViewById(R.id.application_name),
                fragmentHeader = getActivity().findViewById(R.id.create_item_header),
                fragmentMessage = getActivity().findViewById(R.id.create_item_message);

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                contentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = contentView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    appName.setVisibility(View.INVISIBLE);
                    fragmentHeader.setVisibility(View.INVISIBLE);
                    fragmentMessage.setVisibility(View.INVISIBLE);
                }
                else {
                    // keyboard is closed
                    appName.setVisibility(View.VISIBLE);
                    fragmentHeader.setVisibility(View.VISIBLE);
                    fragmentMessage.setVisibility(View.VISIBLE);
                }
            }
        });

        getActivity().setTitle("Create New Item");

        imageView = getActivity().findViewById(R.id.create_item_image);
        submitItem = getActivity().findViewById(R.id.item_submit_button);
        cancelSubmit = getActivity().findViewById(R.id.item_cancel_button);
        buttonListener();
    }

    private void browseGallery(View v)
    {
        Intent gallery = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri imageUri = data.getData();
            imageView.setImageURI(imageUri);
            System.out.println(imageUri);
            image = imageUri.toString();
            System.out.println(image);
            imageSet = true;
        }
    }

    public void buttonListener(){
        submitItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(v);
            }
        });

        cancelSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel(v);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseGallery(v);
            }
        });
    }

    public void submit(View v) {
        InventoryDBAdapter db = new InventoryDBAdapter(this.getContext());
        db.openToWrite();

        String itemSerialNumber =  ((MainActivity)getActivity()).serialNum;

        //Declare values for table entry
        name = getActivity().findViewById(R.id.create_item_name);
        quantity = getActivity().findViewById(R.id.create_item_quantity);
        description = getActivity().findViewById(R.id.create_item_description);
        location = getActivity().findViewById(R.id.create_item_location);
        serialNum = (itemSerialNumber != null ? itemSerialNumber : "");

        if (imageSet) {
            db.createItemEntryWithImage(name.getText().toString(),
                    Integer.parseInt(quantity.getText().toString()),
                    description.getText().toString(), location.getText().toString(), image,
                    ((MainActivity)getActivity()).warehouseId, serialNum);
        }
        else {
            db.createItemEntryNoImage(name.getText().toString(),
                    Integer.parseInt(quantity.getText().toString()),
                    description.getText().toString(), location.getText().toString(),
                    ((MainActivity)getActivity()).warehouseId, serialNum);
        }
        db.close();

        //Mark the epc as processed and return to the previous fragment
        ((MainActivity)getActivity()).epcProcessed(itemSerialNumber);
        getFragmentManager().popBackStackImmediate();
    }

    public void cancel(View v) {
        getFragmentManager().popBackStackImmediate();
    }
}
