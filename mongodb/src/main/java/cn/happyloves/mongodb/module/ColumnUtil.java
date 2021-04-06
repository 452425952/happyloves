package cn.happyloves.mongodb.module;

import com.sun.istack.internal.NotNull;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author ZC
 * @date 2021/4/2 14:31
 */
public class ColumnUtil {

    public static <T> SerializedLambda serializedLambda(SFunction<T, ?> fn) {
        // 从function取出序列化方法
        Method writeReplaceMethod;
        try {
            writeReplaceMethod = fn.getClass().getDeclaredMethod("writeReplace");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        // 从序列化方法取出序列化的lambda信息
        boolean isAccessible = writeReplaceMethod.isAccessible();
        //isAccessible()值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查。值为 false 则指示反射的对象应该实施 Java 语言访问检查。
        //实际上setAccessible是启用和禁用访问安全检查的开关,并不是为true就能访问为false就不能访问
        //由于JDK的安全检查耗时较多.所以通过setAccessible(true)的方式关闭安全检查就可以达到提升反射速度的目的
        writeReplaceMethod.setAccessible(true);
        SerializedLambda serializedLambda;
        try {
            serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(fn);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        writeReplaceMethod.setAccessible(isAccessible);
        return serializedLambda;
    }

    public static <T> String getName(SFunction<T, ?> fn) {
        SerializedLambda serializedLambda = serializedLambda(fn);
        // 从lambda信息取出method、field、class等
        String fieldName = serializedLambda.getImplMethodName().substring("get".length());
        fieldName = fieldName.replaceFirst(fieldName.charAt(0) + "", (fieldName.charAt(0) + "").toLowerCase());
        Field field;
        try {
            field = Class.forName(serializedLambda.getImplClass().replace("/", ".")).getDeclaredField(fieldName);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        // 从field取出字段名，可以根据实际情况调整
        ColumnName columnName = field.getAnnotation(ColumnName.class);
        if (columnName != null && columnName.value().length() > 0) {
            return columnName.value();
        } else {
            return fieldName.replaceAll("[A-Z]", "_$0").toLowerCase();
        }
    }

    /**
     * 静态方法-设置子列值
     *
     * @param parent 父
     * @param son    子
     */
    public static void setParentSon(@NotNull LambdaCriteria<?> parent, @NotNull LambdaCriteria<?> son) {
        parent.getColumnList().add(son.getColumnName());
        if (parent.getSonLambdaCriteria() != null) {
            //递归
            setParentSon(parent.getSonLambdaCriteria(), son);
        } else {
            //获取父字段类型
            final Class<?> fieldType = parent.getField().getType();
            //当父字段类型与子实体类，类型不匹配
            if(fieldType != son.getEntityType()){
                throw new RuntimeException(fieldType + " & " + son.getEntityType() + ", 父字段类型与子实体类型不匹配");
            }
//            assert fieldType == son.getEntityType() : fieldType + " & " + son.getEntityType() + ", 父字段类型与子实体类型不匹配";
            //设置子类
            parent.setSonLambdaCriteria(son);
        }
    }
//
//    /**
//     * 静态方法-获取字段名
//     *
//     * @param parent      父
//     * @param columnNames 字段名集合
//     */
//    public static void getColumnName(LambdaCriteria<?> parent, List<String> columnNames) {
//        LambdaCriteria<?> son = parent.getSonLambdaCriteria();
//        if (son != null) {
//
//            columnNames.add(son.getColumnName());
//            //递归
//            getColumnName(son, columnNames);
//        }
//    }
}
