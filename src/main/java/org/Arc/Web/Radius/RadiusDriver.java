package org.Arc.Web.Radius;
import org.Arc.Web.ArcDriver;
import org.Arc.Web.Website;
import org.Arc.Client.Center;
import org.Arc.Client.Student;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.TimeUnit;

public class RadiusDriver extends ArcDriver{
    public RadiusDriver() {
        super(Website.RADIUS);
        signIn();
    }

    public void signIn() {
        assert (getPage() == Website.RADIUS);

        Website radiusPage = this.getPage();
        String username = radiusPage.getUsername();
        String password = radiusPage.getPassword();

        WebElement signInField = this.findElement(By.id("UserName"));
        signInField.sendKeys(username);

        WebElement passwordField = this.findElement(By.id("Password"));
        passwordField.sendKeys(password);
        passwordField.submit();
    }

    public void search(String key) {
        assert (key != null && !key.isBlank() && !key.isEmpty());

        WebElement searchBtn = this.findElementById("SearchIcon");
        searchBtn.click();

        WebElement searchInput = this.findElementById("ContactSearch");
        searchInput.sendKeys(key);

        WebElement searchSumbit = this.findElementById("globalbtnsearch");
        searchSumbit.click();

        manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        List<WebElement> searchResults = this.findElementsByClassName("linker");
        for (WebElement elem : searchResults)
            System.out.println(elem.getText());
    }
    
    public List<Student> searchStudent(String studentName) {
        get("https://radius.mathnasium.com/Student");

        manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        WebElement searchField = this.findElementById("StudentNameSearch");
        searchField.sendKeys(studentName);

        WebElement searchBtn = this.findElementById("btnsearch");
        searchBtn.click();
        manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        boolean resultsPresent = findElementsByCssSelector("#gridStudent > table > tbody > tr:nth-child(1)")
                .size() != 0;

        if (!resultsPresent)
            quit();

        int[] childIndices = { 1, 2, 3, 8, 10 };
        List<Student> studentResults = new ArrayList<>();

        int counter = 1;
        try {
            while (true) {
                String rowSelector = "#gridStudent > table > tbody > tr:nth-child(" + counter + ")";
                Student currStudent = new Student();
                for (int i : childIndices) {
                    String gridSelector = rowSelector + " > td:nth-child(" + i + ")";
                    WebElement currCell = findElementByCssSelector(gridSelector);
                    String info = currCell.getText();

                    switch (i) {
                        case 1:
                            WebElement linkElement = findElementByCssSelector(gridSelector + " > a");

                            String link = linkElement.getAttribute("href");
                            String ID = link.replace("https://radius.mathnasium.com/Student/Details/", "");

                            currStudent.setID(ID);
                            currStudent.setFirstName(info);
                            break;
                        case 2:
                            currStudent.setLastName(info);
                            break;
                        case 3:
                            if (info.equals("College"))
                                currStudent.setGrade(13);
                            else
                                currStudent.setGrade(Integer.parseInt(info));
                            break;
                        // case 8:
                        //     System.out.println(info);
                        //     break;
                        case 10:
                            currStudent.setCenter(Center.toCenter(info));
                            break;
                    }
                }
                studentResults.add(currStudent);
                counter += 1;
            }
        } catch (NoSuchElementException e) {
            return studentResults;
        }
    }

    public void openStudentProfile(Student s) {
        assert (!s.getID().equals(null));
        get("https://radius.mathnasium.com/Student/Details/" + s.getID());
    }

    public void markAttendance(Student student, String time) {
        get("https://radius.mathnasium.com/Attendance/Roster");

        String[] splitTime = time.split(":");
        int hour = Integer.parseInt(splitTime[0]);
        int next_hour = hour + 1;
        String next_time = next_hour + ":" + splitTime[1];
        
        System.out.println(next_time);
        manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        List<WebElement> fNames = findElementsByClassName("StudentFirstNameTD");
        List<WebElement> lNames = findElementsByClassName("StudentLastNameTD");
        List<WebElement> timeInInputs = findElementsByClassName("timePickerArrival");
        List<WebElement> timeOutInputs = findElementsByClassName("timePickerDeparture");

        String firstName = student.getFirstName();
        String lastName = student.getLastName();

        for (int i = 0; i < fNames.size(); i++) {
            String fName = fNames.get(i).getText();
            String lName = lNames.get(i).getText();

            if (fName.equals(firstName) && lName.equals(lastName)) {
                int adjustI = 2 * i + 1;
                WebElement inInput = timeInInputs.get(adjustI);
                WebElement outInput = timeOutInputs.get(adjustI);

                inInput.sendKeys("");
                inInput.sendKeys(time);
                inInput.sendKeys(Keys.TAB);
                inInput.sendKeys(Keys.TAB);

                manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

                outInput.sendKeys("");
                outInput.sendKeys(next_time);
                outInput.sendKeys(Keys.TAB);
                break;
            }
        }
    }
    
    public void openLearningPlan(Student s) {
        openStudentProfile(s);

    }
    
    public static void main(String[] args) {
        RadiusDriver test = new RadiusDriver();
        test.markAttendance(new Student(null,"Piper", "Mancuso"), "4:00 PM");
    }
}