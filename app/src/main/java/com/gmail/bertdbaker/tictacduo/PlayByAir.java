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

    //turn data sent to google
    Boolean gameOver = false;
    Boolean xTurn = true;

    char[] cellState = {' ', ' ',' ', ' ', ' ', ' ', ' ', ' ', ' '};  //options are X, O, and space

    int move = -1; //which move just happened:  going from 0 to 8 (nine possible moves), negative 1 means no move yet

    moveTrack[] moveTracker = new moveTrack[9];  //keeps track of what spot is taken in each move and who took it
    class moveTrack {
        int spot = -1;  //minus 1 means the spot is not taken
        ImageView v;
        boolean xTurn;
    }

    //turn data internal to this device
    RadioButton playByAirButton;

    TextView message;
    TextView redoText;
    TextView undoText;

    ImageView redoGraphic;
    ImageView undoGraphic;
    ImageView winningLine;


    AlertDialog.Builder alert;
    public void displayAlert(View paramView){alert.show();}

    @Override
    protected void onCreate(Bundle paramBundle)
    {
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

        int i = 0;
        while (i < moveTracker.length) {
            moveTracker[i] = new moveTrack();
            i += 1;}

        GridView gridview = (GridView)findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                ImageView imageView = (ImageView) v;

                if ((cellState[position] != ' ') || (gameOver)) {
                    return;
                }
                if (xTurn) {
                    imageView.setImageResource(R.drawable.x);
                    message.setText("O's Turn");
                } else {
                    imageView.setImageResource(R.drawable.o);
                    message.setText("X's Turn");
                }

                move++;

                moveTracker[move].spot = position;
                moveTracker[move].xTurn = xTurn;
                moveTracker[move].v = imageView;

                //null out the rest of the moveTracker now that we are moving forward
                int j = move + 1;
                while (j < 9) {
                    moveTracker[j].spot = -1;
                    j += 1;
                }

                cellState[position] = xTurn ? 'X' : 'O';

                gameOver = checkBoard();

                if (gameOver) {
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

                xTurn = !xTurn;
                Log.i("GridView", "xTurn = " + xTurn);
            }

        });
    }

    public void playPhone(View view)
    {
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

    public void startOver(final View view)
    {
        final Intent intent = new Intent(this, PlayByAir.class);
        if (gameOver)
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

    public void playByAir(View view)
    {//if playByAir button is clicked then do nothing
    }

    public void onUndo(View view)
    {
        if ((this.move == -1) || (gameOver)) {return;}
        cellState[moveTracker[move].spot] = ' ';
        moveTracker[move].v.setImageResource(R.drawable.blank);
        move -= 1;
        xTurn = !xTurn;
        Log.i("onUndo", "xTurn = " + xTurn);
        if (xTurn) {
            message.setText("X's Turn");
        } else {
            message.setText("O's Turn");
        }
        //turn redo button graphics on
        redoGraphic.setImageResource(R.drawable.redo);
        redoText.setText("redo");

        if (move == -1) {
            //turn undo button graphics off
            undoGraphic.setImageResource(R.drawable.blank);
            undoText.setText("");
        }
    }

    public void onRedo(View view)
    {
        if (moveTracker[move + 1].spot == -1) {return;}
        move += 1;

        cellState[moveTracker[move].spot] = moveTracker[move].xTurn ? 'X' : 'O';


        if (moveTracker[move].xTurn) {
            moveTracker[move].v.setImageResource(R.drawable.x);
        } else {
            moveTracker[move].v.setImageResource(R.drawable.o);
        }

        xTurn = !xTurn;
        Log.i("onRedo", "xTurn = " + xTurn);
        if (xTurn) {
            message.setText("X's Turn");
        } else {
            message.setText("O's Turn");
        }

        //turn undo button graphics on
        undoGraphic.setImageResource(R.drawable.undo);
        undoText.setText("undo");

        if (moveTracker[move+1].spot ==-1) {
            //turn redo button graphics off
            redoGraphic.setImageResource(R.drawable.undo);
            redoText.setText("");
        }
    }

    public Boolean checkBoard()
    {
        if ((cellState[0] == 'X') && (cellState[1] == 'X') && (cellState[2] == 'X'))
        {
            winningLine.setImageResource(R.drawable.row1);
            message.setText("X Wins!");
            return true;
        }
        if ((cellState[3] == 'X') && (cellState[4] == 'X') && (cellState[5] == 'X'))
        {
            winningLine.setImageResource(R.drawable.row2);
            message.setText("X Wins!");
            return true;
        }
        if ((cellState[6] == 'X') && (cellState[7] == 'X') && (cellState[8] == 'X'))
        {
            winningLine.setImageResource(R.drawable.row3);
            message.setText("X Wins!");
            return true;
        }
        if ((cellState[0] == 'X') && (cellState[3] == 'X') && (cellState[6] == 'X'))
        {
            winningLine.setImageResource(R.drawable.column1);
            message.setText("X Wins!");
            return true;
        }
        if ((cellState[1] == 'X') && (cellState[4] == 'X') && (cellState[7] == 'X'))
        {
            winningLine.setImageResource(R.drawable.column2);
            message.setText("X Wins!");
            return true;
        }
        if ((cellState[2] == 'X') && (cellState[5] == 'X') && (cellState[8] == 'X'))
        {
            winningLine.setImageResource(R.drawable.column3);
            message.setText("X Wins!");
            return true;
        }
        if ((cellState[0] == 'X') && (cellState[4] == 'X') && (cellState[8] == 'X'))
        {
            winningLine.setImageResource(R.drawable.diagonal1);
            message.setText("X Wins!");
            return true;
        }
        if ((cellState[6] == 'X') && (cellState[4] == 'X') && (cellState[2] == 'X'))
        {
            winningLine.setImageResource(R.drawable.diagonal2);
            message.setText("X Wins!");
            return true;
        }
        if ((cellState[0] == 'O') && (cellState[1] == 'O') && (cellState[2] == 'O'))
        {
            winningLine.setImageResource(R.drawable.row1);
            message.setText("O Wins!");
            return true;
        }
        if ((cellState[3] == 'O') && (cellState[4] == 'O') && (cellState[5] == 'O'))
        {
            winningLine.setImageResource(R.drawable.row2);
            message.setText("O Wins!");
            return true;
        }
        if ((cellState[6] == 'O') && (cellState[7] == 'O') && (cellState[8] == 'O'))
        {
            winningLine.setImageResource(R.drawable.row3);
            message.setText("O Wins!");
            return true;
        }
        if ((cellState[0] == 'O') && (cellState[3] == 'O') && (cellState[6] == 'O'))
        {
            winningLine.setImageResource(R.drawable.column1);
            message.setText("O Wins!");
            return true;
        }
        if ((cellState[1] == 'O') && (cellState[4] == 'O') && (cellState[7] == 'O'))
        {
            winningLine.setImageResource(R.drawable.column2);
            message.setText("O Wins!");
            return true;
        }
        if ((cellState[2] == 'O') && (cellState[5] == 'O') && (cellState[8] == 'O'))
        {
            winningLine.setImageResource(R.drawable.column3);
            message.setText("O Wins!");
            return true;
        }
        if ((cellState[0] == 'O') && (cellState[4] == 'O') && (cellState[8] == 'O'))
        {
            winningLine.setImageResource(R.drawable.diagonal1);
            message.setText("O Wins!");
            return true;
        }
        if ((cellState[6] == 'O') && (cellState[4] == 'O') && (cellState[2] == 'O'))
        {
            winningLine.setImageResource(R.drawable.diagonal2);
            message.setText("O Wins!");
            return true;
        }
        if (move == 8)
        {
            message.setText("Cat's Game");
            return true;
        }
        return false;
    }

    public class ImageAdapter extends BaseAdapter
    {
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
            return imageView;
        }
    }
}
