package org.firstinspires.ftc.teamcode.supers;

public class Auto {
    private Robot r;

    private boolean clawPos = false;

    public Auto(Robot robot){
        this.r = robot;
    }
    public void claw(){

       if(clawPos)
        r.clawRot.setTargetPosition(r.clawRot.getCurrentPosition() + 144);
        else
        r.clawRot.setTargetPosition(r.clawRot.getCurrentPosition() - 144);

        r.clawRot .setPower(1.0);
        while(r.opMode.opModeIsActive() && r.clawRot.isBusy()){

            r.opMode.telemetry.addData("Current Position: ",r.clawRot.getCurrentPosition());
            r.opMode.telemetry.update();

        }
        r.clawRot.setPower(0.0);

        clawPos = !clawPos;
    }
    public void shoot(){
        r.out1.setPower(1.0);
        r.out2.setPower(1.0);
        r.opMode.sleep(1000); //<---- can adjust time
        r.out1.setPower(0.0);
        r.out2.setPower(0.0);
    }

}
