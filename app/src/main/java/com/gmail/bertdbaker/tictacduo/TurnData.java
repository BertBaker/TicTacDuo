package com.gmail.bertdbaker.tictacduo;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.ImageView;

public class TurnData {

    public static final String TAG = "TurnData";

    public Boolean gameOver = false;
    public Boolean xTurn = true;

    public char[] cellState = {' ', ' ',' ', ' ', ' ', ' ', ' ', ' ', ' '};  //options are X, O, and space

    public int move = -1; //which move just happened:  going from 0 to 8 (nine possible moves), negative 1 means no move yet

    public moveTrack[] moveTracker = new moveTrack[9];  //keeps track of what spot is taken in each move and who took it
    public static class moveTrack {
        int spot = -1;  //minus 1 means the spot is not taken
        boolean xTurn;
    }

    public TurnData() {
    }

    // This is the byte array to be written out to google play.
    public byte[] toGoogle() {
        JSONObject retVal = new JSONObject();
        JSONArray jsonArr = new JSONArray();
        String cellStateString = new String(cellState);

        try {
            retVal.put("gameOver", gameOver);
            retVal.put("xTurn", xTurn);
            retVal.put("cellState", cellStateString);

            Log.i("TurnData", "cellState=" + cellState[0] + cellState[1] + cellState[2] + cellState[3] + cellState[4] + cellState[5] + cellState[6] + cellState[7] + cellState[8] + ".");
            Log.i("TurnData", "cellStateString=" + cellStateString + ".");

            retVal.put("move", move);
            for (int i=0; i<9; i++){
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("spot", moveTracker[i].spot);
                jsonObj.put("xTurn", moveTracker[i].xTurn);
                jsonArr.put(jsonObj);
            }
            retVal.put("moveTracker", jsonArr);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String st = retVal.toString();

        Log.d(TAG, "==== PERSISTING\n" + st);

        return st.getBytes(Charset.forName("UTF-8"));
    }

    // This reads a byte array from google play
    static public TurnData fromGoogle(byte[] byteArray) {

        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            return new TurnData();
        }

        String st = null;
        try {
            st = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }

        Log.d(TAG, "====UNPERSIST \n" + st);

        TurnData retVal = new TurnData();

        try {
            JSONObject obj = new JSONObject(st);

            if (obj.has("gameOver")) {
                retVal.gameOver = obj.getBoolean("gameOver");
            }
            if (obj.has("xTurn")) {
                retVal.xTurn = obj.getBoolean("xTurn");
            }
            if (obj.has("cellState")) {
                retVal.cellState = obj.getString("cellState").toCharArray();
            }
            if (obj.has("move")) {
                retVal.move = obj.getInt("move");
            }
            if (obj.has("moveTracker")) {
                JSONArray jsonArr = obj.getJSONArray("moveTracker");
                for (int i=0; i<9 ; i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    retVal.moveTracker[i] = new moveTrack();
                    retVal.moveTracker[i].spot = jsonObj.getInt("spot");
                    retVal.moveTracker[i].xTurn = jsonObj.getBoolean("xTurn");
                }

            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return retVal;
    }
}



