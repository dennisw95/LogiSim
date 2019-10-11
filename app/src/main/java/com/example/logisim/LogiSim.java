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
    public Switch(){}
    public Switch( boolean state) { this . state = state; }
    public void toggle() { this . state = ! this . state ; }
    public boolean eval() { return state ; }
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
                canvas.drawBitmap(LogiSim._led,posX,posY,null);
            default:
                break;
        }
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public String getComponent() {
        return component;
    }
}
class Wire{
    float startX, startY, stopX, stopY;

    int posX, posY;
    public Wire(float startX, float startY,float stopX, float stopY){
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;

    }
    public void wireComponent(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawLine(startX,startY,stopX,stopY,paint);
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }



}
class Touch{
    public static float fourthHorizontalTouch;
    public static float fourthVerticalTouch;
    static float thirdHorizontalTouch,thirdVerticalTouch;
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

    static class TouchStatev2{
        static String state = "StandBy";
        String getState(){
            return state;
        }

        void toggleState(String touched){
            switch(state){
                case "StandBy":
                    if(touched.equals("AND") || touched.equals("NOT") || touched.equals("OR") || touched.equals("LED") || touched.equals("SWITCH")){
                        state = "PlaceGate";
                    }else if(touched.equals("WIRE")){
                        state = "PlaceStartWire";
                    }else if(touched.equals("DELETE")){
                        state = "DELETE";
                    }
                    break;
                case "PlaceStartWire":
                    state = "PlaceEndWire";
                    break;

                default:
                    state = "StandBy";
            }
        }
    }
    static class TouchState {
        static boolean state = false;

        boolean getState(){
            return state;
        }
        void toggleState(){
             state = !state;
        }
    }
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
    static Bitmap _and,_or,_not,_switch,_led;
    List<Wire> visualWires;
    List<VisualComponents> visualComponents;
    List<Node> logicalComponents;
    ArrayList<List<Node>> logicalSchematicList;
    ArrayList<List<VisualComponents>> visualSchematicList;
    TouchState placeState;
    TouchStatev2 touchState;
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
        visualComponents = new ArrayList<>();
        logicalComponents = new ArrayList<>();
        logicalSchematicList = new ArrayList<>();
        visualSchematicList = new ArrayList<>();
        placeState = new TouchState();
        touchState = new TouchStatev2();
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
        _led = BitmapFactory.decodeResource(getResources(),R.drawable.bulb);
    }




    void draw() {
        gameView.setImageBitmap(blankBitmap);

        // Change the paint color to black
        paint.setColor(Color.BLACK);

        // draw grid
        grid.draw(paint,canvas);


        // draw players touch
        touch.draw(canvas,grid,paint);

        drawComponentsList();


        UIlayout();

        //drawVisualComponents();
        Log.d("Debugging", "In draw");

        if (debugging) {
            printDebuggingText();
        }


    }

    private void drawComponentsList() {
        final int listSize = visualComponents.size();
        for (int i=0;i<listSize;i++)
            visualComponents.get(i).drawComponent(canvas);
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
        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP){
            switch (touchState.getState()) {
                case "StandBy":
                    Touch.horizontalTouched = (int) motionEvent.getX() / grid.getBlockSize();
                    Touch.verticalTouched = (int) motionEvent.getY() / grid.getBlockSize();
                    whatWasTouched = whatWasTouched(Touch.horizontalTouched, Touch.verticalTouched);

                    if (whatWasTouched.equals("WIRE")) {
                        touchState.toggleState(whatWasTouched);
                    }
                    if (whatWasTouched.equals("AND") || whatWasTouched.equals("NOT") || whatWasTouched.equals("OR") || whatWasTouched.equals("LED") || whatWasTouched.equals("SWITCH")) {
                        touchState.toggleState(whatWasTouched);
                    }

                    break;
                case "PlaceGate":
                    Touch.secondHorizontalTouch = (int) motionEvent.getX() / grid.getBlockSize();
                    Touch.secondVerticalTouch = (int) motionEvent.getY() / grid.getBlockSize();
                    placeComponent();
                    touchState.toggleState("");
                    draw();
                    break;
                case "PlaceStartWire":
                    Touch.thirdHorizontalTouch = (int) motionEvent.getX() / grid.getBlockSize();
                    Touch.thirdVerticalTouch = (int) motionEvent.getY() / grid.getBlockSize();
                    touchState.toggleState("PlaceEndWire");
                    canvas.drawText("PlaceStartWire", 1500,1500,paint);
                    break;
                case "PlaceEndWire":
                    Touch.fourthHorizontalTouch = (int) motionEvent.getX() / grid.getBlockSize();
                    Touch.fourthVerticalTouch = (int) motionEvent.getY() / grid.getBlockSize();
                    canvas.drawLine(Touch.thirdHorizontalTouch, Touch.thirdVerticalTouch, Touch.fourthHorizontalTouch, Touch.fourthVerticalTouch, paint);
                    touchState.toggleState("");
                    break;
                case "DELETE":
                    visualSchematicList.clear();
                    logicalComponents.clear();
                    touchState.toggleState("");
                    break;
            }
            draw();
        }
        return true;
    }

 /*

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        Log.d("Debugging", "In onTouchEvent");
        if (placeState.getState() == false) {
            if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                Touch.horizontalTouched = (int) motionEvent.getX() / grid.getBlockSize();
                Touch.verticalTouched = (int) motionEvent.getY() / grid.getBlockSize();
                whatWasTouched = whatWasTouched(Touch.horizontalTouched, Touch.verticalTouched);
                if(whatWasTouched.equals("DELETE")){
                    visualComponents.clear();
                    logicalComponents.clear();
                }else if(whatWasTouched.equals("PLAY")){
                    simulate();
                }
                placeState.toggleState();//sets to true
                draw();
            }

        }else if (placeState.getState() == true) {
            Touch.secondHorizontalTouch = (int) motionEvent.getX() / grid.getBlockSize();
            Touch.secondVerticalTouch = (int) motionEvent.getY() / grid.getBlockSize();
            placeComponent();
            placeState.toggleState();//sets to false
            draw();
        }
        return true;
    }

  */


    private void simulate() {
        final int listSize = logicalComponents.size();


    }

    void placeComponent(){
        Log.d("Debugging", "In placeComponent");

        // Convert the float screen coordinates
        // into int grid coordinates
        if(whatWasTouched.equals("DELETE")){
            visualSchematicList.clear();
            logicalComponents.clear();
        }
        else {
            VisualComponents addThis = new VisualComponents(Touch.secondHorizontalTouch,Touch.secondVerticalTouch,whatWasTouched);
            visualComponents.add(addThis);
            createLogicalComponent(whatWasTouched);
        }

}

    private void createLogicalComponent(String whatWasTouched) {
        switch(whatWasTouched){
            case "AND":
                logicalComponents.add(new AND());
                break;
            case "OR":
                logicalComponents.add(new OR());
                break;
            case "NOT":
                logicalComponents.add(new NOT());
                break;
            case "SWITCH":
                logicalComponents.add(new Switch(false));
                break;
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
        canvas.drawBitmap(_led,5 ,900, null);

        uiPaint.setTextSize(Grid.blockSize-3);
        uiPaint.setColor(Color.BLACK);
        canvas.drawText("Play",55,Grid.blockSize*3,uiPaint);
        canvas.drawText("Delete",50,Grid.blockSize*8,uiPaint);
        canvas.drawText("Wire",50,Grid.blockSize*13,uiPaint);
        uiPaint.setColor(Color.argb(255,128,0,255));
        canvas.drawText("LED",90,Grid.blockSize*18,uiPaint);

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
