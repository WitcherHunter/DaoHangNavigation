package com.serenegiant.entiy;

public class PracticeEvent {

  public static final int START_TRAIN = 0;
  public static final int COACH_LOGIN_SUCCESS = 1;
  public static final int STUDENT_LOGIN_SUCCESS = 2;
  public static final int STUDENT_EXIT_SUCCESS = 3;
  public static final int COACH_EXIT_SUCCESS = 4;
  public static final int ENTER_FINGER = 5;
  public static final int FINGER_MATCH_FAIL = 6;
  public static final int TIMEOUT = 7;

  public int code = -1;

  public PracticeEvent(int code) {
    this.code = code;
  }
}
