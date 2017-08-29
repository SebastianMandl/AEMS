package com.example.knoll.aems.data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Niklas on 29.08.2017.
 */

public class TimeFrame {
    private Date startDate;
    private Date endDate;

    public TimeFrame(Date start, Date end) {
        this.startDate = start;
        this.endDate = end;
    }

    public Date getStart() {
        return this.startDate;
    }

    public Date getEnd() {
        return this.endDate;
    }

    public String getTimeframeString(String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        StringBuilder builder = new StringBuilder();
        builder.append(sdf.format(startDate)).append(" - ").append(sdf.format(endDate));
        return builder.toString();
    }
}
