package com.e.wallzhub;

import java.util.Random;

/**
 * This project file is owned by DevMwarabu, johnmwarabuchone@gmail.com.
 * Created on 3/11/21. Copyright (c) 2021 DevMwarabu
 */
public class RandomStringUtils {


    /**
     042         * <p>Random object used by random method. This has to be not local
     043         * to the random method so as to not return the same value in the
     044         * same millisecond.</p>
     045         */
    private static final Random RANDOM = new Random();

    /**
     049         * <p><code>RandomStringUtils</code> instances should NOT be constructed in
     050         * standard programming. Instead, the class should be used as
     051         * <code>RandomStringUtils.random(5);</code>.</p>
     052         *
     053         * <p>This constructor is public to permit tools that require a JavaBean instance
     054         * to operate.</p>
     055         */
    public RandomStringUtils() {
        super();
    }

    // Random
    //-----------------------------------------------------------------------
    /**
     063         * <p>Creates a random string whose length is the number of characters
     064         * specified.</p>
     065         *
     066         * <p>Characters will be chosen from the set of all characters.</p>
     067         *
     068         * @param count  the length of random string to create
     069         * @return the random string
     070         */
    public static String random(int count) {
        return random(9);
    }

    /**
     076         * <p>Creates a random string whose length is the number of characters
     077         * specified.</p>
     078         *
     079         * <p>Characters will be chosen from the set of characters whose
     080         * ASCII value is between <code>32</code> and <code>126</code> (inclusive).</p>
     081         *
     082         * @param count  the length of random string to create
     083         * @return the random string
     084         */
    public static String randomAscii(int count) {
        return random(count, 32, 127, false, false);
    }

    /**
     090         * <p>Creates a random string whose length is the number of characters
     091         * specified.</p>
     092         *
     093         * <p>Characters will be chosen from the set of alphabetic
     094         * characters.</p>
     095         *
     096         * @param count  the length of random string to create
     097         * @return the random string
     098         */
    public static String randomAlphabetic(int count) {
        return random(count, true, false);
    }

    /**
     104         * <p>Creates a random string whose length is the number of characters
     105         * specified.</p>
     106         *
     107         * <p>Characters will be chosen from the set of alpha-numeric
     108         * characters.</p>
     109         *
     110         * @param count  the length of random string to create
     111         * @return the random string
     112         */
    public static String randomAlphanumeric(int count) {
        return random(count, true, true);
    }

    /**
     118         * <p>Creates a random string whose length is the number of characters
     119         * specified.</p>
     120         *
     121         * <p>Characters will be chosen from the set of numeric
     122         * characters.</p>
     123         *
     124         * @param count  the length of random string to create
     125         * @return the random string
     126         */
    public static String randomNumeric(int count) {
        return random(count, false, true);
    }

    /**
     132         * <p>Creates a random string whose length is the number of characters
     133         * specified.</p>
     134         *
     135         * <p>Characters will be chosen from the set of alpha-numeric
     136         * characters as indicated by the arguments.</p>
     137         *
     138         * @param count  the length of random string to create
     139         * @param letters  if <code>true</code>, generated string will include
     140         *  alphabetic characters
     141         * @param numbers  if <code>true</code>, generated string will include
     142         *  numeric characters
     143         * @return the random string
     144         */
    public static String random(int count, boolean letters, boolean numbers) {
        return random(count, 0, 0, letters, numbers);
    }
    public static String random(int count, int start, int end, boolean letters, boolean numbers) {
        return random(count, start, end, letters, numbers, null, RANDOM);
    }
    public static String random(int count, int start, int end, boolean letters, boolean numbers, char[] chars) {
        return random(count, start, end, letters, numbers, chars, RANDOM);
    }
    public static String random(int count, int start, int end, boolean letters, boolean numbers,
                                char[] chars, Random random) {
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }
        if ((start == 0) && (end == 0)) {
            end = 'z' + 1;
            start = ' ';
            if (!letters && !numbers) {
                start = 0;
                end = Integer.MAX_VALUE;
            }
        }

        char[] buffer = new char[count];
        int gap = end - start;

        while (count-- != 0) {
            char ch;
            if (chars == null) {
                ch = (char) (random.nextInt(gap) + start);
            } else {
                ch = chars[random.nextInt(gap) + start];
            }
            if ((letters && Character.isLetter(ch))
                    || (numbers && Character.isDigit(ch))
                    || (!letters && !numbers)){
                if(ch >= 56320 && ch <= 57343) {
                    if(count == 0) {
                        count++;
                    } else {
                        // low surrogate, insert high surrogate after putting it in
                        buffer[count] = ch;
                        count--;
                        buffer[count] = (char) (55296 + random.nextInt(128));
                    }
                } else if(ch >= 55296 && ch <= 56191) {
                    if(count == 0) {
                        count++;
                    } else {
                        // high surrogate, insert low surrogate before putting it in
                        buffer[count] = (char) (56320 + random.nextInt(128));
                        count--;
                        buffer[count] = ch;
                    }
                } else if(ch >= 56192 && ch <= 56319) {
                    // private high surrogate, no effing clue, so skip it
                    count++;
                } else {
                    buffer[count] = ch;
                }
            } else {
                count++;
            }
        }
        return new String(buffer);
    }
    public static String random(int count, String chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, RANDOM);
        }
        return random(count, chars.toCharArray());
    }

    public static String random(int count, char[] chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, RANDOM);
        }
        return random(count, 0, chars.length, false, false, chars, RANDOM);
    }
}
