package org.firstinspires.ftc.teamcode.opmodes.demobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="demobot", group="demo")
public class DemoOpMode extends LinearOpMode {
    DcMotor lf, lb, rf, rb, clawRot;
    Servo claw;

    double speedSetting = 1.0;
    boolean clawPos = false, lastB = false, lastX = false;

    @Override
    public void runOpMode() throws InterruptedException {
        lf = hardwareMap.dcMotor.get("lf");
        lb = hardwareMap.dcMotor.get("lb");
        rf = hardwareMap.dcMotor.get("rf");
        rb = hardwareMap.dcMotor.get("rb");
        clawRot = hardwareMap.dcMotor.get("clawRot");

        telemetry.addData("> ", "Initialized");
        telemetry.update();

        waitForStart();

        while(isStarted() && !isStopRequested()){
            double r = Math.hypot(gamepad1.left_stick_x, gamepad1.left_stick_y);
            double robotAngle = Math.atan2(gamepad1.left_stick_y, gamepad1.left_stick_x) - Math.PI / 4;
            double rightX = -gamepad1.right_stick_x;

            double lf = r * Math.sin(robotAngle) * speedSetting + rightX * speedSetting;
            double lb = r * Math.cos(robotAngle) * speedSetting + rightX * speedSetting;
            double rf = r * Math.cos(robotAngle) * speedSetting - rightX * speedSetting;
            double rb = r * Math.sin(robotAngle) * speedSetting - rightX * speedSetting;

            this.lf.setPower(lf);
            this.lb.setPower(lb);
            this.rf.setPower(rf);
            this.rb.setPower(rb);

            if (gamepad1.b && lastB){
                if (!clawPos){
                    clawRot.setTargetPosition(clawRot.getCurrentPosition() + 144);
                }
                else{
                    clawRot.setTargetPosition(clawRot.getCurrentPosition() - 144);
                }
                clawRot.setPower(1.0);
                clawPos = !clawPos;
            }
            lastB = gamepad1.b;

            if(!clawRot.isBusy()){
                clawRot.setPower(0.0);
            }

            if(gamepad1.x && !lastX){
                claw.setPosition(claw.getPosition() == 0.0 ? 0.0 : 0.0); // TODO: Find good values for claw
            }
            lastX = gamepad1.x;
        }
    }
}
