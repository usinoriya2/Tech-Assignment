package com.example.techassignment;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.view.View;
import android.view.ViewGroup;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withParentIndex;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityInstrumentedTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void init() {

    }

    @Test
    public void offlineTest() throws InterruptedException {

        Thread.sleep(2000);
    }

    @Test
    public void mainActivityInstrumentedTest() throws InterruptedException {

        //check if shimmer animation is displayed
        onView(withId(R.id.shimmer_frame_layout))
                .check(matches(isDisplayed()));

        //wait for 4 seconds to api fetch data and shimmer animation is stopped
        Thread.sleep(4000);
        onView(withId(R.id.shimmer_frame_layout))
                .check(matches(not(isDisplayed())));

//        onView(allOf(withId(R.id.author)))
//                .check(matches(isDisplayed()));
        int responseSize = MainActivity.jsonArraySize;
        for(int i=0;i <responseSize;i++){
            onView(nthChildOf(withId(R.id.repo_layout), i)).check(matches(isDisplayed())).perform(click());
            onView(nthChildOf(withId(R.id.repo_layout), i)).check(matches(isDisplayed())).perform(click());
        }


//        onView(
//                withPositionInParent(R.id.repo_layout, 0)
//        ).check(
//                matches(withId(R.id.description_layout))
//        ).perform(click());
    }

    public static Matcher<View> nthChildOf(final Matcher<View> parentMatcher, final int childPosition) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with "+childPosition+" child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view.getParent() instanceof ViewGroup)) {
                    return parentMatcher.matches(view.getParent());
                }

                ViewGroup group = (ViewGroup) view.getParent();
                return parentMatcher.matches(view.getParent()) && view.equals(group.getChildAt(childPosition));
            }
        };
    }

    public static Matcher<View> withPositionInParent(int parentViewId, int position){
        return allOf(withParent(withId(parentViewId)), withParentIndex(position));
    }
}