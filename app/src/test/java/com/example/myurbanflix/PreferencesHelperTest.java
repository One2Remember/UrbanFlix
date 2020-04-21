package com.example.myurbanflix;

import com.github.ivanshafran.sharedpreferencesmock.SPMockBuilder;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A test suite to test every method of the PreferencesHelper class using a dummy Context
 * to enable unit testing outside of Android framework
 */
public class PreferencesHelperTest {
    /**
     * Builds a dummy context for the purpose of testing
     */
    private final SPMockBuilder spMockBuilder = new SPMockBuilder();
    /**
     * Local PreferencesHelper object for testing the class
     */
    private PreferencesHelper testHelper;

    /**
     * Initializes an empty dummy UserPreferences Context and a PreferencesHelper constructed from
     * this fake Context - for testing purposes
     */
    @Before
    public void setUpFakePrefs() {
        testHelper = new PreferencesHelper(spMockBuilder.createContext(), "UserPreferences");
    }

    /**
     * tests getPreference method that returns a String value
     */
    @Test
    public void testGetPreference0() {
        // assert that trying to find a fake preference provides default value
        assertEquals(testHelper.getPreference("FakeGet", "Default"), "Default");
        // add a new preference
        testHelper.setPreference("RealGet", "A Real Preference");
        // assert that now we found the correct preference
        assertEquals(testHelper.getPreference("RealGet", "Didn't Work"), "A Real Preference");
    }

    /**
     * tests getPreference method that returns a boolean value
     */
    @Test
    public void testGetPreference1() {
        // assert that trying to find a fake preference provides default value (false)
        assertEquals(testHelper.getPreference("FakeBool", false), false);
        // add a new preference set to true
        testHelper.setPreference("RealBool", true);
        // assert that now we found the correct preference (true) instead of default (false)
        assertEquals(testHelper.getPreference("RealBool", false), true);
    }

    /**
     * tests getPreference method that returns an integer value
     */
    @Test
    public void testGetPreference2() {
        // assert that trying to find a fake preference provides default value (-1)
        assertEquals(testHelper.getPreference("FakeInt", -1), -1);
        // add a new preference set to 307
        testHelper.setPreference("RealInt", 307);
        // assert that now we found the correct preference (307) instead of default (-1)
        assertEquals(testHelper.getPreference("RealInt", -1), 307);
    }

    /**
     * tests setPreference method that takes a String value
     */
    @Test
    public void testSetPreference0() {
        // assert that trying to find a non-existent preference provides default value
        assertEquals(testHelper.getPreference("FakeStringSet", "Default"), "Default");
        // add a new preference with value "A Real Preference"
        testHelper.setPreference("RealStringSet", "A Real Preference");
        // assert that now we found the correct preference (value "A Real Preference")
        assertEquals(testHelper.getPreference("RealStringSet", "Didn't Work"), "A Real Preference");
    }

    /**
     * tests setPreference method that takes a boolean value
     */
    @Test
    public void testSetPreference1() {
        // assert that trying to find a non-existent preference provides default value
        assertEquals(testHelper.getPreference("FakeBoolSet", false), false);
        // add a new preference with value true
        testHelper.setPreference("RealBoolSet", true);
        // assert that now we found the correct preference (value true)
        assertEquals(testHelper.getPreference("RealBoolSet", false), true);
    }

    /**
     * tests setPreference method that takes an integer value
     */
    @Test
    public void testSetPreference2() {
        // assert that trying to find a non-existent preference provides default value
        assertEquals(testHelper.getPreference("FakeIntSet", -1), -1);
        // add a new preference with value 38927
        testHelper.setPreference("RealIntSet", 38927);
        // assert that now we found the correct preference (value 38927)
        assertEquals(testHelper.getPreference("RealIntSet", -1), 38927);
    }

    @Test
    public void testRemovePreference() {
        // assert that fake String preference is initially empty
        assertEquals(testHelper.getPreference("FakeString", "Default"), "Default");
        // add a new preference with value "A Real Preference"
        testHelper.setPreference("RealStringSet", "A Real Preference");
        // assert that now we found the correct preference (value "A Real Preference")
        assertEquals(testHelper.getPreference("RealStringSet", "Didn't Work"), "A Real Preference");
        // now call removePreference on our newly created preference RealStringSet
        testHelper.removePreference("RealStringSet");
        // and assert that it is now equal to the default value
        assertEquals(testHelper.getPreference("RealStringSet", "Default"), "Default");
    }
}