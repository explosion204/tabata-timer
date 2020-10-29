package com.explosion204.tabatatimer

object Constants {
    const val MAIN_NOTIFICATION_CHANNEL =
        "com.explosion204.tabatatimer.MAIN_NOTIFICATION_CHANNEL"

    const val TAG_TIMER_LIST_FRAGMENT =
        "com.explosion204.tabatatimer.TIMER_LIST_FRAGMENT"
    const val TAG_SEQUENCE_DETAIL_FRAGMENT =
        "com.explosion204.tabatatimer.SEQUENCE_DETAIL_FRAGMENT"
    const val TAG_TIMER_FRAGMENT =
        "com.explosion204.tabatatimer.TIMER_FRAGMENT"
    const val TAG_UPCOMING_TIMERS_LIST_FRAGMENT =
        "com.explosion204.tabatatimer.UPCOMING_TIMERS_LIST_FRAGMENT"

    const val ACTION_CONTEXTUAL_MENU =
        "com.explosion204.tabatatimer.CONTEXTUAL_MENU_ACTION"
    const val ACTION_ADD_NEW_ASSOCIATED_TIMERS =
        "com.explosion204.tabatimer.ADD_NEW_ASSOCIATED_TIMERS_ACTION"
    const val ACTION_SELECT_TIMERS_MODE =
        "com.explosion204.tabatatimer.SELECT_TIMERS_MODE_ACTION"

    const val ACTION_TIMER_STATE_CHANGED =
        "com.explosion204.tabatatimer.TIMER_STATE_CHANGED_ACTION"
    const val ACTION_SET_TIMER_STATE =
        "com.explosion204.tabatatimer.SET_TIMER_STATE_ACTION"
    const val ACTION_PREV_TIMER =
        "com.explosion204.tabatatimer.PREV_TIMER_ACTION"
    const val ACTION_NEXT_TIMER =
        "com.explosion204.tabatatimer.NEXT_TIMER_ACTION"
    const val ACTION_SELECT_PHASE =
        "com.explosion204.tabatatimer.SELECT_PHASE_ACTION"
    const val ACTION_SELECT_TIMER =
        "com.explosion204.tabatatimer.SELECT_TIMER"

    const val EXTRA_TITLE = "com.explosion204.tabatatimer.EXTRA_TITLE"
    const val EXTRA_DESCRIPTION = "com.explosion204.tabatatimer.EXTRA_DESCRIPTION"
    const val EXTRA_ALL_TIMERS = "com.explosion204.tabatimer.EXTRA_ALL_TIMERS"
    const val EXTRA_ASSOCIATED_TIMERS = "com.explosion204.tabatatimer.EXTRA_ASSOCIATED_TIMERS"
    const val EXTRA_TIMER = "com.explosion204.tabatatimer.EXTRA_TIMER"
    const val EXTRA_SEQUENCE = "com.explosion204.tabatatimer.EXTRA_SEQUENCE"

    const val TIMER_BROADCAST_ACTION = "com.explosion204.tabatimer.TIMER_BROADCAST_ACTION"
    const val TIMER_ACTION_TYPE = "com.explosion204.tabatatimer.TIMER_STATE"
    const val TIMER_STARTED = "com.explosion204.tabatimer.TIMER_STARTED"
    const val TIMER_STOPPED = "com.explosion204.tabatimer.TIMER_STOPPED"

    const val NOTIFICATION_BROADCAST_ACTION = "com.explosion204.tabatatimer.NOTIFICATION_BROADCAST_ACTION"
    const val NOTIFICATION_ID = 100

    const val NIGHT_MODE_PREFERENCE = "com.explosion204.tabatatimer.NIGHT_MODE_PREFERENCE"
    const val SETTINGS_ACTIVITY_RESULT_CODE = 0
    const val SETTINGS_THEME_CHANGED = "com.explosion204.tabatatimer.THEME_CHANGED"
}