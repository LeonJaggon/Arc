package org.Arc.Web.Acuity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.Arc.Web.ArcDriver;
import org.Arc.Web.Website;
import org.Arc.Web.Conex.ConexStudent;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class AcuityDriver extends ArcDriver {
    public AcuityDriver() {
        super(Website.ACUITY);
        signIn();
    }

    public void signIn() {
        WebElement submitButton = findElementById("next-button");

        WebElement loginField = findElementById("username");
        loginField.sendKeys(Website.ACUITY.getUsername());

        submitButton.click();
        manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

        WebElement passwordField = findElementById("password");
        passwordField.sendKeys(Website.ACUITY.getPassword());

        submitButton.click();
    }

    public List<ConexStudent> getScheduledStudents() {
        List<ConexStudent> schStudents = new ArrayList<>();

        get("https://secure.acuityscheduling.com/reports.php");

        WebElement dateSelectElement = findElementById("type");
        Select dateSelect = new Select(dateSelectElement);
        dateSelect.selectByVisibleText("Custom date range...");

        manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String currDate = dateFormat.format(new Date());

        WebElement fromDateInput = findElementById("startDate-input");
        WebElement toDateInput = findElementById("endDate-input");

        fromDateInput.sendKeys(currDate);
        toDateInput.sendKeys(currDate);
        
        WebElement calElement = findElementByCssSelector("#report-form > p:nth-child(4) > select:nth-child(2)");
        Select calSelect = new Select(calElement);
        calSelect.selectByVisibleText("School Year 2020/2021 @HOME");

        manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        
        WebElement submitButton = findElementByCssSelector("#report-form > p:nth-child(4) > input");
        submitButton.click();
        
        WebElement expandAppointments = findElementByCssSelector("#reports-page > a");
        expandAppointments.click();

        List<WebElement> nameElements = findElementsByClassName("client-name");
        List<WebElement> timeElements = findElementsByClassName("start-time");
        for (int i = 0; i < nameElements.size(); i++) {
            String time = timeElements.get(i).getText();

            String[] splitName = nameElements.get(i).getText().split(" ");
            String firstName = splitName[0];
            String lastName = splitName[1];
            schStudents.add(new ConexStudent(firstName, lastName, time));
        }
    
        return schStudents;
    }

    public static void main(String[] args) {
        AcuityDriver test = new AcuityDriver();
        test.getScheduledStudents();
    }

    
}

