
// The stuff
module.exports = window.CopyToDownload = CopyToDownload = {

    /**
     * Copy a file to the Download directory, and notify the DownloadManager of that file.
     * @param {string}  url     URL / path to the local file to copy to the Download directory.
     * @param {string}  title   the title that would appear for this file in Downloads App.
     * @param {string}  description the description that would appear for this file in Downloads App.
     * @param {boolean} isMediaScannerScannable true if the file is to be scanned by MediaScanner. Files scanned by MediaScanner appear in 
     *                                          the applications used to view media (for example, Gallery app).
     * @param {string}  mimeType    mimetype of the file.
     * @param {boolean} showNotification    true if a notification is to be sent, false otherwise
     * @param {function}    callbackSuccess Callback to call if the call was OK. A parameter will be passed with the id given by the Download Manager 
     *                                      to the download, as an String
     * @param {function}    callbackError   Callback to call if the call did fail. A parameter with the error will be passed
     */
    copyToDownload: function( url, title, description, isMediaScannerScannable, mimeType, showNotification , callbackSuccess, callbackError ) {

        var args = [ url, title, description, isMediaScannerScannable, mimeType, showNotification ];
        return cordova.exec(callbackSuccess, callbackError, "CopyToDownload", "copyToDownload", args);
    },

    /**
     * Copy a file to a destination directory, with native URLs
     * @param {string} srcFileUrl URL to to the local file to copy (copy source)
     * @param {string} dstDirectoryUrl URL to the local directory where to copy (copy destination)
     * @param {function} callbackSuccess Success callback. A parameter will be passed with the new copied file URL
     * @param {function}    callbackError   Callback to call if the call did fail. A parameter with the error will be passed
     */
    copyNativePaths: function( srcFileUrl , dstDirectoryUrl , callbackSuccess, callbackError ) {

        var args = [ srcFileUrl , dstDirectoryUrl ];
        return cordova.exec(callbackSuccess, callbackError, "CopyToDownload", "copyNativePaths", args);
    }

};