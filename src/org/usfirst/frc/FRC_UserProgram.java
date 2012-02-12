package org.usfirst.frc;

/*
 * StartApplication.java
 *
 */
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
//import org.mclv.test.Jaguar; //@TODO CHANGE TO WPI JAGUAR BEFORE RUNNING!!
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.camera.AxisCamera;

/**
 * The startApp method of this class is called by the VM to start the
 * application.
 * 
 * The manifest specifies this class as MIDlet-1, which means it will
 * be selected for execution.
 */
public class FRC_UserProgram extends MIDlet {
    // CONFIGURATION PARAMETERS
    private static final int TRIM_INDEX = 2;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int AUX = 2;
    private static final int DRIVE_AXIS = 1;
    private static final boolean[] DRIVE_INV = {false,false};
    private static final int[] INTAKE_AXIS = {LEFT,TRIM_INDEX};
    private static final int[] INTAKE_IN = {LEFT,2};
    private static final int[] INTAKE_OUT = {LEFT,3};
    private static final double INTAKE_VAL = 0.75;
    private static final boolean INTAKE_INV = false;
    private static final int[] AIM_UP = {RIGHT,2};
    private static final int[] AIM_DOWN = {RIGHT,3};
    private static final double AIM_VAL = 0.3;
    private static final double AIM_OFFSET = 0.2;
    private static final boolean AIM_INV = false;
    private static final int[] FEED_AXIS = {RIGHT,TRIM_INDEX};
    private static final int[] FEED_UP = {AUX,2};
    private static final int[] FEED_DOWN = {AUX,3};
    private static final boolean FEED_INV = false;
    private static final double FEED_VAL = 0.75;
    private static final int[] SHOOT = {LEFT,1};
    private static final int[] SHOOT_AXIS = {RIGHT,TRIM_INDEX};
    private static final double SHOOT_VAL = 1;
    private static final boolean SHOOT_INV = false;
    private static final long PERIOD = 10;
    // END CONFIG PARAMS
    private boolean live = true;
    private Jaguar[][] driveJags;
    private Jaguar[] shootJags;
    private Jaguar[] feedJags;
    private Jaguar aimJag;
    private Joystick[] driveJoys;
    private Joystick aux;
    private boolean useCam = false;
    private AxisCamera cam;
    protected void startApp() throws MIDletStateChangeException {
        driveJags = new Jaguar[2][2];
        shootJags = new Jaguar[2];
        feedJags = new Jaguar[2];
        driveJoys = new Joystick[2];
        String u = "USB Assignment\n";
        String p = "PWM Assignment\n";
        int pwm = 1;
        int usb = 1;
        for(int i = 0; i<2; i++){
            driveJoys[i] = new Joystick(usb);
            u += usb+": Drive Joystick";
            usb++;
            for(int j = 0; j<2; j++){
                Jaguar thisJag = new Jaguar(pwm);
                driveJags[i][j] = thisJag;
                p += pwm +": Drive Jaguar";
                if(i == LEFT){
                    p += ", Left\n";
                }
                else{
                    p += ", Right\n";
                }
                pwm++;
            }
            if(i == LEFT){
                u += ", Left\n";
            }
            else{
                u += ", Right\n";
            }
        }
        for(int i = 0; i<2; i++){
            shootJags[i] = new Jaguar(pwm);
            p += pwm +": Shoot Jaguar\n";
            pwm++;
        }
        
        for(int i = 0; i<2; i++){
            feedJags[i] = new Jaguar(pwm);
            p += pwm +": Feed Jaguar\n";
            pwm++;
        }
        aimJag = new Jaguar(pwm);
        p += pwm +": Aim Jaguar\n";
        aux = new Joystick(usb);
         u += usb + ": Aux Joystick\n";
        if(useCam){
            cam = AxisCamera.getInstance();
        }
        System.out.println(p);
        System.out.println(u);
        //END INIT
        
        while(true){
            if(live){
                run();
            }
            try{Thread.sleep(PERIOD);}catch(InterruptedException e){}
        }
    }
    protected void pauseApp() {
        live = false;
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }
    public void run(){
        drive();
        intake();
        aim();
        feed();
        shoot();
    }
    private void intake(){
        if(getButton(INTAKE_IN)){
            setIntake(INTAKE_VAL);
        }
        else if(getButton(INTAKE_OUT)){
            setIntake(-INTAKE_VAL);
        }
        else{
            setIntake();
        }
    }
    private void aim(){
        if(getButton(AIM_UP)){
            setAim(AIM_VAL + AIM_OFFSET);
        }
        else if(getButton(AIM_DOWN)){
            setAim(-AIM_VAL);
        }
        else{
            setAim(0);
        }
    }
    private void feed(){
        if(getButton(FEED_UP)){
            setFeed(FEED_VAL);
        }
        else if(getButton(FEED_DOWN)){
            setFeed(-FEED_VAL);
        }
        else{
            setFeed(0);
        }
    }
    private void shoot(){
        if(getButton(SHOOT)){
            setShoot(SHOOT_VAL);
        }
        else{
            setShoot();
        }
    }
    private boolean getButton(int[] button){
        if(button[0] != 2){
            return driveJoys[button[0]].getRawButton(button[1]);
        }
        return aux.getRawButton(button[1]);
    }
    private double getAxis(int[] axis){
        if(axis[0] != 2){
            return driveJoys[axis[0]].getRawAxis(axis[1]);
        }
        return aux.getRawAxis(axis[1]);
    }
    private void setShoot(){
        setShoot(getAxis(SHOOT_AXIS));
    }
    private void setShoot(double val){
        if(SHOOT_INV){
            val = -val;
        }
        for(int i = 0; i<feedJags.length; i++){
            feedJags[i].set(val);
        }
    }
    private void setFeed(){
        setFeed(getAxis(FEED_AXIS));
    }
    private void setFeed(double val){
        if(FEED_INV){
            val = -val;
        }
        feedJags[1].set(val);
    }
    private void setAim(double val){
        if(AIM_INV){
            val = -val;
        }
        aimJag.set(val);
    }
    private void setIntake(){
        setIntake(getAxis(INTAKE_AXIS));
    }
    private void setIntake(double val){
        if(INTAKE_INV){
            val = -val;
        }
        feedJags[0].set(val);
    }
    private void drive(){
        int[] left = {0,DRIVE_AXIS};
        int[] right = {1,DRIVE_AXIS};
        drive(getAxis(left),getAxis(right));
    }
    private void drive(double left, double right){
        double[] vals = {left,right};
        drive(vals);
    }
    private void drive(double[] vals){
        for(int i = 0; i<Math.min(vals.length, driveJags.length); i++){
            double val = vals[i];
            if(DRIVE_INV[i]){
                val = -val;
            }
            for(int j = 0; j<driveJags[i].length; j++){
                driveJags[i][j].set(val);
            }
        }
    }
}
