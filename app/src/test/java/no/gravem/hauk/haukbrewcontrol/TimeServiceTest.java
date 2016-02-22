package no.gravem.hauk.haukbrewcontrol;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Test;

import java.sql.Time;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by anne-marit.gravem on 21.02.2016.
 */
public class TimeServiceTest {

    @Test
    public void test_getSecondsToStartForOneDay(){
        long secondsInADay = 86400;
        DateTime timeSet = new DateTime(2000, 1, 2, 00, 00, 00);

        long actualSeconds = TimeService.getSecondsToStart(timeSet);
        Assert.assertEquals(secondsInADay, actualSeconds);
    }

    @Test
    public void test_getSecondsToStart(){
        long secondsSince2000 = 509486400;
        DateTime timeSet = new DateTime(2016, 2 , 22, 20, 00);

        long actualSeconds = TimeService.getSecondsToStart(timeSet);
        Assert.assertEquals(secondsSince2000, actualSeconds);
    }

    @Test
    public void test_getDateTimeFromDuration(){
        DateTime timeSet = new DateTime(2016, 2 , 22, 20, 00);
        int secondsSince2000 = 509486400;

        DateTime givenTime = TimeService.getDateTimeFromSeconds(secondsSince2000);
        Assert.assertEquals(timeSet, givenTime);
    }
}
