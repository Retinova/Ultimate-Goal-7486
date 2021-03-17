package org.firstinspires.ftc.teamcode.supers;

public class Auto {
    private Robot r;

    private boolean clawPos = false;

    public Auto(Robot robot){
        this.r = robot;
    }

    robot = new Robot(this, Mode.Auto);
    waitForStart();

    while(isStarted() && !isStopRequested()){
        robot.clawRot.setTargetPosition(robot.clawRot.getCurrentPosition() + 144);

        robot.clawRot.setPower(1.0);
        clawPos = !clawPos;

    }
}
