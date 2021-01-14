package org.Arc.Web.Conex;

import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.AWTException;

import org.Arc.Client.Student;
import org.Arc.Web.ArcDriver;
import org.Arc.Web.Website;
import org.openqa.selenium.WebElement;

public class ConexDriver extends ArcDriver{
    public ConexDriver() {
        super(Website.CONEXED);
        signIn();
    }

    public void signIn() {
        switchTo().frame("login-iframe");
        WebElement signInBtn = findElementById("craniumcafe-button");
        signInBtn.click();

        WebElement userField = findElementById("login-text");
        userField.sendKeys(getPage().getUsername());

        WebElement passwordField = findElementById("password-text");
        passwordField.sendKeys(getPage().getPassword());

        WebElement loginBtn = findElementById("login-button");
        loginBtn.click();

        manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        switchTo().defaultContent();
        switchTo().frame("cafe-frame");
    }
    
    public void openMenuChoice(MenuChoice choice) {
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ESCAPE);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        WebElement menuButton = findElementByCssSelector(choice.getCSSSelector());
        System.out.println(menuButton.getText());
        menuButton.click();
        manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }
    
    public void addBreakoutRoom(Student student, String hour, String minute, String time) {
        openMenuChoice(MenuChoice.BREAKOUT_ROOMS);
        WebElement addRoomBtn = findElementByCssSelector(
                "#breakout-rooms-dialog > div.modal-footer > div:nth-child(1) > button");
        addRoomBtn.click();

        String roomTitle = "(" + hour + ":" + minute + " " + time + ") " + student.getFirstName() + " "
                + student.getLastName();
        switchTo().alert().sendKeys(roomTitle);
        switchTo().alert().accept();
    }
    
    private void changeRoomsStatus(boolean isOpen) {
        openMenuChoice(MenuChoice.BREAKOUT_ROOMS);
        WebElement roomsButton = findElementByCssSelector(
                "#breakout-rooms-dialog > div.modal-footer > div:nth-child(2) > button");
        String roomStatus = roomsButton.getText();
        System.out.println("\n" + roomStatus + "\n");
        if (roomStatus.equals("Open All Rooms") && isOpen) {
            roomsButton.click();
        }
        else if (roomStatus.equals("Close All Rooms") && !isOpen) {
            roomsButton.click();
            switchTo().alert().accept();
        }
    }

    public void changeClassroomAccess(boolean isOpen) {
        String currentStatus = findElementByCssSelector(MenuChoice.OPEN.getCSSSelector()).getText().trim();
        if (currentStatus.equals("Open") && !isOpen) {
            openMenuChoice(MenuChoice.OPEN);
            switchTo().alert().accept();
        } else if (currentStatus.equals("Private") && isOpen) {
            openMenuChoice(MenuChoice.OPEN);
        }

    }
    
    public void uploadDocument() {
        openMenuChoice(MenuChoice.DOCUMENT_LIBRARY);
    
        String filePath = "";
        final JFileChooser fileChooser = new JFileChooser();
        int choiceVal = fileChooser.showOpenDialog(null);
        switch (choiceVal) {
            case JFileChooser.APPROVE_OPTION:
                filePath = fileChooser.getSelectedFile().getAbsolutePath();
                break;
            default:
                return;
        }

        WebElement docBtn = findElementByCssSelector(
                "#document-library-dialog > div.modal-content > div > div.document-library-content > div:nth-child(1) > div:nth-child(2) > button");
        docBtn.click();

        WebElement docUpload = findElementById("user-library-upload");
        docUpload.sendKeys(filePath);
        docUpload.submit();
    }
    public static void main(String[] args) {
        ConexDriver test = new ConexDriver();
        test.uploadDocument();
    }

    private enum MenuChoice {
        DOCUMENT_LIBRARY, BREAKOUT_ROOMS, INVITE, OPEN;

        public String getCSSSelector() {
            String menuSelector = "#menu-buttons > button:nth-child(";
            int choice = 1;
            switch (this) {
                case DOCUMENT_LIBRARY:
                    choice = 1;
                    break;
                case BREAKOUT_ROOMS:
                    choice = 2;
                    break;
                case INVITE:
                    choice = 3;
                    break;
                case OPEN:
                    choice = 4;
                    break;   
            }
            return menuSelector + choice + ")";
        }
    }
}
