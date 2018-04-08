package com.inschos.cloud.trading.annotation;

import com.inschos.cloud.trading.assist.kit.StringKit;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建日期：2018/4/3 on 11:24
 * 描述：
 * 作者：zhangyunhe
 */
public class CheckParamsKit {

    public static final String DEFAULT = "default";
    public static final String OK = "参数正常";
    public static final String FAIL = "参数有误";

    private final static String[] basic = {"byte", "java.lang.Byte",
            "short", "java.lang.Short",
            "int", "java.lang.Integer",
            "long", "java.lang.Long",
            "char", "java.lang.Character",
            "boolean", "java.lang.Boolean",
            "float", "java.lang.Float",
            "double", "java.lang.Double"
    };

    private final static String stringClass = "java.lang.String";

    private final static String listClass = "java.util.List";

    public static void checkToMap(Object o, Map<String, Object> result) {
        if (result == null) {
            return;
        }
        final Class<?> objClass = o.getClass();
        Field[] farray = objClass.getDeclaredFields();//获取所有字段  包括private 不包括父类字段
        Class<CheckParams> chkString = CheckParams.class;//CheckString的class
        for (int i = 0; i < farray.length; i++) {
            Field field = farray[i];//获取其中字段
            String fieldName = field.getName();//获取字段名称
            if (field.isAnnotationPresent(chkString)) {//判断是否被chkstring注解所标识
                //如果被标识
                CheckParams chk = field.getAnnotation(chkString);// 返回这个类所标识的注解对象

                Class<?> type = field.getType();
                String name = type.getName();
                boolean flag = true;

                if (isBasic(name)) {
                    try {
                        String s = checkBasic(fieldName, field.get(o), chk);
                        flag = StringKit.isEmpty(s);
                        result.put(fieldName, s);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        flag = false;
                    }
                } else if (isString(name)) {
                    try {
                        Object o1 = field.get(o);
                        String s = checkStringType(fieldName, o1, chk);
                        flag = StringKit.isEmpty(s);
                        result.put(fieldName, s);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        flag = false;
                    }

                } else if (isList(name)) {
                    boolean necessity = chk.isNecessity();
                    try {
                        Object o1 = field.get(o);
                        if (necessity) {
                            if (o1 == null) {
                                result.put(fieldName, "缺少" + fieldName + "参数");
                                flag = false;
                            } else {
                                List<LinkedHashMap<String, Object>> list = new ArrayList<>();
                                result.put(fieldName, list);
                                List o11 = (List) o1;
                                for (Object o2 : o11) {
                                    LinkedHashMap<String, Object> objectObjectLinkedHashMap = new LinkedHashMap<>();
                                    list.add(objectObjectLinkedHashMap);
                                    checkToMap(o2, objectObjectLinkedHashMap);
                                }
                            }
                        } else {
                            if (o1 != null) {
                                List<LinkedHashMap<String, Object>> list = new ArrayList<>();
                                result.put(fieldName, list);
                                List o11 = (List) o1;
                                for (Object o2 : o11) {
                                    LinkedHashMap<String, Object> objectObjectLinkedHashMap = new LinkedHashMap<>();
                                    list.add(objectObjectLinkedHashMap);
                                    checkToMap(o2, objectObjectLinkedHashMap);
                                }
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        flag = false;
                    }
                } else {
                    boolean necessity = chk.isNecessity();
                    try {
                        LinkedHashMap<String, Object> objectObjectLinkedHashMap = new LinkedHashMap<>();
                        result.put(fieldName, objectObjectLinkedHashMap);
                        Object o1 = field.get(o);
                        if (necessity) {
                            if (o1 == null) {
                                try {
                                    Class<?> aClass = ClassLoader.getSystemClassLoader().loadClass(name);
                                    o1 = aClass.newInstance();
                                } catch (ClassNotFoundException | InstantiationException e) {
                                    e.printStackTrace();
                                }
                                flag = false;
                                objectObjectLinkedHashMap.put(DEFAULT, FAIL);
                            }
                            checkToMap(o1, objectObjectLinkedHashMap);
                        } else {
                            if (o1 != null) {
                                checkToMap(o1, objectObjectLinkedHashMap);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        flag = false;
                    }
                }

                if (flag) {
                    result.put(DEFAULT, OK);
                } else {
                    result.put(DEFAULT, FAIL);
                }
            }
        }
    }

    public static void checkToArray(Object o, List<Entry<String, String>> result) {
        if (result == null) {
            return;
        }
        final Class<?> objClass = o.getClass();
        Field[] farray = objClass.getDeclaredFields();//获取所有字段  包括private 不包括父类字段
        Class<CheckParams> chkString = CheckParams.class;//CheckString的class
        boolean flag = true;
        for (int i = 0; i < farray.length; i++) {
            Field field = farray[i];//获取其中字段
            String fieldName = field.getName();//获取字段名称
            if (field.isAnnotationPresent(chkString)) {//判断是否被chkstring注解所标识
                //如果被标识
                CheckParams chk = field.getAnnotation(chkString);// 返回这个类所标识的注解对象

                Class<?> type = field.getType();
                String name = type.getName();

                if (isBasic(name)) {
                    try {
                        String s = checkBasic(fieldName, field.get(o), chk);
                        flag = StringKit.isEmpty(s);
                        if (!flag) {
                            result.add(new Entry<>(fieldName, s));
                        }

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        flag = false;
                        result.add(new Entry<>(fieldName, "检查错误"));
                    }
                } else if (isString(name)) {
                    try {
                        Object o1 = field.get(o);
                        String s = checkStringType(fieldName, o1, chk);
                        flag = StringKit.isEmpty(s);
                        if (!flag) {
                            result.add(new Entry<>(fieldName, s));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        flag = false;
                        result.add(new Entry<>(fieldName, "检查错误"));
                    }

                } else if (isList(name)) {
                    try {
                        Object o1 = field.get(o);
                        Entry<String, String> stringStringEntry = checkListFirst(fieldName, o1, chk);
                        flag = stringStringEntry == null;
                        if (!flag) {
                            result.add(stringStringEntry);
                        }

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        flag = false;
                        result.add(new Entry<>(fieldName, "检查错误"));
                    }
                } else {
                    boolean necessity = chk.isNecessity();
                    try {
                        Object o1 = field.get(o);
                        if (necessity) {
                            if (o1 == null) {
                                result.add(new Entry<>(fieldName, "缺少" + fieldName + "参数"));
                            } else {
                                checkToArray(o1, result);
                            }
                        } else {
                            if (o1 != null) {
                                checkToArray(o1, result);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        flag = false;
                        result.add(new Entry<>(fieldName, "检查错误"));
                    }
                }

            }
        }

//        Entry<String, String> defaultEntry = getDefaultEntry();
//        if (flag) {
//            defaultEntry.details = OK;
//        } else {
//            defaultEntry.details = FAIL;
//        }
//        result.add(0, defaultEntry);


        if (!flag) {
            Entry<String, String> defaultEntry = getDefaultEntry();
            defaultEntry.details = FAIL;
            result.add(0, defaultEntry);
        }
    }

    private static boolean isBasic(String typeName) {
        boolean flag = false;
        if (typeName == null) {
            return false;
        }

        if (typeName.length() < 3 || typeName.length() > 18) {
            return false;
        }

        for (String s : basic) {
            if (StringKit.equals(typeName, s)) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    private static String checkBasic(String name, Object value, CheckParams chk) {

        if (value instanceof Byte) {
            Byte v = (Byte) value;
            return checkLong(name, v, chk.min(), chk.max());
        } else if (value instanceof Short) {
            Short v = (Short) value;
            return checkLong(name, v, chk.min(), chk.max());
        } else if (value instanceof Integer) {
            Integer v = (Integer) value;
            return checkLong(name, v, chk.min(), chk.max());
        } else if (value instanceof Long) {
            Long v = (Long) value;
            return checkLong(name, v, chk.min(), chk.max());
        } else if (value instanceof Character) {
            Character v = (Character) value;
            return checkLong(name, v, chk.min(), chk.max());
        }
//        else if (details instanceof Boolean) {
//
//        }
        else if (value instanceof Float) {
            Float v = (Float) value;
            return checkDouble(name, v, chk.min(), chk.max());
        } else if (value instanceof Double) {
            Double v = (Double) value;
            return checkDouble(name, v, chk.min(), chk.max());
        }

        return null;
    }

    private static String checkStringType(String name, Object value, CheckParams chk) {
        String str = (String) value;

        boolean necessity = chk.isNecessity();

        if (necessity) {
            if (str == null) {
                return "缺少" + name + "参数";
            } else {
                return checkString(name, str, chk);
            }
        } else {
            if (str != null) {
                // 校验长度的前提是，校验逻辑 正确(maxLen >= minLen)
                return checkString(name, str, chk);
            }
        }

        return null;
    }

    private static boolean isString(String typeName) {
        return StringKit.equals(stringClass, typeName);
    }

    private static boolean isList(String typeName) {
        return StringKit.equals(listClass, typeName);
    }

    private static Class getListClass(Field field) {
        // 如果是List类型，得到其Generic的类型
        Type genericType = field.getGenericType();
        if (genericType == null)
            return null;
        // 如果是泛型参数的类型
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            //得到泛型里的class类型对象
            return (Class<?>) pt.getActualTypeArguments()[0];
        }
        return null;
    }

    private static String checkLong(String name, long value, double min, double max) {
        return checkDouble(name, value, min, max);
    }

    private static String checkDouble(String name, double value, double min, double max) {
        boolean b = value >= min && value < max;
        if (min == max) {
            return b ? null : name + "数值必须为：" + min;
        } else {
            return b ? null : name + "数值必须在：[" + min + "," + max + ")区间";
        }
    }

    private static String checkString(String name, String str, CheckParams chk) {
        // 校验长度的前提是，校验逻辑 正确(maxLen >= minLen)
        if (chk.stringType() == CheckParams.StringType.STRING) {
            int maxLen = chk.maxLen();
            if (maxLen < 0) {
                maxLen = 0;
            }

            int minLen = chk.minLen();
            if (minLen < 0) {
                minLen = 0;
            }

            if (maxLen >= minLen) {
                int length = str.length();
                if (length < minLen || length >= maxLen) {
                    if (maxLen == minLen) {
                        return name + "参数长度必须为：" + minLen;
                    } else {
                        return name + "参数长度必须在：[" + minLen + "," + maxLen + ")区间";
                    }
                }
            }
        } else if (chk.stringType() == CheckParams.StringType.NUMBER) {
            if (StringKit.isNumeric(str)) {
                Double aDouble = Double.valueOf(str);
                return checkDouble(name, aDouble, chk.min(), chk.max());
            } else {
                return name + "参数不是数字";
            }
        }
        return null;
    }

    private static Entry<String, String> checkListFirst(String name, Object o1, CheckParams chk) {
        boolean necessity = chk.isNecessity();
        if (necessity) {
            if (o1 == null) {
                return new Entry<>(name, "缺少" + name + "参数");
            } else {
                List o11 = (List) o1;
                for (int i = 0; i < o11.size(); i++) {
                    Object o2 = o11.get(i);
                    Entry<String, String> stringStringEntry = checkObject(o2);
                    if (stringStringEntry != null) {
                        stringStringEntry.details = "参数" +
                                name +
                                "第" +
                                (i + 1) +
                                "个元素，" +
                                stringStringEntry.details;
                        return stringStringEntry;
                    }
                }
            }
        } else {
            if (o1 != null) {
                List o11 = (List) o1;
                for (Object o2 : o11) {
                    Entry<String, String> stringStringEntry = checkObject(o2);
                    if (stringStringEntry != null) {
                        return stringStringEntry;
                    }
                }
            }
        }
        return null;
    }

    private static Entry<String, String> checkObject(Object o) {
        final Class<?> objClass = o.getClass();
        Field[] farray = objClass.getDeclaredFields();//获取所有字段  包括private 不包括父类字段
        Class<CheckParams> chkString = CheckParams.class;//CheckString的class
        for (int i = 0; i < farray.length; i++) {
            Field field = farray[i];//获取其中字段
            String fieldName = field.getName();//获取字段名称
            if (field.isAnnotationPresent(chkString)) {//判断是否被chkstring注解所标识
                //如果被标识
                CheckParams chk = field.getAnnotation(chkString);// 返回这个类所标识的注解对象

                Class<?> type = field.getType();
                String name = type.getName();

                if (isBasic(name)) {
                    try {
                        String s = checkBasic(fieldName, field.get(o), chk);
                        if (!StringKit.isEmpty(s)) {
                            return new Entry<>(fieldName, s);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return new Entry<>(fieldName, "检查错误");
                    }
                } else if (isString(name)) {
                    try {
                        Object o1 = field.get(o);
                        String s = checkStringType(fieldName, o1, chk);
                        if (!StringKit.isEmpty(s)) {
                            return new Entry<>(fieldName, s);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return new Entry<>(fieldName, "检查错误");
                    }

                } else if (isList(name)) {
                    try {
                        return checkListFirst(fieldName, field.get(o), chk);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return new Entry<>(fieldName, "检查错误");
                    }
                } else {
                    boolean necessity = chk.isNecessity();
                    try {
                        Object o1 = field.get(o);
                        if (necessity) {
                            if (o1 == null) {
                                return new Entry<>(fieldName, "缺少" + fieldName + "参数");
                            } else {
                                checkObject(o1);
                            }
                        } else {
                            if (o1 != null) {
                                checkObject(o1);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return new Entry<>(fieldName, "检查错误");
                    }
                }
            }
        }
        return null;
    }

    public static Entry<String, String> getEntry() {
        return new Entry<>();
    }

    public static Entry<String, String> getDefaultEntry() {
        return new Entry<>(DEFAULT);
    }

    public final static class Entry<K, V> {
        public K digest;
        public V details;

        public Entry() {

        }

        public Entry(K digest) {
            this.digest = digest;
        }

        public Entry(K digest, V details) {
            this.digest = digest;
            this.details = details;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "digest=" + digest +
                    ", details=" + details +
                    '}';
        }
    }

}
