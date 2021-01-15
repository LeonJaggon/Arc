package org.Arc.Web.Conex;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import org.Arc.Client.Student;
import org.Arc.Web.ArcDriver;
import org.Arc.Web.Website;
import org.Arc.Web.Acuity.AcuityDriver;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class ConexDriver extends ArcDriver {
    List<BreakoutRoom> breakoutRooms;

    public ConexDriver() {
        super(Website.CONEXED);
        signIn();
        updateBreakoutRooms();
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
        Actions action = new Actions(this);
        action.sendKeys(Keys.ESCAPE);
        action.perform();
        
        WebElement menuButton = findElementByCssSelector(choice.getCSSSelector());
        menuButton.click();
        manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }
    
    public void addBreakoutRoomsFromAcuity() {
        AcuityDriver acuityDriver = new AcuityDriver();
        List<ConexStudent> onlineStudents = acuityDriver.getScheduledStudents();
        acuityDriver.quit();

        for (ConexStudent student : onlineStudents)
            addBreakoutRoom(student, student.getTime());
    }
    
    public void addBreakoutRoom(Student student, String time) {
        
        openMenuChoice(MenuChoice.BREAKOUT_ROOMS);
        WebElement addRoomBtn = findElementByCssSelector(
                "#breakout-rooms-dialog > div.modal-footer > div:nth-child(1) > button");
        addRoomBtn.click();

        String roomTitle = "(" + time + ") " + student.getFirstName() + " "
                + student.getLastName();
        switchTo().alert().sendKeys(roomTitle);
        switchTo().alert().accept();
    }
    
    private void changeRoomsStatus(boolean isOpen) {
        openMenuChoice(MenuChoice.BREAKOUT_ROOMS);
        WebElement roomsButton = findElementByCssSelector(
                "#breakout-rooms-dialog > div.modal-footer > div:nth-child(2) > button");
        String roomStatus = roomsButton.getText();
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

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        WebElement docUpload = findElementById("user-library-upload");
        docUpload.sendKeys(filePath);
    }
    
    public void updateBreakoutRooms() {
        WebElement userTab = findElementByCssSelector("#menu-user-tabs > button:nth-child(2)");
        userTab.click();

        List<WebElement> titles = findElementsByClassName("breakout-room-title");
        List<WebElement> buttons = findElementsByClassName("classroom-user-button");

        breakoutRooms = new ArrayList<>();

        for (int i = 0; i < titles.size(); i++) {
            String currTitle = titles.get(i).getText();
            WebElement currBtn = buttons.get(i);
            breakoutRooms.add(new BreakoutRoom(currTitle, currBtn));
        }
    }
    
    public void printCurrentRooms() {
        for (BreakoutRoom room : breakoutRooms) {
            System.out.println(room.getTitle());
            room.getEnterButton().click();
        }
            
    }
    
    public static void main(String[] args) {
        ConexDriver test = new ConexDriver();
        test.addBreakoutRoomsFromAcuity();
        System.out.println("------DONE------");
        test.changeRoomsStatus(true);
    }

    private class BreakoutRoom {
        private String title;
        private WebElement enterBtn;

        public BreakoutRoom(String title, WebElement enterBtn) {
            this.title = title;
            this.enterBtn = enterBtn;
        }

        public String getTitle() {
            return title;
        }

        public WebElement getEnterButton() {
            return enterBtn;
        }
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
