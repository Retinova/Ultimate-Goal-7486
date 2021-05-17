package org.firstinspires.ftc.teamcode.supers;

import android.view.KeyEvent;

import com.qualcomm.robotcore.hardware.Gamepad;

public class BetterGamepad extends Gamepad {
    private boolean la = false,
                    lb = false,
                    lx = false,
                    ly = false,
                    ldpad_up = false,
                    ldpad_down = false,
                    ldpad_left = false,
                    ldpad_right = false,
                    lguide = false,
                    lstart = false,
                    lback = false,
                    lright_bumper = false,
                    lleft_bumper = false,
                    lleft_stick_button = false,
                    lright_stick_button = false;

    // Override Gamepad update method to also set the previous button state
    // TODO: Update last vars and then do super.update(event);?
    @Override
    public void update(KeyEvent event) {
        setGamepadId(event.getDeviceId());
        setTimestamp(event.getEventTime());

        int key = event.getKeyCode();

        if      (key == KeyEvent.KEYCODE_DPAD_UP){
            ldpad_up = dpad_up;
            dpad_up = pressed(event);
        }
        else if (key == KeyEvent.KEYCODE_DPAD_DOWN){
            ldpad_down = dpad_down;
            dpad_down = pressed(event);
        }
        else if (key == KeyEvent.KEYCODE_DPAD_RIGHT){
            ldpad_right = dpad_right;
            dpad_right = pressed(event);
        }
        else if (key == KeyEvent.KEYCODE_DPAD_LEFT){
            ldpad_left = dpad_left;
            dpad_left = pressed(event);
        }
        else if (key == KeyEvent.KEYCODE_BUTTON_A){
            la = a;
            a = pressed(event);
        }
        else if (key == KeyEvent.KEYCODE_BUTTON_B){
            lb = b;
            b = pressed(event);
        }
        else if (key == KeyEvent.KEYCODE_BUTTON_X){
            lx = x;
            x = pressed(event);
        }
        else if (key == KeyEvent.KEYCODE_BUTTON_Y){
            ly = y;
            y = pressed(event);
        }
        else if (key == KeyEvent.KEYCODE_BUTTON_MODE){
            lguide = guide;
            guide = pressed(event);
        }
        else if (key == KeyEvent.KEYCODE_BUTTON_START){
            lstart = start;
            start = pressed(event);
        }

            // Handle "select" and "back" key codes as a "back" button event.
        else if (key == KeyEvent.KEYCODE_BUTTON_SELECT || key == KeyEvent.KEYCODE_BACK){
            lback = back;
            back = pressed(event);
        }

        else if (key == KeyEvent.KEYCODE_BUTTON_R1){
            lright_bumper = right_bumper;
            right_bumper = pressed(event);
        }
        else if (key == KeyEvent.KEYCODE_BUTTON_L1){
            lleft_bumper = left_bumper;
            left_bumper = pressed(event);
        }
        else if (key == KeyEvent.KEYCODE_BUTTON_THUMBL){
            lleft_stick_button = left_stick_button;
            left_stick_button = pressed(event);
        }
        else if (key == KeyEvent.KEYCODE_BUTTON_THUMBR){
            lright_stick_button = right_stick_button;
            right_stick_button = pressed(event);
        }

        updateButtonAliases();
        callCallback();
    }

    public boolean aPressed(){
        return a && !la;
    }
    public boolean bPressed(){
        return b && !lb;
    }
    public boolean xPressed(){
        return x && !lx;
    }
    public boolean yPressed(){
        return y && !ly;
    }
    public boolean dpad_upPressed(){
        return dpad_up && !ldpad_up;
    }
    public boolean dpad_downPressed(){
        return dpad_down && !ldpad_down;
    }
    public boolean dpad_rightPressed(){
        return dpad_right && !ldpad_right;
    }
    public boolean dpad_leftPressed(){
        return dpad_left && !ldpad_left;
    }
    public boolean guidePressed(){
        return guide && !lguide;
    }
    public boolean startPressed(){
        return start && !lstart;
    }
    public boolean backPressed(){
        return back && !lback;
    }
    public boolean right_bumperPressed(){
        return right_bumper && !lright_bumper;
    }
    public boolean left_bumperPressed(){
        return left_bumper && !lleft_bumper;
    }
    public boolean right_stick_buttonPressed(){
        return right_stick_button && !lright_stick_button;
    }
    public boolean left_stick_buttonPressed(){
        return left_stick_button && !lleft_stick_button;
    }
}
