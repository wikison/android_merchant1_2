package com.zemult.merchant.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.zemult.merchant.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期时间选择控件 使用方法： private EditText inputDate;//需要设置的日期时间文本编辑框 private String
 * initDateTime="2012年9月3日 14:44",//初始日期时间值 在点击事件中使用：
 * inputDate.setOnClickListener(new OnClickListener() {
 *
 * @author
 * @Override public void onClick(View v) { DateTimePickDialogUtil
 * dateTimePicKDialog=new
 * DateTimePickDialogUtil(SinvestigateActivity.this,initDateTime);
 * dateTimePicKDialog.dateTimePicKDialog(inputDate);
 * <p/>
 * } });
 */
public class DateTimePickDialogUtil implements OnDateChangedListener,
        OnTimeChangedListener {
    private DatePicker datePicker;
    private TimePicker timePicker;
    private AlertDialog ad;
    private String dateTime;
    private String initDateTime;
    private Activity activity;
    private String tipInfo;
    private int mhaslimit;
    /**
     * 日期时间弹出选择框构造函数
     *
     * @param activity     ：调用的父activity
     * @param initDateTime 初始日期时间值,作为弹出窗口的标题和日期时间初始值
     */
    public DateTimePickDialogUtil(Activity activity, String initDateTime, String tip,int haslimit) {
        this.activity = activity;
        this.initDateTime = toSuitDateTime(initDateTime);
        this.tipInfo = tip;
        this.mhaslimit=haslimit;

    }

    /**
     * 截取子串
     *
     * @param srcStr      源串
     * @param pattern     匹配模式
     * @param indexOrLast
     * @param frontOrBack
     * @return
     */
    public static String spliteString(String srcStr, String pattern,
                                      String indexOrLast, String frontOrBack) {
        String result = "";
        int loc = -1;
        if (indexOrLast.equalsIgnoreCase("index")) {
            loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
        } else {
            loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
        }
        if (frontOrBack.equalsIgnoreCase("front")) {
            if (loc != -1)
                result = srcStr.substring(0, loc); // 截取子串
        } else {
            if (loc != -1)
                result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
        }
        return result;
    }

    /**
     * 日期字符串格式化
     *
     * @param dateStr
     * @return
     */
    private String toSuitDateTime(String dateStr) {
        Date date = new Date();
        try {
            if (dateStr.equals("")) {
                dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
            }
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateStr);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return DateTimeUtil.formatDate(date, "yyyy年MM月dd日 HH:mm");
    }

    public void init(DatePicker datePicker, TimePicker timePicker) {
        Calendar calendar = Calendar.getInstance();
        if (!(null == initDateTime || "".equals(initDateTime))) {
            calendar = this.getCalendarByInintData(initDateTime);
        } else {
            initDateTime = calendar.get(Calendar.YEAR) + "年"
                    + calendar.get(Calendar.MONTH) + "月"
                    + calendar.get(Calendar.DAY_OF_MONTH) + "日 "
                    + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                    + calendar.get(Calendar.MINUTE);
        }

        datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), this);
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

    /**
     * 弹出日期时间选择框方法
     *
     * @param inputDate :为需要设置的日期时间文本编辑框
     * @return
     */
    public AlertDialog dateTimePicKDialog(final TextView inputDate) {
        final LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.common_datetime, null);
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
        timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);

        resizePikcer(datePicker);//调整datepicker大小
        resizePikcer(timePicker);//调整timepicker大小


        timePicker.setIs24HourView(true);
        init(datePicker, timePicker);
        timePicker.setOnTimeChangedListener(this);
        ad = new AlertDialog.Builder(activity)
                .setTitle(initDateTime)
                .setView(dateTimeLayout)
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        timePicker.clearFocus();
                        datePicker.clearFocus();
                        if (isDateAfter(datePicker, timePicker)) {
                            if (mhaslimit == 1) {
                                if (isIn7Days(datePicker, timePicker)) {
                                    inputDate.setText(dateTime);
                                } else {
                                    ToastUtil.showMessage("请选择7天以内的时间");
                                }

                            } else if (mhaslimit == 2) {
                                if (isIn180Days(datePicker, timePicker) && isOut30Days(datePicker, timePicker)) {
                                    inputDate.setText(dateTime);
                                } else {
                                    ToastUtil.showMessage("请选择大于1个月小于6个月的时间");
                                }
                            } else {
                                inputDate.setText(dateTime);
                            }
                        }
                        else{
                            ToastUtil.showMessage(tipInfo);
                        }


                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        //默认截止时间为一天后
                        calendar.add(Calendar.DATE, 1);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        dateTime = sdf.format(calendar.getTime());
                        inputDate.setText(dateTime);
                    }
                }).show();

        onDateChanged(null, 0, 0, 0);
        return ad;
    }

    private void resizePikcer(FrameLayout tp) {
        List<NumberPicker> npList = findNumberPicker(tp);
        for (NumberPicker np : npList) {
            resizeNumberPicker(np);
        }
    }

    /*
 * 调整numberpicker大小
 */
    private void resizeNumberPicker(NumberPicker np) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(activity,40), LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        np.setLayoutParams(params);
    }

    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;
        if (null != viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    npList.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        return result;
                    }
                }
            }
        }
        return npList;
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        onDateChanged(null, 0, 0, 0);
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
        // 获得日历实例
        if (isDateAfter(datePicker, timePicker)) {
            if( mhaslimit==1){
                if (isIn7Days(datePicker, timePicker)) {
                    Calendar calendar = Calendar.getInstance();

                    calendar.set(datePicker.getYear(), datePicker.getMonth(),
                            datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
                            timePicker.getCurrentMinute());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    dateTime = sdf.format(calendar.getTime());
                    ad.setTitle(dateTime);
                } else {
                    ToastUtil.showMessage("请选择7天以内的时间");
                }
            }
           else if( mhaslimit==2){
                if (isIn180Days(datePicker, timePicker)&&isOut30Days(datePicker, timePicker)) {
                    Calendar calendar = Calendar.getInstance();

                    calendar.set(datePicker.getYear(), datePicker.getMonth(),
                            datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
                            timePicker.getCurrentMinute());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    dateTime = sdf.format(calendar.getTime());
                    ad.setTitle(dateTime);
                } else {
                    ToastUtil.showMessage("请选择大于1个月小于6个月的时间");
                }
            }
            else{
                Calendar calendar = Calendar.getInstance();

                calendar.set(datePicker.getYear(), datePicker.getMonth(),
                        datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                dateTime = sdf.format(calendar.getTime());
                ad.setTitle(dateTime);
            }

        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            //默认截止时间为一天后
            calendar.add(Calendar.DATE, 1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            dateTime = sdf.format(calendar.getTime());
            ad.setTitle(dateTime);
            ToastUtil.showMessage(tipInfo);
        }
    }

    private boolean isDateAfter(DatePicker tempView, TimePicker timePicker) {
        Calendar mCalendar = Calendar.getInstance();
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(tempView.getYear(), tempView.getMonth(),
                tempView.getDayOfMonth(), timePicker.getCurrentHour(),
                timePicker.getCurrentMinute(), 0);
        return tempCalendar.after(mCalendar);
    }

    private boolean isIn7Days(DatePicker tempView, TimePicker timePicker) {
        Calendar mCalendar = Calendar.getInstance();
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(tempView.getYear(), tempView.getMonth(),
                tempView.getDayOfMonth(), timePicker.getCurrentHour(),
                timePicker.getCurrentMinute(), 0);
        long templong = tempCalendar.getTimeInMillis();
        long nowlong = mCalendar.getTimeInMillis();
        if (templong - nowlong < 7 * 24 * 60 * 60 * 1000) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isIn180Days(DatePicker tempView, TimePicker timePicker) {
        Calendar mCalendar = Calendar.getInstance();
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(tempView.getYear(), tempView.getMonth(),
                tempView.getDayOfMonth(), timePicker.getCurrentHour(),
                timePicker.getCurrentMinute(), 0);
        long templong = tempCalendar.getTimeInMillis();
        long nowlong = mCalendar.getTimeInMillis();
        if (templong - nowlong < ((long)180 * (long)24 * (long)60 * (long)60 * (long)1000)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isOut30Days(DatePicker tempView, TimePicker timePicker) {
        Calendar mCalendar = Calendar.getInstance();
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(tempView.getYear(), tempView.getMonth(),
                tempView.getDayOfMonth(), timePicker.getCurrentHour(),
                timePicker.getCurrentMinute(), 0);
        long templong = tempCalendar.getTimeInMillis();
        long nowlong = mCalendar.getTimeInMillis();
        if (templong - nowlong > 30 * 24 * 60 * 60 * 1000) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
     *
     * @param initDateTime 初始日期时间值 字符串型
     * @return Calendar
     */
    private Calendar getCalendarByInintData(String initDateTime) {
        Calendar calendar = Calendar.getInstance();

        // 将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒
        String date = spliteString(initDateTime, "日", "index", "front"); // 日期
        String time = spliteString(initDateTime, "日", "index", "back"); // 时间

        String yearStr = spliteString(date, "年", "index", "front"); // 年份
        String monthAndDay = spliteString(date, "年", "index", "back"); // 月日

        String monthStr = spliteString(monthAndDay, "月", "index", "front"); // 月
        String dayStr = spliteString(monthAndDay, "月", "index", "back"); // 日

        String hourStr = spliteString(time, ":", "index", "front"); // 时
        String minuteStr = spliteString(time, ":", "index", "back"); // 分

        int currentYear = Integer.valueOf(yearStr.trim()).intValue();
        int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
        int currentDay = Integer.valueOf(dayStr.trim()).intValue();
        int currentHour = Integer.valueOf(hourStr.trim()).intValue();
        int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();

        calendar.set(currentYear, currentMonth, currentDay, currentHour,
                currentMinute);
        return calendar;
    }

}
