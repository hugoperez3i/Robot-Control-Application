package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import controllers.Client;
import entities.ServoIDs;
import entities.TextDescriptions;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ClientController implements Initializable{
    
    private int[] targetmovements = new int[27];
    private boolean[] positiveMovements = new boolean[27];
    private char currentScreen=0;
    private boolean showGroupExecution=false;
    private boolean SYNC_IN_PROCESS=false;

    @FXML
    public Pane paneSelectMovementMenu;

    @FXML
    public Text textInformation;
    @FXML
    public Button buttonBackToMVSelect;

    private ArrayList<char[]> getMovements(){
        ArrayList<char[]> mList=new ArrayList<char[]>();
        for (char i = 0; i < targetmovements.length; i++) {
            if(targetmovements[i]!=0){
                char[] ca = new char[2];
                ca[0]=i;
                if(positiveMovements[i]){
                    ca[1]=(char)(Client.currentpositions[i]+targetmovements[i]);
                }else{
                    ca[1]=(char)(Client.currentpositions[i]-targetmovements[i]);
                }
                mList.add(ca);
            }
        }
        return mList;
    }

    public void backToMVSelect(){
        hideall();
        currentScreen=0;
        textInformation.setText("Robot Control Panel");
        paneSelectMovementMenu.setDisable(false);
        paneSelectMovementMenu.setVisible(true);
    }

    @FXML
    public Pane paneInfoMSG;

        @FXML 
        public Text textInfoMSG;
        @FXML 
        public Text textInfoDescription;
        @FXML
        public Button buttonInfoMSG;

        @FXML
        public ImageView imgErr;
        @FXML
        public ImageView imgWarn;

            private void swapToWarn(){
                imgErr.setVisible(false);
                imgErr.setDisable(true);
                imgWarn.setVisible(true);
                imgWarn.setDisable(false);
            }

            private void swapToErr(){
                imgWarn.setVisible(false);
                imgWarn.setDisable(true);
                imgErr.setVisible(true);
                imgErr.setDisable(false);
            }

        private boolean SHUTDOWN_FLAG = false;

        public void closeInfoMSG(){
            paneInfoMSG.setDisable(true);
            paneInfoMSG.setVisible(false);
            buttonInfoMSG.setCancelButton(false);
            buttonBackToMVSelect.setDisable(false);
            buttonBackToMVSelect.setCancelButton(true);

            if(SHUTDOWN_FLAG){System.exit(0);}
        }

        /**
         * When called displays a message with error/warning information
         * <p> {@code 0} WARNING: No selected movements
         * <p> {@code 1} ERROR: Movement order rejected by server
         * <p> {@code 2} ERROR: Movement order rejected by robot
         * <p> {@code 3} ERROR: Server connection lost 
         * <p> {@code 4} WARNING: Torso movement warn
         * <p> {@code X} keep adding stuff here...
         *
         * @param type - int
         * @throws IOException
         */
        private void showInfoMSG(int type){
            buttonBackToMVSelect.setCancelButton(false);
            buttonBackToMVSelect.setDisable(true);
            buttonInfoMSG.setCancelButton(true);
            switch (type) {
                case 0: // No selected movements
                    swapToWarn();
                    textInfoMSG.setText(TextDescriptions.titleNoMovements);
                    textInfoDescription.setText(TextDescriptions.textNoMovements);
                    break;
                case 1: // Movement order rejected by server
                    swapToErr();
                    textInfoMSG.setText(TextDescriptions.titleMoveRejected);
                    textInfoDescription.setText(TextDescriptions.textMoveRejected);
                    swtichBackToPreviousScreen();
                    break;
                case 2: // Movement execution failed
                    swapToErr();
                    textInfoMSG.setText(TextDescriptions.titleMoveRejectedRobot);
                    textInfoDescription.setText(TextDescriptions.textMoveRejectedRobot);
                    clientSyncServoPositions();            
                    break;
                case 3: // Server connection lost
                    swapToErr();
                    textInfoMSG.setText(TextDescriptions.titleLostConn);
                    textInfoDescription.setText(TextDescriptions.textLostConn);
                    SHUTDOWN_FLAG=true;
                    break;
                case 4: // Be careful with torso movements
                    swapToWarn();
                    textInfoMSG.setText(TextDescriptions.titleCautionTorso);
                    textInfoDescription.setText(TextDescriptions.textCautionTorso);
                    break;
                default:
                    break;
            }
            paneInfoMSG.setDisable(false);
            paneInfoMSG.setVisible(true);            
        }

    @FXML
    public Text textInvalidAction;
        private FadeTransition fadeOut;

        private void showErrTxt(){
            textInvalidAction.setVisible(true);
            textInvalidAction.setOpacity(1.0);
            fadeOut.playFromStart();
        }

    @FXML
    public Pane paneTutorial;

        @FXML
        public Button buttonTutorial;
        @FXML 
        public Text textTutorialT;
        @FXML 
        public Text textTutorial;        

        private boolean tutorialUp=false;

        public void hideTutorial(){
            tutorialUp=false;
            paneTutorial.setDisable(true);
            paneTutorial.setVisible(false);
            buttonTutorial.setCancelButton(false);
            buttonBackToMVSelect.setDisable(false);
            buttonBackToMVSelect.setCancelButton(true);
        }

        public void showTutorial(){
            if(tutorialUp){hideTutorial();return;}
            buttonBackToMVSelect.setCancelButton(false);
            buttonBackToMVSelect.setDisable(true);
            buttonTutorial.setCancelButton(true);

            if(currentScreen==0){
                textTutorialT.setText(TextDescriptions.titleMainGuide);
                textTutorial.setText(TextDescriptions.textMainGuide);
            }else{
                textTutorialT.setText(TextDescriptions.titleMovGuide);
                textTutorial.setText(TextDescriptions.textMovGuide);
            }

            paneTutorial.setDisable(false);
            paneTutorial.setVisible(true);   
            tutorialUp=true;
        }

    @FXML
    public Pane paneMovementE;

        @FXML
        public Button buttonMovementE;
        @FXML 
        public Text textMovementET;
        @FXML 
        public Text textMovementE;   
        @FXML
        public ImageView imgViewMov;

        public void hideMovementExp(){
            paneMovementE.setDisable(true);
            paneMovementE.setVisible(false);
            buttonMovementE.setCancelButton(false);
            buttonBackToMVSelect.setDisable(false);
            buttonBackToMVSelect.setCancelButton(true);
        }

        public void showFingerMovTutorial(){
            buttonBackToMVSelect.setCancelButton(false);
            buttonBackToMVSelect.setDisable(true);
            buttonMovementE.setCancelButton(true);

            textMovementET.setText(TextDescriptions.titleMov_Finger);
            textMovementE.setText(TextDescriptions.textMov_Finger);
            imgViewMov.setImage(new Image("/img/fingerMov.png"));

            paneMovementE.setDisable(false);
            paneMovementE.setVisible(true);
        }        
        
        public void showPalmMovTutorial(){
            buttonBackToMVSelect.setCancelButton(false);
            buttonBackToMVSelect.setDisable(true);
            buttonMovementE.setCancelButton(true);

            textMovementET.setText(TextDescriptions.titleMov_Pam);
            textMovementE.setText(TextDescriptions.textMov_Pam);
            imgViewMov.setImage(new Image("/img/palmMov.png"));

            paneMovementE.setDisable(false);
            paneMovementE.setVisible(true);
        }        
        
        public void showHeadTMovTutorial(){
            buttonBackToMVSelect.setCancelButton(false);
            buttonBackToMVSelect.setDisable(true);
            buttonMovementE.setCancelButton(true);

            textMovementET.setText(TextDescriptions.titleMov_HeadT);
            textMovementE.setText(TextDescriptions.textMov_HeadT);
            imgViewMov.setImage(new Image("/img/headTiltMov.png"));

            paneMovementE.setDisable(false);
            paneMovementE.setVisible(true);
        }        
        
        public void showHeadRotMovTutorial(){
            buttonBackToMVSelect.setCancelButton(false);
            buttonBackToMVSelect.setDisable(true);
            buttonMovementE.setCancelButton(true);

            textMovementET.setText(TextDescriptions.titleMov_HeadR);
            textMovementE.setText(TextDescriptions.textMov_HeadR);
            imgViewMov.setImage(new Image("/img/headRotationMov.png"));

            paneMovementE.setDisable(false);
            paneMovementE.setVisible(true);
        }        
        
        public void showTorsoTMovTutorial(){
            buttonBackToMVSelect.setCancelButton(false);
            buttonBackToMVSelect.setDisable(true);
            buttonMovementE.setCancelButton(true);

            textMovementET.setText(TextDescriptions.titleMov_TorsoT);
            textMovementE.setText(TextDescriptions.textMov_TorsoT);
            imgViewMov.setImage(new Image("/img/torsoTiltMov.png"));

            paneMovementE.setDisable(false);
            paneMovementE.setVisible(true);
        }        
        
        public void showTorsoRMovTutorial(){
            buttonBackToMVSelect.setCancelButton(false);
            buttonBackToMVSelect.setDisable(true);
            buttonMovementE.setCancelButton(true);

            textMovementET.setText(TextDescriptions.titleMov_TorsoR);
            textMovementE.setText(TextDescriptions.textMov_TorsoR);
            imgViewMov.setImage(new Image("/img/torsoRotMov.png"));

            paneMovementE.setDisable(false);
            paneMovementE.setVisible(true);
        }        
        
        public void showShoulderMovTutorial(){
            buttonBackToMVSelect.setCancelButton(false);
            buttonBackToMVSelect.setDisable(true);
            buttonMovementE.setCancelButton(true);

            textMovementET.setText(TextDescriptions.titleMov_Shoulder);
            textMovementE.setText(TextDescriptions.textMov_Shoulder);
            imgViewMov.setImage(new Image("/img/shoulderMov.png"));

            paneMovementE.setDisable(false);
            paneMovementE.setVisible(true);
        }        

        public void showArmFMovTutorial(){
            buttonBackToMVSelect.setCancelButton(false);
            buttonBackToMVSelect.setDisable(true);
            buttonMovementE.setCancelButton(true);

            textMovementET.setText(TextDescriptions.titleMov_ArmFlex);
            textMovementE.setText(TextDescriptions.textMov_ArmFlex);
            imgViewMov.setImage(new Image("/img/armFlexMov.png"));

            paneMovementE.setDisable(false);
            paneMovementE.setVisible(true);
        }        

        public void showArmRMovTutorial(){
            buttonBackToMVSelect.setCancelButton(false);
            buttonBackToMVSelect.setDisable(true);
            buttonMovementE.setCancelButton(true);

            textMovementET.setText(TextDescriptions.titleMov_ArmRot);
            textMovementE.setText(TextDescriptions.textMov_ArmFlex);
            imgViewMov.setImage(new Image("/img/armRotMov.png"));

            paneMovementE.setDisable(false);
            paneMovementE.setVisible(true);
        }        

        public void showForearMovTutorial(){
            buttonBackToMVSelect.setCancelButton(false);
            buttonBackToMVSelect.setDisable(true);
            buttonMovementE.setCancelButton(true);

            textMovementET.setText(TextDescriptions.titleMov_Forearm);
            textMovementE.setText(TextDescriptions.textMov_Forearm);
            imgViewMov.setImage(new Image("/img/forearmMov.png"));

            paneMovementE.setDisable(false);
            paneMovementE.setVisible(true);
        }        

    @FXML
    public Button buttonExecuteMovement;

        public void executeMovement(){

            buttonExecuteMovement.setDisable(true);
            ArrayList<char[]> ml = getMovements();
            if(ml.isEmpty()){
                showInfoMSG(0);
                buttonExecuteMovement.setDisable(false);
                return;
            }

            buttonBackToMVSelect.setDisable(true);
            paneSendingMVOrders.setDisable(false);
            paneSendingMVOrders.setVisible(true);
            
            /* TASK -> Send the movement */
            new Thread() {
                public void run() {

                    try {
                        Client.executeMovement(ml);
                    } catch (Exception e) {
                        Client.SERVER_CONNECTION_STATUS.ERR=true;
                        e.printStackTrace();
                    }

                }
            }.start();

            /* TASK -> update GUI with cli codes */
            new Thread() {
                public void run() {

                    do{ /* Wait for server responses and update GUI accordingly */
                        try {Thread.sleep(40);} catch (InterruptedException e) {e.printStackTrace();}
                        if(Client.SERVER_CONNECTION_STATUS.ERR){
                            showInfoMSG(1);
                            Client.SERVER_CONNECTION_STATUS.ERR_CODE=0;
                            Client.SERVER_CONNECTION_STATUS.MV_ORDER_SENT=false;
                            Client.SERVER_CONNECTION_STATUS.ERR=false; 
                            return;
                        }
                    } while(!Client.SERVER_CONNECTION_STATUS.MV_ORDER_ACCEPTED);

                    /* "Free" the GUI and show the "In execution texts" */

                    showGroupExecution=true;

                    swtichBackToPreviousScreen();
                    
                    groupExecution.setVisible(true);
                    groupExecution.setDisable(false);

                    char c=0; String dots="";
                    do{ 
                        try {Thread.sleep(400);} catch (InterruptedException e) {e.printStackTrace();}
                        dots="";for (int i = 0; i < c; i++) { dots+=".";}c++;
                        textExectuion.setText("The robot is currently executing a movement"+dots);
                        if(c==4){c=0;}
                        if(Client.SERVER_CONNECTION_STATUS.ERR){
                            showInfoMSG(2);
                            Client.SERVER_CONNECTION_STATUS.MV_ORDER_SENT=false;
                            Client.SERVER_CONNECTION_STATUS.MV_ORDER_ACCEPTED=false;
                            Client.SERVER_CONNECTION_STATUS.ERR=false; 
                            break;
                        }
                    }while(!Client.SERVER_CONNECTION_STATUS.MV_ORDER_COMPLETED);

                    showGroupExecution=false;

                    Client.SERVER_CONNECTION_STATUS.MV_ORDER_SENT=false;
                    Client.SERVER_CONNECTION_STATUS.MV_ORDER_ACCEPTED=false;
                    Client.SERVER_CONNECTION_STATUS.MV_ORDER_COMPLETED=false;
                    Client.SERVER_CONNECTION_STATUS.ERR=false; 
                                            
                    groupExecution.setVisible(false);
                    groupExecution.setDisable(true);
                }
            }.start();

            
        }

    public void syncClient(){
        if(!SYNC_IN_PROCESS){
            clientSyncServoPositions();
            backToMVSelect();
        }
    }

        private void clientSyncServoPositions(){
            new Thread() {
                public void run() {
                    SYNC_IN_PROCESS=true;
                    try {
                        Client.updateCurrentPositions();
                    } catch (Exception e) {
                        e.printStackTrace();
                        showInfoMSG(3);
                    }
                    SYNC_IN_PROCESS=false;
                }
            }.start();
        }

    @FXML
    public Pane paneSendingMVOrders;

        @FXML
        public Group groupExecution; 
        @FXML
        public ProgressIndicator pIndicatorExecution;
        @FXML
        public Text textExectuion; 

        /* TODO Head GUI things */
    @FXML
    public Pane paneMovementHead;

        public void swapToMoveHead(){
            hideall();
            currentScreen=1;
            textInformation.setText("Head Servo Controls");
            setDefaultPMbuttons_head();

            textCP_h_tilt_L.setText(String.valueOf(Client.currentpositions[ServoIDs.HEAD_TILT_L])+"º");
            textCP_h_tilt_R.setText(String.valueOf(Client.currentpositions[ServoIDs.HEAD_TILT_R])+"º");
            textCP_h_rot.setText(String.valueOf(Client.currentpositions[ServoIDs.HEAD_ROTATION]+"º"));
            spinner_h_tilt_L.getValueFactory().setValue(targetmovements[ServoIDs.HEAD_TILT_L]);
            spinner_h_tilt_R.getValueFactory().setValue(targetmovements[ServoIDs.HEAD_TILT_R]);
            spinner_h_rot.getValueFactory().setValue(targetmovements[ServoIDs.HEAD_ROTATION]);

            buttonBackToMVSelect.setDisable(false);
            buttonBackToMVSelect.setVisible(true);

            buttonExecuteMovement.setVisible(true);
            buttonExecuteMovement.setDisable(false);

            if(showGroupExecution){
                groupExecution.setVisible(true);
                groupExecution.setDisable(false);                
            }

            paneMovementHead.setDisable(false);
            paneMovementHead.setVisible(true);
        }

        @FXML
        public Text textCP_h_rot;
        @FXML
        public Spinner<Integer> spinner_h_rot;
        @FXML
        public Button buttonPlus_h_rot;
        @FXML
        public Button buttonPlus_h_rot_selected;
        @FXML
        public Button buttonMinus_h_rot;
        @FXML
        public Button buttonMinus_h_rot_selected;

            public void changeToPositive_h_rot(){
                positiveMovements[ServoIDs.HEAD_ROTATION]=true;
                buttonMinus_h_rot_selected.setVisible(false);
                buttonMinus_h_rot_selected.setDisable(true);
                buttonPlus_h_rot_selected.setVisible(true);
                buttonPlus_h_rot_selected.setDisable(false);
                buttonMinus_h_rot.setVisible(true);
                buttonMinus_h_rot.setDisable(false);
                buttonPlus_h_rot.setVisible(false);
                buttonPlus_h_rot.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.HEAD_ROTATION] + targetmovements[ServoIDs.HEAD_ROTATION] >180){
                    targetmovements[ServoIDs.HEAD_ROTATION]=180-Client.currentpositions[15];
                    spinner_h_rot.getValueFactory().setValue(targetmovements[ServoIDs.HEAD_ROTATION]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_h_rot(){
                positiveMovements[ServoIDs.HEAD_ROTATION]=false;
                buttonMinus_h_rot_selected.setVisible(true);
                buttonMinus_h_rot_selected.setDisable(false);
                buttonPlus_h_rot_selected.setVisible(false);
                buttonPlus_h_rot_selected.setDisable(true);
                buttonPlus_h_rot.setVisible(true);
                buttonPlus_h_rot.setDisable(false);
                buttonMinus_h_rot.setVisible(false);
                buttonMinus_h_rot.setDisable(true);
                
                if(targetmovements[ServoIDs.HEAD_ROTATION] >= Client.currentpositions[ServoIDs.HEAD_ROTATION]){
                    targetmovements[ServoIDs.HEAD_ROTATION]=Client.currentpositions[ServoIDs.HEAD_ROTATION]-1;
                    spinner_h_rot.getValueFactory().setValue(targetmovements[ServoIDs.HEAD_ROTATION]);
                    showErrTxt();
                }

            }

        @FXML
        public Text textCP_h_tilt_L;
        @FXML
        public Spinner<Integer> spinner_h_tilt_L;
        @FXML
        public Button buttonPlus_h_tilt_L;
        @FXML
        public Button buttonPlus_h_tilt_L_selected;
        @FXML
        public Button buttonMinus_h_tilt_L;
        @FXML
        public Button buttonMinus_h_tilt_L_selected;

            public void changeToPositive_h_tilt_L(){
                positiveMovements[ServoIDs.HEAD_TILT_L]=true;
                buttonMinus_h_tilt_L_selected.setVisible(false);
                buttonMinus_h_tilt_L_selected.setDisable(true);
                buttonPlus_h_tilt_L_selected.setVisible(true);
                buttonPlus_h_tilt_L_selected.setDisable(false);
                buttonMinus_h_tilt_L.setVisible(true);
                buttonMinus_h_tilt_L.setDisable(false);
                buttonPlus_h_tilt_L.setVisible(false);
                buttonPlus_h_tilt_L.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.HEAD_TILT_L] + targetmovements[ServoIDs.HEAD_TILT_L] >180){
                    targetmovements[ServoIDs.HEAD_TILT_L]=180-Client.currentpositions[ServoIDs.HEAD_TILT_L];
                    spinner_h_tilt_L.getValueFactory().setValue(targetmovements[ServoIDs.HEAD_TILT_L]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_h_tilt_L(){
                positiveMovements[ServoIDs.HEAD_TILT_L]=false;
                buttonMinus_h_tilt_L_selected.setVisible(true);
                buttonMinus_h_tilt_L_selected.setDisable(false);
                buttonPlus_h_tilt_L_selected.setVisible(false);
                buttonPlus_h_tilt_L_selected.setDisable(true);
                buttonPlus_h_tilt_L.setVisible(true);
                buttonPlus_h_tilt_L.setDisable(false);
                buttonMinus_h_tilt_L.setVisible(false);
                buttonMinus_h_tilt_L.setDisable(true);
                
                if(targetmovements[ServoIDs.HEAD_TILT_L] >= Client.currentpositions[ServoIDs.HEAD_TILT_L]){
                    targetmovements[ServoIDs.HEAD_TILT_L]=Client.currentpositions[ServoIDs.HEAD_TILT_L]-1;
                    spinner_h_tilt_L.getValueFactory().setValue(targetmovements[ServoIDs.HEAD_TILT_L]);
                    showErrTxt();
                }

            }

        @FXML
        public Text textCP_h_tilt_R;
        @FXML
        public Spinner<Integer> spinner_h_tilt_R;
        @FXML
        public Button buttonPlus_h_tilt_R;
        @FXML
        public Button buttonPlus_h_tilt_R_selected;
        @FXML
        public Button buttonMinus_h_tilt_R;
        @FXML
        public Button buttonMinus_h_tilt_R_selected;

            public void changeToPositive_h_tilt_R(){
                positiveMovements[ServoIDs.HEAD_TILT_R]=true;
                buttonMinus_h_tilt_R_selected.setVisible(false);
                buttonMinus_h_tilt_R_selected.setDisable(true);
                buttonPlus_h_tilt_R_selected.setVisible(true);
                buttonPlus_h_tilt_R_selected.setDisable(false);
                buttonMinus_h_tilt_R.setVisible(true);
                buttonMinus_h_tilt_R.setDisable(false);
                buttonPlus_h_tilt_R.setVisible(false);
                buttonPlus_h_tilt_R.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.HEAD_TILT_R] + targetmovements[ServoIDs.HEAD_TILT_R] >180){
                    targetmovements[ServoIDs.HEAD_TILT_R]=180-Client.currentpositions[ServoIDs.HEAD_TILT_R];
                    spinner_h_tilt_R.getValueFactory().setValue(targetmovements[ServoIDs.HEAD_TILT_R]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_h_tilt_R(){
                positiveMovements[ServoIDs.HEAD_TILT_R]=false;
                buttonMinus_h_tilt_R_selected.setVisible(true);
                buttonMinus_h_tilt_R_selected.setDisable(false);
                buttonPlus_h_tilt_R_selected.setVisible(false);
                buttonPlus_h_tilt_R_selected.setDisable(true);
                buttonPlus_h_tilt_R.setVisible(true);
                buttonPlus_h_tilt_R.setDisable(false);
                buttonMinus_h_tilt_R.setVisible(false);
                buttonMinus_h_tilt_R.setDisable(true);
                
                if(targetmovements[ServoIDs.HEAD_TILT_R] >= Client.currentpositions[ServoIDs.HEAD_TILT_R]){
                    targetmovements[ServoIDs.HEAD_TILT_R]=Client.currentpositions[ServoIDs.HEAD_TILT_R]-1;
                    spinner_h_tilt_R.getValueFactory().setValue(targetmovements[ServoIDs.HEAD_TILT_R]);
                    showErrTxt();
                }

            }
                
            /* TODO Torso GUI things */
    @FXML
    public Pane paneMovementTorso;

        public void swapToMoveTorso(){
            hideall();
            currentScreen=2;
            textInformation.setText("Torso Servo Controls");
            setDefaultPMbuttons_torso();

            textCP_t_tilt_L.setText(String.valueOf(Client.currentpositions[ServoIDs.TORSO_TILT_L])+"º");
            textCP_t_tilt_R.setText(String.valueOf(Client.currentpositions[ServoIDs.TORSO_TILT_R])+"º");
            textCP_t_rot_L.setText(String.valueOf(Client.currentpositions[ServoIDs.SHOULDER_ROTATION_L]+"º"));
            textCP_t_rot_R.setText(String.valueOf(Client.currentpositions[ServoIDs.SHOULDER_ROTATION_R]+"º"));
            spinner_t_tilt_L.getValueFactory().setValue(targetmovements[ServoIDs.TORSO_TILT_L]);
            spinner_t_tilt_R.getValueFactory().setValue(targetmovements[ServoIDs.TORSO_TILT_R]);
            spinner_t_rot_L.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_ROTATION_L]);
            spinner_t_rot_R.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_ROTATION_R]);

            buttonBackToMVSelect.setDisable(false);
            buttonBackToMVSelect.setVisible(true);
            buttonExecuteMovement.setVisible(true);
            buttonExecuteMovement.setDisable(false);

            if(showGroupExecution){
                groupExecution.setVisible(true);
                groupExecution.setDisable(false);                
            }

            paneMovementTorso.setDisable(false);
            paneMovementTorso.setVisible(true);
            showInfoMSG(4);
        }

        @FXML
        public Text textCP_t_tilt_L;
        @FXML
        public Spinner<Integer> spinner_t_tilt_L;
        @FXML
        public Button buttonPlus_t_tilt_L;
        @FXML
        public Button buttonPlus_t_tilt_L_selected;
        @FXML
        public Button buttonMinus_t_tilt_L;
        @FXML
        public Button buttonMinus_t_tilt_L_selected;

            public void changeToPositive_t_tilt_L(){
                positiveMovements[ServoIDs.TORSO_TILT_L]=true;
                buttonMinus_t_tilt_L_selected.setVisible(false);
                buttonMinus_t_tilt_L_selected.setDisable(true);
                buttonPlus_t_tilt_L_selected.setVisible(true);
                buttonPlus_t_tilt_L_selected.setDisable(false);
                buttonMinus_t_tilt_L.setVisible(true);
                buttonMinus_t_tilt_L.setDisable(false);
                buttonPlus_t_tilt_L.setVisible(false);
                buttonPlus_t_tilt_L.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.TORSO_TILT_L] + targetmovements[ServoIDs.TORSO_TILT_L] >180){
                    targetmovements[ServoIDs.TORSO_TILT_L]=180-Client.currentpositions[ServoIDs.TORSO_TILT_L];
                    spinner_t_tilt_L.getValueFactory().setValue(targetmovements[ServoIDs.TORSO_TILT_L]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_t_tilt_L(){
                positiveMovements[ServoIDs.TORSO_TILT_L]=false;
                buttonMinus_t_tilt_L_selected.setVisible(true);
                buttonMinus_t_tilt_L_selected.setDisable(false);
                buttonPlus_t_tilt_L_selected.setVisible(false);
                buttonPlus_t_tilt_L_selected.setDisable(true);
                buttonPlus_t_tilt_L.setVisible(true);
                buttonPlus_t_tilt_L.setDisable(false);
                buttonMinus_t_tilt_L.setVisible(false);
                buttonMinus_t_tilt_L.setDisable(true);
                
                if(targetmovements[ServoIDs.TORSO_TILT_L] >= Client.currentpositions[ServoIDs.TORSO_TILT_L]){
                    targetmovements[ServoIDs.TORSO_TILT_L]=Client.currentpositions[ServoIDs.TORSO_TILT_L]-1;
                    spinner_t_tilt_L.getValueFactory().setValue(targetmovements[ServoIDs.TORSO_TILT_L]);
                    showErrTxt();
                }

            }

        @FXML
        public Text textCP_t_tilt_R;
        @FXML
        public Spinner<Integer> spinner_t_tilt_R;
        @FXML
        public Button buttonPlus_t_tilt_R;
        @FXML
        public Button buttonPlus_t_tilt_R_selected;
        @FXML
        public Button buttonMinus_t_tilt_R;
        @FXML
        public Button buttonMinus_t_tilt_R_selected;

            public void changeToPositive_t_tilt_R(){
                positiveMovements[ServoIDs.TORSO_TILT_R]=true;
                buttonMinus_t_tilt_R_selected.setVisible(false);
                buttonMinus_t_tilt_R_selected.setDisable(true);
                buttonPlus_t_tilt_R_selected.setVisible(true);
                buttonPlus_t_tilt_R_selected.setDisable(false);
                buttonMinus_t_tilt_R.setVisible(true);
                buttonMinus_t_tilt_R.setDisable(false);
                buttonPlus_t_tilt_R.setVisible(false);
                buttonPlus_t_tilt_R.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.TORSO_TILT_R] + targetmovements[ServoIDs.TORSO_TILT_R] >180){
                    targetmovements[ServoIDs.TORSO_TILT_R]=180-Client.currentpositions[ServoIDs.TORSO_TILT_R];
                    spinner_t_tilt_R.getValueFactory().setValue(targetmovements[ServoIDs.TORSO_TILT_R]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_t_tilt_R(){
                positiveMovements[ServoIDs.TORSO_TILT_R]=false;
                buttonMinus_t_tilt_R_selected.setVisible(true);
                buttonMinus_t_tilt_R_selected.setDisable(false);
                buttonPlus_t_tilt_R_selected.setVisible(false);
                buttonPlus_t_tilt_R_selected.setDisable(true);
                buttonPlus_t_tilt_R.setVisible(true);
                buttonPlus_t_tilt_R.setDisable(false);
                buttonMinus_t_tilt_R.setVisible(false);
                buttonMinus_t_tilt_R.setDisable(true);
                
                if(targetmovements[ServoIDs.TORSO_TILT_R] >= Client.currentpositions[ServoIDs.TORSO_TILT_R]){
                    targetmovements[ServoIDs.TORSO_TILT_R]=Client.currentpositions[ServoIDs.TORSO_TILT_R]-1;
                    spinner_t_tilt_R.getValueFactory().setValue(targetmovements[ServoIDs.TORSO_TILT_R]);
                    showErrTxt();
                }

            }

        @FXML
        public Text textCP_t_rot_L;
        @FXML
        public Spinner<Integer> spinner_t_rot_L;
        @FXML
        public Button buttonPlus_t_rot_L;
        @FXML
        public Button buttonPlus_t_rot_L_selected;
        @FXML
        public Button buttonMinus_t_rot_L;
        @FXML
        public Button buttonMinus_t_rot_L_selected;

            public void changeToPositive_t_rot_L(){
                positiveMovements[ServoIDs.SHOULDER_ROTATION_L]=true;
                buttonMinus_t_rot_L_selected.setVisible(false);
                buttonMinus_t_rot_L_selected.setDisable(true);
                buttonPlus_t_rot_L_selected.setVisible(true);
                buttonPlus_t_rot_L_selected.setDisable(false);
                buttonMinus_t_rot_L.setVisible(true);
                buttonMinus_t_rot_L.setDisable(false);
                buttonPlus_t_rot_L.setVisible(false);
                buttonPlus_t_rot_L.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.SHOULDER_ROTATION_L] + targetmovements[ServoIDs.SHOULDER_ROTATION_L] >180){
                    targetmovements[ServoIDs.SHOULDER_ROTATION_L]=180-Client.currentpositions[ServoIDs.SHOULDER_ROTATION_L];
                    spinner_t_rot_L.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_ROTATION_L]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_t_rot_L(){
                positiveMovements[ServoIDs.SHOULDER_ROTATION_L]=false;
                buttonMinus_t_rot_L_selected.setVisible(true);
                buttonMinus_t_rot_L_selected.setDisable(false);
                buttonPlus_t_rot_L_selected.setVisible(false);
                buttonPlus_t_rot_L_selected.setDisable(true);
                buttonPlus_t_rot_L.setVisible(true);
                buttonPlus_t_rot_L.setDisable(false);
                buttonMinus_t_rot_L.setVisible(false);
                buttonMinus_t_rot_L.setDisable(true);
                
                if(targetmovements[ServoIDs.SHOULDER_ROTATION_L] >= Client.currentpositions[ServoIDs.SHOULDER_ROTATION_L]){
                    targetmovements[ServoIDs.SHOULDER_ROTATION_L]=Client.currentpositions[ServoIDs.SHOULDER_ROTATION_L]-1;
                    spinner_t_rot_L.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_ROTATION_L]);
                    showErrTxt();
                }

            }
        
        @FXML
        public Text textCP_t_rot_R;
        @FXML
        public Spinner<Integer> spinner_t_rot_R;
        @FXML
        public Button buttonPlus_t_rot_R;
        @FXML
        public Button buttonPlus_t_rot_R_selected;
        @FXML
        public Button buttonMinus_t_rot_R;
        @FXML
        public Button buttonMinus_t_rot_R_selected;

            public void changeToPositive_t_rot_R(){
                positiveMovements[ServoIDs.SHOULDER_ROTATION_R]=true;
                buttonMinus_t_rot_R_selected.setVisible(false);
                buttonMinus_t_rot_R_selected.setDisable(true);
                buttonPlus_t_rot_R_selected.setVisible(true);
                buttonPlus_t_rot_R_selected.setDisable(false);
                buttonMinus_t_rot_R.setVisible(true);
                buttonMinus_t_rot_R.setDisable(false);
                buttonPlus_t_rot_R.setVisible(false);
                buttonPlus_t_rot_R.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.SHOULDER_ROTATION_R] + targetmovements[ServoIDs.SHOULDER_ROTATION_R] >180){
                    targetmovements[ServoIDs.SHOULDER_ROTATION_R]=180-Client.currentpositions[ServoIDs.SHOULDER_ROTATION_R];
                    spinner_t_rot_R.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_ROTATION_R]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_t_rot_R(){
                positiveMovements[ServoIDs.SHOULDER_ROTATION_R]=false;
                buttonMinus_t_rot_R_selected.setVisible(true);
                buttonMinus_t_rot_R_selected.setDisable(false);
                buttonPlus_t_rot_R_selected.setVisible(false);
                buttonPlus_t_rot_R_selected.setDisable(true);
                buttonPlus_t_rot_R.setVisible(true);
                buttonPlus_t_rot_R.setDisable(false);
                buttonMinus_t_rot_R.setVisible(false);
                buttonMinus_t_rot_R.setDisable(true);
                
                if(targetmovements[ServoIDs.SHOULDER_ROTATION_R] >= Client.currentpositions[ServoIDs.SHOULDER_ROTATION_R]){
                    targetmovements[ServoIDs.SHOULDER_ROTATION_R]=Client.currentpositions[ServoIDs.SHOULDER_ROTATION_R]-1;
                    spinner_t_rot_R.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_ROTATION_R]);
                    showErrTxt();
                }

            }            
            
            /* TODO Left Arm GUI things */
    @FXML
    public Pane paneMovementLArm;
        
        public void swapToMoveLeftArm(){
            hideall();
            currentScreen=3;
            textInformation.setText("Left Arm Servo Controls");
            setDefaultPMbuttons_LArm();

            textCP_forerot_L.setText(String.valueOf(Client.currentpositions[ServoIDs.FOREARM_ROTATION_L])+"º");
            textCP_armrot_L.setText(String.valueOf(Client.currentpositions[ServoIDs.ARM_ROTATION_L])+"º");
            textCP_sldr_L.setText(String.valueOf(Client.currentpositions[ServoIDs.SHOULDER_FLEX_L]+"º"));
            textCP_bicep_L.setText(String.valueOf(Client.currentpositions[ServoIDs.ARM_FLEX_L]+"º"));
            spinner_forerot_L.getValueFactory().setValue(targetmovements[ServoIDs.FOREARM_ROTATION_L]);
            spinner_armrot_L.getValueFactory().setValue(targetmovements[ServoIDs.ARM_ROTATION_L]);
            spinner_sldr_L.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_FLEX_L]);
            spinner_bicep_L.getValueFactory().setValue(targetmovements[ServoIDs.ARM_FLEX_L]);

            buttonBackToMVSelect.setDisable(false);
            buttonBackToMVSelect.setVisible(true);
            buttonExecuteMovement.setVisible(true);
            buttonExecuteMovement.setDisable(false);

            if(showGroupExecution){
                groupExecution.setVisible(true);
                groupExecution.setDisable(false);                
            }

            paneMovementLArm.setDisable(false);
            paneMovementLArm.setVisible(true);
        }
        
        @FXML
        public Text textCP_forerot_L;
        @FXML
        public Spinner<Integer> spinner_forerot_L;
        @FXML
        public Button buttonPlus_forerot_L;
        @FXML
        public Button buttonPlus_forerot_L_selected;
        @FXML
        public Button buttonMinus_forerot_L;
        @FXML
        public Button buttonMinus_forerot_L_selected;

            public void changeToPositive_forerot_L(){
                positiveMovements[ServoIDs.FOREARM_ROTATION_L]=true;
                buttonMinus_forerot_L_selected.setVisible(false);
                buttonMinus_forerot_L_selected.setDisable(true);
                buttonPlus_forerot_L_selected.setVisible(true);
                buttonPlus_forerot_L_selected.setDisable(false);
                buttonMinus_forerot_L.setVisible(true);
                buttonMinus_forerot_L.setDisable(false);
                buttonPlus_forerot_L.setVisible(false);
                buttonPlus_forerot_L.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.FOREARM_ROTATION_L] + targetmovements[ServoIDs.FOREARM_ROTATION_L] >180){
                    targetmovements[ServoIDs.FOREARM_ROTATION_L]=180-Client.currentpositions[ServoIDs.FOREARM_ROTATION_L];
                    spinner_forerot_L.getValueFactory().setValue(targetmovements[ServoIDs.FOREARM_ROTATION_L]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_forerot_L(){
                positiveMovements[ServoIDs.FOREARM_ROTATION_L]=false;
                buttonMinus_forerot_L_selected.setVisible(true);
                buttonMinus_forerot_L_selected.setDisable(false);
                buttonPlus_forerot_L_selected.setVisible(false);
                buttonPlus_forerot_L_selected.setDisable(true);
                buttonPlus_forerot_L.setVisible(true);
                buttonPlus_forerot_L.setDisable(false);
                buttonMinus_forerot_L.setVisible(false);
                buttonMinus_forerot_L.setDisable(true);
                
                if(targetmovements[ServoIDs.FOREARM_ROTATION_L] >= Client.currentpositions[ServoIDs.FOREARM_ROTATION_L]){
                    targetmovements[ServoIDs.FOREARM_ROTATION_L]=Client.currentpositions[ServoIDs.FOREARM_ROTATION_L]-1;
                    spinner_forerot_L.getValueFactory().setValue(targetmovements[ServoIDs.FOREARM_ROTATION_L]);
                    showErrTxt();
                }

            }

        @FXML
        public Text textCP_armrot_L;
        @FXML
        public Spinner<Integer> spinner_armrot_L;
        @FXML
        public Button buttonPlus_armrot_L;
        @FXML
        public Button buttonPlus_armrot_L_selected;
        @FXML
        public Button buttonMinus_armrot_L;
        @FXML
        public Button buttonMinus_armrot_L_selected;

            public void changeToPositive_armrot_L(){
                positiveMovements[ServoIDs.ARM_ROTATION_L]=true;
                buttonMinus_armrot_L_selected.setVisible(false);
                buttonMinus_armrot_L_selected.setDisable(true);
                buttonPlus_armrot_L_selected.setVisible(true);
                buttonPlus_armrot_L_selected.setDisable(false);
                buttonMinus_armrot_L.setVisible(true);
                buttonMinus_armrot_L.setDisable(false);
                buttonPlus_armrot_L.setVisible(false);
                buttonPlus_armrot_L.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.ARM_ROTATION_L] + targetmovements[ServoIDs.ARM_ROTATION_L] >180){
                    targetmovements[ServoIDs.ARM_ROTATION_L]=180-Client.currentpositions[ServoIDs.ARM_ROTATION_L];
                    spinner_armrot_L.getValueFactory().setValue(targetmovements[ServoIDs.ARM_ROTATION_L]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_armrot_L(){
                positiveMovements[ServoIDs.ARM_ROTATION_L]=false;
                buttonMinus_armrot_L_selected.setVisible(true);
                buttonMinus_armrot_L_selected.setDisable(false);
                buttonPlus_armrot_L_selected.setVisible(false);
                buttonPlus_armrot_L_selected.setDisable(true);
                buttonPlus_armrot_L.setVisible(true);
                buttonPlus_armrot_L.setDisable(false);
                buttonMinus_armrot_L.setVisible(false);
                buttonMinus_armrot_L.setDisable(true);
                
                if(targetmovements[ServoIDs.ARM_ROTATION_L] >= Client.currentpositions[ServoIDs.ARM_ROTATION_L]){
                    targetmovements[ServoIDs.ARM_ROTATION_L]=Client.currentpositions[ServoIDs.ARM_ROTATION_L]-1;
                    spinner_armrot_L.getValueFactory().setValue(targetmovements[ServoIDs.ARM_ROTATION_L]);
                    showErrTxt();
                }

            }

        @FXML
        public Text textCP_sldr_L;
        @FXML
        public Spinner<Integer> spinner_sldr_L;
        @FXML
        public Button buttonPlus_sldr_L;
        @FXML
        public Button buttonPlus_sldr_L_selected;
        @FXML
        public Button buttonMinus_sldr_L;
        @FXML
        public Button buttonMinus_sldr_L_selected;

            public void changeToPositive_sldr_L(){
                positiveMovements[ServoIDs.SHOULDER_FLEX_L]=true;
                buttonMinus_sldr_L_selected.setVisible(false);
                buttonMinus_sldr_L_selected.setDisable(true);
                buttonPlus_sldr_L_selected.setVisible(true);
                buttonPlus_sldr_L_selected.setDisable(false);
                buttonMinus_sldr_L.setVisible(true);
                buttonMinus_sldr_L.setDisable(false);
                buttonPlus_sldr_L.setVisible(false);
                buttonPlus_sldr_L.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.SHOULDER_FLEX_L] + targetmovements[ServoIDs.SHOULDER_FLEX_L] >180){
                    targetmovements[ServoIDs.SHOULDER_FLEX_L]=180-Client.currentpositions[ServoIDs.SHOULDER_FLEX_L];
                    spinner_sldr_L.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_FLEX_L]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_sldr_L(){
                positiveMovements[ServoIDs.SHOULDER_FLEX_L]=false;
                buttonMinus_sldr_L_selected.setVisible(true);
                buttonMinus_sldr_L_selected.setDisable(false);
                buttonPlus_sldr_L_selected.setVisible(false);
                buttonPlus_sldr_L_selected.setDisable(true);
                buttonPlus_sldr_L.setVisible(true);
                buttonPlus_sldr_L.setDisable(false);
                buttonMinus_sldr_L.setVisible(false);
                buttonMinus_sldr_L.setDisable(true);
                
                if(targetmovements[ServoIDs.SHOULDER_FLEX_L] >= Client.currentpositions[ServoIDs.SHOULDER_FLEX_L]){
                    targetmovements[ServoIDs.SHOULDER_FLEX_L]=Client.currentpositions[ServoIDs.SHOULDER_FLEX_L]-1;
                    spinner_sldr_L.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_FLEX_L]);
                    showErrTxt();
                }

            }
            
        @FXML
        public Text textCP_bicep_L;
        @FXML
        public Spinner<Integer> spinner_bicep_L;
        @FXML
        public Button buttonPlus_bicep_L;
        @FXML
        public Button buttonPlus_bicep_L_selected;
        @FXML
        public Button buttonMinus_bicep_L;
        @FXML
        public Button buttonMinus_bicep_L_selected;

            public void changeToPositive_bicep_L(){
                positiveMovements[ServoIDs.ARM_FLEX_L]=true;
                buttonMinus_bicep_L_selected.setVisible(false);
                buttonMinus_bicep_L_selected.setDisable(true);
                buttonPlus_bicep_L_selected.setVisible(true);
                buttonPlus_bicep_L_selected.setDisable(false);
                buttonMinus_bicep_L.setVisible(true);
                buttonMinus_bicep_L.setDisable(false);
                buttonPlus_bicep_L.setVisible(false);
                buttonPlus_bicep_L.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.ARM_FLEX_L] + targetmovements[ServoIDs.ARM_FLEX_L] >180){
                    targetmovements[ServoIDs.ARM_FLEX_L]=180-Client.currentpositions[ServoIDs.ARM_FLEX_L];
                    spinner_bicep_L.getValueFactory().setValue(targetmovements[ServoIDs.ARM_FLEX_L]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_bicep_L(){
                positiveMovements[ServoIDs.ARM_FLEX_L]=false;
                buttonMinus_bicep_L_selected.setVisible(true);
                buttonMinus_bicep_L_selected.setDisable(false);
                buttonPlus_bicep_L_selected.setVisible(false);
                buttonPlus_bicep_L_selected.setDisable(true);
                buttonPlus_bicep_L.setVisible(true);
                buttonPlus_bicep_L.setDisable(false);
                buttonMinus_bicep_L.setVisible(false);
                buttonMinus_bicep_L.setDisable(true);
                
                if(targetmovements[ServoIDs.ARM_FLEX_L] >= Client.currentpositions[ServoIDs.ARM_FLEX_L]){
                    targetmovements[ServoIDs.ARM_FLEX_L]=Client.currentpositions[ServoIDs.ARM_FLEX_L]-1;
                    spinner_bicep_L.getValueFactory().setValue(targetmovements[ServoIDs.ARM_FLEX_L]);
                    showErrTxt();
                }

            }                        
            
            /* TODO Left Hand GUI things */
    @FXML
    public Pane paneMovementLHand;

        public void swapToMoveLeftHand(){
            hideall();
            currentScreen=4;
            textInformation.setText("Left Hand Servo Controls");
            setDefaultPMbuttons_LHand();

            textCP_thumb_L.setText(String.valueOf(Client.currentpositions[ServoIDs.THUMB_L])+"º");
            textCP_index_L.setText(String.valueOf(Client.currentpositions[ServoIDs.INDEX_L])+"º");
            textCP_middle_L.setText(String.valueOf(Client.currentpositions[ServoIDs.MIDDLE_L]+"º"));
            textCP_ring_L.setText(String.valueOf(Client.currentpositions[ServoIDs.RING_L]+"º"));
            textCP_little_L.setText(String.valueOf(Client.currentpositions[ServoIDs.LITTLE_L]+"º"));
            textCP_palm_L.setText(String.valueOf(Client.currentpositions[ServoIDs.PALM_L]+"º"));
            spinner_thumb_L.getValueFactory().setValue(targetmovements[ServoIDs.THUMB_L]);
            spinner_index_L.getValueFactory().setValue(targetmovements[ServoIDs.INDEX_L]);
            spinner_middle_L.getValueFactory().setValue(targetmovements[ServoIDs.MIDDLE_L]);
            spinner_ring_L.getValueFactory().setValue(targetmovements[ServoIDs.RING_L]);
            spinner_little_L.getValueFactory().setValue(targetmovements[ServoIDs.LITTLE_L]);
            spinner_palm_L.getValueFactory().setValue(targetmovements[ServoIDs.PALM_L]);

            buttonBackToMVSelect.setDisable(false);
            buttonBackToMVSelect.setVisible(true);
            buttonExecuteMovement.setVisible(true);
            buttonExecuteMovement.setDisable(false);

            if(showGroupExecution){
                groupExecution.setVisible(true);
                groupExecution.setDisable(false);                
            }

            paneMovementLHand.setDisable(false);
            paneMovementLHand.setVisible(true);
        }

        @FXML
        public Text textCP_thumb_L;
        @FXML
        public Spinner<Integer> spinner_thumb_L;
        @FXML
        public Button buttonPlus_thumb_L;
        @FXML
        public Button buttonPlus_thumb_L_selected;
        @FXML
        public Button buttonMinus_thumb_L;
        @FXML
        public Button buttonMinus_thumb_L_selected;

            public void changeToPositive_thumb_L(){
                positiveMovements[ServoIDs.THUMB_L]=true;
                buttonMinus_thumb_L_selected.setVisible(false);
                buttonMinus_thumb_L_selected.setDisable(true);
                buttonPlus_thumb_L_selected.setVisible(true);
                buttonPlus_thumb_L_selected.setDisable(false);
                buttonMinus_thumb_L.setVisible(true);
                buttonMinus_thumb_L.setDisable(false);
                buttonPlus_thumb_L.setVisible(false);
                buttonPlus_thumb_L.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.THUMB_L] + targetmovements[ServoIDs.THUMB_L] >180){
                    targetmovements[ServoIDs.THUMB_L]=180-Client.currentpositions[ServoIDs.THUMB_L];
                    spinner_thumb_L.getValueFactory().setValue(targetmovements[ServoIDs.THUMB_L]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_thumb_L(){
                positiveMovements[ServoIDs.THUMB_L]=false;
                buttonMinus_thumb_L_selected.setVisible(true);
                buttonMinus_thumb_L_selected.setDisable(false);
                buttonPlus_thumb_L_selected.setVisible(false);
                buttonPlus_thumb_L_selected.setDisable(true);
                buttonPlus_thumb_L.setVisible(true);
                buttonPlus_thumb_L.setDisable(false);
                buttonMinus_thumb_L.setVisible(false);
                buttonMinus_thumb_L.setDisable(true);
                
                if(targetmovements[ServoIDs.THUMB_L] >= Client.currentpositions[ServoIDs.THUMB_L]){
                    targetmovements[ServoIDs.THUMB_L]=Client.currentpositions[ServoIDs.THUMB_L]-1;
                    spinner_thumb_L.getValueFactory().setValue(targetmovements[ServoIDs.THUMB_L]);
                    showErrTxt();
                }

            }       
            
        @FXML
        public Text textCP_index_L;
        @FXML
        public Spinner<Integer> spinner_index_L;
        @FXML
        public Button buttonPlus_index_L;
        @FXML
        public Button buttonPlus_index_L_selected;
        @FXML
        public Button buttonMinus_index_L;
        @FXML
        public Button buttonMinus_index_L_selected;

            public void changeToPositive_index_L(){
                positiveMovements[ServoIDs.INDEX_L]=true;
                buttonMinus_index_L_selected.setVisible(false);
                buttonMinus_index_L_selected.setDisable(true);
                buttonPlus_index_L_selected.setVisible(true);
                buttonPlus_index_L_selected.setDisable(false);
                buttonMinus_index_L.setVisible(true);
                buttonMinus_index_L.setDisable(false);
                buttonPlus_index_L.setVisible(false);
                buttonPlus_index_L.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.INDEX_L] + targetmovements[ServoIDs.INDEX_L] >180){
                    targetmovements[ServoIDs.INDEX_L]=180-Client.currentpositions[ServoIDs.INDEX_L];
                    spinner_index_L.getValueFactory().setValue(targetmovements[ServoIDs.INDEX_L]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_index_L(){
                positiveMovements[ServoIDs.INDEX_L]=false;
                buttonMinus_index_L_selected.setVisible(true);
                buttonMinus_index_L_selected.setDisable(false);
                buttonPlus_index_L_selected.setVisible(false);
                buttonPlus_index_L_selected.setDisable(true);
                buttonPlus_index_L.setVisible(true);
                buttonPlus_index_L.setDisable(false);
                buttonMinus_index_L.setVisible(false);
                buttonMinus_index_L.setDisable(true);
                
                if(targetmovements[ServoIDs.INDEX_L] >= Client.currentpositions[ServoIDs.INDEX_L]){
                    targetmovements[ServoIDs.INDEX_L]=Client.currentpositions[ServoIDs.INDEX_L]-1;
                    spinner_index_L.getValueFactory().setValue(targetmovements[ServoIDs.INDEX_L]);
                    showErrTxt();
                }

            }            
            
        @FXML
        public Text textCP_middle_L;
        @FXML
        public Spinner<Integer> spinner_middle_L;
        @FXML
        public Button buttonPlus_middle_L;
        @FXML
        public Button buttonPlus_middle_L_selected;
        @FXML
        public Button buttonMinus_middle_L;
        @FXML
        public Button buttonMinus_middle_L_selected;

            public void changeToPositive_middle_L(){
                positiveMovements[ServoIDs.MIDDLE_L]=true;
                buttonMinus_middle_L_selected.setVisible(false);
                buttonMinus_middle_L_selected.setDisable(true);
                buttonPlus_middle_L_selected.setVisible(true);
                buttonPlus_middle_L_selected.setDisable(false);
                buttonMinus_middle_L.setVisible(true);
                buttonMinus_middle_L.setDisable(false);
                buttonPlus_middle_L.setVisible(false);
                buttonPlus_middle_L.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.MIDDLE_L] + targetmovements[ServoIDs.MIDDLE_L] >180){
                    targetmovements[ServoIDs.MIDDLE_L]=180-Client.currentpositions[ServoIDs.MIDDLE_L];
                    spinner_middle_L.getValueFactory().setValue(targetmovements[ServoIDs.MIDDLE_L]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_middle_L(){
                positiveMovements[ServoIDs.MIDDLE_L]=false;
                buttonMinus_middle_L_selected.setVisible(true);
                buttonMinus_middle_L_selected.setDisable(false);
                buttonPlus_middle_L_selected.setVisible(false);
                buttonPlus_middle_L_selected.setDisable(true);
                buttonPlus_middle_L.setVisible(true);
                buttonPlus_middle_L.setDisable(false);
                buttonMinus_middle_L.setVisible(false);
                buttonMinus_middle_L.setDisable(true);
                
                if(targetmovements[ServoIDs.MIDDLE_L] >= Client.currentpositions[ServoIDs.MIDDLE_L]){
                    targetmovements[ServoIDs.MIDDLE_L]=Client.currentpositions[ServoIDs.MIDDLE_L]-1;
                    spinner_middle_L.getValueFactory().setValue(targetmovements[ServoIDs.MIDDLE_L]);
                    showErrTxt();
                }

            }            

        @FXML
        public Text textCP_ring_L;
        @FXML
        public Spinner<Integer> spinner_ring_L;
        @FXML
        public Button buttonPlus_ring_L;
        @FXML
        public Button buttonPlus_ring_L_selected;
        @FXML
        public Button buttonMinus_ring_L;
        @FXML
        public Button buttonMinus_ring_L_selected;

            public void changeToPositive_ring_L(){
                positiveMovements[ServoIDs.RING_L]=true;
                buttonMinus_ring_L_selected.setVisible(false);
                buttonMinus_ring_L_selected.setDisable(true);
                buttonPlus_ring_L_selected.setVisible(true);
                buttonPlus_ring_L_selected.setDisable(false);
                buttonMinus_ring_L.setVisible(true);
                buttonMinus_ring_L.setDisable(false);
                buttonPlus_ring_L.setVisible(false);
                buttonPlus_ring_L.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.RING_L] + targetmovements[ServoIDs.RING_L] >180){
                    targetmovements[ServoIDs.RING_L]=180-Client.currentpositions[ServoIDs.RING_L];
                    spinner_ring_L.getValueFactory().setValue(targetmovements[ServoIDs.RING_L]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_ring_L(){
                positiveMovements[ServoIDs.RING_L]=false;
                buttonMinus_ring_L_selected.setVisible(true);
                buttonMinus_ring_L_selected.setDisable(false);
                buttonPlus_ring_L_selected.setVisible(false);
                buttonPlus_ring_L_selected.setDisable(true);
                buttonPlus_ring_L.setVisible(true);
                buttonPlus_ring_L.setDisable(false);
                buttonMinus_ring_L.setVisible(false);
                buttonMinus_ring_L.setDisable(true);
                
                if(targetmovements[ServoIDs.RING_L] >= Client.currentpositions[ServoIDs.RING_L]){
                    targetmovements[ServoIDs.RING_L]=Client.currentpositions[ServoIDs.RING_L]-1;
                    spinner_ring_L.getValueFactory().setValue(targetmovements[ServoIDs.RING_L]);
                    showErrTxt();
                }

            }            
            
        @FXML
        public Text textCP_little_L;
        @FXML
        public Spinner<Integer> spinner_little_L;
        @FXML
        public Button buttonPlus_little_L;
        @FXML
        public Button buttonPlus_little_L_selected;
        @FXML
        public Button buttonMinus_little_L;
        @FXML
        public Button buttonMinus_little_L_selected;

            public void changeToPositive_little_L(){
                positiveMovements[ServoIDs.LITTLE_L]=true;
                buttonMinus_little_L_selected.setVisible(false);
                buttonMinus_little_L_selected.setDisable(true);
                buttonPlus_little_L_selected.setVisible(true);
                buttonPlus_little_L_selected.setDisable(false);
                buttonMinus_little_L.setVisible(true);
                buttonMinus_little_L.setDisable(false);
                buttonPlus_little_L.setVisible(false);
                buttonPlus_little_L.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.LITTLE_L] + targetmovements[ServoIDs.LITTLE_L] >180){
                    targetmovements[ServoIDs.LITTLE_L]=180-Client.currentpositions[ServoIDs.LITTLE_L];
                    spinner_little_L.getValueFactory().setValue(targetmovements[ServoIDs.LITTLE_L]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_little_L(){
                positiveMovements[ServoIDs.LITTLE_L]=false;
                buttonMinus_little_L_selected.setVisible(true);
                buttonMinus_little_L_selected.setDisable(false);
                buttonPlus_little_L_selected.setVisible(false);
                buttonPlus_little_L_selected.setDisable(true);
                buttonPlus_little_L.setVisible(true);
                buttonPlus_little_L.setDisable(false);
                buttonMinus_little_L.setVisible(false);
                buttonMinus_little_L.setDisable(true);
                
                if(targetmovements[ServoIDs.LITTLE_L] >= Client.currentpositions[ServoIDs.LITTLE_L]){
                    targetmovements[ServoIDs.LITTLE_L]=Client.currentpositions[ServoIDs.LITTLE_L]-1;
                    spinner_little_L.getValueFactory().setValue(targetmovements[ServoIDs.LITTLE_L]);
                    showErrTxt();
                }

            }            
            
        @FXML
        public Text textCP_palm_L;
        @FXML
        public Spinner<Integer> spinner_palm_L;
        @FXML
        public Button buttonPlus_palm_L;
        @FXML
        public Button buttonPlus_palm_L_selected;
        @FXML
        public Button buttonMinus_palm_L;
        @FXML
        public Button buttonMinus_palm_L_selected;

            public void changeToPositive_palm_L(){
                positiveMovements[ServoIDs.PALM_L]=true;
                buttonMinus_palm_L_selected.setVisible(false);
                buttonMinus_palm_L_selected.setDisable(true);
                buttonPlus_palm_L_selected.setVisible(true);
                buttonPlus_palm_L_selected.setDisable(false);
                buttonMinus_palm_L.setVisible(true);
                buttonMinus_palm_L.setDisable(false);
                buttonPlus_palm_L.setVisible(false);
                buttonPlus_palm_L.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.PALM_L] + targetmovements[ServoIDs.PALM_L] >180){
                    targetmovements[ServoIDs.PALM_L]=180-Client.currentpositions[ServoIDs.PALM_L];
                    spinner_palm_L.getValueFactory().setValue(targetmovements[ServoIDs.PALM_L]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_palm_L(){
                positiveMovements[ServoIDs.PALM_L]=false;
                buttonMinus_palm_L_selected.setVisible(true);
                buttonMinus_palm_L_selected.setDisable(false);
                buttonPlus_palm_L_selected.setVisible(false);
                buttonPlus_palm_L_selected.setDisable(true);
                buttonPlus_palm_L.setVisible(true);
                buttonPlus_palm_L.setDisable(false);
                buttonMinus_palm_L.setVisible(false);
                buttonMinus_palm_L.setDisable(true);
                
                if(targetmovements[ServoIDs.PALM_L] >= Client.currentpositions[ServoIDs.PALM_L]){
                    targetmovements[ServoIDs.PALM_L]=Client.currentpositions[ServoIDs.PALM_L]-1;
                    spinner_palm_L.getValueFactory().setValue(targetmovements[ServoIDs.PALM_L]);
                    showErrTxt();
                }

            }            
            
            /* TODO Right Arm GUI things */
    @FXML
    public Pane paneMovementRArm;
        
        public void swapToMoveRightArm(){
            hideall();
            currentScreen=5;
            textInformation.setText("Right Arm Servo Controls");
            setDefaultPMbuttons_RArm();

            textCP_forerot_R.setText(String.valueOf(Client.currentpositions[ServoIDs.FOREARM_ROTATION_R])+"º");
            textCP_armrot_R.setText(String.valueOf(Client.currentpositions[ServoIDs.ARM_ROTATION_R])+"º");
            textCP_sldr_R.setText(String.valueOf(Client.currentpositions[ServoIDs.SHOULDER_FLEX_R]+"º"));
            textCP_bicep_R.setText(String.valueOf(Client.currentpositions[ServoIDs.ARM_FLEX_R]+"º"));
            spinner_forerot_R.getValueFactory().setValue(targetmovements[ServoIDs.FOREARM_ROTATION_R]);
            spinner_armrot_R.getValueFactory().setValue(targetmovements[ServoIDs.ARM_ROTATION_R]);
            spinner_sldr_R.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_FLEX_R]);
            spinner_bicep_R.getValueFactory().setValue(targetmovements[ServoIDs.ARM_FLEX_R]);

            buttonBackToMVSelect.setDisable(false);
            buttonBackToMVSelect.setVisible(true);
            buttonExecuteMovement.setVisible(true);
            buttonExecuteMovement.setDisable(false);

            if(showGroupExecution){
                groupExecution.setVisible(true);
                groupExecution.setDisable(false);                
            }

            paneMovementRArm.setDisable(false);
            paneMovementRArm.setVisible(true);
        }
        
        @FXML
        public Text textCP_forerot_R;
        @FXML
        public Spinner<Integer> spinner_forerot_R;
        @FXML
        public Button buttonPlus_forerot_R;
        @FXML
        public Button buttonPlus_forerot_R_selected;
        @FXML
        public Button buttonMinus_forerot_R;
        @FXML
        public Button buttonMinus_forerot_R_selected;

            public void changeToPositive_forerot_R(){
                positiveMovements[ServoIDs.FOREARM_ROTATION_R]=true;
                buttonMinus_forerot_R_selected.setVisible(false);
                buttonMinus_forerot_R_selected.setDisable(true);
                buttonPlus_forerot_R_selected.setVisible(true);
                buttonPlus_forerot_R_selected.setDisable(false);
                buttonMinus_forerot_R.setVisible(true);
                buttonMinus_forerot_R.setDisable(false);
                buttonPlus_forerot_R.setVisible(false);
                buttonPlus_forerot_R.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.FOREARM_ROTATION_R] + targetmovements[ServoIDs.FOREARM_ROTATION_R] >180){
                    targetmovements[ServoIDs.FOREARM_ROTATION_R]=180-Client.currentpositions[ServoIDs.FOREARM_ROTATION_R];
                    spinner_forerot_R.getValueFactory().setValue(targetmovements[ServoIDs.FOREARM_ROTATION_R]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_forerot_R(){
                positiveMovements[ServoIDs.FOREARM_ROTATION_R]=false;
                buttonMinus_forerot_R_selected.setVisible(true);
                buttonMinus_forerot_R_selected.setDisable(false);
                buttonPlus_forerot_R_selected.setVisible(false);
                buttonPlus_forerot_R_selected.setDisable(true);
                buttonPlus_forerot_R.setVisible(true);
                buttonPlus_forerot_R.setDisable(false);
                buttonMinus_forerot_R.setVisible(false);
                buttonMinus_forerot_R.setDisable(true);
                
                if(targetmovements[ServoIDs.FOREARM_ROTATION_R] >= Client.currentpositions[ServoIDs.FOREARM_ROTATION_R]){
                    targetmovements[ServoIDs.FOREARM_ROTATION_R]=Client.currentpositions[ServoIDs.FOREARM_ROTATION_R]-1;
                    spinner_forerot_R.getValueFactory().setValue(targetmovements[ServoIDs.FOREARM_ROTATION_R]);
                    showErrTxt();
                }

            }

        @FXML
        public Text textCP_armrot_R;
        @FXML
        public Spinner<Integer> spinner_armrot_R;
        @FXML
        public Button buttonPlus_armrot_R;
        @FXML
        public Button buttonPlus_armrot_R_selected;
        @FXML
        public Button buttonMinus_armrot_R;
        @FXML
        public Button buttonMinus_armrot_R_selected;

            public void changeToPositive_armrot_R(){
                positiveMovements[ServoIDs.ARM_ROTATION_R]=true;
                buttonMinus_armrot_R_selected.setVisible(false);
                buttonMinus_armrot_R_selected.setDisable(true);
                buttonPlus_armrot_R_selected.setVisible(true);
                buttonPlus_armrot_R_selected.setDisable(false);
                buttonMinus_armrot_R.setVisible(true);
                buttonMinus_armrot_R.setDisable(false);
                buttonPlus_armrot_R.setVisible(false);
                buttonPlus_armrot_R.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.ARM_ROTATION_R] + targetmovements[ServoIDs.ARM_ROTATION_R] >180){
                    targetmovements[ServoIDs.ARM_ROTATION_R]=180-Client.currentpositions[ServoIDs.ARM_ROTATION_R];
                    spinner_armrot_R.getValueFactory().setValue(targetmovements[ServoIDs.ARM_ROTATION_R]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_armrot_R(){
                positiveMovements[ServoIDs.ARM_ROTATION_R]=false;
                buttonMinus_armrot_R_selected.setVisible(true);
                buttonMinus_armrot_R_selected.setDisable(false);
                buttonPlus_armrot_R_selected.setVisible(false);
                buttonPlus_armrot_R_selected.setDisable(true);
                buttonPlus_armrot_R.setVisible(true);
                buttonPlus_armrot_R.setDisable(false);
                buttonMinus_armrot_R.setVisible(false);
                buttonMinus_armrot_R.setDisable(true);
                
                if(targetmovements[ServoIDs.ARM_ROTATION_R] >= Client.currentpositions[ServoIDs.ARM_ROTATION_R]){
                    targetmovements[ServoIDs.ARM_ROTATION_R]=Client.currentpositions[ServoIDs.ARM_ROTATION_R]-1;
                    spinner_armrot_R.getValueFactory().setValue(targetmovements[ServoIDs.ARM_ROTATION_R]);
                    showErrTxt();
                }

            }

        @FXML
        public Text textCP_sldr_R;
        @FXML
        public Spinner<Integer> spinner_sldr_R;
        @FXML
        public Button buttonPlus_sldr_R;
        @FXML
        public Button buttonPlus_sldr_R_selected;
        @FXML
        public Button buttonMinus_sldr_R;
        @FXML
        public Button buttonMinus_sldr_R_selected;

            public void changeToPositive_sldr_R(){
                positiveMovements[ServoIDs.SHOULDER_FLEX_R]=true;
                buttonMinus_sldr_R_selected.setVisible(false);
                buttonMinus_sldr_R_selected.setDisable(true);
                buttonPlus_sldr_R_selected.setVisible(true);
                buttonPlus_sldr_R_selected.setDisable(false);
                buttonMinus_sldr_R.setVisible(true);
                buttonMinus_sldr_R.setDisable(false);
                buttonPlus_sldr_R.setVisible(false);
                buttonPlus_sldr_R.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.SHOULDER_FLEX_R] + targetmovements[ServoIDs.SHOULDER_FLEX_R] >180){
                    targetmovements[ServoIDs.SHOULDER_FLEX_R]=180-Client.currentpositions[ServoIDs.SHOULDER_FLEX_R];
                    spinner_sldr_R.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_FLEX_R]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_sldr_R(){
                positiveMovements[ServoIDs.SHOULDER_FLEX_R]=false;
                buttonMinus_sldr_R_selected.setVisible(true);
                buttonMinus_sldr_R_selected.setDisable(false);
                buttonPlus_sldr_R_selected.setVisible(false);
                buttonPlus_sldr_R_selected.setDisable(true);
                buttonPlus_sldr_R.setVisible(true);
                buttonPlus_sldr_R.setDisable(false);
                buttonMinus_sldr_R.setVisible(false);
                buttonMinus_sldr_R.setDisable(true);
                
                if(targetmovements[ServoIDs.SHOULDER_FLEX_R] >= Client.currentpositions[ServoIDs.SHOULDER_FLEX_R]){
                    targetmovements[ServoIDs.SHOULDER_FLEX_R]=Client.currentpositions[ServoIDs.SHOULDER_FLEX_R]-1;
                    spinner_sldr_R.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_FLEX_R]);
                    showErrTxt();
                }

            }
            
        @FXML
        public Text textCP_bicep_R;
        @FXML
        public Spinner<Integer> spinner_bicep_R;
        @FXML
        public Button buttonPlus_bicep_R;
        @FXML
        public Button buttonPlus_bicep_R_selected;
        @FXML
        public Button buttonMinus_bicep_R;
        @FXML
        public Button buttonMinus_bicep_R_selected;

            public void changeToPositive_bicep_R(){
                positiveMovements[ServoIDs.ARM_FLEX_R]=true;
                buttonMinus_bicep_R_selected.setVisible(false);
                buttonMinus_bicep_R_selected.setDisable(true);
                buttonPlus_bicep_R_selected.setVisible(true);
                buttonPlus_bicep_R_selected.setDisable(false);
                buttonMinus_bicep_R.setVisible(true);
                buttonMinus_bicep_R.setDisable(false);
                buttonPlus_bicep_R.setVisible(false);
                buttonPlus_bicep_R.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.ARM_FLEX_R] + targetmovements[ServoIDs.ARM_FLEX_R] >180){
                    targetmovements[ServoIDs.ARM_FLEX_R]=180-Client.currentpositions[ServoIDs.ARM_FLEX_R];
                    spinner_bicep_R.getValueFactory().setValue(targetmovements[ServoIDs.ARM_FLEX_R]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_bicep_R(){
                positiveMovements[ServoIDs.ARM_FLEX_R]=false;
                buttonMinus_bicep_R_selected.setVisible(true);
                buttonMinus_bicep_R_selected.setDisable(false);
                buttonPlus_bicep_R_selected.setVisible(false);
                buttonPlus_bicep_R_selected.setDisable(true);
                buttonPlus_bicep_R.setVisible(true);
                buttonPlus_bicep_R.setDisable(false);
                buttonMinus_bicep_R.setVisible(false);
                buttonMinus_bicep_R.setDisable(true);
                
                if(targetmovements[ServoIDs.ARM_FLEX_R] >= Client.currentpositions[ServoIDs.ARM_FLEX_R]){
                    targetmovements[ServoIDs.ARM_FLEX_R]=Client.currentpositions[ServoIDs.ARM_FLEX_R]-1;
                    spinner_bicep_R.getValueFactory().setValue(targetmovements[ServoIDs.ARM_FLEX_R]);
                    showErrTxt();
                }

            }                        
            
            /* TODO Left Hand GUI things */
    @FXML
    public Pane paneMovementRHand;

        public void swapToMoveRightHand(){
            hideall();
            currentScreen=6;
            textInformation.setText("Right Hand Servo Controls");
            setDefaultPMbuttons_RHand();

            textCP_thumb_R.setText(String.valueOf(Client.currentpositions[ServoIDs.THUMB_R])+"º");
            textCP_index_R.setText(String.valueOf(Client.currentpositions[ServoIDs.INDEX_R])+"º");
            textCP_middle_R.setText(String.valueOf(Client.currentpositions[ServoIDs.MIDDLE_R]+"º"));
            textCP_ring_R.setText(String.valueOf(Client.currentpositions[ServoIDs.RING_R]+"º"));
            textCP_Rittle_R.setText(String.valueOf(Client.currentpositions[ServoIDs.LITTLE_R]+"º"));
            textCP_palm_R.setText(String.valueOf(Client.currentpositions[ServoIDs.PALM_R]+"º"));
            spinner_thumb_R.getValueFactory().setValue(targetmovements[ServoIDs.THUMB_R]);
            spinner_index_R.getValueFactory().setValue(targetmovements[ServoIDs.INDEX_R]);
            spinner_middle_R.getValueFactory().setValue(targetmovements[ServoIDs.MIDDLE_R]);
            spinner_ring_R.getValueFactory().setValue(targetmovements[ServoIDs.RING_R]);
            spinner_Rittle_R.getValueFactory().setValue(targetmovements[ServoIDs.LITTLE_R]);
            spinner_palm_R.getValueFactory().setValue(targetmovements[ServoIDs.PALM_R]);

            buttonBackToMVSelect.setDisable(false);
            buttonBackToMVSelect.setVisible(true);
            buttonExecuteMovement.setVisible(true);
            buttonExecuteMovement.setDisable(false);

            if(showGroupExecution){
                groupExecution.setVisible(true);
                groupExecution.setDisable(false);                
            }

            paneMovementRHand.setDisable(false);
            paneMovementRHand.setVisible(true);
        }

        @FXML
        public Text textCP_thumb_R;
        @FXML
        public Spinner<Integer> spinner_thumb_R;
        @FXML
        public Button buttonPlus_thumb_R;
        @FXML
        public Button buttonPlus_thumb_R_selected;
        @FXML
        public Button buttonMinus_thumb_R;
        @FXML
        public Button buttonMinus_thumb_R_selected;

            public void changeToPositive_thumb_R(){
                positiveMovements[ServoIDs.THUMB_R]=true;
                buttonMinus_thumb_R_selected.setVisible(false);
                buttonMinus_thumb_R_selected.setDisable(true);
                buttonPlus_thumb_R_selected.setVisible(true);
                buttonPlus_thumb_R_selected.setDisable(false);
                buttonMinus_thumb_R.setVisible(true);
                buttonMinus_thumb_R.setDisable(false);
                buttonPlus_thumb_R.setVisible(false);
                buttonPlus_thumb_R.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.THUMB_R] + targetmovements[ServoIDs.THUMB_R] >180){
                    targetmovements[ServoIDs.THUMB_R]=180-Client.currentpositions[ServoIDs.THUMB_R];
                    spinner_thumb_R.getValueFactory().setValue(targetmovements[ServoIDs.THUMB_R]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_thumb_R(){
                positiveMovements[ServoIDs.THUMB_R]=false;
                buttonMinus_thumb_R_selected.setVisible(true);
                buttonMinus_thumb_R_selected.setDisable(false);
                buttonPlus_thumb_R_selected.setVisible(false);
                buttonPlus_thumb_R_selected.setDisable(true);
                buttonPlus_thumb_R.setVisible(true);
                buttonPlus_thumb_R.setDisable(false);
                buttonMinus_thumb_R.setVisible(false);
                buttonMinus_thumb_R.setDisable(true);
                
                if(targetmovements[ServoIDs.THUMB_R] >= Client.currentpositions[ServoIDs.THUMB_R]){
                    targetmovements[ServoIDs.THUMB_R]=Client.currentpositions[ServoIDs.THUMB_R]-1;
                    spinner_thumb_R.getValueFactory().setValue(targetmovements[ServoIDs.THUMB_R]);
                    showErrTxt();
                }

            }       
            
        @FXML
        public Text textCP_index_R;
        @FXML
        public Spinner<Integer> spinner_index_R;
        @FXML
        public Button buttonPlus_index_R;
        @FXML
        public Button buttonPlus_index_R_selected;
        @FXML
        public Button buttonMinus_index_R;
        @FXML
        public Button buttonMinus_index_R_selected;

            public void changeToPositive_index_R(){
                positiveMovements[ServoIDs.INDEX_R]=true;
                buttonMinus_index_R_selected.setVisible(false);
                buttonMinus_index_R_selected.setDisable(true);
                buttonPlus_index_R_selected.setVisible(true);
                buttonPlus_index_R_selected.setDisable(false);
                buttonMinus_index_R.setVisible(true);
                buttonMinus_index_R.setDisable(false);
                buttonPlus_index_R.setVisible(false);
                buttonPlus_index_R.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.INDEX_R] + targetmovements[ServoIDs.INDEX_R] >180){
                    targetmovements[ServoIDs.INDEX_R]=180-Client.currentpositions[ServoIDs.INDEX_R];
                    spinner_index_R.getValueFactory().setValue(targetmovements[ServoIDs.INDEX_R]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_index_R(){
                positiveMovements[ServoIDs.INDEX_R]=false;
                buttonMinus_index_R_selected.setVisible(true);
                buttonMinus_index_R_selected.setDisable(false);
                buttonPlus_index_R_selected.setVisible(false);
                buttonPlus_index_R_selected.setDisable(true);
                buttonPlus_index_R.setVisible(true);
                buttonPlus_index_R.setDisable(false);
                buttonMinus_index_R.setVisible(false);
                buttonMinus_index_R.setDisable(true);
                
                if(targetmovements[ServoIDs.INDEX_R] >= Client.currentpositions[ServoIDs.INDEX_R]){
                    targetmovements[ServoIDs.INDEX_R]=Client.currentpositions[ServoIDs.INDEX_R]-1;
                    spinner_index_R.getValueFactory().setValue(targetmovements[ServoIDs.INDEX_R]);
                    showErrTxt();
                }

            }            
            
        @FXML
        public Text textCP_middle_R;
        @FXML
        public Spinner<Integer> spinner_middle_R;
        @FXML
        public Button buttonPlus_middle_R;
        @FXML
        public Button buttonPlus_middle_R_selected;
        @FXML
        public Button buttonMinus_middle_R;
        @FXML
        public Button buttonMinus_middle_R_selected;

            public void changeToPositive_middle_R(){
                positiveMovements[ServoIDs.MIDDLE_R]=true;
                buttonMinus_middle_R_selected.setVisible(false);
                buttonMinus_middle_R_selected.setDisable(true);
                buttonPlus_middle_R_selected.setVisible(true);
                buttonPlus_middle_R_selected.setDisable(false);
                buttonMinus_middle_R.setVisible(true);
                buttonMinus_middle_R.setDisable(false);
                buttonPlus_middle_R.setVisible(false);
                buttonPlus_middle_R.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.MIDDLE_R] + targetmovements[ServoIDs.MIDDLE_R] >180){
                    targetmovements[ServoIDs.MIDDLE_R]=180-Client.currentpositions[ServoIDs.MIDDLE_R];
                    spinner_middle_R.getValueFactory().setValue(targetmovements[ServoIDs.MIDDLE_R]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_middle_R(){
                positiveMovements[ServoIDs.MIDDLE_R]=false;
                buttonMinus_middle_R_selected.setVisible(true);
                buttonMinus_middle_R_selected.setDisable(false);
                buttonPlus_middle_R_selected.setVisible(false);
                buttonPlus_middle_R_selected.setDisable(true);
                buttonPlus_middle_R.setVisible(true);
                buttonPlus_middle_R.setDisable(false);
                buttonMinus_middle_R.setVisible(false);
                buttonMinus_middle_R.setDisable(true);
                
                if(targetmovements[ServoIDs.MIDDLE_R] >= Client.currentpositions[ServoIDs.MIDDLE_R]){
                    targetmovements[ServoIDs.MIDDLE_R]=Client.currentpositions[ServoIDs.MIDDLE_R]-1;
                    spinner_middle_R.getValueFactory().setValue(targetmovements[ServoIDs.MIDDLE_R]);
                    showErrTxt();
                }

            }            

        @FXML
        public Text textCP_ring_R;
        @FXML
        public Spinner<Integer> spinner_ring_R;
        @FXML
        public Button buttonPlus_ring_R;
        @FXML
        public Button buttonPlus_ring_R_selected;
        @FXML
        public Button buttonMinus_ring_R;
        @FXML
        public Button buttonMinus_ring_R_selected;

            public void changeToPositive_ring_R(){
                positiveMovements[ServoIDs.RING_R]=true;
                buttonMinus_ring_R_selected.setVisible(false);
                buttonMinus_ring_R_selected.setDisable(true);
                buttonPlus_ring_R_selected.setVisible(true);
                buttonPlus_ring_R_selected.setDisable(false);
                buttonMinus_ring_R.setVisible(true);
                buttonMinus_ring_R.setDisable(false);
                buttonPlus_ring_R.setVisible(false);
                buttonPlus_ring_R.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.RING_R] + targetmovements[ServoIDs.RING_R] >180){
                    targetmovements[ServoIDs.RING_R]=180-Client.currentpositions[ServoIDs.RING_R];
                    spinner_ring_R.getValueFactory().setValue(targetmovements[ServoIDs.RING_R]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_ring_R(){
                positiveMovements[ServoIDs.RING_R]=false;
                buttonMinus_ring_R_selected.setVisible(true);
                buttonMinus_ring_R_selected.setDisable(false);
                buttonPlus_ring_R_selected.setVisible(false);
                buttonPlus_ring_R_selected.setDisable(true);
                buttonPlus_ring_R.setVisible(true);
                buttonPlus_ring_R.setDisable(false);
                buttonMinus_ring_R.setVisible(false);
                buttonMinus_ring_R.setDisable(true);
                
                if(targetmovements[ServoIDs.RING_R] >= Client.currentpositions[ServoIDs.RING_R]){
                    targetmovements[ServoIDs.RING_R]=Client.currentpositions[ServoIDs.RING_R]-1;
                    spinner_ring_R.getValueFactory().setValue(targetmovements[ServoIDs.RING_R]);
                    showErrTxt();
                }

            }            
            
        @FXML
        public Text textCP_Rittle_R;
        @FXML
        public Spinner<Integer> spinner_Rittle_R;
        @FXML
        public Button buttonPlus_Rittle_R;
        @FXML
        public Button buttonPlus_Rittle_R_selected;
        @FXML
        public Button buttonMinus_Rittle_R;
        @FXML
        public Button buttonMinus_Rittle_R_selected;

            public void changeToPositive_Rittle_R(){
                positiveMovements[ServoIDs.LITTLE_R]=true;
                buttonMinus_Rittle_R_selected.setVisible(false);
                buttonMinus_Rittle_R_selected.setDisable(true);
                buttonPlus_Rittle_R_selected.setVisible(true);
                buttonPlus_Rittle_R_selected.setDisable(false);
                buttonMinus_Rittle_R.setVisible(true);
                buttonMinus_Rittle_R.setDisable(false);
                buttonPlus_Rittle_R.setVisible(false);
                buttonPlus_Rittle_R.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.LITTLE_R] + targetmovements[ServoIDs.LITTLE_R] >180){
                    targetmovements[ServoIDs.LITTLE_R]=180-Client.currentpositions[ServoIDs.LITTLE_R];
                    spinner_Rittle_R.getValueFactory().setValue(targetmovements[ServoIDs.LITTLE_R]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_Rittle_R(){
                positiveMovements[ServoIDs.LITTLE_R]=false;
                buttonMinus_Rittle_R_selected.setVisible(true);
                buttonMinus_Rittle_R_selected.setDisable(false);
                buttonPlus_Rittle_R_selected.setVisible(false);
                buttonPlus_Rittle_R_selected.setDisable(true);
                buttonPlus_Rittle_R.setVisible(true);
                buttonPlus_Rittle_R.setDisable(false);
                buttonMinus_Rittle_R.setVisible(false);
                buttonMinus_Rittle_R.setDisable(true);
                
                if(targetmovements[ServoIDs.LITTLE_R] >= Client.currentpositions[ServoIDs.LITTLE_R]){
                    targetmovements[ServoIDs.LITTLE_R]=Client.currentpositions[ServoIDs.LITTLE_R]-1;
                    spinner_Rittle_R.getValueFactory().setValue(targetmovements[ServoIDs.LITTLE_R]);
                    showErrTxt();
                }

            }            
            
        @FXML
        public Text textCP_palm_R;
        @FXML
        public Spinner<Integer> spinner_palm_R;
        @FXML
        public Button buttonPlus_palm_R;
        @FXML
        public Button buttonPlus_palm_R_selected;
        @FXML
        public Button buttonMinus_palm_R;
        @FXML
        public Button buttonMinus_palm_R_selected;

            public void changeToPositive_palm_R(){
                positiveMovements[ServoIDs.PALM_R]=true;
                buttonMinus_palm_R_selected.setVisible(false);
                buttonMinus_palm_R_selected.setDisable(true);
                buttonPlus_palm_R_selected.setVisible(true);
                buttonPlus_palm_R_selected.setDisable(false);
                buttonMinus_palm_R.setVisible(true);
                buttonMinus_palm_R.setDisable(false);
                buttonPlus_palm_R.setVisible(false);
                buttonPlus_palm_R.setDisable(true);
                
                if(Client.currentpositions[ServoIDs.PALM_R] + targetmovements[ServoIDs.PALM_R] >180){
                    targetmovements[ServoIDs.PALM_R]=180-Client.currentpositions[ServoIDs.PALM_R];
                    spinner_palm_R.getValueFactory().setValue(targetmovements[ServoIDs.PALM_R]);
                    showErrTxt();
                }
                
            }

            public void changeToNegative_palm_R(){
                positiveMovements[ServoIDs.PALM_R]=false;
                buttonMinus_palm_R_selected.setVisible(true);
                buttonMinus_palm_R_selected.setDisable(false);
                buttonPlus_palm_R_selected.setVisible(false);
                buttonPlus_palm_R_selected.setDisable(true);
                buttonPlus_palm_R.setVisible(true);
                buttonPlus_palm_R.setDisable(false);
                buttonMinus_palm_R.setVisible(false);
                buttonMinus_palm_R.setDisable(true);
                
                if(targetmovements[ServoIDs.PALM_R] >= Client.currentpositions[ServoIDs.PALM_R]){
                    targetmovements[ServoIDs.PALM_R]=Client.currentpositions[ServoIDs.PALM_R]-1;
                    spinner_palm_R.getValueFactory().setValue(targetmovements[ServoIDs.PALM_R]);
                    showErrTxt();
                }

            }            

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        SpinnerValueFactory<Integer> spinnerVF_h_rot = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_h_rot.setValue(0);
            spinner_h_rot.setValueFactory(spinnerVF_h_rot);
            spinner_h_rot.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.HEAD_ROTATION]=spinner_h_rot.getValue(); 
                    if(positiveMovements[ServoIDs.HEAD_ROTATION]){
                        if(Client.currentpositions[ServoIDs.HEAD_ROTATION] + targetmovements[ServoIDs.HEAD_ROTATION] >180){
                            targetmovements[ServoIDs.HEAD_ROTATION]=180-Client.currentpositions[ServoIDs.HEAD_ROTATION];
                            spinner_h_rot.getValueFactory().setValue(targetmovements[ServoIDs.HEAD_ROTATION]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.HEAD_ROTATION] >= Client.currentpositions[ServoIDs.HEAD_ROTATION]){
                            targetmovements[ServoIDs.HEAD_ROTATION]=Client.currentpositions[ServoIDs.HEAD_ROTATION]-1;
                            if(Client.currentpositions[ServoIDs.HEAD_ROTATION]==0){targetmovements[ServoIDs.HEAD_ROTATION]=0;}
                            spinner_h_rot.getValueFactory().setValue(targetmovements[ServoIDs.HEAD_ROTATION]);
                            showErrTxt();
                        }
                    }

                }
                
            });

        SpinnerValueFactory<Integer> spinnerVF_h_tilt_L = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_h_tilt_L.setValue(0);
            spinner_h_tilt_L.setValueFactory(spinnerVF_h_tilt_L);
            spinner_h_tilt_L.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.HEAD_TILT_L]=spinner_h_tilt_L.getValue(); 
                    if(positiveMovements[ServoIDs.HEAD_TILT_L]){
                        if(Client.currentpositions[ServoIDs.HEAD_TILT_L] + targetmovements[ServoIDs.HEAD_TILT_L] >180){
                            targetmovements[ServoIDs.HEAD_TILT_L]=180-Client.currentpositions[ServoIDs.HEAD_TILT_L];
                            spinner_h_tilt_L.getValueFactory().setValue(targetmovements[ServoIDs.HEAD_TILT_L]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.HEAD_TILT_L] >= Client.currentpositions[ServoIDs.HEAD_TILT_L]){
                            targetmovements[ServoIDs.HEAD_TILT_L]=Client.currentpositions[ServoIDs.HEAD_TILT_L]-1;
                            if(Client.currentpositions[ServoIDs.HEAD_TILT_L]==0){targetmovements[ServoIDs.HEAD_TILT_L]=0;}
                            spinner_h_tilt_L.getValueFactory().setValue(targetmovements[ServoIDs.HEAD_TILT_L]);
                            showErrTxt();
                        }
                    }

                }
                
            });

        SpinnerValueFactory<Integer> spinnerVF_h_tilt_R = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_h_tilt_R.setValue(0);
            spinner_h_tilt_R.setValueFactory(spinnerVF_h_tilt_R);
            spinner_h_tilt_R.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.HEAD_TILT_R]=spinner_h_tilt_R.getValue(); 
                    if(positiveMovements[ServoIDs.HEAD_TILT_R]){
                        if(Client.currentpositions[ServoIDs.HEAD_TILT_R] + targetmovements[ServoIDs.HEAD_TILT_R] >180){
                            targetmovements[ServoIDs.HEAD_TILT_R]=180-Client.currentpositions[ServoIDs.HEAD_TILT_R];
                            spinner_h_tilt_R.getValueFactory().setValue(targetmovements[ServoIDs.HEAD_TILT_R]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.HEAD_TILT_R] >= Client.currentpositions[ServoIDs.HEAD_TILT_R]){
                            targetmovements[ServoIDs.HEAD_TILT_R]=Client.currentpositions[ServoIDs.HEAD_TILT_R]-1;
                            if(Client.currentpositions[ServoIDs.HEAD_TILT_R]==0){targetmovements[ServoIDs.HEAD_TILT_R]=0;}
                            spinner_h_tilt_R.getValueFactory().setValue(targetmovements[ServoIDs.HEAD_TILT_R]);
                            showErrTxt();
                        }
                    }

                }
                
            });

        SpinnerValueFactory<Integer> spinnerVF_t_tilt_L = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_t_tilt_L.setValue(0);
            spinner_t_tilt_L.setValueFactory(spinnerVF_t_tilt_L);
            spinner_t_tilt_L.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.TORSO_TILT_L]=spinner_t_tilt_L.getValue(); 
                    if(positiveMovements[ServoIDs.TORSO_TILT_L]){
                        if(Client.currentpositions[ServoIDs.TORSO_TILT_L] + targetmovements[ServoIDs.TORSO_TILT_L] >180){
                            targetmovements[ServoIDs.TORSO_TILT_L]=180-Client.currentpositions[ServoIDs.TORSO_TILT_L];
                            spinner_t_tilt_L.getValueFactory().setValue(targetmovements[ServoIDs.TORSO_TILT_L]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.TORSO_TILT_L] >= Client.currentpositions[ServoIDs.TORSO_TILT_L]){
                            targetmovements[ServoIDs.TORSO_TILT_L]=Client.currentpositions[ServoIDs.TORSO_TILT_L]-1;
                            if(Client.currentpositions[ServoIDs.TORSO_TILT_L]==0){targetmovements[ServoIDs.TORSO_TILT_L]=0;}
                            spinner_t_tilt_L.getValueFactory().setValue(targetmovements[ServoIDs.TORSO_TILT_L]);
                            showErrTxt();
                        }
                    }

                }
                
            });

        SpinnerValueFactory<Integer> spinnerVF_t_tilt_R = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_t_tilt_R.setValue(0);
            spinner_t_tilt_R.setValueFactory(spinnerVF_t_tilt_R);
            spinner_t_tilt_R.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.TORSO_TILT_R]=spinner_t_tilt_R.getValue(); 
                    if(positiveMovements[ServoIDs.TORSO_TILT_R]){
                        if(Client.currentpositions[ServoIDs.TORSO_TILT_R] + targetmovements[ServoIDs.TORSO_TILT_R] >180){
                            targetmovements[ServoIDs.TORSO_TILT_R]=180-Client.currentpositions[ServoIDs.TORSO_TILT_R];
                            spinner_t_tilt_R.getValueFactory().setValue(targetmovements[ServoIDs.TORSO_TILT_R]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.TORSO_TILT_R] >= Client.currentpositions[ServoIDs.TORSO_TILT_R]){
                            targetmovements[ServoIDs.TORSO_TILT_R]=Client.currentpositions[ServoIDs.TORSO_TILT_R]-1;
                            if(Client.currentpositions[ServoIDs.TORSO_TILT_R]==0){targetmovements[ServoIDs.TORSO_TILT_R]=0;}
                            spinner_t_tilt_R.getValueFactory().setValue(targetmovements[ServoIDs.TORSO_TILT_R]);
                            showErrTxt();
                        }
                    }

                }
                
            });     
            
        SpinnerValueFactory<Integer> spinnerVF_t_rot_L = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_t_rot_L.setValue(0);
            spinner_t_rot_L.setValueFactory(spinnerVF_t_rot_L);
            spinner_t_rot_L.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.SHOULDER_ROTATION_L]=spinner_t_rot_L.getValue(); 
                    if(positiveMovements[ServoIDs.SHOULDER_ROTATION_L]){
                        if(Client.currentpositions[ServoIDs.SHOULDER_ROTATION_L] + targetmovements[ServoIDs.SHOULDER_ROTATION_L] >180){
                            targetmovements[ServoIDs.SHOULDER_ROTATION_L]=180-Client.currentpositions[ServoIDs.SHOULDER_ROTATION_L];
                            spinner_t_rot_L.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_ROTATION_L]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.SHOULDER_ROTATION_L] >= Client.currentpositions[ServoIDs.SHOULDER_ROTATION_L]){
                            targetmovements[ServoIDs.SHOULDER_ROTATION_L]=Client.currentpositions[ServoIDs.SHOULDER_ROTATION_L]-1;
                            if(Client.currentpositions[ServoIDs.SHOULDER_ROTATION_L]==0){targetmovements[ServoIDs.SHOULDER_ROTATION_L]=0;}
                            spinner_t_rot_L.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_ROTATION_L]);
                            showErrTxt();
                        }
                    }

                }
                
            });

        SpinnerValueFactory<Integer> spinnerVF_t_rot_R = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_t_rot_R.setValue(0);
            spinner_t_rot_R.setValueFactory(spinnerVF_t_rot_R);
            spinner_t_rot_R.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.SHOULDER_ROTATION_R]=spinner_t_rot_R.getValue(); 
                    if(positiveMovements[ServoIDs.SHOULDER_ROTATION_R]){
                        if(Client.currentpositions[ServoIDs.SHOULDER_ROTATION_R] + targetmovements[ServoIDs.SHOULDER_ROTATION_R] >180){
                            targetmovements[ServoIDs.SHOULDER_ROTATION_R]=180-Client.currentpositions[ServoIDs.SHOULDER_ROTATION_R];
                            spinner_t_rot_R.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_ROTATION_R]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.SHOULDER_ROTATION_R] >= Client.currentpositions[ServoIDs.SHOULDER_ROTATION_R]){
                            targetmovements[ServoIDs.SHOULDER_ROTATION_R]=Client.currentpositions[ServoIDs.SHOULDER_ROTATION_R]-1;
                            if(Client.currentpositions[ServoIDs.SHOULDER_ROTATION_R]==0){targetmovements[ServoIDs.SHOULDER_ROTATION_R]=0;}
                            spinner_t_rot_R.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_ROTATION_R]);
                            showErrTxt();
                        }
                    }

                }
                
            });             

        SpinnerValueFactory<Integer> spinnerVF_forerot_L = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_forerot_L.setValue(0);
            spinner_forerot_L.setValueFactory(spinnerVF_forerot_L);
            spinner_forerot_L.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.FOREARM_ROTATION_L]=spinner_forerot_L.getValue(); 
                    if(positiveMovements[ServoIDs.FOREARM_ROTATION_L]){
                        if(Client.currentpositions[ServoIDs.FOREARM_ROTATION_L] + targetmovements[ServoIDs.FOREARM_ROTATION_L] >180){
                            targetmovements[ServoIDs.FOREARM_ROTATION_L]=180-Client.currentpositions[ServoIDs.FOREARM_ROTATION_L];
                            spinner_forerot_L.getValueFactory().setValue(targetmovements[ServoIDs.FOREARM_ROTATION_L]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.FOREARM_ROTATION_L] >= Client.currentpositions[ServoIDs.FOREARM_ROTATION_L]){
                            targetmovements[ServoIDs.FOREARM_ROTATION_L]=Client.currentpositions[ServoIDs.FOREARM_ROTATION_L]-1;
                            if(Client.currentpositions[ServoIDs.FOREARM_ROTATION_L]==0){targetmovements[ServoIDs.FOREARM_ROTATION_L]=0;}
                            spinner_forerot_L.getValueFactory().setValue(targetmovements[ServoIDs.FOREARM_ROTATION_L]);
                            showErrTxt();
                        }
                    }

                }
                
            });  

        SpinnerValueFactory<Integer> spinnerVF_armrot_L = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_armrot_L.setValue(0);
            spinner_armrot_L.setValueFactory(spinnerVF_armrot_L);
            spinner_armrot_L.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.ARM_ROTATION_L]=spinner_armrot_L.getValue(); 
                    if(positiveMovements[ServoIDs.ARM_ROTATION_L]){
                        if(Client.currentpositions[ServoIDs.ARM_ROTATION_L] + targetmovements[ServoIDs.ARM_ROTATION_L] >180){
                            targetmovements[ServoIDs.ARM_ROTATION_L]=180-Client.currentpositions[ServoIDs.ARM_ROTATION_L];
                            spinner_armrot_L.getValueFactory().setValue(targetmovements[ServoIDs.ARM_ROTATION_L]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.ARM_ROTATION_L] >= Client.currentpositions[ServoIDs.ARM_ROTATION_L]){
                            targetmovements[ServoIDs.ARM_ROTATION_L]=Client.currentpositions[ServoIDs.ARM_ROTATION_L]-1;
                            if(Client.currentpositions[ServoIDs.ARM_ROTATION_L]==0){targetmovements[ServoIDs.ARM_ROTATION_L]=0;}
                            spinner_armrot_L.getValueFactory().setValue(targetmovements[ServoIDs.ARM_ROTATION_L]);
                            showErrTxt();
                        }
                    }

                }
                
            });             
            
        SpinnerValueFactory<Integer> spinnerVF_sldr_L = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_sldr_L.setValue(0);
            spinner_sldr_L.setValueFactory(spinnerVF_sldr_L);
            spinner_sldr_L.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.SHOULDER_FLEX_L]=spinner_sldr_L.getValue(); 
                    if(positiveMovements[ServoIDs.SHOULDER_FLEX_L]){
                        if(Client.currentpositions[ServoIDs.SHOULDER_FLEX_L] + targetmovements[ServoIDs.SHOULDER_FLEX_L] >180){
                            targetmovements[ServoIDs.SHOULDER_FLEX_L]=180-Client.currentpositions[ServoIDs.SHOULDER_FLEX_L];
                            spinner_sldr_L.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_FLEX_L]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.SHOULDER_FLEX_L] >= Client.currentpositions[ServoIDs.SHOULDER_FLEX_L]){
                            targetmovements[ServoIDs.SHOULDER_FLEX_L]=Client.currentpositions[ServoIDs.SHOULDER_FLEX_L]-1;
                            if(Client.currentpositions[ServoIDs.SHOULDER_FLEX_L]==0){targetmovements[ServoIDs.SHOULDER_FLEX_L]=0;}
                            spinner_sldr_L.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_FLEX_L]);
                            showErrTxt();
                        }
                    }

                }
                
            });     
            
        SpinnerValueFactory<Integer> spinnerVF_bicep_L = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_bicep_L.setValue(0);
            spinner_bicep_L.setValueFactory(spinnerVF_bicep_L);
            spinner_bicep_L.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.ARM_FLEX_L]=spinner_bicep_L.getValue(); 
                    if(positiveMovements[ServoIDs.ARM_FLEX_L]){
                        if(Client.currentpositions[ServoIDs.ARM_FLEX_L] + targetmovements[ServoIDs.ARM_FLEX_L] >180){
                            targetmovements[ServoIDs.ARM_FLEX_L]=180-Client.currentpositions[ServoIDs.ARM_FLEX_L];
                            spinner_bicep_L.getValueFactory().setValue(targetmovements[ServoIDs.ARM_FLEX_L]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.ARM_FLEX_L] >= Client.currentpositions[ServoIDs.ARM_FLEX_L]){
                            targetmovements[ServoIDs.ARM_FLEX_L]=Client.currentpositions[ServoIDs.ARM_FLEX_L]-1;
                            if(Client.currentpositions[ServoIDs.ARM_FLEX_L]==0){targetmovements[ServoIDs.ARM_FLEX_L]=0;}
                            spinner_bicep_L.getValueFactory().setValue(targetmovements[ServoIDs.ARM_FLEX_L]);
                            showErrTxt();
                        }
                    }

                }
                
            });           
            
        SpinnerValueFactory<Integer> spinnerVF_thumb_L = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_thumb_L.setValue(0);
            spinner_thumb_L.setValueFactory(spinnerVF_thumb_L);
            spinner_thumb_L.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.THUMB_L]=spinner_thumb_L.getValue(); 
                    if(positiveMovements[ServoIDs.THUMB_L]){
                        if(Client.currentpositions[ServoIDs.THUMB_L] + targetmovements[ServoIDs.THUMB_L] >180){
                            targetmovements[ServoIDs.THUMB_L]=180-Client.currentpositions[ServoIDs.THUMB_L];
                            spinner_thumb_L.getValueFactory().setValue(targetmovements[ServoIDs.THUMB_L]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.THUMB_L] >= Client.currentpositions[ServoIDs.THUMB_L]){
                            targetmovements[ServoIDs.THUMB_L]=Client.currentpositions[ServoIDs.THUMB_L]-1;
                            if(Client.currentpositions[ServoIDs.THUMB_L]==0){targetmovements[ServoIDs.THUMB_L]=0;}
                            spinner_thumb_L.getValueFactory().setValue(targetmovements[ServoIDs.THUMB_L]);
                            showErrTxt();
                        }
                    }

                }
                
            }); 

        SpinnerValueFactory<Integer> spinnerVF_index_L = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_index_L.setValue(0);
            spinner_index_L.setValueFactory(spinnerVF_index_L);
            spinner_index_L.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.INDEX_L]=spinner_index_L.getValue(); 
                    if(positiveMovements[ServoIDs.INDEX_L]){
                        if(Client.currentpositions[ServoIDs.INDEX_L] + targetmovements[ServoIDs.INDEX_L] >180){
                            targetmovements[ServoIDs.INDEX_L]=180-Client.currentpositions[ServoIDs.INDEX_L];
                            spinner_index_L.getValueFactory().setValue(targetmovements[ServoIDs.INDEX_L]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.INDEX_L] >= Client.currentpositions[ServoIDs.INDEX_L]){
                            targetmovements[ServoIDs.INDEX_L]=Client.currentpositions[ServoIDs.INDEX_L]-1;
                            if(Client.currentpositions[ServoIDs.INDEX_L]==0){targetmovements[ServoIDs.INDEX_L]=0;}
                            spinner_index_L.getValueFactory().setValue(targetmovements[ServoIDs.INDEX_L]);
                            showErrTxt();
                        }
                    }

                }
                
            }); 
            
        SpinnerValueFactory<Integer> spinnerVF_middle_L = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_middle_L.setValue(0);
            spinner_middle_L.setValueFactory(spinnerVF_middle_L);
            spinner_middle_L.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.MIDDLE_L]=spinner_middle_L.getValue(); 
                    if(positiveMovements[ServoIDs.MIDDLE_L]){
                        if(Client.currentpositions[ServoIDs.MIDDLE_L] + targetmovements[ServoIDs.MIDDLE_L] >180){
                            targetmovements[ServoIDs.MIDDLE_L]=180-Client.currentpositions[ServoIDs.MIDDLE_L];
                            spinner_middle_L.getValueFactory().setValue(targetmovements[ServoIDs.MIDDLE_L]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.MIDDLE_L] >= Client.currentpositions[ServoIDs.MIDDLE_L]){
                            targetmovements[ServoIDs.MIDDLE_L]=Client.currentpositions[ServoIDs.MIDDLE_L]-1;
                            if(Client.currentpositions[ServoIDs.MIDDLE_L]==0){targetmovements[ServoIDs.MIDDLE_L]=0;}
                            spinner_middle_L.getValueFactory().setValue(targetmovements[ServoIDs.MIDDLE_L]);
                            showErrTxt();
                        }
                    }

                }
                
            }); 
            
        SpinnerValueFactory<Integer> spinnerVF_ring_L = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_ring_L.setValue(0);
            spinner_ring_L.setValueFactory(spinnerVF_ring_L);
            spinner_ring_L.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.RING_L]=spinner_ring_L.getValue(); 
                    if(positiveMovements[ServoIDs.RING_L]){
                        if(Client.currentpositions[ServoIDs.RING_L] + targetmovements[ServoIDs.RING_L] >180){
                            targetmovements[ServoIDs.RING_L]=180-Client.currentpositions[ServoIDs.RING_L];
                            spinner_ring_L.getValueFactory().setValue(targetmovements[ServoIDs.RING_L]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.RING_L] >= Client.currentpositions[ServoIDs.RING_L]){
                            targetmovements[ServoIDs.RING_L]=Client.currentpositions[ServoIDs.RING_L]-1;
                            if(Client.currentpositions[ServoIDs.RING_L]==0){targetmovements[ServoIDs.RING_L]=0;}
                            spinner_ring_L.getValueFactory().setValue(targetmovements[ServoIDs.RING_L]);
                            showErrTxt();
                        }
                    }

                }
                
            }); 
            
        SpinnerValueFactory<Integer> spinnerVF_little_L = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_little_L.setValue(0);
            spinner_little_L.setValueFactory(spinnerVF_little_L);
            spinner_little_L.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.LITTLE_L]=spinner_little_L.getValue(); 
                    if(positiveMovements[ServoIDs.LITTLE_L]){
                        if(Client.currentpositions[ServoIDs.LITTLE_L] + targetmovements[ServoIDs.LITTLE_L] >180){
                            targetmovements[ServoIDs.LITTLE_L]=180-Client.currentpositions[ServoIDs.LITTLE_L];
                            spinner_little_L.getValueFactory().setValue(targetmovements[ServoIDs.LITTLE_L]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.LITTLE_L] >= Client.currentpositions[ServoIDs.LITTLE_L]){
                            targetmovements[ServoIDs.LITTLE_L]=Client.currentpositions[ServoIDs.LITTLE_L]-1;
                            if(Client.currentpositions[ServoIDs.LITTLE_L]==0){targetmovements[ServoIDs.LITTLE_L]=0;}
                            spinner_little_L.getValueFactory().setValue(targetmovements[ServoIDs.LITTLE_L]);
                            showErrTxt();
                        }
                    }

                }
                
            }); 
            
        SpinnerValueFactory<Integer> spinnerVF_palm_L = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_palm_L.setValue(0);
            spinner_palm_L.setValueFactory(spinnerVF_palm_L);
            spinner_palm_L.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.PALM_L]=spinner_palm_L.getValue(); 
                    if(positiveMovements[ServoIDs.PALM_L]){
                        if(Client.currentpositions[ServoIDs.PALM_L] + targetmovements[ServoIDs.PALM_L] >180){
                            targetmovements[ServoIDs.PALM_L]=180-Client.currentpositions[ServoIDs.PALM_L];
                            spinner_palm_L.getValueFactory().setValue(targetmovements[ServoIDs.PALM_L]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.PALM_L] >= Client.currentpositions[ServoIDs.PALM_L]){
                            targetmovements[ServoIDs.PALM_L]=Client.currentpositions[ServoIDs.PALM_L]-1;
                            if(Client.currentpositions[ServoIDs.PALM_L]==0){targetmovements[ServoIDs.PALM_L]=0;}
                            spinner_palm_L.getValueFactory().setValue(targetmovements[ServoIDs.PALM_L]);
                            showErrTxt();
                        }
                    }

                }
                
            });             

        SpinnerValueFactory<Integer> spinnerVF_forerot_R = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_forerot_R.setValue(0);
            spinner_forerot_R.setValueFactory(spinnerVF_forerot_R);
            spinner_forerot_R.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.FOREARM_ROTATION_R]=spinner_forerot_R.getValue(); 
                    if(positiveMovements[ServoIDs.FOREARM_ROTATION_R]){
                        if(Client.currentpositions[ServoIDs.FOREARM_ROTATION_R] + targetmovements[ServoIDs.FOREARM_ROTATION_R] >180){
                            targetmovements[ServoIDs.FOREARM_ROTATION_R]=180-Client.currentpositions[ServoIDs.FOREARM_ROTATION_R];
                            spinner_forerot_R.getValueFactory().setValue(targetmovements[ServoIDs.FOREARM_ROTATION_R]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.FOREARM_ROTATION_R] >= Client.currentpositions[ServoIDs.FOREARM_ROTATION_R]){
                            targetmovements[ServoIDs.FOREARM_ROTATION_R]=Client.currentpositions[ServoIDs.FOREARM_ROTATION_R]-1;
                            if(Client.currentpositions[ServoIDs.FOREARM_ROTATION_R]==0){targetmovements[ServoIDs.FOREARM_ROTATION_R]=0;}
                            spinner_forerot_R.getValueFactory().setValue(targetmovements[ServoIDs.FOREARM_ROTATION_R]);
                            showErrTxt();
                        }
                    }

                }
                
            });  

        SpinnerValueFactory<Integer> spinnerVF_armrot_R = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_armrot_R.setValue(0);
            spinner_armrot_R.setValueFactory(spinnerVF_armrot_R);
            spinner_armrot_R.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.ARM_ROTATION_R]=spinner_armrot_R.getValue(); 
                    if(positiveMovements[ServoIDs.ARM_ROTATION_R]){
                        if(Client.currentpositions[ServoIDs.ARM_ROTATION_R] + targetmovements[ServoIDs.ARM_ROTATION_R] >180){
                            targetmovements[ServoIDs.ARM_ROTATION_R]=180-Client.currentpositions[ServoIDs.ARM_ROTATION_R];
                            spinner_armrot_R.getValueFactory().setValue(targetmovements[ServoIDs.ARM_ROTATION_R]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.ARM_ROTATION_R] >= Client.currentpositions[ServoIDs.ARM_ROTATION_R]){
                            targetmovements[ServoIDs.ARM_ROTATION_R]=Client.currentpositions[ServoIDs.ARM_ROTATION_R]-1;
                            if(Client.currentpositions[ServoIDs.ARM_ROTATION_R]==0){targetmovements[ServoIDs.ARM_ROTATION_R]=0;}
                            spinner_armrot_R.getValueFactory().setValue(targetmovements[ServoIDs.ARM_ROTATION_R]);
                            showErrTxt();
                        }
                    }

                }
                
            });             
            
        SpinnerValueFactory<Integer> spinnerVF_sldr_R = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_sldr_R.setValue(0);
            spinner_sldr_R.setValueFactory(spinnerVF_sldr_R);
            spinner_sldr_R.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.SHOULDER_FLEX_R]=spinner_sldr_R.getValue(); 
                    if(positiveMovements[ServoIDs.SHOULDER_FLEX_R]){
                        if(Client.currentpositions[ServoIDs.SHOULDER_FLEX_R] + targetmovements[ServoIDs.SHOULDER_FLEX_R] >180){
                            targetmovements[ServoIDs.SHOULDER_FLEX_R]=180-Client.currentpositions[ServoIDs.SHOULDER_FLEX_R];
                            spinner_sldr_R.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_FLEX_R]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.SHOULDER_FLEX_R] >= Client.currentpositions[ServoIDs.SHOULDER_FLEX_R]){
                            targetmovements[ServoIDs.SHOULDER_FLEX_R]=Client.currentpositions[ServoIDs.SHOULDER_FLEX_R]-1;
                            if(Client.currentpositions[ServoIDs.SHOULDER_FLEX_R]==0){targetmovements[ServoIDs.SHOULDER_FLEX_R]=0;}
                            spinner_sldr_R.getValueFactory().setValue(targetmovements[ServoIDs.SHOULDER_FLEX_R]);
                            showErrTxt();
                        }
                    }

                }
                
            });     
            
        SpinnerValueFactory<Integer> spinnerVF_bicep_R = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_bicep_R.setValue(0);
            spinner_bicep_R.setValueFactory(spinnerVF_bicep_R);
            spinner_bicep_R.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.ARM_FLEX_R]=spinner_bicep_R.getValue(); 
                    if(positiveMovements[ServoIDs.ARM_FLEX_R]){
                        if(Client.currentpositions[ServoIDs.ARM_FLEX_R] + targetmovements[ServoIDs.ARM_FLEX_R] >180){
                            targetmovements[ServoIDs.ARM_FLEX_R]=180-Client.currentpositions[ServoIDs.ARM_FLEX_R];
                            spinner_bicep_R.getValueFactory().setValue(targetmovements[ServoIDs.ARM_FLEX_R]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.ARM_FLEX_R] >= Client.currentpositions[ServoIDs.ARM_FLEX_R]){
                            targetmovements[ServoIDs.ARM_FLEX_R]=Client.currentpositions[ServoIDs.ARM_FLEX_R]-1;
                            if(Client.currentpositions[ServoIDs.ARM_FLEX_R]==0){targetmovements[ServoIDs.ARM_FLEX_R]=0;}
                            spinner_bicep_R.getValueFactory().setValue(targetmovements[ServoIDs.ARM_FLEX_R]);
                            showErrTxt();
                        }
                    }

                }
                
            });           
            
        SpinnerValueFactory<Integer> spinnerVF_thumb_R = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_thumb_R.setValue(0);
            spinner_thumb_R.setValueFactory(spinnerVF_thumb_R);
            spinner_thumb_R.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.THUMB_R]=spinner_thumb_R.getValue(); 
                    if(positiveMovements[ServoIDs.THUMB_R]){
                        if(Client.currentpositions[ServoIDs.THUMB_R] + targetmovements[ServoIDs.THUMB_R] >180){
                            targetmovements[ServoIDs.THUMB_R]=180-Client.currentpositions[ServoIDs.THUMB_R];
                            spinner_thumb_R.getValueFactory().setValue(targetmovements[ServoIDs.THUMB_R]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.THUMB_R] >= Client.currentpositions[ServoIDs.THUMB_R]){
                            targetmovements[ServoIDs.THUMB_R]=Client.currentpositions[ServoIDs.THUMB_R]-1;
                            if(Client.currentpositions[ServoIDs.THUMB_R]==0){targetmovements[ServoIDs.THUMB_R]=0;}
                            spinner_thumb_R.getValueFactory().setValue(targetmovements[ServoIDs.THUMB_R]);
                            showErrTxt();
                        }
                    }

                }
                
            }); 

        SpinnerValueFactory<Integer> spinnerVF_index_R = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_index_R.setValue(0);
            spinner_index_R.setValueFactory(spinnerVF_index_R);
            spinner_index_R.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.INDEX_R]=spinner_index_R.getValue(); 
                    if(positiveMovements[ServoIDs.INDEX_R]){
                        if(Client.currentpositions[ServoIDs.INDEX_R] + targetmovements[ServoIDs.INDEX_R] >180){
                            targetmovements[ServoIDs.INDEX_R]=180-Client.currentpositions[ServoIDs.INDEX_R];
                            spinner_index_R.getValueFactory().setValue(targetmovements[ServoIDs.INDEX_R]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.INDEX_R] >= Client.currentpositions[ServoIDs.INDEX_R]){
                            targetmovements[ServoIDs.INDEX_R]=Client.currentpositions[ServoIDs.INDEX_R]-1;
                            if(Client.currentpositions[ServoIDs.INDEX_R]==0){targetmovements[ServoIDs.INDEX_R]=0;}
                            spinner_index_R.getValueFactory().setValue(targetmovements[ServoIDs.INDEX_R]);
                            showErrTxt();
                        }
                    }

                }
                
            }); 
            
        SpinnerValueFactory<Integer> spinnerVF_middle_R = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_middle_R.setValue(0);
            spinner_middle_R.setValueFactory(spinnerVF_middle_R);
            spinner_middle_R.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.MIDDLE_R]=spinner_middle_R.getValue(); 
                    if(positiveMovements[ServoIDs.MIDDLE_R]){
                        if(Client.currentpositions[ServoIDs.MIDDLE_R] + targetmovements[ServoIDs.MIDDLE_R] >180){
                            targetmovements[ServoIDs.MIDDLE_R]=180-Client.currentpositions[ServoIDs.MIDDLE_R];
                            spinner_middle_R.getValueFactory().setValue(targetmovements[ServoIDs.MIDDLE_R]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.MIDDLE_R] >= Client.currentpositions[ServoIDs.MIDDLE_R]){
                            targetmovements[ServoIDs.MIDDLE_R]=Client.currentpositions[ServoIDs.MIDDLE_R]-1;
                            if(Client.currentpositions[ServoIDs.MIDDLE_R]==0){targetmovements[ServoIDs.MIDDLE_R]=0;}
                            spinner_middle_R.getValueFactory().setValue(targetmovements[ServoIDs.MIDDLE_R]);
                            showErrTxt();
                        }
                    }

                }
                
            }); 
            
        SpinnerValueFactory<Integer> spinnerVF_ring_R = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_ring_R.setValue(0);
            spinner_ring_R.setValueFactory(spinnerVF_ring_R);
            spinner_ring_R.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.RING_R]=spinner_ring_R.getValue(); 
                    if(positiveMovements[ServoIDs.RING_R]){
                        if(Client.currentpositions[ServoIDs.RING_R] + targetmovements[ServoIDs.RING_R] >180){
                            targetmovements[ServoIDs.RING_R]=180-Client.currentpositions[ServoIDs.RING_R];
                            spinner_ring_R.getValueFactory().setValue(targetmovements[ServoIDs.RING_R]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.RING_R] >= Client.currentpositions[ServoIDs.RING_R]){
                            targetmovements[ServoIDs.RING_R]=Client.currentpositions[ServoIDs.RING_R]-1;
                            if(Client.currentpositions[ServoIDs.RING_R]==0){targetmovements[ServoIDs.RING_R]=0;}
                            spinner_ring_R.getValueFactory().setValue(targetmovements[ServoIDs.RING_R]);
                            showErrTxt();
                        }
                    }

                }
                
            }); 
            
        SpinnerValueFactory<Integer> spinnerVF_Rittle_R = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_Rittle_R.setValue(0);
            spinner_Rittle_R.setValueFactory(spinnerVF_Rittle_R);
            spinner_Rittle_R.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.LITTLE_R]=spinner_Rittle_R.getValue(); 
                    if(positiveMovements[ServoIDs.LITTLE_R]){
                        if(Client.currentpositions[ServoIDs.LITTLE_R] + targetmovements[ServoIDs.LITTLE_R] >180){
                            targetmovements[ServoIDs.LITTLE_R]=180-Client.currentpositions[ServoIDs.LITTLE_R];
                            spinner_Rittle_R.getValueFactory().setValue(targetmovements[ServoIDs.LITTLE_R]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.LITTLE_R] >= Client.currentpositions[ServoIDs.LITTLE_R]){
                            targetmovements[ServoIDs.LITTLE_R]=Client.currentpositions[ServoIDs.LITTLE_R]-1;
                            if(Client.currentpositions[ServoIDs.LITTLE_R]==0){targetmovements[ServoIDs.LITTLE_R]=0;}
                            spinner_Rittle_R.getValueFactory().setValue(targetmovements[ServoIDs.LITTLE_R]);
                            showErrTxt();
                        }
                    }

                }
                
            }); 
            
        SpinnerValueFactory<Integer> spinnerVF_palm_R = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,180);
            spinnerVF_palm_R.setValue(0);
            spinner_palm_R.setValueFactory(spinnerVF_palm_R);
            spinner_palm_R.valueProperty().addListener(new ChangeListener<Integer>() {

                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    
                    targetmovements[ServoIDs.PALM_R]=spinner_palm_R.getValue(); 
                    if(positiveMovements[ServoIDs.PALM_R]){
                        if(Client.currentpositions[ServoIDs.PALM_R] + targetmovements[ServoIDs.PALM_R] >180){
                            targetmovements[ServoIDs.PALM_R]=180-Client.currentpositions[ServoIDs.PALM_R];
                            spinner_palm_R.getValueFactory().setValue(targetmovements[ServoIDs.PALM_R]);
                            showErrTxt();
                        }
                    }else{
                        if(targetmovements[ServoIDs.PALM_R] >= Client.currentpositions[ServoIDs.PALM_R]){
                            targetmovements[ServoIDs.PALM_R]=Client.currentpositions[ServoIDs.PALM_R]-1;
                            if(Client.currentpositions[ServoIDs.PALM_R]==0){targetmovements[ServoIDs.PALM_R]=0;}
                            spinner_palm_R.getValueFactory().setValue(targetmovements[ServoIDs.PALM_R]);
                            showErrTxt();
                        }
                    }

                }
                
            });                 

        fadeOut=new FadeTransition(Duration.millis(2500),textInvalidAction);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0);

        SHUTDOWN_FLAG=false;
        showGroupExecution=false;
        currentScreen=0;
        backToMVSelect();

    }

    private void hideall(){
        setDefaultValues();
        closeInfoMSG();
        hideTutorial();
        hideMovementExp();
        textInvalidAction.setVisible(false);
        buttonExecuteMovement.setDisable(true);
        buttonExecuteMovement.setVisible(false);
        groupExecution.setVisible(false);
        groupExecution.setDisable(true);
        paneSelectMovementMenu.setDisable(true);
        paneSelectMovementMenu.setVisible(false);
        paneMovementHead.setDisable(true);
        paneMovementHead.setVisible(false);
        paneMovementTorso.setDisable(true);
        paneMovementTorso.setVisible(false);
        paneMovementLArm.setDisable(true);
        paneMovementLArm.setVisible(false);       
        paneMovementLHand.setDisable(true);
        paneMovementLHand.setVisible(false);
        paneMovementRArm.setDisable(true);
        paneMovementRArm.setVisible(false);        
        paneMovementRHand.setDisable(true);
        paneMovementRHand.setVisible(false);
        paneSendingMVOrders.setDisable(true);
        paneSendingMVOrders.setVisible(false);
        buttonBackToMVSelect.setDisable(true);  
        buttonBackToMVSelect.setVisible(false); 
    }

    private void setDefaultPMbuttons_head(){
        buttonPlus_h_rot_selected.setVisible(true);
        buttonPlus_h_rot_selected.setDisable(false);
        buttonPlus_h_rot.setVisible(false);
        buttonPlus_h_rot.setDisable(true);

        buttonMinus_h_rot_selected.setVisible(false);
        buttonMinus_h_rot_selected.setDisable(true);
        buttonMinus_h_rot.setVisible(true);
        buttonMinus_h_rot.setDisable(false);

        buttonPlus_h_tilt_L_selected.setVisible(true);
        buttonPlus_h_tilt_L_selected.setDisable(false);
        buttonPlus_h_tilt_L.setVisible(false);
        buttonPlus_h_tilt_L.setDisable(true);

        buttonMinus_h_tilt_L_selected.setVisible(false);
        buttonMinus_h_tilt_L_selected.setDisable(true);
        buttonMinus_h_tilt_L.setVisible(true);
        buttonMinus_h_tilt_L.setDisable(false);

        buttonPlus_h_tilt_R_selected.setVisible(true);
        buttonPlus_h_tilt_R_selected.setDisable(false);
        buttonPlus_h_tilt_R.setVisible(false);
        buttonPlus_h_tilt_R.setDisable(true);

        buttonMinus_h_tilt_R_selected.setVisible(false);
        buttonMinus_h_tilt_R_selected.setDisable(true);
        buttonMinus_h_tilt_R.setVisible(true);
        buttonMinus_h_tilt_R.setDisable(false);
    }

    private void setDefaultPMbuttons_torso(){
        buttonPlus_t_rot_R_selected.setVisible(true);
        buttonPlus_t_rot_R_selected.setDisable(false);
        buttonPlus_t_rot_R.setVisible(false);
        buttonPlus_t_rot_R.setDisable(true);

        buttonMinus_t_rot_R_selected.setVisible(false);
        buttonMinus_t_rot_R_selected.setDisable(true);
        buttonMinus_t_rot_R.setVisible(true);
        buttonMinus_t_rot_R.setDisable(false);

        buttonPlus_t_rot_L_selected.setVisible(true);
        buttonPlus_t_rot_L_selected.setDisable(false);
        buttonPlus_t_rot_L.setVisible(false);
        buttonPlus_t_rot_L.setDisable(true);

        buttonMinus_t_rot_L_selected.setVisible(false);
        buttonMinus_t_rot_L_selected.setDisable(true);
        buttonMinus_t_rot_L.setVisible(true);
        buttonMinus_t_rot_L.setDisable(false);

        buttonPlus_t_tilt_R_selected.setVisible(true);
        buttonPlus_t_tilt_R_selected.setDisable(false);
        buttonPlus_t_tilt_R.setVisible(false);
        buttonPlus_t_tilt_R.setDisable(true);
        
        buttonMinus_t_tilt_R_selected.setVisible(false);
        buttonMinus_t_tilt_R_selected.setDisable(true);
        buttonMinus_t_tilt_R.setVisible(true);
        buttonMinus_t_tilt_R.setDisable(false);

        buttonPlus_t_tilt_L_selected.setVisible(true);
        buttonPlus_t_tilt_L_selected.setDisable(false);
        buttonPlus_t_tilt_L.setVisible(false);
        buttonPlus_t_tilt_L.setDisable(true);

        buttonMinus_t_tilt_L_selected.setVisible(false);
        buttonMinus_t_tilt_L_selected.setDisable(true);
        buttonMinus_t_tilt_L.setVisible(true);
        buttonMinus_t_tilt_L.setDisable(false);        
    }    

    private void setDefaultPMbuttons_LArm(){
        buttonPlus_forerot_L_selected.setVisible(true);
        buttonPlus_forerot_L_selected.setDisable(false);
        buttonPlus_forerot_L.setVisible(false);
        buttonPlus_forerot_L.setDisable(true);

        buttonMinus_forerot_L_selected.setVisible(false);
        buttonMinus_forerot_L_selected.setDisable(true);
        buttonMinus_forerot_L.setVisible(true);
        buttonMinus_forerot_L.setDisable(false);

        buttonPlus_armrot_L_selected.setVisible(true);
        buttonPlus_armrot_L_selected.setDisable(false);
        buttonPlus_armrot_L.setVisible(false);
        buttonPlus_armrot_L.setDisable(true);

        buttonMinus_armrot_L_selected.setVisible(false);
        buttonMinus_armrot_L_selected.setDisable(true);
        buttonMinus_armrot_L.setVisible(true);
        buttonMinus_armrot_L.setDisable(false);

        buttonPlus_sldr_L_selected.setVisible(true);
        buttonPlus_sldr_L_selected.setDisable(false);
        buttonPlus_sldr_L.setVisible(false);
        buttonPlus_sldr_L.setDisable(true);
        
        buttonMinus_sldr_L_selected.setVisible(false);
        buttonMinus_sldr_L_selected.setDisable(true);
        buttonMinus_sldr_L.setVisible(true);
        buttonMinus_sldr_L.setDisable(false);

        buttonPlus_bicep_L_selected.setVisible(true);
        buttonPlus_bicep_L_selected.setDisable(false);
        buttonPlus_bicep_L.setVisible(false);
        buttonPlus_bicep_L.setDisable(true);

        buttonMinus_bicep_L_selected.setVisible(false);
        buttonMinus_bicep_L_selected.setDisable(true);
        buttonMinus_bicep_L.setVisible(true);
        buttonMinus_bicep_L.setDisable(false);
    }    

    private void setDefaultPMbuttons_LHand(){
        buttonPlus_thumb_L_selected.setVisible(true);
        buttonPlus_thumb_L_selected.setDisable(false);
        buttonPlus_thumb_L.setVisible(false);
        buttonPlus_thumb_L.setDisable(true);

        buttonMinus_thumb_L_selected.setVisible(false);
        buttonMinus_thumb_L_selected.setDisable(true);
        buttonMinus_thumb_L.setVisible(true);
        buttonMinus_thumb_L.setDisable(false);

        buttonPlus_index_L_selected.setVisible(true);
        buttonPlus_index_L_selected.setDisable(false);
        buttonPlus_index_L.setVisible(false);
        buttonPlus_index_L.setDisable(true);

        buttonMinus_index_L_selected.setVisible(false);
        buttonMinus_index_L_selected.setDisable(true);
        buttonMinus_index_L.setVisible(true);
        buttonMinus_index_L.setDisable(false);

        buttonPlus_middle_L_selected.setVisible(true);
        buttonPlus_middle_L_selected.setDisable(false);
        buttonPlus_middle_L.setVisible(false);
        buttonPlus_middle_L.setDisable(true);

        buttonMinus_middle_L_selected.setVisible(false);
        buttonMinus_middle_L_selected.setDisable(true);
        buttonMinus_middle_L.setVisible(true);
        buttonMinus_middle_L.setDisable(false);

        buttonPlus_ring_L_selected.setVisible(true);
        buttonPlus_ring_L_selected.setDisable(false);
        buttonPlus_ring_L.setVisible(false);
        buttonPlus_ring_L.setDisable(true);
        
        buttonMinus_ring_L_selected.setVisible(false);
        buttonMinus_ring_L_selected.setDisable(true);
        buttonMinus_ring_L.setVisible(true);
        buttonMinus_ring_L.setDisable(false);

        buttonPlus_little_L_selected.setVisible(true);
        buttonPlus_little_L_selected.setDisable(false);
        buttonPlus_little_L.setVisible(false);
        buttonPlus_little_L.setDisable(true);

        buttonMinus_little_L_selected.setVisible(false);
        buttonMinus_little_L_selected.setDisable(true);
        buttonMinus_little_L.setVisible(true);
        buttonMinus_little_L.setDisable(false);

        buttonPlus_palm_L_selected.setVisible(true);
        buttonPlus_palm_L_selected.setDisable(false);
        buttonPlus_palm_L.setVisible(false);
        buttonPlus_palm_L.setDisable(true);

        buttonMinus_palm_L_selected.setVisible(false);
        buttonMinus_palm_L_selected.setDisable(true);
        buttonMinus_palm_L.setVisible(true);
        buttonMinus_palm_L.setDisable(false);        
    }

    private void setDefaultPMbuttons_RArm(){
        buttonPlus_forerot_R_selected.setVisible(true);
        buttonPlus_forerot_R_selected.setDisable(false);
        buttonPlus_forerot_R.setVisible(false);
        buttonPlus_forerot_R.setDisable(true);

        buttonMinus_forerot_R_selected.setVisible(false);
        buttonMinus_forerot_R_selected.setDisable(true);
        buttonMinus_forerot_R.setVisible(true);
        buttonMinus_forerot_R.setDisable(false);

        buttonPlus_armrot_R_selected.setVisible(true);
        buttonPlus_armrot_R_selected.setDisable(false);
        buttonPlus_armrot_R.setVisible(false);
        buttonPlus_armrot_R.setDisable(true);

        buttonMinus_armrot_R_selected.setVisible(false);
        buttonMinus_armrot_R_selected.setDisable(true);
        buttonMinus_armrot_R.setVisible(true);
        buttonMinus_armrot_R.setDisable(false);

        buttonPlus_sldr_R_selected.setVisible(true);
        buttonPlus_sldr_R_selected.setDisable(false);
        buttonPlus_sldr_R.setVisible(false);
        buttonPlus_sldr_R.setDisable(true);
        
        buttonMinus_sldr_R_selected.setVisible(false);
        buttonMinus_sldr_R_selected.setDisable(true);
        buttonMinus_sldr_R.setVisible(true);
        buttonMinus_sldr_R.setDisable(false);

        buttonPlus_bicep_R_selected.setVisible(true);
        buttonPlus_bicep_R_selected.setDisable(false);
        buttonPlus_bicep_R.setVisible(false);
        buttonPlus_bicep_R.setDisable(true);

        buttonMinus_bicep_R_selected.setVisible(false);
        buttonMinus_bicep_R_selected.setDisable(true);
        buttonMinus_bicep_R.setVisible(true);
        buttonMinus_bicep_R.setDisable(false);
    }    

    private void setDefaultPMbuttons_RHand(){
        buttonPlus_thumb_R_selected.setVisible(true);
        buttonPlus_thumb_R_selected.setDisable(false);
        buttonPlus_thumb_R.setVisible(false);
        buttonPlus_thumb_R.setDisable(true);

        buttonMinus_thumb_R_selected.setVisible(false);
        buttonMinus_thumb_R_selected.setDisable(true);
        buttonMinus_thumb_R.setVisible(true);
        buttonMinus_thumb_R.setDisable(false);

        buttonPlus_index_R_selected.setVisible(true);
        buttonPlus_index_R_selected.setDisable(false);
        buttonPlus_index_R.setVisible(false);
        buttonPlus_index_R.setDisable(true);

        buttonMinus_index_R_selected.setVisible(false);
        buttonMinus_index_R_selected.setDisable(true);
        buttonMinus_index_R.setVisible(true);
        buttonMinus_index_R.setDisable(false);

        buttonPlus_middle_R_selected.setVisible(true);
        buttonPlus_middle_R_selected.setDisable(false);
        buttonPlus_middle_R.setVisible(false);
        buttonPlus_middle_R.setDisable(true);

        buttonMinus_middle_R_selected.setVisible(false);
        buttonMinus_middle_R_selected.setDisable(true);
        buttonMinus_middle_R.setVisible(true);
        buttonMinus_middle_R.setDisable(false);

        buttonPlus_ring_R_selected.setVisible(true);
        buttonPlus_ring_R_selected.setDisable(false);
        buttonPlus_ring_R.setVisible(false);
        buttonPlus_ring_R.setDisable(true);
        
        buttonMinus_ring_R_selected.setVisible(false);
        buttonMinus_ring_R_selected.setDisable(true);
        buttonMinus_ring_R.setVisible(true);
        buttonMinus_ring_R.setDisable(false);

        buttonPlus_Rittle_R_selected.setVisible(true);
        buttonPlus_Rittle_R_selected.setDisable(false);
        buttonPlus_Rittle_R.setVisible(false);
        buttonPlus_Rittle_R.setDisable(true);

        buttonMinus_Rittle_R_selected.setVisible(false);
        buttonMinus_Rittle_R_selected.setDisable(true);
        buttonMinus_Rittle_R.setVisible(true);
        buttonMinus_Rittle_R.setDisable(false);

        buttonPlus_palm_R_selected.setVisible(true);
        buttonPlus_palm_R_selected.setDisable(false);
        buttonPlus_palm_R.setVisible(false);
        buttonPlus_palm_R.setDisable(true);

        buttonMinus_palm_R_selected.setVisible(false);
        buttonMinus_palm_R_selected.setDisable(true);
        buttonMinus_palm_R.setVisible(true);
        buttonMinus_palm_R.setDisable(false);        
    }

    private void setDefaultValues(){
        for (int i = 0; i < positiveMovements.length; i++) {
            targetmovements[i]=0;positiveMovements[i]=true;
        }
    }

    private void swtichBackToPreviousScreen(){
        switch (currentScreen) {
            case 1:swapToMoveHead();
                break;
            case 2:swapToMoveTorso();
                break;                            
            case 3:swapToMoveLeftArm();
                break;                            
            case 4:swapToMoveLeftHand();
                break;                                   
            case 5:swapToMoveRightArm();
                break;                            
            case 6:swapToMoveRightHand();
                break;                            
        }
    }

}