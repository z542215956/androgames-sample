package net.androgames.blog.sample.customdialog;

import net.androgames.blog.sample.customdialog.dialog.CustomDialog;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class CustomDialogActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        CustomDialog.Builder customBuilder = new
        CustomDialog.Builder(CustomDialogActivity.this);
        customBuilder.setTitle("Custom dialog")
               .setMessage("This is my custom dialog")
               .setNegativeButton("Cancel", new View.OnClickListener() {
                       public void onClick(View v) {
                               CustomDialogActivity.this.dismissDialog(CUSTOM_DIALOG);
                       }
               })
               .setPositiveButton("Confirm", new View.OnClickListener() {
                       public void onClick(View v) {
                               CustomDialogActivity.this.dismissDialog(CUSTOM_DIALOG);
                       }
               });
        dialog = customBuilder.create();
        
    }
}