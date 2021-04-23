 package org.firstinspires.ftc.teamcode.opmodes.tuning;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.supers.BetterGamepad;
import org.firstinspires.ftc.teamcode.supers.Mode;
import org.firstinspires.ftc.teamcode.supers.Robot;

 @Disabled
 @TeleOp(name="Better Controller Test", group="Testing")
 public class BetterControllerOpMode extends LinearOpMode {
     private Robot robot;

     private double speedSetting = 1.0;
     private boolean clawPos = false;
     private double platformPos = 0.0;
     private BetterGamepad pad1 = (BetterGamepad) gamepad1, pad2 = (BetterGamepad) gamepad2;

     @Override
     public void runOpMode() throws InterruptedException{
         robot = new Robot(this, Mode.TELEOP);

         robot.initCheck();

         waitForStart();

         robot.in.setPower(1.0);

         while(isStarted() && !isStopRequested()){
             // Mecannum math
             double r = Math.hypot(pad1.left_stick_x, pad1.left_stick_y);
             double robotAngle = Math.atan2(pad1.left_stick_y, pad1.left_stick_x) - Math.PI / 4;
             double rightX = -pad1.right_stick_x;

             double lf = r * Math.sin(robotAngle) * speedSetting + rightX * speedSetting;
             double lb = r * Math.cos(robotAngle) * speedSetting + rightX * speedSetting;
             double rf = r * Math.cos(robotAngle) * speedSetting - rightX * speedSetting;
             double rb = r * Math.sin(robotAngle) * speedSetting - rightX * speedSetting;

             robot.lf.setPower(lf);
             robot.lb.setPower(lb);
             robot.rf.setPower(rf);
             robot.rb.setPower(rb);

             // Toggle mechanism for intake
             if(pad1.dpad_downPressed()){
                 robot.in.setPower(robot.in.getPower() > 0.0 ? 0.0 : 1.0);
             }

             // Output button, start/stop launch motors
             if(pad1.a){
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
             // Each bumper press changes rotation by ~9 degrees
             if(pad1.left_bumperPressed()){
                 platformPos = Range.clip(robot.platform.getPosition() - (9 * 0.0037037037), 0.0, 0.16666666666666666);
                 robot.platform.setPosition(platformPos);
             }
             else if(pad1.right_bumperPressed()){
                 platformPos = Range.clip(robot.platform.getPosition() + (9 * 0.0037037037), 0.0, 0.16666666666666666);
                 robot.platform.setPosition(platformPos);
             }

             if(pad1.xPressed()){
                 robot.claw.setPosition(robot.claw.getPosition() == 0.0 ? 0.0 : 0.0); // TODO: Find good values for claw
             }

             // rotating grabber arm (motor)
             if (pad1.bPressed()){
                 if (!clawPos){
                     robot.clawRot.setTargetPosition(robot.clawRot.getCurrentPosition() + 144);
                 }
                 else{
                     robot.clawRot.setTargetPosition(robot.clawRot.getCurrentPosition() - 144);
                 }
                 robot.clawRot.setPower(1.0);
                 clawPos = !clawPos;
             }

             if(!robot.clawRot.isBusy()){
                 robot.clawRot.setPower(0.0);
             }
         }
     }
 }