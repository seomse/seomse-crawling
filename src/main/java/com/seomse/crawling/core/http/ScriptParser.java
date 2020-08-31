/*
 * Copyright (C) 2020 Seomse Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.seomse.crawling.core.http;

import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.string.Remove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * ScriptParser
 * @author yh.heo
 */
public class ScriptParser {

    private static final Logger logger = LoggerFactory.getLogger(ScriptParser.class);

    /**
     * @param contents String
     * @param prefix String
     * @return String
     */
    public static String removeContents ( String contents , String prefix ) {
        return removeContents(contents, prefix,'*');
    }

    /**
     * @param contents String
     * @param prefix String
     * @param splitter char
     * @return String
     */
    public static String removeContents ( String contents , String prefix , char splitter) {
        int startIdx = prefix.indexOf(splitter);
        String start = prefix.substring(0 , startIdx);
        String end = prefix.substring(startIdx+1);
        return removeContents(contents , start , end);
    }

    /**
     * @param contents String
     * @param start String
     * @param end String
     * @return String
     */
    public static String removeContents ( String contents , String start , String end) {
        int startIdx = contents.indexOf(start) + start.length();
        int endIdx = contents.lastIndexOf(end);
        if ( startIdx == -1 || endIdx == -1) {
            return "";
        }
        return contents.substring(startIdx , endIdx);
    }

    /**
     * @param contents String
     * @param prefix String
     * @return String
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
     * @param contents String
     * @param prefix String
     * @return String
     */
    public static String parseStringWithRemoveHtml(String contents, String prefix){
        return Remove.htmlTag(parseString(contents, prefix, '*'));
    }

    /**
     * splitter을 설정하여 파싱함
     * @param contents String
     * @param prefix String
     * @param splitter char
     * @return String
     */
    public static String parseString(String contents , String prefix , char splitter){
        int startIdx = prefix.indexOf(splitter);
        String start = prefix.substring(0 , startIdx);
        String end = prefix.substring(startIdx+1);
        return parseString(contents , start , end , true);
    }

    /**
     * splitter을 설정하여 파싱함
     * @param contents String
     * @param prefix String
     * @param splitter char
     * @param findFirst char
     * @return String
     */
    public static String parseString(String contents , String prefix , char splitter , boolean findFirst){
        int startIdx = prefix.indexOf(splitter);
        String start = prefix.substring(0 , startIdx);
        String end = prefix.substring(startIdx+1);
        return parseString(contents , start , end , findFirst);
    }

    /**
     * @param contents String
     * @param start String
     * @param end String
     * @param findFirst boolean
     * @return String
     */
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

    /**
     * @param contents String
     * @param prefix String
     * @param splitter char
     * @return List
     */
    public static List<String> parseStringMany(String contents, String prefix , char splitter)  {
        int startIdx = prefix.indexOf(splitter);
        String start = prefix.substring(0 , startIdx);
        String end = prefix.substring(startIdx+1);
        return parseStringMany(contents , start , end);
    }

    /**
     * @param contents String
     * @param start String
     * @param end String
     * @param findFirst boolean
     * @return List
     */
    public static List<String> parseStringMany(String contents , String start , String end , boolean findFirst) {
        List<String> list = new ArrayList<>();
        while (contents.contains(end)){
            String find = parseString( contents.substring(0 , contents.indexOf(end)+end.length() ) , start , end , findFirst );
            list.add(find);
            contents = contents.substring(contents.indexOf(end)+end.length());
        }
        return list;
    }

    /**
     * @param contents String
     * @param prefix String
     * @param splitter char
     * @param findFirst boolean
     * @return List
     */
    public static List<String> parseStringMany(String contents ,String prefix , char splitter , boolean findFirst) {
        int startIdx = prefix.indexOf(splitter);
        String start = prefix.substring(0 , startIdx);
        String end = prefix.substring(startIdx+1);
        return parseStringMany(contents , start , end , findFirst);
    }

    /**
     * @param contents String
     * @param prefix String
     * @return List
     */
    public static List<String> parseStringMany(String contents ,String prefix) {
        return parseStringMany(contents , prefix , '*');
    }

    /**
     * @param contents String
     * @param start String
     * @param end String
     * @return List
     */
    public static List<String> parseStringMany(String contents , String start , String end) {
        List<Integer> startIdxList = indexOfAny(contents , start );
        List<String> list = new ArrayList<>();
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

    /**
     * @param contents String
     * @param prefix String
     * @return int
     */
    public static int parseIntWithRemoveHtml(String contents, String prefix){
        return parseInt(contents, prefix, '*', true, true);
    }

    /**
     * @param contents String
     * @param prefix String
     * @return int
     */
    public static int parseInt(String contents , String prefix) {
        int value = 0;
        try {
            value = parseInt(contents , prefix , '*' , true, false);
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return value;
    }

    /**
     * @param contents String
     * @param prefix String
     * @param splitter char
     * @return int
     */
    public static int parseInt(String contents , String prefix , char splitter) {
        return parseInt(contents , prefix , splitter , true, false);
    }

    /**
     * @param contents String
     * @param prefix String
     * @param splitter char
     * @param findFirst boolean
     * @param isRemoveHtml boolean
     * @return int
     */
    public static int parseInt(String contents , String prefix , char splitter , boolean findFirst, boolean isRemoveHtml){
        int startIdx = prefix.indexOf(splitter);
        String start = prefix.substring(0 , startIdx);
        String end = prefix.substring(startIdx+1);
        return parseInt(contents , start , end , findFirst, isRemoveHtml);
    }

    /**
     * @param contents String
     * @param start String
     * @param end String
     * @return int
     */
    public static int parseInt(String contents , String start , String end) {
        return parseInt(contents , start , end , true, false);
    }

    /**
     * @param contents String
     * @param start String
     * @param end String
     * @param isFirst boolean
     * @param isRemoveHtml boolean
     * @return int
     */
    public static int parseInt(String contents , String start , String end , boolean isFirst, boolean isRemoveHtml){
        String parse = parseString(contents,start,end,isFirst).trim().replace(",", "");

        if(parse.equals("")){
            return 0;
        }
        if(isRemoveHtml){
            parse = Remove.htmlTag(parse);
        }

        if(parse.contains(">")){
            parse = parse.substring(parse.indexOf(">")+1);
        }
        if(parse.contains("<")) {
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

    /**
     * @param contents String
     * @param prefix String
     * @return List
     */
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
     * @param contents String
     * @param start String
     * @param end String
     * @return List
     */
    public static List<String> parseWithBracket(String contents , String start , String end) {
        String bracketed;
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
        List<String> newList = new ArrayList<>();
        for ( String str : list ){
            if ( isStart )
                str = bracketed + str;
            else
                str += bracketed;

            newList.add(str);
        }
        return newList;
    }

    /**
     * @param contents String
     * @param prefix String
     * @param splitter char
     * @return List
     */
    public static List<String> parseWithBracket(String contents , String prefix , char splitter) {
        int startIdx = prefix.indexOf(splitter);
        String start = prefix.substring(0 , startIdx);
        String end = prefix.substring(startIdx+1);
        return parseWithBracket(contents , start , end);
    }

    /**
     * @param contents String
     * @param prefix String
     * @return List
     */
    public static List<String> parseWithBracket(String contents , String prefix) {
        return parseWithBracket(contents , prefix , '*');
    }

    /**
     * @param contents String
     * @param url String
     * @param prefix String
     * @return String
     */
    public static String parseStringWithAbsolutePath(String contents , String url , String prefix) {
        String parsedContent = parseString(contents, prefix);
        return toAbsolutePath(parsedContent,url);
    }

    /**
     * @param contents String
     * @param url String
     * @return String
     */
    public static String toAbsolutePath(String contents , String url){
        return contents;
    }
}
