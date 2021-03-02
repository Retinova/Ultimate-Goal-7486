package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.supers.Mode;
import org.firstinspires.ftc.teamcode.supers.Robot;

@TeleOp(name="Drive OpMode", group="TeleOp")
public class DriveOpMode extends LinearOpMode {
    private Robot robot;

    private double speedSetting = 1.0;
    private boolean lastDDown = false, lastLBumper = false, lastRBumper = false;

    @Override
    public void runOpMode() throws InterruptedException{
        robot = new Robot(this, Mode.TELEOP);

        robot.initCheck();

        waitForStart();

        robot.in.setPower(1.0);

        while(isStarted() && !isStopRequested()){
            // Mecannum math
            double r = Math.hypot(gamepad1.left_stick_x, gamepad1.left_stick_y);
            double robotAngle = Math.atan2(gamepad1.left_stick_y, gamepad1.left_stick_x) - Math.PI / 4;
            double rightX = -gamepad1.right_stick_x;

            double lf = r * Math.sin(robotAngle) * speedSetting + rightX * speedSetting;
            double lb = r * Math.cos(robotAngle) * speedSetting + rightX * speedSetting;
            double rf = r * Math.cos(robotAngle) * speedSetting - rightX * speedSetting;
            double rb = r * Math.sin(robotAngle) * speedSetting - rightX * speedSetting;

            robot.lf.setPower(lf);
            robot.lb.setPower(lb);
            robot.rf.setPower(rf);
            robot.rb.setPower(rb);

            // Toggle mechanism for intake
            if(gamepad1.dpad_down && !lastDDown){
                robot.in.setPower(robot.in.getPower() > 0.0 ? 0.0 : 1.0);
            }
            lastDDown = gamepad1.dpad_down;

            // Output button, start/stop launch motors
            if(gamepad1.a){
                robot.out1.setPower(1.0);
                robot.out2.setPower(1.0);
            }
            else{
                robot.out1.setPower(0.0);
                robot.out1.setPower(0.0);
            }

            // Platform rotation
            // For a standard 270 degree servo, 0.0037037037 in position is equivalent to ~1 degree of rotation
            // For a range 0-45: [0.0, 0.1666...]
            if(gamepad1.left_bumper && !lastLBumper){
                robot.platform.setPosition(robot.platform.getPosition() - (9 * 0.0037037037));
            }
        }
    }
}