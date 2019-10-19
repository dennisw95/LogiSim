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
import java.util.Iterator;
import java.util.List;


/*
    classes Switch, AND, OR, NOT, and LED come from
    Daryl Posnett's LogiSimEvaluationExample
 */
interface Node{

    boolean eval();
    void draw(Canvas canvas);
    String whatObjAmI();
    int getPosX();
    int getPosY();

}
// All the logical gates
class Switch implements Node{
    boolean state=false ;
    int posX, posY;
    public Switch(){}
    public Switch(float posX, float posY){
        this.posX = (int) (posX * Grid.blockSize);
        this.posY = (int) (posY * Grid.blockSize);
    }
    public Switch( boolean state) { this . state = state; }
    public void toggle() { this . state = ! this . state ; }
    public boolean eval() { return state ; }

    public void draw(Canvas canvas){
        canvas.drawBitmap(LogiSim._switch,posX,posY,null);
    }

    @Override
    public String whatObjAmI() {
        return "Switch";
    }

    @Override
    public int getPosX() {
        return posX;
    }

    @Override
    public int getPosY() {
        return posY;
    }

}
class AND implements Node{
    Node a,b;
    int posX, posY;
    public AND () {}
    public AND(float posX, float posY){
        this.posX = (int) (posX * Grid.blockSize);
        this.posY = (int) (posY * Grid.blockSize);
    }

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

    public void draw(Canvas canvas){
        canvas.drawBitmap(LogiSim._and,posX,posY,null);
    }

    @Override
    public String whatObjAmI() {
        return "AND";
    }
    @Override
    public int getPosX() {
        return posX;
    }

    @Override
    public int getPosY() {
        return posY;
    }

}
class OR implements Node{
    Node a,b;
    int posX, posY;

    public OR () {}
    public OR(float posX, float posY){
        this.posX = (int) (posX * Grid.blockSize);
        this.posY = (int) (posY * Grid.blockSize);
    }
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
    public void draw(Canvas canvas){
        canvas.drawBitmap(LogiSim._or,posX,posY,null);
    }

    @Override
    public String whatObjAmI() {
        return "OR";
    }
    @Override
    public int getPosX() {
        return posX;
    }

    @Override
    public int getPosY() {
        return posY;
    }
}
class NOT implements Node{
    Node n;
    int posX, posY;
    public NOT(){}
    public NOT(float posX, float posY){
        this.posX = (int) (posX * Grid.blockSize);
        this.posY = (int) (posY * Grid.blockSize);
    }
    public NOT(Node n){this.setSource(n);}
    public void setSource(Node n){this.n=n;}
    public boolean eval(){return !n.eval();}
    public void draw(Canvas canvas){
        canvas.drawBitmap(LogiSim._not,posX,posY,null);
    }

    @Override
    public String whatObjAmI() {
        return "NOT";
    }
    @Override
    public int getPosX() {
        return posX;
    }

    @Override
    public int getPosY() {
        return posY;
    }
}
class LED implements Node{
    Node n;
    int posX, posY;
    public LED(){}
    public LED(float posX, float posY){
        this.posX = (int) (posX * Grid.blockSize);
        this.posY = (int) (posY * Grid.blockSize);
    }
    public LED(Node n){this.setSource(n);}
    public void setSource(Node n){this.n=n;}

    public boolean eval() {
        return n.eval();
    }


    public void draw(Canvas canvas) {
        canvas.drawBitmap(LogiSim._led,posX,posY,null);
    }

    @Override
    public String whatObjAmI() {
        return "LED";
    }
    @Override
    public int getPosX() {
        return posX;
    }

    @Override
    public int getPosY() {
        return posY;
    }
}

interface Mode{
    void save();
    void delete();
    void playPause();
    void wire();
    void move();
    void load();
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
        interfaceWidth = 6*blockSize;
        halfOfInterfaceWidth = interfaceWidth/2;
        numberOfDividers = blockSize * 2;
    }


    public void draw(Paint paint, Canvas myCanvas){


        myCanvas.drawColor(Color.argb(255,255,255,255));
        //draws 2 horizontal lines to encapsulate the icons
        myCanvas.drawLine(interfaceWidth,0,interfaceWidth,(float) (numberVerticalPixels*0.71),paint);
        myCanvas.drawLine(halfOfInterfaceWidth,0,halfOfInterfaceWidth, (float) (numberVerticalPixels*0.71),paint);


        //a vertical divider to encapsulate the icons
        for (int i=0; i<8;i++){

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

public class LogiSim extends Activity implements Mode{

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
    static Bitmap _and,_or,_not,_switch,_led,_playpause,_delete,_move,_wire,_save,_a,_b,_c;
    List<Node> circuitComponents;
    ArrayList<List<Node>> schematicList;
    TouchState placeState;
    TouchStatev2 touchState;
    Mode mode;
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
        circuitComponents = new ArrayList<>();
        schematicList = new ArrayList<>();
        placeState = new TouchState();
        touchState = new TouchStatev2();

        setContentView(gameView);


        getImagesReady();

        Log.d("Debugging", "In onCreate");

        draw();
    }

    public void getImagesReady() {
        _and = BitmapFactory.decodeResource(getResources(), R.drawable.smallandgatetrans);
        _or = BitmapFactory.decodeResource(getResources(), R.drawable.smallorgate);
        _not = BitmapFactory.decodeResource(getResources(), R.drawable.smallnotgate);
        _switch = BitmapFactory.decodeResource(getResources(), R.drawable.switchsymbol);
        _led = BitmapFactory.decodeResource(getResources(),R.drawable.smallbulb);
        _playpause = BitmapFactory.decodeResource(getResources(),R.drawable.playpause);
        _delete = BitmapFactory.decodeResource(getResources(),R.drawable.delete);
        _move = BitmapFactory.decodeResource(getResources(),R.drawable.move);
        _wire = BitmapFactory.decodeResource(getResources(),R.drawable.wire);
        _save = BitmapFactory.decodeResource(getResources(),R.drawable.save);
        _a = BitmapFactory.decodeResource(getResources(),R.drawable.a);
        _b = BitmapFactory.decodeResource(getResources(),R.drawable.b);
        _c = BitmapFactory.decodeResource(getResources(),R.drawable.c);

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


        Log.d("Debugging", "In draw");

        if (debugging) {
            printDebuggingText();
        }


    }



    private void drawComponentsList() {
        final int listSize = circuitComponents.size();

        //final int listSize = visualComponents.size();
        for (int i=0;i<listSize;i++){
            //visualComponents.get(i).drawComponent(canvas);
            circuitComponents.get(i).draw(canvas);
        }



    }

    //testing logic
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
        if (placeState.getState() == false) {
            if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                Touch.horizontalTouched = (int) motionEvent.getX() / grid.getBlockSize();
                Touch.verticalTouched = (int) motionEvent.getY() / grid.getBlockSize();
                whatWasTouched = whatWasTouched(Touch.horizontalTouched, Touch.verticalTouched);
                if(whatWasTouched.equals("DELETE")){
                    circuitComponents.clear();
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


    void placeComponent(){
        Log.d("Debugging", "In placeComponent");

        // Convert the float screen coordinates
        // into int grid coordinates
        if(whatWasTouched.equals("DELETE")){
            circuitComponents.clear();
        }else {
            createComponent(whatWasTouched);
        }

}

    private void createComponent(String whatWasTouched) {
        switch(whatWasTouched){
            case "AND":
                circuitComponents.add(new AND(Touch.secondHorizontalTouch,Touch.secondVerticalTouch));
                break;
            case "OR":
                circuitComponents.add(new OR(Touch.secondHorizontalTouch,Touch.secondVerticalTouch));
                break;
            case "NOT":
                circuitComponents.add(new NOT(Touch.secondHorizontalTouch,Touch.secondVerticalTouch));
                break;
            case "SWITCH":
                circuitComponents.add(new Switch(Touch.secondHorizontalTouch,Touch.secondVerticalTouch));
                break;
            case "LED":
                circuitComponents.add(new LED(Touch.secondHorizontalTouch,Touch.secondVerticalTouch));
                break;
        }
    }



    // used to tell regionHit() what to do
    private String whatWasTouched(float horizontalTouched, float verticalTouched) {
        if(horizontalTouched >= 3.0 && horizontalTouched <= 5.0){
            if(verticalTouched >= 0.0 && verticalTouched <=1.0){
                return "AND";
            }
        }
        if(horizontalTouched >= 3.0 && horizontalTouched <= 5.0){
            if(verticalTouched >= 2.0 && verticalTouched <=3.0){
                return "OR";
            }
        }
        if(horizontalTouched >= 3.0 && horizontalTouched <= 5.0){
            if(verticalTouched >= 4.0 && verticalTouched <=5.0){
                return "NOT";
            }
        }
        if(horizontalTouched >= 3.0 && horizontalTouched <= 5.0){
            if(verticalTouched >= 6.0 && verticalTouched <=7.0){
                return "SWITCH";
            }
        }
        if(horizontalTouched >= 3.0 && horizontalTouched <= 5.0){
            if(verticalTouched >= 8.0 && verticalTouched <=9.0){
                return "LED";
            }
        }
        if(horizontalTouched >= 3.0 && horizontalTouched <= 5.0){
            if(verticalTouched >= 10.0 && verticalTouched <=11.0){
                return "B";
            }
        }
        if(horizontalTouched >= 0.0 && horizontalTouched <= 2.0){
            if(verticalTouched >= 0.0 && verticalTouched <=1.0){
                return "PlayPause";
            }
        }
        if(horizontalTouched >= 0.0 && horizontalTouched <= 2.0){
            if(verticalTouched >= 2.0 && verticalTouched <=3.0){
                return "DELETE";
            }
        }
        if(horizontalTouched >= 0.0 && horizontalTouched <= 2.0){
            if(verticalTouched >= 4.0 && verticalTouched <=5.0){
                return "MOVE";
            }
        }
        if(horizontalTouched >= 0.0 && horizontalTouched <= 2.0){
            if(verticalTouched >= 6.0 && verticalTouched <=7.0){
                return "SAVE";
            }
        }
        if(horizontalTouched >= 0.0 && horizontalTouched <= 2.0){
            if(verticalTouched >= 8.0 && verticalTouched <=9.0){
                return "A";
            }
        }
        if(horizontalTouched >= 0.0 && horizontalTouched <= 2.0){
            if(verticalTouched >= 10.0 && verticalTouched <=11.0){
                return "C";
            }
        }
        if(horizontalTouched >= 0.0 && horizontalTouched <= 2.0){
            if(verticalTouched >= 6.0 && verticalTouched <=7.0){
                return "WIRE";
            }
        }
        if(horizontalTouched >5.0){
            if(verticalTouched > 11.0){
                return "CANVAS";
            }
        }


        return "-1";
    }


    public void save() {

        switch (whatWasTouched){
            case "A":
                schematicList.add(0,circuitComponents);
                break;
            case "B":
                schematicList.add(1,circuitComponents);
                break;
            case "C":
                schematicList.add(2,circuitComponents);
                break;
        }
    }

    public void load() {
        switch(whatWasTouched){
            case "A":
                circuitComponents = schematicList.get(0);
                break;
            case "B":
                circuitComponents = schematicList.get(1);
                break;
            case "C":
                circuitComponents = schematicList.get(2);
                break;
        }
    }

    public void delete() {
        circuitComponents.clear();
    }

    //evaluating circuit
    public void playPause() {
        int i=0;
        while(!circuitComponents.get(i).whatObjAmI().equals("LED")){
            i++;
        }
        boolean eval;
        eval = circuitComponents.get(i).eval();
        if(eval){
            canvas.drawText("1",circuitComponents.get(i).getPosX(),circuitComponents.get(i).getPosY(),paint);
        }else
            canvas.drawText("0",circuitComponents.get(i).getPosX(),circuitComponents.get(i).getPosY(),paint);

    }

    public void wire() {
        int srcX,srcY,destX,destY;
        srcX = (int) (Touch.secondHorizontalTouch *Grid.blockSize);
        srcY = (int) (Touch.secondVerticalTouch *Grid.blockSize);
        destX = (int) (Touch.thirdHorizontalTouch * Grid.blockSize);
        destY = (int) (Touch.thirdVerticalTouch * Grid.blockSize);
        int i=0;
        while(circuitComponents.get(i).getPosX()!=srcX && circuitComponents.get(i).getPosY() !=srcY){
            i++;
        }
        int j=0;
        while(circuitComponents.get(j).getPosY() != destX && circuitComponents.get(j).getPosY() != destY){
            j++;
        }
        canvas.drawLine(srcX,srcY,destX,destY,paint);

    }

    public void move() {
        int srcX,srcY,destX,destY;
        srcX = (int) (Touch.secondHorizontalTouch *Grid.blockSize);
        srcY = (int) (Touch.secondVerticalTouch *Grid.blockSize);
        destX = (int) (Touch.thirdHorizontalTouch * Grid.blockSize);
        destY = (int) (Touch.thirdVerticalTouch * Grid.blockSize);
    }




    public void UIlayout(){
        canvas.drawBitmap(_playpause,0,0,null);
        canvas.drawBitmap(_delete,0,110,null);
        canvas.drawBitmap(_move,0,220,null);
        canvas.drawBitmap(_wire,0,330,null);
        canvas.drawBitmap(_save,0,440,null);
        canvas.drawBitmap(_a,0,550,null);
        canvas.drawBitmap(_c,0,660,null);

        canvas.drawBitmap(_and,165 ,0,null );
        canvas.drawBitmap(_or,165 ,110, null);
        canvas.drawBitmap(_not,165 ,220, null);
        canvas.drawBitmap(_switch,165 ,330, null);
        canvas.drawBitmap(_led,165 ,440, null);
        canvas.drawBitmap(_b,165,550,null);
    }

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
