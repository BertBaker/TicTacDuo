package com.gmail.bertdbaker.tictacduo;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Random;

public class PlayPhoneN extends AppCompatActivity {

    Boolean gameOver = false;
    Boolean xTurn = true;  //X always goes first
    Boolean phoneFirst;    //when human is first, then human is X, this variable set in onCreate from the intent's bundle
    boolean flagHumanTookCenter = false;
    Boolean phonePlaysRandomly;

    int initialProgress;
    int k1;
    int k2;
    int k3;
    int k4;
    int move = -1;  //which move just happened: going from 0 to 8 (nine possible moves), negative 1 means no move yet
    int q = 1;

    int[] basicNumbering = { 1, 2, 3, 8, 0, 4, 7, 6, 5 };
    int[] javaNumbering = { 4, 0, 1, 2, 5, 8, 7, 6, 3 };

    Random r = new Random();

    char[] cellState = {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};  //options are X, O, and space

    RadioButton playPhoneButton;

    TextView message;
    TextView difficultyMessage;

    ImageView[] viewFinder = new ImageView[9];
    ImageView winningLine;

    SeekBar seekBar;

    GridView gridview;

    moveTrack[] moveTracker = new moveTrack[9];  //keeps track of what spot is taken in each move and who took it
    class moveTrack {
        int spot = -1;
        ImageView v;
        boolean xTurn;
    }

    AlertDialog.Builder alert;
    public void displayAlert(View paramView) {alert.show();}

    @Override
    protected void onCreate(Bundle savedInsanceState) {
        super.onCreate(savedInsanceState);
        setContentView(R.layout.activity_play_phone_n);
        Intent intent = getIntent();

        Bundle bundle = getIntent().getExtras();

        phoneFirst = bundle.getBoolean("PHONEFIRST");
        initialProgress = bundle.getInt("PROGRESS");

        phonePlaysRandomly = ((int) (Math.pow(initialProgress, 2.0D) / 100.0D) < r.nextInt(100));

        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        playPhoneButton = ((RadioButton) findViewById(R.id.play_phone_button));
        playPhoneButton.setChecked(true);

        message = ((TextView) findViewById(R.id.message));
        difficultyMessage = ((TextView) findViewById(R.id.difficulty_message));

        winningLine = ((ImageView) findViewById(R.id.winning_line));

        int i = 0;
        while (i < moveTracker.length) {
            moveTracker[i] = new moveTrack();
            i += 1;
        }

        seekBar = ((SeekBar) findViewById(R.id.seek_bar));
        difficultyMessage.setText("Difficulty: " + initialProgress + "%");
        seekBar.setProgress(initialProgress);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                difficultyMessage.setText("Difficulty: " + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        GridView gridview = ((GridView) findViewById(R.id.gridview));
        gridview.setAdapter(new ImageAdapter(this));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageView imageView = (ImageView) v;

                // ignore click:  if position taken, if the phone is x and it is x's turn, if the phone is o and it is o's turn, or if the game is over
                if ((cellState[position] != ' ') || ((phoneFirst) && (xTurn)) || ((!phoneFirst) && (!xTurn)) || (gameOver)) {
                    return;
                }

                if ((!phoneFirst) && (xTurn)) {
                    imageView.setImageResource(R.drawable.x);
                    cellState[position] = 'X';
                } else {
                    imageView.setImageResource(R.drawable.o);
                    cellState[position] = 'O';
                }
                move += 1;

                moveTracker[move].spot = position;
                moveTracker[move].xTurn = xTurn;
                moveTracker[move].v = imageView;

                flagHumanTookCenter = (flagHumanTookCenter || (position == 4));

                gameOver = checkBoard();

                if (!gameOver) {
                    xTurn = !xTurn;
                    Log.i("GridView", "xTurn = " + xTurn);
                    message.setText("My Turn");
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            phoneTurn();
                        }
                    }, 700L);
                }
            }
        });
    }

    public void playPhone(View paramView) {
        // if playPhone button is clicked then do nothing
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

    public void startOver(View view) {

        Bundle bundle = new Bundle();
        bundle.putInt("PROGRESS", initialProgress);

        final Intent localIntent = new Intent(this, PlayPhone1.class);
        localIntent.putExtras(bundle);

        if (gameOver.booleanValue())
        {
            startActivity(localIntent);
            overridePendingTransition(0, 0);
            finish();
            return;
        }
        alert = new AlertDialog.Builder(this);
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
                startActivity(localIntent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        alert.setNegativeButton("NO", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
        });

        alert.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            public void onCancel(DialogInterface paramAnonymousDialogInterface) {}
        });

        alert.setMessage("Do you want to start over?");
        alert.show();
    }

    public void playByBluetooth(View view) {
        RadioButton playByBluetoothButton = (RadioButton)findViewById(R.id.play_by_bluetooth_button);
        final Intent localIntent = new Intent(this, PlayByBluetooth.class);
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
        playByBluetoothButton.setChecked(false);
        alert.setMessage("Do you want to start over, and play by bluetooth?");
        alert.show();
    }

    @SuppressLint({"LongLogTag"})
    public void phoneTurn()
    {
        move += 1;
        Log.i("PhoneTurn starting", "cellState=" + cellState[0] + cellState[1] + cellState[2] + cellState[3] + cellState[4] + cellState[5] + cellState[6] + cellState[7] + cellState[8] + ".");
        int j;

        String availableCells;

        int i;
        if (!phonePlaysRandomly)
        {
            int k = thirdInARow();
            j = k;
            if (k == -1) {
                switch (move)
                {
                    case 0: j = takeACorner(); break;
                    case 1: j = move1();break;
                    case 2: j = move2(); break;
                    case 3: j = move3(); break;
                    case 4: j = move4(); break;
                    case 5:case 6:case 7:case 8: j = moves5to8(); break;
                    default:
                        Log.e("phoneTurn ERROR", "move = " + move);
                        throw new RuntimeException("move out of bounds");
                }
            }

        } else {
            availableCells = "";
            j = 0;
            while (j < cellState.length) {
                if (cellState[j] == ' ') {availableCells = availableCells + j;}
                j += 1;
            }
            Log.i("phoneTurn", "availableCells=" + availableCells);
            j = Character.getNumericValue((availableCells).charAt(r.nextInt((availableCells).length())));
        }
        if (xTurn) {
            viewFinder[j].setImageResource(R.drawable.x);
        } else {
            viewFinder[j].setImageResource(R.drawable.o);
        }

        moveTracker[move].spot = j;
        moveTracker[move].xTurn = xTurn;
        moveTracker[move].v = viewFinder[j];

        cellState[j] = xTurn ? 'X' : 'O';

        gameOver = checkBoard();

        if (!gameOver) {
            xTurn = !xTurn;
            Log.i("phoneTurn", "xTurn = " + xTurn);
            message.setText("Your Turn");
            }

        return;
    }

    public int thirdInARow()
    {
        Log.i("thirdInARow starting", "cellState=" + cellState[0] + cellState[1] + cellState[2] + cellState[3] + cellState[4] + cellState[5] + cellState[6] + cellState[7] + cellState[8] + ".");
        int i = 1;
        while (i != -3)
        {
            int m = 0;
            int k = 0;
            int j = 1;
            while (j < 4)
            {
                int i3 = 0;
                int i4 = 0;
                int i1 = m + b(j, j);
                Log.i("thirdInARow", "w2=" + i1);
                int i2 = k + b(j - (j - 2) * 2, j);
                int n = 1;
                k = i4;
                m = i3;
                while (n < 4)
                {
                    m += b(j, n);
                    k += b(n, j);
                    n += 1;
                }
                if ((m == i * 2) || (k == i * 2))
                {
                    n = 1;
                    while (n < 4)
                    {
                        if ((b(j, n) != 0) || (m != i * 2))
                        {
                            if ((b(n, j) == 0) && (k == i * 2)) {
                                return n * 3 + j - 4;
                            }
                        }
                        else {
                            return j * 3 + n - 4;
                        }
                        n += 1;
                    }
                }
                j += 1;
                m = i1;
                k = i2;
            }
            if ((m == i * 2) || (k == i * 2))
            {
                j = 1;
                while (j < 4)
                {
                    if ((b(j, j) == 0) && (m == i * 2)) {
                        return j * 3 + j - 4;
                    }
                    if ((b(j - (j - 2) * 2, j) == 0) && (k == i * 2)) {
                        return (j - (j - 2) * 2) * 3 + j - 4;
                    }
                    j += 1;
                }
            }
            i -= 2;
        }
        return -1;
    }

   public int move1()
    {
        if (flagHumanTookCenter) {
            return takeACorner();
        }
        if ((moveTracker[0].spot == 0) || (moveTracker[0].spot == 2) || (moveTracker[0].spot == 6) || (moveTracker[0].spot == 8)) {
            return 4;
        }
        q = (r.nextInt(2) * 2 - 1);
        switch (moveTracker[0].spot) {
            case 1:  return q + 1;
            case 3:  return 3 - q * 3;
            case 5:  return q * 3 + 5;
            case 7:  return 7 - q;
            default: return -1;
        }
    }

    public int move2()
    {
        int f = basicNumbering[moveTracker[0].spot];
        k1 = (f + 2 + r.nextInt(2) * 4);
        k2 = (f + 2);
        k3 = (f + 4);
        k4 = fnr(f - 2);
        int l = basicNumbering[moveTracker[1].spot];
        int x = boolToInt(l == 0)*k3;
        x = Math.max(boolToInt((l < f + 4) && ((l > f) || (f == 7)))*k4, x);
        x = Math.max(boolToInt((l > f - 4) && (((f == 1) && (l > 5)) || (l < f)))*k2, x);
        x = fnr(Math.max(boolToInt(l == fnr(f + 4))*k1, x));
        return this.javaNumbering[x];
    }

    public int move3()
    {   //730  IF F1 THEN 760
        //740  	X=INT(RND(0)*4)*2+1
        //750  	GOTO NOT B[FNF(X),FNS(X)]+1 OF 740,640 (repeat until find an empty corner, then go to 640 to take this spot)
        if (flagHumanTookCenter) {
            return takeACorner();
        }

        //760  	X=
        //          F[1]+
        //              3*Q*(
        //                  F[1]=FNR(F[3]+Q)
        //                  OR F[3]=1 AND F[1]=8
        //                  OR F[1]=FNR(F[3]-Q*6)
        //                  )
        //770  	IF X THEN 860 (if x is positive then go to 860, to check that spot is available, if not available go to 810 (moves5to8), if available then go to 640 to take the spot)
        //780  	IF B[2,2] THEN 810 (if I took the center in move 1, then go to 810 (moves5to8) ((but I will never take the center in move 1, so ignore these lines))
        //790  	B[2,2]=1 (take the center)
        //800  	GOTO 910 (print the board)

        int x = (basicNumbering[moveTracker[0].spot]+3*q*
                boolToInt(basicNumbering[moveTracker[0].spot] == fnr(basicNumbering[moveTracker[2].spot]+q)
                    || (basicNumbering[moveTracker[2].spot]==1 && basicNumbering[moveTracker[0].spot]==8)
                    || (basicNumbering[moveTracker[0].spot] == fnr(basicNumbering[moveTracker[2].spot]-q*6))));

        if (x>0){
            Log.i("move3", "x=" + x);
            if (cellState[javaNumbering[x]] == ' ') {
                return javaNumbering[x];
                }
            }
        return moves5to8();
    }

    public int move4()
    {
        int f = basicNumbering[moveTracker[0].spot];
        int l = basicNumbering[moveTracker[1].spot];

        //880  	K1=(K1=F+2)*(F+6) MAX (K1=F+6)*(F+2)
        if (k1 == f+2){k1=f+6;} else {k1=f+2;}
        //890  	K2=K4=F+4
        k2 = f+4;
        k4 = k2;

        //900  	GOTO 710
        //710  	X=FNR((L=FNR(F+4))*K1 MAX (L>F-4)*(F=1 AND L>5 OR L<F)*K2 MAX (L<F+4)*(L>F OR F=7)*K4 MAX (L=0)*K3)
        int x = boolToInt(l == 0)*k3;
        x = Math.max(boolToInt((l < f + 4) && ((l > f) || (f == 7)))*k4, x);
        x = Math.max(boolToInt((l > f - 4) && (((f == 1) && (l > 5)) || (l < f)))*k2, x);
        x = fnr(Math.max(boolToInt(l == fnr(f + 4))*k1, x));

        //720  	GOTO 640
        return this.javaNumbering[x];
    }

    public int moves5to8()
    {
        int x;
        int j = 0;

        int[] h = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

        do {
            do {
                //810  	X=INT(4*RND(0)+1)*2-
                //          (M=6 AND (
                //              F[1]=FNR(F[3]+Q)
                //              OR F[3]=1 AND F[1]=8
                //              OR F[1]=FNR(F[3]-Q*6)
                //              )
                //          )
                x = (r.nextInt(4) + 1) * 2 - boolToInt(
                    (move == 5) && (
                        (basicNumbering[moveTracker[0].spot] == fnr(basicNumbering[moveTracker[2].spot] + q))
                        || ((basicNumbering[this.moveTracker[2].spot] == 1) && (this.basicNumbering[this.moveTracker[0].spot] == 8))
                        || (basicNumbering[this.moveTracker[0].spot] == fnr(this.basicNumbering[this.moveTracker[2].spot] - q * 6))
                        )
                );
                //840  	IF H[X] THEN 810
            } while (h[x] != 0);  // find an h[x] which has not been taken
            //850  	J=H[X]=J+1
            h[x] = (j + 1);
            j += 1;
            if (j == 4) {
                if (cellState[4] == ' ') {
                    //820  	IF J=4 AND B[2,2]=0 THEN 790
                        //790  	B[2,2]=1
                        //800  	GOTO 910
                    return 4;
                } else {
                    //830  	IF J=4 THEN 580
                    return takeACorner();}
            }
            //860  	IF B[FNF(X),FNS(X)] THEN 810
        } while (cellState[javaNumbering[x]] != ' ');
        //870  	GOTO 640
            //640  	B[FNF(X),FNS(X)]=1
            //650  	GOTO 910
        return javaNumbering[x];
    }

    public int takeACorner()
    {
        String availableCorners = "";
        Log.i("takeACorner starting", "cellState=" + cellState[0] + cellState[1] + cellState[2] + cellState[3] + cellState[4] + cellState[5] + cellState[6] + cellState[7] + cellState[8] + ".");
        int i = 0;
        while (i < cellState.length)
        {
            if ((cellState[i] == ' ') && (i != 4)) {availableCorners = availableCorners + i;}
            i += 2;
        }
        Log.i("takeACorner", "availableCorners=" + availableCorners);
        return Character.getNumericValue((availableCorners).charAt(r.nextInt((availableCorners).length())));
    }

    public Boolean checkBoard()
    {
        if ((cellState[0] == 'X') && (cellState[1] == 'X') && (cellState[2] == 'X')) {
            winningLine.setImageResource(R.drawable.row1);
            if (phoneFirst) {message.setText("I Win!");} else {message.setText("You Win!");}
            return true;
        }
        if ((cellState[3] == 'X') && (cellState[4] == 'X') && (cellState[5] == 'X')) {
            winningLine.setImageResource(R.drawable.row2);
            if (phoneFirst) {message.setText("I Win!");} else {message.setText("You Win!");}
            return true;
        }
        if ((cellState[6] == 'X') && (cellState[7] == 'X') && (cellState[8] == 'X')) {
            winningLine.setImageResource(R.drawable.row3);
            if (phoneFirst) {message.setText("I Win!");} else {message.setText("You Win!");}
            return true;
        }
        if ((cellState[0] == 'X') && (cellState[3] == 'X') && (cellState[6] == 'X')) {
            winningLine.setImageResource(R.drawable.column1);
            if (phoneFirst) {message.setText("I Win!");} else {message.setText("You Win!");}
            return true;
        }
        if ((cellState[1] == 'X') && (cellState[4] == 'X') && (cellState[7] == 'X')) {
            winningLine.setImageResource(R.drawable.column2);
            if (phoneFirst) {message.setText("I Win!");} else {message.setText("You Win!");}
            return true;
        }
        if ((cellState[2] == 'X') && (cellState[5] == 'X') && (cellState[8] == 'X')) {
            winningLine.setImageResource(R.drawable.column3);
            if (phoneFirst) {message.setText("I Win!");} else {message.setText("You Win!");}
            return true;
        }
        if ((cellState[0] == 'X') && (cellState[4] == 'X') && (cellState[8] == 'X')) {
            winningLine.setImageResource(R.drawable.diagonal1);
            if (phoneFirst) {message.setText("I Win!");} else {message.setText("You Win!");}
            return true;
        }
        if ((cellState[6] == 'X') && (cellState[4] == 'X') && (cellState[2] == 'X')) {
            winningLine.setImageResource(R.drawable.diagonal2);
            if (phoneFirst) {message.setText("I Win!");} else {message.setText("You Win!");}
            return true;
        }
        if ((cellState[0] == 'O') && (cellState[1] == 'O') && (cellState[2] == 'O')) {
            winningLine.setImageResource(R.drawable.row1);
            if (phoneFirst) {message.setText("You Win!");} else {message.setText("I Win!");}
            return true;
        }
        if ((cellState[3] == 'O') && (cellState[4] == 'O') && (cellState[5] == 'O')) {
            winningLine.setImageResource(R.drawable.row2);
            if (phoneFirst) {message.setText("You Win!");} else {message.setText("I Win!");}
            return true;
        }
        if ((cellState[6] == 'O') && (cellState[7] == 'O') && (cellState[8] == 'O')) {
            winningLine.setImageResource(R.drawable.row3);
            if (phoneFirst) {message.setText("You Win!");} else {message.setText("I Win!");}
            return true;
        }
        if ((cellState[0] == 'O') && (cellState[3] == 'O') && (cellState[6] == 'O')) {
            winningLine.setImageResource(R.drawable.column1);
            if (phoneFirst) {message.setText("You Win!");} else {message.setText("I Win!");}
            return true;
        }
        if ((cellState[1] == 'O') && (cellState[4] == 'O') && (cellState[7] == 'O')) {
            winningLine.setImageResource(R.drawable.column2);
            if (phoneFirst) {message.setText("You Win!");} else {message.setText("I Win!");}
            return true;
        }
        if ((cellState[2] == 'O') && (cellState[5] == 'O') && (cellState[8] == 'O')) {
            winningLine.setImageResource(R.drawable.column3);
            if (phoneFirst) {message.setText("You Win!");} else {message.setText("I Win!");
            }
            return true;
        }
        if ((cellState[0] == 'O') && (cellState[4] == 'O') && (cellState[8] == 'O')) {
            winningLine.setImageResource(R.drawable.diagonal1);
            if (phoneFirst) {message.setText("You Win!");} else {message.setText("I Win!");}
            return true;
        }
        if ((cellState[6] == 'O') && (cellState[4] == 'O') && (cellState[2] == 'O')) {
            winningLine.setImageResource(R.drawable.diagonal2);
            if (phoneFirst) {message.setText("You Win!");} else {message.setText("I Win!");}
            return true;
        }
        if (move == 8)
        {
            message.setText("Cat's Game");
            return true;
        }

        return false;
    }

    public int fnr(int x)
    {
        while (x>8) {x=x-8;}
        while (x<0) {x=x+8;}
        return x;
    }

    public int b(int x, int y)
    {
        Log.i("b", "3 * x + y - 4=" + (x * 3 + y - 4));
        Log.i("b", "cellState[3 * x + y - 4]=" + cellState[(x * 3 + y - 4)] + ".");
        switch (cellState[(x * 3 + y - 4)]) {
            default:
                Log.i("b", "returning zero");
                return 0;
            case ' ':
                Log.i("b", "blank");
                return 0;
            case 'O':
                Log.i("b", "oh");
                if (phoneFirst) {
                    return -1;
                }
                return 1;
            case 'X':
                Log.i("b", "ex");
                if (phoneFirst) {
                    return 1;
                }
                return -1;
        }
    }

    public int boolToInt(boolean paramBoolean)
    {
        if (paramBoolean) {
            return 1;
        }
        return 0;
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

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                //if it is not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(50, 50));
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setPadding(2, 2, 2, 2);

            } else {
                imageView = (ImageView) convertView;}

            if (viewFinder[position] == null) {
                viewFinder[position] = imageView;
                Log.i("getView", "position = " + position);
                // note this code below is totally wrong, this is not the place to start the game, but I can't figure out where else
                // to put this so I am assured the grid is created before starting
                if (position == 8) {
                    if (phoneFirst) {
                        Log.i("PhoneTurn starting", "cellState=" + cellState[0] + cellState[1] + cellState[2] + cellState[3] + cellState[4] + cellState[5] + cellState[6] + cellState[7] + cellState[8] + ".");
                        message.setText("My Turn");
                        phoneTurn();
                    } else {
                        message.setText("Your Turn");
                    }
                }
            }
            //imageView.setImageResource(mThumbIDs[position]);
            return imageView;
        }
    }

}

