package org.firstinspires.ftc.teamcode.odometry;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.supers.Direction;
import org.firstinspires.ftc.teamcode.supers.Globals;
import org.firstinspires.ftc.teamcode.supers.Robot;
import org.firstinspires.ftc.teamcode.supers.Target;

import java.util.HashMap;
import java.util.Iterator;

public class Odometry {
    private Robot r;

    // backend mouse stuff + constants
    private final int dpi = 1000;
    private final int fieldLength = 144000;
    private final double rZ = 0.0;
    private final HashMap<Target, double[]> targetCoords;
    private int[] lastTotals = {0, 0}; // used to find deltas at every call of update()

    // turning vars
    public double angleError;
    // TODO: set threshold + tuning
    private double turnThreshhold = 1.0;
    private PIDController turnPid = new PIDController(0.015, 0.0, 0.0);

    // odo vars
    private double currentX = 0;
    private double currentY = 0;
    private double coordError;
    // TODO: set threshold + tuning
    private double coordThreshold = 0b110010000; // 0.4 in
    private PIDController posPid = new PIDController(0, 0, 0);

    // mouse hardware
    private UsbManager usbManager;
    private UsbDevice device;
    private HashMap<String, UsbDevice> deviceList = new HashMap<>();
    private MouseThread mouseThread;
    private boolean isMouseInitialized = false;

    public Odometry(Robot robot){
        this.r = robot;

        // mouse
        try {
            usbManager = (UsbManager) AppUtil.getDefContext().getSystemService(Context.USB_SERVICE);
            deviceList = usbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            while(deviceIterator.hasNext()){
                device = deviceIterator.next();
                if(device.getProductId() == 0x4d0f) break;
            }

            mouseThread = new MouseThread(usbManager, device);
            isMouseInitialized = true;
        } catch (Exception e){
            r.opMode.telemetry.addData("Failed to setup mouse: ", e);
            r.opMode.telemetry.update();
        }

        // construct field coords
        targetCoords = new HashMap<>(); // TODO: get actual coords
        targetCoords.put(Target.TOP, new double[]{0.0, 0.0, 0.0});
        targetCoords.put(Target.MID, new double[]{0.0, 0.0, 0.0});
        targetCoords.put(Target.LOW, new double[]{0.0, 0.0, 0.0});
        targetCoords.put(Target.POWER, new double[]{0.0, 0.0, 0.0});
    }

    public void setVelocity(double forward, double clockwise){
        // TODO: make sure directions are correct
        double right = forward - clockwise;
        double left = forward + clockwise;
        double max = Math.max(Math.max(Math.abs(left), Math.abs(right)), 1.0);

        r.lf.setPower(left / max);
        r.lb.setPower(left / max);
        r.rf.setPower(right / max);
        r.rb.setPower(right / max);
    }

    public void initMouse(){
        if(isMouseInitialized) mouseThread.start();
    }

    public void turnTo(double angle){

        // use 0 - 360
        if(angle >= 180 || angle <= -180) {
            angle = normalize(angle); // normalize the angle if it's negative

            int counter = 0; // counter to keep track of consecutive times robot was within threshold

            turnPid.start();
            angleError = getError(angle, normalize(getCurrentAngle()));

            while (r.opMode.opModeIsActive()) {
                if(Math.abs(angleError) > turnThreshhold) counter++; // threshold check
                else counter = 0; // reset threshold counter (i.e. overshoot)

                if(counter >= 2) break;

                setVelocity(0, turnPid.getOutput(angleError)); // set motor velocities with controller output

                r.opMode.telemetry.addData("Current error: ", angleError);
                r.opMode.telemetry.addData("Current angle: ", getCurrentAngle());
                r.opMode.telemetry.addData("Target: ", angle);
                r.opMode.telemetry.update();

                angleError = getError(angle, normalize(getCurrentAngle())); // update error using normalized angle
            }
        }

        // use 180 - -180
        // all components are the same as above case but adjusted for not using 0 - 360
        else{
            int counter = 0;

            turnPid.start();
            angleError = getError(angle, getCurrentAngle());

            while(r.opMode.opModeIsActive()) {
                if(Math.abs(angleError) > turnThreshhold) counter++;
                else counter = 0;

                if(counter >= 2) break;

                setVelocity(0, turnPid.getOutput(angleError));
                r.opMode.telemetry.addData("Current error: ", angleError);
                r.opMode.telemetry.addData("Current angle: ", getCurrentAngle());
                r.opMode.telemetry.addData("Target: ", angle);
                r.opMode.telemetry.update();
                angleError = getError(angle, getCurrentAngle());
            }
        }

        setVelocity(0, 0); // stop motion

        turnPid.reset();

        r.opMode.telemetry.addData("Current error: ", angleError);
        r.opMode.telemetry.addData("Current angle: ", getCurrentAngle());
        r.opMode.telemetry.addData("Target: ", angle);
        r.opMode.telemetry.update();
    }

    public double getCurrentAngle(){
        return r.imu.getAngularOrientation().firstAngle;
    }

    public double normalize(double angle){
        angle = angle % 360;
        if(angle < 0) return angle + 360;
        else return angle;
    }

    public double getError(double target, double actual){
        return actual - target;
    }

    public void update() {
        double currentAng = Math.toRadians(getCurrentAngle()); // get angle in radians
        double shifted = currentAng + (Math.PI / 2.0); // get angle shifted 90 degrees for y inputs

        int[] totals = mouseThread.getCoords(); // 0 = mouse x, 1 = mouse y

        // shift the angle for the y-input, but dont for the x-input because perpendicular to y movement(already shifted)
        // y
        double deltaX1 = (totals[1] - lastTotals[1]) * Math.cos(shifted);
        double deltaY1 = (totals[1] - lastTotals[1]) * Math.sin(shifted);

        // x
        double deltaX2 = (totals[0] - lastTotals[0]) * Math.cos(currentAng);
        double deltaY2 = (totals[0] - lastTotals[0]) * Math.sin(currentAng);

        // add the calculated deltas
        currentX += deltaX1 + deltaX2;
        currentY += deltaY1 + deltaY2;

        // record last update
        lastTotals = totals;
    }

    public void drive(double deltaX, double deltaY){

        // get the angle to turn for aligning with the hypotenuse from inverse tan, subtract 90 to shift into proper robot orientation
        double targetAngle = Math.toDegrees(Math.atan2(deltaY, deltaX)) - 90;

        // optimize the angle being passed into turnTo() (positive angle vs. negative counterpart, finds whichever is closest)
        if(targetAngle < 0){
            if(Math.abs(targetAngle - getCurrentAngle()) > Math.abs((targetAngle + 360) - getCurrentAngle())) targetAngle += 360;
        }
        else{
            if(Math.abs(targetAngle - getCurrentAngle()) > Math.abs((targetAngle - 360) - getCurrentAngle())) targetAngle -= 360;
        }

        // align with hypotenuse
        turnTo(targetAngle);


        // get the target distance as the current "y" value plus the hypotenuse of the desired change in coordinates
//        double targetDist = currentY + Math.hypot(Math.abs(deltaX), Math.abs(deltaY)); // irrelevant now?

        double targetX = currentX + deltaX;
        double targetY = currentY + deltaY;

        double lf;
        double lb;
        double rf;
        double rb;
        double coordOut;
        double curAng;

        int counter = 0;

        // TODO: eventually add maintaining of angle (see PushbotAutoDriveByGyro)

        posPid.start();

        coordError = Math.hypot(getError(targetX, currentX), getError(targetY, currentY));
        double angleToTarget = Math.atan2(getError(targetY, currentY), getError(targetX, currentX)) - Math.PI / 4 - Math.toRadians(getCurrentAngle());

        while(r.opMode.opModeIsActive()){
//            setVelocity(posPid.getOutput(coordError), 0);
            if(Math.abs(coordError) < coordThreshold) counter++;
            else counter = 0;

            if(counter >= 2) break;

            coordOut = posPid.getOutput(coordError);

            lf = coordOut * Math.sin(angleToTarget);
            lb = coordOut * Math.cos(angleToTarget);
            rf = coordOut * Math.cos(angleToTarget);
            rb = coordOut * Math.sin(angleToTarget);

            r.lf.setPower(lf);
            r.lb.setPower(lb);
            r.rf.setPower(rf);
            r.rb.setPower(rb);

            // update coordinates
            update();
            curAng = Math.toRadians(getCurrentAngle());

            coordError = Math.hypot(getError(targetX, currentX), getError(targetY, currentY));
            // subtracts the current angle for field-centric
            angleToTarget = Math.atan2(getError(targetY, currentY), getError(targetX, currentX)) - Math.PI / 4 - curAng;
        }
        setVelocity(0, 0);

        posPid.reset();
    }

    public void resetEncoders(){
        r.lf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r.lb.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r.rf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r.rb.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        r.lf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        r.lb.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        r.rf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        r.rb.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void drive(Direction direction, double distance, double speed){

        int newLeftFrontTarget = 0;
        int newLeftBackTarget = 0;
        int newRightFrontTarget = 0;
        int newRightBackTarget = 0;

        double wheelDiam = 4.0;
        double ticksPerRev = 537.6;
        double inchesPerRev = wheelDiam * Math.PI;
        double ticksPerInch = ticksPerRev/inchesPerRev;

        if (direction == Direction.FORWARD) {
            distance *= ticksPerInch;
            newLeftFrontTarget = r.lf.getCurrentPosition() + (int) distance;
            newLeftBackTarget = r.lb.getCurrentPosition() + (int) distance;
            newRightFrontTarget = r.rf.getCurrentPosition() + (int) distance;
            newRightBackTarget = r.rb.getCurrentPosition() + (int) distance;
        }
        if (direction == Direction.BACK) {
            distance *= ticksPerInch;
            newLeftFrontTarget = r.lf.getCurrentPosition() - (int) distance;
            newLeftBackTarget = r.lb.getCurrentPosition() - (int) distance;
            newRightFrontTarget = r.rf.getCurrentPosition() - (int) distance;
            newRightBackTarget = r.rb.getCurrentPosition() - (int) distance;
        }
        if (direction == Direction.LEFT) {
            distance *= ticksPerInch;
            newLeftFrontTarget = r.lf.getCurrentPosition() - (int) distance;
            newLeftBackTarget = r.lb.getCurrentPosition() + (int) distance;
            newRightFrontTarget = r.rf.getCurrentPosition() + (int) distance;
            newRightBackTarget = r.rb.getCurrentPosition() - (int) distance;
        }
        if (direction == Direction.RIGHT) {
            distance *= ticksPerInch;
            newLeftFrontTarget = r.lf.getCurrentPosition() + (int) distance;
            newLeftBackTarget = r.lb.getCurrentPosition() - (int) distance;
            newRightFrontTarget = r.rf.getCurrentPosition() - (int) distance;
            newRightBackTarget = r.rb.getCurrentPosition() + (int) distance;
        }

        // Ensure that the OpMode is still active
        if (r.opMode.opModeIsActive()) {
            r.lf.setTargetPosition(newLeftFrontTarget);
            r.lb.setTargetPosition(newLeftBackTarget);
            r.rf.setTargetPosition(newRightFrontTarget);
            r.rb.setTargetPosition(newRightBackTarget);

            // Turn On RUN_TO_POSITION
            r.lf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            r.lb.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            r.rf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            r.rb.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Reset timer and begin to run the motors
            if(direction == Direction.LEFT || direction == Direction.RIGHT){
                r.lf.setPower(Math.abs(speed));
                r.lb.setPower(Math.abs(speed));
                r.rf.setPower(Math.abs(speed));
                r.rb.setPower(Math.abs(speed));
            }
            else {
                r.lf.setPower(Math.abs(speed));
                r.rf.setPower(Math.abs(speed));
                r.lb.setPower(Math.abs(speed));
                r.rb.setPower(Math.abs(speed));
            }

            // Keep looping until the motor is at the desired position that was inputted
            while (r.opMode.opModeIsActive() &&
                    (r.lf.isBusy() && r.lb.isBusy() && r.rf.isBusy() && r.rb.isBusy())) {

                // Display current status of motor paths
                r.opMode.telemetry.addData("Path1", "Running to %7d :%7d :%7d :%7d", newLeftFrontTarget, newLeftBackTarget, newRightFrontTarget, newRightBackTarget);
                r.opMode.telemetry.addData("Path2", "Running at %7d :%7d :%7d :%7d", r.lf.getCurrentPosition(), r.lb.getCurrentPosition(), r.rf.getCurrentPosition(), r.rb.getCurrentPosition());
                r.opMode.telemetry.addData("right back", r.rb.getPower());
                r.opMode.telemetry.addData("right front", r.rf.getPower());
                r.opMode.telemetry.addData("left back", r.lb.getPower());
                r.opMode.telemetry.addData("left front", r.lf.getPower());
                r.opMode.telemetry.update();
            }

            // Stop all motion
            if(direction == Direction.LEFT || direction == Direction.RIGHT) {
                r.lf.setPower(0);
                r.lb.setPower(0);
                r.rf.setPower(0);
                r.rb.setPower(0);
            }
            else {
                r.lf.setPower(0);
                r.rf.setPower(0);
                r.lb.setPower(0);
                r.rb.setPower(0);
            }

            resetEncoders();

        }
    }

    public boolean alignAndCheckShot(Target target){
        double[] targetCoords = this.targetCoords.get(target);
        double angleToTarget = Math.toDegrees(Math.atan2(targetCoords[1] - currentY, targetCoords[0] - currentX)) - 90;


        return true;
    }
}
