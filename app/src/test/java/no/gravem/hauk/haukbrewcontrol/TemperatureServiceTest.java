package no.gravem.hauk.haukbrewcontrol;

import junit.framework.Assert;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by anne-marit.gravem on 22.02.2016.
 */
public class TemperatureServiceTest {

    @Test
    public void test_getPLSFormattedTemperatureInt_willWorkWithOneDecimal(){
        int temperature = 201;

        int temp = TemperatureService.getPLSFormattedTemperatureInt("20.1");
        Assert.assertEquals(temperature, temp);
    }

    @Test
    public void test_getPLSFormattedTemperatureInt_willWorkWithDecimals(){
        int temperature = 203;

        int temp = TemperatureService.getPLSFormattedTemperatureInt("20.25");
        Assert.assertEquals(temperature, temp);
    }

    @Test
    public void test_getPLSFormattedTemperatureInt_willWorkWithoutDot(){
        int temperature = 200;

        int temp = TemperatureService.getPLSFormattedTemperatureInt("20");
        Assert.assertEquals(temperature, temp);
    }

    @Test
    public void test_getPLSFormattedTemperatureInt_willWorkWith0(){
        int temperature = 0;

        int temp = TemperatureService.getPLSFormattedTemperatureInt("0");
        Assert.assertEquals(temperature, temp);
    }

}
