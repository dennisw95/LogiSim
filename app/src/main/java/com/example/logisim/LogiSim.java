package com.example.logisim;


import android.app.Activity;
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
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


/*
    classes Switch, AND, OR, NOT, and LED come from
    Daryl Posnett's LogiSimEvaluationExample
 */
interface Node{
    boolean eval();
}
// All the logical gates
class Switch implements Node{
    boolean state=false ;
    Position location;
    public Switch(){}
    public Switch( boolean state) { this . state = state; }
    public void toggle() { this . state = ! this . state ; }
    public boolean eval() { return state ; }
}
class AND implements Node{
    Node a,b;
    Position location;

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
    Position location;

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
    Position location;

    public NOT(){}
    public NOT(Node n){this.setSource(n);}
    public void setSource(Node n){this.n=n;}
    public boolean eval(){return !n.eval();}
}

class VisualComponents{
    int posX, posY;
    String component;
    public VisualComponents(float posX, float posY, String component){
        this.posX = (int) (posX * Grid.blockSize);
        this.posY = (int) (posY * Grid.blockSize);
        this.component = component;
    }
    public void drawComponent(Canvas canvas){
        switch (component){
            case "AND":
                canvas.drawBitmap(LogiSim._and,posX,posY,null);
                break;
            case "NOT":
                canvas.drawBitmap(LogiSim._not,posX,posY,null);
                break;
            case "OR":
                canvas.drawBitmap(LogiSim._or,posX,posY,null);
                break;
            case "SWITCH":
                canvas.drawBitmap(LogiSim._switch,posX,posY,null);
                break;
            case "LED":
                canvas.drawBitmap(LogiSim._switch,posX,posY,null);
            default:
                break;
        }
    }

}
class Touch{
    static float horizontalTouched = -100;
    static float verticalTouched = -100;
    static float secondHorizontalTouch = -100;
    static float secondVerticalTouch = -100;

    void draw(Canvas canvas, Grid grid, Paint paint) {
        // Draw the player's shot
        paint.setColor(Color.BLACK);
        canvas.drawRect(horizontalTouched * grid.getBlockSize(),
                verticalTouched * grid.getBlockSize(),
                (horizontalTouched * grid.getBlockSize()) + grid.getBlockSize(),
                (verticalTouched * grid.getBlockSize()) + grid.getBlockSize(),
                paint);
    }

}
class Position {
    float posX,posY;

    Position(float x, float y){
        posX = x;
        posY = y;
    }


    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }
}
class Grid {
    private int numberHorizontalPixels, numberVerticalPixels;
    static int blockSize;
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






    int getHeight() {return gridHeight;}
    int getWidth() {return gridWidth;}
    int getBlockSize() {return blockSize;}
    int getNumberHorizontalPixels() {return numberHorizontalPixels;}
    int getNumberVerticalPixels() {return numberVerticalPixels;}
    float getHalfOfInterfaceWidth() {return halfOfInterfaceWidth;}
    int getNumberOfDividers(){return numberOfDividers;}
    float getInterfaceWidth(){return interfaceWidth;}

}

public class LogiSim extends Activity {

    String whatWasTouched = "-1";
    boolean debugging = false;


    // Here are all the objects(instances)
    // of classes that we need to do some drawing
    Grid grid;
    ImageView gameView;
    Bitmap blankBitmap;
    Canvas canvas;
    Paint paint,paint2;
    Touch touch;
    static Bitmap _and,_or,_not,_switch;
    List<VisualComponents> componentsList;
    List<Node> logicComponentsList;

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
        paint2 = new Paint();
        touch = new Touch();
        componentsList = new ArrayList<>();
        logicComponentsList = new ArrayList<>();
        setContentView(gameView);


        getImagesReady();

        Log.d("Debugging", "In onCreate");

        draw();
    }

    public void getImagesReady() {
        _and = BitmapFactory.decodeResource(getResources(), R.drawable.andgatetrans);
        _or = BitmapFactory.decodeResource(getResources(), R.drawable.orgate);
        _not = BitmapFactory.decodeResource(getResources(), R.drawable.notgate);
        _switch = BitmapFactory.decodeResource(getResources(), R.drawable.switchsymbol);
    }




    void draw() {
        gameView.setImageBitmap(blankBitmap);

        // Change the paint color to black
        paint.setColor(Color.BLACK);

        // draw grid
        grid.draw(paint,canvas);


        // draw players touch
        touch.draw(canvas,grid,paint);

        //testing
        final int listSize = componentsList.size();
        for (int i=0;i<listSize;i++)
            componentsList.get(i).drawComponent(canvas);


        UIlayout();
        //drawCircuit();
        //dynamicXOR();
        //drawVisualComponents();
        Log.d("Debugging", "In draw");

        if (debugging) {
            printDebuggingText();
        }


    }



    private void drawCircuit() {

        canvas.drawBitmap(_not,1000 ,660, null);
        canvas.drawBitmap(_not,1000 ,330, null);
        canvas.drawBitmap(_and,1400 ,330, null);
        canvas.drawBitmap(_and,1400 ,660, null);
        canvas.drawBitmap(_or,1800 ,495, null);
        canvas.drawBitmap(_switch,600 ,330, null);
        canvas.drawBitmap(_switch,600 ,660, null);
        canvas.drawBitmap(_switch,1870 ,165, null);



        canvas.drawLine(835,400,1000,400,paint); //switch A to g1
        canvas.drawLine(835,745,1000,745,paint); //switch B to g2
        canvas.drawLine(1595,385,1760,510,paint); // g3 to g4
        canvas.drawLine(1210,715 ,1375 ,440,paint); //g2 to g3
        canvas.drawLine(1210,385 ,1375 ,660,paint); // g2 to g4
        canvas.drawLine(825,385 ,1375 ,330,paint); // switch A to g3
        canvas.drawLine(825,715 ,1375 ,770,paint); // switch B to g4
        canvas.drawLine(1595,715 ,1760 ,550,paint); // g4 to g5
        canvas.drawLine(2035,550 ,2035 ,275,paint); // g4 to g5



    }

    void drawWire(){
        canvas.drawLine(Touch.horizontalTouched,Touch.verticalTouched,Touch.secondHorizontalTouch,Touch.secondVerticalTouch,paint);
    }
    public void dynamicXOR(){
    // declare the objects
    //


        Switch A = new Switch();
        Switch B = new Switch();
        NOT g1 = new NOT();
        NOT g2 = new NOT();
        AND g3 = new AND();
        AND g4 = new AND();
        OR g5 = new OR();


    // wire the objects
    //

        g1.setSource(A);
        g2.setSource(B);
        g3.setA(A);
        g3.setB(g2);
        g4.setA(g1);
        g4.setB(B);
        g5.setA(g3);
        g5.setB(g4);

        B.toggle();
        paint.setTextSize(grid.getBlockSize()+3);
        paint.setColor(Color.BLUE);
        if(A.state){
            canvas.drawText("1",600+110,330+110,paint);
        }else{
            canvas.drawText("0",600+110,330+110,paint);
        }
        if(B.state){
            canvas.drawText("1",600+110,660+110,paint);
        }else{
            canvas.drawText("0",600+110,660+110,paint);
        }
        if(g5.eval()){
            canvas.drawText("1",1870+110,165+110,paint); // draw on the LED
        }else{
            canvas.drawText("0",1870+110,165+110,paint);
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        Log.d("Debugging", "In onTouchEvent");

        if((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            //placeComponent();
            Touch.horizontalTouched = (int)motionEvent.getX()/ grid.getBlockSize();
            Touch.verticalTouched = (int)motionEvent.getY()/ grid.getBlockSize();
            whatWasTouched = whatWasTouched(Touch.horizontalTouched, Touch.verticalTouched);

        }else if((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE){

        }
        else if((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP){
            Touch.secondHorizontalTouch = (int)motionEvent.getX()/ grid.getBlockSize();
            Touch.secondVerticalTouch = (int)motionEvent.getY()/ grid.getBlockSize();
            placeComponent();
            draw();
        }
        return true;
    }

    void placeComponent(){
        Log.d("Debugging", "In placeComponent");

        // Convert the float screen coordinates
        // into int grid coordinates
        //whatWasTouched = whatWasTouched(Touch.horizontalTouched, Touch.verticalTouched);
        if(whatWasTouched.equals("DELETE")){
            componentsList.clear();
            logicComponentsList.clear();
        }else {
            VisualComponents addThis = new VisualComponents(Touch.secondHorizontalTouch,Touch.secondVerticalTouch,whatWasTouched);
            componentsList.add(addThis);
            createLogicalComponent(whatWasTouched);
        }

}

    private void createLogicalComponent(String whatWasTouched) {
        switch(whatWasTouched){
            case "AND":
                logicComponentsList.add(new AND());
                break;
            case "OR":
                logicComponentsList.add(new OR());
                break;
            case "NOT":
                logicComponentsList.add(new NOT());
                break;
            case "SWITCH":
                logicComponentsList.add(new Switch(false));
                break;
        }
    }

    private void regionHit() {

        if(whatWasTouched.equals("AND")){
            canvas.drawBitmap(_and,Touch.horizontalTouched*Grid.blockSize,Touch.verticalTouched*Grid.blockSize,null);
        }
        if(whatWasTouched.equals("OR")){
        }
        if(whatWasTouched.equals("NOT")){
        }
        if(whatWasTouched.equals("SWITCH")){
        }

    }
    // used to tell regionHit() what to do
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
                return "Play";
            }
        }
        if(horizontalTouched >= 0.0 && horizontalTouched <= 4.0){
            if(verticalTouched >= 5.0 && verticalTouched <=9.0){
                return "DELETE";
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


    public void UIlayout(){


        Paint uiPaint = new Paint();

        //canvas.drawColor(Color.WHITE);

        canvas.drawBitmap(_and,280 ,50,null );
        canvas.drawBitmap(_or,280 ,300, null);
        canvas.drawBitmap(_not,280 ,600, null);
        canvas.drawBitmap(_switch,280 ,900, null);
        canvas.drawBitmap(_switch,5 ,900, null);

        uiPaint.setTextSize(Grid.blockSize-3);
        uiPaint.setColor(Color.BLACK);
        canvas.drawText("Play",55,Grid.blockSize*3,uiPaint);
        canvas.drawText("Delete",50,Grid.blockSize*8,uiPaint);
        canvas.drawText("Wire",50,Grid.blockSize*13,uiPaint);
        uiPaint.setColor(Color.argb(255,128,0,255));
        canvas.drawText("LED",50,Grid.blockSize*18,uiPaint);

        uiPaint.setColor(Color.argb(255,0,0,255));
        canvas.drawText("AND",350,Grid.blockSize*3,uiPaint);
        uiPaint.setColor(Color.argb(255,0,255,0));
        canvas.drawText("OR",350,Grid.blockSize*7,uiPaint);
        uiPaint.setColor(Color.argb(255,255,0,0));
        canvas.drawText("NOT",340,Grid.blockSize*(float)12.75,uiPaint);
        uiPaint.setColor(Color.argb(255,128,0,255));
        canvas.drawText("Switch",350,Grid.blockSize*18,uiPaint);


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
                        Touch.horizontalTouched, 750,
                grid.getBlockSize() * 8, paint);
        canvas.drawText("verticalTouched = " +
                        Touch.verticalTouched, 750,
                grid.getBlockSize() * 9, paint);
        canvas.drawText("horizontalTouched*55 = " +
                        Touch.horizontalTouched*55, 750,
                grid.getBlockSize() * 10, paint);
        canvas.drawText("verticalTouched*55 = " +
                        Touch.verticalTouched*55, 750,
                grid.getBlockSize() * 11, paint);


    }



}
