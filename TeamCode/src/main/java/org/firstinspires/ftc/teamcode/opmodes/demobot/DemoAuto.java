package org.firstinspires.ftc.teamcode.opmodes.demobot;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.supers.Direction;

@Autonomous(name="demobot auto", group="demo")
public class DemoAuto extends LinearOpMode {
    DcMotor lf, lb, rf, rb, clawRot;
    Servo claw1, claw2;

    boolean clawPos = false;

    @Override
    public void runOpMode() throws InterruptedException {
        lf = hardwareMap.dcMotor.get("lf");
        lb = hardwareMap.dcMotor.get("lb");
        rf = hardwareMap.dcMotor.get("rf");
        rb = hardwareMap.dcMotor.get("rb");
        clawRot = hardwareMap.dcMotor.get("clawRot");

        claw1 = hardwareMap.servo.get("hook");
        claw2 = hardwareMap.servo.get("claw");

        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        lb.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("> ", "Initialized");
        telemetry.update();

        waitForStart();

        drive(Direction.FORWARD, 83, 0.7);

    }

    // TODO: go over this again and adapt it back into the version in Odometry
    public void drive(Direction direction, double distance, double speed){
        int newLeftFrontTarget = 0;
        int newLeftBackTarget = 0;
        int newRightFrontTarget = 0;
        int newRightBackTarget = 0;

        double wheelDiam = 4.0;
        double gearRatio = 3.0 / 2.0;
        double ticksPerRev = 1120;
        double actualTicksPR = ticksPerRev / gearRatio;
        double inchesPerRev = wheelDiam * Math.PI;
        double ticksPerInch = actualTicksPR/inchesPerRev;

        distance *= ticksPerInch;

        switch (direction){
            case FORWARD:
                newLeftFrontTarget = lf.getCurrentPosition() + (int) distance;
                newLeftBackTarget = lb.getCurrentPosition() + (int) distance;
                newRightFrontTarget = rf.getCurrentPosition() + (int) distance;
                newRightBackTarget = rb.getCurrentPosition() + (int) distance;
                break;
            case BACK:
                newLeftFrontTarget = lf.getCurrentPosition() - (int) distance;
                newLeftBackTarget = lb.getCurrentPosition() - (int) distance;
                newRightFrontTarget = rf.getCurrentPosition() - (int) distance;
                newRightBackTarget = rb.getCurrentPosition() - (int) distance;
                break;
            case LEFT:
                newLeftFrontTarget = lf.getCurrentPosition() - (int) distance;
                newLeftBackTarget = lb.getCurrentPosition() + (int) distance;
                newRightFrontTarget = rf.getCurrentPosition() + (int) distance;
                newRightBackTarget = rb.getCurrentPosition() - (int) distance;
                break;
            case RIGHT:
                newLeftFrontTarget = lf.getCurrentPosition() + (int) distance;
                newLeftBackTarget = lb.getCurrentPosition() - (int) distance;
                newRightFrontTarget = rf.getCurrentPosition() - (int) distance;
                newRightBackTarget = rb.getCurrentPosition() + (int) distance;
                break;
        }

        // Ensure that the OpMode is still active
        if (opModeIsActive()) {
            lf.setTargetPosition(newLeftFrontTarget);
            lb.setTargetPosition(newLeftBackTarget);
            rf.setTargetPosition(newRightFrontTarget);
            rb.setTargetPosition(newRightBackTarget);

            // Turn On RUN_TO_POSITION
            lf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            lb.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rb.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Reset timer and begin to run the motors
            if(direction == Direction.LEFT || direction == Direction.RIGHT){
                lf.setPower(Math.abs(speed));
                lb.setPower(Math.abs(speed));
                rf.setPower(Math.abs(speed));
                rb.setPower(Math.abs(speed));
            }
            else {
                lf.setPower(Math.abs(speed));
                rf.setPower(Math.abs(speed));
                lb.setPower(Math.abs(speed));
                rb.setPower(Math.abs(speed));
            }

            // Keep looping until the motor is at the desired position that was inputted
            while (opModeIsActive() &&
                    (lf.isBusy() || lb.isBusy() || rf.isBusy() || rb.isBusy())) {

                // Display current status of motor paths
                telemetry.addData("Path1", "Running to %7d :%7d :%7d :%7d", newLeftFrontTarget, newLeftBackTarget, newRightFrontTarget, newRightBackTarget);
                telemetry.addData("Path2", "Running at %7d :%7d :%7d :%7d", lf.getCurrentPosition(), lb.getCurrentPosition(), rf.getCurrentPosition(), rb.getCurrentPosition());
                telemetry.addData("right back", rb.getPower());
                telemetry.addData("right front", rf.getPower());
                telemetry.addData("left back", lb.getPower());
                telemetry.addData("left front", lf.getPower());
                telemetry.update();
            }

            // Stop all motion
            if(direction == Direction.LEFT || direction == Direction.RIGHT) {
                lf.setPower(0);
                lb.setPower(0);
                rf.setPower(0);
                rb.setPower(0);
            }
            else {
                lf.setPower(0);
                rf.setPower(0);
                lb.setPower(0);
                rb.setPower(0);
            }
        }
    }
}
