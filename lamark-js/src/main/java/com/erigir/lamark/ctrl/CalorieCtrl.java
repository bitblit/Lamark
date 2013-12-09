package com.erigir.pinch.ctrl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.PrintWriter;

/**
 * cweiss : 2/22/13 10:07 AM
 */
@Controller
public class CalorieCtrl {
    private static final float CALORIC_BASE = 11F;
    private static final float METABOLIC_RATE = 0.35F;
    private static final float CALS_PER_POUND = 3500;
    private static final float CAL_DEFICIT_PER_POUND = 30;

    @RequestMapping(value = "/Calorie/{weight}/{bodyFatPercent}/{targetBodyFatPercent}")
    public void process(
            @PathVariable("weight")float weight,
            @PathVariable("bodyFatPercent")float bfp,
            @PathVariable("targetBodyFatPercent")float tbfp,
            PrintWriter pw
           ) {

        final float fs = (weight * (bfp / 100));
        final float lbm = (weight - fs);
        final float tweight = (lbm / ((100 - tbfp) / 100));
        final float fe = (tweight * (tbfp / 100));
        final float deficit = (fs * CAL_DEFICIT_PER_POUND);

        final float baseCals = (weight * CALORIC_BASE);
        final float mCals = (baseCals + (baseCals * METABOLIC_RATE));

        final float daysToReach = (((fs - fe) * CALS_PER_POUND) / ((fs + fe) * 15));

        pw.println("<html><body><pre>");
        pw.println("Current Pounds of Fat: " + fs);
        pw.println("Current Lean Body Mass: " + lbm);
        pw.println("Target Weight: " + tweight);
        pw.println("");
        pw.println("Required calories per day: " + (mCals - deficit));
        pw.println("Days to reach goal: " + daysToReach);
        pw.println("</pre></body></html>");
    }

    public static void print(final String value) {
        System.out.println(value);
    }


}
