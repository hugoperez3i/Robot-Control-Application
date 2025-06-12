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
    public static final String textCautionTorso = "Please, be careful when operating the torso servos as the robot is made out of fragile materials, and sudden, big movements can cause torso pieces to snap or break.";
    
    /* "GUI" guides */

    public static final String titleMainGuide = "What Do I Have to Do?";
    public static final String textMainGuide = "On the home page, select the part of the robot you want to control. Once selected, a control panel will appear showing all available servo controls for that area.";
    public static final String titleMovGuide = "How Do I Move a Servo?";
    public static final String textMovGuide = "Each joint displays its current position. To adjust it, use the + or - buttons next to the value. Enter the amount you want to add or subtract to the current value.\n Click the \"Execute Movements\" button to send the movement order to the robot.";

    /* Movement guides */
    // Hand
    public static final String titleMov_Finger = "Finger Flexion-Extension";
    public static final String textMov_Finger = "This movement controls the flexion and extension of each finger, simulating grasping and releasing actions.";
    public static final String titleMov_Pam = "Thumb Abduction-Adduction";
    public static final String textMov_Pam = "This action moves the thumb toward or away from the palm, mimicking human thumb abduction and adduction.";
    // Head
    public static final String titleMov_HeadR = "Head Rotation";
    public static final String textMov_HeadR = "This movement rotates the head left and right, simulating human cervical rotation.";
    public static final String titleMov_HeadT = "Head Tilt";
    public static final String textMov_HeadT = "These servos tilt the head diagonally, enabling lateral flexion and forward/backward flexion movement through the combination of both of them.";
    // Torso
    public static final String titleMov_TorsoT = "Torso Tilt";
    public static final String textMov_TorsoT = "Controls diagonal tilting of the torso, allowing lateral bending and forward flexion at the waist through the combination of both of them.";
    public static final String titleMov_TorsoR = "Shoulder Circumduction";
    public static final String textMov_TorsoR = "This movement enables circular motion of the arm at the shoulder, combining flexion, extension, abduction, and rotation.";
    // Arm
    public static final String titleMov_Shoulder = "Shoulder Abduction";
    public static final String textMov_Shoulder = "Lifts the arm away from the torso along the frontal plane.";
    public static final String titleMov_ArmFlex = "Elbow Flexion";
    public static final String textMov_ArmFlex = "Bends the arm at the elbow, simulating a curling motion.";
    public static final String titleMov_ArmRot = "Upper Arm Rotation";
    public static final String textMov_ArmRot = "Rotates the upper arm along its axis, turning the arm inward or outward.";
    public static final String titleMov_Forearm = "Forearm Rotation";
    public static final String textMov_Forearm = "Rotates the forearm to turn the palm up (supination) or down (pronation).";
}
