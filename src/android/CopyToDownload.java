package tonib.copytodownload;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

            // Get parameters
            String path = args.getString( 0 );
            String title = args.getString( 1 ) ;
            String description = args.getString( 2 );
            boolean isMediaScannerScannable = args.getBoolean( 3 );
            String mimeType = args.getString( 4 ) ;
            boolean showNotification = args.getBoolean( 5 );

            try {
                long result = copyToDownload(this.cordova.getActivity(), path, title, description, isMediaScannerScannable, mimeType, showNotification);
                // Cannot return a long (oh!). Return a string instead with that long
                //callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result));
                callbackContext.success( new Long(result).toString() );
            }
            catch(Exception e) {
                Log.e("CopyToDownload", e.getMessage(), e);
                callbackContext.error( e.getMessage() );
            }

            return true;
        }
        
        return false;
    }

    /**
     * Copy a file to the Download folder, and notify of this copy to the Download Manager
     * @param ctx Android context
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
    static private long copyToDownload(Context ctx, String url, String title, String description, boolean isMediaScannerScannable, String mimeType,
        boolean showNotification) throws Exception {

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
