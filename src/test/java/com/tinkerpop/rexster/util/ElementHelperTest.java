package com.tinkerpop.rexster.util;

import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: smallette
 * Date: 9/22/11
 * Time: 8:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class ElementHelperTest {
    
    @Test
    public void getTypedPropertyValueNullPropertyValue() {
        Object emptyString = ElementHelper.getTypedPropertyValue(null);
        Assert.assertNotNull(emptyString);
        Assert.assertEquals("", emptyString);
    }

    @Test
    public void getTypedPropertyValueEmptyPropertyValue() {
        Object emptyString = ElementHelper.getTypedPropertyValue("");
        Assert.assertNotNull(emptyString);
        Assert.assertEquals("", emptyString);
    }

    @Test
    public void getTypedPropertyValueNonTyped() {
        Object typedPropertyValue = ElementHelper.getTypedPropertyValue("xyz");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertEquals("xyz", typedPropertyValue);

        typedPropertyValue = ElementHelper.getTypedPropertyValue("123");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertEquals("123", typedPropertyValue);
    }

    @Test
    public void getTypedPropertyValueBadFormats() {
        Object typedPropertyValue = ElementHelper.getTypedPropertyValue("i,123)");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertEquals("i,123)", typedPropertyValue);

        typedPropertyValue = ElementHelper.getTypedPropertyValue("(dfd,123)");
        Assert.assertNotNull(typedPropertyValue);
        // TODO (string, marko) should be a datatype of string (causes problems for this model though)
        // Assert.assertEquals("(dfd,123)", typedPropertyValue);
    }

    @Test
    public void getTypedPropertyValueIntegerTyped() {
        Object typedPropertyValue = ElementHelper.getTypedPropertyValue("(i,123)");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertEquals(123, typedPropertyValue);

        typedPropertyValue = ElementHelper.getTypedPropertyValue("(integer,123)");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertEquals(123, typedPropertyValue);

        typedPropertyValue = ElementHelper.getTypedPropertyValue("(integer,123bad)");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertEquals("123bad", typedPropertyValue);
    }

    @Test
    public void getTypedPropertyValueLongTyped() {
        Object typedPropertyValue = ElementHelper.getTypedPropertyValue("(l,123)");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertEquals(123l, typedPropertyValue);

        typedPropertyValue = ElementHelper.getTypedPropertyValue("(long,123)");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertEquals(123l, typedPropertyValue);

        typedPropertyValue = ElementHelper.getTypedPropertyValue("(long,123bad)");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertEquals("123bad", typedPropertyValue);
    }

    @Test
    public void getTypedPropertyValueDoubleTyped() {
        Object typedPropertyValue = ElementHelper.getTypedPropertyValue("(d,123)");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertEquals(123d, typedPropertyValue);

        typedPropertyValue = ElementHelper.getTypedPropertyValue("(double,123)");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertEquals(123d, typedPropertyValue);

        typedPropertyValue = ElementHelper.getTypedPropertyValue("(double,123bad)");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertEquals("123bad", typedPropertyValue);
    }

    @Test
    public void getTypedPropertyValueFloatTyped() {
        Object typedPropertyValue = ElementHelper.getTypedPropertyValue("(f,123)");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertEquals(123f, typedPropertyValue);

        typedPropertyValue = ElementHelper.getTypedPropertyValue("(float,123)");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertEquals(123f, typedPropertyValue);

        typedPropertyValue = ElementHelper.getTypedPropertyValue("(float,123bad)");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertEquals("123bad", typedPropertyValue);
    }

    @Test
    public void getTypedPropertyValueListNonTyped() {
        Object typedPropertyValue = ElementHelper.getTypedPropertyValue("(list,(123,321,456,678))");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertTrue(typedPropertyValue instanceof List);

        List list = (List) typedPropertyValue;
        Assert.assertEquals(4, list.size());
        Assert.assertEquals("123", list.get(0));
        Assert.assertEquals("321", list.get(1));
        Assert.assertEquals("456", list.get(2));
        Assert.assertEquals("678", list.get(3));
    }

    @Test
    public void getTypedPropertyValueListTyped() {
        Object typedPropertyValue = ElementHelper.getTypedPropertyValue("(list,((integer,123),(d,321),456,(f,678)))");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertTrue(typedPropertyValue instanceof List);

        List list = (List) typedPropertyValue;
        Assert.assertEquals(4, list.size());
        Assert.assertEquals(123, list.get(0));
        Assert.assertEquals(321d, list.get(1));
        Assert.assertEquals("456", list.get(2));
        Assert.assertEquals(678f, list.get(3));
    }

    @Test
    public void getTypedPropertyValueListTypedBadFormat() {
        Object typedPropertyValue = ElementHelper.getTypedPropertyValue("(list,((integer,123,(d,321),456,(f,678)))");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertTrue(typedPropertyValue instanceof List);

        List list = (List) typedPropertyValue;
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("123,(d,321),456,(f,678", list.get(0));
    }

    @Test
    public void getTypedPropertyValueMapNonTyped() {
        Object typedPropertyValue = ElementHelper.getTypedPropertyValue("(map,(a=123,b=321))");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertTrue(typedPropertyValue instanceof Map);

        Map map = (Map) typedPropertyValue;
        Assert.assertEquals(2, map.size());
        Assert.assertTrue(map.containsKey("a"));
        Assert.assertTrue(map.containsKey("b"));
        Assert.assertEquals("123", map.get("a"));
        Assert.assertEquals("321", map.get("b"));
    }

    @Test
    public void getTypedPropertyValueMapTyped() {
        Object typedPropertyValue = ElementHelper.getTypedPropertyValue("(map,(a=(i,123),b=(d,321)))");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertTrue(typedPropertyValue instanceof Map);

        Map map = (Map) typedPropertyValue;
        Assert.assertEquals(2, map.size());
        Assert.assertTrue(map.containsKey("a"));
        Assert.assertTrue(map.containsKey("b"));
        Assert.assertEquals(123, map.get("a"));
        Assert.assertEquals(321d, map.get("b"));
    }

    @Test
    public void getTypedPropertyValueMapTypedEmbedded() {
        Object typedPropertyValue = ElementHelper.getTypedPropertyValue("(map,(a=(i,123),b=(d,321),c=(map,(x=y))))");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertTrue(typedPropertyValue instanceof Map);

        Map map = (Map) typedPropertyValue;
        Assert.assertEquals(3, map.size());
        Assert.assertTrue(map.containsKey("a"));
        Assert.assertTrue(map.containsKey("b"));
        Assert.assertTrue(map.containsKey("c"));
        Assert.assertEquals(123, map.get("a"));
        Assert.assertEquals(321d, map.get("b"));

        Assert.assertTrue(map.get("c") instanceof Map);
        Map inner = (Map) map.get("c");
        Assert.assertEquals("y", inner.get("x"));
    }

    @Test
    public void getTypedPropertyValueListTypedEmbeddedList() {
        Object typedPropertyValue = ElementHelper.getTypedPropertyValue("(list,((integer,123),(d,321),456,(f,678),(list,(123,(i,456),789))))");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertTrue(typedPropertyValue instanceof List);

        List list = (List) typedPropertyValue;
        Assert.assertEquals(5, list.size());
        Assert.assertEquals(123, list.get(0));
        Assert.assertEquals(321d, list.get(1));
        Assert.assertEquals("456", list.get(2));
        Assert.assertEquals(678f, list.get(3));

        Assert.assertTrue(list.get(4) instanceof List);
        List innerList = (List) list.get(4);
        Assert.assertEquals(3, innerList.size());
        Assert.assertEquals("123", innerList.get(0));
        Assert.assertEquals(456, innerList.get(1));
        Assert.assertEquals("789", innerList.get(2));

    }

    @Test
    public void getTypedPropertyValueMapTypedComplexMap() {
        Object typedPropertyValue = ElementHelper.getTypedPropertyValue("(map,(a=(i,123),b=(d,321),c=(map,(x=y)),d=(list,(321,(f,123)))))");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertTrue(typedPropertyValue instanceof Map);

        Map map = (Map) typedPropertyValue;
        Assert.assertEquals(4, map.size());
        Assert.assertTrue(map.containsKey("a"));
        Assert.assertTrue(map.containsKey("b"));
        Assert.assertTrue(map.containsKey("c"));
        Assert.assertEquals(123, map.get("a"));
        Assert.assertEquals(321d, map.get("b"));

        Assert.assertTrue(map.get("c") instanceof Map);
        Map inner = (Map) map.get("c");
        Assert.assertEquals("y", inner.get("x"));

        Assert.assertTrue(map.get("d") instanceof List);
        List innerList = (List) map.get("d");
        Assert.assertEquals(2, innerList.size());
        Assert.assertEquals("321", innerList.get(0));
        Assert.assertEquals(123f, innerList.get(1));
    }

    @Test
    public void getTypedPropertyValueListTypedComplexList() {
        Object typedPropertyValue = ElementHelper.getTypedPropertyValue("(list,((integer,123),(d,321),456,(f,678),(list,(123,(i,456),789)),(map,(x=y)),(map,(x=(i,123)))))");
        Assert.assertNotNull(typedPropertyValue);
        Assert.assertTrue(typedPropertyValue instanceof List);

        List list = (List) typedPropertyValue;
        Assert.assertEquals(7, list.size());
        Assert.assertEquals(123, list.get(0));
        Assert.assertEquals(321d, list.get(1));
        Assert.assertEquals("456", list.get(2));
        Assert.assertEquals(678f, list.get(3));

        Assert.assertTrue(list.get(4) instanceof List);
        List innerList = (List) list.get(4);
        Assert.assertEquals(3, innerList.size());
        Assert.assertEquals("123", innerList.get(0));
        Assert.assertEquals(456, innerList.get(1));
        Assert.assertEquals("789", innerList.get(2));

        Assert.assertTrue(list.get(5) instanceof Map);
        Map innerMap1 = (Map) list.get(5);
        Assert.assertTrue(innerMap1.containsKey("x"));
        Assert.assertEquals("y", innerMap1.get("x"));

        Assert.assertTrue(list.get(6) instanceof Map);
        Map innerMap2 = (Map) list.get(6);
        Assert.assertTrue(innerMap2.containsKey("x"));
        Assert.assertEquals(123, innerMap2.get("x"));
    }
}