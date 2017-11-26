package tonib.copytodownload;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.app.DownloadManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class CopyToDownload extends CordovaPlugin {

    /**
     * Write storage Android permission
     */
    private static final String WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    /**
     * Write storage request code to ask permission to the user
     */
    private static final int WRITE_STORAGE_REQ_CODE = 0;

    /**
     * The latest pending callback context.
     * Used if a user permission request is needed. 
     * I don't know if this the rigth way to do it. Documentation says CallbackContext will be deleted if a new activity is launched, but, 
     * what if a permission dialog is launched???
     */
    private CallbackContext pendingCallbackContext = null;

    /**
     * The latest pending callback arguments.
     * Used if a user permission request is needed. 
     */
    private JSONArray pendingArguments = null;

    /**
     * Constructor.
     */
    public CopyToDownload() {
    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArray of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false if not.
     */
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        
        if( action.equals( "copyToDownload" ) ) {

            // Check WRITE_EXTERNAL_STORAGE permission 
            if( cordova.hasPermission( WRITE_STORAGE ) )
                // Run the process
                copyToDownload( callbackContext , args );
            else
                // Ask permission to the user
                this.pendingCallbackContext = callbackContext;
                this.pendingArguments = args;
                cordova.requestPermission(this, WRITE_STORAGE_REQ_CODE, WRITE_STORAGE);

            return true;
        }
        
        return false;
    }

    /**
     * Called when a user permission request is resolved
     */
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException
    {
        // Check callback context is safe (maybe not needed)
        if( this.pendingCallbackContext == null ) {
            Log.e("CopyToDownload", "this.pendingCallbackContext is null !!!");
            return;
        }

        // Check result
        for( int r : grantResults )
        {
            if(r == PackageManager.PERMISSION_DENIED)
            {
                // User denied the permission
                this.pendingCallbackContext.error( "Permission denied to write to external storage" );
                return;
            }
        }

        // Check the request type
        switch(requestCode)
        {
            case WRITE_STORAGE_REQ_CODE:
                copyToDownload(this.pendingCallbackContext, this.pendingArguments);
                break;
        }

        // Release memory
        this.pendingCallbackContext = null;
        this.pendingArguments = null;
    }

    /**
     * Copy a file to the Download folder, and notify of this copy to the Download Manager
     * @param callbackContext The callback context
     * @param args Call arguments
     */
    private void copyToDownload( CallbackContext callbackContext, JSONArray args ) {

        try {
            // Get parameters
            String path = args.getString( 0 );
            String title = args.getString( 1 ) ;
            String description = args.getString( 2 );
            boolean isMediaScannerScannable = args.getBoolean( 3 );
            String mimeType = args.getString( 4 ) ;
            boolean showNotification = args.getBoolean( 5 );

        
            long result = copyToDownload( path, title, description, isMediaScannerScannable, mimeType, showNotification);
            // Cannot return a long (oh!). Return a string instead with that long
            //callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result));
            callbackContext.success( new Long(result).toString() );
        }
        catch(Exception e) {
            Log.e("CopyToDownload", e.getMessage(), e);
            callbackContext.error( e.getMessage() );
        }
    }

    /**
     * Copy a file to the Download folder, and notify of this copy to the Download Manager
     * @param url URI to the local file to copy to the Download directory
     * @param title the title that would appear for this file in Downloads App.
     * @param description the description that would appear for this file in Downloads App.
     * @param isMediaScannerScannable true if the file is to be scanned by MediaScanner. Files scanned by MediaScanner appear in
     *                                the applications used to view media (for example, Gallery app).
     * @param mimeType mimetype of the file.
     * @param showNotification true if a notification is to be sent, false otherwise
     * @return the id given by the Download Manager to the download
     * @throws Exception Error!
     */
    private long copyToDownload(String url, String title, String description, boolean isMediaScannerScannable, String mimeType,
        boolean showNotification) throws Exception {

        // Android context:
        Context ctx = this.cordova.getActivity();

        // Get file info
        File f = new File( new URI(url) );
        String path = f.getAbsolutePath();

        // Resolve the Download directory
        File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Copy the file (require WRITE_EXTERNAL_STORAGE permission)
        File targetFile = new File(downloadDirectory, f.getName() );
        String targetFilePath = targetFile.getAbsolutePath();
        copyFile(path, targetFilePath);

        // Notify the Download Manager (require INTERNET permission)
        DownloadManager downloadmanager = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
        return downloadmanager.addCompletedDownload(title, description, isMediaScannerScannable , mimeType , targetFilePath , f.length() , showNotification );
    }

    /**
     * Copy a file
     * https://stackoverflow.com/questions/4178168/how-to-programmatically-move-copy-and-delete-files-and-directories-on-sd
     * @param sourceFilePath Source file path
     * @param targetFilePath Target file path
     * @throws Exception Error!
     */
    static private void copyFile(String sourceFilePath, String targetFilePath) throws Exception {

        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(sourceFilePath);
            out = new FileOutputStream(targetFilePath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;
        }
        finally {
            try {
                if( in != null )
                    in.close();
                if( out != null )
                    out.close();
            }
            catch(Exception ex2){}
        }
    }
}
