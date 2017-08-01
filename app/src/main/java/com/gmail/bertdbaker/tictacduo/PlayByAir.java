package com.gmail.bertdbaker.tictacduo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class PlayByAir extends AppCompatActivity {

    //turn data sent to and from google
    public byte[] persistantData;

    //turn data internal to this device
    RadioButton playByAirButton;

    TextView message;
    TextView redoText;
    TextView undoText;

    ImageView redoGraphic;
    ImageView undoGraphic;
    ImageView winningLine;

    ImageView[] imageViewArray = {null, null, null, null, null, null, null, null, null};

    AlertDialog.Builder alert;
    public void displayAlert(View paramView){alert.show();}

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_play_by_air);
        Intent intent = getIntent();

        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        playByAirButton = ((RadioButton)findViewById(R.id.play_by_air_button));
        playByAirButton.setChecked(true);

        message = ((TextView)findViewById(R.id.message));
        redoText = ((TextView)findViewById(R.id.redo_text));
        undoText = ((TextView)findViewById(R.id.undo_text));

        redoGraphic = ((ImageView)findViewById(R.id.redo_graphic));
        undoGraphic = ((ImageView)findViewById(R.id.undo_graphic));
        winningLine = ((ImageView)findViewById(R.id.winning_line));

        TurnData mTurnData;
        mTurnData = new TurnData();
        int i = 0;
        while (i < mTurnData.moveTracker.length) {
            mTurnData.moveTracker[i] = new TurnData.moveTrack();
            i += 1;}

        Log.i("onCreate", "persistantData" + persistantData);

        persistantData = mTurnData.toGoogle();

        Log.i("onCreate", "persistantData" + persistantData);

        GridView gridview = (GridView)findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {

                TurnData mTurnData = TurnData.fromGoogle(persistantData);

                ImageView imageView = (ImageView) v;

                Log.i("GridView", "cellState=" + mTurnData.cellState[0] + mTurnData.cellState[1] + mTurnData.cellState[2] + mTurnData.cellState[3] + mTurnData.cellState[4] + mTurnData.cellState[5] + mTurnData.cellState[6] + mTurnData.cellState[7] + mTurnData.cellState[8] + ".");

                if ((mTurnData.cellState[position] != ' ') || (mTurnData.gameOver)) {
                    return;
                }
                if (mTurnData.xTurn) {
                    imageView.setImageResource(R.drawable.x);
                    message.setText("O's Turn");
                } else {
                    imageView.setImageResource(R.drawable.o);
                    message.setText("X's Turn");
                }

                mTurnData.move++;

                Log.i("GridView", "persistantData" + persistantData);

                mTurnData.moveTracker[mTurnData.move].spot = position;
                mTurnData.moveTracker[mTurnData.move].xTurn = mTurnData.xTurn;

                //null out the rest of the moveTracker now that we are moving forward
                int j = mTurnData.move + 1;
                while (j < 9) {
                    mTurnData.moveTracker[j].spot = -1;
                    j += 1;
                }

                mTurnData.cellState[position] = mTurnData.xTurn ? 'X' : 'O';

                mTurnData.gameOver = checkBoard(mTurnData);

                if (mTurnData.gameOver) {
                    //make sure undo button graphics off
                    undoGraphic.setImageResource(R.drawable.blank);
                    undoText.setText("");
                } else {
                    //make sure undo button graphics on
                    undoGraphic.setImageResource(R.drawable.undo);
                    undoText.setText("undo");
                }

                //turn redo button graphics off
                redoGraphic.setImageResource(R.drawable.blank);
                redoText.setText("");

                mTurnData.xTurn = !mTurnData.xTurn;
                Log.i("GridView", "xTurn = " + mTurnData.xTurn);

                persistantData = mTurnData.toGoogle();

            }

        });
    }

    public void playPhone(View view) {
        RadioButton playPhoneButton = (RadioButton)findViewById(R.id.play_phone_button);

        final Intent localIntent = new Intent(this, PlayPhone1.class);

        Bundle localBundle = new Bundle();
        localBundle.putInt("PROGRESS", 50);
        localIntent.putExtras(localBundle);

        alert = new AlertDialog.Builder(this);
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                startActivity(localIntent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                return;
            }
        });
        alert.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                return;
            }
        });

        playPhoneButton.setChecked(false);
        alert.setMessage("Do you want to start over, and play against the phone?");
        alert.show();
    }

    public void playSharing(View view) {
        RadioButton playSharingButton = (RadioButton)findViewById(R.id.play_sharing_button);
        final Intent localIntent = new Intent(this, PlaySharing.class);
        alert = new AlertDialog.Builder(this);
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {startActivity(localIntent);
                overridePendingTransition(0, 0);
                finish();}});
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });
        playSharingButton.setChecked(false);
        alert.setMessage("Do you want to start over, and play by sharing the phone?");
        alert.show();
    }

    public void playByAir(View view) {//if playByAir button is clicked then do nothing
    }

    public void startOver(final View view) {
        final Intent intent = new Intent(this, PlayByAir.class);

        TurnData mTurnData = TurnData.fromGoogle(persistantData);

        if (mTurnData.gameOver)
        {
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
            return;
        }
        alert = new AlertDialog.Builder(this);
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                return;
            }
        });
        alert.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                return;
            }
        });
        alert.setMessage("Do you want to start over?");
        alert.show();
    }

    public void onUndo(View view) {

        TurnData mTurnData = TurnData.fromGoogle(persistantData);

        if ((mTurnData.move == -1) || (mTurnData.gameOver)) {return;}
        mTurnData.cellState[mTurnData.moveTracker[mTurnData.move].spot] = ' ';
        imageViewArray[mTurnData.moveTracker[mTurnData.move].spot].setImageResource(R.drawable.blank);

        Log.i("onUndo", "spot = " + mTurnData.moveTracker[mTurnData.move].spot);
        Log.i("onUndo", "imageViewArray[" + mTurnData.moveTracker[mTurnData.move].spot +  "] = " + imageViewArray[mTurnData.moveTracker[mTurnData.move].spot]);

        mTurnData.move -= 1;
        mTurnData.xTurn = !mTurnData.xTurn;
        Log.i("onUndo", "xTurn = " + mTurnData.xTurn);
        if (mTurnData.xTurn) {
            message.setText("X's Turn");
        } else {
            message.setText("O's Turn");
        }
        //turn redo button graphics on
        redoGraphic.setImageResource(R.drawable.redo);
        redoText.setText("redo");

        if (mTurnData.move == -1) {
            //turn undo button graphics off
            undoGraphic.setImageResource(R.drawable.blank);
            undoText.setText("");
        }

        persistantData = mTurnData.toGoogle();
    }

    public void onRedo(View view) {

        TurnData mTurnData = TurnData.fromGoogle(persistantData);

        if (mTurnData.moveTracker[mTurnData.move + 1].spot == -1) {return;}
        mTurnData.move += 1;

        mTurnData.cellState[mTurnData.moveTracker[mTurnData.move].spot] = mTurnData.moveTracker[mTurnData.move].xTurn ? 'X' : 'O';


        if (mTurnData.moveTracker[mTurnData.move].xTurn) {
            imageViewArray[mTurnData.moveTracker[mTurnData.move].spot].setImageResource(R.drawable.x);
        } else {
            imageViewArray[mTurnData.moveTracker[mTurnData.move].spot].setImageResource(R.drawable.o);
        }

        Log.i("onRedo", "spot = " + mTurnData.moveTracker[mTurnData.move].spot);
        Log.i("onRedo", "imageViewArray[" + mTurnData.moveTracker[mTurnData.move].spot +  "] = " + imageViewArray[mTurnData.moveTracker[mTurnData.move].spot]);


        mTurnData.xTurn = !mTurnData.xTurn;
        Log.i("onRedo", "xTurn = " + mTurnData.xTurn);
        if (mTurnData.xTurn) {
            message.setText("X's Turn");
        } else {
            message.setText("O's Turn");
        }

        //turn undo button graphics on
        undoGraphic.setImageResource(R.drawable.undo);
        undoText.setText("undo");

        if (mTurnData.moveTracker[mTurnData.move+1].spot ==-1) {
            //turn redo button graphics off
            redoGraphic.setImageResource(R.drawable.blank);
            redoText.setText("");
        }

        persistantData = mTurnData.toGoogle();
    }

    public Boolean checkBoard(TurnData mTurnData) {
        if ((mTurnData.cellState[0] == 'X') && (mTurnData.cellState[1] == 'X') && (mTurnData.cellState[2] == 'X'))
        {
            winningLine.setImageResource(R.drawable.row1);
            message.setText("X Wins!");
            return true;
        }
        if ((mTurnData.cellState[3] == 'X') && (mTurnData.cellState[4] == 'X') && (mTurnData.cellState[5] == 'X'))
        {
            winningLine.setImageResource(R.drawable.row2);
            message.setText("X Wins!");
            return true;
        }
        if ((mTurnData.cellState[6] == 'X') && (mTurnData.cellState[7] == 'X') && (mTurnData.cellState[8] == 'X'))
        {
            winningLine.setImageResource(R.drawable.row3);
            message.setText("X Wins!");
            return true;
        }
        if ((mTurnData.cellState[0] == 'X') && (mTurnData.cellState[3] == 'X') && (mTurnData.cellState[6] == 'X'))
        {
            winningLine.setImageResource(R.drawable.column1);
            message.setText("X Wins!");
            return true;
        }
        if ((mTurnData.cellState[1] == 'X') && (mTurnData.cellState[4] == 'X') && (mTurnData.cellState[7] == 'X'))
        {
            winningLine.setImageResource(R.drawable.column2);
            message.setText("X Wins!");
            return true;
        }
        if ((mTurnData.cellState[2] == 'X') && (mTurnData.cellState[5] == 'X') && (mTurnData.cellState[8] == 'X'))
        {
            winningLine.setImageResource(R.drawable.column3);
            message.setText("X Wins!");
            return true;
        }
        if ((mTurnData.cellState[0] == 'X') && (mTurnData.cellState[4] == 'X') && (mTurnData.cellState[8] == 'X'))
        {
            winningLine.setImageResource(R.drawable.diagonal1);
            message.setText("X Wins!");
            return true;
        }
        if ((mTurnData.cellState[6] == 'X') && (mTurnData.cellState[4] == 'X') && (mTurnData.cellState[2] == 'X'))
        {
            winningLine.setImageResource(R.drawable.diagonal2);
            message.setText("X Wins!");
            return true;
        }
        if ((mTurnData.cellState[0] == 'O') && (mTurnData.cellState[1] == 'O') && (mTurnData.cellState[2] == 'O'))
        {
            winningLine.setImageResource(R.drawable.row1);
            message.setText("O Wins!");
            return true;
        }
        if ((mTurnData.cellState[3] == 'O') && (mTurnData.cellState[4] == 'O') && (mTurnData.cellState[5] == 'O'))
        {
            winningLine.setImageResource(R.drawable.row2);
            message.setText("O Wins!");
            return true;
        }
        if ((mTurnData.cellState[6] == 'O') && (mTurnData.cellState[7] == 'O') && (mTurnData.cellState[8] == 'O'))
        {
            winningLine.setImageResource(R.drawable.row3);
            message.setText("O Wins!");
            return true;
        }
        if ((mTurnData.cellState[0] == 'O') && (mTurnData.cellState[3] == 'O') && (mTurnData.cellState[6] == 'O'))
        {
            winningLine.setImageResource(R.drawable.column1);
            message.setText("O Wins!");
            return true;
        }
        if ((mTurnData.cellState[1] == 'O') && (mTurnData.cellState[4] == 'O') && (mTurnData.cellState[7] == 'O'))
        {
            winningLine.setImageResource(R.drawable.column2);
            message.setText("O Wins!");
            return true;
        }
        if ((mTurnData.cellState[2] == 'O') && (mTurnData.cellState[5] == 'O') && (mTurnData.cellState[8] == 'O'))
        {
            winningLine.setImageResource(R.drawable.column3);
            message.setText("O Wins!");
            return true;
        }
        if ((mTurnData.cellState[0] == 'O') && (mTurnData.cellState[4] == 'O') && (mTurnData.cellState[8] == 'O'))
        {
            winningLine.setImageResource(R.drawable.diagonal1);
            message.setText("O Wins!");
            return true;
        }
        if ((mTurnData.cellState[6] == 'O') && (mTurnData.cellState[4] == 'O') && (mTurnData.cellState[2] == 'O'))
        {
            winningLine.setImageResource(R.drawable.diagonal2);
            message.setText("O Wins!");
            return true;
        }
        if (mTurnData.move == 8)
        {
            message.setText("Cat's Game");
            return true;
        }
        return false;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        //references to out images
        private Integer[] mThumbIds = { R.drawable.blank, R.drawable.blank, R.drawable.blank, R.drawable.blank, R.drawable.blank, R.drawable.blank,
                R.drawable.blank, R.drawable.blank, R.drawable.blank };

        public ImageAdapter(Context c)
        {
            mContext = c;
        }

        public int getCount()
        {
            return mThumbIds.length;
        }

        public Object getItem(int position)
        {
            return null;
        }

        public long getItemId(int position)
        {
            return 0L;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                //if it is not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(50, 50));
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setPadding(2, 2, 2, 2);
            } else {
                imageView = (ImageView)convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            imageViewArray[position]=imageView;
            return imageView;
        }
    }
}
