package xyz.junerver.utils;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Description:
 *
 * @author Junerver
 * date: 2022/2/24-10:31
 * Email: junerver@gmail.com
 * Version: v1.0
 */
public class TestJava {
    @Test
    public void addition_isCorrect() throws Exception {
        System.out.println();
//        "SSS".equals("") ? 1 : 2;
//        1 > 2;
        ExampleUnitTest.Person zhangsan = new ExampleUnitTest.Person("张三");
        ExampleUnitTest.Person lisi = new ExampleUnitTest.Person("李四");
        swap(zhangsan, lisi);
        System.out.println("-----------交换后------------");
        System.out.println(zhangsan.getName());
        System.out.println(lisi.getName());
    }

    public void swap(ExampleUnitTest.Person p1, ExampleUnitTest.Person p2) {
        ExampleUnitTest.Person person;
        person = p1;
        p1 = p2;
        p2 = person;
        System.out.println("-----------交换中------------");
        System.out.println(p1.getName());
        System.out.println(p2.getName());

    }
}
