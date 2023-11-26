package edu.appstate.cs.moments

import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToLastPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MomentsListFragmentTest {

    private lateinit var scenario: FragmentScenario<MomentsListFragment>

    @Before
    fun setUp() {
        scenario = FragmentScenario.launchInContainer(
            MomentsListFragment::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun recyclerViewAppears() {
        onView(withId(R.id.moments_recycler_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun recyclerViewScrolls() {
        onView(withId(R.id.moments_recycler_view))
            .perform(scrollToLastPosition<MomentHolder>())
    }
}