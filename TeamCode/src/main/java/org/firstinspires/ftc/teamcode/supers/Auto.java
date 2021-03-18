package org.firstinspires.ftc.teamcode.supers;

public class Auto {
    private Robot r;

    private boolean clawPos = false;

    public Auto(Robot robot){
        this.r = robot;
    }

    r = new Robot(this, Mode.AUTO);
    waitForStart();

    while(isStarted() && !isStopRequested()){
        r.clawRot.setTargetPosition(r.clawRot.getCurrentPosition() + 144);

        r.clawRot.setPower(1.0);
        clawPos = !clawPos;

        break

    }
}
