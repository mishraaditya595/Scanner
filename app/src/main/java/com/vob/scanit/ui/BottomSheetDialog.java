package com.vob.scanit.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.vob.scanit.R;
import com.vob.scanit.ui.activities.MainActivity;
import com.vob.scanit.ui.fragments.HomeFragment;

import java.io.File;

/* BottomSheetDialog implements BottomSheetDialogFragment to display a dialog with three options for the
   corresponding file: Share, Delete and Rename */
public class BottomSheetDialog extends BottomSheetDialogFragment {

    File file;
    public BottomSheetDialog(File file) {
        this.file = file;
    }

    /*Following function inflates the XML file to show the dialog box with the three options*/
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.file_options_bottom_sheet_view,
                container, false);

        final Uri uri = FileProvider.getUriForFile(getContext(), "com.vob.scanit.provider", file);

        Button deleteBtn = v.findViewById(R.id.delete_btn);
        Button shareBtn = v.findViewById(R.id.share_btn);
        Button renameBtn = v.findViewById(R.id.rename_btn);

        /*Opens a dialog box with options for the user to choose from to share the file with*/
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("application/pdf");
                startActivity(Intent.createChooser(intent,"Share via"));
                dismiss();
            }
        });

        /* Renames the file and dismisses the dialog */
        renameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        /* Deletes the file and dismisses the dialog*/
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                File fdelete = file;
                if (fdelete.exists())
                {
                    if (fdelete.delete())
                    {
                        DynamicToast.makeSuccess(getActivity(),"File deleted successfully.",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"File could not be deleted." + uri.getPath(),Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(),"Error: File not found",Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }
        });
        return v;
    }
}
