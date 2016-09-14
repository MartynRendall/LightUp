package com.rendall.martyn.lightup;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import net.danlew.android.joda.JodaTimeAndroid;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        Location location = new Location("55.949031", "-2.950737");

        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, "Europe/London");

        Calendar cal = calculator.getOfficialSunsetCalendarForDate(Calendar.getInstance());


        System.out.println(cal);
        System.out.println(calculator.getOfficialSunsetForDate(Calendar.getInstance()));
        System.out.println(cal.getTimeInMillis());
        //JodaTimeAndroid.init();
    }
}