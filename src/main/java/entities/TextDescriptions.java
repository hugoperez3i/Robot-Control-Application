package entities;

import controllers.Client;

public abstract class TextDescriptions {
    public static final String titleNoMovements = "Warning: No movements configured.";
    public static final String textNoMovements = "Please configure at least one movement before trying to execute movements. In order to configure movements you should select what kind of change you intend to do, and how many degrees you want it to move from the actual position.\n\n\nFor instance, if you wanted to rotate the head another 5 degrees from the current position you'd select the + button, then change the value to 5 and click the \"Execute movements\" button to send the order to the robot.";
    public static final String titleMoveRejected = "Error: Movement order rejected by server.";
    public static final String textMoveRejected = "The movement order was rejected by the server with error code: "+ Client.SERVER_CONNECTION_STATUS.ERR_CODE + ". Please try again, and if the issue presists consider reseting the application.";
    public static final String titleMoveRejectedRobot = "Error: Movement order rejected by robot.";
    public static final String textMoveRejectedRobot = "The movement order was rejected by the remotely controlled robot with error code: "+ Client.SERVER_CONNECTION_STATUS.ERR_CODE + ". Please try again, and if the issue presists consider reseting the application or the robot, if possible.";
    public static final String titleLostConn = "Critical Error: Lost Server Connection.";
    public static final String textLostConn = "The app lost connection with the server and will now terminate. If the issue persists after restart, please check if the server is still running properly.";
    public static final String titleCautionTorso = "Warning: Caution with Torso Servo Movements.";
    public static final String textCautionTorso = "Please, be careful when operating the torso servos as the robot is made out of frail materials, and sudden, big movements can cause torso pieces to snap or break.";
    
    public static final String titleMainGuide = "What do I have to do?";
    public static final String textMainGuide = "In the home page, one must select the area of the robot that wishes to operate. Once selected, a new control pane will appear with all the controls for each of the joints of the area seleted.";
    public static final String titleMovGuide = "How do I move some servo?";
    public static final String textMovGuide = "The \"Current Position\" value of each joint indicates what's the current position of each of the robot joints. In order to generate a movement change, you should select wether you want to increment or decrease this position by selecting the + or - buttons on the right of \"Current Position\" and adding the amount you want this value to change.\nNote that the new position will be the current position with the specified value added or substracted from it.";

    public static final String titleMov_Finger = "Finger Flexion-Extension Movement";
    public static final String textMov_Finger = "On this action, the servo movement will translate into the flexion or extension movements for each of the fingers. ";
    public static final String titleMov_Pam = "Thumb abduction movement";
    public static final String textMov_Pam = "During this movement, the robot will execute an approximation of the thumb adduction movement, moving the thumb closer or further away from the palm.";
    public static final String titleMov_HeadR = "Head Rotation";
    public static final String textMov_HeadR = "On this movement, the robot will rotate its head, simmulating the <todo> movement of humans.";
    public static final String titleMov_HeadT = "Head tilt movement";
    public static final String textMov_HeadT = "Test";
    public static final String titleMov_TorsoT = "Torso tilt movement";
    public static final String textMov_TorsoT = "Test";
    public static final String titleMov_TorsoR = "Shoulder rotation (Circonducction)";
    public static final String textMov_TorsoR = "Test";
    public static final String titleMov_Shoulder = "Shoulder-Arm flexion";
    public static final String textMov_Shoulder = "moving the arms from i pose to T pose";
    public static final String titleMov_ArmFlex = "Arm flexion";
    public static final String textMov_ArmFlex = "biceps flexion";
    public static final String titleMov_ArmRot = "Arm rotation";
    public static final String textMov_ArmRot = "Test";
    public static final String titleMov_Forearm = "Forearm rotation";
    public static final String textMov_Forearm = "Test";
}
