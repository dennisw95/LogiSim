package com.example.logisim;


import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.widget.ImageView;

class Grid {
    private int numberHorizontalPixels, numberVerticalPixels, blockSize;
    private final int gridWidth = 40;
    private int gridHeight;
    final float interfaceWidth;
    final float halfOfInterfaceWidth;
    final int numberOfDividers;


    Grid(Point size){
        numberHorizontalPixels = size.x;
        numberVerticalPixels = size.y;
        blockSize = numberHorizontalPixels/gridWidth;
        gridHeight = numberVerticalPixels/blockSize;
        interfaceWidth = 10*blockSize;
        halfOfInterfaceWidth = interfaceWidth/2;
        numberOfDividers = blockSize * 5;
    }


    public void draw(Paint paint, Canvas myCanvas){


        myCanvas.drawColor(Color.argb(255,255,255,255));
        //draws 2 horizontal lines to encapsulate the icons
        myCanvas.drawLine(interfaceWidth,0,interfaceWidth,numberHorizontalPixels,paint);
        myCanvas.drawLine(halfOfInterfaceWidth,0,halfOfInterfaceWidth,numberHorizontalPixels,paint);

        //a vertical divider to encapsulate the icons
        for (int i=0; i<4;i++){

            myCanvas.drawLine(0, numberOfDividers *i,interfaceWidth, numberOfDividers *i,paint);
        }



    }

    void createIcons(Paint paint, Canvas myCanvas){
        paint.setTextSize(blockSize-3);
        myCanvas.drawText("Play/Pause",10,blockSize*3,paint);
        myCanvas.drawText("Edit",50,blockSize*8,paint);
        myCanvas.drawText("Wire",50,blockSize*13,paint);
        myCanvas.drawText("LED",50,blockSize*18,paint);

        paint.setColor(Color.argb(255,0,0,255));
        myCanvas.drawText("AND",350,blockSize*3,paint);
        paint.setColor(Color.argb(255,0,255,0));
        myCanvas.drawText("OR",350,blockSize*8,paint);
        paint.setColor(Color.argb(255,255,0,0));
        myCanvas.drawText("NOT",350,blockSize*13,paint);
        paint.setColor(Color.argb(255,128,0,255));
        myCanvas.drawText("Switch",350,blockSize*18,paint);

    }


    int getHeight() {return gridHeight;}
    int getWidth() {return gridWidth;}
    int getBlockSize() {return blockSize;}
    int getNumberHorizontalPixels() {return numberHorizontalPixels;}
    int getNumberVerticalPixels() {return numberVerticalPixels;}
    float getHalfOfInterfaceWidth() {return halfOfInterfaceWidth;}
    int getNumberOfDividers(){return numberOfDividers;}
    float getInterfaceWidth(){return interfaceWidth;}

}
class Touch{
    static float horizontalTouched = -100;
    static float verticalTouched = -100;

    void draw(Canvas canvas, Grid grid, Paint paint) {
        // Draw the player's shot
        canvas.drawRect(horizontalTouched * grid.getBlockSize(),
                verticalTouched * grid.getBlockSize(),
                (horizontalTouched * grid.getBlockSize()) + grid.getBlockSize(),
                (verticalTouched * grid.getBlockSize()) + grid.getBlockSize(),
                paint);
    }

}
class Distance{


    void draw(Paint paint, Grid grid,Canvas canvas, int distanceFromSub){
        // Re-size the text appropriate for the
        // score and distance text
        paint.setTextSize(grid.getBlockSize() * 2);
        paint.setColor(Color.argb(255, 0, 0, 255));

    }
    /*
    void assignDistance(int[] touchDistanceArray, int[][] gapArray,ArrayList<Sub> mySubs) {
        int i;
        for(i = 0; i < mySubs.size(); i++){
            touchDistanceArray[i] = (int)Math.sqrt(
                    ((gapArray[i][0] * gapArray[i][0]) +
                            (gapArray[i][1] * gapArray[i][1])));
        }
    }

     */
}

public class LogiSim extends Activity {

    boolean hit = false;
    int subsHit;
    int distanceFromSub;
    boolean debugging = false;
    Grid grid;

    // Here are all the objects(instances)
    // of classes that we need to do some drawing
    ImageView gameView;
    Bitmap blankBitmap;
    Canvas canvas;
    Paint paint,paintTemp;
    Touch touch;
    Distance distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the current device's screen resolution
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        grid = new Grid(size);


        // Initialize all the objects ready for drawing
        blankBitmap = Bitmap.createBitmap(grid.getNumberHorizontalPixels(),
                grid.getNumberVerticalPixels(),
                Bitmap.Config.ARGB_8888);

        canvas = new Canvas(blankBitmap);
        gameView = new ImageView(this);
        paint = new Paint();
        touch = new Touch();
        distance = new Distance();
        paintTemp = new Paint();


        setContentView(gameView);

        Log.d("Debugging", "In onCreate");
        newGame();
        draw();
    }

    void newGame(){


        Log.d("Debugging", "In newGame");

    }

    void draw() {
        gameView.setImageBitmap(blankBitmap);

        // Change the paint color to black
        paint.setColor(Color.argb(255, 0, 0, 0));

        // draw grid
        grid.draw(paint,canvas);

        // draw players shot
        touch.draw(canvas,grid,paint);
        grid.createIcons(paint,canvas);


        distance.draw(paint,grid,canvas,distanceFromSub);



        Log.d("Debugging", "In draw");
        /*
        if (debugging) {
            printDebuggingText();
        }

         */
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        Log.d("Debugging", "In onTouchEvent");
        // Has the player removed their finger from the screen?
        if((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {

            // Process the player's shot by passing the
            // coordinates of the player's finger to placeComponent
            placeComponent(motionEvent.getX(), motionEvent.getY());
        }

        return true;
    }

    void placeComponent(float touchX, float touchY){
        Log.d("Debugging", "In placeComponent");

        // Convert the float screen coordinates
        // into int grid coordinates
        Touch.horizontalTouched = (int)touchX/ grid.getBlockSize();
        Touch.verticalTouched = (int)touchY/ grid.getBlockSize();

        paintTemp = whatWasTouched(Touch.horizontalTouched, Touch.verticalTouched);
        touch.draw(canvas,grid,paintTemp);



        /*
        // checks if any of our subs got hit
        int i;
        for(i = 0;i<mySubs.size();i++) {
            hit = mySubs.get(i).isHit(touch);
            if(hit){
                mySubs.get(i).hit = true;
                subsHit++;
                break;
            }
        }

         */

        /*
        // How far away horizontally and vertically
        // was the shot from each of the subs
        // we will use this to calculate the distance of our touch to the closest sub
        // rows will correspond to how many gaps, while col[0]==horizontalGap && col[1]==verticalGap
        int[][] gapArray = new int[mySubs.size()][2];
        assignGaps(gapArray);


         */

        /*
        // For every touch on the screen, we need to know how far away we are from each sub
        // at that given moment
        int[] touchDistanceArray = new int[mySubs.size()];
        distance.assignDistance(touchDistanceArray,gapArray,mySubs);
        Arrays.sort(touchDistanceArray);

        // after sorting, the shortest distance will be in the first index
        distanceFromSub = touchDistanceArray[0];

        // If there is a hit call boom
        if(subsHit == 3)
            boom();
            // Otherwise call draw as usual
        else draw();

         */
        draw();
    }

    private Paint whatWasTouched(float horizontalTouched, float verticalTouched) {
        if(horizontalTouched >= grid.getHalfOfInterfaceWidth() && horizontalTouched <= grid.getInterfaceWidth()){ //right column
            if(verticalTouched <= grid.getNumberOfDividers()/5.0){ //first option
                paint.setColor(Color.argb(255,0,0,255));
                return paint;
            }
        }
        paint.setColor(Color.argb(255,0,0,255));
        return paint;
    }

    /*
    // This code prints the debugging text
    public void printDebuggingText(){
        paint.setTextSize(grid.getBlockSize());
        canvas.drawText("numberHorizontalPixels = "
                        + grid.getNumberHorizontalPixels(),
                50, grid.getBlockSize() * 3, paint);
        canvas.drawText("numberVerticalPixels = "
                        + grid.getNumberVerticalPixels(),
                50, grid.getBlockSize() * 4, paint);
        canvas.drawText("blockSize = " + grid.getBlockSize(),
                50, grid.getBlockSize() * 5, paint);
        canvas.drawText("gridWidth = " + grid.getWidth(),
                50, grid.getBlockSize() * 6, paint);
        canvas.drawText("gridHeight = " + grid.getHeight(),
                50, grid.getBlockSize() * 7, paint);
        canvas.drawText("horizontalTouched = " +
                        touch.horizontalTouched, 50,
                grid.getBlockSize() * 8, paint);
        canvas.drawText("verticalTouched = " +
                        touch.verticalTouched, 50,
                grid.getBlockSize() * 9, paint);
        canvas.drawText("sub1HorizontalPosition = " +
                        mySubs.get(0).subHorizontalPosition, 50,
                grid.getBlockSize() * 10, paint);
        canvas.drawText("sub1VerticalPosition = " +
                        mySubs.get(0).subVerticalPosition, 50,
                grid.getBlockSize() * 11, paint);
        canvas.drawText("hit = " + hit,
                50, grid.getBlockSize() * 12, paint);
        canvas.drawText("subsHit = " +
                        subsHit,
                50, grid.getBlockSize() * 13, paint);
        canvas.drawText("debugging = " + debugging,
                50, grid.getBlockSize() * 14, paint);
        canvas.drawText("sub2HorizontalPosition = " +
                        mySubs.get(1).subHorizontalPosition, 50,
                grid.getBlockSize() * 15, paint);
        canvas.drawText("sub2VerticalPosition = " +
                        mySubs.get(1).subVerticalPosition, 50,
                grid.getBlockSize() * 16, paint);
        canvas.drawText("sub3HorizontalPosition = " +
                        mySubs.get(2).subHorizontalPosition, 50,
                grid.getBlockSize() * 17, paint);
        canvas.drawText("sub3VerticalPosition = " +
                        mySubs.get(2).subVerticalPosition, 50,
                grid.getBlockSize() * 18, paint);


    }

     */

}
