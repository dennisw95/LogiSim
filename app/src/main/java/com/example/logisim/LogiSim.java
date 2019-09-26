package com.example.logisim;


import android.app.Activity;


import android.content.Context;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.view.MotionEvent;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
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
        paint.setColor(Color.argb(255,128,0,255));
        myCanvas.drawText("LED",50,blockSize*18,paint);

        paint.setColor(Color.argb(255,0,0,255));
        myCanvas.drawText("AND",350,blockSize*3,paint);
        paint.setColor(Color.argb(255,0,255,0));
        myCanvas.drawText("OR",350,blockSize*7,paint);
        paint.setColor(Color.argb(255,255,0,0));
        myCanvas.drawText("NOT",340,blockSize*(float)12.75,paint);
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
interface Node{
    boolean eval();
}
/*
    classes Switch, AND, OR, NOT, and LED come from
    Daryl Posnett's LogiSimEvaluationExample
 */
class Switch implements Node{
    boolean state;
    public Switch(boolean state){this.state = state;}
    public void toggle(){this.state = !this.state;}
    public boolean eval(){return state;}
}
class AND implements Node{
    Node a,b;

    public AND () {}
    public AND(Node a, Node b){
        this.setA(a);
        this.setB(b);
    }
    public void setA(Node n){
        this.a = n;
    }
    public void setB(Node n){
        this.b = n;
    }

    public boolean eval() {return a.eval() & b.eval();}


}
class OR implements Node{
    Node a,b;
    public OR () {}
    public OR(Node a, Node b){
        this.setA(a);
        this.setB(b);
    }
    public void setA(Node n){
        this.a = n;
    }
    public void setB(Node n){
        this.b = n;
    }

    public boolean eval() {return a.eval() | b.eval();}
}
class NOT implements Node{
    Node n;
    public NOT(){}
    public NOT(Node n){this.setSource(n);}
    public void setSource(Node n){this.n=n;}
    public boolean eval(){return !n.eval();}
}
class LED {}
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
class Icons extends View {

    public Icons(Context context) {
        super(context);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Bitmap _and = BitmapFactory.decodeResource(getResources(), R.drawable.andgatetrans);
        Bitmap _or = BitmapFactory.decodeResource(getResources(), R.drawable.orgate);
        Bitmap _not = BitmapFactory.decodeResource(getResources(), R.drawable.notgate);
        Bitmap _switch = BitmapFactory.decodeResource(getResources(), R.drawable.switchsymbol);

        //canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(_and,280 ,50, null);
        canvas.save();
        canvas.drawBitmap(_or,280 ,300, null);
        canvas.drawBitmap(_not,280 ,600, null);
        canvas.drawBitmap(_switch,280 ,900, null);
        canvas.drawBitmap(_switch,5 ,900, null);

    }
}


public class LogiSim extends Activity {

    String touchTemp = "-1";
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
    Icons drawIcons;
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
        drawIcons = new Icons(this);
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
        canvas.save();
        drawIcons.draw(canvas);

        // draw players shot
        touch.draw(canvas,grid,paint);
        grid.createIcons(paint,canvas);


        distance.draw(paint,grid,canvas,distanceFromSub);

        regionHit();
        touch.draw(canvas,grid,paintTemp);
        Log.d("Debugging", "In draw");

        if (debugging) {
            printDebuggingText();
        }


    }

    private void regionHit() {
        if(touchTemp.equals("AND")){
            paintTemp.setColor(Color.argb(255,0,0,255));
        }
        if(touchTemp.equals("OR")){
            paintTemp.setColor(Color.argb(255,0,255,0));
        }
        if(touchTemp.equals("NOT")){
            paintTemp.setColor(Color.argb(255,255,0,0));
        }
        if(touchTemp.equals("SWITCH")){
            paintTemp.setColor(Color.argb(255,128,0,255));
        }

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

        touchTemp = whatWasTouched(Touch.horizontalTouched, Touch.verticalTouched);

        draw();
    }

    private String whatWasTouched(float horizontalTouched, float verticalTouched) {
        if(horizontalTouched >= 5.0 && horizontalTouched <= 9.0){
            if(verticalTouched >= 0.0 && verticalTouched <=4.0){
                return "AND";
            }
        }
        if(horizontalTouched >= 5.0 && horizontalTouched <= 9.0){
            if(verticalTouched >= 5.0 && verticalTouched <=9.0){
                return "OR";
            }
        }
        if(horizontalTouched >= 5.0 && horizontalTouched <= 9.0){
            if(verticalTouched >= 10.0 && verticalTouched <=14.0){
                return "NOT";
            }
        }
        if(horizontalTouched >= 5.0 && horizontalTouched <= 9.0){
            if(verticalTouched >= 15.0 && verticalTouched <=19.0){
                return "SWITCH";
            }
        }
        if(horizontalTouched >= 0.0 && horizontalTouched <= 4.0){
            if(verticalTouched >= 0.0 && verticalTouched <=4.0){
                return "Play/Pause";
            }
        }
        if(horizontalTouched >= 0.0 && horizontalTouched <= 4.0){
            if(verticalTouched >= 5.0 && verticalTouched <=9.0){
                return "EDIT";
            }
        }
        if(horizontalTouched >= 0.0 && horizontalTouched <= 4.0){
            if(verticalTouched >= 10.0 && verticalTouched <=14.0){
                return "WIRE";
            }
        }
        if(horizontalTouched >= 0.0 && horizontalTouched <= 4.0){
            if(verticalTouched >= 15.0 && verticalTouched <=19.0){
                return "LED";
            }
        }

        return "-1";
    }


    // This code prints the debugging text
    public void printDebuggingText(){
        paint.setTextSize(grid.getBlockSize());
        canvas.drawText("numberHorizontalPixels = "
                        + grid.getNumberHorizontalPixels(),
                750, grid.getBlockSize() * 3, paint);
        canvas.drawText("numberVerticalPixels = "
                        + grid.getNumberVerticalPixels(),
                750, grid.getBlockSize() * 4, paint);
        canvas.drawText("blockSize = " + grid.getBlockSize(),
                750, grid.getBlockSize() * 5, paint);
        canvas.drawText("gridWidth = " + grid.getWidth(),
                750, grid.getBlockSize() * 6, paint);
        canvas.drawText("gridHeight = " + grid.getHeight(),
                750, grid.getBlockSize() * 7, paint);
        canvas.drawText("horizontalTouched = " +
                        touch.horizontalTouched, 750,
                grid.getBlockSize() * 8, paint);
        canvas.drawText("verticalTouched = " +
                        touch.verticalTouched, 750,
                grid.getBlockSize() * 9, paint);



    }



}
