/**
 * <pre>
 *  파 일 명 : HtmlParser.java
 *  설    명 : Html 분리기
 *
 *  작 성 자 : yhheo(허영회)
 *  작 성 일 : 2020.05
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 *
 * @author Copyrights 2014 ~ 2020 by ㈜ WIGO. All right reserved.
 */

package com.seomse.crawling.parser;

import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.string.Remove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class HtmlParser {

    private static final Logger logger = LoggerFactory.getLogger(HtmlParser.class);

    public static String removeContents ( String contents , String prefix ) {
        return removeContents(contents, prefix,'*');
    }

    public static String removeContents ( String contents , String prefix , char splitter) {
        int startIdx = prefix.indexOf(splitter);
        String start = prefix.substring(0 , startIdx);
        String end = prefix.substring(startIdx+1);
        return removeContents(contents , start , end);
    }

    public static String removeContents ( String contents , String start , String end) {
        int startIdx = contents.indexOf(start) + start.length();
        int endIdx = contents.lastIndexOf(end);
        if ( startIdx == -1 || endIdx == -1) {
            return "";
        }
        return contents.substring(startIdx , endIdx);
    }

    /**
     * 기본셋팅된 String파싱
     * default splitter = '*'
     * @param contents
     * @param prefix
     * @return
     */
    public static String parseString(String contents , String prefix){
        String value = null;
        try {
            value = parseString(contents , prefix , '*');
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return value;
    }
    /**
     * 기본셋팅된 String파싱
     * 마지막에 Remove.htmlTag실행
     * @param contents
     * @param prefix
     * @return
     */
    public static String parseStringWithRemoveHtml(String contents, String prefix){
        return Remove.htmlTag(parseString(contents, prefix, '*'));
    }
    /**
     * splitter을 설정하여 파싱함
     * @param contents
     * @param prefix
     * @param splitter
     * @return
     */
    public static String parseString(String contents , String prefix , char splitter){
        int startIdx = prefix.indexOf(splitter);
        String start = prefix.substring(0 , startIdx);
        String end = prefix.substring(startIdx+1);
        return parseString(contents , start , end , true);
    }

    public static String parseString(String contents , String prefix , char splitter , boolean findFirst){
        int startIdx = prefix.indexOf(splitter);
        String start = prefix.substring(0 , startIdx);
        String end = prefix.substring(startIdx+1);
        return parseString(contents , start , end , findFirst);
    }

    public static String parseString(String contents , String start , String end , boolean findFirst) {
        if ( findFirst ){
            int startIdx = contents.indexOf(start);
            if(startIdx == -1){
                return "";
            }
            startIdx = startIdx + start.length();

            int endIdx = contents.indexOf(end, startIdx);
            if( endIdx == -1){
                return "";
            }

            if(startIdx > endIdx){
                return "";
            }

            return contents.substring(startIdx, endIdx);
        } else {
            int endIdx = contents.indexOf(end);
            if(endIdx == -1){
                return "";
            }

            int startIdx = contents.lastIndexOf(start, endIdx);
            if(startIdx == -1){
                return "";
            }
            startIdx = startIdx + start.length();

            if(startIdx > endIdx){
                return "";
            }
            return contents.substring(startIdx, endIdx);
        }
    }

    public static List<String> parseStringMany(String contents, String prefix , char splitter)  {
        int startIdx = prefix.indexOf(splitter);
        String start = prefix.substring(0 , startIdx);
        String end = prefix.substring(startIdx+1);
        return parseStringMany(contents , start , end);
    }

    public static List<String> parseStringMany(String contents , String start , String end , boolean findFirst) {
        List<String> list = new ArrayList<String>();
        while ( contents.indexOf(end)!= -1  ){
            String find = parseString( contents.substring(0 , contents.indexOf(end)+end.length() ) , start , end , findFirst );
            list.add(find);
            contents = contents.substring(contents.indexOf(end)+end.length());
        }
        return list;
    }
    public static List<String> parseStringMany(String contents ,String prefix , char splitter , boolean findFirst) {
        int startIdx = prefix.indexOf(splitter);
        String start = prefix.substring(0 , startIdx);
        String end = prefix.substring(startIdx+1);
        return parseStringMany(contents , start , end , findFirst);
    }

    public static List<String> parseStringMany(String contents ,String prefix) {
        return parseStringMany(contents , prefix , '*');
    }

    public static List<String> parseStringMany(String contents , String start , String end) {
        List<Integer> startIdxList = indexOfAny(contents , start );
        List<String> list = new ArrayList<String>();
        int startLength = start.length();
        for ( int startIdx : startIdxList ) {
            int endIdx = contents.indexOf(end , startIdx + startLength);
            if ( endIdx == -1) {
                continue;
            }
            String find = contents.substring(startIdx + startLength , endIdx );
            list.add(find);
        }
        return list;
    }

    public static int parseIntWithRemoveHtml(String contents, String prefix){
        return parseInt(contents, prefix, '*', true, true);
    }
    public static int parseInt(String contents , String prefix) {
        int value = 0;
        try {
            value = parseInt(contents , prefix , '*' , true, false);
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return value;
    }
    public static int parseInt(String contents , String prefix , char splitter) {
        return parseInt(contents , prefix , splitter , true, false);
    }
    public static int parseInt(String contents , String prefix , char splitter , boolean findFirst, boolean isRemoveHtml){
        int startIdx = prefix.indexOf(splitter);
        String start = prefix.substring(0 , startIdx);
        String end = prefix.substring(startIdx+1 , prefix.length());
        return parseInt(contents , start , end , findFirst, isRemoveHtml);
    }
    public static int parseInt(String contents , String start , String end) {
        return parseInt(contents , start , end , true, false);
    }
    public static int parseInt(String contents , String start , String end , boolean isFirst, boolean isRemoveHtml){
        String parse = parseString(contents,start,end,isFirst).trim().replace(",", "");

        if(parse == ""){
            return 0;
        }
        if(isRemoveHtml){
            parse = Remove.htmlTag(parse);
        }

        if( parse.indexOf(">") != -1 ){
            parse = parse.substring(parse.indexOf(">")+1 , parse.length());
        }
        if( parse.indexOf("<") != -1 ) {
            parse = parse.substring(0, parse.indexOf("<"));
        }
        int result = 0;

        if( parse.equals("") ) {
            return result;
        }
        try{
            result = Integer.parseInt(parse);
        } catch ( NumberFormatException e ) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return result;
    }

    public static List<Integer> indexOfAny(String contents , String prefix) {
        List<Integer> idxList = new ArrayList<>();
        int fromIdx = 0;
        while ( contents.indexOf(prefix , fromIdx) != -1 ) {
            int findIdx = contents.indexOf(prefix , fromIdx);
            idxList.add(findIdx);
            fromIdx = prefix.length() + findIdx;
        }
        return idxList;
    }
    /**
     * 괄호에 쌓인 내용을 포함한 리스트를 찾아서 괄호의 내용까지 포함하여 돌려준다.
     * @param contents
     * @param start
     * @param end
     * @return
     */
    public static List<String> parseWithBracket(String contents , String start , String end) {
        String bracketed = "";
        boolean isStart = true;

        if ( start.indexOf('(') != -1 && start.indexOf(')') != -1 ) {
            bracketed = start.substring( start.indexOf('(')+1 , start.lastIndexOf(')') );
            start = start.replace("(", "").replace(")", "");
        }
        else if ( end.indexOf('(') != -1 && end.indexOf(')') != -1 ){
            bracketed = end.substring( end.indexOf('(')+1 , end.lastIndexOf(')') );
            isStart = false;
            end = end.replace("(", "").replace(")", "");
        }
        else {
            return null;
        }

        List<String> list = parseStringMany( contents , start , end );
        List<String> newList = new ArrayList<String>();
        for ( String str : list ){
            if ( isStart )
                str = bracketed + str;
            else
                str += bracketed;

            newList.add(str);
        }
        return newList;
    }
    public static List<String> parseWithBracket(String contents , String prefix , char splitter) {
        int startIdx = prefix.indexOf(splitter);
        String start = prefix.substring(0 , startIdx);
        String end = prefix.substring(startIdx+1);
        return parseWithBracket(contents , start , end);
    }
    public static List<String> parseWithBracket(String contents , String prefix) {
        return parseWithBracket(contents , prefix , '*');
    }


    public static String parseStringWithAbsolutePath(String contents , String url , String prefix) {
        String parsedContent = parseString(contents, prefix);
        return toAbsolutePath(parsedContent,url);
    }

    public static String toAbsolutePath(String contents , String url){
        return contents;
    }
}
