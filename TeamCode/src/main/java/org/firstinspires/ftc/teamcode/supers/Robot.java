package org.firstinspires.ftc.teamcode.supers;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Robot {
    public final DcMotorEx lf, lb, rf, rb, out1, out2, in, clawRot;
    public final Servo claw;

    public final BNO055IMU imu;
    private final BNO055IMU.Parameters params = new BNO055IMU.Parameters();

    public Auto auto;
    public TeleOp tele;

    private final LinearOpMode opMode;
    private final HardwareMap hwMap;


    public Robot(LinearOpMode opMode, Mode mode){
        this.opMode = opMode;
        this.hwMap = opMode.hardwareMap;

        Globals.opMode = opMode;
        Globals.hwMap = opMode.hardwareMap;

        lf = hwMap.get(DcMotorEx.class, "lf");
        lb = hwMap.get(DcMotorEx.class, "lb");
        rf = hwMap.get(DcMotorEx.class, "rf");
        rb = hwMap.get(DcMotorEx.class, "rb");
        out1 = hwMap.get(DcMotorEx.class, "out1");
        out2 = hwMap.get(DcMotorEx.class, "out2");
        in = hwMap.get(DcMotorEx.class, "in");
        clawRot = hwMap.get(DcMotorEx.class, "clawRot");

        claw = hwMap.get(Servo.class, "claw");

        imu = hwMap.get(BNO055IMU.class, "imu");
        params.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        params.calibrationDataFile = "BNO055IMUCalibration.json";

        if(mode == Mode.AUTO){
            auto = new Auto(this);
        }
        else tele = new TeleOp(this);
    }

    public void initCheck(){
        Globals.opMode.telemetry.addData(">", " Initialized");
        Globals.opMode.telemetry.update();
    }

    public void initGyro(){
        imu.initialize(params);
    }
}
