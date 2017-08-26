package palarax.com.logbook.NFC;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * NDEF TAG object that retrieves and reads all TAG data
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 */

public class NdefTag {

    private static final String TAG = NdefTag.class.getSimpleName();



    public ArrayList read(Tag tag) {
        ArrayList<String> msgRecords = new ArrayList<>();
        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {

            Log.e(TAG, "NDEF is not supported by this Tag");
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
        Log.e(TAG, "Cache: " + ndefMessage);
        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            Log.e(TAG,"TYPE: "+new String(ndefRecord.getType(), StandardCharsets.UTF_8));
            Log.e(TAG,"toMimeType: "+ndefRecord.toMimeType());
            Log.e(TAG,"TNF: "+Short.toString(ndefRecord.getTnf()));
            Log.e(TAG,"toURI: "+ndefRecord.toUri());
            try {
                msgRecords.add(readText(ndefRecord));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "Unsupported Encoding", e);
            }
        }
        return msgRecords;
    }

    /**
     *
     * @param record record to be read
     * @return message from tag
     * @throws UnsupportedEncodingException throws an exception
     */
    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();
        String UTF8 = "UTF-8";
        String UTF16 = "UTF-16";
        // Get the Text Encoding
        try {
            String textEncoding = ((payload[0] & 128) == 0) ? UTF8 : UTF16;

            // Get the Language Code
            int languageCodeLength = payload[0] & 51;
            // Get the Text
            Log.i(TAG, textEncoding);
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }catch (ArrayIndexOutOfBoundsException e){Log.i(TAG,"Error: "+e);}
        return "";
    }

    /**
     * Get size of tag
     * @param tag scanned NFC tag object
     * @return size of the NFC tag
     */
    public String getSize(Tag tag)
    {
        Ndef ndef = Ndef.get(tag);
        String size = "undefined";
        try{
            ndef.connect();
            size = Integer.toString(ndef.getMaxSize());
            ndef.close();
        }
        catch(Exception e) {
            Log.i(TAG, "Error when reading tag: "+e);
        }

        return size;
    }
}
