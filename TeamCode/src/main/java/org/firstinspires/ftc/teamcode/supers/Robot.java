package org.firstinspires.ftc.teamcode.supers;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Robot {
    public final DcMotor lf, lb, rf, rb, out1, out2, in, clawRot;
    public final Servo claw, platform;

    public final AnalogInput pAngle;

    public final BNO055IMU imu;
    private final BNO055IMU.Parameters params = new BNO055IMU.Parameters();

    public Auto auto;
    public TeleOp tele;

    public final LinearOpMode opMode;
    public final HardwareMap hwMap;


    public Robot(LinearOpMode opMode, Mode mode){
        this.opMode = opMode;
        this.hwMap = opMode.hardwareMap;

        Globals.opMode = opMode;
        Globals.hwMap = opMode.hardwareMap;

        lf = hwMap.get(DcMotor.class, "lf");
        lb = hwMap.get(DcMotor.class, "lb");
        rf = hwMap.get(DcMotor.class, "rf");
        rb = hwMap.get(DcMotor.class, "rb");
        out1 = hwMap.get(DcMotor.class, "out1");
        out2 = hwMap.get(DcMotor.class, "out2");
        in = hwMap.get(DcMotor.class, "in");
        clawRot = hwMap.get(DcMotor.class, "clawRot");

        claw = hwMap.get(Servo.class, "claw");
        platform = hwMap.get(Servo.class, "platform");

        pAngle = hwMap.get(AnalogInput.class, "pAngle");

        imu = hwMap.get(BNO055IMU.class, "imu");
        params.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        params.calibrationDataFile = "BNO055IMUCalibration.json";

        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        lb.setDirection(DcMotorSimple.Direction.REVERSE);
        // TODO: Reverse one of the output motors, test for which one

        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        out1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        out2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        in.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        clawRot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        if(mode == Mode.AUTO){
            auto = new Auto(this);
            initGyro();
        }
        else tele = new TeleOp(this);
    }

    public void initCheck(){
        Globals.opMode.telemetry.addData(">", " Initialized");
        Globals.opMode.telemetry.update();
    }

    private void initGyro(){
        while(imu.getSystemStatus() != BNO055IMU.SystemStatus.RUNNING_FUSION && !Globals.opMode.isStopRequested()) {
            imu.initialize(params);
            opMode.telemetry.addData("Status: ", imu.getSystemStatus());
            opMode.telemetry.update();
        }
        opMode.telemetry.addData("status; ", imu.getSystemStatus());
    }
}
