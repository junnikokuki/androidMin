package com.ubtechinc.alpha.mini.utils;

import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;

import com.ubtechinc.alpha.mini.entity.Contact;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Date: 2018/2/5.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class ChineseToPinYinUtils {

    private static HanyuPinyinOutputFormat pinyinForm = new HanyuPinyinOutputFormat();

    private static final SimpleArrayMap<Character, String> surnames;

    static {
        pinyinForm.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        pinyinForm.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
        pinyinForm.setVCharType(HanyuPinyinVCharType.WITH_V);
        surnames = new SimpleArrayMap<>(32);
        /*surnames.put('乐', "YUÈ");
        surnames.put('乘', "SHÈNG");
        surnames.put('乜', "NIÈ");
        surnames.put('仇', "QIÚ");
        surnames.put('会', "GUÌ");
        surnames.put('便', "PIÁN");
        surnames.put('区', "ŌU");
        surnames.put('单', "SHÀN");
        surnames.put('参', "SHĒN");
        surnames.put('句', "GŌU");
        surnames.put('召', "SHÀO");
        surnames.put('员', "YÙN");
        surnames.put('宓', "FÚ");
        surnames.put('折', "SHÉ");
        surnames.put('曾', "ZĒNG");
        surnames.put('朴', "PIÁO");
        surnames.put('查', "ZHĀ");
        surnames.put('洗', "XIǍN");
        surnames.put('盖', "GĚ");
        surnames.put('祭', "ZHÀI");
        surnames.put('种', "CHÓNG");
        surnames.put('秘', "BÌ");
        surnames.put('繁', "PÓ");
        surnames.put('缪', "MIÀO");
        surnames.put('能', "NÀI");
        surnames.put('覃', "QÍN");
        surnames.put('解', "XIÈ");
        surnames.put('谌', "SHÈN");
        surnames.put('适', "KUÒ");
        surnames.put('都', "DŪ");
        surnames.put('阿', "Ē");
        surnames.put('难', "NÌNG");
        surnames.put('黑', "HÈ");*/
        surnames.put('乐', "YUE4");
        surnames.put('乘', "SHENG4");
        surnames.put('乜', "NIE4");
        surnames.put('仇', "QIU2");
        surnames.put('会', "GUI4");
        surnames.put('便', "PIAN2");
        surnames.put('区', "OU1");
        surnames.put('单', "SHAN4");
        surnames.put('参', "SHEN1");
        surnames.put('句', "GOU1");
        surnames.put('召', "SHAO4");
        surnames.put('员', "YUN4");
        surnames.put('宓', "FU2");
        surnames.put('折', "SHE2");
        surnames.put('曾', "ZENG1");
        surnames.put('朴', "PIAO2");
        surnames.put('查', "ZHA1");
        surnames.put('洗', "XIAN3");
        surnames.put('盖', "GE3");
        surnames.put('祭', "ZHAI4");
        surnames.put('种', "CHONG2");
        surnames.put('秘', "BI4");
        surnames.put('繁', "PO2");
        surnames.put('缪', "MIAO4");
        surnames.put('能', "NAI4");
        surnames.put('覃', "QIN2");
        surnames.put('解', "XIE4");
        surnames.put('谌', "CHEN2");
        surnames.put('适', "KUO4");
        surnames.put('都', "DU1");
        surnames.put('阿', "E1");
        surnames.put('难', "NING4");
        surnames.put('黑', "HE4");
    }

    private ChineseToPinYinUtils() {
    }

    public static <T> List<T> chineseToPinYin(List<T> data) {
        if (data == null || data.size() == 0) {
            return data;
        }
        int size = data.size();
        for (int i = 0; i < size; i++) {
            Contact contact = (Contact) data.get(i);
            String target = contact.getTarget();//取出需要被拼音化的字段
            //遍历target的每个char得到它的全拼音
            String py = chineseToPinYin(target);
            contact.setNamePy(py);//设置全拼音

            //以下代码设置拼音首字母
            String firstLetter = py.substring(0, 1).toUpperCase();
            if (firstLetter.matches("[A-Z]")) {//如果是A-Z字母开头
                contact.setFirstLetterPy(firstLetter);
            } else {//特殊字母这里统一用#处理
                contact.setFirstLetterPy("#");
            }
        }
        return data;
    }


    public static Contact setContactPinYinProperty(Contact contact) {
        if (contact == null) {
            return null;
        }
        String target = contact.getTarget();
        String py = chineseToPinYin(target);
        contact.setNamePy(py);
        return contact;
    }

    public static <T> void sortWithPinYin(List<T> data) {
        if (data == null || data.size() == 0 || data.size() == 1) {
            return;
        }
        Collections.sort(data, new Comparator<T>() {
            @Override
            public int compare(T lhs, T rhs) {
                if (lhs == rhs) {
                    return 0;
                }
                Contact left = (Contact) lhs;
                Contact right = (Contact) rhs;
                if ("#".equals(left.getFirstLetterPy())) {
                    if (!"#".equals(right.getFirstLetterPy())) {
                        return 1;
                    }
                }
                if ("#".equals(right.getFirstLetterPy())) {
                    return -1;
                }
                return left.getNamePy().compareTo(right.getNamePy());
            }
        });
    }

    public static Map<String, List<Contact>> getIndexBoradPinYin(List<Contact> contacts) {
        Map<String, List<Contact>> maps = new TreeMap<String, List<Contact>>();
        for (int i = 0; i < contacts.size(); i++) {
            String firstLetter = contacts.get(i).getNamePy();

            if (!firstLetter.matches("[A-Za-z]")) {//如果不是字母
                List<Contact> others = maps.get("#");
                if (others != null) {
                    others.add(contacts.get(i));
                } else {
                    others = new ArrayList<>();
                    others.add(contacts.get(i));
                }
                maps.put("#", others);
                continue;
            }

            if (maps.containsKey(firstLetter)) {
                List<Contact> groups = maps.get(firstLetter);
                groups.add(contacts.get(i));
            } else {
                List<Contact> groups = new ArrayList<>();
                groups.add(contacts.get(i));
                maps.put(firstLetter, groups);
            }
        }
        return maps;
    }

    public static String chineseToPinYin(String chinese) {
        if (TextUtils.isEmpty(chinese)) {
            return chinese;
        }
        int start = 0;
        StringBuilder pySb = new StringBuilder();
        if (chinese.startsWith("澹台")) {
//            pySb.append("TÁNTÁI");
            pySb.append("TAN2TAI2");
            start = 2;
        } else if (chinese.startsWith("尉迟")) {
//            pySb.append("YÙCHÍ");
            pySb.append("YU4CHI2");
            start = 2;
        } else if (chinese.startsWith("万俟")) {
//            pySb.append("MÒQÍ");
            pySb.append("MO4QI2");
            start = 2;
        } else if (chinese.startsWith("单于")) {
//            pySb.append("CHÁNYÚ");
            pySb.append("CHAN2YU2");
            start = 2;
        } else {
            Character first = chinese.charAt(0);
            String firstPy = surnames.get(first);
            if (firstPy != null) {
                pySb.append(firstPy);
                start = 1;
            }
        }
        //遍历target的每个char得到它的全拼音
        for (int i1 = start; i1 < chinese.length(); i1++) {
            //利用TinyPinyin将char转成拼音
            //查看源码，方法内 如果char为汉字，则返回大写拼音
            //如果c不是汉字，则返回String.valueOf(c)
            try {
                String[] result = PinyinHelper.toHanyuPinyinStringArray(chinese.charAt(i1), pinyinForm);
                if (result == null) {
                    pySb.append(chinese.substring(i1, i1 + 1).toUpperCase());
                } else {
                    pySb.append(result[0]);
                }
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
        }
        return pySb.toString();
    }
}
