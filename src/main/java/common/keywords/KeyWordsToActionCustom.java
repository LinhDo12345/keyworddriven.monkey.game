package common.keywords;

import execute.RunTestScript;

public class KeyWordsToActionCustom extends KeyWordsToActionToVerify{
    public static void swipeRightToLeftEx(String locator,String component) {
        int currentLesson = Integer.valueOf(getText(locator,component));
        int selectLesson = RunTestScript.currentLesson;
        if(selectLesson!=currentLesson) {
            swipe(String.valueOf(600), String.valueOf(200), String.valueOf(450));
        }
    }
    public static void getIndexStoryThenSearch(String locator){

    }
}
