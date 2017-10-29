package com.example.msystem.utils;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by stevenZhang on 2017/7/17.
 */

public class XmlUtils {

    public static  String getJosn(String str ) throws XmlPullParserException, IOException {

//        String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
//                "\n" +
//                "<string xmlns=\"http://localhost/\">{\"strResult\":\"0\"}</string>";
//
//
//        String str2 = "<string>{\"strResult\":\"0\"}</string>";
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new StringReader(str));
        int event = 0;
        event = parser.getEventType();

        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:

                    String str22="";
                    if ("string".equals(parser.getName())) {
                        //进行标签里面的内容后，再进行getText()获取
                               parser.next();
                            if (parser.getEventType() == XmlPullParser.TEXT)
                                str22 = parser.getText();
                             L.d("MainActivity",str22);
                            return str22;
                    }
                    break;

                case XmlPullParser.END_TAG:
                    break;
            }
                event = parser.next();
        }

        return null;
    }
}
