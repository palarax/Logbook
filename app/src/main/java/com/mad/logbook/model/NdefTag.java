package com.mad.logbook.model;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.util.Log;

import com.mad.logbook.Utils;

import java.io.UnsupportedEncodingException;
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
        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
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
        // Get the Text Encoding
        try {
            String textEncoding = ((payload[0] & 128) == 0) ? Utils.UTF8 : Utils.UTF16;

            // Get the Language Code
            int languageCodeLength = payload[0] & 51;
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
