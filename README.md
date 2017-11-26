## Copy to Download folder

Cordova plugin for Android only (API >= 12). It allows to copy a local file to the Download folder and notify the
Download Manager app that the file was "downloaded".

Additionally you can show a notification with that "download"

Requires WRITE_EXTERNAL_STORAGE and INTERNET permission. It can prompt the user for a permission to write the file.

Tested only with Cordova 6.1

# Installation

```bash
cordova plugin add https://github.com/tonib/cordova-plugin-copytodownload.git
```

or add to your config.xml file:

```xml
<widget ...>
    <plugin name="cordova-plugin-copytodownload" spec="https://github.com/tonib/cordova-plugin-copytodownload.git#1.0" />
</widget>
```

# API

```javascript
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
    copyToDownload: function( url, title, description, isMediaScannerScannable, mimeType, showNotification , callbackSuccess, callbackError );

}
```
