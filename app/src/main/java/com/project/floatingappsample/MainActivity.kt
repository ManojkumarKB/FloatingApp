package com.project.floatingappsample

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    // The reference variables for the
    // Button, AlertDialog, EditText
    // classes are created
    private var minimizeBtn: Button? = null
    private var dialog: AlertDialog? = null
    private var descEditArea: EditText? = null
    private var save: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // The Buttons and the EditText are connected with
        // the corresponding component id used in layout file

        // The Buttons and the EditText are connected with
        // the corresponding component id used in layout file
        minimizeBtn = findViewById(R.id.buttonMinimize)
        descEditArea = findViewById<EditText>(R.id.descEditText)
        save = findViewById(R.id.saveBtn)

        // If the app is started again while the
        // floating window service is running
        // then the floating window service will stop

        // If the app is started again while the
        // floating window service is running
        // then the floating window service will stop
        if (isMyServiceRunning()) {
            // onDestroy() method in FloatingWindowGFG
            // class will be called here
            stopService(Intent(this@MainActivity, FloatingWindow::class.java))
        }

        // currentDesc String will be empty
        // at first time launch
        // but the text written in floating
        // window will not gone

        // currentDesc String will be empty
        // at first time launch
        // but the text written in floating
        // window will not gone
        descEditArea?.setText(Common.currentDesc)
        descEditArea?.setSelection(descEditArea?.getText().toString().length)

        // The EditText string will be stored in
        // currentDesc while writing

        // The EditText string will be stored in
        // currentDesc while writing
        descEditArea?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                // Not Necessary
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                Common.currentDesc = descEditArea?.getText().toString()
            }

            override fun afterTextChanged(editable: Editable) {
                // Not Necessary
            }
        })

        // Here the save button is used just to store the
        // EditText string in saveDesc variable

        // Here the save button is used just to store the
        // EditText string in saveDesc variable
        save?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Common.savedDesc = descEditArea?.getText().toString()
                descEditArea?.setCursorVisible(false)
                descEditArea?.clearFocus()
                Toast.makeText(this@MainActivity, "Text Saved!!!", Toast.LENGTH_SHORT).show()
            }
        })

        // The Main Button that helps to minimize the app

        // The Main Button that helps to minimize the app
        minimizeBtn?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // First it confirms whether the
                // 'Display over other apps' permission in given
                if (checkOverlayDisplayPermission()) {
                    // FloatingWindowGFG service is started
                    startService(Intent(this@MainActivity, FloatingWindow::class.java))
                    // The MainActivity closes here
                    finish()
                } else {
                    // If permission is not given,
                    // it shows the AlertDialog box and
                    // redirects to the Settings
                    requestOverlayDisplayPermission()
                }
            }

        })
    }

    private fun isMyServiceRunning(): Boolean {
        // The ACTIVITY_SERVICE is needed to retrieve a
        // ActivityManager for interacting with the global system
        // It has a constant String value "activity".
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // A loop is needed to get Service information that are currently running in the System.
        // So ActivityManager.RunningServiceInfo is used. It helps to retrieve a
        // particular service information, here its this service.
        // getRunningServices() method returns a list of the services that are currently running
        // and MAX_VALUE is 2147483647. So at most this many services can be returned by this method.
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            // If this service is found as a running, it will return true or else false.
            if (FloatingWindow::class.java.getName() == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun requestOverlayDisplayPermission() {
        // An AlertDialog is created
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        // This dialog can be closed, just by
        // taping outside the dialog-box
        builder.setCancelable(true)

        // The title of the Dialog-box is set
        builder.setTitle("Screen Overlay Permission Needed")

        // The message of the Dialog-box is set
        builder.setMessage("Enable 'Display over other apps' from System Settings.")

        // The event of the Positive-Button is set
        builder.setPositiveButton("Open Settings",
            DialogInterface.OnClickListener { dialog, which -> // The app will redirect to the 'Display over other apps' in Settings.
                // This is an Implicit Intent. This is needed when any Action is needed
                // to perform, here it is
                // redirecting to an other app(Settings).
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )

                // This method will start the intent. It takes two parameter,
                // one is the Intent and the other is
                // an requestCode Integer. Here it is -1.
                startActivityForResult(intent, RESULT_OK)
            })
        dialog = builder.create()
        // The Dialog will show in the screen
        dialog?.show()
    }

    private fun checkOverlayDisplayPermission(): Boolean {
        // Android Version is lesser than Marshmallow
        // or the API is lesser than 23
        // doesn't need 'Display over other apps' permission enabling.
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            // If 'Display over other apps' is not enabled it
            // will return false or else true
            if (!Settings.canDrawOverlays(this)) {
                false
            } else {
                true
            }
        } else {
            true
        }
    }

}