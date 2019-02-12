package com.playgilround.schedule.client.Schedule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.android.material.navigation.NavigationView;
import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.activity.FriendActivity;
import com.playgilround.schedule.client.activity.ManyScheduleActivity;
import com.playgilround.schedule.client.activity.ScheduleInfoActivity;
import com.playgilround.schedule.client.model.MonthEnum;
import com.playgilround.schedule.client.model.Schedule;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.playgilround.schedule.client.activity.ScheduleInfoActivity.ADD_SCHEDULE;

/**
 * 18-12-26
 * Schedule Calendar Activity
 * added by CHO
 * Test
 */
public class ScheduleCalendarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ScheduleContract.View {

    static final String TAG = ScheduleCalendarActivity.class.getSimpleName();

    @BindView(R.id.drawerLayout)
    DrawerLayout drawer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.naviView)
    NavigationView navigationView;

    @BindView(R.id.calendarView)
    CalendarView calendarView;

    @BindView(R.id.btn_save)
    Button saveBtn;

    private ScheduleContract.Presenter mPresenter;

    String strMDay, strManyDay;
    int strMYear, strMonth;
    Realm realm;

    List<EventDay> events;
    private RealmResults<Schedule> realmSchedule; //저장된 스케줄 RealmResults

    String currentCalendar = "";
    View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        ButterKnife.bind(this);
        setTitle(getString(R.string.text_schedule_calendar));
        events = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        events.add(new EventDay(calendar, R.drawable.sample_icon_3));
        realm = Realm.getDefaultInstance();

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

        new ScheduleCalendarPresenter(this);

        //날짜 클릭 시 다이얼로그
        calendarView.setOnDayClickListener(eventDay -> {
            //유저가 클릭한 날짜와, 현재 클릭되어있는 날짜가 같을 경우에만 Dialog 표시
            if (currentCalendar.equals(eventDay.getCalendar().getTime().toString())) {
                setDialogDate(eventDay.getCalendar().getTime().toString());
            }
            currentCalendar = eventDay.getCalendar().getTime().toString();

        });

        //다음 달로 이동
        calendarView.setOnForwardPageChangeListener(this::getScheduleRealm);

        //전 달로 이동
        calendarView.setOnPreviousPageChangeListener(this::getScheduleRealm);

        getScheduleRealm();

        saveBtn.setOnClickListener(v -> {
            if (calendarView.getSelectedDates().size() > 1) {
                //2개 이상일 경우에만 다중 저장 실행.
                ArrayList<Calendar> arrTime = new ArrayList<>(calendarView.getSelectedDates());
                ArrayList<String> arrRetTime = new ArrayList<>();
                for (Calendar retTime : arrTime) {
                    try {
                        Date date = new SimpleDateFormat(getString(R.string.text_date_all_format), Locale.ENGLISH).parse(retTime.getTime().toString());
                        long milliseconds = date.getTime();

                        DateTime dateTime = new DateTime(Long.valueOf(milliseconds), DateTimeZone.UTC);

                        strManyDay = dateTime.plusHours(9).toString(getString(R.string.text_date_year_month_day));
                        arrRetTime.add(strManyDay);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                Intent intent = new Intent(this, ManyScheduleActivity.class);
                intent.putExtra("manyDate", arrRetTime);
                startActivityForResult(intent, ADD_SCHEDULE);
            }
        });
    }

    //yyyy-MM 기준으로 저장된 스케줄 표시
    private void getScheduleRealm() {

        //현재 페이지 달력 시간 얻기
        long currentPageTime = calendarView.getCurrentPageDate().getTimeInMillis();
        DateTime dateTime = new DateTime(Long.valueOf(currentPageTime), DateTimeZone.UTC);

        strMYear = dateTime.plusHours(9).getYear(); //연도
        strMonth = dateTime.plusHours(9).getMonthOfYear() - 1; //달
        strMDay = dateTime.plusHours(9).toString(getString(R.string.text_date_year_month)); //yyyy-MM

        realm.executeTransaction(realm -> {
            realmSchedule = realm.where(Schedule.class).equalTo("date", strMDay).findAll();
            //해당 yyyy-MM 에 저장된 스케줄 dd 얻기.

            for (Schedule schedule : realmSchedule) {
                DateTime realmTime = new DateTime(Long.valueOf(schedule.getTime()), DateTimeZone.UTC);

                int realmDay = realmTime.getDayOfMonth();

                Calendar realmCalendar = Calendar.getInstance();
                realmCalendar.set(Calendar.YEAR, strMYear);
                realmCalendar.set(Calendar.MONTH, strMonth);
                realmCalendar.set(Calendar.DATE, realmDay);
                events.add(new EventDay(realmCalendar, R.mipmap.schedule_star));
            }
            calendarView.setEvents(events);

        });
    }

    public void setDialogDate(String date) {
        mPresenter.setDialogDate(date);
    }

    //show Dialog When User Click Calendar
    @Override
    public void showDialogCalendar(String date) {
        //https://hashcode.co.kr/questions/3073/mvp-패턴에서-startactivity는-어디서-해야하나요
        Intent intent = new Intent(this, ScheduleInfoActivity.class);
        intent.putExtra("date", date);
        startActivityForResult(intent, ADD_SCHEDULE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_SCHEDULE:
                //스케줄 입력이 완료됬을 때
                getScheduleRealm();
                calendarView.setSelectedDates(setSelectInit());
                break;
        }
    }

    //선택 된 날짜 초기화
    private List<Calendar> setSelectInit() {
        return new ArrayList<>();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navCalendar) {
            //현재 캘린더 뷰라면 실행하지않도록 수정예정
            startActivity(new Intent(this, ScheduleCalendarActivity.class));
        } else if (id == R.id.navFriend) {
//            finish();
            startActivity(new Intent(this, FriendActivity.class));
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Navigation
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    //실제 View 가 만들어지는 시점
    @Override
    public void setPresenter(ScheduleContract.Presenter presenter) {
        mPresenter = presenter;
    }



}