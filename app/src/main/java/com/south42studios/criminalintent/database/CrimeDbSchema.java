package com.south42studios.criminalintent.database;

/**
 * Created by Jsin on 10/3/2015.
 */
public class CrimeDbSchema {

    public static final class CrimeTable {
        public static final String NAME = "CRIMES";

        public static final class Cols {
            public static final String UUID = "UUID";
            public static final String TITLE = "TITLE";
            public static final String DATE = "DATE";
            public static final String SOLVED = "SOLVED";
            public static final String SUSPECT = "SUSPECT";
            public static final String PHONE_NUMBER = "PHONE_NUMBER";
        }
    }
}
